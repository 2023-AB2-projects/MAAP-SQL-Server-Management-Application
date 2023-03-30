package backend.exceptions.databaseActionsExceptions;

import backend.databaseModels.FieldModel;

import java.util.ArrayList;

public class FieldsAreNotUnique extends Exception {
    public FieldsAreNotUnique(ArrayList<FieldModel> attributes) {
        super("Attributes are not unique, attributes=" + attributes);
    }
}
