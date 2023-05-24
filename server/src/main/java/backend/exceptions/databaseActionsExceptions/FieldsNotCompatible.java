package backend.exceptions.databaseActionsExceptions;

import java.util.List;

public class FieldsNotCompatible extends Exception {

    public FieldsNotCompatible(Boolean forUniqueIndex, List<String> fields) {
        super("UniqueIndex Value:" + forUniqueIndex + ", for fields:" + fields.toString());
    }
}
