/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

import com.ustasoft.pos.dao.jdbc.AccCoaDao;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmInfoPerusahaan extends javax.swing.JInternalFrame {
    private Connection conn;
    ResultSet rs=null;
    boolean isNew=false;
    private GeneralFunction fn=new GeneralFunction();
    AccCoaDao coaDao;
    /**
     * Creates new form FrmInfoPerusahaan
     */
    public FrmInfoPerusahaan() {
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        btnSave.addKeyListener(kListener);
        btnKeluar.addKeyListener(kListener);
    }

    public void setConn(Connection c){
        this.conn=c;
        fn.setConn(conn);
        coaDao=new AccCoaDao(conn);
    }
    
    private void udfInitForm(){
        try {
            rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from m_setting ");
            
            if(rs.next()){
                txtNamaUsaha.setText(rs.getString("nama_toko")==null? "": rs.getString("nama_toko"));
                txtAlamat1.setText(rs.getString("alamat1")==null? "": rs.getString("alamat1"));
                txtAlamat2.setText(rs.getString("alamat2")==null? "": rs.getString("alamat2"));
                txtKota.setText(rs.getString("kota")==null? "": rs.getString("kota"));
                txtTelp1.setText(rs.getString("telp1")==null? "": rs.getString("telp1"));
                txtTelp2.setText(rs.getString("telp2")==null? "": rs.getString("telp2"));
                txtEmail.setText(rs.getString("email")==null? "": rs.getString("email"));
                txtAkunLabaDitahan.setText(rs.getString("akun_laba_ditahan")==null? "": rs.getString("akun_laba_ditahan") );
                lblAkunLabaDitahan.setText(coaDao.cariAccCoaByID(txtAkunLabaDitahan.getText()).getAcc_name());
            }else{
                rs.moveToInsertRow();
                isNew=true;
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmInfoPerusahaan.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void udfSave(){
        try {
            rs.updateString("nama_toko", txtNamaUsaha.getText());
            rs.updateString("alamat1", txtAlamat1.getText());
            rs.updateString("alamat2", txtAlamat2.getText());
            rs.updateString("kota", txtKota.getText());
            rs.updateString("telp1", txtTelp1.getText());
            rs.updateString("telp2", txtTelp2.getText());
            rs.updateString("email", txtEmail.getText());
            rs.updateString("akun_laba_ditahan", txtAkunLabaDitahan.getText());
            
            if(isNew)
                rs.insertRow();
            else
                rs.updateRow();
            rs.close();
            MainForm.sToko=txtNamaUsaha.getText();
            MainForm.sAlamat1=txtAlamat1.getText();
            MainForm.sAlamat2=txtAlamat2.getText();
            MainForm.sKota=txtKota.getText();
            MainForm.sTelp1=txtTelp1.getText();
            MainForm.sTelp2=txtTelp2.getText();
            MainForm.sEmail=txtEmail.getText();
            JOptionPane.showMessageDialog(this, "Update sukses!");
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(FrmInfoPerusahaan.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNamaUsaha = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtAlamat1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAlamat2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtKota = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTelp1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTelp2 = new javax.swing.JTextField();
        lblAkunLabaDitahan = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtAkunLabaDitahan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        panelHeader1 = new com.ustasoft.component.panelHeader();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Informasi Perusahaan");
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
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        jLabel1.setText("Nama Perusahaan : ");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 130, 20);
        jPanel1.add(txtNamaUsaha);
        txtNamaUsaha.setBounds(140, 10, 360, 22);

        jLabel2.setText("Alamat 1 :");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 35, 130, 20);
        jPanel1.add(txtAlamat1);
        txtAlamat1.setBounds(140, 35, 360, 22);

        jLabel3.setText("Alamat 2 :");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 60, 130, 20);
        jPanel1.add(txtAlamat2);
        txtAlamat2.setBounds(140, 60, 360, 22);

        jLabel4.setText("Kota : ");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 85, 130, 20);
        jPanel1.add(txtKota);
        txtKota.setBounds(140, 85, 360, 22);

        jLabel5.setText("Telp 1 : ");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(10, 110, 130, 20);
        jPanel1.add(txtTelp1);
        txtTelp1.setBounds(140, 110, 360, 22);

        jLabel6.setText("Telp 2 : ");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(10, 135, 130, 20);
        jPanel1.add(txtTelp2);
        txtTelp2.setBounds(140, 135, 360, 22);

        lblAkunLabaDitahan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblAkunLabaDitahan);
        lblAkunLabaDitahan.setBounds(205, 195, 295, 22);
        jPanel1.add(txtEmail);
        txtEmail.setBounds(140, 160, 360, 22);

        jLabel8.setText("Email : ");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(10, 160, 130, 20);

        txtAkunLabaDitahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAkunLabaDitahanKeyReleased(evt);
            }
        });
        jPanel1.add(txtAkunLabaDitahan);
        txtAkunLabaDitahan.setBounds(140, 195, 60, 22);

        jLabel9.setText("Akun Laba Ditahan :");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(10, 195, 130, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 70, 510, 235);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave);
        btnSave.setBounds(330, 310, 100, 25);

        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnKeluar.setText("Keluar");
        btnKeluar.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        getContentPane().add(btnKeluar);
        btnKeluar.setBounds(430, 310, 90, 25);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Setting");

        javax.swing.GroupLayout panelHeader1Layout = new javax.swing.GroupLayout(panelHeader1);
        panelHeader1.setLayout(panelHeader1Layout);
        panelHeader1Layout.setHorizontalGroup(
            panelHeader1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeader1Layout.createSequentialGroup()
                .addContainerGap(294, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelHeader1Layout.setVerticalGroup(
            panelHeader1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
        );

        getContentPane().add(panelHeader1);
        panelHeader1.setBounds(-5, 0, 535, 55);

        setBounds(0, 0, 543, 377);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        try {
            if(rs!=null) 
                rs.close();
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(FrmInfoPerusahaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtAkunLabaDitahanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAkunLabaDitahanKeyReleased
        fn.lookup(evt, new Object[]{lblAkunLabaDitahan}, "select acc_no, coalesce(acc_name,'') as acc_name "
                + "from acc_coa where "
                + "acc_no||coalesce(acc_name,'') ilike '%"+txtAkunLabaDitahan.getText()+"%' "
                + "and active order by acc_no, acc_name", txtAkunLabaDitahan.getWidth()+lblAkunLabaDitahan.getWidth()+20, 150);
    }//GEN-LAST:event_txtAkunLabaDitahanKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAkunLabaDitahan;
    private com.ustasoft.component.panelHeader panelHeader1;
    private javax.swing.JTextField txtAkunLabaDitahan;
    private javax.swing.JTextField txtAlamat1;
    private javax.swing.JTextField txtAlamat2;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtKota;
    private javax.swing.JTextField txtNamaUsaha;
    private javax.swing.JTextField txtTelp1;
    private javax.swing.JTextField txtTelp2;
    // End of variables declaration//GEN-END:variables

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
                case KeyEvent.VK_ESCAPE:{
                    dispose();
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
}