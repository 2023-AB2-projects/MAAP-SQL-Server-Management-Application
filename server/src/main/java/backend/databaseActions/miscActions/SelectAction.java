package backend.databaseActions.miscActions;

import backend.databaseActions.DatabaseAction;
import backend.databaseModels.JoinModel;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.conditions.Condition;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@AllArgsConstructor
public class SelectAction implements DatabaseAction {
    @Setter
    private ArrayList<String> tableNames;
    @Setter
    private ArrayList<String> fieldNames, aliasFieldNames;  // FieldName: tableName.fieldName

    @Setter
    private ArrayList<JoinModel> joins;

    @Setter
    private ArrayList<Aggregator> aggregators;
    @Setter
    private ArrayList<Condition> conditions;
    @Setter
    private ArrayList<String> groupByFieldNames;

    @Override
    public Object actionPerform() {
        return null;
    }
}
