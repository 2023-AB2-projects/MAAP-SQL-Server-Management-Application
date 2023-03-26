package backend.databaseactions.createactions;

import backend.config.Config;
import backend.databaseactions.DatabaseAction;
import backend.exceptions.DatabaseNameAlreadyExists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class CreateDatabaseAction implements DatabaseAction {
    @JsonProperty
    private String databaseName;

    @JsonProperty
    private List<String> tables;

    public CreateDatabaseAction(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new ArrayList<>();
    }

    @Override
    public void actionPerform() throws DatabaseNameAlreadyExists {
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
                log.error("CreateDatabaseAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(this.databaseName)) {
                log.info("CreateDatabaseAction -> database already exists " + currentDatabaseName);
                throw new DatabaseNameAlreadyExists(currentDatabaseName);
            }
        }

        // Create new database
        JsonNode newDatabase = JsonNodeFactory.instance.objectNode().putPOJO("database", this);
        databasesArray.add(newDatabase);        // Add the new database

        // Mapper -> Write entire catalog
        try {
            mapper.writeValue(catalog, rootNode);
        } catch (IOException e) {
            log.error("CreateDatabaseAction -> Write value (mapper) failed");
            throw new RuntimeException(e);
        }
    }
}
