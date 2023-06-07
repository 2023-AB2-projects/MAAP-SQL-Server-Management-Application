package backend.databaseActions.themightySelectAction;

import backend.Utilities.BaseTable;
import backend.Utilities.JoinedTable;
import backend.Utilities.Table;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.JoinModel;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.databaseModels.conditions.FunctionCall;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.*;
import backend.validators.SelectValidator;
import backend.validators.Validator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SelectAction implements DatabaseAction {

    private final String databaseName;
    private final String baseTable;
    private final ArrayList<String> projectionColumns;
    private final List<Condition> conditions;
    private List<JoinModel> joinModels;
    private final List<String> joinedTables;
    private final List<String> groupedByColumns;
    private final ArrayList<Aggregator> aggregations;

    /**
     * @param databaseName      This is databases name that is currently used
     * @param baseTable         This is the base table name in FROM clause
     * @param projectionColumns These are the table + column names that are projected eg: "users.name" or "package.price", where 'users' & 'package' are tables names, and 'name' & 'price' are column names, IMPORTANT is SELECT * is present, than the only elem of the list is:  ['*']
     * @param conditions        These are the conditions of the WHERE clause, see the Condition interface for more information
     * @param joinedTables
     * @param groupedByColumns  A list of table names and column names eq: 'users.name'
     * @param aggregations      A list of columns inside functions eq: SUM(users.ID) see Aggregator Class
     * @author Kovacs Elek Akos
     */
    public SelectAction(String databaseName, String baseTable, ArrayList<String> projectionColumns, List<Condition> conditions, List<JoinModel> joinModels, List<String> joinedTables, List<String> groupedByColumns, ArrayList<Aggregator> aggregations) {
        this.databaseName = databaseName;
        this.baseTable = baseTable;

        this.projectionColumns = projectionColumns;
        this.joinedTables = joinedTables;
        this.projectionColumns.addAll(aggregations.stream().map(elem -> elem.getAlias()).collect(Collectors.toCollection(ArrayList::new)));
        this.conditions = conditions;
        this.joinModels = joinModels;
        this.groupedByColumns = groupedByColumns;
        this.aggregations = aggregations;


    }

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException, PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates, PrimaryKeyValueAlreadyInTable, UniqueValueAlreadyInTable, ForeignKeyValueNotFoundInParentTable, InvalidReadException, ForeignKeyValueIsBeingReferencedInAnotherTable, FieldsNotCompatible {
        Validator validator = new SelectValidator(databaseName, baseTable, projectionColumns, conditions, joinModels, groupedByColumns, aggregations);
        try {
            validator.validate();
        } catch (FieldNotFound e) {
            log.error(e.toString());
        }

        log.info("Select passed the validation!");
        // Group the conditions by tables
        LinkedHashMap<String, ArrayList<Condition>> tableConditions = new LinkedHashMap<>();
        tableConditions.put(baseTable, new ArrayList<>());

        for (Condition condition : conditions) {
            if (condition instanceof Equation equation) {
                String tableName = equation.getLFieldTable();

                ArrayList<Condition> conditionsList = tableConditions.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(equation);
            } else if (condition instanceof FunctionCall functionCall) {
                String tableName = functionCall.getFieldTable();
                ArrayList<Condition> conditionsList = tableConditions.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(functionCall);
            }
        }
        log.info("There were" + conditions.size() + " conditions for " + tableConditions.size() + " tables!");

        // Create base tables and apply conditions
        ArrayList<BaseTable> baseConditionedTables = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Condition>> entry : tableConditions.entrySet()) {
            String tableName = entry.getKey();
            ArrayList<Condition> conditions = entry.getValue();

            baseConditionedTables.add(new BaseTable(databaseName, tableName, conditions));
        }

        // Create base tables that have no condition but appear on JOIN
        ArrayList<Table> finalTables = new ArrayList<>();
        ArrayList<String> finalTableNames = new ArrayList<>();

        // Append the tables in the order of join
        for( JoinModel model : joinModels) {
            String ltable = model.getLeftTableName();
            String rtable = model.getRightTableName();


            // check if there was a condition for the table
            // check if it is in the final tables
            if (! tableConditions.containsKey(ltable) && !finalTableNames.contains(ltable)) {
                finalTables.add(new BaseTable(databaseName, ltable, new ArrayList<>()));
                finalTableNames.add(ltable);
            } else if ( !finalTableNames.contains(ltable) ) {
                // find the filtered table
                for (BaseTable table : baseConditionedTables) {
                    if (table.getTableName().equals(ltable)) {
                        finalTables.add(table);
                        finalTableNames.add(ltable);
                    }
                }
            }


            // same for the right table
            if (! tableConditions.containsKey(rtable) && !finalTableNames.contains(rtable)) {
                finalTables.add(new BaseTable(databaseName, rtable, new ArrayList<>()));
                finalTableNames.add(rtable);
            } else if (!finalTableNames.contains(rtable)) {
                for (BaseTable table : baseConditionedTables) {
                    if (table.getTableName().equals(rtable)) {
                        finalTables.add(table);
                        finalTableNames.add(rtable);
                    }
                }
            }
        }
        log.info("There are " + finalTables.size() + " tables in total");
        finalTables.stream().forEach(e -> System.out.println(e.getColumnNames()));

        log.info(joinedTables.toString());
        for (JoinModel joinModel : joinModels) {
            log.info(joinModel.getLeftFieldName() + " x " + joinModel.getRightFieldName());
        }
        // put the joinModel in the right order
        JoinModel.sort(joinModels, joinedTables);
        // put the created basetables in the right order
        if ( !(finalTables.size() == 0 && joinedTables.size() == 1)) {
            finalTables = sortTables(finalTables, finalTableNames, joinedTables);
        }
        for (JoinModel joinModel : joinModels) {
            log.info(joinModel.getLeftFieldName() + " x " + joinModel.getRightFieldName());
        }
        log.info("Final tablenames");
        for ( String name : finalTableNames) {
            log.info(name);
        }


        // If there were no joins, the only table that could be filtered is the base table
        // From now on the working table is called 'joinedTable'
        Table joinedTable;
        if (joinModels.size() > 0) {
            // Join the tables together
            joinedTable = JoinedTable.join(finalTables, (ArrayList<JoinModel>) joinModels);
            log.info(" Tables joined");
        } else {
            joinedTable = baseConditionedTables.get(0);
            log.info("No join was executed, working table : " + baseTable);
        }

        Table grouppedTable;
        if (groupedByColumns.size() > 0) {
            grouppedTable = joinedTable.groupBy((ArrayList<String>) groupedByColumns);
            grouppedTable.aggregation(aggregations);
            log.info("Tables groupped!");
        } else if( aggregations.size() > 0) {
            joinedTable.aggregation(aggregations);
            grouppedTable = joinedTable;
            log.info("Tables aggregated!");
        } else {
            grouppedTable = joinedTable;
            log.info("No groupBy was executed!");
        }

        // Project selected fields
        if (!projectionColumns.get(0).equals("*")) {
            grouppedTable.projection(projectionColumns);
            log.info("Projection executed!");
        }

        return grouppedTable;
    }

    private ArrayList<Table> sortTables(ArrayList<Table> finalTables, ArrayList<String> finalTableNames, List<String> joinedTables) {
        ArrayList<Table> sortedTables = new ArrayList<>();

        for ( String tableName : joinedTables ) {
            int indexOfUnsortedTable = finalTableNames.indexOf(tableName);
            sortedTables.add(finalTables.get(indexOfUnsortedTable));
        }

        return sortedTables;
    }
}
