package backend.service;

import backend.config.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

@Slf4j
public class CatalogManager {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static File catalog = Config.getCatalogFile();
    private static JsonNode root;

    static {
        try {
            root = mapper.readTree(catalog);
        } catch (IOException e) {
            log.error("CatalogManager -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(e);
        }
    }

    public static List<String> getColumnNames(String databaseName, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        // find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        assert tableNode != null;

        ArrayNode fields = (ArrayNode) tableNode.get("fields");
        System.out.println(fields.asText());
        for (JsonNode field : fields) {
            columnNames.add(field.get("fieldName").asText());
        }

        return columnNames;
    }

    private static JsonNode findTableNode(JsonNode databaseNode, String tableName) {
        // find the databases table nodes
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");

        // check if given tablename was found
        for(final JsonNode tableNode : databaseTables) {
            if(tableNode.get("table").get("tableName").asText().equals(tableName)) {
                return tableNode.get("table");
            }
        }
        log.error("findTableNode() ->  No table were found with given name" + tableName);
        return null;
    }

    private static JsonNode findTableNode(String databaseName, String tableName) {
        // find the database json node
        JsonNode databaseNode = findDatabaseNode(databaseName);
        assert databaseNode != null;

        return findTableNode(databaseNode, tableName);
    }

    public static List<String> getColumnTypes(String databaseName, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        // find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        assert tableNode != null;

        ArrayNode fields = (ArrayNode) tableNode.get("fields");
        for (JsonNode field : fields) {
            columnNames.add(field.get("type").asText());
        }

        return columnNames;
    }

    private static JsonNode findDatabaseNode(String databaseName) {
        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) root.get(Config.getDbCatalogRoot());
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

    public static List<String> getPrimaryKeys(String databaseName, String tableName) {
        List<String> pks = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode primaryKeyArrayNode = (ArrayNode) tableNode.get("primaryKey").get("primaryKeyFields");
        //System.out.println(primaryKeyArrayNode.toPrettyString());
        for (JsonNode field : primaryKeyArrayNode) {
                pks.add(field.asText());
        }
        return pks;
    }
}
