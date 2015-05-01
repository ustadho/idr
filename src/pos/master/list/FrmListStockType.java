/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.list;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.dao.jdbc.TrxTypeDao;
import com.ustasoft.pos.domain.TrxType;
import com.ustasoft.pos.domain.view.AccCoaView;
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
import java.util.ArrayList;
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
public class FrmListStockType extends javax.swing.JInternalFrame {
    private Connection conn;
    TrxTypeDao dao;
    TrxType tipeStok=new TrxType();
    private GeneralFunction fn=new GeneralFunction();
    AccCoaDao coaDao;
    List<AccCoaView> listCoa=new ArrayList<AccCoaView>();
    private boolean isNew;
    /**
     * Creates new form ItemKategori
     */
    public FrmListStockType() {
        initComponents();
    }
    
    public void initForm(Connection con){
        this.conn=con;
        dao=new TrxTypeDao(conn);
        coaDao=new AccCoaDao(conn);
        for(int i=0; i<jTable1.getColumnCount(); i++){
            jTable1.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        try {
            cmbAkun.removeAllItems();
            for(AccCoaView c: coaDao.cariSemuaCoa("")){
                cmbAkun.addItem(c.getAcc_no()+" - "+c.getAcc_name());
                listCoa.add(c);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(FrmListStockType.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=jTable1.getSelectedRow();
                if(iRow<0 || dao==null)
                    return;
                try {
                    tipeStok=dao.cariByKode(jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("Kode")).toString());
                    if(tipeStok!=null){
                        txtKode.setText(tipeStok.getKode());
                        txtKeterangan.setText(tipeStok.getKeterangan());
                        cmbInOut.setSelectedItem(tipeStok.getInOut());
                        jCheckBox1.setSelected(tipeStok.isPlanned());
                        cmbAkun.setSelectedItem(coaDao.cariAccCoaByID(tipeStok.getAkun()).getAcc_name());
                        btnSave.setEnabled(iRow>=0 && menuAuth!=null && (menuAuth.canUpdate() || menuAuth.canInsert()));
                        btnDelete.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canDelete());
                        
                        jLabel1.setVisible(!tipeStok.isPlanned());
                        cmbAkun.setVisible(!tipeStok.isPlanned());
                    }else{
                        udfNew();
                    }
                    
                } catch (Exception ex) {
                    Logger.getLogger(FrmListStockType.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        tampilkanData("");
        
        MyKeyListener kListener=new MyKeyListener();
        //txtKet.addKeyListener(kListener);
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
            List<TrxType> list=dao.cariSemuaData();
            int i=0;
            for(TrxType item : list){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    item.getKode(),
                    item.getKeterangan(),
                    
                });
                if(kode.equalsIgnoreCase(item.getKode())){
                    i=jTable1.getRowCount()-1;
                }
            }
            if(jTable1.getRowCount()>0){
                jTable1.setRowSelectionInterval(i, i);
                jTable1.setModel(fn.autoResizeColWidth(jTable1, ((DefaultTableModel)jTable1.getModel())).getModel());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmListStockType.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void udfNew() {
        txtKode.setText("");
        txtKode.setText("");
        cmbInOut.setSelectedItem("IN");
        this.isNew=true;
        
    }

    private void udfSave() {
        btnSave.requestFocusInWindow();
        if(txtKode.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Kode harus diisi!");
            txtKode.requestFocusInWindow();
            return;
        }
        try {
            TrxType t=new TrxType();
            //Integer id=lblID.getText().equalsIgnoreCase("")? null: Integer.parseInt(lblID.getText());
            t.setKode(txtKode.getText());
            t.setKeterangan(txtKode.getText());
            t.setInOut(cmbInOut.getSelectedItem().toString());
            t.setAkun(listCoa.get(cmbAkun.getSelectedIndex()).getAcc_no());
            dao.simpan(t, isNew);
            tampilkanData(txtKode.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfDelete() {
        try {
            if(JOptionPane.showConfirmDialog(this, "Kategori akan dihapus", "Hapus kateogori", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                return;
            dao.delete(tipeStok);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        txtKeterangan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbAkun = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        cmbInOut = new javax.swing.JComboBox();

        setClosable(true);
        setTitle("Tipe Transaksi Stok");
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
                "Kode", "Keterangan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 177, 536, 185));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Akun : ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        jLabel3.setText("Keterangan : ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 100, 20));

        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 70, 20));

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        btnDelete.setText("Hapus");
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 120, 85, -1));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        btnNew.setText("Baru");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel1.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 120, 80, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Keluar");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 120, 80, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 120, 85, -1));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 65, 405, 20));

        jLabel5.setText("Kode :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 20));

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IN", "OUT" }));
        jPanel1.add(cmbAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 405, -1));

        jLabel6.setText("IN/ OUT : ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 85, 20));

        jCheckBox1.setText("Terencana");
        jPanel1.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 35, 180, -1));

        cmbInOut.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IN", "OUT" }));
        jPanel1.add(cmbInOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 95, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 536, 165));

        setBounds(0, 0, 562, 400);
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
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Master Satuan", MainForm.sUserName);
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
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JComboBox cmbInOut;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtKode;
    // End of variables declaration//GEN-END:variables
}
