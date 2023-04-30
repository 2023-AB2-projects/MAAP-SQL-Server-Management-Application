package backend.service;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteRowValidator {
    private ArrayList<String> foreignKeyFieldNames;
    private HashMap<String, ArrayList<String>> referencedByIndexes;

    private void validateForeignKey(String foreignKeyName, String foreignKeyValue) {

    }

    public DeleteRowValidator(String databaseName, String tableName) {
        // find each index file which is referencing each foreign
    }

    public void validateRow(ArrayList<String> row) {
        //
    }
}
