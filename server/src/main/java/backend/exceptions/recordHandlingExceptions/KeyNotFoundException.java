package backend.exceptions.recordHandlingExceptions;

public class KeyNotFoundException extends Exception{
    public KeyNotFoundException() {super("Key not in node");}
}
