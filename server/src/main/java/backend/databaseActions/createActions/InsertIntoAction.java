package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.recordHandlingExceptions.InvalidTypeException;
import backend.exceptions.validatorExceptions.ForeignKeyValueNotFoundInParentTable;
import backend.exceptions.validatorExceptions.PrimaryKeyValueAlreadyInTable;
import backend.exceptions.validatorExceptions.UniqueValueAlreadyInTable;
import backend.recordHandling.RecordInserter;
import backend.recordHandling.RecordStandardizer;
import backend.service.CatalogManager;
import backend.service.InsertRowValidator;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        // Standardize all string values (add padding basically)
        List<String> fieldTypes = CatalogManager.getFieldTypes(this.databaseName, this.tableName);
        // Find all the indices in table, where we store strings
        List<Integer> stringIndexes = new ArrayList<>();
        Integer ind = 0;
        for(final String type : fieldTypes) {
            if (type.contains("char")) stringIndexes.add(ind);
            ind++;
        }

        if (!stringIndexes.isEmpty()) {
            // Iterate over values matrix and add padding to every column where there is a string
            for (final ArrayList<String> row : values) {
                // Add padding to certain indexes
                for (final Integer stringInd : stringIndexes) {
                    try {
                        row.set(stringInd, RecordStandardizer.formatString(row.get(stringInd), fieldTypes.get(stringInd)));
                    } catch (InvalidTypeException e) {
                        log.error("Could not add padding to string value -> Invalid type!");
                        throw new RuntimeException(e);
                    }
                }
            }
        }

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
