package backend.databaseActions.createActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.IndexFileModel;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.exceptions.databaseActionsExceptions.IndexAlreadyExists;
import backend.exceptions.databaseActionsExceptions.TableDoesntExist;
import backend.exceptions.databaseActionsExceptions.TableNameAlreadyExists;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CreateIndexAction implements DatabaseAction {
    // Information needed
    private final String tableName, databaseName;

    // Data
    private final IndexFileModel indexFile;

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

    private JsonNode findTableNode(String tableName, JsonNode databaseNode) {
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(tableName)) {
                return tableNode;
            }
        }
        return null;
    }
    /* /Utility */

    public CreateIndexAction(String tableName, String databaseName, IndexFileModel indexFile) {
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.indexFile = indexFile;
    }

    @Override
    public Object actionPerform() throws DatabaseDoesntExist, TableDoesntExist, IndexAlreadyExists {
        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(Config.getCatalogFile());
        } catch (IOException exception) {
            log.error("CreateIndexAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Check if database exists
        JsonNode databaseNode = this.findDatabaseNodeFromRoot(this.databaseName, rootNode);
        if(databaseNode == null) {
            log.error("CreateIndexAction -> Database doesn't exits: " + this.databaseName + "!");
            throw new DatabaseDoesntExist(this.databaseName);
        }

        // Check if table exists in database
        JsonNode tableNode = this.findTableNode(this.tableName, databaseNode);
        if (tableNode == null) {
            log.error("CreateIndexAction -> Table doesn't exist in database=" + this.databaseName + " tableName=" + this.tableName);
            throw new TableDoesntExist(this.tableName, this.databaseName);
        }

        // Check if an index file with the same name already exists
        ArrayNode indexFilesNode = (ArrayNode) tableNode.get("table").get("indexFiles");
        for(final JsonNode indexFileNode : indexFilesNode) {
            String currentIndexName = indexFileNode.get("indexFile").get("indexName").asText();
            if(this.indexFile.getIndexName().equals(currentIndexName)) {
                log.error("CreateIndexAction -> Index file with name=" + this.indexFile.getIndexName() +
                        " already exists in table=" + this.tableName);
                throw new IndexAlreadyExists(this.indexFile.getIndexName(), this.tableName);
            }
        }


        // Add index to table
        JsonNode newIndex = JsonNodeFactory.instance.objectNode().putPOJO("indexFile", this.indexFile);
        indexFilesNode.add(newIndex);

        // Mapper -> Write entire catalog
        try {
            mapper.writeValue(Config.getCatalogFile(), rootNode);
        } catch (IOException e) {
            log.error("CreateIndexAction -> Write value (mapper) failed");
            throw new RuntimeException(e);
        }

        return null;
    }
}
