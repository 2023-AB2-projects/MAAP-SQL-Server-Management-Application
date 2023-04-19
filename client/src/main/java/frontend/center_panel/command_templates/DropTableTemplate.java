package frontend.center_panel.command_templates;

public class DropTableTemplate {
    @Override
    public String toString() {
        return """
               DROP TABLE table_name
               """;
    }
}
