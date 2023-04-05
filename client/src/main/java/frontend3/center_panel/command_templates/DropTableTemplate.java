package frontend3.center_panel.command_templates;

public class DropTableTemplate {
    @Override
    public String toString() {
        return """
               DROP TABLE [ IF EXISTS ] table_name
               """;
    }
}
