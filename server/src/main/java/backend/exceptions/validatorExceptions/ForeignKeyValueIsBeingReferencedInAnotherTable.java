package backend.exceptions.validatorExceptions;

public class ForeignKeyValueIsBeingReferencedInAnotherTable extends Exception {
    public ForeignKeyValueIsBeingReferencedInAnotherTable() {
        super("Primary key is referenced in a child table DELETE not possible");
    }
}
