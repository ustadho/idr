/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.ItemKategoriDao;
import com.ustasoft.pos.domain.ItemKategori;
import com.ustasoft.pos.domain.Relasi;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import pos.master.list.FrmListItemKategori;

/**
 *
 * @author cak-ust
 */
public class MasterItemKategori extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private ItemKategoriDao daoItemKategori;
    private AccCoaDao daoAcc;
    private String sOldKode="";
    GeneralFunction fn=new GeneralFunction();
    List<ItemKategori> itemKategori;
    List<AccCoaView> akunPersediaan;
    List<AccCoaView> akunPembelian;
    List<AccCoaView> akunReturPembelian;
    List<AccCoaView> akunPenjualan;
    List<AccCoaView> akunReturPenjualan;
    List<AccCoaView> akunHpp;
    List<Relasi> supplier;
    /**
     * Creates new form MasterCoa
     */
    public MasterItemKategori(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fn.addKeyListenerInContainer(jPanel2, new MyKeyListener(), txtFocusListener);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                txtNama.requestFocusInWindow();
            }
        });
    }
    
    public void setSourceForm(Object obj){
        srcForm=obj;
        
    }
    
    public void setConn(Connection con){
        this.conn=con;
        udfInitForm();
    }

    private void udfClear() {
        lblID.setText("");
        txtNama.setText("");
        txtKeterangan.setText("");
    }

    private void udfInitForm(){
        try {
            daoAcc=new AccCoaDao(conn);
            daoItemKategori=new ItemKategoriDao(conn);
            
            cmbAkunPersediaan.removeAllItems();
            akunPersediaan=daoAcc.cariCoaByAccType("03");
            for(AccCoaView lcoa : akunPersediaan){
                cmbAkunPersediaan.addItem(lcoa.getAcc_name());
            }
            
            cmbAkunPembelian.removeAllItems();
            akunPembelian=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : akunPembelian){
                cmbAkunPembelian.addItem(lcoa.getAcc_name());
            }
            
            cmbAkunReturPembelian.removeAllItems();
            akunReturPembelian=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : akunReturPembelian){
                cmbAkunReturPembelian.addItem(lcoa.getAcc_name());
            }
            
            cmbAkunPenjualan.removeAllItems();
            akunPenjualan=daoAcc.cariSemuaCoa("11");
            for(AccCoaView lcoa : akunPenjualan){
                cmbAkunPenjualan.addItem(lcoa.getAcc_name());
            }
            cmbAkunReturPenjualan.removeAllItems();
            akunReturPenjualan=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : akunReturPenjualan){
                cmbAkunReturPenjualan.addItem(lcoa.getAcc_name());
            }
            
            cmbAkunHpp.removeAllItems();
            akunHpp=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : akunHpp){
                cmbAkunHpp.addItem(lcoa.getAcc_name());
            }
            
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void udfSave() {
        try {
            if(txtNama.getText().length()==0){
                JOptionPane.showConfirmDialog(this, "Nama Item harus diisi!");
                txtNama.requestFocus();
                return;
            }
            conn.setAutoCommit(false);
            Integer id=lblID.getText().trim().equalsIgnoreCase("") ? null: Integer.parseInt(lblID.getText());
            ItemKategori item=new ItemKategori();
            item.setId(id);
            item.setNamaKategori(txtNama.getText());
            item.setKetKategori(txtKeterangan.getText());
            item.setAcc_persediaan(akunPersediaan.get(cmbAkunPersediaan.getSelectedIndex()).getAcc_no());
            item.setAcc_pembelian(akunPembelian.get(cmbAkunPembelian.getSelectedIndex()).getAcc_no());
            item.setAcc_retur_pembelian(akunReturPembelian.get(cmbAkunReturPembelian.getSelectedIndex()).getAcc_no());
            item.setAcc_penjualan(akunPenjualan.get(cmbAkunPenjualan.getSelectedIndex()).getAcc_no());
            item.setAcc_retur_penjualan(akunReturPenjualan.get(cmbAkunReturPenjualan.getSelectedIndex()).getAcc_no());
            item.setAcc_hpp(akunHpp.get(cmbAkunHpp.getSelectedIndex()).getAcc_no());
            daoItemKategori.simpan(item);
            conn.setAutoCommit(true);
            if(srcForm instanceof FrmListItemKategori){
                ((FrmListItemKategori)srcForm).tampilkanData(item.getId());
            }
            JOptionPane.showMessageDialog(this, "Simpan Item sukses!");
            this.dispose();
        } catch (Exception ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(MasterItemKategori.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } 
        
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
                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    dispose();
                    break;
                }
                
                case KeyEvent.VK_DELETE:{
                    
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

        jPanel2 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lblID = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cmbAkunPersediaan = new javax.swing.JComboBox();
        cmbAkunPembelian = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        cmbAkunReturPembelian = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cmbAkunPenjualan = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cmbAkunReturPenjualan = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbAkunHpp = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Kategori Stiok");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 320, 20));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Bersihkan (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 285, 150, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Keluar (Esc)");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 285, 120, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 285, 115, -1));

        jLabel5.setText("ID :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        lblID.setEditable(false);
        lblID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblID, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        jLabel6.setText("Nama Kategeori ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 110, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKeteranganActionPerformed(evt);
            }
        });
        jPanel2.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 320, 20));

        jLabel18.setText("Keterangan");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Akun-akun", 0, 0, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("Persediaan");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

        cmbAkunPersediaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunPersediaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 330, -1));

        cmbAkunPembelian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunPembelian, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 55, 330, -1));

        jLabel13.setText("Pembelian");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 90, 20));

        cmbAkunReturPembelian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunReturPembelian, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 330, -1));

        jLabel14.setText("Retur Pembelian");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 105, 20));

        cmbAkunPenjualan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 105, 330, -1));

        jLabel16.setText("Penjualan");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 105, 20));

        cmbAkunReturPenjualan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunReturPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 330, -1));

        jLabel19.setText("Retur Penjualan");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 100, 20));

        cmbAkunHpp.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkunHpp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 155, 330, -1));

        jLabel20.setText("Harga Pokok");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 105, 20));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 450, 190));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 330));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Kategori Stok");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        setSize(new java.awt.Dimension(501, 414));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfClear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        
    }//GEN-LAST:event_formWindowOpened

    private void txtKeteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKeteranganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKeteranganActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MasterItemKategori.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterItemKategori.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterItemKategori.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterItemKategori.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterItemKategori dialog = new MasterItemKategori(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbAkunHpp;
    private javax.swing.JComboBox cmbAkunPembelian;
    private javax.swing.JComboBox cmbAkunPenjualan;
    private javax.swing.JComboBox cmbAkunPersediaan;
    private javax.swing.JComboBox cmbAkunReturPembelian;
    private javax.swing.JComboBox cmbAkunReturPenjualan;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField lblID;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables

    public void tampilkanItem(Integer id) {
        try {
            ItemKategori view=daoItemKategori.cariKategoriByID(id);
            lblID.setText(view.getId().toString());
            txtNama.setText(view.getNamaKategori());
            txtKeterangan.setText(view.getKetKategori());
            if(view.getAcc_persediaan()!=null){
                cmbAkunPersediaan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_persediaan()).getAcc_name());
            }
            if(view.getAcc_pembelian()!=null){
                cmbAkunPembelian.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_pembelian()).getAcc_name());
            }
            if(view.getAcc_retur_pembelian()!=null){
                cmbAkunReturPembelian.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_retur_pembelian()).getAcc_name());
            }
            if(view.getAcc_penjualan()!=null){
                cmbAkunPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_penjualan()).getAcc_name());
            }
            if(view.getAcc_retur_penjualan()!=null){
                cmbAkunReturPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_retur_penjualan()).getAcc_name());
            }
            if(view.getAcc_hpp()!=null){
                cmbAkunHpp.setSelectedItem(daoAcc.cariAccCoaByID(view.getAcc_hpp()).getAcc_name());
            }
            
        } catch (Exception ex) {
            Logger.getLogger(MasterItemKategori.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
