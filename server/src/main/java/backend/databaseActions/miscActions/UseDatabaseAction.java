package backend.databaseActions.miscActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class UseDatabaseAction implements DatabaseAction {
    private final DatabaseModel database;

    public UseDatabaseAction(DatabaseModel databaseModel) {
        this.database = databaseModel;
    }

    @Override
    public Object actionPerform() throws DatabaseDoesntExist {
        // File that contains the whole catalog
        File catalog = Config.getCatalogFile();

        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(catalog);
        } catch (IOException exception) {
            log.error("CreateDatabaseAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Get current array of databases stored in 'Databases' json node
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("UseDatabaseAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(this.database.getDatabaseName())) {
                return this.database.getDatabaseName();
            }
        }

        // Database doesn't exists
        throw new DatabaseDoesntExist(this.database.getDatabaseName());
    }
}
