package backend.exceptions.recordHandlingExceptions;

public class InvalidWriteException extends Exception{
    public InvalidWriteException(){super("Tried to overwrite record");}
}
