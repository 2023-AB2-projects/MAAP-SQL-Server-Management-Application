package backend.databaseModels.conditions;

public enum Function {
    BETWEEN;

    public static Function getFunction(String s) {
        return switch (s) {
            case "between" -> BETWEEN;
            default -> throw new IllegalArgumentException("Invalid function: " + s);
        };
    }

    public static int getNumArgs(Function f) {
        return switch (f) {
            case BETWEEN -> 2;
        };
    }

    public static boolean isValidFunction(String s) {
        try {
            getFunction(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
