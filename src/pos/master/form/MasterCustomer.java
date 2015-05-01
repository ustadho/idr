/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.KotaDao;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.dao.jdbc.RelasiKategoriDao;
import com.ustasoft.pos.domain.Kota;
import com.ustasoft.pos.domain.Relasi;
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
import pos.master.list.FrmListCustomer;

/**
 *
 * @author cak-ust
 */
public class MasterCustomer extends javax.swing.JDialog {
    private Connection conn;
    private Object srcForm;
    private RelasiDao relasiDao;
    private RelasiKategoriDao daoRelasiKategori;
    private KotaDao daoKota;
    private AccCoaDao daoAcc;
    private String sOldKode="";
    GeneralFunction fn=new GeneralFunction();
    List<Kota> kota;
    List<AccCoaView> coa;
    List<RelasiKategori> relasiKategori;
    /**
     * Creates new form MasterCoa
     */
    public MasterCustomer(java.awt.Frame parent, boolean modal) {
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
        relasiDao=new RelasiDao(conn);
        udfInitForm();
    }

    private void udfClear() {
        txtIDSupp.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelp.setText("");
        txtHp.setText("");
        txtKontak.setText("");
        txtEmail.setText("");
        txtNoNpwp.setText("");
        txtFax.setText("");
    }

    private void udfInitForm(){
        try {
            cmbKota.removeAllItems();
            daoKota=new KotaDao(conn);
            kota=daoKota.cariSemuaData();
            
            for(Kota ac : kota){
                cmbKota.addItem(ac.getNamaKota());
            }
            
            cmbTipeCust.removeAllItems();
            daoRelasiKategori=new RelasiKategoriDao(conn);
            relasiKategori=daoRelasiKategori.cariSemuaKategori();
            for(RelasiKategori kt : relasiKategori){
                cmbTipeCust.addItem(kt.getNamaKategori());
            }
            
            cmbAkun.removeAllItems();
            daoAcc=new AccCoaDao(conn);
            coa=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : coa){
                cmbAkun.addItem(lcoa.getAcc_name());
            }
            txtKode.setEnabled(false);
            txtKode.setDisabledTextColor(Color.black);
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
            Integer idTipeSupp=cmbTipeCust.getSelectedIndex()<0? null: relasiKategori.get(cmbTipeCust.getSelectedIndex()).getId();
            if(id==null){
                txtKode.setText(relasiDao.getKode());
            }
            Relasi cust=new Relasi();
            cust.setIdRelasi(id);
            cust.setTipeRelasi(0);
            cust.setNamaRelasi(txtNama.getText());
            cust.setAlamat(txtAlamat.getText());
            cust.setIdKota(idKota);
            cust.setKontak(txtKontak.getText());
            cust.setTelp(txtTelp.getText());
            cust.setFax(txtFax.getText());
            cust.setHp(txtHp.getText());
            cust.setEmail(txtEmail.getText());
            cust.setAccNo(cmbAkun.getSelectedIndex()>=0? coa.get(cmbAkun.getSelectedIndex()).getAcc_no(): null);
            cust.setTop(GeneralFunction.udfGetInt(txtTop.getText()));
            cust.setNoNpwp(txtNoNpwp.getText());
            cust.setKode(txtKode.getText());
            cust.setIdKategori(relasiKategori.get(cmbTipeCust.getSelectedIndex()).getId());
            cust.setCust(true);
            cust.setSupp(false);
            
            relasiDao.simpan(cust);
            if(srcForm instanceof FrmListCustomer){
                ((FrmListCustomer)srcForm).tampilkanData(cust.getIdRelasi(), "");
            }
            JOptionPane.showMessageDialog(this, "Simpan customer sukses!");
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
        jLabel8 = new javax.swing.JLabel();
        txtKontak = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtTelp = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtHp = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cmbAkun = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cmbTipeCust = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        txtNoNpwp = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Customer\n");
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
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 270, 100, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 270, 80, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 270, 120, -1));

        jLabel5.setText("ID :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtIDSupp.setEditable(false);
        txtIDSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtIDSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 90, 20));

        chkActive.setSelected(true);
        chkActive.setText("Active");
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 120, -1));

        cmbKota.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 270, -1));

        jLabel6.setText("Nama Customer");
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
        jPanel2.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 180, 20));

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

        jLabel12.setText("No. Akun");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 90, 20));

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 250, -1));

        jLabel13.setText("T.O.P :");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 185, 40, 20));

        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTopActionPerformed(evt);
            }
        });
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 185, 40, 20));

        jLabel14.setText(" Hari");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 185, 50, 20));

        jLabel15.setText("Kategori :");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 90, 20));

        cmbTipeCust.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbTipeCust, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 185, 170, -1));

        jLabel16.setText("No. NPWP");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 235, 90, 20));

        txtNoNpwp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoNpwp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoNpwpActionPerformed(evt);
            }
        });
        jPanel2.add(txtNoNpwp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 235, 320, 20));

        jLabel17.setText("Fax :");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 160, 30, 20));

        txtFax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtFax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaxActionPerformed(evt);
            }
        });
        jPanel2.add(txtFax, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 160, 100, 20));

        jLabel18.setText("Kode :");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 70, 20));

        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeActionPerformed(evt);
            }
        });
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 140, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 440, 310));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Customer");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        setSize(new java.awt.Dimension(468, 394));
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

    private void txtNoNpwpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoNpwpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoNpwpActionPerformed

    private void txtFaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFaxActionPerformed

    private void txtKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKodeActionPerformed

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
            java.util.logging.Logger.getLogger(MasterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterCustomer dialog = new MasterCustomer(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JComboBox cmbKota;
    private javax.swing.JComboBox cmbTipeCust;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JTextField txtIDSupp;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoNpwp;
    private javax.swing.JTextField txtTelp;
    private javax.swing.JTextField txtTop;
    // End of variables declaration//GEN-END:variables

    public void tampilkanCustomer(Integer idSupp) {
        try {
            Relasi view=relasiDao.cariByID(idSupp);
            txtIDSupp.setText(view.getIdRelasi().toString());
            txtNama.setText(view.getNamaRelasi());
            txtAlamat.setText(view.getAlamat());
            txtKontak.setText(view.getKontak());
            cmbKota.setSelectedItem(daoKota.cariByID(view.getIdKota()).getNamaKota());
            chkActive.setSelected(view.getActive());
            txtTelp.setText(view.getTelp());
            txtFax.setText(view.getFax());
            txtHp.setText(view.getHp());
            txtEmail.setText(view.getEmail());
            txtTop.setText(GeneralFunction.intFmt.format(view.getTop()));
            txtNoNpwp.setText(view.getNoNpwp());
            txtKode.setText(view.getKode());
            cmbKota.setSelectedItem(daoKota.cariByID(view.getIdKota()).getNamaKota());
            cmbTipeCust.setSelectedItem(daoRelasiKategori.cariKategoriByID(view.getIdKategori()).getNamaKategori());
        } catch (Exception ex) {
            Logger.getLogger(MasterCustomer.class.getName()).log(Level.SEVERE, null, ex);
                    
        }
        
    }
}
