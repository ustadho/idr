/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

/**
 *
 * @author faheem
 */
public class MenuTaskPane extends javax.swing.JPanel {

    /**
     * Creates new form MenuTaskPane
     */
    public MenuTaskPane() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        jXTaskPane1 = new org.jdesktop.swingx.JXTaskPane();
        jXTaskPane2 = new org.jdesktop.swingx.JXTaskPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jXTaskPane1.setTitle("Master");
        jXTaskPaneContainer1.add(jXTaskPane1);

        jXTaskPane2.setTitle("Transaksi");
        jXTaskPaneContainer1.add(jXTaskPane2);

        jPanel1.add(jXTaskPaneContainer1);

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane2;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    // End of variables declaration//GEN-END:variables
}