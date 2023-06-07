package backend.parser;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.CreateDatabaseAction;
import backend.databaseActions.createActions.CreateIndexAction;
import backend.databaseActions.createActions.CreateTableAction;
import backend.databaseActions.createActions.InsertIntoAction;
import backend.databaseActions.dropActions.DeleteFromAction;
import backend.databaseActions.dropActions.DropDatabaseAction;
import backend.databaseActions.dropActions.DropTableAction;
import backend.databaseActions.miscActions.NothingDatabaseAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseActions.themightySelectAction.SelectAction;
import backend.databaseModels.*;
import backend.databaseModels.aggregations.Aggregator;
import backend.databaseModels.aggregations.AggregatorSymbol;
import backend.databaseModels.conditions.*;
import backend.exceptions.InvalidSQLCommand;
import backend.exceptions.SQLParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.*;

import static backend.databaseModels.aggregations.AggregatorSymbol.isValidAggregatorSymbol;

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
        "and",
        "sum", "min", "max", "avg", "count"
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

    /**
     * @param input SQL string
     * @return database action corresponding to string
     * @throws InvalidSQLCommand
     */
    public DatabaseAction parseInput(String input, String databaseName) throws SQLParseException {

        // if the sql message is empty return nothing
        if (input.equals("")) { return new NothingDatabaseAction(); }

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
        SELECT, FROM, JOIN, WHERE, END, GROUP_BY
    }
    private DatabaseAction parseSelect(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String baseTable = "";
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Condition> conditions = new ArrayList<>();

        List<String> joinTables = new ArrayList<>();            // not included in SelectAction, but parsed for proofing
        List<JoinModel> joinModels = new ArrayList<>();

        List<String> groupedByColumns = new ArrayList<>();
        ArrayList<Aggregator> aggregations = new ArrayList<>();

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
                            if (columns.size() + aggregations.size() == 0) {
                                throw (new SQLParseException("Missing column name(s) between `select` and `from`"));
                            }
                            state = SELECT_STATE.FROM;
                            break;
                        } else if (nextToken.equals(",")) {
                            if (columns.size() + aggregations.size() == 0) {
                                throw (new SQLParseException("Missing column name(s) between `select` and `,`"));
                            }
                            // continue
                        } else if (nextToken.equals("*")) {
                            columns.add(nextToken);
                            // continue
                        } else {
                            // check if is aggregate function
                            if (isValidAggregatorSymbol(nextToken)) {
                                // check for format `function ( table.field ) `
                                AggregatorSymbol aggregatorSymbol = AggregatorSymbol.getAggregatorSymbol(nextToken);

                                if (!it.next().equals("(")) {
                                    throw (new SQLParseException("Expected opening parenthesis after aggregate function`" + nextToken + "`"));
                                }

                                // check for format `table.field`
                                String aggregateColumn = it.next();
                                if (aggregateColumn.split("\\.").length != 2) {
                                    throw (new SQLParseException("Invalid field name in projection: `" + aggregateColumn + "` - Must be in table.field format"));
                                }

                                if (!it.next().equals(")")) {
                                    throw (new SQLParseException("Expected closing parenthesis after aggregate function parameter`" + aggregateColumn + "`"));
                                }

                                aggregations.add(new Aggregator(aggregateColumn, aggregatorSymbol));
                            }
                            // else normal projection on column, needs to have format `table.field`
                            else if (nextToken.split("\\.").length == 2) {
                                columns.add(nextToken);
                            } else {
                                throw new SQLParseException("Invalid field name in projection: `" + nextToken + "` - Must be in table.field format");
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

                    baseTable = tableName;

                    // Select that ends on FROM clause is valid, check for end of input
                    if (!it.hasNext()) {
                        state = SELECT_STATE.END;
                        break;
                    }

                    // continue parsing
                    String nextToken = it.next();
                    if (nextToken.equals("join")) {
                        state = SELECT_STATE.JOIN;
                    } else if (nextToken.equals("where")) {
                        state = SELECT_STATE.WHERE;
                    } else if (nextToken.equals("group")) {
                        state = SELECT_STATE.GROUP_BY;
                    } else {
                        throw new SQLParseException("Expected `join`, `where`, or `group by` after `from` clause");
                    }

                    break;
                }
                case JOIN: {
                    if (!it.hasNext()) {
                        throw (new SQLParseException("Expected table name after `join`"));
                    }

                    // get table name of join
                    String tableName = it.next();
                    joinTables.add(tableName);

                    // `on` keyword is required
                    if (!it.hasNext() || !it.next().equals("on")) {
                        throw (new SQLParseException("Expected `on` after `join` and table name `" + tableName + "`"));
                    }

                    // get join condition
                    String field1 = it.next();
                    String opOrFunc = it.next();
                    String field2 = it.next();

                    // both field1 and field2 need to be format `table.field`
                    // opOrFunc can only be EQUALS on join

                    // validate if field1 and field2 are in format `table.field`
                    String[] split1 = field1.split("\\.");
                    String[] split2 = field2.split("\\.");
                    if (split1.length != 2) {
                        throw (new SQLParseException("Invalid field name in join condition left side: `" + field1 + "` - Must be in table.field format"));
                    }
                    if (split2.length != 2) {
                        throw (new SQLParseException("Invalid field name in join condition right side: `" + field2 + "` - Must be in table.field format"));
                    }

                    // validate that opOrFunc is EQUALS
                    if (!Operator.isValidOperator(opOrFunc) || Operator.getOperator(opOrFunc) != Operator.EQUALS) {
                        throw (new SQLParseException("Invalid operator: " + opOrFunc + " - Must be `=`"));
                    }

                    // add to join model
                    // according to special requests, JoinModel tableNames contain the 'tablename', but fieldNames contain 'tablename.fieldname'
                    joinModels.add(new JoinModel(split1[0], field1, split2[0], field2));

                    // check if end of input
                    if (!it.hasNext()) {
                        state = SELECT_STATE.END;
                        break;
                    }

                    // continue parsing
                    String nextToken = it.next();
                    if (nextToken.equals("join")) {
                        continue;
                    } else if (nextToken.equals("where")) {
                        state = SELECT_STATE.WHERE;
                    } else if (nextToken.equals("group")) {
                        state = SELECT_STATE.GROUP_BY;
                    } else {
                        throw new SQLParseException("Expected `join` or `where` after join condition, got `" + nextToken + "`");
                    }
                    break;
                }
                case WHERE: {
                    // where condition starts with a field name in format `table.field`
                    String field1 = it.next();
                    String[] split1 = field1.split("\\.");
                    if (split1.length != 2) {
                        throw new SQLParseException("Invalid field name in where condition left side: `" + field1 + "` - Must be in table.field format");
                    }

                    // next we get a function or operator (between, <, >, =, etc)
                    String opOrFunc = it.next();

                    // case for operator
                    if (Operator.isValidOperator(opOrFunc)) {
                        Operator op = Operator.getOperator(opOrFunc);

                        // on the right side of a condition, we have a constant
                        String field2 = it.next();

                        // once again, according to special requests, Equation tableNames contain the 'tablename', but fieldNames contain 'tablename.fieldname'
                        // field2 is a constant, so column is null
                        Equation equation = new Equation(split1[0], field1, op, null, field2);
                        conditions.add(equation);
                    } // case for function
                    else if (Function.isValidFunction(opOrFunc)) {
                        Function func = Function.getFunction(opOrFunc);

                        int numArgs = Function.getNumArgs(func);

                        ArrayList<String> args = new ArrayList<>();
                        for (int i=0; i<numArgs; i++) {
                            String arg = it.next();
                            args.add(arg);
                        }

                        FunctionCall fc = new FunctionCall(split1[0], field1, func, args);
                        conditions.add(fc);
                    }
                    else {
                        throw (new SQLParseException("Expected operator or function after field name in WHERE clause"));
                    }

                    // check for end
                    if (!it.hasNext()) {
                        state = SELECT_STATE.END;
                        break;
                    }

                    // continue parsing
                    String nextToken = it.next();
                    if (nextToken.equals("and")) {
                        continue;
                    }
                    else if (nextToken.equals("group")) {
                        state = SELECT_STATE.GROUP_BY;
                    }
                    else {
                        throw (new SQLParseException("Expected `and` or 'group by' or end of input after where condition, got `" + nextToken + "`"));
                    }
                    break;
                }
                case GROUP_BY: {
                    if (!it.hasNext() || !it.next().equals("by")) {
                        throw (new SQLParseException("Expected keyword `by` after `group`"));
                    }

                    // get column name list
                    while (true) {
                        String columnName = it.next();

                        // check if columnName is in format `table.field`
                        if (columnName.split("\\.").length != 2) {
                            throw (new SQLParseException("Invalid column name in group by clause: `" + columnName + "` - Must be in table.field format"));
                        }

                        groupedByColumns.add(columnName);

                        // check if end of input
                        if (!it.hasNext()) {
                            state = SELECT_STATE.END;
                            break;
                        }

                        if (it.next().equals(",")) {
                            continue;
                        } else {
                            throw new SQLParseException("Expected `,` or end of input after column name in group by clause");
                        }
                    }
                    break;
                }
            }
        }

        log.info("BaseTable: " + baseTable);
        log.info("Columns: " + columns);
        log.info("Aggregations: " + aggregations);
        log.info("JoinTables: " + joinTables);
        log.info("JoinModels: " + joinModels);
        log.info("Conditions: " + conditions);
        log.info("GroupedByColumns: " + groupedByColumns);

        return new SelectAction(databaseName, baseTable, columns, conditions, joinModels, joinTables, groupedByColumns, aggregations);
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
        ArrayList<Condition> conditions = new ArrayList<>();

        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for table name"));
        }

        tableName = it.next();

        // check for conditions
        if (it.hasNext()) {

            if (!it.next().equals("where")) {
                throw(new SQLParseException("Expected `where` keyword or end of input after table name"));
            }

            while (true) {
                if (!it.hasNext()) {
                    throw(new SQLParseException("Unexpected end of input"));
                }

                String field1 = it.next();
                String opOrFunc = it.next();

                // check if field name has structure `table.field`
                String[] split1 = field1.split("\\.");
                if (split1.length != 2) {
                    throw (new SQLParseException("Invalid field name: `" + field1 + "` - Must be in table.field format"));
                }

                // note: copy-pasterino from SelectFromAction
                // case for operator
                if (Operator.isValidOperator(opOrFunc)) {
                    Operator op = Operator.getOperator(opOrFunc);

                    // on the right side of a condition, we have a constant
                    String field2 = it.next();

                    // once again, according to special requests, Equation tableNames contain the 'tablename', but fieldNames contain 'tablename.fieldname'
                    // field2 is a constant, so column is null
                    Equation equation = new Equation(split1[0], field1, op, null, field2);
                    conditions.add(equation);
                } // case for function
                else if (Function.isValidFunction(opOrFunc)) {
                    Function func = Function.getFunction(opOrFunc);

                    int numArgs = Function.getNumArgs(func);

                    ArrayList<String> args = new ArrayList<>();
                    for (int i=0; i<numArgs; i++) {
                        String arg = it.next();
                        args.add(arg);
                    }

                    FunctionCall fc = new FunctionCall(split1[0], field1, func, args);
                    conditions.add(fc);
                }
                else {
                    throw (new SQLParseException("Expected operator or function after field name in WHERE clause"));
                }

                // stop if end of input
                if (!it.hasNext()) {
                    break;
                }

                // continue if there is another condition
                String nextToken = it.next();
                if (nextToken.equals("and")) {
                    continue;
                } else {
                    throw(new SQLParseException("Expected `and` or end of input after condition"));
                }
            }
        }

        log.info("tableName: " + tableName);
        log.info("conditions: " + conditions.toString());

        // DeleteFromAction da = new DeleteFromAction(databaseName, tableName, keys);
        return null;
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