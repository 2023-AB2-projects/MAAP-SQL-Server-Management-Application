package backend.exceptions.validatorExceptions;

public class ForeignKeyValueIsBeingReferencedInAnotherTable extends Exception {
    public ForeignKeyValueIsBeingReferencedInAnotherTable(String value, String tableName) {
        super("Foreign key with value '" + value + "' already found in table '" + tableName + "'!");
    }
}
