/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pos.akuntansi;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.AccRekonsiliasiBankDao;
import com.ustasoft.pos.domain.AccJurnalDetail;
import com.ustasoft.pos.domain.AccRekonsiliasi;
import com.ustasoft.pos.domain.view.AccCoaView;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmBankRekonsiliasi extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private AccRekonsiliasiBankDao dao;
    private AccCoaDao coaDao;
    List<AccCoaView> coas;
    /**
     * Creates new form FrmBankRekonsiliasi
     */
    public FrmBankRekonsiliasi() {
        initComponents();
        jTable1.getColumn("Keterangan").setPreferredWidth(200);
        jTable1.getColumn("JournalNo").setPreferredWidth(0);
        jTable1.getColumn("JournalNo").setMinWidth(0);
        jTable1.getColumn("JournalNo").setMaxWidth(0);
        jTable1.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                
                double totBeredar=0, totSesuai=0; 
                int colDebit=jTable1.getColumnModel().getColumnIndex("Terima");
                int colCredit=jTable1.getColumnModel().getColumnIndex("Keluar");
                int colBeres=jTable1.getColumnModel().getColumnIndex("Beres");
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    if(jTable1.getValueAt(i, colBeres) !=null && (Boolean)jTable1.getValueAt(i, colBeres)==true){
                        totSesuai+=fn.udfGetDouble(jTable1.getValueAt(i, colDebit))-fn.udfGetDouble(jTable1.getValueAt(i, colCredit));
                    }else{
                        totBeredar+=fn.udfGetDouble(jTable1.getValueAt(i, colDebit))-fn.udfGetDouble(jTable1.getValueAt(i, colCredit));
                    }
                }
                lblTotBeredar.setText(GeneralFunction.intFmt.format(totBeredar)); 
                lblTotSesuai.setText(GeneralFunction.intFmt.format(totSesuai)); 
                hitungSaldo();
            }
        });
        txtSaldoRekKoran.addFocusListener(txtFocusListener);
    }

    private void hitungSaldo(){
        if(cmbAkun.getSelectedIndex() >=0){
            lblKalkukasiSaldo.setText(fn.intFmt.format(dao.getSaldoBefore(coas.get(cmbAkun.getSelectedIndex()).getAcc_no())+fn.udfGetDouble(lblTotSesuai.getText()) ));
        }else{
            lblKalkukasiSaldo.setText("0");
        }
        lblSelisihSaldo.setText(fn.intFmt.format(fn.udfGetDouble(txtSaldoRekKoran.getText())-fn.udfGetDouble(lblKalkukasiSaldo.getText())));
    }
    public void setConn(Connection c){
        this.conn=c;
        fn.setConn(conn);
        dao=new AccRekonsiliasiBankDao(c);
        coaDao=new AccCoaDao(c);
    }
    
    private void bersihkan(){
//        cmbAkun.setSelectedIndex(-1);
        txtSaldoRekKoran.setText("0");
        lblKalkukasiSaldo.setText("0");
        lblSelisihSaldo.setText("0");
        lblTotBeredar.setText("0");
        lblTotSesuai.setText("0");
        lblTglTerakhir.setText("-");
        ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
        cmbAkun.requestFocus();
        
    }
    
    private boolean cekDulu(){
        btnSave.requestFocus();
        if(cmbAkun.getSelectedIndex()<0){
            JOptionPane.showMessageDialog(this, "Silahkan isi AccNo terlebih dulu!");
            cmbAkun.requestFocus();
            return false;
        }
        if(jTable1.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Daftar jurnal yang akan direkonsiliasi masih kosong!");
            cmbAkun.requestFocus();
            return false;
        }
        boolean st=false;
        int col=jTable1.getColumnModel().getColumnIndex("Beres");
        for(int i=0; i<jTable1.getRowCount(); i++){
            if((Boolean)jTable1.getValueAt(i, col) == true){
                st=true;
                break;
            }
        }
        return true && st;
    }
    
    private void initForm(){
        jxTanggal.setFormats(MainForm.sDateFormat);
        cmbAkun.removeAllItems();
        coas=coaDao.listKasBank();
        for(AccCoaView v: coas){
            cmbAkun.addItem(v.getAcc_name());
        }
        cmbAkun.setSelectedIndex(-1);
    }
    
    private void loadRekonsiliasi(){
        try {
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            if(cmbAkun.getSelectedIndex() < 0){
                bersihkan();
                return;
            }
            String accNo=coas.get(cmbAkun.getSelectedIndex()).getAcc_no();
            ResultSet rs=conn.createStatement().executeQuery(
                    "select * from fn_acc_reconsile_load('"+accNo+"') as "
                    + "(tanggal date, source_no varchar, no_cek varchar, description text, \n" +
                    "debit double precision, credit double precision, journal_no varchar)");
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getDate("tanggal"),
                    rs.getString("source_no"),
                    rs.getString("no_cek"),
                    rs.getString("description"),
                    rs.getDouble("debit"),
                    rs.getDouble("credit"),
                    false,
                    rs.getString("journal_no"),
                });
            }
            rs.close();
            if(jTable1.getRowCount() > 0){
                jTable1.setRowSelectionInterval(0, 0);
            }
            if(dao.lastReconsile(accNo) !=null){
                lblTglTerakhir.setText(new SimpleDateFormat("dd/MM/yyyy").format(dao.lastReconsile(accNo)));
            }else{
                lblTglTerakhir.setText("-");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmBankRekonsiliasi.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotSesuai = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblTotBeredar = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblSelisihSaldo = new javax.swing.JLabel();
        txtSaldoRekKoran = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblKalkukasiSaldo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTglTerakhir = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jxTanggal = new org.jdesktop.swingx.JXDatePicker();
        cmbAkun = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Rekonsiliasi Bank");
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

        jLabel5.setText("Total yang Sesuai : ");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(500, 10, 145, 20);

        jLabel6.setText("Total Beredar : ");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(500, 30, 145, 20);

        lblTotSesuai.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotSesuai.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotSesuai.setText("0");
        lblTotSesuai.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTotSesuai);
        lblTotSesuai.setBounds(645, 10, 130, 20);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(5, 60, 770, 5);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/accept.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btnSave);
        btnSave.setBounds(550, 70, 110, 30);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnCancel.setText("Batal");
        btnCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel1.add(btnCancel);
        btnCancel.setBounds(665, 70, 110, 30);

        lblTotBeredar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotBeredar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotBeredar.setText("0");
        lblTotBeredar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTotBeredar);
        lblTotBeredar.setBounds(645, 30, 130, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 390, 785, 110);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "No. Sumber", "No. Cek", "Keterangan", "Terima", "Keluar", "Beres", "JournalNo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 115, 785, 275);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        lblSelisihSaldo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSelisihSaldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSelisihSaldo.setText("0");
        lblSelisihSaldo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblSelisihSaldo);
        lblSelisihSaldo.setBounds(155, 80, 130, 20);

        txtSaldoRekKoran.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtSaldoRekKoran.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldoRekKoran.setText("0");
        txtSaldoRekKoran.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtSaldoRekKoran);
        txtSaldoRekKoran.setBounds(155, 40, 130, 20);

        jLabel10.setText("Selisih Saldo : ");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(10, 80, 145, 20);

        jLabel11.setText("Akun Bank : ");
        jPanel2.add(jLabel11);
        jLabel11.setBounds(10, 10, 80, 20);

        jLabel12.setText("Saldo Rekening Koran : ");
        jPanel2.add(jLabel12);
        jLabel12.setBounds(10, 40, 145, 20);

        jLabel13.setText("Kalkulasi Saldo :");
        jPanel2.add(jLabel13);
        jLabel13.setBounds(10, 60, 145, 20);

        lblKalkukasiSaldo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblKalkukasiSaldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKalkukasiSaldo.setText("0");
        lblKalkukasiSaldo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblKalkukasiSaldo);
        lblKalkukasiSaldo.setBounds(155, 60, 130, 20);

        jLabel14.setText("Tgl. Rekonsiliasi : ");
        jPanel2.add(jLabel14);
        jLabel14.setBounds(395, 55, 145, 20);

        lblTglTerakhir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTglTerakhir.setText("-");
        jPanel2.add(lblTglTerakhir);
        lblTglTerakhir.setBounds(550, 80, 100, 20);

        jLabel16.setText("Tgl. Terakhir Rekonsiliasi : ");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(395, 80, 145, 20);
        jPanel2.add(jxTanggal);
        jxTanggal.setBounds(550, 55, 105, 22);

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAkun.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAkunItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbAkun);
        cmbAkun.setBounds(105, 10, 445, 20);

        jCheckBox1.setText("Pilih Semua");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jPanel2.add(jCheckBox1);
        jCheckBox1.setBounds(675, 75, 95, 23);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(10, 5, 785, 110);

        setBounds(0, 0, 818, 557);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void cmbAkunItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAkunItemStateChanged
        loadRekonsiliasi();
    }//GEN-LAST:event_cmbAkunItemStateChanged

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        int colBeres=jTable1.getColumnModel().getColumnIndex("Beres");
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            jTable1.setValueAt(jCheckBox1.isSelected(), i, colBeres);
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private org.jdesktop.swingx.JXDatePicker jxTanggal;
    private javax.swing.JLabel lblKalkukasiSaldo;
    private javax.swing.JLabel lblSelisihSaldo;
    private javax.swing.JLabel lblTglTerakhir;
    private javax.swing.JLabel lblTotBeredar;
    private javax.swing.JLabel lblTotSesuai;
    private javax.swing.JTextField txtSaldoRekKoran;
    // End of variables declaration//GEN-END:variables

    private void udfSave() {
        if(!cekDulu())
            return;
        
        AccRekonsiliasi h=new AccRekonsiliasi();
        h.setAccNo(coas.get(cmbAkun.getSelectedIndex()).getAcc_no());
        h.setSaldoRekKoran(fn.udfGetDouble(txtSaldoRekKoran.getText()));
        h.setTanggal(jxTanggal.getDate());
        h.setUserIns(MainForm.sUserName);
        List<AccJurnalDetail> listJr=new ArrayList<AccJurnalDetail>();
        int colJournalNo=jTable1.getColumnModel().getColumnIndex("JournalNo");
        int colBeres=jTable1.getColumnModel().getColumnIndex("Beres");
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if((Boolean)jTable1.getValueAt(i, colBeres) ==true){
                AccJurnalDetail d=new AccJurnalDetail();
                d.setAccNo(h.getAccNo());
                d.setJournalNo(jTable1.getValueAt(i, colJournalNo).toString());
                listJr.add(d);
            }
        }
        
        dao.simpan(h, listJr);
        bersihkan();
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
                
                if(e.getSource().equals(txtSaldoRekKoran)){
                    txtSaldoRekKoran.setText(fn.intFmt.format(fn.udfGetDouble(txtSaldoRekKoran.getText())));
                    hitungSaldo();
                }
           }
        }
    } ;
}
