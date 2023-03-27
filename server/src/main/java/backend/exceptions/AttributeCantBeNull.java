package backend.exceptions;

public class AttributeCantBeNull extends Exception {
    public AttributeCantBeNull(String attributeName) {
        super("Attribute=" + attributeName + " can't be null!");
    }
}
