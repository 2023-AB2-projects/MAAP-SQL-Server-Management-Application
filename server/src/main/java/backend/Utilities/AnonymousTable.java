package backend.Utilities;

import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.recordHandling.TypeConverter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AnonymousTable implements Table {

    @Getter
    private ArrayList<String> columnNames, columnTypes;

    @Getter
    private ArrayList<ArrayList<Object>> tableContent;

    public AnonymousTable(ArrayList<String> columnNames, ArrayList<String> columnTypes) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        tableContent = new ArrayList<>();
    }

    public void addNewRecord(ArrayList<Object> record) {
        tableContent.add(record);
    }

    @Override
    public GroupedTable groupBy(ArrayList<String> groupingColumns) {
        return null;
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
        //unused
    }
}
