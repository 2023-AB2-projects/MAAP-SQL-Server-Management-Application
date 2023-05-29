package backend.databaseModels;

import lombok.Getter;

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
}
