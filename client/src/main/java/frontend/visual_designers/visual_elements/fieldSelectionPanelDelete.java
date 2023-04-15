package frontend.visual_designers.visual_elements;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.ArrayList;

@Slf4j
public class FieldSelectionPanelDelete extends javax.swing.JPanel {
    // Logic
    private final ArrayList<FieldPanelDelete> fieldPanels;

    public FieldSelectionPanelDelete() {
        initComponents();

        // Init variables
        this.fieldPanels = new ArrayList<>();
    }

    /* Setters */
    public void setFieldPanelData(ArrayList<String> columnNames, ArrayList<String> tableNames) {
        // Check equal length
        if(columnNames.size() != tableNames.size()) {
            log.error("ColumnNames length must match table names length!");
        }

        // Clear old panels
        for(final FieldPanelDelete panel : this.fieldPanels) {
            this.selectionPanel.remove(panel);
        }
        this.fieldPanels.clear();       // Clear old panels

        // Iterate over names and create new panels
        int length = Math.min(columnNames.size(), tableNames.size());
        this.selectionPanel.setLayout(new GridLayout(length, 1));
        for(int i = 0; i < length; ++i) {
            FieldPanelDelete panel = new FieldPanelDelete();
            panel.setColumnName(columnNames.get(i));
            panel.setTableName(tableNames.get(i));

            // Set sizes
            Dimension size = new Dimension(1000, 50);
            panel.setMinimumSize(size);
            panel.setMaximumSize(size);
            panel.setPreferredSize(size);

            // Add to list and selection panels
            this.fieldPanels.add(panel);
            this.selectionPanel.add(panel);
        }

        // Revalidate and redraw panels
        this.selectionPanel.revalidate();
        this.selectionPanel.repaint();

        this.revalidate();
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        onOffLabel = new javax.swing.JLabel();
        columnLabel = new javax.swing.JLabel();
        conditionLabel3 = new javax.swing.JLabel();
        tableLabel = new javax.swing.JLabel();
        conditionLabel1 = new javax.swing.JLabel();
        conditionLabel2 = new javax.swing.JLabel();
        selectionPanel = new javax.swing.JPanel();

        onOffLabel.setBackground(new java.awt.Color(102, 102, 102));
        onOffLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        onOffLabel.setText("On/Off");
        onOffLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        onOffLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        onOffLabel.setOpaque(true);

        columnLabel.setBackground(new java.awt.Color(102, 102, 102));
        columnLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        columnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        columnLabel.setText("Column Name");
        columnLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        columnLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        columnLabel.setOpaque(true);

        conditionLabel3.setBackground(new java.awt.Color(102, 102, 102));
        conditionLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        conditionLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conditionLabel3.setText("Or...");
        conditionLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        conditionLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        conditionLabel3.setOpaque(true);

        tableLabel.setBackground(new java.awt.Color(102, 102, 102));
        tableLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tableLabel.setText("Table Name");
        tableLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableLabel.setOpaque(true);

        conditionLabel1.setBackground(new java.awt.Color(102, 102, 102));
        conditionLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        conditionLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conditionLabel1.setText("Condition");
        conditionLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        conditionLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        conditionLabel1.setOpaque(true);

        conditionLabel2.setBackground(new java.awt.Color(102, 102, 102));
        conditionLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        conditionLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conditionLabel2.setText("Or...");
        conditionLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        conditionLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        conditionLabel2.setOpaque(true);

        javax.swing.GroupLayout selectionPanelLayout = new javax.swing.GroupLayout(selectionPanel);
        selectionPanel.setLayout(selectionPanelLayout);
        selectionPanelLayout.setHorizontalGroup(
            selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        selectionPanelLayout.setVerticalGroup(
            selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(onOffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(columnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(conditionLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(conditionLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(conditionLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(selectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(columnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(onOffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conditionLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conditionLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conditionLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel columnLabel;
    private javax.swing.JLabel conditionLabel1;
    private javax.swing.JLabel conditionLabel2;
    private javax.swing.JLabel conditionLabel3;
    private javax.swing.JLabel onOffLabel;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JLabel tableLabel;
    // End of variables declaration//GEN-END:variables
}
