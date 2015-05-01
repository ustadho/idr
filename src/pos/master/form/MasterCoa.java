/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.domain.AccCoa;
import com.ustasoft.pos.domain.AccGroup;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.AccGroupDao;
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
import javax.swing.table.DefaultTableModel;
import pos.master.list.FrmListCoa;

/**
 *
 * @author cak-ust
 */
public class MasterCoa extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private List<AccGroup> lGroup;
    private List<AccCoaView> listCoa;
    private AccCoaDao dao;
    private String sOldKode="";
    GeneralFunction fn=new GeneralFunction();
    /**
     * Creates new form MasterCoa
     */
    public MasterCoa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fn.addKeyListenerInContainer(jPanel2, new MyKeyListener(), txtFocusListener);
    }
    
    public void setSourceForm(Object obj, String kodeAkunAwal){
        srcForm=obj;
        sOldKode=kodeAkunAwal;
    }
    
    public void setConn(Connection con){
        this.conn=con;
        dao=new AccCoaDao(conn);
    }

    private void udfClear() {
        txtKodeAkun.setText("");
        txtNamaAkun.setText("");
        txtCatatan.setText("");
        txtNamaAkun.setText("");
    }

    private void udfSave() {
        try {
            if(txtKodeAkun.getText().length()==0){
                JOptionPane.showMessageDialog(this, "Kode akun harus diisi!");
                txtKodeAkun.requestFocus();
                return;
            }
            if(txtNamaAkun.getText().length()==0){
                JOptionPane.showMessageDialog(this, "Kode akun harus diisi!");
                txtNamaAkun.requestFocus();
                return;
            }
            if(jCheckBox1.isVisible() && cmbSubAccOf.getSelectedIndex() <0){
                JOptionPane.showMessageDialog(this, "Silahkan pilih sub akun terlebih dulu!");
                cmbSubAccOf.requestFocus();
                return;
            }
            AccCoa acc=new AccCoa();
            acc.setAcc_no(txtKodeAkun.getText());
            acc.setAcc_name(txtNamaAkun.getText());
            acc.setAcc_type(lGroup.get(cmbTipe.getSelectedIndex()).getType_id());
            acc.setCatatan(txtCatatan.getText());
            acc.setActive(chkActive.isSelected());
            acc.setSaldo_awal(new Double(0));
            acc.setCurr_id("");
            acc.setAcc_group("");
            acc.setSub_acc_of(cmbSubAccOf.isVisible()? listCoa.get(cmbSubAccOf.getSelectedIndex()).getAcc_no(): null);
            dao.simpan(acc, sOldKode);
            if(srcForm instanceof FrmListCoa){
                ((FrmListCoa)srcForm).tampilkanData(txtKodeAkun.getText(), "");
            }
            JOptionPane.showMessageDialog(this, "Simpan COA sukses!");
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
        txtNamaAkun = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtKodeAkun = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        cmbTipe = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmbSubAccOf = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master COA");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Tipe");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtNamaAkun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaAkun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaAkunActionPerformed(evt);
            }
        });
        jPanel2.add(txtNamaAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 35, 310, 20));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Clear (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 165, 100, 30));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Close (Esc)");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 165, 110, 30));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Save (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 165, 100, 30));

        jLabel5.setText("Kode Akun");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtKodeAkun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtKodeAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 110, 20));

        chkActive.setSelected(true);
        chkActive.setText("Active");
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 120, -1));

        cmbTipe.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, 190, -1));

        jLabel6.setText("Keterangan Akun :");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 130, 20));

        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCatatanActionPerformed(evt);
            }
        });
        jPanel2.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 125, 310, 20));

        jLabel7.setText("Catatan:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 125, 90, 20));

        cmbSubAccOf.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbSubAccOf, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 85, 300, -1));

        jCheckBox1.setText("Sub Akun Dari");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jPanel2.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 85, 110, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 440, 205));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Chart of Account");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        setSize(new java.awt.Dimension(470, 292));
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

    private void txtNamaAkunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaAkunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaAkunActionPerformed

    private void txtCatatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCatatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCatatanActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            cmbTipe.removeAllItems();
            lGroup=new AccGroupDao(conn).cariSemuaAccGroup();
            for(AccGroup ac : lGroup){
                cmbTipe.addItem(ac.getType_name());
                
            }
            cmbSubAccOf.removeAllItems();
            listCoa=new AccCoaDao(conn).cariSemuaCoa("");
            for(AccCoaView v:listCoa){
                cmbSubAccOf.addItem(v.getAcc_name());
            }
            if(!sOldKode.equalsIgnoreCase("")){
                tampilkanCoa(sOldKode);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        cmbSubAccOf.setVisible(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

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
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterCoa dialog = new MasterCoa(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cmbSubAccOf;
    private javax.swing.JComboBox cmbTipe;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtKodeAkun;
    private javax.swing.JTextField txtNamaAkun;
    // End of variables declaration//GEN-END:variables

    public void tampilkanCoa(String sAccNo) {
        try {
            sOldKode=sAccNo;
            AccCoaView view=dao.cariAccCoaByID(sAccNo);
            txtKodeAkun.setText(view.getAcc_no());
            txtNamaAkun.setText(view.getAcc_name());
            chkActive.setSelected(view.getActive());
            txtCatatan.setText(view.getCatatan());
            cmbTipe.setSelectedItem(view.getType_name());
            if(view.getSub_acc_of()!=null && view.getSub_acc_of().length() > 0){
                jCheckBox1.setSelected(true);
                cmbSubAccOf.setSelectedItem(dao.cariAccCoaByID(view.getSub_acc_of()).getAcc_name());
            }else{
                jCheckBox1.setSelected(false);
                cmbSubAccOf.setSelectedIndex(-1);
            }
        } catch (Exception ex) {
            Logger.getLogger(MasterCoa.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
