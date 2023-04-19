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

    public static String getTableDataPath(String databaseName, String tableName) {
        return Config.getDbRecordsPath() + File.separator + databaseName + File.separator + tableName + File.separator + tableName + ".data.bin";
    }

    private static JsonNode getRoot(){
        updateRoot();
        return root;
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
                log.error("CreateTableAction -> Database null -> \"databaseName\" not found");
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

    public static List<String> getPrimaryKeys(String databaseName, String tableName) {
        List<String> pks = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode primaryKeyArrayNode = (ArrayNode) tableNode.get("primaryKey").get("primaryKeyFields");
        // System.out.println(primaryKeyArrayNode.toPrettyString());
        for (JsonNode field : primaryKeyArrayNode) {
            pks.add(field.asText());

    private static void updateRoot(){
        try {
            root = mapper.readTree(catalog);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getPrimaryKeyTypes(String databaseName, String tableName) {
        ArrayList<String> col_type = (ArrayList<String>) getColumnTypes(databaseName, tableName);
        ArrayList<String> col_name = (ArrayList<String>) getColumnNames(databaseName, tableName);
        List<String> key_name = (ArrayList<String>) getPrimaryKeys(databaseName, tableName);

        ArrayList<String> key_type = new ArrayList<>();
        for(String key : key_name){
            int index = col_name.indexOf(key);
            if(index != -1) {
                key_type.add(col_type.get(index));
            } else {
                log.warn("getPrimaryKeyTypes() " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return key_type;
    }

    public static List<Integer> getPrimaryKeyIndexes(String databaseName, String tableName) {
        ArrayList<String> col_type = (ArrayList<String>) getColumnTypes(databaseName, tableName);
        ArrayList<String> col_name = (ArrayList<String>) getColumnNames(databaseName, tableName);
        List<String> key_name = (ArrayList<String>) getPrimaryKeys(databaseName, tableName);

        ArrayList<Integer> key_index = new ArrayList<>();
        for(String key : key_name){
            int index = col_name.indexOf(key);
            if(index != -1) {
                key_index.add(index);
            } else {
                log.warn("getPrimaryKeyTypes() " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return key_index;
    }


}
