package backend.Utilities;

import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.recordHandling.TypeConverter;
import lombok.Getter;

import java.util.ArrayList;
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

    public static JoinedTable join(ArrayList<BaseTable> tables){
        return null;
    }

    private static JoinedTable nestedJoin(){
        return null;
    }

    private static JoinedTable hashJoin(){
        return null;
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


}
