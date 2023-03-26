package backend.databaseactions.createactions;

import backend.config.Config;
import backend.databaseactions.DatabaseAction;
import backend.databaseelements.Attribute;
import backend.databaseelements.ForeignKey;
import backend.databaseelements.IndexFile;
import backend.databaseelements.PrimaryKey;
import backend.exceptions.DatabaseDoesntExist;
import backend.exceptions.ForeignKeyNotFound;
import backend.exceptions.PrimaryKeyNotFound;
import backend.exceptions.TableNameAlreadyExists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class CreateTableAction implements DatabaseAction {
    private final String databaseName;

    @JsonProperty
    private final String tableName, fileName;

    @JsonProperty
    private final int rowLength;

    @JsonProperty
    private final ArrayList<Attribute> attributes;

    @JsonProperty
    private final PrimaryKey primaryKey;

    @JsonProperty
    private final ArrayList<ForeignKey> foreignKeys;

    @JsonProperty
    private final ArrayList<String> uniqueAttributes;

    @JsonProperty
    private final ArrayList<IndexFile> indexFiles;

    public CreateTableAction(String databaseName, String tableName, String fileName, int rowLength, ArrayList<Attribute> attributes,
                             PrimaryKey primaryKey, ArrayList<ForeignKey> foreignKeys, ArrayList<String> uniqueAttributes, ArrayList<IndexFile> indexFiles) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.fileName = fileName;
        this.rowLength = rowLength;
        this.attributes = attributes;
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.uniqueAttributes = uniqueAttributes;
        this.indexFiles = indexFiles;
    }

    /* Utility */
    private JsonNode findDatabaseNode(JsonNode rootNode) {
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
            if(currentDatabaseName.equals(this.databaseName)) {
                return databaseNode;
            }
        }

        return null;
    }

    private boolean tableAlreadyExists(JsonNode databaseNode) {
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(this.tableName)) {
                return true;
            }
        }
        return false;
    }

    private boolean attributeExistsInTable(String attributeName) {
        // Iterate through all attributes and check if we have one with the given name
        for(final Attribute attribute : this.attributes) {
            if(attribute.attributeName().equals(attributeName)) return true;
        }
        return false;
    }
    /* / Utility */

    @Override
    public void actionPerform() throws TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound {
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
        JsonNode databaseNode = this.findDatabaseNode(rootNode);
        if(databaseNode == null) {
            log.error("CreateTableAction -> Database doesn't exits: " + this.databaseName + "!");
            throw new DatabaseDoesntExist(this.databaseName);
        }

        // Check if table already exists in database
        if (this.tableAlreadyExists(databaseNode)) {
            log.error("CreateTableAction -> Table already exists in database=" + this.databaseName + " tableName=" + this.tableName);
            throw new TableNameAlreadyExists(this.tableName);
        }

        // Check if PK attributes exist in table
        for (final String pKAttribute : this.primaryKey.primaryKeyAttributes()) {
            if(!this.attributeExistsInTable(pKAttribute)) {
                log.error("CreateTableAction -> PK doesn't exist in the table=" + this.tableName + ", pK=" + pKAttribute);
                throw new PrimaryKeyNotFound(this.tableName, pKAttribute);
            }
        }

        // Check if FK attributes exist in other tables

        // Create new table in database
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        JsonNode newTable = JsonNodeFactory.instance.objectNode().putPOJO("table", this);
        databaseTables.add(newTable);

        // Mapper -> Write entire catalog
        try {
            mapper.writeValue(catalog, rootNode);
        } catch (IOException e) {
            log.error("CreateTableAction -> Write value (mapper) failed");
            throw new RuntimeException(e);
        }
    }
}
