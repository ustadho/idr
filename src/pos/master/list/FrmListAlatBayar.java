/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.list;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.dao.jdbc.AccAlatBayarDao;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.domain.AccAlatBayar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmListAlatBayar extends javax.swing.JInternalFrame {
    private Connection conn;
    AccAlatBayarDao dao;
    AccAlatBayar  ab;
    private GeneralFunction fn=new GeneralFunction();
    private String sOldKode;
    /**
     * Creates new form ItemKategori
     */
    public FrmListAlatBayar(Connection conn) {
        initComponents();
        this.conn=conn;
        dao=new AccAlatBayarDao(conn);
        for(int i=0; i<jTable1.getColumnCount(); i++){
            jTable1.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        } 
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=jTable1.getSelectedRow();
                if(iRow<0)
                    return;
                btnNew.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canInsert());
                btnSave.setEnabled(iRow>=0 && menuAuth!=null && (menuAuth.canUpdate()||menuAuth.canInsert()) );
                btnDelete.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canDelete());
                try {
                    ab=dao.getByKode(jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("Kode")).toString());
                    if(ab!=null){
                        txtKode.setText(ab.getKode());
                        txtAlatBayar.setText(ab.getAlatBayar());
                        chkLangsungCair.setSelected(ab.isLangsungCair());
                        sOldKode=jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("Kode")).toString();
                    }
                    
                    
                } catch (Exception ex) {
                    Logger.getLogger(FrmListAlatBayar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        tampilkanData("");
        
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        jTable1.addKeyListener(kListener);
    }
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;


    private void tampilkanData(String kode){
        try {
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            List<AccAlatBayar> list=dao.cariSemuaData();
            int i=0;
            for(AccAlatBayar ab : list){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    ab.getKode(),
                    ab.getAlatBayar(),
                    ab.isLangsungCair(),
                    
                });
                if(kode.equalsIgnoreCase(ab.getKode())){
                    i=jTable1.getRowCount()-1;
                }
            }
            if(jTable1.getRowCount()>0){
                jTable1.setRowSelectionInterval(i, i);
                jTable1.setModel(fn.autoResizeColWidth(jTable1, ((DefaultTableModel)jTable1.getModel())).getModel());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmListAlatBayar.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void udfNew() {
        btnNew.requestFocusInWindow();
        txtKode.setText("");
        txtAlatBayar.setText("");
        txtKode.requestFocusInWindow();
        sOldKode="";
    }

    private void udfSave() {
        btnSave.requestFocusInWindow();
        if(txtKode.getText().equalsIgnoreCase("") || txtKode.getText().length()>2){
            JOptionPane.showMessageDialog(this, "Kode Alat Bayar harus diisi, maksimal 2 karater!");
            txtKode.requestFocusInWindow();
            return;
        }
        if(txtAlatBayar.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Alat Bayar harus diisi!");
            txtAlatBayar.requestFocusInWindow();
            return;
        }
        try {
            AccAlatBayar k=new AccAlatBayar();
            k.setKode(txtKode.getText());
            k.setAlatBayar(txtAlatBayar.getText());
            k.setLangsungCair(chkLangsungCair.isSelected());
            dao.simpan(k, sOldKode);
            sOldKode=k.getKode();
            tampilkanData(sOldKode);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfDelete() {
        try {
            if(JOptionPane.showConfirmDialog(this, "Data Alat Bayar akan dihapus?", "Hapus Alat Bayar", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                return;
            dao.delete(ab);
            ((DefaultTableModel)jTable1.getModel()).removeRow(jTable1.getSelectedRow());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
            
//            if(getTableSource()==null)
//                return;
            
            if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {
                fn.keyTyped(evt);

           }

        }
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!fn.isListVisible()){
                                Component c = findNextFocus();
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(jTable1))
                        udfDelete();
                    break;

                }
                
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

        public Component findNextFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component nextFocus = policy.getComponentAfter(root, c);
                if (nextFocus == null) {
                    nextFocus = policy.getDefaultComponent(root);
                }
                return nextFocus;
            }
            return null;
        }

        public Component findPrevFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component prevFocus = policy.getComponentBefore(root, c);
                if (prevFocus == null) {
                    prevFocus = policy.getDefaultComponent(root);
                }
                return prevFocus;
            }
            return null;
        }
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
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        txtAlatBayar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        chkLangsungCair = new javax.swing.JCheckBox();

        setClosable(true);
        setTitle("Alat Bayar");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Alat Bayar", "Langsung Cair"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, 390, 115));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Kode :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 70, 20));

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        btnDelete.setText("Hapus");
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 60, 80, -1));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        btnNew.setText("Baru");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel1.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 60, 80, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 80, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 90, -1));

        txtAlatBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtAlatBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 280, 20));

        jLabel2.setText("Alat Bayar :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        chkLangsungCair.setText("Langsung Cair");
        jPanel1.add(chkLangsungCair, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 180, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 390, 100));

        setBounds(0, 0, 421, 266);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
    MenuAuth menuAuth;
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Master Alat Bayar", MainForm.sUserName);
        if(menuAuth!=null){
            btnNew.setEnabled(menuAuth.canInsert());
            btnSave.setEnabled(menuAuth.canUpdate() || menuAuth.canInsert());
            btnDelete.setEnabled(menuAuth.canDelete());
        }
        
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkLangsungCair;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtAlatBayar;
    private javax.swing.JTextField txtKode;
    // End of variables declaration//GEN-END:variables
}
