package backend.databaseModels.conditions;

import lombok.*;

// Equation structure:
// lFieldTable.lFieldName op rFieldTable.rFieldName

@Getter
@Setter
public class Equation implements Condition {
    // the two table Strings can be null

    private String lFieldTable;
    private String lFieldName;

    private Operator op;

    private String rFieldTable;
    private String rFieldName;

    public Equation(String lFieldTable, String lFieldName, Operator op, String rFieldTable, String rFieldName) {
        if (lFieldName == null || op == null || rFieldName == null) {
            throw new IllegalArgumentException("Equation constructor parameters lField and rField cannot be null");
        }

        this.lFieldTable = lFieldTable;
        this.lFieldName = lFieldName;
        this.op = op;
        this.rFieldTable = rFieldTable;
        this.rFieldName = rFieldName;
    }

    public Equation(String lFieldName, Operator op, String rFieldName) {
        this(null, lFieldName, op, null, rFieldName);
    }
}
