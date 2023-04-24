package backend.exceptions.recordHandlingExceptions;

public class DeletedRecordLinesEmpty extends Exception {
    public DeletedRecordLinesEmpty() {super("RecordLines deque is empty!");}
}
