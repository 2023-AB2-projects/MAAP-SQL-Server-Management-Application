package backend.exceptions.validatorExceptions;

public class PrimaryKeyValuesContainDuplicates extends Exception {
    public PrimaryKeyValuesContainDuplicates(String primaryKeyFieldName) {
        super("Primary key column with name=" + primaryKeyFieldName + " contains duplicates!");
    }
}
