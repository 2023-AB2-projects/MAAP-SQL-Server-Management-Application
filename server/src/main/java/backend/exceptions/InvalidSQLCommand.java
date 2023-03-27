package backend.exceptions;

public class InvalidSQLCommand extends Exception {
    public InvalidSQLCommand(String msg) {
        super(msg);
    }
}
