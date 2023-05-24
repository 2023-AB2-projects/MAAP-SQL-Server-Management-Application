package backend.databaseModels.conditions;

public enum Operator {
    EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN_OR_EQUAL_TO;

    public static Operator getOperator(String op) {
        return switch (op) {
            case "=" -> Operator.EQUALS;
            case "!=" -> Operator.NOT_EQUALS;
            case ">" -> Operator.GREATER_THAN;
            case "<" -> Operator.LESS_THAN;
            case ">=" -> Operator.GREATER_THAN_OR_EQUAL_TO;
            case "<=" -> Operator.LESS_THAN_OR_EQUAL_TO;
            default -> throw new IllegalArgumentException("Invalid operator: " + op);
        };
    }

    public static boolean isValidOperator(String op) {
        try {
            getOperator(op);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
