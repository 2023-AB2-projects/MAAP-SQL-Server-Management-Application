package backend.databaseActions.createActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.*;
import backend.exceptions.*;
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

    private boolean givenAttributesAreUnique() {
        return this.attributes.stream().map(Attribute::attributeName).distinct().count() == this.attributes.size();
    }

    private boolean tableAlreadyExists(String tableName, JsonNode databaseNode) {
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    private boolean attributeExistsInThisTable(String attributeName) {
        // Iterate through all attributes and check if we have one with the given name
        for(final Attribute attribute : this.attributes) {
            if(attribute.attributeName().equals(attributeName)) return true;
        }
        return false;
    }

    private boolean attributeIsNullableInThisTable(String givenAttributeName) throws AttributeNotFound {
        // Iterate though all given attributes
        for(final Attribute attribute : this.attributes) {
            if(attribute.attributeName().equals(givenAttributeName)) {
                // Check if it's null or not
                return attribute.isNullable();
            }
        }

        // Throw exception -> Attribute doesn't exist in current table
        throw new AttributeNotFound(givenAttributeName, this.tableName);
    }

    private boolean foreignKeyExistsInTable(ForeignKey foreignKey, JsonNode databaseNode) throws DatabaseDoesntExist {
        // First check if referenced table exists
        String referencedDatabaseName = foreignKey.referencedTable();
        if(!this.tableAlreadyExists(referencedDatabaseName, databaseNode)) {
            log.error("CreateTable -> Foreign key check -> database=" + referencedDatabaseName + " doesn't exist!");
            throw new DatabaseDoesntExist(referencedDatabaseName);
        }

        // Find referenced table
        JsonNode databaseTableNode = null;
        ArrayNode tablesInDatabase = (ArrayNode) databaseNode.get("database").get("tables");
        for(final JsonNode tableNode : tablesInDatabase) {
            if(tableNode.get("table").get("tableName").asText().equals(referencedDatabaseName)) {
                databaseTableNode = tableNode;
                break;
            }
        }

        // Just for safety
        if(databaseTableNode == null) {
            log.error("CreateTable -> Foreign key check -> database=" + referencedDatabaseName + " doesn't exist!");
            throw new DatabaseDoesntExist(referencedDatabaseName);
        }

        // Now check if the referenced attributes are in the primary key of the referenced table
        ArrayNode primaryKeyAttributes = (ArrayNode) databaseTableNode.get("table").get("primaryKey").get("primaryKeyAttributes");
        for(final String foreignKeyAttribute : foreignKey.referencedAttributes()) {
            boolean currentAttributeIsPresent = false;
            for(final JsonNode primaryKeyAttributeNode : primaryKeyAttributes) {
                if(primaryKeyAttributeNode.asText().equals(foreignKeyAttribute)) {
                    currentAttributeIsPresent = true;
                }
            }

            // If the current foreign key attribute was not present in the primary key attributes
            if(!currentAttributeIsPresent) return false;
        }

        return true;
    }
    /* / Utility */

    @Override
    public void actionPerform() throws TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, AttributeCantBeNull, AttributesAreNotUnique {
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
        JsonNode databaseNode = this.findDatabaseNodeFromRoot(this.databaseName, rootNode);
        if(databaseNode == null) {
            log.error("CreateTableAction -> Database doesn't exits: " + this.databaseName + "!");
            throw new DatabaseDoesntExist(this.databaseName);
        }

        // Check if given table attributes are not unique
        if(!this.givenAttributesAreUnique()) {
            log.error("CreateTableAction -> Attributes are not unique!");
            throw new AttributesAreNotUnique(this.attributes);
        }

        // Check if table already exists in database
        if (this.tableAlreadyExists(this.tableName, databaseNode)) {
            log.error("CreateTableAction -> Table already exists in database=" + this.databaseName + " tableName=" + this.tableName);
            throw new TableNameAlreadyExists(this.tableName);
        }

        // PK check
        for (final String pKAttribute : this.primaryKey.primaryKeyAttributes()) {
            // Check if PK attributes exist in table
            if(!this.attributeExistsInThisTable(pKAttribute)) {
                log.error("CreateTableAction -> PK doesn't exist in the table=" + this.tableName + ", pK=" + pKAttribute);
                throw new PrimaryKeyNotFound(this.tableName, pKAttribute);
            }

            // Check if PK attributes are nullable (if they are, they can't be primary key attributes)
            try {
                if(this.attributeIsNullableInThisTable(pKAttribute)) {
                    log.error("CreateTableAction -> PK attribute can't be nullable!");
                    throw new AttributeCantBeNull(pKAttribute);
                }
            } catch (AttributeNotFound e) {
                log.error("CreateTableAction -> PK attribute (this is a problem since we checked above) doesn't exist" +
                        " in table=" + this.tableName + ", pkAttribute=" + pKAttribute);
                throw new RuntimeException(e);
            }
        }

        // Check if FK attributes exist in other tables
        for(final ForeignKey foreignKey : this.foreignKeys) {
            try {
                if(!this.foreignKeyExistsInTable(foreignKey, databaseNode)) {
                    log.error("CreateTableAction -> FK doesn't exits in table=" + foreignKey.referencedTable() + "," +
                            " referenced values=" + foreignKey.referencedAttributes());
                    throw new ForeignKeyNotFound(foreignKey.referencedTable(), foreignKey.referencedAttributes());
                }
            } catch (DatabaseDoesntExist exception) {
                log.error("CreateTableAction -> FK is referencing a table=" + foreignKey.referencedTable() + "in a non-existing database!");
                throw new ForeignKeyNotFound(foreignKey.referencedTable(), foreignKey.referencedAttributes());
            }
        }

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
