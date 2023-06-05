package backend.validators;

import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.exceptions.databaseActionsExceptions.FieldNotFound;
import backend.exceptions.databaseActionsExceptions.TableDoesntExist;

public interface Validator {
    void validate() throws DatabaseDoesntExist, TableDoesntExist, FieldNotFound;
}
