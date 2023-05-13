package backend.exceptions.recordHandlingExceptions;

public class UndefinedQueryException extends Exception{
    public UndefinedQueryException(){super("You ought not use this rangeQuery on an index with a complex key");}
}
