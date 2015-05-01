/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.inventory;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.GudangDao;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.ItemHistoryDao;
import com.ustasoft.pos.dao.jdbc.StockDao;
import com.ustasoft.pos.domain.Gudang;
import com.ustasoft.pos.domain.ItemHistory;
import com.ustasoft.pos.domain.PenerimaanStok;
import com.ustasoft.pos.domain.PenerimaanStokDetail;
import com.ustasoft.pos.domain.TrxType;
import com.ustasoft.pos.domain.view.AccCoaView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmMutasiStok extends javax.swing.JInternalFrame {
    private Connection conn;
    DlgLookup lookup;
    private Component aThis;
    private GeneralFunction fn = new GeneralFunction();
    private ItemDao itemDao;
    private StockDao stokDao;
    MyKeyListener kListener = new MyKeyListener();
    List<Gudang> gudang;
    List<TrxType> tipeStok;
    ItemHistoryDao daoItemHistory = new ItemHistoryDao();
    private Integer idPenerimaan;
    private String sInOut="IN";

    /**
     * Creates new form PenerimaanBarang
     */
    public FrmMutasiStok() {
        initComponents();
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "selectNextColumnCell");
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jXDatePicker1.setFormats(MainForm.sDateFormat);
    }

    public void setConn(Connection con) {
        this.conn = con;
        fn.setConn(conn);
        daoItemHistory.setConn(con);
    }

    private void udfInitForm() {
        jLabel2.setText(getTitle());
        txtNoInvoice.setEnabled(false);
        try {
            aThis = this;
            lookup = new DlgLookup(JOptionPane.getFrameForComponent(this), true);
            fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
            jXDatePicker1.addKeyListener(kListener);

            jXDatePicker1.addFocusListener(txtFocusListener);
            jScrollPane1.addKeyListener(kListener);
            table.addKeyListener(kListener);
            itemDao = new ItemDao(conn);
            stokDao = new StockDao(conn);


            table.getColumn("No").setPreferredWidth(40);
            table.getColumn("ProductID").setPreferredWidth(80);
            table.getColumn("Nama Barang").setPreferredWidth(200);
            table.getColumn("ProductID").setMinWidth(0);
            table.getColumn("ProductID").setMaxWidth(0);
            table.getColumn("ProductID").setPreferredWidth(0);

            table.setRowHeight(22);
            table.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    double total = 0;

                    int iRow = table.getSelectedRow();
                    TableColumnModel col = table.getColumnModel();
                    if (e.getColumn() == col.getColumnIndex("Qty") || e.getColumn() == col.getColumnIndex("Harga")) {
                        double nett = fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga"))) * fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty")));
                        table.setValueAt(nett, iRow, col.getColumnIndex("Sub Total"));
                    }

                    int colSubTotal = table.getColumnModel().getColumnIndex("Sub Total");
                    for (int i = 0; i < table.getRowCount(); i++) {
                        total += GeneralFunction.udfGetDouble(table.getValueAt(i, colSubTotal));
                    }
                    lblSubTotal.setText(GeneralFunction.intFmt.format(total));
                }
            });
            MyTableCellEditor cEditor = new MyTableCellEditor();
            table.getColumn("Qty").setCellEditor(cEditor);
            table.getColumn("Harga").setCellEditor(cEditor);
            table.getColumn("Keterangan").setCellEditor(cEditor);

            cmbGudang.removeAllItems();
            gudang = new GudangDao(conn).cariSemuaData();
            for (Gudang gd : gudang) {
                cmbGudang.addItem(gd.getNama_gudang());
            }

            cmbTipeStok.removeAllItems();
            tipeStok = stokDao.cariSemuaStokTipeUnplanedByInOut(sInOut);
            for (TrxType st : tipeStok) {
                cmbTipeStok.addItem(st.getKeterangan());
            }
            cmbAkun.removeAllItems();
            AccCoaDao daoAcc=new AccCoaDao(conn);
            List<AccCoaView> coa=daoAcc.cariSemuaCoa("");
            for(AccCoaView lcoa : coa){
                cmbAkun.addItem(lcoa.getAcc_name()+" - "+lcoa.getAcc_no());
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    txtKeterangan.requestFocusInWindow();
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(FrmMutasiStok.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void udfLookupItem() {
//        if(tblPaket.getSelectedRow()<0)
//            return;
//        
        DlgLookup d1 = new DlgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sItem = "";
        int colKode = table.getColumnModel().getColumnIndex("ProductID");
        for (int i = 0; i < table.getRowCount(); i++) {
            sItem += (sItem.length() == 0 ? "" : ",") + "'" + table.getValueAt(i, colKode).toString() + "'";
        }

        String s = "select *  from ("
                + "select coalesce(i.plu,'') as kode, i.nama_barang, coalesce(k.nama_kategori,'') as kategori, i.id::varchar as id "
                + "from m_item i "
                + "left join m_item_kategori k on k.id=i.id_kategori "
                + (sItem.length() > 0 ? "where i.id::varchar not in(" + sItem + ")" : "") + " "
                + "order by upper(i.nama_barang)  "
                + ")x ";

        System.out.println(s);
        d1.setTitle("Lookup Stok");
        d1.udfLoad(conn, s, "(x.id||x.nama_barang||kategori)", null);

        d1.setVisible(true);

        //if (d1.getKode().length() > 0) {
        if (d1.getTable().getSelectedRow() >= 0) {
            TableColumnModel col = d1.getTable().getColumnModel();
            JTable tbl = d1.getTable();
            int iRow = tbl.getSelectedRow();
            double nett = 0;
            double harga, ppn;
            String disc;
            ItemHistory itemHistory = null;
            try {
                itemHistory = daoItemHistory.cariHistoriTerakhir(Integer.parseInt(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString()));
            } catch (Exception se) {
                System.out.println("Error: " + se.getMessage());
            }
            harga = itemHistory == null || itemHistory.getItemCost() == null ? 0 : itemHistory.getItemCost();
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{
                        table.getRowCount() + 1,
                        tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString(),
                        tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                        1,
                        harga,
                        harga,
                        "",
                        tbl.getValueAt(iRow, col.getColumnIndex("id")).toString(),});

            table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
            table.requestFocusInWindow();
            table.changeSelection(table.getRowCount() - 1, table.getColumnModel().getColumnIndex("Qty"), false, false);
        }
    }

    private boolean udfCekBeforeSave() {
        btnSave.requestFocusInWindow();
        if (cmbGudang.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih gudang terlebih dulu!!");
            cmbGudang.requestFocusInWindow();
            return false;
        }
        if (cmbGudang.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih gudang terlebih dulu!!");
            cmbGudang.requestFocusInWindow();
            return false;
        }
        if (cmbTipeStok.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tipe transaksi stok masih belum disetting!");
            cmbTipeStok.requestFocusInWindow();
            return false;
        }
        if (txtNoInvoice.getText().equalsIgnoreCase("") && !txtNoInvoice.isEnabled()) {
            try {
                txtNoInvoice.setText(stokDao.getNoBuktiPenerimaan(tipeStok.get(cmbTipeStok.getSelectedIndex()).getKode()));
            } catch (SQLException ex) {
                Logger.getLogger(FrmMutasiStok.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        int colHarga=table.getColumnModel().getColumnIndex("Harga");
        for (int i = 0; i <table.getRowCount(); i++) {
            if(fn.udfGetDouble(table.getValueAt(i, colHarga))<=0){
                JOptionPane.showMessageDialog(this, "Harga harus dimasukkan supaya HPP tidak Nol!");
                table.requestFocusInWindow();
                table.changeSelection(i, colHarga, false, false);
                return false;
            }
        }
        return true;
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
        jLabel3 = new javax.swing.JLabel();
        txtNoInvoice = new javax.swing.JTextField();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cmbTipeStok = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cmbAkun = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblSubTotal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Penerimaan Stok");
        setToolTipText("");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Tanggal :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 100, 20));

        jLabel3.setText("No. Bukti :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNoInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNoInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 170, 20));
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 150, -1));

        jLabel8.setText("Gudang :");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 210, -1));

        jLabel5.setText("Keterangan :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 90, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 540, 20));

        jLabel9.setText("Tipe Transaksi :");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 35, 120, 20));

        cmbTipeStok.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel1.add(cmbTipeStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 35, 210, -1));

        jLabel12.setText("Kredit Akun : ");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 540, -1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "Nama Barang", "Qty", "Harga", "Sub Total", "Keterangan", "ProductID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(table);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 5, 130, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Total :");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 5, 90, 20));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnCancel.setText("Batal");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 80, 30));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 30, 105, 30));

        jLabel4.setBackground(new java.awt.Color(204, 255, 255));
        jLabel4.setForeground(new java.awt.Color(0, 0, 153));
        jLabel4.setText("<html>\n &nbsp <b>F4 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Membuat Transaksi baru <br> \n &nbsp <b>F5 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Menyimpan Transaksi <br>\n &nbsp <b>Del &nbsp &nbsp &nbsp : </b> &nbsp  Menghapus item pembelian  &nbsp  &nbsp <br>\n &nbsp <b>Insert : </b> &nbsp Menambah ItemTransaski<br>\n<hr>\n</html>"); // NOI18N
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setOpaque(true);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel2.setBackground(new java.awt.Color(51, 0, 102));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("  Penerimaan Stok");
        jLabel2.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        setBounds(0, 0, 742, 460);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbTipeStok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNoInvoice;
    // End of variables declaration//GEN-END:variables

    private Timestamp getTimeServer() {
        Timestamp skg = null;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select now() as skg");
            rs.next();
            skg = rs.getTimestamp("skg");
            rs.close();
        } catch (SQLException se) {
        }

        return skg;
    }

    private void udfSave() {
        if (!udfCekBeforeSave()) {
            return;
        }
        try {
            Timestamp timeServer = getTimeServer();
            PenerimaanStok header = new PenerimaanStok();
            header.setId(idPenerimaan);
            header.setDescription(txtKeterangan.getText());
            header.setIdGudang(gudang.get(cmbGudang.getSelectedIndex()).getId());
            header.setTanggal(jXDatePicker1.getDate());
            header.setTrxType(tipeStok.get(cmbTipeStok.getSelectedIndex()).getKode());
            header.setTimeIns(timeServer);
            header.setUserIns(MainForm.sUserName);
            header.setTimeUpd(timeServer);
            header.setUserUpd(MainForm.sUserName);
            header.setReffNo(txtNoInvoice.getText());
            stokDao.simpanMutasiStokHeader(header);

            List<PenerimaanStokDetail> detail = new ArrayList<PenerimaanStokDetail>();
            TableColumnModel col = table.getColumnModel();

            for (int i = 0; i < table.getRowCount(); i++) {
                PenerimaanStokDetail det = new PenerimaanStokDetail();
                det.setId(header.getId());
                det.setIdBarang(Integer.parseInt(table.getValueAt(i, col.getColumnIndex("ProductID")).toString()));
                det.setQty(GeneralFunction.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty"))));
                det.setHarga(GeneralFunction.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Harga"))));
                det.setKetItem(table.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                detail.add(det);
            }

            conn.setAutoCommit(false);
            stokDao.simpanMutasiStokDetail(detail);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Data penerimaan stok tersimpan!");
        } catch (SQLException se) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmMutasiStok.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setInOut(String s){
        this.sInOut=s;
    }

    private void setLocationRelativeTo(Object object) {
    }
    
    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {

//            if(getTableSource()==null)
//                return;

            if (evt.getSource() instanceof JTextField
                    && ((JTextField) evt.getSource()).getName() != null
                    && ((JTextField) evt.getSource()).getName().equalsIgnoreCase("textEditor")) {
                fn.keyTyped(evt);

            }

        }

        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if (!(evt.getSource() instanceof JTable)) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_F2: {
                    //udfClear();
                    break;
                }
                case KeyEvent.VK_F5: {
                    udfSave();

                    break;
                }
                case KeyEvent.VK_INSERT: {
                    udfLookupItem();

                    break;
                }

                case KeyEvent.VK_DELETE: {
                    if (evt.getSource().equals(table) && table.getSelectedRow() >= 0) {
                        int iRow = table.getSelectedRow();
                        if (iRow < 0) {
                            return;
                        }

                        ((DefaultTableModel) table.getModel()).removeRow(iRow);
                        if (table.getRowCount() < 0) {
                            return;
                        }
                        if (table.getRowCount() > iRow) {
                            table.setRowSelectionInterval(iRow, iRow);
                        } else if(table.getRowCount() >0){
                            table.setRowSelectionInterval(0, 0);
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
    private FocusListener txtFocusListener = new FocusListener() {
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField) {
                ((JTextField) e.getSource()).setBackground(Color.YELLOW);
                if ((e.getSource() instanceof JTextField && ((JTextField) e.getSource()).getName() != null && ((JTextField) e.getSource()).getName().equalsIgnoreCase("textEditor"))) {
                    ((JTextField) e.getSource()).setSelectionStart(0);
                    ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());

                }
            }
        }

        public void focusLost(FocusEvent e) {
            if (e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")
                    || e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")) {
                ((JTextField) e.getSource()).setBackground(Color.WHITE);
            }
        }
    };

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
                if (col == table.getColumnModel().getColumnIndex("Harga")
                        || col == table.getColumnModel().getColumnIndex("Qty")) {
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

    private Date getDueDate(Date d, int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }
}
