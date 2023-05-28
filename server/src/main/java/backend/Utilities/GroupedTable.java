package backend.Utilities;

import backend.databaseModels.aggregations.Aggregator;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupedTable implements Table{
    private ArrayList<String> groupedColumnNames, groupedColumnTypes;
    private ArrayList<String> unGroupedColumnNames, unGroupedColumnTypes;
    private HashMap<ArrayList<Object>, AnonymousTable> tableMap;

    public GroupedTable(ArrayList<String> columnNames, ArrayList<String> columnTypes, ArrayList<String> wantedColumns, ArrayList<ArrayList<Object>> tableContent) {
        groupedColumnNames = wantedColumns;
        groupedColumnTypes = new ArrayList<>();
        unGroupedColumnNames = new ArrayList<>();
        unGroupedColumnTypes = new ArrayList<>();
        tableMap = new HashMap<>();
        ArrayList<Integer> wantedColumnIndexes = new ArrayList<>();

        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i), columnType = columnTypes.get(i);

            if(wantedColumns.contains(columnName)) {
                groupedColumnTypes.add(columnType);
                wantedColumnIndexes.add(i);
            } else {
                unGroupedColumnNames.add(columnName);
                unGroupedColumnTypes.add(columnType);
            }
        }

        for (var row : tableContent) {
            ArrayList<Object> key = new ArrayList<>(), value = new ArrayList<>();

            for (int i = 0; i < row.size(); i++) {
                if (wantedColumnIndexes.contains(i)) {
                    key.add(row.get(i));
                } else {
                    value.add(row.get(i));
                }
            }

            if (tableMap.containsKey(key)) {
                tableMap.get(key).addNewRecord(value);
            } else {
                tableMap.put(key, new AnonymousTable(unGroupedColumnNames, unGroupedColumnTypes));
                tableMap.get(key).addNewRecord(value);
            }
        }
    }

    @Override
    public void aggregation(ArrayList<Aggregator> aggregators) {
        HashMap<ArrayList<Object>, AnonymousTable> newTableMap = new HashMap<>();
        AnonymousTable anonymousTable = null;
        for(var key : tableMap.keySet()){
            anonymousTable = tableMap.get(key);
            anonymousTable.aggregation(aggregators);
            ArrayList<Object> newKey = (ArrayList<Object>) key.clone();
            newKey.addAll(anonymousTable.getTableContent().get(0));
            newTableMap.put(newKey, anonymousTable);

        }

        assert anonymousTable != null;
        unGroupedColumnNames = anonymousTable.getColumnNames();
        unGroupedColumnTypes = anonymousTable.getColumnTypes();
        groupedColumnNames.addAll(unGroupedColumnNames);
        groupedColumnTypes.addAll(unGroupedColumnTypes);

        tableMap = newTableMap;
    }

    @Override
    public void projection(ArrayList<String> wantedColumns) {

    }

    public void printState() {
        System.out.println(groupedColumnNames);
        System.out.println(groupedColumnTypes);

        System.out.println(unGroupedColumnNames);
        System.out.println(unGroupedColumnTypes);

        for(var key : tableMap.keySet()){
            System.out.println(key);
        }
    }
}
