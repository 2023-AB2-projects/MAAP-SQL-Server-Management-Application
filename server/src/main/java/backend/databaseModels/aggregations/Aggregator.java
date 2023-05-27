package backend.databaseModels.aggregations;

import lombok.Getter;

@Getter
public class Aggregator {
    private final String fieldName;
    private final AggregatorSymbol aggr;

    public Aggregator(String fieldName, AggregatorSymbol aggr) {
        this.fieldName = fieldName;
        this.aggr = aggr;
    }
}
