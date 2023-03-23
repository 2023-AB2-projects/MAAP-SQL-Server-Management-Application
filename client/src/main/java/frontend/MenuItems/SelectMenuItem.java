package frontend.MenuItems;

public class SelectMenuItem extends CommandMenuItem {
    public SelectMenuItem() {
        super("Default SELECT",
                new String(
                "SELECT *\n" +
                        "FROM table_name"
        ));
    }
}
