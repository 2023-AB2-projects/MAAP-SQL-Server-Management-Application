package backend.parser;

import java.util.*;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.*;
import backend.databaseModels.*;
import backend.exceptions.InvalidSQLCommand;
import backend.exceptions.SQLParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Parser {
    public Parser() {}

    private static String[] RESERVED_KEYWORDS = {
        "create", "drop", "database", "table",
        "select", "from", "where", "group", "order", "by",
        "update", "set",
        "insert", "into", "values",
        "delete", "from",
        "(", ")", ",",
        "!=", "=", ">=", "<=", ">", "<",
        "foreign", "primary", "key", "unique", "references"
    };
    private static String[] ATTRIBUTE_TYPES = {
        "int", "float", "bit", "date", "datetime", "char"
    };

    /**
     * @param token
     * @return boolean if given token is keyword or not
     */
    private boolean isKeyword(String token) {
        return Arrays.asList(RESERVED_KEYWORDS).contains(token);
    }
    /**
     * @param token
     * @return boolean if given token is comprised of alphanumeric characters only
     */
    private boolean isAlphaNumeric(String token) {
        return token != null && token.matches("^[a-zA-Z0-9_]*$");
    }

    /**
     * @param token
     * @return boolean if given token is a valid field type
     */
    private boolean isValidFieldType(String token) {
        return Arrays.asList(ATTRIBUTE_TYPES).contains(token);
    } 

    /**
     * @param token
     * @return checks if token is valid name for database/table/column
     * @throws SQLParseException
     */
    private enum NAME_TYPE {
        DATABASE("database"), TABLE("table"), COLUMN("column");
        private final String value;
        private NAME_TYPE(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    private boolean checkName(String token, NAME_TYPE nt) throws SQLParseException {
        if (isKeyword(token)) {
            throw(new SQLParseException("Invalid name for " + nt.getValue() + ": " + token + " - Reserved keyword"));
        }
        if (!isAlphaNumeric(token)) {
            throw(new SQLParseException("Invalid name for " + nt.getValue() + ": " + token + " - Name can only contain alphanumeric characters"));
        }
        return true;
    } 

    // TODO: remove function parameter `databaseName` and replace it with field referencing ServerController
    /**
     * @param input SQL string
     * @return database action corresponding to string
     * @throws InvalidSQLCommand
     */
    public DatabaseAction parseInput(String input, String databaseName) throws SQLParseException {
        List<String> tokens = tokenize(input);

        try {
            if (tokens.get(0).equals("create")) {
                if (tokens.get(1).equals("database")) {
                    return parseCreateDatabase(tokens);
                }
                if (tokens.get(1).equals("table")) {
                    return parseCreateTable(tokens, databaseName);
                }
            }
            if (tokens.get(0).equals("drop")) {
                if (tokens.get(1).equals("database")) {
                    return parseDropDatabase(tokens);
                }
                if (tokens.get(1).equals("table")) {
                    return parseDropTable(tokens, databaseName);
                }
            }
            if (tokens.get(0).equals(("use"))) {
                return parseUseDatabase(tokens);
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw(new SQLParseException("Missing token after `" + tokens.get(tokens.size() - 1) + "`"));
        }

        throw (new SQLParseException("Invalid SQL command"));
    }

    /**
     * @param tokens
     * @return
     * @throws SQLParseException
     */
    private UseDatabaseAction parseUseDatabase(List<String> tokens) throws SQLParseException{
        if (tokens.size() < 2) {
            throw(new SQLParseException("Missing token for database name"));
        }
        if (tokens.size() > 2) {
            throw(new SQLParseException("Too many tokens after `" + tokens.get(1) + "`"));
        }

        String databaseName = tokens.get(1);
        checkName(databaseName, NAME_TYPE.DATABASE);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        UseDatabaseAction uda = new UseDatabaseAction(databaseModel);
        return uda;
    }

    /**
     * @param tokens
     * @return
     * @throws SQLParseException
     */
    private CreateDatabaseAction parseCreateDatabase(List<String> tokens) throws SQLParseException {
        if (tokens.size() < 2) {
            throw(new SQLParseException("Missing token for database name"));
        }
        if (tokens.size() > 3) {
            throw(new SQLParseException("Too many tokens after `" + tokens.get(2) + "`"));
        }

        String databaseName = tokens.get(2);
        checkName(databaseName, NAME_TYPE.DATABASE);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());

        CreateDatabaseAction cda = new CreateDatabaseAction(databaseModel);
        return cda;
    }

    private enum CreateTableStates {
        GET_TABLE_NAME, GET_FIELD_NAME_TYPE, GET_FIELD_CONSTRAINTS, COMMA, CLOSING_BRACKET
    }
    private CreateTableAction parseCreateTable(List<String> tokens, String databaseName) throws SQLParseException {
        int i = 2;

        // Used for constructing multiple AttributeModels
        String fieldName = "";
        String fieldType = "";
        int fieldLength = 0;

        // used for constructing TableModel
        String                      tableName           = null;
        String                      fileName            = "";
        int                         rowLength           = 0;
        ArrayList<AttributeModel>   attributes          = new ArrayList<AttributeModel>();
        PrimaryKeyModel             primaryKey          = null;
        ArrayList<ForeignKeyModel>  foreignKeys         = new ArrayList<ForeignKeyModel>();
        ArrayList<String>           uniqueAttributes    = new ArrayList<String>();
        ArrayList<IndexFileModel>   indexFiles          = new ArrayList<IndexFileModel>();

        // used for costructing PrimaryKeyModel
        ArrayList<String>           primaryKeyAttributes = new ArrayList<String>();

        CreateTableStates state = CreateTableStates.GET_TABLE_NAME;
        try {
            while (i < tokens.size()) {
                switch (state) {
                case GET_TABLE_NAME:
                    // check if table name is valid
                    if (checkName(tokens.get(i), NAME_TYPE.TABLE)) {
                        tableName = tokens.get(i);
                    }

                    // if no more tokens after table name we can exit (table is specified without any fields, constraints)
                    if (i+1 >= tokens.size()) {
                        i+=1;
                        break;
                    }
                    // if there are more tokens but next one isn't a '(', invalid instruction
                    else if (!tokens.get(i+1).equals("(")) {
                        throw(new SQLParseException("Expected '(' after table name"));
                    }
                    // else skip the '(' and start reading fields
                    else {
                        i += 2;
                        state = CreateTableStates.GET_FIELD_NAME_TYPE;
                    }
                    break;

                case GET_FIELD_NAME_TYPE:
                    // read field name and type - mandatory
                    fieldName = tokens.get(i);
                    fieldType = tokens.get(i+1);
                    i += 2;
                    checkName(fieldName, NAME_TYPE.COLUMN);

                    if (!isValidFieldType(fieldType)) {
                        throw(new SQLParseException("Invalid field type :" + fieldType));
                    }
                    // char field type needs to have following structure: (  num  )
                    if (fieldType.equals("char")) {
                        try {
                            fieldLength = Integer.parseInt(tokens.get(i+1));
                        } catch (NumberFormatException e) {
                            throw(new SQLParseException("Invalid length of char: " + tokens.get(i+1)));
                        }
                        if (!tokens.get(i).equals("(") || !tokens.get(i+2).equals(")")) {
                            throw(new SQLParseException("Expected length of char attribute in form -> (len)"));
                        }
                        i += 3;
                    }
                    else {
                        fieldLength = 0;
                    }

                    if (tokens.get(i).equals(")")) {
                        /*if (i+1 >= tokens.size()) {
                            throw(new SQLParseException("Too many tokens after closing ')'"));
                        }*/
                        // done
                        log.info("Finished reading all fields");
                        state = CreateTableStates.CLOSING_BRACKET;
                    }
                    else if (tokens.get(i).equals(",")) {
                        log.info("Finished reading field");
                        state = CreateTableStates.COMMA;
                    }
                    else {
                        state = CreateTableStates.GET_FIELD_CONSTRAINTS;
                    }
                    break;
                    
                case GET_FIELD_CONSTRAINTS:
                    if (tokens.get(i).equals("unique")) {
                        uniqueAttributes.add(fieldName);
                        i += 1;
                        break;
                    }
                    else if (tokens.get(i).equals("primary")) {
                        if (tokens.get(i+1).equals("key")) {
                            primaryKeyAttributes.add(fieldName);
                        }
                        else {
                            throw(new SQLParseException("Unknown constraint " + tokens.get(i) + " " + tokens.get(i+1)));
                        }
                        i += 2;
                        break;
                    }
                    else if (tokens.get(i).equals("foreign")) {
                        if (!tokens.get(i+1).equals("key") || !tokens.get(i+2).equals("references")) {
                            throw(new SQLParseException("Unknown constraint " + tokens.get(i) + " " + tokens.get(i+1)));
                        }
                        i += 3;

                        String foreignTable = tokens.get(i);
                        String foreignField = tokens.get(i+2);
                        if (!tokens.get(i+1).equals("(") || !tokens.get(i+3).equals(")")) {
                            throw(new SQLParseException("Expected following structure for foreign key constraint -> foreign key references table(field)"));
                        }

                        String finalFieldName = fieldName; //TODO
                        var fkm = new ForeignKeyModel(foreignTable, new ArrayList<String>(Collections.singletonList(foreignField)), new ArrayList<>(){{
                            add(finalFieldName);
                        }});
                        foreignKeys.add(fkm);
                        i += 4;
                        break;
                    }
                    else if (tokens.get(i).equals(",")) {
                        state = CreateTableStates.COMMA;
                        break;
                    }
                    else {
                        state = CreateTableStates.GET_FIELD_CONSTRAINTS;
                    }
                    
                    //break;

                case COMMA, CLOSING_BRACKET:
                    attributes.add(new AttributeModel(fieldName, fieldType, fieldLength, false, false));
                    i++;
                    log.info("Added field " + fieldName + ", " + fieldType);

                    fieldName = "";
                    fieldType = "";
                    fieldLength = 0;

                    if (state == CreateTableStates.CLOSING_BRACKET) {
                        break;
                    }
                    else if (state == CreateTableStates.COMMA) {
                        state = CreateTableStates.GET_FIELD_NAME_TYPE;
                    }
                    break;

                default:
                    break;
                }
            }
        } 
        catch (IndexOutOfBoundsException e) {
            throw (new SQLParseException("Unexpected end of string"));
        }
        
        primaryKey = new PrimaryKeyModel(primaryKeyAttributes);
        TableModel tableModel = new TableModel(tableName, fileName, rowLength, attributes, primaryKey, foreignKeys, uniqueAttributes, indexFiles);
        CreateTableAction cta = new CreateTableAction(tableModel, databaseName);
        return cta;
    }

    private DropDatabaseAction parseDropDatabase(List<String> tokens) throws SQLParseException {
        if (tokens.size() < 2) {
            throw(new SQLParseException("Missing token for database name"));
        }
        if (tokens.size() > 3) {
            throw(new SQLParseException("Too many tokens after `" + tokens.get(2) + "`"));
        }

        String databaseName = tokens.get(2);
        checkName(databaseName, NAME_TYPE.DATABASE);

        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());

        DropDatabaseAction dda = new DropDatabaseAction(databaseModel);
        return dda;
    }    

    private DropTableAction parseDropTable(List<String> tokens, String databaseName) throws SQLParseException {
        if (tokens.size() < 2) {
            throw(new SQLParseException("Missing token for table name"));
        }
        if (tokens.size() > 3) {
            throw(new SQLParseException("Too many tokens after `" + tokens.get(2) + "`"));
        }

        String tableName = tokens.get(2);
        checkName(tableName, NAME_TYPE.TABLE);

        DropTableAction dta = new DropTableAction(tableName, databaseName);
        return dta;
    }  

    /**
     * Converts string to lowercase
     * @param input SQL string
     * @return List of tokens
     */
    private List<String> tokenize(String input) {
        String inputLwr = input.toLowerCase(); 
        
        // tokens with some empty lines and extra whitespaces
        String[] tokensDirty = inputLwr.split("\\s*(?=[(),])|(?<=[(),])|\\s");

        List<String> tokens = new ArrayList<>(List.of(tokensDirty));

        // remove empty strings and delete whitespaces from words (maybe update regex?)
        for (int i=0; i< tokens.size(); i++) {
            if (tokens.get(i).isBlank()) {
                tokens.remove(i);
                i--;
            }
            else {
                tokens.set(i, tokens.get(i).trim());
            }
        }

        return tokens;
    }
}