package backend.exceptions;

import backend.databaseModels.AttributeModel;

import java.util.ArrayList;

public class AttributesAreNotUnique extends Exception {
    public AttributesAreNotUnique(ArrayList<AttributeModel> attributes) {
        super("Attributes are not unique, attributes=" + attributes);
    }
}
