/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPettyCash.java
 *
 * Created on 13 Mei 11, 19:34:45
 */
package com.ustasoft.petty_cash;

import com.ustasoft.component.GeneralFunction;
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
import java.text.SimpleDateFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import pos.MainForm;
/**
 *
 * @author cak-ust
 */
public class FrmPettyCash extends javax.swing.JInternalFrame {
    private Connection conn;
    private String sKM="M";
    private GeneralFunction fn=new GeneralFunction();
    private Component aThis;
    private boolean isNew=true;
    private Object srcForm;
    
    /** Creates new form FrmPettyCash */
    public FrmPettyCash() {
        initComponents();
        jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void setKeluarMasuk(String s){
        this.sKM =s;
    }
    
    private void udfInitForm(){
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        btnSimpan.addKeyListener(kListener);
        btnBatal.addKeyListener(kListener);
        jLabel7.setText(getTitle());
        txtNoTrx.setEnabled(!isNew);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        txtTerimaDari = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNoBukti = new javax.swing.JTextField();
        lblTerimaKe = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Petty Cash");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Tanggal :");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 80, 20));

        jLabel2.setText("No. Trans :");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setName("txtNoTrx"); // NOI18N
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 120, 22));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 170, -1));

        jLabel3.setText("Keterangan :");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 80, 20));

        txtTerimaDari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTerimaDari.setName("txtTerimaDari"); // NOI18N
        jPanel1.add(txtTerimaDari, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 380, 22));

        jLabel4.setText("No. Bukti :");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 80, 20));

        txtNoBukti.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoBukti.setName("txtNoBukti"); // NOI18N
        jPanel1.add(txtNoBukti, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 35, 220, 22));

        lblTerimaKe.setText("Terima Dari :");
        lblTerimaKe.setName("lblTerimaKe"); // NOI18N
        jPanel1.add(lblTerimaKe, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 80, 20));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtKeterangan.setColumns(20);
        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtKeterangan.setRows(5);
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jScrollPane1.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 85, 380, 60));

        jLabel6.setText("Jumlah :");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 80, 20));

        txtJumlah.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtJumlah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtJumlah.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtJumlah.setName("txtJumlah"); // NOI18N
        jPanel1.add(txtJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 160, 22));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 480, 180));

        btnSimpan.setText("Simpan");
        btnSimpan.setName("btnSimpan"); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        getContentPane().add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 225, 90, -1));

        btnBatal.setText("Batal");
        btnBatal.setName("btnBatal"); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        getContentPane().add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 225, 70, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 153));
        jLabel7.setText("Kas Masuk");
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 480, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSimpanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblTerimaKe;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtNoBukti;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtTerimaDari;
    // End of variables declaration//GEN-END:variables

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(txtTerimaDari.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Masukkan "+lblTerimaKe.getText()+" terlebih dulu!");
            if(!txtTerimaDari.isFocusOwner()) txtTerimaDari.requestFocus();
            return false;
        }
        if(txtKeterangan.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Masukkan Keterangan terlebih dulu!");
            if(!txtKeterangan.isFocusOwner()) txtKeterangan.requestFocus();
            return false;
        }
        if(fn.udfGetDouble(txtJumlah.getText())==0){
            JOptionPane.showMessageDialog(this, "Masukkan jumlah rupiah terlebih dulu!");
            if(!txtJumlah.isFocusOwner()) txtJumlah.requestFocus();
            return false;
        }
        return b;
    }
    
    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        try{
            if(btnSimpan.getText().equalsIgnoreCase("Simpan")) {
                ResultSet rs = conn.createStatement().executeQuery("select fn_pc_get_no_pettycash('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "', '" + sKM + "')");
                if (rs.next()) {
                    txtNoTrx.setText(rs.getString(1));
                }
                rs.close();
                String sIns = "INSERT INTO petty_cash("
                        + "            no_trx, tanggal, no_bukti, k_m, bayar_ke_dari, keterangan, keluar, masuk, "
                        + "            user_ins, time_ins, user_upd, time_upd)"
                        + "    VALUES ('" + txtNoTrx.getText() + "', '" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "', "
                        + "'" + txtNoBukti.getText() + "', '" + sKM + "', '" + txtTerimaDari.getText() + "', '" + txtKeterangan.getText() + "', "
                        + "" + (sKM.equalsIgnoreCase("K")? fn.udfGetDouble(txtJumlah.getText()): 0) + ", "
                        + "" + (sKM.equalsIgnoreCase("M")? fn.udfGetDouble(txtJumlah.getText()): 0) + ", "
                        + "            '" + MainForm.sUserName + "', now(), null, null);";

                int i = conn.createStatement().executeUpdate(sIns);

                JOptionPane.showMessageDialog(this, "Simpan Petty Cash Sukses!");
                this.dispose();
                
            }else{
                String sIns = "update petty_cash "
                        + "SET no_trx='" + txtNoTrx.getText() + "', "
                        + "tanggal='" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "', "
                        + "no_bukti='" + txtNoBukti.getText() + "', "
                        + "k_m='" + sKM + "', "
                        + "bayar_ke_dari='" + txtTerimaDari.getText() + "', "
                        + "keterangan='" + txtKeterangan.getText() + "', "
                        + "keluar=" + (sKM.equalsIgnoreCase("K")? fn.udfGetDouble(txtJumlah.getText()): 0) + ", "
                        + "masuk=" + (sKM.equalsIgnoreCase("M")? fn.udfGetDouble(txtJumlah.getText()): 0) + ", "
                        + "user_upd='" + MainForm.sUserName + "', time_upd=now() "
                        + "where no_trx='"+txtNoTrx.getText()+"'";

                int i = conn.createStatement().executeUpdate(sIns);

                JOptionPane.showMessageDialog(this, "Update Petty Cash Sukses!");
                this.dispose();
            }
            if(srcForm!=null && srcForm instanceof FrmPettyCashList )
                    ((FrmPettyCashList)srcForm).udfLoadPettyCash();
                
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }
    
    public void udfLoadPettyCash(String sNo){
        txtNoTrx.setText(sNo);
        String s="SELECT no_trx, tanggal, coalesce(no_bukti,'') as no_bukti, k_m, coalesce(bayar_ke_dari,'') as bayar_ke_dari, "
                + "coalesce(keterangan,'') as keterangan, coalesce(keluar,0) as keluar, coalesce(masuk,0) as masuk,"
                + "case when k_m='M' then  coalesce(masuk,0) else coalesce(keluar,0)  end as jumlah ,"
                + "       user_ins, time_ins "
                + "  FROM petty_cash where no_trx='"+sNo+"';";
        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                txtNoTrx.setText(rs.getString("no_trx"));
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                txtNoBukti.setText(rs.getString("no_bukti"));
                txtTerimaDari.setText(rs.getString("bayar_ke_dari"));
                txtKeterangan.setText(rs.getString("keterangan"));
                sKM=rs.getString("k_m");
                txtJumlah.setText(fn.dFmt.format(rs.getDouble("jumlah")));
                btnSimpan.setText("Update");
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }
    
    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtJumlah))
                fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F4:{
                //    udfNew();
                    break;
                }
                case KeyEvent.VK_F5:{
                //    udfSave();
                    break;
                }
                case KeyEvent.VK_ENTER : {
                   if(!(ct instanceof JTable)){
//                        if(txtKode.isFocusOwner()){
//                            txtQty.requestFocusInWindow();
//                            return;
                        //}else
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
//                        if(((JTable)ct).getSelectedRow()==0){
////                            Component c = findNextFocus();
////                            if (c==null) return;
////                            if(c.isEnabled())
////                                c.requestFocus();
////                            else{
////                                c = findNextFocus();
////                                if (c!=null) c.requestFocus();;
////                            }
//                        }
                    }else{
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findPrevFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
//                            c = findPreFocus();
//                            if (c!=null) c.requestFocus();;
                        }
                    }
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
                if((e.getSource() instanceof JTextField )){
//                if(e.getSource().equals(txtQty)){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
//                else if(e.getSource().equals(txtKelas) && !fn.isListVisible()){
//                    sOldKelas=txtKelas.getText();
//                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtJumlah) )
                    txtJumlah.setText(fn.dFmt.format(fn.udfGetDouble(txtJumlah.getText())));
           }
        }
    } ;
}