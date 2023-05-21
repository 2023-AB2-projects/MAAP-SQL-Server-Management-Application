package frontend.center_panel.command_templates;

public class CreateUniqueIndexTemplate {
    @Override
    public String toString() {
        return "CREATE UNIQUE INDEX index_name\n" +
                "ON table_name (field_name)";
    }
}
