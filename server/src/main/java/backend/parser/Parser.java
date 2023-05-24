package backend.parser;

import java.util.*;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.*;
import backend.databaseModels.*;
import backend.exceptions.InvalidSQLCommand;
import backend.exceptions.SQLParseException;
import backend.databaseModels.conditions.*;

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
        "!=", "=", ">=", "<=", ">", "<", "between",
        "foreign", "primary", "key", "unique", "references",
        "and"
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
        DATABASE("database"), TABLE("table"), COLUMN("column"), INDEX("index");
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

            if (firstWord.equals("use")) {
                return parseUseDatabase(tokens, it);
            }
            else if (firstWord.equals("select")) {
                return parseSelect(tokens, databaseName, it);
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
                    if (secondWord.equals("index")) {
                        return parseCreateIndex(tokens, databaseName, it);
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

    private enum SELECT_STATE {
        SELECT, FROM, JOIN, WHERE, END
    }
    private DatabaseAction parseSelect(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        ArrayList<String> columnsTables = new ArrayList<>();
        ArrayList<String> columns = new ArrayList<>();
        String fromTable = null;
        ArrayList<Condition> conditions = new ArrayList<>();

        SELECT_STATE state = SELECT_STATE.SELECT;

        while (state != SELECT_STATE.END) {
            switch (state) {
                case SELECT: {
                    while (true) {
                        if (!it.hasNext()) {
                            throw (new SQLParseException("Unexpected end of input in SELECT clause"));
                        }

                        String nextToken = it.next();
                        if (nextToken.equals("from")) {
                            if (columns.size() == 0) {
                                throw (new SQLParseException("Missing column name(s) between `select` and `from`"));
                            }
                            state = SELECT_STATE.FROM;
                            break;
                        } else if (nextToken.equals(",")) {
                            if (columns.size() == 0) {
                                throw (new SQLParseException("Missing column name(s) between `select` and `,`"));
                            }
                            // continue
                        } else if (nextToken.equals("*")) {
                            columnsTables.add(null);
                            columns.add(nextToken);
                            // continue
                        } else {
                            String field = nextToken;

                            if (field.contains(".")) {
                                String[] split = field.split("[.]", 2);

                                String tableName = split[0];
                                String columnName = split[1];

                                checkName(tableName, NAME_TYPE.TABLE);
                                checkName(columnName, NAME_TYPE.COLUMN);

                                columnsTables.add(tableName);
                                columns.add(columnName);
                            } else {
                                checkName(field, NAME_TYPE.COLUMN);

                                columnsTables.add(null);
                                columns.add(field);
                            }
                        }
                    }

                    break;
                }
                case FROM: {
                    if (!it.hasNext()) {
                        throw (new SQLParseException("Expected table name after `from`"));
                    }
                    String tableName = it.next();
                    checkName(tableName, NAME_TYPE.TABLE);

                    fromTable = tableName;

                    // Select that ends on FROM clause is valid, check for end of input
                    if (!it.hasNext()) {
                        state = SELECT_STATE.END;
                        break;
                    }

                    // Else go onto next clause: join or where
                    // TODO after adding GROUP BY, add it here
                    String nextToken = it.next();
                    if (nextToken.equals("join")) {
                        // TODO: Implement JOIN clause into switch statement
                        state = SELECT_STATE.JOIN;
                        throw new RuntimeException("Implement this you lazy bastard");
                    } else if (nextToken.equals("where")) {
                        state = SELECT_STATE.WHERE;
                    }

                    break;
                }
                case WHERE: {
                    String field1TableName = null, field1ColumnName;
                    String field2TableName = null, field2ColumnName;

                    // where condition always starts with at least a field
                    String field1 = it.next();
                    String[] split = field1.split("[.]", 2);
                    if (split.length == 2) {
                        field1TableName = split[0];
                        field1ColumnName = split[1];

                        checkName(field1TableName, NAME_TYPE.TABLE);
                        // TODO: maybe check column name too?
                    } else {
                        field1ColumnName = field1;
                    }

                    // next we get a function or operator (between, <, >, =, etc)
                    String opOrFunc = it.next();

                    // case for operator
                    if (Operator.isValidOperator(opOrFunc)) {
                        Operator op = Operator.getOperator(opOrFunc);

                        String field2 = it.next();
                        split = field2.split("[.]", 2);
                        if (split.length == 2) {
                            field2TableName = split[0];
                            field2ColumnName = split[1];

                            checkName(field1TableName, NAME_TYPE.TABLE);
                            // TODO: maybe check column name too?
                        } else {
                            field2ColumnName = field2;
                        }

                        Equation c = new Equation(field1TableName, field1ColumnName, op, field2TableName, field2ColumnName);
                        conditions.add(c);
                    } // case for function
                    else if (Function.isValidFunction(opOrFunc)) {
                        Function func = Function.getFunction(opOrFunc);

                        int numArgs = Function.getNumArgs(func);

                        ArrayList<String> args = new ArrayList<>();
                        for (int i=0; i<numArgs; i++) {
                            String arg = it.next();
                            args.add(arg);
                        }

                        FunctionCall fc = new FunctionCall(field1TableName, field1ColumnName, func, args);
                        conditions.add(fc);
                    }
                    else {
                        throw (new SQLParseException("Expected operator or function after field name in WHERE clause"));
                    }

                    if (it.hasNext()) {
                        String nextToken = it.next();
                        if (nextToken.equals("and")) {
                            continue;
                        }
                    }

                    if (!it.hasNext()) {
                        state = SELECT_STATE.END;
                    }
                }
            }
        }

        System.out.println(fromTable);
        System.out.println(columnsTables);
        System.out.println(columns);
        System.out.println(conditions);

        // return new SelectAction(databaseName, columnsTables, columns, fromTable, whereClause);
        return null;
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
    private InsertIntoAction parseInsertInto(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
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

        return new InsertIntoAction(databaseName, tableName, values);
    }

    private DeleteFromAction parseDeleteFrom(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
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

        DeleteFromAction da = new DeleteFromAction(databaseName, tableName, keys);
        return da;
    }

    private enum CreateIndexStates {
        GET_TABLE_NAME, GET_FIELD_NAMES, GET_VALUES, GET_VALUES_STRINGS, CLOSING_BRACKET
    }

    private CreateIndexAction parseCreateIndex(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String tableName = "";
        String indexName = "";
        ArrayList<String> columns = new ArrayList<>();
        IndexFileModel ifm = new IndexFileModel();

        String token;

        try {
            if (!it.hasNext()) {
                throw(new SQLParseException("Missing token for index name"));
            }

            // get indexName and optional `unique` keyword first
            token = it.next();
            if (token.equals("unique")) {
                ifm.setUnique(true);
                indexName = it.next();
            }
            else {
                ifm.setUnique(false);
                indexName = token;
            }
            checkName(indexName, NAME_TYPE.INDEX);
            ifm.setIndexName(indexName);

            // `ON` keyword
            if (!it.hasNext()) {
                throw(new SQLParseException("Expected keyword `on` after index name"));
            }
            token = it.next();
            if (!token.equals("on")) {
                throw(new SQLParseException("Expected keyword `on` after index name"));
            }

            // get table name
            tableName = it.next();
            checkName(tableName, NAME_TYPE.TABLE);

            // get parentheses first, and then column names to create index on
            if (!it.hasNext()) {
                throw new SQLParseException("Expected parenthesis after table name: `" + tableName + "`");
            }
            token = it.next();
            if (!token.equals("(")) {
                throw new SQLParseException("Expected parenthesis after table name: `" + tableName + "`");
            }
            if (!it.hasNext()) {
                throw new SQLParseException("Expected column name(s) in parentheses");
            }

            while (it.hasNext()) {
                String columnName = it.next();
                checkName(columnName, NAME_TYPE.COLUMN);

                columns.add(columnName);

                // if comma continue reading other column names
                String delimiter = it.next();
                if (delimiter.equals(",")) {
                    continue;
                }
                // if closing bracket, done
                else if (delimiter.equals(")")) {
                    break;
                }
                else {
                    throw new SQLParseException("Expected either closing bracket or comma after column name `" + columnName + "`");
                }
            }

            if (it.hasNext()) {
                throw new SQLParseException("Expected end of input after closing bracket of column-list");
            }

            ifm.setIndexFields(columns);
            ifm.setIndexFileName(tableName + ".index." + indexName + ".bin");

            return new CreateIndexAction(databaseName, tableName, ifm);
        }
        catch (NoSuchElementException e) {
            throw new SQLParseException("Unexpected end of command");
        }
    }

    /**
     * Converts string to lowercase
     * @param input SQL string
     * @return List of tokens
     */
    private static final String[] controlChars = {"(", ")", ","};
    private List<String> tokenize(String input) throws SQLParseException {
        List<String> tokens = new ArrayList<>();

        StringBuilder currentToken = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : input.toCharArray()) {
            if (insideQuotes) {
                // if we are inside quotes, we only care about closing quotes
                if (c == '"') {
                    currentToken.append(c);
                    insideQuotes = false;
                }
                // add every other character to the current token
                else {
                    currentToken.append(c);
                }
            }
            else {
                if (Character.isWhitespace(c)) {
                    // when not inside quotes, whitespace is delimiter
                    // we add the current token to the list and reset it
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                }
                // on encounter with quotes, we start a new token (string)
                else if (c == '"') {
                    currentToken.append(c);
                    insideQuotes = true;
                }
                // else on control characters, we add BOTH the current token AND the delimiter to the list and reset it
                else if (Arrays.asList(controlChars).contains(String.valueOf(c))) {
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
                // else we just add the character to the current token
                else {
                    currentToken.append(c);
                }
            }
        }

        if (insideQuotes) {
            throw new SQLParseException("Missing closing quotes");
        }

        // add the last token to the list if it is not empty
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        for (int i=0; i<tokens.size(); i++) {
            // lowercase every token that doesn't start and end with "
            if (!tokens.get(i).startsWith("\"") && !tokens.get(i).endsWith("\"")) {
                tokens.set(i, tokens.get(i).toLowerCase());
            }
            // else remove the " at beginning and end
            else {
                tokens.set(i, tokens.get(i).substring(1, tokens.get(i).length()-1));
            }
        }

        return tokens;
    }

}