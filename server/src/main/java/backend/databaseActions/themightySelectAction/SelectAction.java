package backend.databaseActions.themightySelectAction;

import backend.Utilities.BaseTable;
import backend.Utilities.JoinedTable;
import backend.Utilities.Table;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.JoinModel;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.databaseModels.conditions.FunctionCall;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SelectAction implements DatabaseAction {

    private String databaseName;
    private String baseTable;
    private List<String> projectionColumns;
    private List<Condition> conditions;
    private List<JoinModel> joinModels;
    private List<String> groupedByColumns;
    private List<String> aggregations;

    /**
     * @author Kovacs Elek Akos
     * @version 1.0
     * @param databaseName This is databases name that is currently used
     * @param baseTable This is the base table name in FROM clause
     * @param projectionColumns These are the table + column names that are projected eg: "users.name" or "package.price", where 'users' & 'package' are tables names, and 'name' & 'price' are column names
     * @param conditions These are the conditions of the WHERE clause, see the Condition interface for more information
     * @param groupedByColumns A list of table names and column names eq: 'users.name'
     * @param aggregations A list of columns inside functions eq: SUM(users.ID)
     * */
    public SelectAction(String databaseName, String baseTable, List<String> projectionColumns, List<Condition> conditions, List<JoinModel> joinModels, List<String> groupedByColumns, ArrayList<String> aggregations) {
        this.databaseName = databaseName;
        this.baseTable = baseTable;
        this.projectionColumns = projectionColumns;
        this.conditions = conditions;
        this.joinModels = joinModels;
        this.groupedByColumns = groupedByColumns;
        this.aggregations = aggregations;
    }

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException, PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates, PrimaryKeyValueAlreadyInTable, UniqueValueAlreadyInTable, ForeignKeyValueNotFoundInParentTable, InvalidReadException, ForeignKeyValueIsBeingReferencedInAnotherTable, FieldsNotCompatible {
        //TODO validate everything:

        // Group the conditions by tables
        LinkedHashMap<String, ArrayList<Condition>> tableConditions = new LinkedHashMap<>();
        tableConditions.put(baseTable, new ArrayList<>());

        for (Condition condition : conditions) {
            if (condition instanceof Equation) {
                Equation equation = (Equation) condition;
                String tableName = equation.getLFieldTable();

                ArrayList<Condition> conditionsList = tableConditions.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(equation);
            } else if (condition instanceof FunctionCall) {
                FunctionCall functionCall = (FunctionCall) condition;
                String tableName = functionCall.getFieldTable();
                ArrayList<Condition> conditionsList = tableConditions.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(functionCall);
            }
        }

        // Create base tables and apply conditions
        ArrayList<BaseTable> baseConditionedTables = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Condition>> entry : tableConditions.entrySet()) {
            String tableName = entry.getKey();
            ArrayList<Condition> conditions = entry.getValue();

            baseConditionedTables.add(new BaseTable(databaseName, tableName, conditions));
        }

        // Create base tables that have no condition but appear on JOIN
        ArrayList<Table> finalTables = new ArrayList<>();

        // Append the tables in the order of join
        for( JoinModel model : joinModels) {
            String ltable = model.getLeftTableName();
            String rtable = model.getRightTableName();

            // check if there was a condition for the table
            if (! tableConditions.containsKey(ltable)) {
                finalTables.add(new BaseTable(databaseName, ltable, new ArrayList<>()));
            } else {
                // find the filtered table
                for (BaseTable table : baseConditionedTables) {
                    if (table.getTableName().equals(ltable)) {
                        finalTables.add(table);
                    }
                }
            }

            // same for the right table
            if (! tableConditions.containsKey(rtable)) {
                finalTables.add(new BaseTable(databaseName, ltable, new ArrayList<>()));
            } else {
                for (BaseTable table : baseConditionedTables) {
                    if (table.getTableName().equals(rtable)) {
                        finalTables.add(table);
                    }
                }
            }
        }

        // If there were no joins, the only table that could be filtered is the base table
        // From now on the working table is called 'joinedTable'
        Table joinedTable = null;
        if (joinModels.size() > 0) {
            // Join the tables together
            joinedTable = (JoinedTable) JoinedTable.join(finalTables, (ArrayList<JoinModel>) joinModels);
        } else {
            joinedTable = baseConditionedTables.get(0);
        }

        Table grouppedTable = null;
        if (groupedByColumns.size() > 0) {

        }




        return null;
    }
}
