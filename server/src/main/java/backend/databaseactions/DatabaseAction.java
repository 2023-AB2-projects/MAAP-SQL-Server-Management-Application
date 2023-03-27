package backend.databaseactions;

import backend.exceptions.DatabaseNameAlreadyExists;

import java.io.IOException;

public interface DatabaseAction {
    void actionPerform() throws IOException, DatabaseNameAlreadyExists;
}
