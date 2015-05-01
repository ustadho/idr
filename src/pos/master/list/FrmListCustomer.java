/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.list;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.dao.jdbc.KotaDao;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.domain.Relasi;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pos.MainForm;
import pos.master.form.MasterCustomer;

/**
 *
 * @author cak-ust
 */
public class FrmListCustomer extends javax.swing.JInternalFrame {
    private Connection conn;
    RelasiDao relasiDao;
    KotaDao kotaDao;
    private GeneralFunction fn=new GeneralFunction();
    private final int CUSTOMER=0;
    /**
     * Creates new form ItemKategori
     */
    public FrmListCustomer() {
        initComponents();
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=jTable1.getSelectedRow();
                if(iRow>=0){
                    btnNew.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canInsert());
                    btnEdit.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canUpdate());
                    btnDelete.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canDelete());
                }
            }
        });
    }

    public void initForm(Connection con){
        this.conn=con;
        for(int i=0; i<jTable1.getColumnCount(); i++){
            jTable1.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        MyKeyListener kListener=new MyKeyListener();
        jTable1.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, null);
        txtCari.addKeyListener(kListener);
        btnRefresh.addKeyListener(kListener);
        
        relasiDao=new RelasiDao(conn);
        kotaDao=new KotaDao(conn);
        tampilkanData(0, "");
        
    }
    
    private void udfNew() {
        MasterCustomer master=new MasterCustomer(JOptionPane.getFrameForComponent(this), true);
        master.setConn(conn);
        master.setSourceForm(this);
        master.setVisible(true);
    }

    private void udfEdit() {
        int iRow=jTable1.getSelectedRow();
        if(iRow<0)
            return;
        Integer id=Integer.valueOf(jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("ID")).toString());
        try {
            MasterCustomer master=new MasterCustomer(JOptionPane.getFrameForComponent(this), true);
            master.setConn(conn);
            master.setSourceForm(this);
            master.tampilkanCustomer(id);
            master.setVisible(true);
        } catch (Exception ex) {
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
                case KeyEvent.VK_F3:{
                    udfEdit();
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
    
    public void tampilkanData(Integer id, String sCari){
        try {
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            List<Relasi> list=relasiDao.filterSemuaData(CUSTOMER, sCari);
            int i=0;
            for(Relasi rel : list){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rel.getIdRelasi(),
                    rel.getNamaRelasi(),
                    rel.getKontak(), 
                    rel.getTelp(), 
                    rel.getAlamat(),
                    kotaDao.cariByID(rel.getIdKota()).getNamaKota()
                });
                if(id==rel.getIdRelasi())
                    i=jTable1.getRowCount()-1;
            }
            if(jTable1.getRowCount()>0){
                jTable1.setRowSelectionInterval(i, i);
                jTable1.setModel(fn.autoResizeColWidth(jTable1, ((DefaultTableModel)jTable1.getModel())).getModel());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmListCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void udfDelete(){
        int iRow=jTable1.getSelectedRow();
        if(iRow<0)
            return;
        try {
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus akun '"+jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("Nama Kota")) +"'", "Hapus Kota", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                return;
            
            relasiDao.delete(Integer.parseInt(jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("ID")).toString()));
            ((DefaultTableModel)jTable1.getModel()).removeRow(jTable1.getSelectedRow());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Customer");
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

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama Customer", "Kotak", "Telepon", "Alamat", "Kota"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        btnDelete.setText("Hapus (Del)");
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 5, 110, -1));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        btnNew.setText("Baru (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel1.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 5, 100, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 5, 90, -1));

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_edit.png"))); // NOI18N
        btnEdit.setText("Ubah (F3)");
        btnEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel1.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 5, 110, -1));

        jLabel2.setText("Pencarian :");

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        setBounds(0, 0, 640, 383);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtCari.setText("");
        tampilkanData(0, "");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            tampilkanData(0, txtCari.getText());
    }//GEN-LAST:event_txtCariKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount()==2 && jTable1.getSelectedRow()>=0){
            udfEdit();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private MenuAuth menuAuth;
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Master Customer", MainForm.sUserName);
        if(menuAuth!=null){
            btnNew.setEnabled(menuAuth.canInsert());
            btnEdit.setEnabled(menuAuth.canUpdate());
            btnDelete.setEnabled(menuAuth.canDelete());
        }
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
