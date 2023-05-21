package frontend.center_panel.command_templates;

public class CreateNonUniqueIndexTemplate {
    @Override
    public String toString() {
        return "CREATE INDEX index_name\n" +
                "ON table_name (field_name)";
    }
}
