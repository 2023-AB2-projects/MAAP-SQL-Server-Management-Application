package backend.databaseactions.createactions;

import backend.config.Config;
import backend.databaseactions.DatabaseAction;
import backend.exceptions.DatabaseNameAlreadyExists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Data
@Slf4j
@JsonRootName(value = "Database")
public class CreateDatabaseAction implements DatabaseAction {
    @JsonProperty
    private String databaseName;

    public CreateDatabaseAction(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void actionPerform() throws IOException, DatabaseNameAlreadyExists {
        // File that contains the whole catalog
        File catalog = Config.getCatalogFile();

        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode = mapper.readTree(catalog);

        // Get array that contains all the databases
        JsonNode databasesArrayValue = rootNode.get(Config.getDbCatalogRoot());
//        System.out.println("Current databases: " + databasesArrayValue);

        // Get current array of databases stored in 'Databases' json node
        ArrayNode catalogDatabaseNodes = (ArrayNode) databasesArrayValue;
        for (final JsonNode databaseNode : catalogDatabaseNodes) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("Database").get("databaseName");
            if (currentDatabaseNodeValue == null) {
                log.info("Database action -> Iterating databases -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(this.databaseName)) {
                log.info("Database action -> Iterating databases -> database already exists " + currentDatabaseName);
                throw new DatabaseNameAlreadyExists(currentDatabaseName);
            }
        }

        // Create new database
        JsonNode newDatabase = JsonNodeFactory.instance.objectNode().putPOJO("Database", this);
        catalogDatabaseNodes.add(newDatabase);        // Add the new database

        // Mapper -> Write entire catalog
        mapper.writeValue(catalog, rootNode);
    }
}
