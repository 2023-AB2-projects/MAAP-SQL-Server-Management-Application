package backend.service;

import backend.config.Config;
import backend.databaseModels.ForeignKeyModel;
import backend.exceptions.recordHandlingExceptions.DeletedRecordLinesEmpty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CatalogManager {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final File catalog = Config.getCatalogFile();
    private static JsonNode root;

    static {
        try {
            root = mapper.readTree(catalog);
        } catch (IOException e) {
            log.error("CatalogManager -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(e);
        }
    }

    /* ------------------------------------------------ Utility ------------------------------------------------------*/
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

    private static void updateCatalog() {
        // Mapper -> Write entire catalog
        try {
            mapper.writeValue(Config.getCatalogFile(), root);
        } catch (IOException e) {
            log.error("CreateIndexAction -> Write value (mapper) failed");
            throw new RuntimeException(e);
        }
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
        throw new RuntimeException();
    }

    private static JsonNode findTableNode(String databaseName, String tableName) {
        // find the database json node
        JsonNode databaseNode = findDatabaseNode(databaseName);
        assert databaseNode != null;

        return findTableNode(databaseNode, tableName);
    }

    private static JsonNode findTableFieldNode(String databaseName, String tableName, String fieldName) {
        // Find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + " not found!");
            throw new RuntimeException();
        }

        // Iterate over fields
        for (final JsonNode fieldNode : tableNode.get("fields")) {
            String currentFieldName = fieldNode.get("fieldName").asText();
            if(currentFieldName.equals(fieldName)) {
                return fieldNode;
            }
        }

        return null;
    }

    private static ArrayNode findTableFieldsArrayNode(String databaseName, String tableName) {
        // Find table JSON node
        JsonNode tableNode = CatalogManager.findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("In database=" + databaseName + ", table=" + tableName + " JSON node not found!");
            throw new RuntimeException();
        }

        return (ArrayNode) tableNode.get("fields");
    }

    private static ArrayNode findTableIndexFilesArrayNode(String databaseName, String tableName) {
        // Find table JSON node
        JsonNode tableNode = CatalogManager.findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("In database=" + databaseName + ", table=" + tableName + " JSON node not found!");
            throw new RuntimeException();
        }

        return (ArrayNode) tableNode.get("indexFiles");
    }

    private static JsonNode findTableIndexNode(String databaseName, String tableName, String indexName) {
        // Find node
        for(final JsonNode indexNode : CatalogManager.findTableIndexFilesArrayNode(databaseName, tableName)) {
            // Check index name
            String currentIndexName = indexNode.get("indexFile").get("indexName").asText();
            if(currentIndexName.equals(indexName)) {
                return indexNode;
            }
        }
        return null;
    }

    private static ArrayDeque<Integer> deletedRecordLinesQueue(String databaseName, String tableName) {
        // Find table JSON node
        JsonNode tableNode = CatalogManager.findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("In database=" + databaseName + ", table=" + tableName + " JSON node not found!");
            throw new RuntimeException();
        }

        // Build up the deque
        ArrayDeque<Integer> recordLines = new ArrayDeque<>();
        for(JsonNode recordLineNode : tableNode.get("deletedRecordLines")) {
            recordLines.add(recordLineNode.asInt());
        }
        return recordLines;
    }

    private static void updateDeletedRecordLinesQueue(String databaseName, String tableName, ArrayDeque<Integer> queue) {
        // Find table JSON node
        JsonNode tableNode = CatalogManager.findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("In database=" + databaseName + ", table=" + tableName + " JSON node not found!");
            throw new RuntimeException();
        }

        // Update node and update catalog
        ArrayNode newDeletedRecordLinesArray = mapper.valueToTree(queue);
        ((ObjectNode) tableNode).set("deletedRecordLines", newDeletedRecordLinesArray);
        CatalogManager.updateCatalog();     // Write root to catalog
    }
    /* ----------------------------------------------- / Utility -----------------------------------------------------*/

    /* ----------------------------------------- Deleted Record Lines ----------------------------------------------- */
    public static Integer deletedRecordLinesPop(String databaseName, String tableName) throws DeletedRecordLinesEmpty {
        ArrayDeque<Integer> deletedRecordLines = CatalogManager.deletedRecordLinesQueue(databaseName, tableName);
        if(deletedRecordLines.isEmpty()) {
            throw new DeletedRecordLinesEmpty();
        }

        // Remove first element
        Integer first = deletedRecordLines.removeFirst();

        // Update catalog with new deque
        CatalogManager.updateDeletedRecordLinesQueue(databaseName, tableName, deletedRecordLines);
        return first;
    }

    public static List<Integer> deletedRecordLinesPopN(String databaseName, String tableName, int n) {
        // Returned value
        List<Integer> recordLines = new ArrayList<>();

        // Get current list
        ArrayDeque<Integer> deletedRecordLines = CatalogManager.deletedRecordLinesQueue(databaseName, tableName);

        // Try to add 'n' items
        for(int i = 0; i < n; ++i) {
            if(deletedRecordLines.isEmpty()) break;

            // If not empty pop first element and add it to list
            recordLines.add(deletedRecordLines.removeFirst());
        }

        // Update catalog with new deque
        CatalogManager.updateDeletedRecordLinesQueue(databaseName, tableName, deletedRecordLines);
        return recordLines;
    }

    public static void deletedRecordLinesEnqueue(String databaseName, String tableName, Integer recordLine) {
        ArrayDeque<Integer> deletedRecordLines = CatalogManager.deletedRecordLinesQueue(databaseName, tableName);
        deletedRecordLines.add(recordLine);

        // Update catalog with new deque
        CatalogManager.updateDeletedRecordLinesQueue(databaseName, tableName, deletedRecordLines);
    }

    public static void deletedRecordLinesEnqueueN(String databaseName, String tableName, List<Integer> recordLines) {
        ArrayDeque<Integer> deletedRecordLines = CatalogManager.deletedRecordLinesQueue(databaseName, tableName);
        deletedRecordLines.addAll(recordLines);

        // Update catalog with new deque
        CatalogManager.updateDeletedRecordLinesQueue(databaseName, tableName, deletedRecordLines);
    }
    /* ---------------------------------------- / Deleted Record Lines ---------------------------------------------- */


    /* ------------------------------------------------ Getters ----------------------------------------------------- */
    public static String getFlattenedName(List<String> values) {
        StringBuilder flattened = new StringBuilder();
        for (int i = 0; i < values.size(); ++i) {
            if (i == 0) {
                flattened.append(values.get(i));
            } else {
                flattened.append("_").append(values.get(i));
            }
        }

        return flattened.toString();
    }

    /* --------------------- Paths ------------------- */
    public static String getTableDataPath(String databaseName, String tableName) {
        return Config.getDbRecordsPath() + File.separator + databaseName + File.separator + tableName + File.separator + tableName + ".data.bin";
    }

    public static String getTableIndexFilePath(String databaseName, String tableName, String indexName) {
        return Config.getDbRecordsPath() + File.separator + databaseName + File.separator + tableName + File.separator + getIndexFileName(tableName, indexName);
    }
    /* -------------------- / Paths ------------------ */

    /* -------------------- Fields ------------------- */
    public static List<String> getFieldNames(String databaseName, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        // find the table json node
        JsonNode tableNode = findTableNode(databaseName, tableName);
        assert tableNode != null;

        ArrayNode fields = (ArrayNode) tableNode.get("fields");
        for (final JsonNode field : fields) {
            columnNames.add(field.get("fieldName").asText());
        }

        return columnNames;
    }

    public static List<String> getPrimaryKeyFieldNames(String databaseName, String tableName) {
        List<String> pks = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode primaryKeyArrayNode = (ArrayNode) tableNode.get("primaryKey").get("primaryKeyFields");
        for (final JsonNode field : primaryKeyArrayNode) {
            pks.add(field.asText());
        }
        return pks;
    }

    public static List<String> getPrimaryKeyTypes (String databaseName, String tableName){
        List<String> col_type = getFieldTypes(databaseName, tableName);
        List<String> col_name = getFieldNames(databaseName, tableName);
        List<String> key_name = getPrimaryKeyFieldNames(databaseName, tableName);

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

    public static List<Integer> getPrimaryKeyFieldIndexes(String databaseName, String tableName){
        List<String> fieldNames = getFieldNames(databaseName, tableName);
        List<String> primaryKeyFieldNames = getPrimaryKeyFieldNames(databaseName, tableName);

        ArrayList<Integer> primaryKeyIndexes = new ArrayList<>();
        for (final String key : primaryKeyFieldNames) {
            int index = fieldNames.indexOf(key);
            if (index != -1) {
                primaryKeyIndexes.add(index);
            } else {
                log.warn("In " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return primaryKeyIndexes;
    }

    public static List<String> getUniqueFieldNames(String databaseName, String tableName) {
        List<String> fields = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode uniqueFieldsArrayNode = (ArrayNode) tableNode.get("uniqueFields");
        for (final JsonNode field : uniqueFieldsArrayNode) {
            fields.add(field.asText());
        }
        return fields;
    }

    public static List<String> getUniqueFieldTypes(String databaseName, String tableName) {
        List<String> fields = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode uniqueFieldsArrayNode = (ArrayNode) tableNode.get("uniqueFields");
        for (final JsonNode field : uniqueFieldsArrayNode) {
            fields.add(field.asText());
        }

        // Find their types
        List<String> types = new ArrayList<>();
        ArrayNode fieldsArrayNode = (ArrayNode) tableNode.get("fields");
        for (final String fieldName : fields) {
            for (final JsonNode field : fieldsArrayNode) {
                if (field.get("fieldName").asText().equals(fieldName)) {
                    types.add(field.get("type").asText());
                    break;
                }
            }
        }

        return types;
    }

    public static List<Integer> getUniqueFieldIndexes(String databaseName, String tableName){
        List<String> fieldNames = getFieldNames(databaseName, tableName);
        List<String> uniqueFieldNames = getUniqueFieldNames(databaseName, tableName);

        ArrayList<Integer> uniqueFieldIndexes = new ArrayList<>();
        for (final String key : uniqueFieldNames) {
            int index = fieldNames.indexOf(key);
            if (index != -1) {
                uniqueFieldIndexes.add(index);
            } else {
                log.warn("In " + databaseName + " : " + tableName + " : keyValue:" + key + " not found in table!");
            }
        }
        return uniqueFieldIndexes;
    }

    public static List<ForeignKeyModel> getForeignKeys(String databaseName, String tableName) {
        List<ForeignKeyModel> foreignKeys = new ArrayList<>();

        // find the tableNode
        JsonNode tableNode = findTableNode(databaseName, tableName);

        ArrayNode foreignKeyNode = (ArrayNode) tableNode.get("foreignKeys");
        try {
            foreignKeys = Utility.getObjectMapper().readValue(foreignKeyNode.toString(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not read foreignKeys JSON to objects!");
            throw new RuntimeException(e);
        }
        return foreignKeys;
    }

    public static List<ForeignKeyModel> getForeignKeysReferencingThisTable(String databaseName, String tableName) {
        List<ForeignKeyModel> referencingForeignKeys = new ArrayList<>();

        // find the tableNode
        JsonNode databaseNode = findDatabaseNode(databaseName);
        if (databaseNode == null) {
            log.error("Database node not found!");
            throw new RuntimeException();
        }

        for (final JsonNode tableNode : databaseNode.get("database").get("tables")) {
            // Foreign key node
            ArrayNode foreignKeyNode = (ArrayNode) tableNode.get("table").get("foreignKeys");
            try {
                ArrayList<ForeignKeyModel> foreignKeys = Utility.getObjectMapper().readValue(foreignKeyNode.toString(), new TypeReference<>() {});

                for (final ForeignKeyModel fKey : foreignKeys) {
                    if (fKey.getReferencedTable().equals(tableName)) {
                        referencingForeignKeys.add(fKey);
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("Could not read foreignKeys JSON to objects!");
                throw new RuntimeException(e);
            }
        }

        return referencingForeignKeys;
    }

    public static List<String> getForeignKeysTableNamesReferencingThisTable(String databaseName, String tableName) {
        List<String> tableNames = new ArrayList<>();

        // find the tableNode
        JsonNode databaseNode = findDatabaseNode(databaseName);
        if (databaseNode == null) {
            log.error("Database node not found!");
            throw new RuntimeException();
        }

        for (final JsonNode tableNode : databaseNode.get("database").get("tables")) {
            // Foreign key node
            ArrayNode foreignKeyNode = (ArrayNode) tableNode.get("table").get("foreignKeys");
            try {
                ArrayList<ForeignKeyModel> foreignKeys = Utility.getObjectMapper().readValue(foreignKeyNode.toString(), new TypeReference<>() {});

                for (final ForeignKeyModel fKey : foreignKeys) {
                    if (fKey.getReferencedTable().equals(tableName)) {
                        tableNames.add(tableNode.get("table").get("tableName").asText());
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("Could not read foreignKeys JSON to objects!");
                throw new RuntimeException(e);
            }
        }

        return tableNames;
    }

    public static boolean isFieldUnique(String databaseName, String tableName, String fieldName) {
        // Find the table json node
        JsonNode fieldNode = CatalogManager.findTableFieldNode(databaseName, tableName, fieldName);
        if(fieldNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", field=" + fieldName + " not found!");
            throw new RuntimeException();
        }

        // Is unique, when it's not nullable
        return !fieldNode.get("nullable").asBoolean();
    }
    /* ------------------- / Fields ------------------ */

    /* ----------------- Field types ----------------- */
    public static List<String> getFieldTypes(String databaseName, String tableName) {
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

    public static String getFieldType(String databaseName, String tableName, String fieldName) {
        // Find the table json node
        JsonNode fieldNode = CatalogManager.findTableFieldNode(databaseName, tableName, fieldName);
        if(fieldNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", field=" + fieldName + " not found!");
            throw new RuntimeException();
        }

        return fieldNode.get("type").asText();
    }
    /* ---------------- / Field types ---------------- */

    /* ------------------- Indexes ------------------- */
    public static List<String> getIndexFileNames(String databaseName, String tableName) {
        ArrayList<String> fileNames = new ArrayList<>();

        // Find table JSON node
        JsonNode tableNode = CatalogManager.findTableNode(databaseName, tableName);
        if(tableNode == null) {
            log.error("In database=" + databaseName + ", table=" + tableName + " JSON node not found!");
            throw new RuntimeException();
        }

        // Iterate over index files
        for(final JsonNode indexFileNode : tableNode.get("indexFiles")) {
            // Add index file name to list
            String indexFileName = indexFileNode.get("indexFile").get("indexFileName").asText();
            fileNames.add(indexFileName);
        }

        return fileNames;
    }

    public static List<String> getIndexFieldNames(String databaseName, String tableName, String indexName) {
        ArrayList<String> fieldNames = new ArrayList<>();

        // In index node
        JsonNode indexNode = CatalogManager.findTableIndexNode(databaseName, tableName, indexName);
        if(indexNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", indexName=" + indexName + " not found!");
            throw new RuntimeException();
        }

        // Iterate over all index file fields
        for(final JsonNode indexFileName : indexNode.get("indexFile").get("indexFields")) {
            fieldNames.add(indexFileName.asText());
        }

        return fieldNames;
    }

    public static List<String> getIndexFieldTypes(String databaseName, String tableName, String indexName) {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<String> fieldTypes = new ArrayList<>();

        // JSON array node containing all fields
        ArrayNode fieldsArray = CatalogManager.findTableFieldsArrayNode(databaseName, tableName);

        // In index node
        JsonNode indexNode = CatalogManager.findTableIndexNode(databaseName, tableName, indexName);
        if(indexNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", indexName=" + indexName + " not found!");
            throw new RuntimeException();
        }

        // Iterate over all index file fields
        for(final JsonNode indexFieldNode : indexNode.get("indexFile").get("indexFields")) {
            String indexFieldName = indexFieldNode.asText();
            fieldNames.add(indexFieldName);

            // Find in the fields of table
            for(final JsonNode fieldNode : fieldsArray) {
                if(fieldNode.get("fieldName").asText().equals(indexFieldName)) {
                    fieldTypes.add(fieldNode.get("type").asText());
                    break;      // Move on to next field
                }
            }
        }
        // Check size
        if (fieldNames.size() != fieldTypes.size()) {
            log.error("Could not find all types for field names in index!");
            throw new RuntimeException();
        }

        return fieldTypes;
    }

    public static String getIndexFileName(String tableName, String indexName) {
        return tableName + ".index." + indexName + ".bin";
    }

    public static String getPrimaryKeyIndexName(String databaseName, String tableName) {
        return getFlattenedName(CatalogManager.getPrimaryKeyFieldNames(databaseName, tableName));
    }

    public static List<String> getUniqueFieldIndexNames(String databaseName, String tableName) {
        return CatalogManager.getUniqueFieldNames(databaseName, tableName); // Each index file name is identical to field name
    }

    public static List<String> getForeignKeyReferencedIndexNames(String databaseName, String tableName) {
        List<String> indexNames = new ArrayList<>();

        // Get all foreign keys and associate the names with them
        List<ForeignKeyModel> foreignKeys = getForeignKeys(databaseName, tableName);
        for (final ForeignKeyModel key : foreignKeys) {
            indexNames.add(getFlattenedName(key.getReferencedFields()));        // We flatten the referenced fields
            // The flattened referenced fields are the names of the index files in the other table
        }
        return indexNames;
    }

    public static boolean isIndexFieldUnique(String databaseName, String tableName, String indexName, String indexFieldName) {
        // Find the corresponding index file node
        JsonNode indexNode = CatalogManager.findTableIndexNode(databaseName, tableName, indexName);
        if(indexNode == null) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", indexName=" + indexName + " not found!");
            throw new RuntimeException();
        }

        // Check if given index field name exists in node fields
        boolean fieldExists = false;
        for(final JsonNode fieldNode : indexNode.get("indexFile").get("indexFields")) {
            if(fieldNode.asText().equals(indexFieldName)) {
                fieldExists = true;
                break;
            }
        }

        // Check if it doesn't exist
        if(!fieldExists) {
            log.error("Database=" + databaseName + ", table=" + tableName + ", indexName=" + indexName + ", indexField=" + indexFieldName + " not found!");
            throw new RuntimeException();
        }

        // Now get field type
        return CatalogManager.isFieldUnique(databaseName, tableName, indexFieldName);
    }
    /* ------------------ / Indexes ------------------ */

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
    /* ----------------------------------------------- / Getters ---------------------------------------------------- */
}