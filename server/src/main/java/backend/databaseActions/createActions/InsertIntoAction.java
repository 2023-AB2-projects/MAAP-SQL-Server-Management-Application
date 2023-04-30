package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.validatorExceptions.ForeignKeyValueNotFoundInParentTable;
import backend.exceptions.validatorExceptions.PrimaryKeyValueAlreadyInTable;
import backend.exceptions.validatorExceptions.UniqueValueAlreadyInTable;
import backend.recordHandling.RecordInserter;
import backend.service.InsertRowValidator;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@AllArgsConstructor
public class InsertIntoAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private ArrayList<ArrayList<String>> values;

    @Override
    public Object actionPerform() throws IOException {
        //TODO -> Add database check, table check
        //TODO check PK is set, unique columns are sets
        //TODO standardize value matrix

        InsertRowValidator rowValidator = new InsertRowValidator(this.databaseName, this.tableName);

        for (final ArrayList<String> row : values) {
            try {
                rowValidator.validateRow(row);
            } catch (PrimaryKeyValueAlreadyInTable e) {
                log.error("PK already in table!");
                throw new RuntimeException(e);
            } catch (UniqueValueAlreadyInTable e) {
                log.error("Unique field already in table!");
                throw new RuntimeException(e);
            } catch (ForeignKeyValueNotFoundInParentTable e) {
                log.error("Foreign key value not found in parent table!");
                throw new RuntimeException(e);
            }
        }

        RecordInserter recordInserter = new RecordInserter(this.databaseName, this.tableName);

        for (final ArrayList<String> row : values) {
            recordInserter.insert(row);
        }

        return null;
    }
}
