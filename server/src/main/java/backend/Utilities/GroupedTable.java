package backend.Utilities;

import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GroupedTable implements Table{

    @Override
    public void aggregation(ArrayList<Aggregator> aggregators) {

    }

    @Override
    public void projection(ArrayList<String> wantedColumns) {

    }
}
