/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ExpedisiDao;
import com.ustasoft.pos.dao.jdbc.KotaDao;
import com.ustasoft.pos.domain.Expedisi;
import com.ustasoft.pos.domain.Kota;
import com.ustasoft.pos.domain.RelasiKategori;
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
import pos.master.list.FrmListExpedisi;

/**
 *
 * @author cak-ust
 */
public class MasterExpedisi extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private ExpedisiDao daoExpedisi;
    private KotaDao daoKota;
    GeneralFunction fn=new GeneralFunction();
    List<Kota> kota;
    List<AccCoaView> coa;
    List<RelasiKategori> suppKategory;
    MyKeyListener kListener=new MyKeyListener();
    /**
     * Creates new form MasterCoa
     */
    public MasterExpedisi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
    }
    
    public void setSourceForm(Object obj){
        srcForm=obj;
        
    }
    
    public void setConn(Connection con){
        this.conn=con;
        daoExpedisi=new ExpedisiDao(conn);
        udfInitForm();
    }

    private void udfClear() {
        txtID.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelp.setText("");
        txtHp.setText("");
        txtEmail.setText("");
    }

    private void udfInitForm(){
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        btnClose.addKeyListener(kListener);
        btnNew.addKeyListener(kListener);
        btnSave.addKeyListener(kListener);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                txtNama.requestFocusInWindow();
            }
        });
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
                JOptionPane.showConfirmDialog(this, "Nama Expedisi harus diisi!");
                txtNama.requestFocus();
                return;
            }
            Integer id=txtID.getText().trim().equalsIgnoreCase("") ? null: Integer.parseInt(txtID.getText());
            Integer idKota=cmbKota.getSelectedIndex()<0? null: kota.get(cmbKota.getSelectedIndex()).getId();
            Expedisi supp=new Expedisi();
            supp.setId(id);
            supp.setNama_expedisi(txtNama.getText());
            supp.setAlamat(txtAlamat.getText());
            supp.setId_kota(idKota);
            supp.setKontak(txtKontak.getText());
            supp.setTelp(txtTelp.getText());
            supp.setHp(txtHp.getText());
            supp.setFax(txtFax.getText());
            supp.setEmail(txtEmail.getText());
            supp.setTop(GeneralFunction.udfGetInt(txtTop.getText()));
            supp.setActive(chkActive.isSelected());
//            supp.setActive(chkActive.isSelected());
            
            daoExpedisi.simpan(supp);
            if(srcForm instanceof FrmListExpedisi){
                ((FrmListExpedisi)srcForm).tampilkanData(supp.getId(), "");
            }
            JOptionPane.showMessageDialog(this, "Simpan ekspedisi sukses!");
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
            if(e.getSource() instanceof JTextField||e.getSource() instanceof JFormattedTextField){
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
        jLabel5 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        cmbKota = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtKontak = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtTelp = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtHp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        jLabel5.setText("ID :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtID.setEditable(false);
        txtID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtID, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        chkActive.setSelected(true);
        chkActive.setText("Active");
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 120, -1));

        cmbKota.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 270, -1));

        jLabel6.setText("Nama Expedisi");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 100, 20));

        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAlamatActionPerformed(evt);
            }
        });
        jPanel2.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 320, 20));

        jLabel7.setText("Alamat :");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        jLabel8.setText("Kontak :");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 90, 20));

        txtKontak.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKontak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKontakActionPerformed(evt);
            }
        });
        jPanel2.add(txtKontak, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 320, 20));

        jLabel9.setText("Email");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 90, 20));

        txtEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        jPanel2.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 190, 20));

        txtTelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelpActionPerformed(evt);
            }
        });
        jPanel2.add(txtTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 135, 140, 20));

        jLabel10.setText("Telp :");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 135, 90, 20));

        jLabel11.setText("Hp :");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 135, 30, 20));

        txtHp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHpActionPerformed(evt);
            }
        });
        jPanel2.add(txtHp, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 135, 140, 20));

        jLabel13.setText("T.O.P :");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 100, 20));

        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTopActionPerformed(evt);
            }
        });
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 185, 40, 20));

        jLabel14.setText(" Hari");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 185, 50, 20));

        jLabel17.setText("Fax :");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 30, 20));

        txtFax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtFax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaxActionPerformed(evt);
            }
        });
        jPanel2.add(txtFax, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 160, 90, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 220));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Expedisi");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Clear");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        getContentPane().add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 270, 90, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, 90, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 270, 90, -1));

        setSize(new java.awt.Dimension(506, 341));
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

    private void txtKontakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKontakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKontakActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelpActionPerformed

    private void txtHpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHpActionPerformed

    private void txtTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTopActionPerformed

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
            java.util.logging.Logger.getLogger(MasterExpedisi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterExpedisi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterExpedisi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterExpedisi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterExpedisi dialog = new MasterExpedisi(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtHp;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTelp;
    private javax.swing.JTextField txtTop;
    // End of variables declaration//GEN-END:variables

    public void tampilkanByID(Integer id) {
        try {
            Expedisi view=daoExpedisi.cariByID(id);
            txtID.setText(view.getId().toString());
            txtNama.setText(view.getNama_expedisi());
            txtAlamat.setText(view.getAlamat());
            txtKontak.setText(view.getKontak());
            cmbKota.setSelectedItem(daoKota.cariByID(view.getId_kota()).getNamaKota());
            chkActive.setSelected(view.getActive());
            txtTelp.setText(view.getTelp());
            txtHp.setText(view.getHp());
            txtFax.setText(view.getFax());
            txtEmail.setText(view.getEmail());
            txtTop.setText(GeneralFunction.intFmt.format(view.getTop()));
            cmbKota.setSelectedItem(daoKota.cariByID(view.getId_kota()).getNamaKota());
        } catch (Exception ex) {
            Logger.getLogger(MasterExpedisi.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
