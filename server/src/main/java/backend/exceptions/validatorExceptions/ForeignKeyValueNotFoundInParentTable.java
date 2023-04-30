package backend.exceptions.validatorExceptions;

public class ForeignKeyValueNotFoundInParentTable extends Exception {
    public ForeignKeyValueNotFoundInParentTable(String value) {
        super("Foreign key with value '" + value + "' not found in table!");
    }
}
