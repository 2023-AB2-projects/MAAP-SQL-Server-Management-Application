package frontend;

import backend.MessageModes;
import control.ClientController;
import frontend.MenuItems.CommandMenuItem;
import frontend.MenuItems.SelectMenuItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

@Slf4j
public class MenuController extends JMenuBar implements MouseListener {
    private JMenu selectMenu, createAlterMenu, dropDeleteMenu, insertUpdateMenu;
    private JButton sendCommandButton, useButton;

    @Getter
    private JComboBox<String> databaseSelector;
    // References
    private GUIController guiController;

    public MenuController(GUIController guiController) {
        // Reference
        this.guiController = guiController;

        // Init main menu and menu bar
        this.initMenus();

        // Add menus to menu bar
        this.addComponentsToMenuBar();

        // Add menu items to menus
        this.addMenuItems();

        // Add listeners to menu items
        this.addListeners();
    }

    private void initMenus() {
        // Init sub menus

        this.databaseSelector = new JComboBox<>();
        databaseSelector.addItem("master");
        databaseSelector.setSelectedIndex(0);

        this.selectMenu = new JMenu("SELECT");
        this.createAlterMenu = new JMenu("CREATE/ALTER");
        this.dropDeleteMenu = new JMenu("DROP/DELETE");
        this.insertUpdateMenu = new JMenu("INSERT/UPDATE");

        // Init button
        this.sendCommandButton = new JButton("Run command");
        this.sendCommandButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        this.useButton = new JButton("USE: ");
    }

    private void addComponentsToMenuBar() {
        this.add(this.useButton);
        this.add(this.databaseSelector);
        this.add(this.selectMenu);
        this.add(this.createAlterMenu);
        this.add(this.dropDeleteMenu);
        this.add(this.insertUpdateMenu);

        this.add(Box.createHorizontalGlue());       // Add filler so send command is on the right side

        this.add(this.sendCommandButton);
    }

    private void addMenuItems() {
        this.selectMenu.add(new SelectMenuItem());
    }

    private void addListeners() {
        useButton.addMouseListener(this);

        for (Component menuItem : this.selectMenu.getMenuComponents()) {
            menuItem.addMouseListener(this);
        }
    }

    public void addDatabaseNames(String databaseNames){
        databaseSelector.removeAllItems();
        if(databaseNames.equals("[]")){
            return;
        }
        databaseNames = databaseNames.substring(1, databaseNames.length() - 1);

        String[] listOfDatabaseNames = databaseNames.split(",");
        for (String databaseName : listOfDatabaseNames) {
            databaseSelector.addItem(databaseName);
        }
        databaseSelector.setSelectedItem(guiController.getSelectedDatabase());
    }

    /* Getters */
    public JButton getSendCommandButton() { return this.sendCommandButton; }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource().equals(useButton)){
            ClientController clientController = guiController.getClientController();
            clientController.sendCommandToServer("USE " + databaseSelector.getSelectedItem());
            guiController.receiveMessageAndPerformAction(MessageModes.none);
            guiController.receiveMessageAndPerformAction(MessageModes.none);
            guiController.setSelectedDatabase((String) databaseSelector.getSelectedItem());
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if(!event.getSource().equals(useButton)){
            // Get event source and set text input area to that string
            CommandMenuItem menuItem = (CommandMenuItem) event.getSource();
            this.guiController.setInputTextAreaString(menuItem.getCommandString());
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
