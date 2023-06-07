package backend.databaseModels;

import lombok.Getter;

import java.util.List;

public class JoinModel {
    @Getter
    private String leftTableName, leftFieldName, rightTableName, rightFieldName;

    public JoinModel(String leftTableName, String leftFieldName, String rightTableName, String rightFieldName) {
        this.leftTableName = leftTableName;
        this.leftFieldName = leftFieldName;
        this.rightTableName = rightTableName;
        this.rightFieldName = rightFieldName;
    }

    public void swap() {
        String temp = leftTableName;
        leftTableName = rightTableName;
        rightTableName = temp;

        temp = leftFieldName;
        leftFieldName = rightFieldName;
        rightFieldName = temp;
    }

    public String toString() {
        return leftFieldName + " = " + rightFieldName;
    }

    public static List<JoinModel> sort(List<JoinModel> models, List<String> tableNamesInOrder) {
        // check every model
        for ( JoinModel model : models) {
            int leftTableIndex = tableNamesInOrder.indexOf(model.getLeftTableName());
            int rightTableIndex = tableNamesInOrder.indexOf(model.getRightTableName());

            if (rightTableIndex < leftTableIndex) {
                model.swap();
            }
        }
        return models;
    }
}
