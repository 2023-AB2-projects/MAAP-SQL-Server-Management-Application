package backend.exceptions.validatorExceptions;

public class UniqueFieldValuesContainDuplicates extends Exception {
    public UniqueFieldValuesContainDuplicates(String uniqueFieldName) {
        super("Unique column with name=" + uniqueFieldName + " contains duplicates!");
    }
}
