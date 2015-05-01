/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ArInvDao;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.domain.ArSuratJalan;
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
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXDatePicker;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmSJ extends javax.swing.JInternalFrame {
    private Connection conn;
    DlgLookup lookup;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private ArInvDao invDao=new ArInvDao();
    MyKeyListener kListener=new MyKeyListener();
    private Integer idPenjualan=null;
    private Object srcForm;
    private boolean isNew;
    private String userIns;
    private Date timeIns;
    
    /**
     * Creates new form PenerimaanBarang
     */
    public FrmSJ() {
        initComponents();
        tblDetail.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblDetail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }

    private void udfInitForm(){
        try {
            aThis=this;
            lookup=new DlgLookup(JOptionPane.getFrameForComponent(this), true);
            fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
            jScrollPane1.addKeyListener(kListener);
            tblDetail.addKeyListener(kListener);
//            itemDao=new ItemDao(conn);
            invDao.setConn(conn);
            tblDetail.getColumn("No").setPreferredWidth(40);
            tblDetail.getColumn("ProductID").setPreferredWidth(80);
            tblDetail.getColumn("Nama Barang").setPreferredWidth(200);
            tblDetail.setRowHeight(22);
            jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
            tblDetail.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    
                }
            });
            
            if(idPenjualan!=null){
                //tampilkanData(idPenjualan);
                udfLoadItemFromPenjualan();
                btnPrint.setEnabled(true);
            }
            jXDatePicker1.setDate(fn.getTimeServer());
            
        } catch (Exception ex) {
            Logger.getLogger(FrmSJ.class.getName()).log(Level.SEVERE, null, ex);
        }
        jLabel6.setText(getTitle());
    }
    
    private boolean udfCekBeforeSave(){
        btnSave.requestFocusInWindow();
        if(!btnSave.isEnabled()){
            return false;
        }
        if(lblIDPenjualan.getText().trim().length()==0){
            JOptionPane.showMessageDialog(aThis, "ID Penjualan masih kosong!");
            txtNoInvoice.requestFocusInWindow();
            return false;
        }
        if(txtNoInvoice.getText().trim().length()==0){
            JOptionPane.showMessageDialog(aThis, "Silahkan isi Nomor Invoice terlebih dulu!");
            txtNoInvoice.requestFocusInWindow();
            return false;
        }
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(aThis, "Item yang diterima masih belum dimasukkan!");
            txtNoInvoice.requestFocusInWindow();
            return false;
        }
        if(txtExpedisi.getText().equalsIgnoreCase("") && 
            JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk mengosongi Expedisi?", "Expedisi", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
            txtExpedisi.requestFocusInWindow();
            return false;
        }
        if(txtNoInvoice.getText().trim().length()==0){
            JOptionPane.showMessageDialog(aThis, "Silahkan masukan nomor Invoice terlebih dulu!", "No Invoice", JOptionPane.YES_NO_OPTION);
            txtNoInvoice.requestFocusInWindow();
            return false;
        }
        if(jXDatePicker1.getDate()==null){
            JOptionPane.showMessageDialog(aThis, "Silahkan isi tanggal Surat Jalan terlebih dulu!", "Tanggal", JOptionPane.YES_NO_OPTION);
            jXDatePicker1.requestFocusInWindow();
            return false;
        }
        return true;
    }
    
    private void udfSave(){
        if(!udfCekBeforeSave())
            return;
        try{
            Timestamp timeServer=fn.getTimeServer();
            ArSuratJalan sj=new ArSuratJalan();
            sj.setNoSj(txtNoSj.getText().trim().equalsIgnoreCase("")? null: txtNoSj.getText());
            sj.setArId(fn.udfGetInt(lblIDPenjualan.getText()));
            sj.setKirimKe(txtKirimKeNama.getText());
            sj.setAlamat(txtKirimKeAlamat.getText());
            sj.setIdKota(fn.udfGetInt(txtKirimKeKota.getText()));
            sj.setColi(fn.udfGetDouble(txtColi));
            sj.setTglSj(jXDatePicker1.getDate());
            sj.setKeterangan(txtKeterangan.getText());
            sj.setHormatKami(txtHormatKami.getText());
            sj.setColi(fn.udfGetDouble(txtColi.getText()));
            
            conn.setAutoCommit(false);
            invDao.simpanSuratJalan(sj);
            invDao.updateExpedisi(idPenjualan, fn.udfGetInt(txtExpedisi.getText()));
            conn.setAutoCommit(true);
            txtNoSj.setText(sj.getNoSj());
            if(srcForm!=null){
//                if(srcForm instanceof FrmPembelianHis){
//                    if(!isNew)
//                        {((FrmPembelianHis)srcForm).udfRefreshDataPerBaris(header.getId());}
//                    else
//                        {((FrmPembelianHis)srcForm).udfLoadData(header.getId());}
//                }
            }
            JOptionPane.showMessageDialog(this, "Surat Jalan tersimpan!");
            udfPrintSuratJalan();
            btnPrint.setEnabled(true);
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println(se.getMessage()+"lanjut:"+se.getNextException());
                if(se.getMessage().indexOf("Key (invoice_no)=("+txtNoInvoice.getText()+") already exists")>0){
                    JOptionPane.showMessageDialog(this, "Nomor invoice '"+txtNoInvoice.getText()+"' telah terpakai!\n"
                            + "Silahkan masukkan nomor lain!");
                    txtNoInvoice.requestFocus();
                    return;
                }
                JOptionPane.showMessageDialog(this, se.getMessage()+se.getNextException());
            } catch (SQLException ex) {
                Logger.getLogger(FrmSJ.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        jPanel4 = new javax.swing.JPanel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtNoInvoice = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtExpedisi = new javax.swing.JTextField();
        lblExpedisi = new javax.swing.JLabel();
        lblIDPenjualan = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtNoSj = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lblCustomer = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtKirimKeNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtKirimKeAlamat = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtKirimKeKota = new javax.swing.JTextField();
        lblKota = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtColi = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtHormatKami = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Surat Jalan");
        setToolTipText("");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameDeactivated(evt);
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
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel4.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 150, -1));

        jLabel23.setText("Tanggal :");
        jPanel4.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 90, 20));

        jLabel24.setText("Penjualan#");
        jPanel4.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 70, 20));

        txtNoInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoInvoiceActionPerformed(evt);
            }
        });
        jPanel4.add(txtNoInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 55, 190, 20));

        jLabel11.setText("Expedisi :");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 70, 20));

        txtExpedisi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtExpedisi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtExpedisiKeyReleased(evt);
            }
        });
        jPanel4.add(txtExpedisi, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 105, 30, 20));

        lblExpedisi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(lblExpedisi, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 105, 170, 20));

        lblIDPenjualan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(lblIDPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 160, 20));

        jLabel30.setText("No. Faktur :");
        jPanel4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 90, 20));

        jLabel32.setText("Surat Jalan#");
        jPanel4.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 90, 20));

        txtNoSj.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoSj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoSjActionPerformed(evt);
            }
        });
        jPanel4.add(txtNoSj, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 5, 190, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 280, 130));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 470, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Mohon Diantar Ke :");
        jPanel5.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 220, 20));

        txtKirimKeNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKirimKeNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKirimKeNamaKeyReleased(evt);
            }
        });
        jPanel5.add(txtKirimKeNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 470, 20));

        jLabel3.setText("Pelanggan:");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel27.setText("Keterangan :");
        jPanel5.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 470, 20));

        jLabel28.setText("Alamat");
        jPanel5.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 70, 20));

        txtKirimKeAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKirimKeAlamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKirimKeAlamatKeyReleased(evt);
            }
        });
        jPanel5.add(txtKirimKeAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 470, 20));

        jLabel12.setText("Kota :");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 70, 20));

        txtKirimKeKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKirimKeKota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKirimKeKotaKeyReleased(evt);
            }
        });
        jPanel5.add(txtKirimKeKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 60, 20));

        lblKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 190, 20));

        jLabel29.setText("Nama :");
        jPanel5.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 70, 20));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 570, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 870, 150));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "Nama Barang", "Qty", "Satuan", "ProductID", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblDetail);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 179, 870, 240));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel3.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 10, 90, 30));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel3.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(659, 10, -1, 30));

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/page.png"))); // NOI18N
        btnClear.setText("Bersihkan (F4)");
        btnClear.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel3.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 120, 30));

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setEnabled(false);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel3.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 10, 90, 30));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 461, 870, 50));

        jLabel6.setBackground(new java.awt.Color(51, 0, 102));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("  Surat Jalan");
        jLabel6.setOpaque(true);
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 870, -1));

        jLabel19.setText("Sales");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, 70, 20));

        jLabel26.setBackground(new java.awt.Color(204, 204, 204));
        jLabel26.setText("Total Coly :");
        jLabel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel26.setOpaque(true);
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 120, 20));

        txtColi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtColi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(txtColi, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 430, 120, 20));

        jLabel31.setBackground(new java.awt.Color(204, 204, 204));
        jLabel31.setText("Hormat Kami :");
        jLabel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel31.setOpaque(true);
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 430, 120, 20));

        txtHormatKami.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(txtHormatKami, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 430, 260, 20));

        setBounds(0, 0, 901, 546);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        udfClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtNoInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoInvoiceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoInvoiceActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated
        fn.setVisibleList(false);fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameDeactivated

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        udfPrintSuratJalan();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void txtExpedisiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtExpedisiKeyReleased
        String sQry="select s.id::varchar as id, nama_expedisi, s.alamat||' - '||kota.nama_kota as alamat "
                + "from m_expedisi s "
                + "left join m_kota kota on kota.id=s.id_kota "
                + "where s.id::varchar||nama_expedisi||s.alamat||' - '||kota.nama_kota "
                + "ilike '%"+txtExpedisi.getText()+"%' order by 2" ;
                        fn.lookup(evt, new Object[]{lblExpedisi}, sQry, txtExpedisi.getWidth()+lblExpedisi.getWidth(), 300);
    }//GEN-LAST:event_txtExpedisiKeyReleased

    private void txtKirimKeNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKirimKeNamaKeyReleased
    }//GEN-LAST:event_txtKirimKeNamaKeyReleased

    
    
    private void txtKirimKeAlamatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKirimKeAlamatKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKirimKeAlamatKeyReleased

    private void txtKirimKeKotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKirimKeKotaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKirimKeKotaKeyReleased

    private void txtNoSjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoSjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoSjActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblExpedisi;
    private javax.swing.JLabel lblIDPenjualan;
    private javax.swing.JLabel lblKota;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtColi;
    private javax.swing.JTextField txtExpedisi;
    private javax.swing.JTextField txtHormatKami;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtKirimKeAlamat;
    private javax.swing.JTextField txtKirimKeKota;
    private javax.swing.JTextField txtKirimKeNama;
    private javax.swing.JTextField txtNoInvoice;
    private javax.swing.JTextField txtNoSj;
    // End of variables declaration//GEN-END:variables

    public void tampilkanData(Integer id) {
//        try {
//            String sQry="select h.id, h.invoice_date as tanggal, h.id_customer, coalesce(c.nama_customer,'') as nama_customer, "
//                    + "coalesce(c.alamat,'')||' - '||coalesce(k.nama_kota,'') as alamat, "
//                    + "coalesce(h.invoice_no,'') as invoice_no, to_Char(h.invoice_date+coalesce(h.top,0), 'dd/MM/yyyy') as jt_tempo, "
//                    + "coalesce(h.top,0) as top, coalesce(h.ar_disc,'') as ar_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
//                    + "coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description, "
//                    + "coalesce(h.disiapkan_oleh,'') as disiapkan_oleh, coalesce(h.no_so,'') as no_so, "
//                    + "coalesce(h.diperiksa_oleh,'') as diperiksa_oleh, "
//                    + "coalesce(h.diterima_oleh,'') as diterima_oleh, "
//                    + "coalesce(u.complete_name,'') as dibuat_oleh, "
//                    + "coalesce(s.nama_sales,'') as nama_sales,"
//                    + "coalesce(h.id_expedisi::varchar,'') as id_expedisi, "
//                    + "coalesce(e.nama_expedisi,'') as nama_expedisi, "
//                    + "coalesce(ket_pembayaran,'') as ket_pembayaran, "
//                    + "coalesce(h.user_ins,'') as user_ins, h.time_ins "
//                    + "from ar_inv h "
//                    + "left join m_customer c on c.id_customer=h.id_customer "
//                    + "left join m_kota k on k.id=c.id_kota "
//                    + "left join m_gudang g on g.id=h.id_gudang "
//                    + "left join m_salesman s on s.id_sales=h.id_sales "
//                    + "left join m_Expedisi e on h.id_expedisi = e.id "
//                    + "left join m_user u on u.user_name=h.user_ins "
//                    + "left join ar_sj sj on sj.ar_id=h.id "
//                    + "where h.id="+id+" ";
//            ResultSet rs=conn.createStatement().executeQuery(sQry);
//            if(rs.next()){
//                idPenjualan=rs.getInt("id");
//                jXDatePicker1.setDate(rs.getDate("tanggal"));
//                txtCustomer.setText(rs.getString("id_customer"));
//                txtNoInvoice.setText(rs.getString("invoice_no"));
//                lblCustomer.setText("nama_customer");
//                lblAlamat.setText(rs.getString("alamat"));
//                txtTOP.setText(rs.getString("top"));
//                lblJtTempo.setText(rs.getString("jt_tempo"));
//                txtExpedisi.setText(rs.getString("id_expedisi"));
//                lblExpedisi.setText(rs.getString("nama_expedisi"));
//                cmbSales.setSelectedItem(rs.getString("nama_sales"));
//                txtKeterangan.setText(rs.getString("description"));
//                txtKirimKeNama.setText(rs.getString("no_so"));
//                
//                txtKetPembayaran.setText(rs.getString("ket_pembayaran"));
//                txtDisiapkanOleh.setText(rs.getString("disiapkan_oleh"));
//                txtDiperiksaOleh.setText(rs.getString("diperiksa_oleh"));
//                txtColi.setText(rs.getString("diterima_oleh"));
//                txtDibuatOleh.setText(rs.getString("dibuat_oleh"));
//                
//                txtDisc.setText(rs.getString("ar_disc"));
//                //txtBiayaLain.setText(fn.intFmt.format(rs.getInt("biaya_lain")));
//                txtBayar.setText(fn.intFmt.format(rs.getInt("paid_amount")));
//                userIns=rs.getString("user_ins");
//                timeIns=rs.getDate("time_ins");
//                rs.close();
//                
//                sQry="select d.ar_id, d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, d.unit_price, d.disc, d.ppn, coalesce(i.satuan,'') as satuan,"
//                        + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), "
//                        + "coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total, coalesce(d.last_Cost,0) as last_cost, "
//                        + "coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan, d.det_id "
//                        + "from ar_inv_det d  "
//                        + "left join m_item i on i.id=d.id_barang "
//                        + "where d.ar_id="+id +" "
//                        + "order by d.seq";
//                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
//                rs=conn.createStatement().executeQuery(sQry);
//                while(rs.next()){
//                    ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
//                        tblDetail.getRowCount()+1, 
//                        rs.getString("plu"), 
//                        rs.getString("nama_barang"), 
//                        rs.getDouble("qty"),
//                        rs.getString("satuan"),
//                        rs.getDouble("unit_price"),
////                        rs.getString("disc"),
////                        rs.getDouble("ppn"),
//                        rs.getDouble("biaya"),
//                        rs.getDouble("sub_Total"),
//                        rs.getDouble("last_cost"),
//                        rs.getString("keterangan"),
//                        rs.getInt("id_barang"), 
//                        rs.getInt("det_id")
//                    });
//                }
//                rs.close();
//                if(tblDetail.getRowCount()>0){
//                    tblDetail.setRowSelectionInterval(0, 0);
//                    tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(FrmSJ.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(FrmSJ.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

    public void setIdPenjualan(Integer id) {
        idPenjualan=id;
    }

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }

    void setIsNew(boolean b) {
        this.isNew=b;
    }

    private void udfClear() {
        idPenjualan=null;
        btnClear.requestFocusInWindow();
        txtNoInvoice.setText("");
        lblCustomer.setText("0");
        txtKirimKeNama.setText("");
        txtKirimKeAlamat.setText("");
        txtKirimKeKota.setText("");
        lblKota.setText("");
        txtNoSj.setText("");
        lblIDPenjualan.setText("");
        txtExpedisi.setText("");
        lblExpedisi.setText("");
                
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
        txtNoInvoice.requestFocusInWindow();
    }

    private void udfPrintSuratJalan() {
        if(txtNoSj.getText().equalsIgnoreCase("")){
            udfSave();
        }
        HashMap param=new HashMap();
        param.put("idPenjualan", idPenjualan);
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

        new ReportService(conn, aThis).cetakSuratJalan(param);
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
                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    
                    break;
                }
                
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
                        else
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
                if(e.getSource().equals(txtNoInvoice)){
                    udfLoadItemFromPenjualan();
                }
           }
        }
    } ;

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
        ;

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
                if (col == tblDetail.getColumnModel().getColumnIndex("Harga")
                        || col == tblDetail.getColumnModel().getColumnIndex("Biaya")
                        || col == tblDetail.getColumnModel().getColumnIndex("Qty")
                        ) {
                    
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
    
    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void udfLoadItemFromPenjualan(){
        String sQry="select h.id_customer, coalesce(h.invoice_no,'') as invoice_no, "
                + "coalesce(sj.kirim_ke, coalesce(c.nama_relasi,'')) as nama_customer, "
                + "coalesce(sj.alamat, coalesce(c.alamat,'')) as alamat, "
                + "coalesce(sj.id_kota::varchar, coalesce( c.id_kota::varchar, '')) as id_kota, "
                + "coalesce(k.nama_kota,'') as nama_kota, "
                + "coalesce(h.id_expedisi::varchar,'') as id_Expedisi, coalesce(e.nama_expedisi,'') as nama_expedisi, "
                + "coalesce(sj.no_sj,'') as no_sj, sj.tgl_sj, "
                + "coalesce(sj.hormat_kami,'') as hormat_kami, "
                + "coalesce(sj.coli,0) as coli  "
                + "from ar_inv h  "
                + "inner join m_relasi c on c.id_relasi=h.id_customer "
                + "left join m_expedisi e on e.id=h.id_expedisi "
                + "left join ar_sj sj on sj.ar_id=h.id "
                + "left join m_kota k on k.id=case when sj.no_sj is not null then sj.id_kota else c.id_kota end "
                + "where h.id="+idPenjualan+" ";
        System.out.println(sQry);
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                lblCustomer.setText(rs.getString("nama_customer"));
                txtKirimKeNama.setText(rs.getString("nama_customer"));
                txtKirimKeAlamat.setText(rs.getString("alamat"));
                txtKirimKeKota.setText(rs.getString("id_kota"));
                lblKota.setText(rs.getString("nama_kota"));
                txtExpedisi.setText(rs.getString("id_expedisi"));
                lblExpedisi.setText(rs.getString("nama_expedisi"));
                lblIDPenjualan.setText(idPenjualan.toString());
                txtNoInvoice.setText(rs.getString("invoice_no"));
                txtHormatKami.setText(rs.getString("hormat_kami"));
                txtColi.setText(rs.getString("coli"));
                txtNoSj.setText(rs.getString("no_sj"));
                jXDatePicker1.setDate(rs.getDate("tgl_sj"));
                rs.close();
                
                sQry="select d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan "
                + "from ar_inv_det d  "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.ar_id="+idPenjualan+" "
                + "order by d.seq";
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
            rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1, 
                    rs.getString("plu"), 
                    rs.getString("nama_barang"), 
                    rs.getDouble("qty"),
                    rs.getString("satuan"),
                    rs.getInt("id_barang"), 
                    null
                });
            }
            if(tblDetail.getRowCount()>0){
                tblDetail.setRowSelectionInterval(0, 0);
                tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
            }
            }
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        
    }
}
