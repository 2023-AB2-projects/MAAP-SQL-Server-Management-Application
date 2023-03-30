package backend.exceptions.databaseActionsExceptions;

public class IndexAlreadyExists extends Exception {
    public IndexAlreadyExists(String indexName, String tableName) {
        super("indexName=" + indexName + " already exists in table=" + tableName);
    }
}
