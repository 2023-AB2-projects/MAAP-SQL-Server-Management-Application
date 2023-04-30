package backend.exceptions.databaseActionsExceptions;

import backend.databaseModels.FieldModel;

import java.util.ArrayList;

public class FieldsAreNotUnique extends Exception {
    public FieldsAreNotUnique(ArrayList<FieldModel> attributes) {
        super("Fields are not unique, fields '" + attributes + "'");
    }
}
