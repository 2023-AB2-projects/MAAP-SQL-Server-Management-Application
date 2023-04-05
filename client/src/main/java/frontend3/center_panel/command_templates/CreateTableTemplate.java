package frontend3.center_panel.command_templates;

public class CreateTableTemplate {
    @Override
    public String toString() {
        return """
               CREATE TABLE table_name (
                   field_1 type;
                   field_2 type
               )
               """;
    }
}
