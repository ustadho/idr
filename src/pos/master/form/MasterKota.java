/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AreaDao;
import com.ustasoft.pos.domain.Kota;
import com.ustasoft.pos.dao.jdbc.KotaDao;
import com.ustasoft.pos.domain.Area;
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
import pos.master.list.FrmListKota;

/**
 *
 * @author cak-ust
 */
public class MasterKota extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private KotaDao dao;
    private AreaDao areaDao;
    GeneralFunction fn=new GeneralFunction();
    List<Area> lstArea=new ArrayList<Area>();
    
    /**
     * Creates new form MasterCoa
     */
    public MasterKota(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fn.addKeyListenerInContainer(jPanel2, new MyKeyListener(), txtFocusListener);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                txtNamaKota.requestFocusInWindow();
            }
        });
    }
    
    public void setSourceForm(Object obj){
        srcForm=obj;
        
    }
    
    public void setConn(Connection con){
        this.conn=con;
        dao=new KotaDao(conn);
        areaDao=new AreaDao(conn);
        try {
            lstArea=areaDao.cariSemuaData();
            cmbArea.removeAllItems();
            for(Area a: lstArea){
                cmbArea.addItem(a.getNama());
            }
        } catch (Exception ex) {
            Logger.getLogger(MasterKota.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void udfClear() {
        txtID.setText("");
        txtProvinsi.setText("");
        txtNegara.setText("");
        txtProvinsi.setText("");
    }

    private void udfSave() {
        try {
            if(txtNamaKota.getText().length()==0){
                JOptionPane.showConfirmDialog(this, "Nama Kota harus diisi!");
                txtNamaKota.requestFocus();
                return;
            }
            Kota kota=new Kota();
            kota.setId(txtID.getText().equalsIgnoreCase("")? null : Integer.valueOf(txtID.getText()));
            kota.setNamaKota(txtNamaKota.getText());
            kota.setProvinsi(txtProvinsi.getText());
            kota.setNegara(txtNegara.getText());
            kota.setIdArea(lstArea.get(cmbArea.getSelectedIndex()).getId());
            dao.simpan(kota);
            
            if(srcForm instanceof FrmListKota){
                ((FrmListKota)srcForm).tampilkanData(txtID.getText().equalsIgnoreCase("")? 0: Integer.valueOf(txtID.getText()), 
                        "");
            }
            JOptionPane.showMessageDialog(this, "Simpan Kota sukses!");
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
        txtProvinsi = new javax.swing.JTextField();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtNegara = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNamaKota = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cmbArea = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Kota");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Provinsi");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtProvinsi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtProvinsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProvinsiActionPerformed(evt);
            }
        });
        jPanel2.add(txtProvinsi, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 320, 20));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Clear (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 115, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Close (Esc)");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 150, 125, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Save (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 150, 120, -1));

        jLabel5.setText("ID");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtID.setEditable(false);
        txtID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtID, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        jLabel6.setText("Nama Kota");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        txtNegara.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNegara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNegaraActionPerformed(evt);
            }
        });
        jPanel2.add(txtNegara, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 320, 20));

        jLabel7.setText("Negara");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtNamaKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaKota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaKotaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNamaKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 320, 20));

        jLabel8.setText("Area ");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 100, 20));

        cmbArea.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 220, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 440, 190));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Kota");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        setSize(new java.awt.Dimension(470, 277));
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

    private void txtProvinsiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProvinsiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProvinsiActionPerformed

    private void txtNegaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNegaraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNegaraActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened

    private void txtNamaKotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaKotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaKotaActionPerformed

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
            java.util.logging.Logger.getLogger(MasterKota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterKota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterKota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterKota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterKota dialog = new MasterKota(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cmbArea;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNamaKota;
    private javax.swing.JTextField txtNegara;
    private javax.swing.JTextField txtProvinsi;
    // End of variables declaration//GEN-END:variables

    public void tampilkanKota(Kota kota) {
        try {
            if(kota!=null){
                txtID.setText(String.valueOf(kota.getId()));
                txtNamaKota.setText(kota.getNamaKota());
                txtProvinsi.setText(kota.getProvinsi());
                txtNegara.setText(kota.getNegara());
                cmbArea.setSelectedItem(areaDao.cariByID(kota.getIdArea()).getNama());
            }
        } catch (Exception ex) {
            Logger.getLogger(MasterKota.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
