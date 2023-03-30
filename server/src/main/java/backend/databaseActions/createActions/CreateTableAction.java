package backend.databaseActions.createActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseModels.*;
import backend.exceptions.databaseActionsExceptions.*;
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
import java.util.stream.Collectors;

@Data
@Slf4j
public class CreateTableAction implements DatabaseAction {
    private final TableModel table;

    // For simplicity
    private final String databaseName;

    public CreateTableAction(TableModel table, String databaseName) {
        this.table = table;
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

    private boolean givenFieldsAreUnique() {
        return this.table.getFields().stream().map(FieldModel::getFieldName).distinct().count() == this.table.getFields().size();
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

    private boolean fieldExistsInThisTable(String fieldName) {
        // Iterate through all attributes and check if we have one with the given name
        for(final FieldModel attribute : this.table.getFields()) {
            if(attribute.getFieldName().equals(fieldName)) return true;
        }
        return false;
    }

    private boolean fieldIsNullableInThisTable(String givenFieldName) throws FieldNotFound {
        // Iterate though all given attributes
        for(final FieldModel attribute : this.table.getFields()) {
            if(attribute.getFieldName().equals(givenFieldName)) {
                // Check if it's null or not
                return attribute.isNullable();
            }
        }

        // Throw exception -> Attribute doesn't exist in current table
        throw new FieldNotFound(givenFieldName, this.table.getTableName());
    }

    private boolean foreignKeyExistsInTable(ForeignKeyModel foreignKey, JsonNode databaseNode) throws DatabaseDoesntExist {
        // First check if referenced table exists
        String referencedDatabaseName = foreignKey.getReferencedTable();
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
        ArrayNode primaryKeyFields = (ArrayNode) databaseTableNode.get("table").get("primaryKey").get("primaryKeyFields");
        for(final String foreignKeyAttribute : foreignKey.getReferencedFields()) {
            boolean currentAttributeIsPresent = false;
            for(final JsonNode primaryKeyAttributeNode : primaryKeyFields) {
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
    public Object actionPerform() throws TableNameAlreadyExists, DatabaseDoesntExist,
            PrimaryKeyNotFound, ForeignKeyNotFound, FieldCantBeNull, FieldsAreNotUnique, ForeignKeyFieldNotFound {
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
        if(!this.givenFieldsAreUnique()) {
            log.error("CreateTableAction -> Fields are not unique!");
            throw new FieldsAreNotUnique(this.table.getFields());
        }

        // Check if table already exists in database
        if (this.tableAlreadyExists(this.table.getTableName(), databaseNode)) {
            log.error("CreateTableAction -> Table already exists in database=" + this.databaseName + " tableName=" + this.table.getTableName());
            throw new TableNameAlreadyExists(this.table.getTableName());
        }

        // PK check
        for (final String pkField : this.table.getPrimaryKey().getPrimaryKeyFields()) {
            // Check if PK attributes exist in table
            if(!this.fieldExistsInThisTable(pkField)) {
                log.error("CreateTableAction -> PK doesn't exist in the table=" + this.table.getTableName() + ", pK=" + pkField);
                throw new PrimaryKeyNotFound(this.table.getTableName(), pkField);
            }

            // Check if PK attributes are nullable (if they are, they can't be primary key attributes)
            try {
                if(this.fieldIsNullableInThisTable(pkField)) {
                    log.error("CreateTableAction -> PK attribute can't be nullable!");
                    throw new FieldCantBeNull(pkField);
                }
            } catch (FieldNotFound e) {
                log.error("CreateTableAction -> PK attribute (this is a problem since we checked above) doesn't exist" +
                        " in table=" + this.table.getTableName() + ", pkAttribute=" + pkField);
                throw new RuntimeException(e);
            }
        }

        // Check if FK attributes exist in other tables
        for(final ForeignKeyModel foreignKey : this.table.getForeignKeys()) {
            try {
                if(!this.foreignKeyExistsInTable(foreignKey, databaseNode)) {
                    log.error("CreateTableAction -> FK doesn't exits in table=" + foreignKey.getReferencedTable() + "," +
                            " referenced values=" + foreignKey.getReferencedTable());
                    throw new ForeignKeyNotFound(foreignKey.getReferencedTable(), foreignKey.getReferencedFields());
                }
            } catch (DatabaseDoesntExist exception) {
                log.error("CreateTableAction -> FK is referencing a table=" + foreignKey.getReferencedTable() + "in a non-existing database!");
                throw new ForeignKeyNotFound(foreignKey.getReferencedTable(), foreignKey.getReferencedFields());
            }
        }

        // Check if FK fields are present in table
        ArrayList<String> fields = new ArrayList<>(this.table.getFields().stream().map(FieldModel::getFieldName).toList());

        // For each foreign key -> Check if it's referencing fields are in this table
        for(final ForeignKeyModel foreignKey : this.table.getForeignKeys()) {
            for(final String foreignKeyField : foreignKey.getReferencingFields()) {
                if(!fields.contains(foreignKeyField)) {
                    log.error("CreateTableAction -> FK is referencing non existent attribute in this table!");
                    throw new ForeignKeyFieldNotFound(foreignKeyField, this.table.getTableName());
                }
            }
        }

        // Create new table in database
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");
        JsonNode newTable = JsonNodeFactory.instance.objectNode().putPOJO("table", this.table);
        databaseTables.add(newTable);

        // Mapper -> Write entire catalog
        try {
            mapper.writeValue(catalog, rootNode);
        } catch (IOException e) {
            log.error("CreateTableAction -> Write value (mapper) failed");
            throw new RuntimeException(e);
        }
        return null;
    }
}
