/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ap;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.PoDao;
import com.ustasoft.pos.domain.Gudang;
import com.ustasoft.pos.domain.PO;
import com.ustasoft.pos.domain.PODet;
import com.ustasoft.pos.domain.view.ItemSupplierView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
public class FrmPO extends javax.swing.JInternalFrame {
    private Connection conn;
    DlgLookup lookup;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private ItemDao itemDao;
    private PoDao invDao=new PoDao();
    MyKeyListener kListener=new MyKeyListener();
    List<Gudang> gudang;
    private String sNoPo=null;
    private Object srcForm;
    private boolean isNew=true;
    
    /**
     * Creates new form PenerimaanBarang
     */
    public FrmPO() {
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
            fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
            jScrollPane1.addKeyListener(kListener);
            tblDetail.addKeyListener(kListener);
            itemDao=new ItemDao(conn);
            invDao.setConn(conn);
            tblDetail.getColumn("No").setPreferredWidth(40);
            tblDetail.getColumn("ProductID").setPreferredWidth(80);
            tblDetail.getColumn("Nama Barang").setPreferredWidth(200);
            tblDetail.setRowHeight(22);
            tblDetail.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    double total=0;
                    int iRow=tblDetail.getSelectedRow();
                    TableColumnModel col=tblDetail.getColumnModel();
                    if(e.getColumn()==col.getColumnIndex("Disc")||
                            e.getColumn()==col.getColumnIndex("PPn")||
                            e.getColumn()==col.getColumnIndex("Qty")||
                            e.getColumn()==col.getColumnIndex("Biaya")||
                            e.getColumn()==col.getColumnIndex("Harga")
                            ){
                        double  nett=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Harga")))*fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")));
                                nett=fn.getDiscBertingkat(nett, tblDetail.getValueAt(iRow, col.getColumnIndex("Disc")).toString());
                                nett=nett*(1+fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("PPn")))/100);
                                nett+=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Biaya")));
                                
                                tblDetail.setValueAt(nett, iRow, col.getColumnIndex("Sub Total"));
                    }
                    
                    int colSubTotal=tblDetail.getColumnModel().getColumnIndex("Sub Total");
                    for(int i=0; i<tblDetail.getRowCount(); i++){
                        total+=GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, colSubTotal));
                    }
                    lblSubTotal.setText(GeneralFunction.intFmt.format(total));
                    setGrandTotal();
                }
            });
            MyTableCellEditor cEditor=new MyTableCellEditor();
            tblDetail.getColumn("Qty").setCellEditor(cEditor);
            tblDetail.getColumn("Harga").setCellEditor(cEditor);
            tblDetail.getColumn("Disc").setCellEditor(cEditor);
            tblDetail.getColumn("PPn").setCellEditor(cEditor);
            tblDetail.getColumn("Biaya").setCellEditor(cEditor);
            tblDetail.getColumn("Keterangan").setCellEditor(cEditor);
            
            if(sNoPo!=null){
                tampilkanData(sNoPo);
                btnPrint.setEnabled(true);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtNoPo.setEnabled(!isNew);
    }
    
    private void setGrandTotal(){
        double gTotal=GeneralFunction.udfGetDouble(lblSubTotal.getText());
        if(txtDisc.getText().trim().length()>0){
            gTotal=fn.getDiscBertingkat(gTotal, txtDisc.getText());
        }
        gTotal+=GeneralFunction.udfGetDouble(txtBiayaLain.getText());        
        lblGrandTotal.setText(fn.intFmt.format(gTotal));
    }
    
    private void udfLookupItem(){
//        if(tblPaket.getSelectedRow()<0)
//            return;
//        
        DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sItem="";
        int colKode=tblDetail.getColumnModel().getColumnIndex("ProductID");
        for(int i=0; i< tblDetail.getRowCount(); i++){
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblDetail.getValueAt(i, colKode).toString()+"'";
        }
        
        String s="select *  from (" +
                "select i.id::varchar as id ,coalesce(i.plu,'') as kode, i.nama_barang, coalesce(i.satuan,'') as satuan, coalesce(k.nama_kategori,'') as kategori "
                + ""
                + "from m_item i "
                + "left join m_item_kategori k on k.id=i.id_kategori "
                +(sItem.length()>0? "where i.id::varchar not in("+sItem+")":"")+" "
                + "order by upper(i.nama_barang)  "
                + ")x " ;
        
        System.out.println(s);
        d1.setTitle("Lookup Stok");
        JTable tbl=d1.getTable();
        d1.udfLoad(conn, s, "(x.id||x.nama_barang||kategori)", null);
        tbl.getColumn("id").setPreferredWidth(0);
        tbl.getColumn("id").setMinWidth(0);
        tbl.getColumn("id").setMaxWidth(0);
        d1.setVisible(true);

        if(d1.getKode().length()>0){
            try {
                TableColumnModel col=d1.getTable().getColumnModel();
                
                int iRow = tbl.getSelectedRow();
                double nett=0;
                double harga, ppn;
                String disc;
                
                ItemSupplierView view=itemDao.cariSupplierById(
                        Integer.valueOf(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString()), 
                        fn.udfGetInt(txtSupplier.getText()));
                harga=view.getHarga()==null? 0: view.getHarga();
                disc=view.getDisc()==null? "0": view.getDisc();
                ppn=view.getPpn()==null? 10: view.getPpn();
                
                nett=fn.getDiscBertingkat(harga, disc);
                nett=nett*(1+ppn/100);

                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1,
                    tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                    1, 
                    tbl.getValueAt(iRow, col.getColumnIndex("satuan")).toString(),
                    harga, 
                    disc, 
                    ppn,
                    0,
                    nett,
                    getPrevCost(Integer.parseInt(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString())),
                    "",
                    tbl.getValueAt(iRow, col.getColumnIndex("id")).toString(),
                    null
                });

                tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                tblDetail.requestFocusInWindow();
                tblDetail.changeSelection(tblDetail.getRowCount()-1, tblDetail.getColumnModel().getColumnIndex("Qty"), false, false);
            } catch (SQLException ex) {
                Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Double getPrevCost(Integer id){
        Double hasil=new Double(0);
        try {
            String sQry="select fn_item_hist_get_prevcost("+id+")";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                hasil=rs.getDouble(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    
    private boolean udfCekBeforeSave(){
        btnSave.requestFocusInWindow();
        if(!btnSave.isEnabled()){
            return false;
        }
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silahkan pilih supplier terlebih dulu!");
            txtSupplier.requestFocusInWindow();
            return false;
        }
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang diterima masih belum dimasukkan!");
            txtSupplier.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    
    private void udfSave(){
        if(!udfCekBeforeSave())
            return;
        try{
            Timestamp timeServer=fn.getTimeServer();
            PO header = new PO();
            header.setNoPo(txtNoPo.getText());
            header.setSupplierId(Integer.parseInt(txtSupplier.getText()));
            header.setDescription(txtKeterangan.getText());
            header.setTanggal(jXDatePicker1.getDate());
            header.setTop(GeneralFunction.udfGetInt(txtTOP.getText()));
            header.setPoDisc(txtDisc.getText());
            header.setBiayaLain(fn.udfGetDouble(txtBiayaLain.getText()));
            header.setTimeIns(new Date(timeServer.getTime()));
            header.setUserIns(MainForm.sUserName);
            header.setTimeUpd(new Date(timeServer.getTime()));
            header.setUserUpd(MainForm.sUserName);
            conn.setAutoCommit(false);
            invDao.simpanHeader(header);
            sNoPo=header.getNoPo();
            txtNoPo.setText(sNoPo);
            header.setNoPo(sNoPo);
            
            List<PODet> detail=new ArrayList<PODet>();
            TableColumnModel col=tblDetail.getColumnModel();

            for(int i=0; i<tblDetail.getRowCount(); i++){
                PODet det=new PODet();
                det.setNoPo(header.getNoPo());
                det.setIdBarang(Integer.parseInt(tblDetail.getValueAt(i, col.getColumnIndex("ProductID")).toString()));
                det.setQty(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty"))));
                det.setUnitPrice(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Harga"))));
                det.setDisc(tblDetail.getValueAt(i, col.getColumnIndex("Disc")).toString());
                det.setPpn(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("PPn"))));
                det.setLastCost(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Hpp Terakhir"))));
                det.setBiaya(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Biaya"))));
                det.setKeterangan(tblDetail.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                detail.add(det);
            }
        
            
            invDao.simpanPODetail(detail, txtNoPo.getText().equalsIgnoreCase(""));
            sNoPo= header.getNoPo();
            
            conn.setAutoCommit(true);
            if(srcForm!=null){
                if(srcForm instanceof FrmPOHis){
                    if(!isNew)
                        {((FrmPOHis)srcForm).udfRefreshDataPerBaris(header.getNoPo());}
                    else
                        {((FrmPOHis)srcForm).udfLoadData(header.getNoPo());}
                }
            }
            JOptionPane.showMessageDialog(this, isNew? "Data PO tersimpan dengan nomor '"+sNoPo+"'!": "Update PO Sukses!");
            btnPrint.setEnabled(true);
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage()+se.getNextException());
            } catch (SQLException ex) {
                Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNoPo = new javax.swing.JTextField();
        lblSupplier = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        lblAlamatSup = new javax.swing.JLabel();
        lblJtTempo = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTOP = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblSubTotal = new javax.swing.JLabel();
        txtDisc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtBiayaLain = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pesanan Pembelian (PO)");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Tanggal :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 90, 20));

        jLabel2.setText("Supplier :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel3.setText("No. PO");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNoPo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoPo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoPoActionPerformed(evt);
            }
        });
        jPanel1.add(txtNoPo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 170, 20));

        lblSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 35, 520, 20));

        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 60, 20));
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 130, -1));

        lblAlamatSup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblAlamatSup, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 580, 20));

        lblJtTempo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 85, 120, 20));

        jLabel10.setText("Termin bayar :");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtTOP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtTOP, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 50, 20));

        jLabel11.setText("Jatuh Tempo :");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 85, 90, 20));

        jLabel12.setText(" Hari");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 85, 40, 20));

        jLabel5.setText("Keterangan :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 90, 20));

        txtKeterangan.setColumns(20);
        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtKeterangan.setRows(5);
        jScrollPane2.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 720, 50));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "Nama Barang", "Qty", "Satuan", "Harga", "Disc", "PPn", "Biaya", "Sub Total", "Hpp Terakhir", "Keterangan", "ProductID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true, true, true, true, false, false, true, false
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtDisc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscKeyTyped(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Disc :");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Sub Total :");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Biaya Lain :");

        txtBiayaLain.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtBiayaLain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaLain.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("GRAND TOTAL :");

        lblGrandTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGrandTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGrandTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBiayaLain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDisc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lblSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDisc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBiayaLain, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel4.setBackground(new java.awt.Color(204, 255, 255));
        jLabel4.setForeground(new java.awt.Color(0, 0, 153));
        jLabel4.setText("<html>\n &nbsp <b>F4 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Membuat Transaksi baru <br> \n &nbsp <b>F5 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Menyimpan Transaksi <br>\n &nbsp <b>Del &nbsp &nbsp &nbsp : </b> &nbsp  Menghapus item pembelian  &nbsp  &nbsp <br>\n &nbsp <b>Insert : </b> &nbsp Menambah ItemTransaski<br>\n<hr>\n</html>"); // NOI18N
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setOpaque(true);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/doc_text_image.png"))); // NOI18N
        btnClear.setText("Bersihkan");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel6.setBackground(new java.awt.Color(51, 0, 102));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("  Pesanan Pembelian (PO)");
        jLabel6.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(867, 546));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        String sQry="select id_relasi::varchar as id_supplier, coalesce(nama_relasi,'') as nama_supplier, alamat||' - '||kota.nama_kota as alamat, coalesce(s.top,0) as top "
                + "from m_relasi s "
                + "left join m_kota kota on kota.id=s.id_kota "
                + "where is_supp=true and id_relasi::varchar||coalesce(nama_relasi,'')||alamat||' - '||kota.nama_kota "
                + "ilike '%"+txtSupplier.getText()+"%' order by 2" ;
                        fn.lookup(evt, new Object[]{lblSupplier, lblAlamatSup, txtTOP}, sQry, txtSupplier.getWidth()+lblSupplier.getWidth(), 300);
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtDiscKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscKeyTyped
        char c = evt.getKeyChar();
          if (!(Character.isDigit(c) ||
             (c == KeyEvent.VK_BACK_SPACE) ||
             (c == '.') ||(c == ',') ||
             (c == '+') ||
             (c == KeyEvent.VK_DELETE)||
             (c == KeyEvent.VK_ENTER))) {
            //getToolkit().beep();
            evt.consume();
          }
    }//GEN-LAST:event_txtDiscKeyTyped

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        udfClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtNoPoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoPoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPoActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated
        fn.setVisibleList(false);fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameDeactivated

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        HashMap param=new HashMap();
        param.put("noPo", txtNoPo.getText());
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

        new ReportService(conn, aThis).cetakPO(param);
    }//GEN-LAST:event_btnPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblAlamatSup;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblJtTempo;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtBiayaLain;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtNoPo;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTOP;
    // End of variables declaration//GEN-END:variables

    public void tampilkanData(String id) {
        try {
            String sQry="select h.no_po, h.tanggal, h.supplier_id, coalesce(s.nama_relasi,'') as nama_supplier, "
                    + "coalesce(s.alamat,'')||' - '||coalesce(k.nama_kota,'') as alamat, "
                    + "to_Char(h.tanggal+coalesce(h.top,0), 'dd/MM/yyyy') as jt_tempo, "
                    + "coalesce(h.top,0) as top, coalesce(h.po_disc,'') as ap_disc , coalesce(h.biaya_lain,0) as biaya_lain, "
                    + "coalesce(h.description,'') as description "
                    + "from po h "
                    + "left join m_relasi s on s.id_relasi=h.supplier_id "
                    + "left join m_kota k on k.id=s.id_kota "
                    + "where h.no_po='"+sNoPo+"' ";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                sNoPo=rs.getString("no_po");
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                txtSupplier.setText(rs.getString("supplier_id"));
                lblSupplier.setText("nama_supplier");
                lblAlamatSup.setText(rs.getString("alamat"));
                txtTOP.setText(rs.getString("top"));
                lblJtTempo.setText(rs.getString("jt_tempo"));
                txtKeterangan.setText(rs.getString("description"));
                txtDisc.setText(rs.getString("ap_disc"));
                txtBiayaLain.setText(fn.intFmt.format(rs.getInt("biaya_lain")));
                rs.close();
                
                sQry="select d.id_barang, coalesce(i.plu,'') as  plu, i.nama_barang, d.qty, d.unit_price, d.disc, d.ppn, coalesce(i.satuan,'') as satuan,"
                        + "coalesce(d.biaya,0) as biaya, (fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), "
                        + "coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total,  coalesce(d.last_Cost,0) as last_cost, "
                        + "coalesce(d.keterangan,'') as keterangan "
                        + "from po_det d  "
                        + "left join m_item i on i.id=d.id_barang "
                        + "where d.no_po='"+sNoPo +"' "
                        + "order by d.seq";
                System.out.println(sQry);
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                        tblDetail.getRowCount()+1, 
                        rs.getString("plu"), 
                        rs.getString("nama_barang"), 
                        rs.getDouble("qty"),
                        rs.getString("satuan"),
                        rs.getDouble("unit_price"),
                        rs.getString("disc"),
                        rs.getDouble("ppn"),
                        rs.getDouble("biaya"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("last_cost"),
                        rs.getString("keterangan"),
                        rs.getInt("id_barang"), 
                        
                    });
                }
                rs.close();
                if(tblDetail.getRowCount()>0){
                    tblDetail.setRowSelectionInterval(0, 0);
                    tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void setIdPembelian(String noPo) {
        sNoPo=noPo;
    }

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }

    public void setIsNew(boolean b) {
        this.isNew=b;
    }

    private void udfClear() {
        sNoPo=null;
        btnClear.requestFocusInWindow();
        txtBiayaLain.setText("0");
        txtDisc.setText("");
        txtKeterangan.setText("");
        txtSupplier.setText("");
        txtTOP.setText("");
        lblAlamatSup.setText("");
        lblGrandTotal.setText("0");
        lblJtTempo.setText("");
        lblSubTotal.setText("0");
        lblSupplier.setText("");
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
        txtSupplier.requestFocusInWindow();
    }

    public void setNoPo(String s) {
        this.sNoPo=s;
        txtNoPo.setText(s);
    }

    private void setLocationRelativeTo(Object object) {
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
                case KeyEvent.VK_INSERT:{
                    udfLookupItem();
                    
                    break;
                }
                
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow()>=0){
                        int iRow=tblDetail.getSelectedRow();
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
                if(e.getSource().equals(txtTOP)||e.getSource().equals(txtSupplier))
                    setDueDate();
                else if(e.getSource().equals(txtBiayaLain)||e.getSource().equals(txtDisc)){
                    if(e.getSource().equals(txtBiayaLain))
                        ((JTextField)e.getSource()).setText(GeneralFunction.intFmt.format(fn.udfGetDouble(((JTextField)e.getSource()).getText())));
                    
                    setGrandTotal();
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
                        || col == tblDetail.getColumnModel().getColumnIndex("PPn")
                        || col == tblDetail.getColumnModel().getColumnIndex("Qty")
                        || col == tblDetail.getColumnModel().getColumnIndex("Biaya")
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
    
    private void setDueDate(){
        lblJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                getDueDate(jXDatePicker1.getDate(),
                fn.udfGetInt(txtTOP.getText()))));
    }
    
    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }
}
