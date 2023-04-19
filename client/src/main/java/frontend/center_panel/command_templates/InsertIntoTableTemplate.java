package frontend.center_panel.command_templates;

public class InsertIntoTableTemplate {
    @Override
    public String toString() {
        return """
               INSERT INTO table_name(value_1, value_2)
               VALUES (value_1, value_2)
               """;
    }
}
