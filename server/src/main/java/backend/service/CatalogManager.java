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
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

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

    /* Utility */
    private static void updateRoot () {
        try {
            root = mapper.readTree(catalog);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonNode getRoot() {
        updateRoot();
        return root;
    }

    private static JsonNode findTableNode(JsonNode databaseNode, String tableName) {
        // find the databases table nodes
        ArrayNode databaseTables = (ArrayNode) databaseNode.get("database").get("tables");

        // check if given tablename was found
        for (final JsonNode tableNode : databaseTables) {
            if (tableNode.get("table").get("tableName").asText().equals(tableName)) {
                return tableNode.get("table");
            }
        }
        log.error("findTableNode() ->  No table were found with given name " + tableName);
        return null;
    }

    private static JsonNode findTableNode(String databaseName, String tableName) {
        // find the database json node
        JsonNode databaseNode = findDatabaseNode(databaseName);
        assert databaseNode != null;

        return findTableNode(databaseNode, tableName);
    }

    private static JsonNode findDatabaseNode(String databaseName) {
        // Check if database exists
        ArrayNode databasesArray = (ArrayNode) getRoot().get(Config.getDbCatalogRoot());
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();
            if (currentDatabaseName.equals(databaseName)) {
                return databaseNode;
            }
        }

        return null;
    }
    /* / Utility */

    /* Getters */
    public static String getTableDataPath(String databaseName, String tableName) {
        return Config.getDbRecordsPath() + File.separator + databaseName + File.separator + tableName + File.separator + tableName + ".data.bin";
    }

    public static String getTableIndexFilePath(String databaseName, String tableName, String indexName) {
        return Config.getDbCatalogPath() + File.separator + databaseName + File.separator + tableName + ".index." + indexName + ".bin";
    }

    public static List<String> getColumnNames(String databaseName, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        // find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        assert tableNode != null;

        ArrayNode fields = (ArrayNode) tableNode.get("fields");
        System.out.println(fields.asText());
        for (final JsonNode field : fields) {
            columnNames.add(field.get("fieldName").asText());
        }

        return columnNames;
    }

    public static List<String> getPrimaryKeys(String databaseName, String tableName) {
        List<String> pks = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode primaryKeyArrayNode = (ArrayNode) tableNode.get("primaryKey").get("primaryKeyFields");
        //System.out.println(primaryKeyArrayNode.toPrettyString());
        for (final JsonNode field : primaryKeyArrayNode) {
            pks.add(field.asText());
        }
        return pks;
    }

    public static List<String> getColumnTypes(String databaseName, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        // find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        assert tableNode != null;

        ArrayNode fields = (ArrayNode) tableNode.get("fields");
        for (final JsonNode field : fields) {
            columnNames.add(field.get("type").asText());
        }

        return columnNames;
    }

    public static List<String> getPrimaryKeyTypes (String databaseName, String tableName){
        ArrayList<String> col_type = (ArrayList<String>) getColumnTypes(databaseName, tableName);
        ArrayList<String> col_name = (ArrayList<String>) getColumnNames(databaseName, tableName);
        List<String> key_name = (ArrayList<String>) getPrimaryKeys(databaseName, tableName);

        ArrayList<String> key_type = new ArrayList<>();
        for (String key : key_name) {
            int index = col_name.indexOf(key);
            if (index != -1) {
                key_type.add(col_type.get(index));
            } else {
                log.warn("getPrimaryKeyTypes() " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return key_type;
    }

    public static List<Integer> getPrimaryKeyIndexes (String databaseName, String tableName){
        ArrayList<String> col_type = (ArrayList<String>) getColumnTypes(databaseName, tableName);
        ArrayList<String> col_name = (ArrayList<String>) getColumnNames(databaseName, tableName);
        List<String> key_name = (ArrayList<String>) getPrimaryKeys(databaseName, tableName);

        ArrayList<Integer> key_index = new ArrayList<>();
        for (String key : key_name) {
            int index = col_name.indexOf(key);
            if (index != -1) {
                key_index.add(index);
            } else {
                log.warn("getPrimaryKeyTypes() " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return key_index;
    }

    public static List<String> getCurrentDatabaseTableNames() {
        List<String> tableNames = new ArrayList<>();

        // get the current database node
        JsonNode currentDatabaseNode = findDatabaseNode(ServerController.getCurrentDatabaseName());
        if(currentDatabaseNode == null) {
            log.error("Database JSON node not found!");
            throw new RuntimeException();
        }

        // find the databases table nodes
        ArrayNode databaseTables = (ArrayNode) currentDatabaseNode.get("database").get("tables");

        // save each tableName
        for (final JsonNode tableNode : databaseTables) {
            String tableName = tableNode.get("table").get("tableName").asText();
            tableNames.add(tableName);
        }
        return tableNames;
    }

    public static List<String> getDatabaseNames() {
        List<String> databaseNames = new ArrayList<>();

        // find the databases json array node
        ArrayNode databasesArray = (ArrayNode) getRoot().get(Config.getDbCatalogRoot());
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            String databaseName = databaseNode.get("database").get("databaseName").asText();
            databaseNames.add(databaseName);
        }
        return databaseNames;
    }
}