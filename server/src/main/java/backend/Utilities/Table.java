package backend.Utilities;

import backend.databaseModels.aggregations.Aggregator;

import java.util.ArrayList;

public interface Table {
    GroupedTable groupBy(ArrayList<String> groupingColumns);
    void aggregation(ArrayList<Aggregator> aggregators);
    void projection(ArrayList<String> wantedColumns);

}
