package frontend.center_panel;

import control.ClientController;
import frontend.other_elements.SQLDocument;
import frontend.other_elements.TabConfig;
import lombok.Setter;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CommandInputOutputPanel extends javax.swing.JPanel {
    @Setter
    private ClientController clientController;

    private static Style redStyle;
    private static Style greenStyle;

    public CommandInputOutputPanel() {
        initComponents();

        // Setting the number of tabs and their length
        this.inputArea.setParagraphAttributes(TabConfig.getTabAttributeSet(), false);

        // Get the text pane's document
        StyledDocument doc = this.outputArea.getStyledDocument();

        // Create the red style with red font color
        redStyle = this.outputArea.addStyle("RedColorStyle", null);
        StyleConstants.setForeground(redStyle, new Color(255, 111, 111));

        // Create the green style with green font color
        greenStyle = this.outputArea.addStyle("GreenColorStyle", null);
        StyleConstants.setForeground(greenStyle, new Color(84, 190, 84));

        doc.setParagraphAttributes(0, doc.getLength(), greenStyle, false);

        // By default, the output is a text area
        this.outputTabbedPane.setSelectedIndex(0);
        this.outputTabbedPane.setEnabledAt(1, false);   // Disable table panel

        // Make JTable not editable
        this.outputTable.setDefaultEditor(Object.class, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commandSplitPane = new javax.swing.JSplitPane();
        input = new javax.swing.JScrollPane();
        inputArea = new javax.swing.JTextPane();
        outputTabbedPane = new javax.swing.JTabbedPane();
        outputAreaScrollPanel = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextPane();
        outputTableScrollPanel = new javax.swing.JScrollPane();
        outputTable = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(900, 1000));
        setPreferredSize(new java.awt.Dimension(900, 900));

        commandSplitPane.setDividerLocation(500);
        commandSplitPane.setDividerSize(3);
        commandSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        inputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        inputArea.setStyledDocument(new SQLDocument());
        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputAreaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputAreaKeyReleased(evt);
            }
        });
        input.setViewportView(inputArea);

        commandSplitPane.setLeftComponent(input);

        outputTabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        outputArea.setEditable(false);
        outputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputAreaScrollPanel.setViewportView(outputArea);

        outputTabbedPane.addTab("Command Output", outputAreaScrollPanel);

        outputTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        outputTable.setGridColor(new java.awt.Color(102, 102, 102));
        outputTable.setRowHeight(25);
        outputTable.setShowGrid(true);
        outputTable.getTableHeader().setReorderingAllowed(false);
        outputTableScrollPanel.setViewportView(outputTable);

        outputTabbedPane.addTab("Table Output", outputTableScrollPanel);

        commandSplitPane.setRightComponent(outputTabbedPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputAreaKeyReleased

    }//GEN-LAST:event_inputAreaKeyReleased

    private void inputAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputAreaKeyPressed
        // Check if Ctrl + S was pressed
        if ((evt.getKeyCode() == KeyEvent.VK_S) && ((evt.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
            this.clientController.saveCurrentFile();
        } else {
            this.clientController.inputAreaChanged();
        }
    }//GEN-LAST:event_inputAreaKeyPressed

    /* Setters */
    public void setInputAreaFont(Font font) { this.inputArea.setFont(font); }

    public void setInputTextAreaString(String string) { this.inputArea.setText(string); }

    public void setOutputAreaString(String string) {
        this.outputArea.setText(string);

        // Change JTextPane color to white
        StyledDocument doc = this.outputArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), greenStyle, false);

        // Switch to first panel
        this.outputTabbedPane.setSelectedIndex(0);
    }

    public void setErrorOutputAreaString(String error) {
        this.outputArea.setText(error);

        // Change JTextPane color to red
        StyledDocument doc = this.outputArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), redStyle, false);

        // Switch to first panel
        this.outputTabbedPane.setSelectedIndex(0);
    }

    public void setOutputTableData(ArrayList<String> fieldNames, ArrayList<ArrayList<String>> data) {
        // Enable table panel
        this.outputTabbedPane.setEnabledAt(1, true);

        // Replace current table data with given data
        DefaultTableModel model = new DefaultTableModel(fieldNames.toArray(), 0);

        // Iterate over the data ArrayList
        for (final ArrayList<String> rowData : data) {
            // Create an array of Objects for each inner ArrayList<String>
            Object[] row = rowData.toArray();

            // Add the array of Objects as a row to the table model
            model.addRow(row);
        }

        // Set model to actual table
        this.outputTable.setModel(model);

        // Switch to second panel
        this.outputTabbedPane.setSelectedIndex(1);
    }

    public void increaseFont() {
        Font font = this.inputArea.getFont();
        Font newFont = new Font("Segoe", Font.PLAIN, font.getSize() + 1);

        // Set size
        this.inputArea.setFont(newFont);
        this.outputArea.setFont(newFont);
    }

    public void decreaseFont() {
        Font font = this.inputArea.getFont();
        if (font.getSize() > 0) {
            Font newFont = new Font("Segoe", Font.PLAIN, font.getSize() - 1);

            // Set size
            this.inputArea.setFont(newFont);
            this.outputArea.setFont(newFont);
        }
    }

    public void setLightMode() {
        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.inputArea.getStyledDocument();
        doc.lightMode();

        // Update text
        this.inputArea.setText(this.inputArea.getText());
    }

    public void setDarkMode() {
        // Update document syntax highlighting
        SQLDocument doc = (SQLDocument) this.inputArea.getStyledDocument();
        doc.darkMode();

        // Update text
        this.inputArea.setText(this.inputArea.getText());
    }

    /* Getters */
    public String getInputAreaText() { return this.inputArea.getText(); }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane commandSplitPane;
    private javax.swing.JScrollPane input;
    private javax.swing.JTextPane inputArea;
    private javax.swing.JTextPane outputArea;
    private javax.swing.JScrollPane outputAreaScrollPanel;
    private javax.swing.JTabbedPane outputTabbedPane;
    private javax.swing.JTable outputTable;
    private javax.swing.JScrollPane outputTableScrollPanel;
    // End of variables declaration//GEN-END:variables
}
