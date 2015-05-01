/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.domain.Kota;
import com.ustasoft.pos.domain.Sales;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.KotaDao;
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
import pos.master.list.FrmListSales;

/**
 *
 * @author cak-ust
 */
public class MasterSales extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private SalesDao daoSales;
    private KotaDao daoKota;
    private AccCoaDao daoAcc;
    private String sOldKode="";
    GeneralFunction fn=new GeneralFunction();
    List<Kota> kota;
    /**
     * Creates new form MasterCoa
     */
    public MasterSales(java.awt.Frame parent, boolean modal) {
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
        daoSales=new SalesDao(conn);
        udfInitForm();
    }

    private void udfClear() {
        txtIDSupp.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelp.setText("");
        txtHp.setText("");
        txtFax.setText("");
        txtEmail.setText("");
        txtNoNpwp.setText("");
    }

    private void udfInitForm(){
        try {
            cmbKota.removeAllItems();
            daoKota=new KotaDao(conn);
            kota=daoKota.cariSemuaData();
            
            for(Kota ac : kota){
                cmbKota.addItem(ac.getNamaKota());
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void udfSave() {
        try {
            if(txtNama.getText().length()==0){
                JOptionPane.showConfirmDialog(this, "Kode akun harus diisi!");
                txtNama.requestFocus();
                return;
            }
            Integer id=txtIDSupp.getText().trim().equalsIgnoreCase("") ? null: Integer.parseInt(txtIDSupp.getText());
            Integer idKota=cmbKota.getSelectedIndex()<0? null: kota.get(cmbKota.getSelectedIndex()).getId();
            Sales sales=new Sales();
            sales.setId_sales(id);
            sales.setNama_sales(txtNama.getText());
            sales.setAlamat(txtAlamat.getText());
            sales.setId_kota(idKota);
            sales.setTelp(txtTelp.getText());
            sales.setFax(txtFax.getText());
            sales.setHp(txtHp.getText());
            sales.setEmail(txtEmail.getText());
            sales.setNo_npwp(txtNoNpwp.getText());
            sales.setActive(chkActive.isSelected());
            
            daoSales.simpan(sales);
            if(srcForm instanceof FrmListSales){
                ((FrmListSales)srcForm).tampilkanData(sales.getId_sales(), "");
            }
            JOptionPane.showMessageDialog(this, "Simpan data Salesman sukses!");
            this.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
        jLabel4 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtIDSupp = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        cmbKota = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtTelp = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtHp = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtNoNpwp = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Salesman");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Kota :");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 320, 20));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Clear (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 100, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 190, 90, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 90, -1));

        jLabel5.setText("ID :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtIDSupp.setEditable(false);
        txtIDSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtIDSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        chkActive.setSelected(true);
        chkActive.setText("Active");
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 120, -1));

        cmbKota.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 270, -1));

        jLabel6.setText("Nama Sales");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAlamatActionPerformed(evt);
            }
        });
        jPanel2.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 320, 20));

        jLabel7.setText("Alamat :");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        jLabel9.setText("Email");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 135, 90, 20));

        txtEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        jPanel2.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 135, 180, 20));

        txtTelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelpActionPerformed(evt);
            }
        });
        jPanel2.add(txtTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 140, 20));

        jLabel10.setText("Telp :");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 90, 20));

        jLabel11.setText("Hp :");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 30, 20));

        txtHp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHpActionPerformed(evt);
            }
        });
        jPanel2.add(txtHp, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 110, 140, 20));

        jLabel16.setText("No. NPWP");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 90, 20));

        txtNoNpwp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoNpwp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoNpwpActionPerformed(evt);
            }
        });
        jPanel2.add(txtNoNpwp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 320, 20));

        jLabel17.setText("Fax :");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 135, 30, 20));

        txtFax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtFax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaxActionPerformed(evt);
            }
        });
        jPanel2.add(txtFax, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 135, 100, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 440, 230));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Salesman");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        setSize(new java.awt.Dimension(468, 309));
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

    private void txtAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAlamatActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        
    }//GEN-LAST:event_formWindowOpened

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelpActionPerformed

    private void txtHpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHpActionPerformed

    private void txtNoNpwpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoNpwpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoNpwpActionPerformed

    private void txtFaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFaxActionPerformed

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
            java.util.logging.Logger.getLogger(MasterSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterSales dialog = new MasterSales(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JComboBox cmbKota;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtHp;
    private javax.swing.JTextField txtIDSupp;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoNpwp;
    private javax.swing.JTextField txtTelp;
    // End of variables declaration//GEN-END:variables

    public void tampilkanSales(Integer idSupp) {
        try {
            Sales view=daoSales.cariByID(idSupp);
            txtIDSupp.setText(view.getId_sales().toString());
            txtNama.setText(view.getNama_sales());
            txtAlamat.setText(view.getAlamat());
            cmbKota.setSelectedItem(daoKota.cariByID(view.getId_kota()).getNamaKota());
            chkActive.setSelected(view.getActive());
            txtTelp.setText(view.getTelp());
            txtFax.setText(view.getFax());
            txtHp.setText(view.getHp());
            txtEmail.setText(view.getEmail());
            txtNoNpwp.setText(view.getNo_npwp());
            cmbKota.setSelectedItem(daoKota.cariByID(view.getId_kota()).getNamaKota());
        } catch (Exception ex) {
            Logger.getLogger(MasterSales.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
