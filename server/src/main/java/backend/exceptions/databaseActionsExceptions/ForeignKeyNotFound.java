package backend.exceptions.databaseActionsExceptions;

import java.util.ArrayList;

public class ForeignKeyNotFound extends Exception {
    public ForeignKeyNotFound(String tableName, ArrayList<String> referencedAttributes) {
        super("In table=" + tableName + " there's no foreign key=" + referencedAttributes);
    }
}