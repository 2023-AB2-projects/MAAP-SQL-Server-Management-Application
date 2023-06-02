package backend.databaseActions.themightySelectAction;

import backend.Utilities.BaseTable;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.databaseModels.conditions.FunctionCall;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.*;

import java.io.IOException;
import java.util.*;

public class SelectAction implements DatabaseAction {

    private String databaseName;
    private List<String> projectionColumns;
    private List<Condition> conditions;
    private HashMap<String, String> joinedTablesOnColumns;
    private List<String> groupedByColumns;
    private List<String> aggregations;

    /**
     * @author Kovacs Elek Akos
     * @version 1.0
     * @param databaseName This is databases name that is currently used
     * @param projectionColumns These are the table + column names that are projected eg: "users.name" or "package.price", where 'users' & 'package' are tables names, and 'name' & 'price' are column names
     * @param conditions These are the conditions of the WHERE clause, see the Condition interface for more information
     * @param joinedTablesOnColumns Hashmap where the key value pairs are the two tables and the two columns eq: "FROM orders JOIN users ON orders.customerID = users.ID" -> joinedTablesOnColumns['orders.customerID'] = 'users.ID'
     * @param groupedByColumns A list of table names and column names eq: 'users.name'
     * @param aggregations A list of columns inside functions eq: SUM(users.ID)
     * */
    public SelectAction(String databaseName, List<String> projectionColumns, List<Condition> conditions, HashMap<String, String> joinedTablesOnColumns, List<String> groupedByColumns, List<String> aggregations) {
        this.databaseName = databaseName;
        this.projectionColumns = projectionColumns;
        this.conditions = conditions;
        this.joinedTablesOnColumns = joinedTablesOnColumns;
        this.groupedByColumns = groupedByColumns;
        this.aggregations = aggregations;
    }

    @Override
    public Object actionPerform() throws DatabaseNameAlreadyExists, TableNameAlreadyExists, DatabaseDoesntExist, PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, TableDoesntExist, IndexAlreadyExists, ForeignKeyFieldNotFound, IOException, RecordNotFoundException, PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates, PrimaryKeyValueAlreadyInTable, UniqueValueAlreadyInTable, ForeignKeyValueNotFoundInParentTable, InvalidReadException, ForeignKeyValueIsBeingReferencedInAnotherTable, FieldsNotCompatible {
        //TODO validate everything:

        // Group the conditions by tables
        LinkedHashMap<String, ArrayList<Condition>> tableConditions = new LinkedHashMap<>();

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
        List<BaseTable> baseTables = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Condition>> entry : tableConditions.entrySet()) {
            String tableName = entry.getKey();
            ArrayList<Condition> conditions = entry.getValue();

            baseTables.add(new BaseTable(databaseName, tableName, conditions));
        }

        //

        return null;
    }
}
