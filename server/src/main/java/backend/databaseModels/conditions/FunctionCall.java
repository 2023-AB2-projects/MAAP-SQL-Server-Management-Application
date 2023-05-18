package backend.databaseModels.conditions;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
public class FunctionCall implements Condition {
    // structure:
    // fieldTable.fieldName f(args...)

    private String fieldTable;
    private String fieldName;

    private Function function;
    private ArrayList<String> args;

    public FunctionCall(String fieldTable, String fieldName, Function function, ArrayList<String> args) {
        if (fieldName == null || function == null) {
            throw new IllegalArgumentException("FunctionCall constructor parameters fieldName and function parameters cannot be null");
        }

        this.fieldTable = fieldTable;
        this.fieldName = fieldName;
        this.function = function;
        this.args = args;

        // check if arguments given match the number of required arguments for the function
        int numArgsGotten;
        if (args == null) numArgsGotten = 0;
        else numArgsGotten = args.size();

        if (Function.getNumArgs(function) != numArgsGotten) {
            throw new IllegalArgumentException("FunctionCall constructor parameters `args` does not match the number of arguments for the function " + function + " (expected " + Function.getNumArgs(function) + ", got " + numArgsGotten + ")");
        }
    }
}
