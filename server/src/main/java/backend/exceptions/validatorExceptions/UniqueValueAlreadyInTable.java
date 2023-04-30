package backend.exceptions.validatorExceptions;

public class UniqueValueAlreadyInTable extends Exception {
    public UniqueValueAlreadyInTable(String value) {
        super("Unique field with value '" + value + "' already exists in table!");
    }
}
