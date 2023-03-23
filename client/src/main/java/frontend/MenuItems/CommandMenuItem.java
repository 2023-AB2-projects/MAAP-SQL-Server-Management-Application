package MenuItems;

import javax.swing.*;

public abstract class CommandMenuItem extends JMenuItem {
    private String commandString;

    public CommandMenuItem(String menuItemString, String commandString) {
        this.setText(menuItemString);
        this.commandString = commandString;
    }

    /* Getters */
    public String getCommandString() { return this.commandString; }
}
