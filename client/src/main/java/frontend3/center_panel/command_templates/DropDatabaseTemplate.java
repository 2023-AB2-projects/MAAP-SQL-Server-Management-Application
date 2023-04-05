package frontend3.center_panel.command_templates;

public class DropDatabaseTemplate {
    @Override
    public String toString() {
        return """
               DROP DATABASE database_name
               """;
    }
}
