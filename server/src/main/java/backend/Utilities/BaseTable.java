package backend.Utilities;

import backend.Indexing.NonUniqueIndexManager;
import backend.Indexing.Queryable;
import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.databaseModels.conditions.Condition;
import backend.databaseModels.conditions.Equation;
import backend.databaseModels.conditions.FunctionCall;
import backend.exceptions.NoIndexException;
import backend.exceptions.recordHandlingExceptions.UndefinedQueryException;
import backend.recordHandling.RecordReader;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BaseTable implements Table {
    @Getter
    private ArrayList<String> columnTypes, columnNames;
    @Getter
    private final String databaseName, tableName;

    @Getter
    private final HashMap<Integer, Integer> pointerMapper;

    @Getter
    private ArrayList<ArrayList<Object>> tableContent;

    public BaseTable(String databaseName, String tableName) throws IOException {
        this(databaseName, tableName, new ArrayList<>());
    }
    public BaseTable(String databaseName, String tableName, ArrayList<Condition> conditions) throws IOException {
        this.columnTypes = (ArrayList<String>) CatalogManager.getFieldTypes(databaseName, tableName);
        this.columnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        this.columnNames = columnNames.stream().map((elem) -> tableName + "." + elem).collect(Collectors.toCollection(ArrayList::new));
        this.databaseName = databaseName;
        this.tableName = tableName;
        pointerMapper = new HashMap<>();

        //use the indexes where able
        RecordReader io = new RecordReader(databaseName, tableName);
        HashSet<Integer> wantedRecordPointers = io.getAllPointers();

        ArrayList<Condition> usedConditions = new ArrayList<>();
        for (var condition : conditions){
            HashSet<Integer> pointers = new HashSet<>();
            if (condition instanceof Equation){
                String fieldName = ((Equation) condition).getLFieldName();
                int fieldIndex = columnNames.indexOf(fieldName);

                String indexName, trueFieldName;
                try {
                    trueFieldName = fieldName.substring(fieldName.indexOf('.') + 1);
                    indexName = CatalogManager.getIndexName(databaseName, tableName, trueFieldName);
                    usedConditions.add(condition);
                } catch (NoIndexException e) {
                    continue;
                }

                String fieldType = columnTypes.get(fieldIndex);
                String compareValueString = ((Equation) condition).getRFieldName();
                Object compareValue = TypeConverter.toObject(fieldType, compareValueString);

                Queryable index;
                if(CatalogManager.isFieldUnique(databaseName, tableName,trueFieldName)){
                    index = new UniqueIndexManager(databaseName, tableName, indexName);
                } else {
                    index = new NonUniqueIndexManager(databaseName, tableName, indexName);
                }

                HashMap<Integer, Object> queryResult = new HashMap<>();
                try {
                    switch (((Equation) condition).getOp()){
                        case EQUALS -> queryResult = index.equalityQuery(compareValue);
                        case LESS_THAN -> queryResult = index.lesserQuery(compareValue, false);
                        case LESS_THAN_OR_EQUAL_TO -> queryResult = index.lesserQuery(compareValue, true);
                        case GREATER_THAN -> queryResult = index.greaterQuery(compareValue, false);
                        case GREATER_THAN_OR_EQUAL_TO -> queryResult = index.greaterQuery(compareValue, true);
                    }
                    pointers = new HashSet<>(queryResult.keySet());

                } catch (UndefinedQueryException ignored){}
                index.close();


            } else if ( condition instanceof FunctionCall) {
                String fieldName = ((FunctionCall) condition).getFieldName();
                int fieldIndex = columnNames.indexOf(fieldName);
                String indexName, trueFieldName;
                try {
                    trueFieldName = fieldName.substring(fieldName.indexOf('.') + 1);
                    indexName = CatalogManager.getIndexName(databaseName, tableName, trueFieldName);
                    System.out.println(indexName);
                    usedConditions.add(condition);
                } catch (NoIndexException e) {
                    continue;
                }

                String fieldType = columnTypes.get(fieldIndex);
                ArrayList<String> args = ((FunctionCall) condition).getArgs();
                Object lower = TypeConverter.toObject(fieldType, args.get(0));
                Object upper = TypeConverter.toObject(fieldType, args.get(1));

                Queryable index;
                if(CatalogManager.isFieldUnique(databaseName, tableName, trueFieldName)){
                    index = new UniqueIndexManager(databaseName, tableName, indexName);
                } else {
                    index = new NonUniqueIndexManager(databaseName, tableName, indexName);
                }

                HashMap<Integer, Object> queryResult = new HashMap<>();
                try {
                    switch (((FunctionCall) condition).getFunction()){
                        case BETWEEN -> queryResult = index.rangeQuery(lower, upper, true, true);
                    }
                    pointers = new HashSet<>(queryResult.keySet());

                } catch (UndefinedQueryException ignored){}

                index.close();
            }

            wantedRecordPointers.retainAll(pointers);
        }

        ArrayList<Integer> listOfWantedPointers =  new ArrayList<>(wantedRecordPointers);
        tableContent = io.scanLines(listOfWantedPointers);

        for (int i = 0; i < tableContent.size(); i++){
            tableContent.get(i).add(listOfWantedPointers.get(i));
        }

        conditions.removeAll(usedConditions);
        for (var condition : conditions){
            Predicate<ArrayList<Object>> lambda = (ArrayList<Object> elem) -> {
                return true;
            };
            Integer fieldIndex;
            if (condition instanceof Equation){
                String fieldName = ((Equation) condition).getLFieldName();
                fieldIndex = columnNames.indexOf(fieldName);

                String fieldType = columnTypes.get(fieldIndex);
                String compareValueString = ((Equation) condition).getRFieldName();
                Object compareValue = TypeConverter.toObject(fieldType, compareValueString);

                switch (((Equation) condition).getOp()){
                    case EQUALS -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), compareValue) == 0;
                    };
                    case LESS_THAN -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), compareValue) < 0;
                    };
                    case LESS_THAN_OR_EQUAL_TO -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), compareValue) <= 0;
                    };
                    case GREATER_THAN -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), compareValue) > 0;
                    };
                    case GREATER_THAN_OR_EQUAL_TO -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), compareValue) >= 0;
                    };
                }

            } else if ( condition instanceof FunctionCall) {
                String fieldName = ((FunctionCall) condition).getFieldName();
                fieldIndex = columnNames.indexOf(fieldName);
                String fieldType = columnTypes.get(fieldIndex);
                ArrayList<String> args = ((FunctionCall) condition).getArgs();
                Object lower = TypeConverter.toObject(fieldType, args.get(0));
                Object upper = TypeConverter.toObject(fieldType, args.get(1));

                switch (((FunctionCall) condition).getFunction()){
                    case BETWEEN -> lambda = (ArrayList<Object> elem) -> {
                        return TypeConverter.compare(fieldType, elem.get(fieldIndex), lower) >= 0 && TypeConverter.compare(fieldType, elem.get(fieldIndex), upper) <= 0;
                    };
                }
            }

            tableContent = tableContent.stream().filter(lambda).collect(Collectors.toCollection(ArrayList::new));
        }

        int last = columnNames.size();
        for (int i = 0; i < tableContent.size(); i++){
            Integer origin = (Integer) tableContent.get(i).remove(last);
            pointerMapper.put(origin, i);
        }

        try {
            io.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }

    //-------------------------------------------------------------------------------------------------------------------------------//
                                            // WARNING:  JOINs are not functional yet //
    public void join(String childColumnName, String tableName) throws IOException {
        // CHECK if table given as parameter is the ParentTable of this Table
        int foreignKeyColumnIndex = columnNames.indexOf(childColumnName);

        ArrayList<String> parentColumnNames = (ArrayList<String>) CatalogManager.getFieldNames(databaseName, tableName);
        columnNames.addAll(parentColumnNames);
        columnTypes.addAll(CatalogManager.getFieldTypes(databaseName, tableName));

        String primaryKeyIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, tableName);
        UniqueIndexManager indexManager = new UniqueIndexManager(databaseName, tableName, primaryKeyIndexName);
        ArrayList<Integer> pointers = new ArrayList<>();
        for(var record : tableContent){
            Object foreignKey = record.get(foreignKeyColumnIndex);

            try {
                HashMap<Integer, Object> map = indexManager.equalityQuery(foreignKey);
                pointers.addAll(map.keySet());
            } catch (Exception ignored) {}
        }
        indexManager.close();

        RecordReader recordReader = new RecordReader(databaseName, tableName);

        try{
            ArrayList<ArrayList<Object>> records = recordReader.scanLines(pointers);

            for(int i = 0; i < tableContent.size(); i++){
                tableContent.get(i).addAll(records.get(i));
            }
        } catch (Exception ignored) {}

        recordReader.close();
    }

    //-------------------------------------------------------------------------------------------------------------------------------//



    public void printState(){
        System.out.println(columnNames);
        System.out.println(columnTypes);
        System.out.println(pointerMapper);
        for(var record : tableContent) {
            System.out.println(record);
        }
    }

    @Override
    public GroupedTable groupBy(ArrayList<String> groupingColumns) {
        return new GroupedTable(columnNames, columnTypes, groupingColumns, tableContent);
    }

    @Override
    public void aggregation(ArrayList<Aggregator> aggregators) {
        ArrayList<String> newTypes = new ArrayList<>();
        ArrayList<ArrayList<Object>> newTable = new ArrayList<>();
        ArrayList<Object> newRow = new ArrayList<>();
        for(var aggregator : aggregators) {
            String fieldName = aggregator.getFieldName();
            int fieldIndex = columnNames.indexOf(fieldName);
            String fieldType = columnTypes.get(fieldIndex);
            AggregatorSymbol aggregatorSymbol = aggregator.getAggr();
            newTypes.add(TypeConverter.mapAggregatorType(fieldType, aggregatorSymbol));

            ArrayList<Object> column = tableContent.stream().map((elem) -> elem.get(fieldIndex)).collect(Collectors.toCollection(ArrayList::new));

            newRow.add(Aggregator.resolve(aggregatorSymbol, column, fieldType));
        }
        newTable.add(newRow);
        
        tableContent = newTable;
        columnNames = aggregators.stream().map(Aggregator::getAlias).collect(Collectors.toCollection(ArrayList::new));
        columnTypes = newTypes;
    }

    @Override
    public void projection(ArrayList<String> wantedColumns){
        ArrayList<Integer> wantedColumnIndexes = new ArrayList<>();
        ArrayList<String> newColumnTypes = new ArrayList<>();
        for(var columnName : columnNames){
            if(wantedColumns.contains(columnName)){
                int i = columnNames.indexOf(columnName);
                newColumnTypes.add(columnTypes.get(i));
                wantedColumnIndexes.add(i);
            }
        }
        tableContent = tableContent.stream().map(elem -> {
            ArrayList<Object> newElem = new ArrayList<>();
            for(var i : wantedColumnIndexes){
                newElem.add(elem.get(i));
            }
            return newElem;
        }).collect(Collectors.toCollection(ArrayList::new));

        columnNames = wantedColumns;
        columnTypes = newColumnTypes;
    }
}
