package frontend3.center_panel.command_templates;

public class FullSelectTemplate {
    @Override
    public String toString() {
        return  """
                SELECT *
                FROM table_name
                WHERE condition
                GROUP BY field
                HAVING aggregate
                ORDER BY field
                """;
    }
}
