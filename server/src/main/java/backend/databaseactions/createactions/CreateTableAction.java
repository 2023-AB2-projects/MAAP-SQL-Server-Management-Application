package backend.databaseactions.createactions;

import backend.config.Config;
import backend.databaseactions.DatabaseAction;
import backend.databaseelements.Attribute;
import backend.databaseelements.IndexFile;
import backend.exceptions.DatabaseDoesntExist;
import backend.exceptions.TableNameAlreadyExists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Data
@Slf4j
public class CreateTableAction implements DatabaseAction {
    private String databaseName;

    @JsonProperty
    private String tableName, fileName;

    @JsonProperty
    private int rowLength;

    @JsonProperty
    private ArrayList<Attribute> attributes;

    @JsonProperty
    private ArrayList<String> pKAttributes, fKAttributes;

    @JsonProperty
    private ArrayList<IndexFile> indexFiles;

    public CreateTableAction(String databaseName, String tableName, String fileName, int rowLength, ArrayList<Attribute> attributes,
                             ArrayList<String> pKAttributes, ArrayList<String> fKAttributes, ArrayList<IndexFile> indexFiles) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.fileName = fileName;
        this.rowLength = rowLength;
        this.attributes = attributes;
        this.pKAttributes = pKAttributes;
        this.fKAttributes = fKAttributes;
        this.indexFiles = indexFiles;
    }

    @Override
    public void actionPerform() throws TableNameAlreadyExists, DatabaseDoesntExist {
        // File that contains the whole catalog
        File catalog = Config.getCatalogFile();

        // Object mapper with indented output
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(catalog);
        } catch (IOException exception) {
            log.error("CreateTableAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        boolean databaseExists = false;
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("CreateTableAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if(currentDatabaseName.equals(this.databaseName)) {
                databaseExists = true;
                break;
            }
        }
        if(!databaseExists) {
            log.error("CreateTableAction -> Database doesn't exits: " + this.databaseName + "!");
            throw new DatabaseDoesntExist(this.databaseName);
        }
    }
}
