package frontend;

import control.ClientController;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class ClientFrame extends javax.swing.JFrame {
    // References
    private final ClientController clientController;

    public ClientFrame(ClientController clientController) {
        // Reference
        this.clientController = clientController;

        initComponents();

        // Set references
        this.centerClientPanel1.setClientController(this.clientController);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        objectExplorerPanel1 = new frontend.object_explorer.ObjectExplorerPanel();
        centerClientPanel1 = new frontend.center_panel.CenterClientPanel();
        projectManagerPanel1 = new frontend.project_manager.ProjectManagerPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MAAP SQL Server Management Application");
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(objectExplorerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(centerClientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectManagerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(objectExplorerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(centerClientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(projectManagerPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /* Setters */
    public void setInputTextAreaString(String string) { this.centerClientPanel1.setInputTextAreaString(string); }

    public void setOutputAreaString(String string) { this.centerClientPanel1.setOutputAreaString(string); }

    public void setErrorOutputAreaString(String error) { this.centerClientPanel1.setErrorOutputAreaString(error); }

    public void setOutputTableData(ArrayList<String> fieldNames, ArrayList<ArrayList<String>> data) { this.centerClientPanel1.setOutputTableData(fieldNames, data); }

    public void setCurrentDatabaseName(String databaseName) { this.objectExplorerPanel1.updateCurrentDatabase(databaseName); }

    public void increaseCenterPanelFont() { this.centerClientPanel1.increaseFont(); }
    public void decreaseCenterPanelFont() { this.centerClientPanel1.decreaseFont(); }

    public void update() {
        this.centerClientPanel1.update();
    }

    public void updateObjectExplorer() { this.objectExplorerPanel1.update(); }

    /* Getters */
    public String getInputTextAreaString() { return this.centerClientPanel1.getInputAreaText(); }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private frontend.center_panel.CenterClientPanel centerClientPanel1;
    private frontend.object_explorer.ObjectExplorerPanel objectExplorerPanel1;
    private frontend.project_manager.ProjectManagerPanel projectManagerPanel1;
    // End of variables declaration//GEN-END:variables
}
