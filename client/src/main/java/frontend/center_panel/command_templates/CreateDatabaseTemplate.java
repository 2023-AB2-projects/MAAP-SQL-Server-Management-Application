package frontend.center_panel.command_templates;

public class CreateDatabaseTemplate {
    @Override
    public String toString() {
        return """
               CREATE database_name
               """;
    }
}
