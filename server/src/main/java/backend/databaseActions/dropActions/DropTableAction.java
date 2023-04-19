package backend.databaseActions.dropActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.exceptions.databaseActionsExceptions.TableDoesntExist;
import backend.service.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DropTableAction implements DatabaseAction {
    private final String tableName, databaseName;

    public DropTableAction(String tableName, String databaseName) {
        this.tableName = tableName;
        this.databaseName = databaseName;
    }

    /* Utility */
    private JsonNode findDatabaseNodeFromRoot(String databaseName, JsonNode rootNode) {
        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("CreateTableAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(databaseName)) {
                return databaseNode;
            }
        }

        return null;
    }
    /* /Utility */

    @Override
    public Object actionPerform() throws DatabaseDoesntExist, TableDoesntExist {
        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(Config.getCatalogFile());
        } catch (IOException exception) {
            log.error("DropTableAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Check if database exists
        JsonNode databaseNode = this.findDatabaseNodeFromRoot(this.databaseName, rootNode);
        if(databaseNode == null) {
            log.error("DropTableAction -> Database doesn't exits: " + this.databaseName + "!");
            throw new DatabaseDoesntExist(this.databaseName);
        }

        // Check if table exists
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        boolean tableExists = false;
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(this.tableName)) {
                tableExists = true;
                break;
            }
        }
        if(!tableExists) {
            log.error("DropTableAction -> Table= " + this.tableName + " doesn't exist in database=" + this.databaseName + "!");
            throw new TableDoesntExist(this.tableName, this.databaseName);
        }

        // Delete table from array
        int remove_ind = 0;
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(this.tableName)) {
                // Remove table folder
                String tableFolderPath = Config.getDbRecordsPath() + File.separator + this.databaseName + File.separator + this.tableName;
                if(!Utility.deleteDirectory(new File(tableFolderPath))) {
                    log.error("Database directory=" + tableFolderPath + " could not be deleted!");
                    throw new RuntimeException();
                }

                // Remove corresponding table node
                databaseTables.remove(remove_ind);

                // Mapper -> Write entire catalog
                try {
                    mapper.writeValue(Config.getCatalogFile(), rootNode);
                } catch (IOException e) {
                    log.error("DropTableAction -> Write value (mapper) failed");
                    throw new RuntimeException(e);
                }
                return null;
            }

            // Next table
            remove_ind += 1;
        }

        return null;
    }
}
