/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemGroupList.java
 *
 * Created on Feb 18, 2011, 7:46:09 PM
 */

package pos.inventory;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.TransferDao;
import com.ustasoft.pos.domain.ArInvDet;
import com.ustasoft.pos.domain.Transfer;
import com.ustasoft.pos.domain.TransferDetail;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import pos.DlgLookupItem;
import pos.MainForm;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class FrmTransfer extends javax.swing.JInternalFrame {
    private Connection conn;
    private List lstSiteFrom=new ArrayList();
    private List lstSiteTo=new ArrayList();
    private GeneralFunction fn=new GeneralFunction();
    private DlgLookupItem lookupItem =new DlgLookupItem(JOptionPane.getFrameForComponent(this), true);
    private MyKeyListener kListener=new MyKeyListener();
    private boolean isKoreksi=false;
    private ItemDao itemDao;

    private Component aThis;
    private boolean stItemUpd=false;
    private Integer  idTransfer;
    private Object srcForm;
    
    /** Creates new form FrmItemGroupList */
    public FrmTransfer() {
        initComponents();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.removeComboUpDown(cmbSiteFrom);
        fn.removeComboUpDown(cmbSiteTo);
        tblDetail.addKeyListener(kListener);
        txtKeterangan.addKeyListener(kListener);
        jXDatePicker1.addKeyListener(kListener);
        tblDetail.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblDetail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

        txtTransferNo.setEnabled(isKoreksi);
//        jXDatePicker1.setEnabled(false);
        aThis=this;
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblDetail.getColumn("Qty").setCellEditor(cEditor);
        tblDetail.getColumn("Keterangan").setCellEditor(cEditor);
        
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
        
    }

    public void setIsKoreksi(boolean b){
        this.isKoreksi=b;
    }

    private void udfInitForm(){
        tblDetail.getTableHeader().setReorderingAllowed(false);
        //table.getTableHeader().setResizingAllowed(false);
        tblDetail.getColumn("No").setPreferredWidth(40);     //table.getColumn("ProductID").setMinWidth(txtKode.getWidth());   table.getColumn("ProductID").setMaxWidth(txtKode.getWidth());
        tblDetail.getColumn("ProductID").setPreferredWidth(80);     //table.getColumn("ProductID").setMinWidth(txtKode.getWidth());   table.getColumn("ProductID").setMaxWidth(txtKode.getWidth());
        tblDetail.getColumn("Nama Barang").setPreferredWidth(250);   //table.getColumn("Nama Barang").setMinWidth(txtKode.getWidth()); table.getColumn("Nama Barang").setMaxWidth(txtKode.getWidth());
        tblDetail.getColumn("Satuan").setPreferredWidth(80);        //table.getColumn("Satuan").setMinWidth(txtKode.getWidth());      table.getColumn("Satuan").setMaxWidth(txtKode.getWidth());
        tblDetail.getColumn("Qty").setPreferredWidth(60);           //table.getColumn("Qty").setMinWidth(txtKode.getWidth());         table.getColumn("Qty").setMaxWidth(txtKode.getWidth());
        tblDetail.setRowHeight(20);
        try{
            cmbSiteFrom.removeAllItems();   lstSiteFrom.clear();
            cmbSiteTo.removeAllItems();     lstSiteTo.clear();
            ResultSet rs=conn.createStatement().executeQuery("select id, coalesce(nama_gudang,'') as nama_gudang, current_date as skg " +
                    "from m_gudang order by 1");
            while(rs.next()){
                lstSiteFrom.add(rs.getString("id"));
                cmbSiteFrom.addItem(rs.getString("nama_gudang"));
                lstSiteTo.add(rs.getString("id"));
                cmbSiteTo.addItem(rs.getString("nama_gudang"));
                jXDatePicker1.setDate(rs.getDate("skg"));
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());

        }
        itemDao=new ItemDao(conn);
        lookupItem.setConn(conn);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtKeterangan.requestFocus();
            }
        });
        
    }

    
    
    private boolean udfCekBeforeSave(){
        boolean b=true;
        btnSimpan.requestFocusInWindow();
        if(cmbSiteFrom.getSelectedIndex()<0||cmbSiteTo.getSelectedIndex()<0){
            JOptionPane.showMessageDialog(this, "Silakan pilih gudang asal atau gudang tujuan terlebih dulu!");
            cmbSiteFrom.requestFocus();
            return false;
        }
        if(cmbSiteFrom.getSelectedItem().toString().equalsIgnoreCase(cmbSiteTo.getSelectedItem().toString())){
            JOptionPane.showMessageDialog(this, "Gudang asal dan tujuan tidak boleh sama!");
            cmbSiteFrom.requestFocus();
            return false;
        }
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang akan ditransfer masih kosong!");
            return false;
        }
        Double saldo=new Double(0);
        Integer gudang=fn.udfGetInt(lstSiteFrom.get(cmbSiteFrom.getSelectedIndex()));
        int colItemID=tblDetail.getColumnModel().getColumnIndex("ProductID");
        for(int i=0; i<tblDetail.getRowCount(); i++){
            saldo=itemDao.getSaldo(fn.udfGetInt(tblDetail.getValueAt(i, colItemID)), gudang );
            if(saldo<0){
                JOptionPane.showMessageDialog(this, "Saldo item '"+tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Nama Stok")) +"' tidak ada digudang asal!");
                return false;
            }
        }
        
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        try{
            TransferDao dao=new TransferDao();
            dao.setConn(conn);
            conn.setAutoCommit(false);
            Transfer header=new Transfer();
            header.setTanggal(new java.sql.Timestamp(jXDatePicker1.getDate().getTime()));
            header.setDescription(txtKeterangan.getText());
            header.setFromGudang(fn.udfGetInt(lstSiteFrom.get(cmbSiteFrom.getSelectedIndex())));
            header.setToGudang(fn.udfGetInt(lstSiteTo.get(cmbSiteTo.getSelectedIndex())));
            header.setUserIns(header.getTransferID()==null? MainForm.sUserName: header.getUserIns());
            header.setTimeIns(header.getTransferID()==null? fn.getTimeServer(): header.getTimeIns());
            header.setUserUpd(MainForm.sUserName);
            header.setTimeIns(fn.getTimeServer());
            header.setNoBukti(txtTransferNo.getText().equalsIgnoreCase("")? null: txtTransferNo.getText());
            dao.simpanHeader(header);
            txtTransferNo.setText(header.getNoBukti());
            
            List<TransferDetail> detail=new ArrayList<TransferDetail>();
            TableColumnModel col=tblDetail.getColumnModel();

            for(int i=0; i<tblDetail.getRowCount(); i++){
                TransferDetail det=new TransferDetail();
                det.setTransferID(header.getTransferID());
                det.setIdBarang(Integer.parseInt(tblDetail.getValueAt(i, col.getColumnIndex("ProductID")).toString()));
                det.setQty(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty"))));
                det.setKetItem(tblDetail.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                detail.add(det);
            }
        
            
            dao.simpanTransferDetail(detail, idTransfer==null);
            idTransfer= header.getTransferID();
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan data sukses!");
            udfNew();
            txtKeterangan.requestFocus();
            
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println(se.getMessage()+se.getNextException());
            } catch (SQLException ex) {
                Logger.getLogger(FrmTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, se.getMessage()+se.getNextException());
        }
    }

    private void udfNew(){
        txtTransferNo.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);

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
        jLabel2 = new javax.swing.JLabel();
        txtTransferNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbSiteFrom = new javax.swing.JComboBox();
        cmbSiteTo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jXDatePicker1 = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Transfer antar Gudang"); // NOI18N
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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Keterangan :");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 90, 20));

        txtTransferNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTransferNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTransferNo.setName("txtTransferNo"); // NOI18N
        jPanel1.add(txtTransferNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 22));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Dari Gudang :");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 100, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("No. Bukti");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 20));

        cmbSiteFrom.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSiteFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSiteFrom.setName("cmbSiteFrom"); // NOI18N
        jPanel1.add(cmbSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 170, -1));

        cmbSiteTo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSiteTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSiteTo.setName("cmbSiteTo"); // NOI18N
        jPanel1.add(cmbSiteTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, 180, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Ke :");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 40, 20));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jScrollPane2.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 320, 45));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 710, 90));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Transfer Stok");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 30));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "PLU", "Nama Barang", "Qty", "Satuan", "Keterangan", "ProductID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.setName("tblDetail"); // NOI18N
        tblDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblDetail);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 710, 300));

        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.setName("btnSimpan"); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        getContentPane().add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 433, 110, 30));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnCancel.setText("Batal");
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 433, 80, 30));

        setBounds(0, 0, 748, 501);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox cmbSiteFrom;
    private javax.swing.JComboBox cmbSiteTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jXDatePicker1;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtTransferNo;
    // End of variables declaration//GEN-END:variables

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }

    void setIdTransfer(Integer id) {
        this.idTransfer=id;
    }

    void tampilkanData(Integer id) {
        String sQry="select h.id, coalesce(h.no_bukti,'') as no_bukti, h.tanggal, coalesce(h.description,'') as description, "
                + "coalesce(g.nama_gudang,'') as asal, coalesce(g2.nama_gudang,'') as tujuan "
                + "from transfer h "
                + "left join m_gudang g on g.id=h.from_gudang "
                + "left join m_gudang g2 on g2.id=h.to_gudang "
                + "where h.id="+id+" "; 
        System.out.println(sQry);
        try{
            int iRow=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                idTransfer=rs.getInt("id");
                txtTransferNo.setText(rs.getString("no_bukti"));
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                cmbSiteFrom.setSelectedItem(rs.getString("asal"));
                cmbSiteTo.setSelectedItem(rs.getString("tujuan"));
                txtKeterangan.setText(rs.getString("description"));
                
                rs.close();
                sQry="select d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan "
                    + "from transfer_detail d  "
                    + "left join m_item i on i.id=d.id_barang "
                    + "where d.id_transfer="+idTransfer +" "
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
                        rs.getInt("id_barang"), 

                    });
                }
                if(tblDetail.getRowCount()>0){
                    tblDetail.setRowSelectionInterval(0, 0);
                    tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
                }
            }
            rs.close();
        }catch(SQLException se){
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, se);
        }
    }

   public class MyKeyListener extends KeyAdapter {
//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtKode) && txtKode.getText().trim().length()==0)
//                udfClearItem();
////            else if(evt.getSource().equals(txtNoPO)){
////                fn.lookup(evt, new Object[]{null},
////                "select * from fn_r_lookup_no_po_supplier('"+txtSupplier.getText()+"','%"+txtNoPO.getText()+"%') " +
////                "as (\"No PO\" varchar)",
////                txtNoPO.getWidth(), 150);
////            }
//        }
//
//        @Override
//        public void keyTyped(KeyEvent evt){
//            if(evt.getSource().equals(txtQty))
//                fn.keyTyped(evt);
//        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    if(cmbSiteFrom.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Silakan pilih gudang asal terlebih dulu!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        if(!cmbSiteFrom.isFocusOwner())
                            cmbSiteFrom.requestFocusInWindow();
                    }
                    udfLookupItem();
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
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
                    }
                    else{
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
                case KeyEvent.VK_LEFT:{
                    if(tblDetail.getSelectedColumn()==2)
                        tblDetail.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow()>=0){
                        if(tblDetail.getCellEditor()!=null)
                            tblDetail.getCellEditor().stopCellEditing();

                        int iRow[]= tblDetail.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= tblDetail.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblDetail.convertRowIndexToModel(iRow[0]));
                            iRow = tblDetail.getSelectedRows();
                        }
                        tblDetail.clearSelection();

                        if(tblDetail.getRowCount()>0 && rowPalingAtas<tblDetail.getRowCount()){
                            tblDetail.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblDetail.getRowCount()>0)
                                tblDetail.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblDetail.requestFocus();
                        }
                        if(tblDetail.getSelectedRow()>=0){
                            tblDetail.changeSelection(tblDetail.getSelectedRow(), 0, false, false);
                            //cEditor.setValue(table.getValueAt(table.getSelectedRow(), 0).toString());
                        }
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(!fn.isListVisible() && tblDetail.getRowCount()>0 && JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Ustasoft",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
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


    }
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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if(e.getSource() instanceof JTextField){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }else if(e.getSource() instanceof JFormattedTextField){
                    ((JFormattedTextField)e.getSource()).setSelectionStart(0);
                    ((JFormattedTextField)e.getSource()).setSelectionEnd(((JFormattedTextField)e.getSource()).getText().length());
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
            int iRow = tbl.getSelectedRow();
            TableColumnModel col=d1.getTable().getColumnModel();
            
            Double saldo=itemDao.getSaldo(
                fn.udfGetInt(tbl.getValueAt(iRow, col.getColumnIndex("id"))), 
                fn.udfGetInt(lstSiteFrom.get(cmbSiteFrom.getSelectedIndex())) );
//            try {
            if(saldo<=0){
                JOptionPane.showMessageDialog(aThis, "Saldo gudang tidak ada ("+saldo+")");
                tblDetail.requestFocusInWindow();
                return;
            }    

                //Item item=itemDao.cariItemByID(Integer.valueOf(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString()));
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1,
                    tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                    1, 
                    tbl.getValueAt(iRow, col.getColumnIndex("satuan")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("id")).toString(),
                    
                });

                tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                tblDetail.requestFocusInWindow();
                tblDetail.changeSelection(tblDetail.getRowCount()-1, tblDetail.getColumnModel().getColumnIndex("Qty"), false, false);
//            } catch (SQLException ex) {
//                Logger.getLogger(FrmPembelian.class.getName()).log(Level.SEVERE, null, ex);
//            }
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
                if(col==tblDetail.getColumnModel().getColumnIndex("Qty")){
                    Double saldo=itemDao.getSaldo(
                            fn.udfGetInt(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("ProductID"))), 
                            fn.udfGetInt(lstSiteFrom.get(cmbSiteFrom.getSelectedIndex())) );
                    //Double qty=fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Qty")));
                    Double qty=fn.udfGetDouble(((JTextField) text).getText());
                    if(saldo<qty){
                        String msg="Saldo tidak mencukupi!\n"
                                + "Saldo saat ini adalah "+fn.intFmt.format(saldo);

                        JOptionPane.showMessageDialog(aThis, msg);
                        qty=fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Qty")));
                        return saldo>0? qty: 0;
                    }
                    
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
}
