package frontend.center_panel.command_templates;

public class DeleteFromTemplate {
    @Override
    public String toString() {
        return """
               DELETE FROM table_name
               WHERE column_name condition
               """;
    }
}
