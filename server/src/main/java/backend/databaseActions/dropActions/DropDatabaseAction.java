package backend.databaseActions.dropActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DropDatabaseAction implements DatabaseAction {
    private final DatabaseModel database;

    // TODO: replace DatabaseModel with simple String
    public DropDatabaseAction(DatabaseModel databaseModel) {
        this.database = databaseModel;
    }

    @Override
    public Object actionPerform() throws DatabaseDoesntExist {
        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(Config.getCatalogFile());
        } catch (IOException exception) {
            log.error("DropDatabaseAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Get current array of databases stored in 'Databases' json node
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        int removed_ind = 0;
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("UseDatabaseAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();

            // If the current databaseName is equal to the deleted database
            if(currentDatabaseName.equals(this.database.databaseName())) {
                // Remove database and write back
                databasesArray.remove(removed_ind);

                // Mapper -> Write entire catalog
                try {
                    mapper.writeValue(Config.getCatalogFile(), rootNode);
                } catch (IOException e) {
                    log.error("CreateDatabaseAction -> Write value (mapper) failed");
                    throw new RuntimeException(e);
                }
                return null;
            }

            // Next database index
            removed_ind += 1;
        }
        throw new DatabaseDoesntExist(this.database.databaseName());
    }
}
