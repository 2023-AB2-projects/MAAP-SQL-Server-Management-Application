package backend.databaseModels.aggregations;

import backend.recordHandling.TypeConverter;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Aggregator {
    private final String fieldName;
    private final String alias;
    private final AggregatorSymbol aggr;
    public Aggregator(String fieldName, AggregatorSymbol aggr) {
        this.fieldName = fieldName;
        this.aggr = aggr;
        this.alias = AggregatorSymbol.toString(aggr) + "(" + fieldName + ")";
    }

    public Aggregator(String fieldName, AggregatorSymbol aggr, String alias) {
        this.fieldName = fieldName;
        this.alias = alias;
        this.aggr = aggr;
    }

    private static Object resolveMIN(ArrayList<Object> column, String columnType){
        Object min = column.get(0);
        for(var elem : column) {
            if(TypeConverter.compare(columnType, elem, min) < 0) {
                min = elem;
            }
        }
        return min;
    }

    private static Object resolveMAX(ArrayList<Object> column, String columnType) {
        Object max = column.get(0);
        for(var elem : column) {
            if(TypeConverter.compare(columnType, elem, max) > 0) {
                max = elem;
            }
        }
        return max;
    }

    private static Object resolveSUM(ArrayList<Object> column, String columnType) {
        Object sum = column.get(0);
        for(int i = 1; i < column.size(); i++) {
            sum = TypeConverter.addObjects(columnType, sum, column.get(i));
        }
        return sum;
    }

    private static Object resolveAVG(ArrayList<Object> column, String columnType) {
        Object sum = resolveSUM(column, columnType);
        if(columnType.equals("int")) {
            return (float) (int) sum / column.size();
        } else {
            return (float) sum / column.size();
        }
    }
    public static Object resolve(AggregatorSymbol aggregatorSymbol, ArrayList<Object> column, String columnType){
        if(column.size() == 0) {
            return null;
        }
        return switch (aggregatorSymbol) {
            case SUM -> resolveSUM(column, columnType);
            case MIN -> resolveMIN(column, columnType);
            case MAX -> resolveMAX(column, columnType);
            case AVG -> resolveAVG(column, columnType);
            case COUNT -> column.size();
        };
    }
}
