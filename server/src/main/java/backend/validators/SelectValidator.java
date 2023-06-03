package backend.validators;

import backend.databaseModels.JoinModel;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.databaseModels.conditions.FunctionCall;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.exceptions.databaseActionsExceptions.FieldNotFound;
import backend.exceptions.databaseActionsExceptions.TableDoesntExist;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SelectValidator implements Validator{

    private final String databaseName;
    private final String baseTable;
    private final ArrayList<String> projectionColumns;
    private final List<Condition> conditions;
    private final List<JoinModel> joinModels;
    private final List<String> groupedByColumns;
    private final ArrayList<Aggregator> aggregations;

    private final String regex = "\\b\\w+\\s*\\(\\s*\\w+\\.\\w+\\s*\\)";
    private final Pattern pattern = Pattern.compile(regex);

    public SelectValidator(String databaseName, String baseTable, ArrayList<String> projectionColumns, List<Condition> conditions, List<JoinModel> joinModels, List<String> groupedByColumns, ArrayList<Aggregator> aggregations) {
        this.databaseName = databaseName;
        this.baseTable = baseTable;
        this.projectionColumns = projectionColumns;
        this.conditions = conditions;
        this.joinModels = joinModels;
        this.groupedByColumns = groupedByColumns;
        this.aggregations = aggregations;
    }

    @Override
    public void validate() throws DatabaseDoesntExist, TableDoesntExist, FieldNotFound {

        // check if database exists
        if ( !CatalogManager.getDatabaseNames().contains(databaseName)) {
            throw new DatabaseDoesntExist(databaseName);
        }
        log.debug("Database " + databaseName + " exists");

        // check if fields and tables exist
        // from the join model, extract the valid tables, other tables' field are not allowed to project
        HashMap<String, ArrayList<String>> tableFields = new HashMap<>();
        for (JoinModel model : joinModels) {
            String ltable = model.getLeftTableName();
            String rtable = model.getRightTableName();

          // check if the two fields have the same type
            try {
                String lType = CatalogManager.getFieldType(databaseName, ltable, model.getLeftFieldName());
                String rType = CatalogManager.getFieldType(databaseName, rtable, model.getRightFieldName());

                if (!lType.equals(rType)) {
                    // accepted types : "int", "float", "bit", "date", "datetime", "char"
                    log.info("Not compatible on join" + model.getLeftFieldName() + " : " + lType + " AND " + model.getRightFieldName() + " : " + rType);
                    throw new RuntimeException(" Types of where clause do not match " + model.getLeftFieldName() + " : " + lType + " AND " + model.getRightFieldName() + " : " + rType);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }

            if (tableFields.containsKey(ltable)) {
                if (!tableFields.get(ltable).contains(model.getLeftFieldName())) {
                    tableFields.get(ltable).add(model.getLeftFieldName());
                }
            } else {
                tableFields.put(ltable, new ArrayList<>());
                tableFields.get(ltable).add(model.getLeftFieldName());
            }

            if (tableFields.containsKey(rtable)) {
                if (!tableFields.get(rtable).contains(model.getRightFieldName())) {
                    tableFields.get(rtable).add(model.getRightFieldName());
                }
            } else {
                tableFields.put(rtable, new ArrayList<>());
                tableFields.get(rtable).add(model.getRightFieldName());
            }
        }

        ArrayList<String> validTables = (ArrayList<String>) tableFields.keySet();
        validTables.forEach(e -> log.info(e.toString()));

        // check if tables and fields exist
        for (Map.Entry<String, ArrayList<String>> entry : tableFields.entrySet()) {
            String currentTable = entry.getKey();
            ArrayList<String> currentJoinedField = entry.getValue();

            // check if table exists
            if ( !CatalogManager.getCurrentDatabaseTableNames().contains(currentTable)) {
                throw new TableDoesntExist(currentTable, databaseName);
            }

            // check if fields exist
            for (String field : currentJoinedField) {
                if (!CatalogManager.getFieldNames(databaseName, currentTable).contains(field)) {
                    throw new FieldNotFound(field, currentTable);
                }
            }
        }

        // check the conditions
        // the tables must be selected from the original joined group
        LinkedHashMap<String, ArrayList<String>> tableConditionedFields = new LinkedHashMap<>();
        tableConditionedFields.put(baseTable, new ArrayList<>());

        // building the hashset for [table] = [fields] from the WHERE clause
        for (Condition condition : conditions) {
            if (condition instanceof Equation equation) {
                String tableName = equation.getLFieldTable();
                String fieldName = equation.getLFieldName();

                //TODO Check if type(id) = type("alma"),

                ArrayList<String> conditionsList = tableConditionedFields.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(fieldName);
            } else if (condition instanceof FunctionCall functionCall) {
                String tableName = functionCall.getFieldTable();
                String fieldName = functionCall.getFieldName();

                ArrayList<String> conditionsList = tableConditionedFields.computeIfAbsent(tableName, k -> new ArrayList<>());
                conditionsList.add(fieldName);
            }
        }

        // check if tables  are from the join model, also everything exists
        for (Map.Entry<String, ArrayList<String>> entry : tableConditionedFields.entrySet()) {
             String currentTable = entry.getKey();
             ArrayList<String> conditionedFields = entry.getValue();

             // check if table are in join, othervise dont care if exits
             if ( !validTables.contains(currentTable)) {
                 log.error(" Table " + currentTable + " is not present in the joinModel!");
                 throw new RuntimeException(" Table " + currentTable + " is not present in the joinModel!");
             }
             log.info("Table " + currentTable + " is present in the joinModel!");

             // check if the tables' fields exist
             for (String field : conditionedFields) {
                 if ( !CatalogManager.getFieldNames(databaseName, currentTable).contains(field)) {
                     log.error( "In table " + currentTable + " field" + field + " doesn't exist!");
                     throw new FieldNotFound(field, currentTable);
                }
            }
        }

        // in the projection only the grouped columns can be present
        ArrayList<String> groupedColumns = new ArrayList<>();
        ArrayList<String> projectedColumns = new ArrayList<>();
        ArrayList<String> aggregatedColumns = new ArrayList<>();

        // TODO Insert the aggregator list shit into aggregatedColumns

        // check if the projection tables and fields are correct
        for (String tableNameAndFieldName : projectionColumns) {
            // check if the projection is an aggregate function
            Matcher matcher = pattern.matcher(tableNameAndFieldName);
            String currentTable;
            String projectedField;

            if (matcher.find()) {
                // aggregate found
                String match = matcher.group();
                log.info("Match found: " + match);

                currentTable = match.split("\\(")[1].split("\\.")[0];
                projectedField = match.split("\\(")[1].split("\\.")[1];
                aggregatedColumns.add(currentTable+"."+projectedField);
            } else {
                // simple column projection
                currentTable = tableNameAndFieldName.split("\\.")[0];
                projectedField = tableNameAndFieldName.split("\\.")[1];
                projectedColumns.add(tableNameAndFieldName);
            }

            if (!validTables.contains(currentTable)) {
                log.error(" Table " + currentTable + " is not present in the joinModel!");
                throw new RuntimeException(" Table " + currentTable + " is not present in the joinModel!");
            }
            log.info("Table " + currentTable + " is present in the joinModel!");

            if (!CatalogManager.getFieldNames(databaseName, currentTable).contains(projectedField)) {
                log.error("In table " + currentTable + " field" + projectedField + " doesn't exist!");
                throw new FieldNotFound(projectedField, currentTable);
            }
        }



        // validate the groupBy section
        for (String tableNameAndFieldName : groupedByColumns) {
            String currentTable = tableNameAndFieldName.split("\\.")[0];
            String projectedField  = tableNameAndFieldName.split("\\.")[1];
            groupedColumns.add(tableNameAndFieldName);

            if (!validTables.contains(currentTable)) {
                log.error(" Table " + currentTable + " is not present in the joinModel!");
                throw new RuntimeException(" Table " + currentTable + " is not present in the joinModel!");
            }
            log.info("Table " + currentTable + " is present in the joinModel!");

            if (!CatalogManager.getFieldNames(databaseName, currentTable).contains(projectedField)) {
                log.error("In table " + currentTable + " field" + projectedField + " doesn't exist!");
                throw new FieldNotFound(projectedField, currentTable);
            }
        }

        //check if every field in projection are from the grouped fields
        for (String projectedField : projectedColumns) {
            if ( !groupedColumns.contains(projectedField) && !aggregatedColumns.contains(projectedField)) {
                log.error("Field" + projectedField + " is not present in GROUP BY columns, neither in aggregate functions!");
                throw new RuntimeException("Field" + projectedField + " is not present in GROUP BY columns, neither in aggregate functions!");
            }
        }

    }
}
