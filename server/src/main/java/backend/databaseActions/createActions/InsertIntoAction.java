package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.exceptions.databaseActionsExceptions.TableDoesntExist;
import backend.exceptions.recordHandlingExceptions.InvalidTypeException;
import backend.exceptions.validatorExceptions.*;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public Object actionPerform() throws IOException, PrimaryKeyValuesContainDuplicates, UniqueFieldValuesContainDuplicates, DatabaseDoesntExist, TableDoesntExist {
        // ----------------------------------- CHECK DB, TABLE NAME ------------------------------------------------- //
        if (!CatalogManager.getDatabaseNames().contains(this.databaseName)) {
            throw new DatabaseDoesntExist(this.databaseName);
        }

        if (!CatalogManager.getCurrentDatabaseTableNames().contains(this.tableName)) {
            throw new TableDoesntExist(this.tableName, this.databaseName);
        }
        // ---------------------------------- / CHECK DB, TABLE NAME ------------------------------------------------ //

        // ----------------------------------- DUPLICATE VALUES CHECK ----------------------------------------------- //
        List<String> fieldNames = CatalogManager.getFieldNames(this.databaseName, this.tableName);
        // Check PK is set, unique columns are sets
        // Find primary key fields indexes and unique column indexes
        List<Integer> primaryKeyIndexes = CatalogManager.getPrimaryKeyFieldIndexes(this.databaseName, this.tableName);
        List<Integer> uniqueFieldIndexes = CatalogManager.getUniqueFieldIndexes(this.databaseName, this.tableName);

        // Now find the primary key values for each column and check if they are unique
        int rowCount = values.size();
        for(final Integer pKIndex : primaryKeyIndexes) {
            // Extract from matrix only the column that we want to check -> Convert to set
            Set<String> pKColumnValuesSet = values.stream()
                    .map(row -> row.get(pKIndex))
                    .collect(Collectors.toSet());

            // Check if there were any duplicates in the column
            if (pKColumnValuesSet.size() != rowCount) {
                throw new PrimaryKeyValuesContainDuplicates(fieldNames.get(pKIndex));
            }
        }

        // Same for unique columns
        for(final Integer uniqueFieldIndex : uniqueFieldIndexes) {
            // Extract from matrix only the column that we want to check -> Convert to set
            Set<String> uniqueColumnSet = values.stream()
                    .map(row -> row.get(uniqueFieldIndex))
                    .collect(Collectors.toSet());

            // Check if there were any duplicates in the column
            if (uniqueColumnSet.size() != rowCount) {
                throw new UniqueFieldValuesContainDuplicates(fieldNames.get(uniqueFieldIndex));
            }
        }
        // ---------------------------------- / DUPLICATE VALUES CHECK ---------------------------------------------- //

        // --------------------------------- STANDARDIZE ALL STRINGS ------------------------------------------------ //
        // Standardize all string values (add padding basically)
        List<String> fieldTypes = CatalogManager.getFieldTypes(this.databaseName, this.tableName);
        // Find all the indices in table, where we store strings
        List<Integer> stringIndexes = IntStream.range(0, fieldTypes.size())
                .boxed()        // Convert primitive to Integer
                .filter(ind -> fieldTypes.get(ind).contains("char"))
                .toList();

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
        // --------------------------------- / STANDARDIZE ALL STRINGS ---------------------------------------------- //


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
