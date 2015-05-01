/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.akuntansi;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccBuktiKasDao;
import com.ustasoft.pos.domain.AccBuktiKas;
import com.ustasoft.pos.domain.AccBuktiKasDetail;
import com.ustasoft.pos.service.ReportService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXDatePicker;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmBuktiKasRM extends javax.swing.JInternalFrame {
    private MyKeyListener kListener=new MyKeyListener();
    private Connection conn;
    private GeneralFunction fn = new GeneralFunction();
    private Component aThis;
    private String sFlag;
    private AccBuktiKasDao dao;
    private Object srcForm;
    private String sNoBukti="";
    private ReportService service;
    /**
     * Creates new form FrmBuktiKasRM
     */
    public FrmBuktiKasRM() {
        initComponents();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        tblDetail.addKeyListener(kListener);
        txtDesc.addKeyListener(kListener);
        jXDatePicker1.addKeyListener(kListener);
        
        tblDetail.getColumn("Jumlah").setCellEditor(new MyTableCellEditor());
        tblDetail.setRowHeight(20);
        tblDetail.getModel().addTableModelListener(new MyTableModelListener(tblDetail));
        tblDetail.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblDetail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    public void setConn(Connection c) {
        this.conn = c;
    }

    public void setNoBukti(String no){
        this.sNoBukti=no;
        txtNoBukti.setText(no);
    }
    private void initForm() {
        aThis = this;
        fn.setConn(conn);
        service=new ReportService(conn, aThis);
        jXDatePicker1.setFormats(MainForm.sDateFormat);
        jDateCheque.setFormats(MainForm.sDateFormat);
        
        dao=new AccBuktiKasDao(conn);
        jLabel2.setText(sFlag.equalsIgnoreCase("K")? "Paid From": "Deposit To");
        jPanel3.setVisible(sFlag.equalsIgnoreCase("K"));
        
        lblOleh1.setText(sFlag.equalsIgnoreCase("M")? "Disetor Oleh" : "Diterima Oleh");
        lblOleh2.setText(sFlag.equalsIgnoreCase("M")? "Diterima Oleh" : "Disetor Oleh");
        txtOleh2.setText(MainForm.sUserName);
        
        tblDetail.getColumn("No").setPreferredWidth(50);
        tblDetail.getColumn("Nama Akun").setPreferredWidth(250);
        tblDetail.getColumn("Keterangan").setPreferredWidth(150);
        
        if(!sNoBukti.equalsIgnoreCase("")){
            udfLoadBK();
        }
    }

    private void udfLoadBK(){
        try{
                String sQry="select no_bukti, b.acc_no, acc_name, rate, tanggal, " +
                        "coalesce(memo,'') as memo, coalesce(amount,0) as amount, coalesce(flag, '') as flag, " +
                        "coalesce(batal, false), coalesce(no_cek,'') as no_cek, " +
                        "tgl_cek, payee, coalesce(no_voucher,'') as no_voucher," +
                        "coalesce(diketahui_oleh, '') as diketahui_oleh, coalesce(diterima_oleh,'') as diterima_oleh, " +
                        "coalesce(dibayar_oleh,'') as dibayar_oleh, terbilang(coalesce(amount,0)) as terbilang " +
                        "from acc_bukti_kas b left join acc_coa a on b.acc_no = a.acc_no " +
                        "where no_bukti='"+ txtNoBukti.getText().toUpperCase() +"' ";

                System.out.println(sQry);
                ResultSet rs=conn.createStatement().executeQuery(sQry);
                int row=0;
                while(rs.next()){
                    //jLabel4.setText(txtCariVoucher.getText());
                    txtNoReff.setText(rs.getString("no_voucher"));
                    txtDepositTo.setText(rs.getString("acc_no"));
                    lblDepositTo.setText(rs.getString("acc_name"));
                    jXDatePicker1.setDate(rs.getDate("tanggal"));
                    txtDesc.setText(rs.getString("memo"));
                    lblTotal.setText(GeneralFunction.dFmt.format(rs.getDouble("amount")));
                    jDateCheque.setDate(rs.getDate("tgl_cek"));
                    sFlag=rs.getString("flag");
                    txtNoCek.setText(rs.getString("no_cek"));
                    txtDiketahuiOleh.setText(rs.getString("diketahui_oleh"));
                    txtOleh1.setText(sFlag.equalsIgnoreCase("M")? rs.getString("dibayar_oleh") : rs.getString("diterima_oleh"));
                    txtOleh2.setText(sFlag.equalsIgnoreCase("K")? rs.getString("dibayar_oleh") : rs.getString("diterima_oleh"));
                    lblTerbilang.setText(rs.getString("terbilang"));
//                    chkVoidCheque.setSelected(rs.getDate("tgl_cek")==null?false:true);
//                    btnPreview.setEnabled(true);
                    
                    row = row +1;

                }

                rs.close();

                if (row==0) return;

                double totJml = 0;
                String sQry1="select no_bukti, d.acc_no, acc_name, amount, coalesce(memo,'') as memo, kode_dept, kode_project, serial_no, source_no, tipe " +
                            " from acc_bukti_kas_detail d left join acc_coa a on d.acc_no = a.acc_no   " +
                            " where no_bukti ='"+ txtNoBukti.getText() +"'";
                 System.out.println(sQry1);
                 ResultSet rs1=conn.createStatement().executeQuery(sQry1);
                 int iRow=0;
                 while(rs1.next()){
                     ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                         tblDetail.getRowCount()+1,
                         rs1.getString("acc_no"), 
                         rs1.getString("acc_name"), 
                         rs1.getDouble("amount"), 
                         rs1.getString("memo")
                     });
                     totJml = totJml + (rs1.getDouble("amount"));
                 }
                 rs1.close();

                 lblTotal.setText(GeneralFunction.dFmt.format(totJml));
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
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
        lblDepositTo = new javax.swing.JLabel();
        txtDepositTo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtNoBukti = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        lblTerbilang = new javax.swing.JLabel();
        txtOleh1 = new javax.swing.JTextField();
        lblOleh1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDesc = new javax.swing.JTextArea();
        lblTotal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtNoCek = new javax.swing.JTextField();
        jDateCheque = new org.jdesktop.swingx.JXDatePicker();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNoReff = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtOleh2 = new javax.swing.JTextField();
        lblOleh2 = new javax.swing.JLabel();
        txtDiketahuiOleh = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Bukti Kas");
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

        jPanel1.setLayout(null);

        lblDepositTo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblDepositTo);
        lblDepositTo.setBounds(195, 10, 545, 22);

        txtDepositTo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDepositTo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDepositToKeyReleased(evt);
            }
        });
        jPanel1.add(txtDepositTo);
        txtDepositTo.setBounds(100, 10, 90, 22);

        jLabel2.setText("Deposit to : ");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(15, 10, 80, 20);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel3.setText("Tanggal :");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(245, 10, 80, 20);

        txtNoBukti.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNoBukti);
        txtNoBukti.setBounds(95, 10, 140, 22);

        jLabel4.setText("No. Bukti :");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(10, 10, 80, 20);
        jPanel2.add(jXDatePicker1);
        jXDatePicker1.setBounds(330, 10, 160, 22);
        jPanel2.add(lblTerbilang);
        lblTerbilang.setBounds(95, 120, 630, 20);

        txtOleh1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtOleh1);
        txtOleh1.setBounds(95, 35, 395, 22);

        lblOleh1.setText("Diterima Oleh :");
        jPanel2.add(lblOleh1);
        lblOleh1.setBounds(10, 35, 80, 20);

        txtDesc.setColumns(20);
        txtDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDesc.setRows(5);
        jScrollPane1.setViewportView(txtDesc);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(95, 60, 395, 55);

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0");
        lblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblTotal);
        lblTotal.setBounds(595, 95, 130, 20);

        jLabel9.setText("Keterangan : ");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(10, 60, 80, 20);

        jLabel10.setText("Terbilang : ");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(10, 120, 80, 20);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("TOTAL : ");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jLabel12);
        jLabel12.setBounds(510, 95, 80, 20);

        jPanel3.setLayout(null);

        jLabel7.setText("No. Cek :");
        jPanel3.add(jLabel7);
        jLabel7.setBounds(5, 5, 80, 20);

        txtNoCek.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtNoCek);
        txtNoCek.setBounds(90, 5, 125, 22);
        jPanel3.add(jDateCheque);
        jDateCheque.setBounds(90, 30, 130, 22);

        jLabel11.setText("Tgl. Cek :");
        jPanel3.add(jLabel11);
        jLabel11.setBounds(5, 30, 80, 20);

        jPanel2.add(jPanel3);
        jPanel3.setBounds(510, 35, 220, 55);

        jLabel8.setText("No. Reff.");
        jPanel2.add(jLabel8);
        jLabel8.setBounds(515, 10, 80, 20);

        txtNoReff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNoReff);
        txtNoReff.setBounds(600, 10, 125, 22);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(5, 35, 735, 150);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 10, 750, 190);

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No. Akun", "Nama Akun", "Jumlah", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblDetail);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(15, 200, 735, 165);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(null);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel4.add(btnClose);
        btnClose.setBounds(625, 10, 90, 25);

        btnClear.setText("Bersihkan");
        btnClear.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel4.add(btnClear);
        btnClear.setBounds(290, 10, 105, 23);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        btnDelete.setText("Hapus");
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel4.add(btnDelete);
        btnDelete.setBounds(420, 10, 100, 25);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/accept.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel4.add(btnSave);
        btnSave.setBounds(520, 10, 105, 25);

        jLabel5.setText("Kasir : ");
        jPanel4.add(jLabel5);
        jLabel5.setBounds(10, 10, 95, 20);

        txtOleh2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(txtOleh2);
        txtOleh2.setBounds(105, 10, 150, 22);

        lblOleh2.setText("Diketahui Oleh : ");
        jPanel4.add(lblOleh2);
        lblOleh2.setBounds(10, 35, 95, 20);

        txtDiketahuiOleh.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(txtDiketahuiOleh);
        txtDiketahuiOleh.setBounds(105, 35, 150, 22);

        getContentPane().add(jPanel4);
        jPanel4.setBounds(15, 370, 735, 70);

        setBounds(0, 0, 773, 475);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDepositToKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepositToKeyReleased
        String sQry = "select acc_no, coalesce(acc_name,'') as acc_name "
                + "from acc_coa where acc_type in('10', '03') "
                + "and acc_no not in(select distinct sub_acc_of from acc_coa where acc_type in('10', '03') and sub_acc_of is not null) "
                + "and acc_no||coalesce(acc_name,'') "
                + "iLike '%" + txtDepositTo.getText() + "%' order by acc_no ";
        fn.lookup(evt, new Object[]{lblDepositTo}, sQry, txtDepositTo.getWidth()+lblDepositTo.getWidth()+20, 200);
    }//GEN-LAST:event_txtDepositToKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        bersihkan();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        simpan();
    }//GEN-LAST:event_btnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private org.jdesktop.swingx.JXDatePicker jDateCheque;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblDepositTo;
    private javax.swing.JLabel lblOleh1;
    private javax.swing.JLabel lblOleh2;
    private javax.swing.JLabel lblTerbilang;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtDepositTo;
    private javax.swing.JTextArea txtDesc;
    private javax.swing.JTextField txtDiketahuiOleh;
    private javax.swing.JTextField txtNoBukti;
    private javax.swing.JTextField txtNoCek;
    private javax.swing.JTextField txtNoReff;
    private javax.swing.JTextField txtOleh1;
    private javax.swing.JTextField txtOleh2;
    // End of variables declaration//GEN-END:variables

    public void setFlag(String mk) {
        this.sFlag=mk;
    }

    private void bersihkan() {
        txtDepositTo.setText("");
        lblDepositTo.setText("");
        txtNoBukti.setText("");
        txtNoCek.setText("");
        txtDesc.setText("");
        txtOleh1.setText("");
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
    }

    private boolean bolehSimpan(){
       btnSave.requestFocusInWindow();
        if(txtDepositTo.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silahkan masukkan Akun Kas/ Bank "+(sFlag.equalsIgnoreCase("M")? "Masuk": "Keluar") +" terlebih dulu!" );
            txtDepositTo.requestFocusInWindow();
            return false;
        }
        if(txtNoBukti.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silahkan masukkan nomor bukti kas terlebih dulu!");
            txtNoBukti.requestFocusInWindow();
            return false;
        }
        if(!sNoBukti.equalsIgnoreCase(txtNoBukti.getText()) && dao.existsKode(txtNoBukti.getText())){
            JOptionPane.showMessageDialog(this, "Nomor bukti tersebut sudah pernah digunakan!");
            txtNoBukti.requestFocusInWindow();
            return false; 
        }
        if(txtOleh1.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silahkan masukkan penerima terlebih dulu!");
            txtNoBukti.requestFocusInWindow();
            return false;
        }
        
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item di detail transaksi!");
            tblDetail.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    private void simpan() {
        if(!bolehSimpan()){
            return;
        }
        AccBuktiKas bk=new AccBuktiKas();
        bk.setNoBukti(txtNoBukti.getText());
        bk.setAccNo(txtDepositTo.getText());
        bk.setRate(1);
        bk.setTanggal(jXDatePicker1.getDate());
        bk.setMemo(txtDesc.getText());
        bk.setAmount(fn.udfGetDouble(lblTotal.getText()));
        bk.setFlag(sFlag);
        bk.setNoCek(txtNoCek.getText());
        bk.setTglCek(jDateCheque.getDate());
        bk.setPayee("");
        bk.setNoVoucher(txtNoReff.getText());
        bk.setTipe("BK"+sFlag);
        bk.setDiterimaOleh(sFlag.equalsIgnoreCase("M")? txtOleh2.getText(): txtOleh1.getText());
        bk.setDibayarOleh(sFlag.equalsIgnoreCase("M")?  txtOleh1.getText(): txtOleh2.getText());
        bk.setDiketahuiOleh(txtDiketahuiOleh.getText());
        bk.setUserIns(MainForm.sUserName);
        bk.setUserUpd(MainForm.sUserName);
        
        List<AccBuktiKasDetail> list=new ArrayList<AccBuktiKasDetail>();
        TableColumnModel col=tblDetail.getColumnModel();
        for(int i=0; i<tblDetail.getRowCount(); i++){
            AccBuktiKasDetail d=new AccBuktiKasDetail();
            d.setNoBukti(txtNoBukti.getText());
            d.setAccNo(tblDetail.getValueAt(i, col.getColumnIndex("No. Akun")).toString());
            d.setAmount(fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Jumlah"))));
            d.setMemo(tblDetail.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
            list.add(d);
        }
        bk.setBuktiKasDetail(list);
        if(sNoBukti.equalsIgnoreCase(""))
            dao.simpan(bk);
        else
            dao.update(sNoBukti, bk);
        JOptionPane.showMessageDialog(this, "Simpan bukti kas "+(sFlag.equalsIgnoreCase("M")? "masuk": "keluar") +" sukses");
        service.previewBuktiKas(txtNoBukti.getText());
        if(srcForm instanceof FrmBuktiKasRMHis){
            ((FrmBuktiKasRMHis)srcForm).udfLoadData(txtNoBukti.getText());
            this.dispose();
        }
    }

    void setSrcForm(Object aThis) {
        this.srcForm=aThis;
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
                case KeyEvent.VK_F4:{
//                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5:{
//                    simpan();
                    
                    break;
                }
                case KeyEvent.VK_INSERT:{
                    udfLookupItem();
                    
                    break;
                }
//                case KeyEvent.VK_ESCAPE:{
//                    dispose();
//                    break;
//                }
                
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow()>=0){
                        int iRow=tblDetail.getSelectedRow();
                        if(iRow<0)
                            return;
                        
                        ((DefaultTableModel)tblDetail.getModel()).removeRow(iRow);
                        if(tblDetail.getRowCount()<0)
                            return;
                        if(tblDetail.getRowCount()>iRow)
                            tblDetail.setRowSelectionInterval(iRow, iRow);
                        else if(tblDetail.getRowCount() > 0)
                            tblDetail.setRowSelectionInterval(0, 0);
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
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField ){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }else if(e.getSource() instanceof JXDatePicker){
                ((JXDatePicker)e.getSource()).setBackground(Color.YELLOW);
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField||e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;

    private void udfLookupItem(){
//        
        DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(this), true);
        
        String s="select *  from (" +
                "select acc_no, acc_name from acc_coa where acc_no not in(select distinct coalesce(sub_acc_of,'') from acc_coa where coalesce(sub_acc_of,'')<>'')\n" +
                "and active\n" +
                "order by acc_no  "
                + ")x " ;
        
        System.out.println(s);
        d1.setTitle("Chart of Account - Lookup");
        JTable tbl=d1.getTable();
        d1.udfLoad(conn, s, "(x.acc_no||x.acc_name)", null);
        d1.setVisible(true);

        if(d1.getKode().length()>0){
                TableColumnModel col=d1.getTable().getColumnModel();
                
                int iRow = tbl.getSelectedRow();
                

                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1,
                    tbl.getValueAt(iRow, col.getColumnIndex("acc_no")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("acc_name")).toString(),
                    0,
                    ""
                });

                tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                tblDetail.requestFocusInWindow();
                tblDetail.changeSelection(tblDetail.getRowCount()-1, tblDetail.getColumnModel().getColumnIndex("Jumlah"), false, false);
        }
    }

    final JTextField ustTextField = new JTextField() {
        protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
            if (hasFocus()) {
                return super.processKeyBinding(ks, e, condition, pressed);
            } else {
                this.requestFocus();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processKeyBinding(ks, e, condition, pressed);
                    }
                });
                return true;
            }
        }
    };

    public class MyNumberCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
//        JTextField text=new JTextField("");
//        ustTextField
        int col, row;

        private NumberFormat nf = NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
            //ustTextField.setEditable(isCellCanEdit);
            //ustTextField.setEnabled(btnGenerate.getText().equalsIgnoreCase("Generate"));
            col = vColIndex;
            row = rowIndex;
            ustTextField.setBackground(new Color(0, 255, 204));
            ustTextField.addFocusListener(txtFocusListener);
            //if(col==jTable1.getColumnModel().getColumnIndex("Minus")||col==jTable1.getColumnModel().getColumnIndex("Ditagihkan")){
            ustTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    if (col != 0) {
                        char c = evt.getKeyChar();
                        if (!((c >= '0' && c <= '9'))
                                && (c != KeyEvent.VK_BACK_SPACE)
                                && (c != KeyEvent.VK_DELETE)
                                && (c != KeyEvent.VK_ENTER)) {
                            getToolkit().beep();
                            evt.consume();
                            return;
                        }
                    }
                }
            });
            ustTextField.addFocusListener(txtFocusListener);
            if (isSelected) {

            }
            //System.out.println("Value dari editor :"+value);
            ustTextField.setText(value == null ? "" : value.toString());
            //component.setText(df.format(value));

            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
                //try {
                //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                //Number dVal = nf.parse(value.toString());
                ustTextField.setText(nf.format((Double) value));

//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            } else {
                ustTextField.setText(value == null ? "" : value.toString());
            }
            return ustTextField;
        }

        public Object getCellEditorValue() {
            Object o = "";//=component.getText();
            Object retVal = 0;
            try {
                return GeneralFunction.udfGetDouble(ustTextField.getText());
                //return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }

    }
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextComponent text = new JTextField() {

            @Override
            public void setFont(Font f) {
                super.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
            }
            
            protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
                if (hasFocus()) {
                    return super.processKeyBinding(ks, e, condition, pressed);
                } else {
                    this.requestFocus();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            processKeyBinding(ks, e, condition, pressed);
                        }
                    });
                    return true;
                }
            }
        };
        int col, row;
        private NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            col = vColIndex;
            row = rowIndex;
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.addKeyListener(kListener);
            text.setFont(table.getFont());
            text.setName("textEditor");
            text.removeKeyListener(kListener);
//           AbstractDocument doc = (AbstractDocument)text.getDocument();
//           doc.setDocumentFilter(null);
//           doc.setDocumentFilter(new FixedSizeFilter(iText));

            text.removeKeyListener(kListener);
            text.addKeyListener(kListener);

            if (isSelected) {
            }


            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
//                try {
                //Daouble dVal=Double.parseDouble(value.toString().replace(",",""));
                double dVal = fn.udfGetFloat(value.toString());
                text.setText(nf.format(dVal));
//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            } else {
                text.setText(value == null ? "" : value.toString());
            }
            return text;
        }

        public Object getCellEditorValue() {
            Object retVal = 0;
            try {
                if (col == tblDetail.getColumnModel().getColumnIndex("Jumlah")) {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                } else {
                    retVal = ((JTextField) text).getText();
                }
                return retVal;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }
        
    }
    
    
    public class MyTableModelListener implements TableModelListener {
        JTable table;
        double dKredit=0, dDebet=0;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        MyTableModelListener(JTable table) {
            this.table = table;
        }

        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();

            int mColIndex = e.getColumn();

            double dTotal=0;
            for (int i=0; i<tblDetail.getRowCount(); i++){
                dTotal+=GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Jumlah")));
            }
            lblTotal.setText(fn.dFmt.format(dTotal));
            if(conn!=null){
                lblTerbilang.setText(fn.terbilang(dTotal));
            }
        }
    }
 
    Color g1 = new Color(153, 255, 255);
    Color g2 = new Color(255, 255, 255);

    Color fHitam = new Color(0, 0, 0);
    Color fPutih = new Color(255, 255, 255);

    Color crtHitam = new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
