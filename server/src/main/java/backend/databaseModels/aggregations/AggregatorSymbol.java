package backend.databaseModels.aggregations;

public enum AggregatorSymbol {
    SUM, MIN, MAX, AVG, COUNT;

    public static AggregatorSymbol getAggregatorSymbol(String aggr){
        return switch (aggr) {
            case "sum" -> AggregatorSymbol.SUM;
            case "min" -> AggregatorSymbol.MIN;
            case "max" -> AggregatorSymbol.MAX;
            case "avg" -> AggregatorSymbol.AVG;
            case "count" -> AggregatorSymbol.COUNT;
            default -> throw new IllegalArgumentException("Unknown aggregator:" + aggr);
        };
    }

    public static String toString(AggregatorSymbol aggr){
        return switch (aggr) {
            case SUM -> "SUM";
            case MAX -> "MAX";
            case AVG -> "AVG";
            case MIN -> "MIN";
            case COUNT -> "COUNT";
        };
    }
}
