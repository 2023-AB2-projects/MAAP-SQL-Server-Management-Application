package backend.exceptions;

import backend.databaseModels.Attribute;

import java.util.ArrayList;

public class AttributesAreNotUnique extends Exception {
    public AttributesAreNotUnique(ArrayList<Attribute> attributes) {
        super("Attributes are not unique, attributes=" + attributes);
    }
}
