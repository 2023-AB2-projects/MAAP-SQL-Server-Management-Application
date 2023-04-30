package backend.exceptions.validatorExceptions;

public class PrimaryKeyValueAlreadyInTable extends Exception {
    public PrimaryKeyValueAlreadyInTable(String value) {
        super("Primary key with value(s)=" + value + " already exists in table!");
    }
}
