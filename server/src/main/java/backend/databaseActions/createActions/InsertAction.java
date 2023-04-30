package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.recordHandling.RecordDeleter;
import backend.recordHandling.RecordInserter;
import backend.service.InsertRowValidator;
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
        InsertRowValidator rowValidator = new InsertRowValidator(this.databaseName, this.tableName);

        for (var row : values) {
            rowValidator.validateRow(row);
        }

        for (var row: values) {
        }

        //TODO

        return null;
    }
}
