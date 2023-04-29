package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.RecordDeleter;
import backend.recordHandling.RecordInserter;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class InsertAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private ArrayList<ArrayList<String>> values;

    @Override
    public Object actionPerform() throws IOException {
        RecordInserter recordInserter = new RecordInserter(databaseName, tableName);
        for (var row : values) {
            // Validate rows and throw exceptions
        }

        for (var row: values) {
            recordInserter.insert(row);
        }

        //TODO

        return null;
    }
}
