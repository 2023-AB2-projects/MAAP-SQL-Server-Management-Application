package backend.exceptions;

public class NoIndexException extends Exception{
    public NoIndexException() {super("No index on this field");}
}
