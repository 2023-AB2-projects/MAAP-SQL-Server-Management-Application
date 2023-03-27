package backend.exceptions;

public class AttributeNotFound extends Exception {
    public AttributeNotFound(String attributeName, String tableName) {
        super("Attribute=" + attributeName + " not found in table=" + tableName + "!");
    }
}
