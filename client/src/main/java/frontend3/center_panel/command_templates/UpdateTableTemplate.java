package frontend3.center_panel.command_templates;

public class UpdateTableTemplate {
    @Override
    public String toString() {
        return """
               UPDATE table_name
               SET field_name = value
               """;
    }
}
