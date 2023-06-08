package test.backend;

import backend.databaseActions.themightySelectAction.SelectAction;
import backend.databaseModels.conditions.*;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.InvalidReadException;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.*;

import java.io.IOException;
import java.util.ArrayList;

public class TestSelectAction {
    public static void main(String[] args) {
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(new Equation("users", "a", Operator.EQUALS, "10", "a"));
        conditions.add(new Equation("package", "a", Operator.EQUALS, "20", "a"));
        conditions.add(new Equation("package", "a", Operator.EQUALS, "20", "a"));
        conditions.add(new Equation("cucc","a",  Operator.EQUALS, "10", "a"));
        conditions.add(new Equation("cucc", "a", Operator.EQUALS, "10", "a"));
        conditions.add(new Equation("users","a", Operator.EQUALS, "20", "a"));

        ArrayList<String> arg = new ArrayList<>();
        arg.add("1");
        arg.add("2");
        conditions.add(new FunctionCall("users", "cucc", Function.BETWEEN, arg));
        conditions.add(new FunctionCall("users", "cucc", Function.BETWEEN, arg));
        conditions.add(new FunctionCall("cucc", "cucc", Function.BETWEEN, arg));
        conditions.add(new FunctionCall("cucc", "cucc", Function.BETWEEN, arg));

        SelectAction selectActionTest = new SelectAction("database", "table",  null, conditions, null, null, null, null);
        try {
            selectActionTest.actionPerform();
        } catch (DatabaseNameAlreadyExists e) {
            throw new RuntimeException(e);
        } catch (TableNameAlreadyExists e) {
            throw new RuntimeException(e);
        } catch (DatabaseDoesntExist e) {
            throw new RuntimeException(e);
        } catch (PrimaryKeyNotFound e) {
            throw new RuntimeException(e);
        } catch (ForeignKeyNotFound e) {
            throw new RuntimeException(e);
        } catch (FieldCantBeNull e) {
            throw new RuntimeException(e);
        } catch (FieldsAreNotUnique e) {
            throw new RuntimeException(e);
        } catch (TableDoesntExist e) {
            throw new RuntimeException(e);
        } catch (IndexAlreadyExists e) {
            throw new RuntimeException(e);
        } catch (ForeignKeyFieldNotFound e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        } catch (PrimaryKeyValuesContainDuplicates e) {
            throw new RuntimeException(e);
        } catch (UniqueFieldValuesContainDuplicates e) {
            throw new RuntimeException(e);
        } catch (PrimaryKeyValueAlreadyInTable e) {
            throw new RuntimeException(e);
        } catch (UniqueValueAlreadyInTable e) {
            throw new RuntimeException(e);
        } catch (ForeignKeyValueNotFoundInParentTable e) {
            throw new RuntimeException(e);
        } catch (InvalidReadException e) {
            throw new RuntimeException(e);
        } catch (ForeignKeyValueIsBeingReferencedInAnotherTable e) {
            throw new RuntimeException(e);
        } catch (FieldsNotCompatible e) {
            throw new RuntimeException(e);
        }

    }
}