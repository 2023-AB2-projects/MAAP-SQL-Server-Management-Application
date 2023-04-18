package backend.exceptions.recordHandlingExceptions;

public class InvalidDeleteException extends Exception{
    public InvalidDeleteException() {super("Tried to delete non-existent record");}
}
