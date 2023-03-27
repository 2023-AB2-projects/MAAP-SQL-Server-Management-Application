package backend.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import backend.databaseActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.InvalidSQLCommand;
import backend.exceptions.SQLParseException;
import backend.databaseActions.createActions.*;

public class Parser {
    Parser() {}

    private static String[] RESERVED_KEYWORDS = {
        "create", "drop", "database", "table",
        "select", "from", "where", "group", "order", "by",
        "update", "set",
        "insert", "into", "values",
        "delete", "from",
        "(", ")", ",",
        "!=", "=", ">=", "<=", ">", "<"
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
    private static boolean isAlphaNumeric(String token) {
        return token != null && token.matches("^[a-zA-Z]*$");
    }

    /**
     * @param token
     * @return checks if token is valid name for database/table/column
     * @throws SQLParseException
     */
    private boolean checkName(String token) throws SQLParseException {
        if (isKeyword(token)) {
            throw(new SQLParseException("Invalid name " + token + " (Reserved keyword)"));
        }
        if (!isAlphaNumeric(token)) {
            throw(new SQLParseException("Invalid name " + token + " (Name can only contain alphanumeric characters)"));
        }
        return true;
    } 

    // TODO: remove function parameter `databaseName` and replace it with field referencing ServerController
    /**
     * @param input SQL string
     * @return database action corresponding to string
     * @throws InvalidSQLCommand
     */
    public DatabaseAction parseInput(String input, String databaseName) throws InvalidSQLCommand, SQLParseException{
        List<String> tokens = tokenize(input);

        if (tokens.get(0) == "create") {
            if (tokens.get(1) == "database") {
                return parseCreateDatabase(tokens);
            }
            if (tokens.get(1) == "table") {
                return parseCreateTable(tokens, databaseName);
            }
        }
        if (tokens.get(0) == "drop") {
            if (tokens.get(1) == "database") {
                return parseDropDatabase(tokens);
            }
            if (tokens.get(1) == "table") {
                return parseDropTable(tokens, databaseName);
            }
        }

        throw(new InvalidSQLCommand("Unimplemented SQL command"));
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
        checkName(databaseName);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());

        CreateDatabaseAction cda = new CreateDatabaseAction(databaseModel);
        return cda;
    }

    private CreateTableAction parseCreateTable(List<String> tokens, String databaseName) throws SQLParseException {

        throw(new SQLParseException("Unimplemented action \"Create Table\" for parser"));
        //CreateTableAction cta = new CreateTableAction();
        //return cta;
    }

    private DropDatabaseAction parseDropDatabase(List<String> tokens) throws SQLParseException {
        if (tokens.size() < 2) {
            throw(new SQLParseException("Missing token for database name"));
        }
        if (tokens.size() > 3) {
            throw(new SQLParseException("Too many tokens after `" + tokens.get(2) + "`"));
        }

        String databaseName = tokens.get(2);
        checkName(databaseName);

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
        checkName(tableName);

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