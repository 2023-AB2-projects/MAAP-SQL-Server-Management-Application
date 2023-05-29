package backend.Utilities;

import backend.Indexing.Queryable;
import backend.Indexing.UniqueIndexManager;
import backend.databaseModels.JoinModel;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.recordHandling.RecordReader;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JoinedTable implements Table{
    @Getter
    private ArrayList<String> columnTypes, columnNames;
    @Getter
    private ArrayList<ArrayList<Object>> tableContent;

    private JoinedTable(ArrayList<String> columnTypes, ArrayList<String> columnNames, ArrayList<ArrayList<Object>> tableContent) {
        this.columnTypes = columnTypes;
        this.columnNames = columnNames;
        this.tableContent = tableContent;
    }

    public static JoinedTable join(ArrayList<BaseTable> tables, ArrayList<JoinModel> joins){
        return null;
    }

                                    // Nested Join \\
    //-----------------------------------------------------------------------------------------\\
    public static JoinedTable join(BaseTable parentTable, BaseTable childTable, JoinModel join) throws IOException {
        String databaseName = parentTable.getDatabaseName();
        ArrayList<String> parentColumnNames = parentTable.getColumnNames(), parentColumnTypes = parentTable.getColumnTypes();
        ArrayList<String> childColumnNames = childTable.getColumnNames(), childColumnTypes = childTable.getColumnTypes();
        ArrayList<String> columnNames = new ArrayList<>(), columnTypes = new ArrayList<>();
        columnNames.addAll(childColumnNames);
        columnNames.addAll(parentColumnNames);
        columnTypes.addAll(childColumnTypes);
        columnTypes.addAll(parentColumnTypes);

        String parentTableName = join.getLeftTableName(), childTableName = join.getRightTableName();
        String parentKey = join.getLeftFieldName(), foreignKey = join.getRightFieldName();

        ArrayList<ArrayList<Object>> parentTableContent = parentTable.getTableContent();
        ArrayList<ArrayList<Object>> childTableContent = childTable.getTableContent();
        ArrayList<ArrayList<Object>> tableContent = new ArrayList<>();

        //---------Intro Over--------\\

        int foreignKeyColumnIndex = childColumnNames.indexOf(foreignKey);
        HashMap<Integer, Integer> pointerMap = parentTable.getPointerMapper();
        String primaryKeyIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, parentTableName);

        UniqueIndexManager indexManager = new UniqueIndexManager(databaseName, parentTableName, primaryKeyIndexName);
        ArrayList<Integer> pointers = new ArrayList<>();

        for(var record : childTableContent){
            Object foreignKeyValue = record.get(foreignKeyColumnIndex);
            try {
                HashMap<Integer, Object> map = indexManager.equalityQuery(foreignKeyValue);
                pointers.addAll(map.keySet());
            } catch (Exception ignored) {}
        }
        indexManager.close();

        pointers = pointers.stream().map(pointerMap::get).collect(Collectors.toCollection(ArrayList::new));
        for(int i = 0; i < pointers.size(); i++) {
            if (pointers.get(i) != null) {
                ArrayList<Object> row = new ArrayList<>();
                row.addAll(childTableContent.get(i));
                row.addAll(parentTableContent.get(pointers.get(i)));
                tableContent.add(row);
            }
        }

        return new JoinedTable(columnTypes, columnNames, tableContent);
    }
    //------------------------------------------------------------------------------------------\\

                                    //  Hash Join  \\
    //------------------------------------------------------------------------------------------\\
    public static JoinedTable join(JoinedTable joinedTable, BaseTable baseTable, JoinModel join) {
        String databaseName = baseTable.getDatabaseName();
        ArrayList<String> leftColumnNames = joinedTable.getColumnNames(), leftColumnTypes = joinedTable.getColumnTypes();
        ArrayList<String> rightColumnNames = baseTable.getColumnNames(), rightColumnTypes = baseTable.getColumnTypes();
        ArrayList<String> columnNames = new ArrayList<>(), columnTypes = new ArrayList<>();
        columnNames.addAll(rightColumnNames);
        columnNames.addAll(leftColumnNames);
        columnTypes.addAll(rightColumnTypes);
        columnTypes.addAll(leftColumnTypes);

        //String parentTableName = join.getLeftTableName(), childTableName = join.getRightTableName();
        String leftKey = join.getLeftFieldName(), rightKey = join.getRightFieldName();

        ArrayList<ArrayList<Object>> leftTableContent = joinedTable.getTableContent();
        ArrayList<ArrayList<Object>> rightTableContent = baseTable.getTableContent();
        ArrayList<ArrayList<Object>> tableContent = new ArrayList<>();

        int leftKeyIndex = leftColumnNames.indexOf(leftKey), rightKeyIndex = rightColumnNames.indexOf(rightKey);
        HashMap<Object, ArrayList<Integer>> keyMap = new HashMap<>();

        for (int i = 0; i < leftTableContent.size(); i++) {
            Object leftKeyValue = leftTableContent.get(i).get(leftKeyIndex);
            if (keyMap.containsKey(leftKeyValue)) {
                keyMap.get(leftKeyValue).add(i);
            } else {
                keyMap.put(leftKeyValue, new ArrayList<>());
                keyMap.get(leftKeyValue).add(i);
            }
        }

        for (ArrayList<Object> rightRow : rightTableContent) {
            Object rightKeyValue = rightRow.get(rightKeyIndex);
            ArrayList<Integer> pointers = keyMap.get(rightKeyValue);

            if (pointers != null) {
                for (var pointer : pointers) {
                    ArrayList<Object> row = new ArrayList<>();
                    row.addAll(rightRow);
                    row.addAll(leftTableContent.get(pointer));
                    tableContent.add(row);
                }
            }
        }

        return new JoinedTable(columnTypes, columnNames, tableContent);
    }
    //-------------------------------------------------------------------------------------------\\

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
    public void projection(ArrayList<String> wantedColumns) {
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


    public void printState(){
        System.out.println(columnNames);
        System.out.println(columnTypes);
        for(var record : tableContent) {
            System.out.println(record);
        }
    }
}
