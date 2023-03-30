package backend.exceptions.databaseActionsExceptions;

public class FieldCantBeNull extends Exception {
    public FieldCantBeNull(String fieldName) {
        super("Attribute=" + fieldName + " can't be null!");
    }
}
