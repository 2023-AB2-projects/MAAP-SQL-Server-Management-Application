package backend.Indexing;

import backend.service.CatalogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipleIndexUpdater {
    // List of field names, PK, FK and unique
    private final List<String> tableFieldNames, primaryKeyFieldNames, uniqueFieldNames;

    // Primary key has one index manager, each FK and unique fields have one manager
    private final UniqueIndexManager primaryKeyIndexManager;
    private final ArrayList<UniqueIndexManager> uniqueIndexManagers;

    private final ArrayList<NonUniqueIndexManager> nonUniqueIndexManagers;

    public MultipleIndexUpdater(String databaseName, String tableName) {
        // Get table field names
        this.tableFieldNames = CatalogManager.getFieldNames(databaseName, tableName);

        // Find each primary key, unique field and foreign key in table
        this.primaryKeyFieldNames = CatalogManager.getPrimaryKeyFieldNames(databaseName, tableName);
        this.uniqueFieldNames = CatalogManager.getUniqueFieldNames(databaseName, tableName);

        // Create index for primary key
        String pKIndexName = CatalogManager.getPrimaryKeyIndexName(databaseName, tableName);
        this.primaryKeyIndexManager = new UniqueIndexManager(databaseName, tableName, pKIndexName);

        // All the unique key index names
        List<String> uniqueIndexNames = CatalogManager.getUniqueFieldIndexNames(databaseName, tableName);
        this.uniqueIndexManagers = new ArrayList<>(this.uniqueFieldNames.size());
        for (final String indexName : uniqueIndexNames) {
            this.uniqueIndexManagers.add(new UniqueIndexManager(databaseName, tableName, indexName));
        }

        nonUniqueIndexManagers = new ArrayList<>();
    }

    public void insert(ArrayList<String> row, Integer pointer) {
        ArrayList<String> primaryKeyValues = new ArrayList<>();
        for (final String primaryKeyFieldName : primaryKeyFieldNames) {
            primaryKeyValues.add(row.get(tableFieldNames.indexOf(primaryKeyFieldName)));
        }

        ArrayList<String> uniqueFieldValues = new ArrayList<>();
        for (final String uniqueFieldName : uniqueFieldNames) {
            uniqueFieldValues.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
        }

        try {
            primaryKeyIndexManager.insert(primaryKeyValues, pointer);
            for(int i = 0; i < uniqueIndexManagers.size(); i++){
                ArrayList<String> values = new ArrayList<>();
                values.add(uniqueFieldValues.get(i));
                uniqueIndexManagers.get(i).insert(values, pointer);
            }
            //nonUniqueInsert
        }catch (Exception e){
            System.out.println("Something went wrong with insert");
            System.out.println(e.getMessage());
        }
    }

    public void delete(ArrayList<String> row, Integer pointer){
        ArrayList<String> primaryKeyValues = new ArrayList<>();
        for (final String primaryKeyFieldName : primaryKeyFieldNames) {
            primaryKeyValues.add(row.get(tableFieldNames.indexOf(primaryKeyFieldName)));
        }

        ArrayList<String> uniqueFieldValues = new ArrayList<>();
        for (final String uniqueFieldName : uniqueFieldNames) {
            uniqueFieldValues.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
        }

        try {
            primaryKeyIndexManager.delete(primaryKeyValues);
            for(int i = 0; i < uniqueIndexManagers.size(); i++){
                ArrayList<String> values = new ArrayList<>();
                values.add(uniqueFieldValues.get(i));
                uniqueIndexManagers.get(i).delete(values);
            }
            //nonUniqueDelete
        }catch (Exception e){
            System.out.println("Something went wrong with insert");
            System.out.println(e.getMessage());
        }
    }

    public void close() throws IOException {
        primaryKeyIndexManager.close();
        for(var manager : uniqueIndexManagers){
            manager.close();
        }
//        for(var manager : nonUniqueIndexManagers){
//            manager.close();
//        }
    }
}
