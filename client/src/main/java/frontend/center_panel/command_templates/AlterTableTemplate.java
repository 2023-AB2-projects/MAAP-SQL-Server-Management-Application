package frontend.center_panel.command_templates;

public class AlterTableTemplate {
    @Override
    public String toString() {
        return """
               ALTER TABLE table_name
                   ADD [ COLUMN, CONSTRAINT ] name
               """;
    }
}
