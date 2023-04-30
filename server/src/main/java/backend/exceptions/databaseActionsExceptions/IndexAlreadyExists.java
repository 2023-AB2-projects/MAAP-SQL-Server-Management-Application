package backend.exceptions.databaseActionsExceptions;

public class IndexAlreadyExists extends Exception {
    public IndexAlreadyExists(String indexName, String tableName) {
        super("Index with name '" + indexName + "' already exists in table '" + tableName + "'");
    }
}
