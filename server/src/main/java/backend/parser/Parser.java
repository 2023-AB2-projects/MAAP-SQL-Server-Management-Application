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
import org.apache.commons.collections4.iterators.PeekingIterator;

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

    /**
     * @param token
     * @return checks if token is valid name for database/table/column
     * @throws SQLParseException
     */
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
        PeekingIterator<String> it = new PeekingIterator<>(tokens.iterator());

        try {
            String firstWord = it.next();

            if (firstWord.equals(("use"))) {
                return parseUseDatabase(tokens, it);
            }
            else {
                String secondWord = it.next();

                if (firstWord.equals("create")) {
                    if (secondWord.equals("database")) {
                        return parseCreateDatabase(tokens, it);
                    }
                    if (secondWord.equals("table")) {
                        return parseCreateTable(tokens, databaseName, it);
                    }
                }
                if (firstWord.equals("drop")) {
                    if (secondWord.equals("database")) {
                        return parseDropDatabase(tokens, it);
                    }
                    if (secondWord.equals("table")) {
                        return parseDropTable(tokens, databaseName, it);
                    }
                }
                if (firstWord.equals("insert") && secondWord.equals("into")) {
                    return parseInsertInto(tokens, databaseName, it);
                }
                if (firstWord.equals("delete") && secondWord.equals("from")) {
                    return parseDeleteFrom(tokens, databaseName, it);
                }
            }
        }
        catch (NoSuchElementException e) {
            throw(new SQLParseException("Missing token after `" + tokens.get(tokens.size() - 1) + "`"));
        }

        throw (new SQLParseException("Invalid SQL command"));
    }

    /**
     * @param tokens
     * @return
     * @throws SQLParseException
     */
    private UseDatabaseAction parseUseDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException{
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }
            
        String databaseName = it.next();
        
        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

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
    private CreateDatabaseAction parseCreateDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }

        String databaseName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

        checkName(databaseName, NAME_TYPE.DATABASE);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        CreateDatabaseAction cda = new CreateDatabaseAction(databaseModel);

        return cda;
    }

    private enum CreateTableStates {
        GET_TABLE_NAME, GET_FIELD_NAME_TYPE, GET_FIELD_CONSTRAINTS, COMMA, CLOSING_BRACKET
    }
    private CreateTableAction parseCreateTable(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        // Used for constructing multiple AttributeModels
        String fieldName = "";
        String fieldType = "";
        boolean nullable = true;

        // used for constructing TableModel
        String                      tableName           = null;
        String                      fileName            = "";
        ArrayList<FieldModel>       attributes          = new ArrayList<FieldModel>();
        PrimaryKeyModel             primaryKey          = null;
        ArrayList<ForeignKeyModel>  foreignKeys         = new ArrayList<ForeignKeyModel>();
        ArrayList<String>           uniqueAttributes    = new ArrayList<String>();
        ArrayList<IndexFileModel>   indexFiles          = new ArrayList<IndexFileModel>();

        // used for costructing PrimaryKeyModel
        ArrayList<String>           primaryKeyAttributes = new ArrayList<String>();

        boolean grace = true;
        CreateTableStates state = CreateTableStates.GET_TABLE_NAME;
        try {
            while (it.hasNext() || grace) {
                if (!it.hasNext()) {
                    grace = false;
                }
                switch (state) {
                case GET_TABLE_NAME:
                    // check if table name is valid
                    tableName = it.next();
                    checkName(tableName, NAME_TYPE.TABLE);

                    // if no more tokens after table name we can exit (table is specified without any fields, constraints)
                    if (!it.hasNext()) {
                        break;
                    }
                    // if there are more tokens but next one isn't a '(', invalid instruction
                    else if (!it.next().equals("(")) {
                        throw(new SQLParseException("Expected '(' after table name"));
                    }
                    // else skip the '(' and start reading fields
                    else {
                        state = CreateTableStates.GET_FIELD_NAME_TYPE;
                    }
                    break;

                case GET_FIELD_NAME_TYPE:
                    // read field name and type - mandatory
                    fieldName = it.next();
                    fieldType = it.next();

                    checkName(fieldName, NAME_TYPE.COLUMN);

                    nullable = true;

                    if (!isValidFieldType(fieldType)) {
                        throw(new SQLParseException("Invalid field type: \"" + fieldType + "\""));
                    }
                    // char field type needs to have following structure: (  num  )
                    if (fieldType.equals("char")) {
                        if (!it.next().equals("(")) {
                            throw(new SQLParseException("Expected length of char attribute in form -> (len)"));
                        }
                        
                        String len = it.next();
                        int fieldLength;
                        try {
                            fieldLength = Integer.parseInt(len);
                        } catch (NumberFormatException e) {
                            throw(new SQLParseException("Invalid length of char: " + len));
                        }

                        if (!it.next().equals(")")) {
                            throw(new SQLParseException("Expected length of char attribute in parentheses -> (len)"));
                        }

                        fieldType = fieldType + "(" + fieldLength + ")";
                    }

                    state = CreateTableStates.GET_FIELD_CONSTRAINTS;
                    break;
                    
                case GET_FIELD_CONSTRAINTS:
                    String token = it.next();

                    if (token.equals("unique")) {
                        uniqueAttributes.add(fieldName);
                        nullable = false;
                        break;
                    }
                    else if (token.equals("primary")) {
                        if (it.next().equals("key")) {
                            primaryKeyAttributes.add(fieldName);
                            nullable = false;
                        }
                        else {
                            throw(new SQLParseException("Unknown constraint foreign " + token));
                        }
                        break;
                    }
                    else if (token.equals("foreign")) {
                        String tokenFirst = it.next();
                        String tokenSecond = it.next();
                        if (!tokenFirst.equals("key") || !tokenSecond.equals("references")) {
                            throw(new SQLParseException("Unknown constraint, expected (foreign key references), instead of " + tokenFirst + " " + tokenSecond));
                        }

                        String foreignTable = it.next();
                        String openingBracket = it.next();
                        String foreignField = it.next();
                        String closingBracket = it.next();
                        if (!openingBracket.equals("(") || !closingBracket.equals(")")) {
                            throw(new SQLParseException("Expected following structure for foreign key constraint -> foreign key references table(field)"));
                        }

                        String finalFieldName = fieldName; //TODO
                        var fkm = new ForeignKeyModel(foreignTable, new ArrayList<String>(Collections.singletonList(foreignField)), new ArrayList<>(){{
                            add(finalFieldName);
                        }});
                        foreignKeys.add(fkm);
                        break;
                    }
                    else if (token.equals(",")) {
                        state = CreateTableStates.COMMA;
                        break;
                    }
                    else if (token.equals(")")) {
                        state = CreateTableStates.CLOSING_BRACKET;
                        break;
                    }
                    else {
                        throw new SQLParseException("Undefined constraint: " + token);
                    }                    
                    //break;

                case COMMA, CLOSING_BRACKET:
                    attributes.add(new FieldModel(fieldName, fieldType, nullable));

                    fieldName = "";
                    fieldType = "";
                    nullable = true;

                    if (state == CreateTableStates.CLOSING_BRACKET) {
                        if (it.hasNext()) {
                            throw new SQLParseException("Expected end of input after closing bracket");
                        }

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
        catch (NoSuchElementException e) {
            throw (new SQLParseException("Unexpected end of string"));
        }
        
        fileName = tableName + ".data.bin";
        primaryKey = new PrimaryKeyModel(primaryKeyAttributes);
        TableModel tableModel = new TableModel(tableName, fileName, attributes, primaryKey, foreignKeys, uniqueAttributes, indexFiles);
        CreateTableAction cta = new CreateTableAction(tableModel, databaseName);
        return cta;
    }

    private DropDatabaseAction parseDropDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }

        String databaseName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

        checkName(databaseName, NAME_TYPE.DATABASE);

        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        DropDatabaseAction dda = new DropDatabaseAction(databaseModel);

        return dda;
    }    

    private DropTableAction parseDropTable(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for table name"));
        }

        String tableName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after table name: `" + tableName + "`"));
        }

        checkName(tableName, NAME_TYPE.TABLE);

        DropTableAction dta = new DropTableAction(tableName, databaseName);

        return dta;
    }  

    private enum InsertIntoStates {
        GET_TABLE_NAME, GET_FIELD_NAMES, GET_VALUES, GET_VALUES_STRINGS, CLOSING_BRACKET
    }
    // TODO: create DatabaseAction for insert into
    private InsertAction parseInsertInto(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String tableName = "";
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<ArrayList<String>> values = new ArrayList<>();

        ArrayList<String> currentValues = null;

        boolean grace = true;
        InsertIntoStates state = InsertIntoStates.GET_TABLE_NAME;
        try {
            while (it.hasNext() || grace) {
                if (!it.hasNext()) {
                    grace = false;
                }
                switch (state) {
                    // called once for table name at start
                    case GET_TABLE_NAME: {
                        if (!it.hasNext()) {
                            throw(new SQLParseException("Missing token for table name"));
                        }
                
                        tableName = it.next();
                        checkName(tableName, NAME_TYPE.TABLE);
                        
                        String nextToken = it.peek();
                        if (!nextToken.equals("(")) {
                            state = InsertIntoStates.GET_VALUES;
                            break;
                        }

                        if (!it.hasNext() || !it.next().equals("(")) {
                            throw new SQLParseException("Expected a list of attribute names after table name. e.g.: table(field1, field2, ...)");
                        }

                        state = InsertIntoStates.GET_FIELD_NAMES;
                    }
                    // called once for every value between the parentheses in the part `table(name1, name2, ...)
                    case GET_FIELD_NAMES: {
                        String fieldName = it.next();

                        checkName(fieldName, NAME_TYPE.COLUMN);

                        fieldNames.add(fieldName);

                        String nextToken = it.next();
                        // if closing bracket, start reading insert values 
                        if (nextToken.equals(")")) {
                            state = InsertIntoStates.GET_VALUES;
                            break;
                        }
                        // if comma, continue reading another field name
                        else if (nextToken.equals(",")) {
                            break;
                        } 
                        else {
                            throw new SQLParseException("Expected comma and another field name, or parenthesis after \"" + fieldName + "\"");
                        }
                    }
                    // for starting a part `values(...)` 
                    case GET_VALUES: {
                        // `values` keyword only needed before first values list
                        if (values.isEmpty()) {
                            // pop `values` keyword
                            if (!it.next().equals("values")) {
                                throw new SQLParseException("Expected `values(value1, value2, ...)` after list of field names");
                            }
                        }

                        if (!it.next().equals("(")) {
                            throw new SQLParseException("Expected parenthesis after keyword `values` or comma");
                        }

                        currentValues = new ArrayList<>();
                        state = InsertIntoStates.GET_VALUES_STRINGS;

                        break;
                    }
                    // called once for every value between the parentheses in the part `values(value1, value2, ...)`
                    case GET_VALUES_STRINGS: {
                        String value = it.next();

                        currentValues.add(value);

                        String nextToken = it.next();
                        if (nextToken.equals(")")) {
                            state = InsertIntoStates.CLOSING_BRACKET;
                            break;
                        }
                        else if (nextToken.equals(",")) {
                            break;
                        }
                        else {
                            throw new SQLParseException("Expected comma and another value, or parenthesis after \"" + value + "\"");
                        }
                    }
                    case CLOSING_BRACKET: {
                        if (currentValues != null) {
                            values.add(currentValues);
                            currentValues = null;
                        }
                        
                        if (it.hasNext()) {
                            log.info(it.peek());

                            if (values.size() == 0) {
                                state = InsertIntoStates.GET_VALUES;
                                break;
                            }
                            else if (!it.next().equals(",")) {
                                throw new SQLParseException("Expected comma after closing bracket");
                            }
                            
                            state = InsertIntoStates.GET_VALUES;
                            break;
                        }
                        else {
                            grace = false;
                        }
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (NoSuchElementException | NullPointerException e) {
            throw new SQLParseException("Unexpected end of command");
        }
        
        log.info(tableName);
        log.info(fieldNames.toString());
        log.info(values.toString());

        return new InsertAction(databaseName, tableName, values);
    }

    private DeleteAction parseDeleteFrom(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String tableName = "";
        ArrayList<String> keys = new ArrayList<>();

        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for table name"));
        }

        tableName = it.next();

        while (it.hasNext()) {
            keys.add(it.next());
        }

        log.info(tableName);
        log.info(keys.toString());

        DeleteAction da = new DeleteAction(databaseName, tableName, keys);
        return da;
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