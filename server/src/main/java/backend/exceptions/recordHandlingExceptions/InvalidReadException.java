package backend.exceptions.recordHandlingExceptions;

public class InvalidReadException extends Exception{
    public InvalidReadException(){super("Tried to read deleted record");}
}
