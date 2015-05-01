/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ArInvDao;
import com.ustasoft.pos.dao.jdbc.GudangDao;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.dao.jdbc.UsersDao;
import com.ustasoft.pos.domain.ArInv;
import com.ustasoft.pos.domain.ArInvDet;
import com.ustasoft.pos.domain.Gudang;
import com.ustasoft.pos.domain.Sales;
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
import org.jdesktop.swingx.JXDatePicker;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmPenjualanRetur extends javax.swing.JInternalFrame {
    private Connection conn;
    DlgLookup lookup;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private ItemDao itemDao;
    private ArInvDao invDao=new ArInvDao();
    private SalesDao salesDao;
    MyKeyListener kListener=new MyKeyListener();
    private List<Gudang> gudang;
    private List<Sales> salesman;
    private Integer idRetur=null;
    private Object srcForm;
    private boolean isNew;
    private String userIns;
    private Date timeIns;
    private double totQty=0;
    
    /**
     * Creates new form PenerimaanBarang
     */
    public FrmPenjualanRetur() {
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
        //txtNoRetur.setEnabled(false);
        try {
            aThis=this;
            lookup=new DlgLookup(JOptionPane.getFrameForComponent(this), true);
            fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
            jScrollPane1.addKeyListener(kListener);
            tblDetail.addKeyListener(kListener);
            itemDao=new ItemDao(conn);
            invDao.setConn(conn);
            tblDetail.getColumn("No").setPreferredWidth(40);
            tblDetail.getColumn("ProductID").setPreferredWidth(80);
            tblDetail.getColumn("Nama Barang").setPreferredWidth(200);
            tblDetail.setRowHeight(22);
            jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
            tblDetail.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    double total=0;
                    int iRow=tblDetail.getSelectedRow();
                    TableColumnModel col=tblDetail.getColumnModel();
                    if( e.getColumn()==col.getColumnIndex("Qty")||
                        e.getColumn()==col.getColumnIndex("Biaya")||
                        e.getColumn()==col.getColumnIndex("Harga")
                            ){
                        double  nett=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Harga")))*fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")));
//                                nett=fn.getDiscBertingkat(nett, tblDetail.getValueAt(iRow, col.getColumnIndex("Disc")).toString());
//                                nett=nett*(1+fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("PPn")))/100);
                                nett+=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Biaya")));
                                
                                tblDetail.setValueAt(nett, iRow, col.getColumnIndex("Sub Total"));
                    }
                    
                    int colSubTotal=tblDetail.getColumnModel().getColumnIndex("Sub Total");
                    int colQty= tblDetail.getColumnModel().getColumnIndex("Qty");
                    double qtyTot=0;
                    
                    for(int i=0; i<tblDetail.getRowCount(); i++){
                        total+=GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, colSubTotal));
                        qtyTot+=GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, colQty));
                        
                    }
                    lblSubTotal.setText(GeneralFunction.intFmt.format(total));
                    totQty=qtyTot;
                    setGrandTotal();
                }
            });
            MyTableCellEditor cEditor=new MyTableCellEditor();
            tblDetail.getColumn("Qty").setCellEditor(cEditor);
            tblDetail.getColumn("Harga").setCellEditor(cEditor);
//            tblDetail.getColumn("Disc").setCellEditor(cEditor);
//            tblDetail.getColumn("PPn").setCellEditor(cEditor);
            tblDetail.getColumn("Biaya").setCellEditor(cEditor);
            tblDetail.getColumn("Keterangan").setCellEditor(cEditor);
            
            cmbGudang.removeAllItems();
            gudang=new GudangDao(conn).cariSemuaData();
            for(Gudang gd : gudang){
                cmbGudang.addItem(gd.getNama_gudang());
            }
            cmbSales.removeAllItems();
            salesman=new SalesDao(conn).cariSemuaData();
            for(Sales s : salesman){
                cmbSales.addItem(s.getNama_sales());
            }
            if(idRetur!=null){
                tampilkanData(idRetur);
                btnPrint.setEnabled(true);
            }
            
            
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(disiapkan_oleh,'') as disiapkan_oleh, "
                    + "coalesce(dibuat_oleh,'') as dibuat_oleh, coalesce(diperiksa_oleh,'') as diperiksa_oleh, "
                    + "coalesce(ket_pembayaran,'') as ket_pembayaran "
                    + "from m_setting_penjualan");
            if(rs.next()){
                txtDisiapkanOleh.setText(rs.getString("disiapkan_oleh"));
                if(rs.getString("dibuat_oleh").equalsIgnoreCase("$user_name")){
                    UsersDao daoUser=new UsersDao(conn);
                    txtDibuatOleh.setText(daoUser.cariByID(MainForm.sUserId).getCompleteName());
                }else{
                    txtDibuatOleh.setText(rs.getString("dibuat_oleh"));
                }
                txtDiperiksaOleh.setText(rs.getString("diperiksa_oleh"));
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        jLabel6.setText(getTitle());
        cmbStatus.setSelectedItem("KREDIT");
    }
    
    private void setGrandTotal(){
        double gTotal=GeneralFunction.udfGetDouble(lblSubTotal.getText());
        double discRp=0;
        if(txtDisc.getText().trim().length()>0){
            gTotal=fn.getDiscBertingkat(gTotal, txtDisc.getText());
            discRp=GeneralFunction.udfGetDouble(lblSubTotal.getText())- gTotal;
            lblDiscRp.setText(fn.intFmt.format(discRp));
        }
//        gTotal+=GeneralFunction.udfGetDouble(txtBiayaLain.getText());        
        lblGrandTotal.setText(fn.intFmt.format(gTotal));
    }
    
    private void udfLookupItem(){
        tblDetail.requestFocus();
        if(txtCustomer.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Masukkan supplier terlebih dulu!");
            txtCustomer.requestFocus();
            return;
        }
        
        if(tblDetail.getRowCount()==0 && txtNoInvoiceJual.getText().length()==0){
            if(JOptionPane.showConfirmDialog(this, "No. Invoice masih kosong? Apakah ini retur dari sebelum tahun 2014?", "No. Invoice", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                JOptionPane.showMessageDialog(this, "No. Invoice harus dimasukkan!");
                txtNoInvoiceJual.requestFocus();
                return;
            }
        }
        
        DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sItem="";
        int colKode=tblDetail.getColumnModel().getColumnIndex("ProductID");
        for(int i=0; i< tblDetail.getRowCount(); i++){
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblDetail.getValueAt(i, colKode).toString()+"'";
        }
        String s="";
        if(txtNoInvoiceJual.getText().equalsIgnoreCase("")){
            s="select * from ("
                    + "select coalesce(i.plu,'') as kode, coalesce(i.nama_barang,'') as nama_barang, 0::double precision as qty, \n" +
                        "coalesce(i.satuan,'') as satuan,\n" +
                        "coalesce(s.price, coalesce(i.harga_jual,0)) as harga, \n" +
                        "0::double precision as biaya, 0 as hpp_terakhir, i.id as id_barang\n" +
                        "from m_item i \n" +
                        "left join m_item_price_by_customer s on s.id_barang=i.id and s.id_customer=1 "
                        +(sItem.length()>0? "where i.id::varchar not in("+sItem+")":"")+" "
                        +" order by coalesce(i.nama_barang,'')"
                    + ")x ";
        }else{    
            s="select *  from (" +
                "select * from fn_ar_retur_lookup("+fn.udfGetInt(lblIDFaktur.getText()) +") as (kode varchar, nama_barang varchar, "
                + "qty double precision, satuan varchar, harga double precision, biaya double precision, "
                + "hpp_terakhir double precision, id_barang integer) "
                +(sItem.length()>0? "where id_barang::varchar not in("+sItem+")":"")+" "
                + "order by nama_barang "
                + ")x " ;
        }
        System.out.println(s);
        d1.setTitle("Lookup Stok");
        JTable tbl=d1.getTable();
        d1.udfLoad(conn, s, "(x.id_barang||x.nama_barang)", null);
        tbl.getColumn("id_barang").setPreferredWidth(0);
        tbl.getColumn("id_barang").setMinWidth(0);
        tbl.getColumn("id_barang").setMaxWidth(0);
        d1.setVisible(true);

        if(d1.getKode().length()>0){
//            try {
                TableColumnModel col=d1.getTable().getColumnModel();
                
                int iRow = tbl.getSelectedRow();
                double nett=0;
                double harga, biaya, hpp;
                String disc="";
                
//                HargaItem view=itemDao.getHargaJual(
//                        Integer.valueOf(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString()), 
//                        fn.udfGetInt(txtCustomer.getText()), 
//                        salesman.get(cmbSales.getSelectedIndex()).getId_sales());
                harga=fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("harga")));
                biaya=fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("biaya")));
//                disc=view.getDisc()==null? "0": view.getDisc();
//                ppn=view.getPpn()==null? 0: view.getPpn();
                
                nett=fn.getDiscBertingkat(harga, disc)+biaya;
//                nett=nett*(1+ppn/100);
                hpp=txtNoInvoiceJual.getText().equalsIgnoreCase("")? 
                        itemDao.getPrevCost(fn.udfGetInt(tbl.getValueAt(iRow, col.getColumnIndex("id_barang")))):
                        fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("hpp_terakhir")));
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1,
                    tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                    fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty"))), 
                    tbl.getValueAt(iRow, col.getColumnIndex("satuan")).toString(),
                    harga, 
                    biaya,
                    nett,
                    hpp,
                    "",
                    tbl.getValueAt(iRow, col.getColumnIndex("id_barang")).toString(),
                    null
                });

                tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                tblDetail.requestFocusInWindow();
                tblDetail.changeSelection(tblDetail.getRowCount()-1, tblDetail.getColumnModel().getColumnIndex("Qty"), false, false);
//            } catch (SQLException ex) {
//                Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }
    
    private Double getPrevCost(Integer id){
        Double hasil=new Double(0);
        try {
            String sQry="select fn_item_hist_get_prevcost_sales("+id+")";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                hasil=rs.getDouble(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    
    
    private boolean udfCekBeforeSave(){
        try {
            btnSave.requestFocusInWindow();
            if(!btnSave.isEnabled()){
                return false;
            }
            if(txtCustomer.getText().trim().length()==0){
                JOptionPane.showMessageDialog(aThis, "Silahkan pilih nama toko terlebih dulu!");
                txtCustomer.requestFocusInWindow();
                return false;
            }
            if(txtNoInvoiceJual.getText().trim().length()==0){
                if(JOptionPane.showConfirmDialog(aThis, "Nomor Invoice kosong. Anda tetap ingin melanjutkan?", "No. Invoice", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                    txtNoInvoiceJual.requestFocusInWindow();
                    return false;
                }
            }
            if(cmbSales.getSelectedIndex()<0){
                JOptionPane.showMessageDialog(aThis, "Silahkan pilih salesman terlebih dulu!");
                tblDetail.requestFocusInWindow();
                return false;
            }
            if(tblDetail.getRowCount()==0){
                JOptionPane.showMessageDialog(aThis, "Item yang diterima masih belum dimasukkan!");
                txtCustomer.requestFocusInWindow();
                return false;
            }
//            if(cmbStatus.getSelectedItem().toString().equalsIgnoreCase("TUNAI") && fn.udfGetDouble(txtBayar.getText())==0 && 
//                JOptionPane.showConfirmDialog(aThis, "Untuk status TUNAI apakah anda yakin untuk mengosongi pembayaran?", "Pembayaran", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
//                txtBayar.requestFocusInWindow();
//                return false;
//            }
            if(txtNoRetur.getText().trim().length()==0){
                //txtNoRetur.setText(invDao.getNoInvoice("RJL"));
                JOptionPane.showMessageDialog(aThis, "Silahkan masukkan nomor bukti retur terlebih dulu!");
                txtNoRetur.requestFocusInWindow();
                return false;
            }
//            if(totQty>=invDao.getQtyTotal(fn.udfGetInt(lblIDFaktur.getText()))){
                double discPenjualan=0, discRetur=0;
                String arDisc="", returDisc="";
                String sQry="select * from fn_ar_nett("+fn.udfGetInt(lblIDFaktur.getText()) +", "
                        + idRetur+") "
                        + "as (nett double precision, disc_rp double precision, ar_disc varchar, "
                        + "ret_nett double precision, ret_disc_rp double precision, retur_disc varchar)";
                System.out.println("");
                ResultSet rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    discPenjualan=rs.getDouble("disc_rp");
                    discRetur=rs.getDouble("ret_disc_rp");
                    arDisc=rs.getString("ar_disc");
                    returDisc=returDisc.length()>0? "\n": "" + rs.getString("retur_disc");
                }
                rs.close();
                
                discRetur+=discRetur+fn.udfGetDouble(lblDiscRp.getText()); 
                if(discPenjualan>discRetur){
                    if(JOptionPane.showConfirmDialog(this, "Informasi diskon:.\n"
                            + "- Diskon penjualan adalah '"+arDisc+"' \n"
                            + "- Diskon retur sebelumnya adalah : '"+returDisc+"'", "Diskon", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                        txtDisc.requestFocusInWindow();
                        return false;
                        
                    }
                }
//            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualanRetur.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private void udfSave(){
        if(!udfCekBeforeSave())
            return;
        try{
            Timestamp timeServer=fn.getTimeServer();
            ArInv header = new ArInv();
            header.setId(idRetur);
            header.setCustomerId(Integer.parseInt(txtCustomer.getText()));
            header.setInvoiceNo(txtNoRetur.getText());
            header.setNoSo("");
            header.setDescription(txtKeterangan.getText());
            header.setInvoiceDate(jXDatePicker1.getDate());
            header.setJenisBayar(cmbStatus.getSelectedItem().toString().substring(0, 1));
            header.setTop(0);
            header.setInvoiceAmount(-fn.udfGetDouble(lblGrandTotal.getText()));
            header.setItemAmount(-fn.udfGetDouble(lblSubTotal.getText()));
            header.setPaidAmount(new Double(0));
            header.setOwing(-fn.udfGetDouble(lblGrandTotal.getText())); //-fn.udfGetDouble(txtBayar.getText())
            header.setIdGudang(gudang.get(cmbGudang.getSelectedIndex()).getId());
            header.setArDisc(txtDisc.getText());
            header.setBiayaLain(new Double(0));//fn.udfGetDouble(txtBiayaLain.getText()));
            header.setTimeIns(idRetur==null? new Date(timeServer.getTime()): timeIns);
            header.setUserIns(idRetur==null? MainForm.sUserName: userIns);
            header.setTimeUpd(new Date(timeServer.getTime()));
            header.setUserUpd(MainForm.sUserName);
            header.setIdSales(salesman.get(cmbSales.getSelectedIndex()).getId_sales());
            header.setIdExpedisi(null);
            header.setKetPembayaran("");
            header.setDisiapkanOleh(txtDisiapkanOleh.getText());
            header.setDiperiksaOleh(txtDiperiksaOleh.getText());
            header.setDiterimaOleh(txtDiterimaOleh.getText());
            header.setTrxType("RJL");
            header.setReturDari(fn.udfGetInt(lblIDFaktur.getText()));
            
            conn.setAutoCommit(false);
            invDao.simpanHeader(header);
            
            idRetur=header.getId();
            List<ArInvDet> detail=new ArrayList<ArInvDet>();
            TableColumnModel col=tblDetail.getColumnModel();

            for(int i=0; i<tblDetail.getRowCount(); i++){
                ArInvDet det=new ArInvDet();
                det.setArId(header.getId());
                det.setItemId(Integer.parseInt(tblDetail.getValueAt(i, col.getColumnIndex("ProductID")).toString()));
                det.setQty(-GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty"))));
                det.setUnitPrice(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Harga"))));
//                det.setDisc(tblDetail.getValueAt(i, col.getColumnIndex("Disc")).toString());
//                det.setPpn(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("PPn"))));
                det.setDisc("");
                det.setPpn(new Double(0));
                det.setLastCost(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Hpp Terakhir"))));
                det.setBiaya(GeneralFunction.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Biaya"))));
                det.setKeterangan(tblDetail.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                det.setDetId(tblDetail.getValueAt(i, col.getColumnIndex("ID"))==null? null: GeneralFunction.udfGetInt(tblDetail.getValueAt(i, col.getColumnIndex("ID"))));
                detail.add(det);
            }
        
            
            invDao.simpanArInvDetail(detail, idRetur==null);
            idRetur= header.getId();
            
            conn.setAutoCommit(true);
            if(srcForm!=null){
//                if(srcForm instanceof FrmPembelianHis){
//                    if(!isNew)
//                        {((FrmPembelianHis)srcForm).udfRefreshDataPerBaris(header.getId());}
//                    else
//                        {((FrmPembelianHis)srcForm).udfLoadData(header.getId());}
//                }
            }
            JOptionPane.showMessageDialog(this, "Retur penjualan tersimpan!");
            udfPrintRetur();
            btnPrint.setEnabled(true);
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println(se.getMessage()+"lanjut:"+se.getNextException());
                if(se.getMessage().indexOf("Key (invoice_no)=("+txtNoInvoiceJual.getText()+") already exists")>0){
                    JOptionPane.showMessageDialog(this, "Nomor invoice '"+txtNoInvoiceJual.getText()+"' telah terpakai!\n"
                            + "Silahkan masukkan nomor lain!");
                    txtNoInvoiceJual.requestFocus();
                    return;
                }
                JOptionPane.showMessageDialog(this, se.getMessage()+se.getNextException());
            } catch (SQLException ex) {
                Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel22 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel23 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cmbSales = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        txtNoRetur = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblCustomer = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        lblAlamat = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        lblKota = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblHP = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        txtNoInvoiceJual = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        lblIDFaktur = new javax.swing.JLabel();
        lblTglTrx = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblSubTotal = new javax.swing.JLabel();
        txtDisc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        lblDiscRp = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtDibuatOleh = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtDisiapkanOleh = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtDiperiksaOleh = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtDiterimaOleh = new javax.swing.JTextField();
        panelHeader1 = new com.ustasoft.component.panelHeader();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Retur Penjualan");
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

        jLabel22.setText("Status :");
        jPanel4.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbStatus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStatusItemStateChanged(evt);
            }
        });
        jPanel4.add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 160, -1));
        jPanel4.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, -1));

        jLabel23.setText("Tanggal Retur :");
        jPanel4.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel17.setText("Salesman :");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        cmbSales.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel4.add(cmbSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 160, -1));

        jLabel18.setText("No. Bukti :");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNoRetur.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoRetur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoReturKeyReleased(evt);
            }
        });
        jPanel4.add(txtNoRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 170, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 280, 120));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Kota :");
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 90, 20));

        lblCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 410, 20));

        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel5.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, 20));

        lblAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 470, 20));

        jLabel8.setText("Gudang");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 60, 20));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel5.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 170, -1));

        lblKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 170, 20));

        jLabel3.setText("Nama Toko :");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel9.setText("Alamat :");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

        jLabel10.setText("HP :");
        jPanel5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 60, 20));

        lblHP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(lblHP, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 170, 20));

        jLabel27.setText("Keterangan :");
        jPanel5.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 70, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 105, 470, 20));

        txtNoInvoiceJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoInvoiceJual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoInvoiceJualActionPerformed(evt);
            }
        });
        txtNoInvoiceJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoInvoiceJualKeyReleased(evt);
            }
        });
        jPanel5.add(txtNoInvoiceJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 100, 20));

        jLabel24.setText("No. Faktur Jual:");
        jPanel5.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 90, 20));

        lblIDFaktur.setText("0");
        jPanel5.add(lblIDFaktur, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 80, 25, 20));
        jPanel5.add(lblTglTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, 90, 20));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 575, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 870, 140));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "Nama Barang", "Qty", "Satuan", "Harga", "Biaya", "Sub Total", "Hpp Terakhir", "Keterangan", "ProductID", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true, true, false, false, true, false, false
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 199, 870, 180));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 5, 140, 20));

        txtDisc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscKeyTyped(evt);
            }
        });
        jPanel2.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 25, 180, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Disc :");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, 100, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Sub Total :");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 100, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("GRAND TOTAL :");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 20));

        lblGrandTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGrandTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 140, 20));

        lblDiscRp.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDiscRp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDiscRp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 25, 140, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 380, 390, 80));

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
        jPanel3.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, 100, 30));

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
        jPanel3.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 90, 30));

        jLabel4.setBackground(new java.awt.Color(204, 255, 255));
        jLabel4.setForeground(new java.awt.Color(0, 0, 153));
        jLabel4.setText("<html>\n &nbsp <b>Del &nbsp &nbsp &nbsp : </b> &nbsp  Menghapus item pembelian  <br>\n &nbsp <b>Insert : </b> &nbsp Menambah ItemTransaski<br>\n</html>"); // NOI18N
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setOpaque(true);
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 220, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 461, 870, 50));
        getContentPane().add(txtDibuatOleh, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 440, 110, -1));

        jLabel19.setText("Sales");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, 70, 20));
        getContentPane().add(txtDisiapkanOleh, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 120, -1));

        jLabel20.setBackground(new java.awt.Color(204, 204, 204));
        jLabel20.setText("Faktur Dibuat Oleh:");
        jLabel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel20.setOpaque(true);
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 420, 110, 20));

        jLabel21.setBackground(new java.awt.Color(204, 204, 204));
        jLabel21.setText("Barang Disiapkan Oleh:");
        jLabel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel21.setOpaque(true);
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 120, 20));

        jLabel25.setBackground(new java.awt.Color(204, 204, 204));
        jLabel25.setText("Diperiksa Oleh:");
        jLabel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel25.setOpaque(true);
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 420, 120, 20));
        getContentPane().add(txtDiperiksaOleh, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 440, 120, -1));

        jLabel26.setBackground(new java.awt.Color(204, 204, 204));
        jLabel26.setText("Diterima Oleh:");
        jLabel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel26.setOpaque(true);
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 420, 120, 20));
        getContentPane().add(txtDiterimaOleh, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 440, 120, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 34)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Retur Penjualan");

        javax.swing.GroupLayout panelHeader1Layout = new javax.swing.GroupLayout(panelHeader1);
        panelHeader1.setLayout(panelHeader1Layout);
        panelHeader1Layout.setHorizontalGroup(
            panelHeader1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeader1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelHeader1Layout.setVerticalGroup(
            panelHeader1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeader1Layout.createSequentialGroup()
                .addGap(0, 21, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(panelHeader1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        setBounds(0, 0, 901, 546);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        String sQry="select id_relasi::varchar as id, coalesce(nama_relasi,'') as nama_Customer, "
                + "alamat||' - '||kota.nama_kota as alamat, coalesce(s.top,0) as top "
                + "from m_relasi s "
                + "left join m_kota kota on kota.id=s.id_kota "
                + "where id_relasi::varchar||coalesce(s.nama_relasi,'')||alamat||' - '||kota.nama_kota "
                + "ilike '%"+txtCustomer.getText()+"%' order by 2" ;
                        fn.lookup(evt, new Object[]{lblCustomer, lblAlamat}, sQry, txtCustomer.getWidth()+lblCustomer.getWidth(), 300);
    }//GEN-LAST:event_txtCustomerKeyReleased

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

    private void txtNoInvoiceJualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoInvoiceJualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoInvoiceJualActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameDeactivated
        fn.setVisibleList(false);fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameDeactivated

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        udfPrintRetur();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void txtNoReturKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoReturKeyReleased
//        fn.lookup(evt, null, "select no_so from so where customer_id='"+txtCustomer.getText()+"' and not closed "
//                + "and no_so ilike '%"+txtNoRetur.getText()+"%' order by 1", txtNoRetur.getWidth()+18, 150);
        
    }//GEN-LAST:event_txtNoReturKeyReleased

    
    
    private void cmbStatusItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStatusItemStateChanged
//        try {
//            txtTOP.setText(cmbStatus.getSelectedIndex()==0||txtCustomer.getText().equalsIgnoreCase("") ? "0": 
//                    new CustomerDao(conn).cariByID(Integer.parseInt(txtCustomer.getText())).getTop().toString()
//                    );
//            setDueDate();
//        } catch (Exception ex) {
//            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_cmbStatusItemStateChanged

    private void txtNoInvoiceJualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoInvoiceJualKeyReleased
        String sQry="select coalesce(invoice_no,'') as no_faktur, to_char(invoice_date, 'dd/MM/yyyy') as tgl_invoice, id "
                + "from ar_inv where trx_type ='PJL' "
                + "and id_customer="+fn.udfGetInt(txtCustomer.getText()) +" "
                + "and coalesce(invoice_no,'') ilike '%"+txtNoInvoiceJual.getText()+"%' "
                + " order by coalesce(invoice_no,'')";
        fn.lookup(evt, new Object[]{lblTglTrx, lblIDFaktur}, sQry, txtNoInvoiceJual.getWidth()+lblTglTrx.getWidth()+ 20, 150);
    }//GEN-LAST:event_txtNoInvoiceJualKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbSales;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDiscRp;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblHP;
    private javax.swing.JLabel lblIDFaktur;
    private javax.swing.JLabel lblKota;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTglTrx;
    private com.ustasoft.component.panelHeader panelHeader1;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDibuatOleh;
    private javax.swing.JTextField txtDiperiksaOleh;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtDisiapkanOleh;
    private javax.swing.JTextField txtDiterimaOleh;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNoInvoiceJual;
    private javax.swing.JTextField txtNoRetur;
    // End of variables declaration//GEN-END:variables

    public void tampilkanData(Integer id) {
        try {
            String sQry="select h.id, h.invoice_date as tanggal, h.id_customer, coalesce(c.nama_relasi,'') as nama_customer, "
                    + "coalesce(c.alamat,'')||' - '||coalesce(k.nama_kota,'') as alamat, "
                    + "coalesce(h.invoice_no,'') as invoice_no, to_Char(h.invoice_date+coalesce(h.top,0), 'dd/MM/yyyy') as jt_tempo, "
                    + "coalesce(h.top,0) as top, coalesce(h.ar_disc,'') as ar_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
                    + "coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description, "
                    + "coalesce(h.disiapkan_oleh,'') as disiapkan_oleh, coalesce(ret.invoice_no,'') as no_retur_dari, "
                    + "coalesce(h.diperiksa_oleh,'') as diperiksa_oleh, "
                    + "coalesce(h.diterima_oleh,'') as diterima_oleh, "
                    + "coalesce(u.complete_name,'') as dibuat_oleh, "
                    + "coalesce(s.nama_sales,'') as nama_sales,"
                    + "coalesce(h.id_expedisi::varchar,'') as id_expedisi, "
                    + "coalesce(e.nama_expedisi,'') as nama_expedisi, "
                    + "coalesce(ket_pembayaran,'') as ket_pembayaran, "
                    + "coalesce(h.user_ins,'') as user_ins, h.time_ins "
                    + "from ar_inv h "
                    + "left join m_relasi c on c.id_relasi=h.id_customer "
                    + "left join m_kota k on k.id=c.id_kota "
                    + "left join m_gudang g on g.id=h.id_gudang "
                    + "left join m_salesman s on s.id_sales=h.id_sales "
                    + "left join m_Expedisi e on h.id_expedisi = e.id "
                    + "left join m_user u on u.user_name=h.user_ins "
                    + "left join ar_inv ret on ret.id=h.retur_dari "
                    + "where h.id="+id+" ";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                idRetur=rs.getInt("id");
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                txtCustomer.setText(rs.getString("id_customer"));
                txtNoRetur.setText(rs.getString("invoice_no"));
                txtNoInvoiceJual.setText(rs.getString("no_retur_dari"));
                lblCustomer.setText("nama_customer");
                lblAlamat.setText(rs.getString("alamat"));
//                txtTOP.setText(rs.getString("top"));
//                lblJtTempo.setText(rs.getString("jt_tempo"));
//                txtExpedisi.setText(rs.getString("id_expedisi"));
//                lblExpedisi.setText(rs.getString("nama_expedisi"));
                cmbSales.setSelectedItem(rs.getString("nama_sales"));
                txtKeterangan.setText(rs.getString("description"));
                
                txtDisiapkanOleh.setText(rs.getString("disiapkan_oleh"));
                txtDiperiksaOleh.setText(rs.getString("diperiksa_oleh"));
                txtDiterimaOleh.setText(rs.getString("diterima_oleh"));
                txtDibuatOleh.setText(rs.getString("dibuat_oleh"));
                
                txtDisc.setText(rs.getString("ar_disc"));
                //txtBiayaLain.setText(fn.intFmt.format(rs.getInt("biaya_lain")));
//                txtBayar.setText(fn.intFmt.format(rs.getInt("paid_amount")));
                userIns=rs.getString("user_ins");
                timeIns=rs.getDate("time_ins");
                rs.close();
                
                sQry="select d.ar_id, d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, d.unit_price, d.disc, d.ppn, coalesce(i.satuan,'') as satuan,"
                        + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), "
                        + "coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total, coalesce(d.last_Cost,0) as last_cost, "
                        + "coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan, d.det_id "
                        + "from ar_inv_det d  "
                        + "left join m_item i on i.id=d.id_barang "
                        + "where d.ar_id="+id +" "
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
                        rs.getDouble("unit_price"),
//                        rs.getString("disc"),
//                        rs.getDouble("ppn"),
                        rs.getDouble("biaya"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("last_cost"),
                        rs.getString("keterangan"),
                        rs.getInt("id_barang"), 
                        rs.getInt("det_id")
                    });
                }
                rs.close();
                if(tblDetail.getRowCount()>0){
                    tblDetail.setRowSelectionInterval(0, 0);
                    tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void setIdPenjualan(Integer id) {
        idRetur=id;
    }

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }

    void setIsNew(boolean b) {
        this.isNew=b;
    }

    private void udfClear() {
        idRetur=null;
        btnClear.requestFocusInWindow();
        txtCustomer.setText("");
        lblCustomer.setText("");
        lblAlamat.setText("");
        lblKota.setText("");
        lblHP.setText("");
        txtNoInvoiceJual.setText("");
        lblIDFaktur.setText("");
//        txtBayar.setText("0");
        txtNoRetur.setText("");
        txtDisc.setText("");
        lblDiscRp.setText("");
        lblGrandTotal.setText("0");
        lblSubTotal.setText("0");
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
        txtCustomer.requestFocusInWindow();
    }

    private void udfPrintRetur() {
        HashMap param=new HashMap();
        param.put("idRetur", idRetur);
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

        new ReportService(conn, aThis).cetakReturPenjualan(param);
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
                        if(iRow<0)
                            return;
                        
                        ((DefaultTableModel)tblDetail.getModel()).removeRow(iRow);
                        if(tblDetail.getRowCount()<0)
                            return;
                        if(tblDetail.getRowCount()>iRow){
                            tblDetail.setRowSelectionInterval(iRow, iRow);
                        }else{
                            if(tblDetail.getRowCount()>0){
                                tblDetail.setRowSelectionInterval(0, 0);
                            }
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
                if(e.getSource().equals(txtNoInvoiceJual))
                    udfLoadArInv();
                else if(e.getSource().equals(txtDisc)){ //||e.getSource().equals(txtBayar)
//                    if(e.getSource().equals(txtBayar))
//                        ((JTextField)e.getSource()).setText(GeneralFunction.intFmt.format(fn.udfGetDouble(((JTextField)e.getSource()).getText())));
                    
                    if(e.getSource().equals(txtDisc))
                        {setGrandTotal();}
                }else if(e.getSource().equals(txtNoRetur)){
                    udfLoadItemFromSO();
                }
           }
        }
    } ;

    private void udfLoadArInv(){
        if(lblIDFaktur.getText().length()==0)
            return;
        try {
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(s.nama_sales,'') as nama_sales "
                    + "from ar_inv i "
                    + "inner join m_salesman s on s.id_sales=i.id_sales "
                    + "where i.id="+lblIDFaktur.getText());
            if(rs.next()){
                cmbSales.setSelectedItem(rs.getString(1));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualanRetur.class.getName()).log(Level.SEVERE, null, ex);
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
                if (col == tblDetail.getColumnModel().getColumnIndex("Harga")
                        || col == tblDetail.getColumnModel().getColumnIndex("Biaya")
                        || col == tblDetail.getColumnModel().getColumnIndex("Qty")
                        ) {
                    if(col==tblDetail.getColumnModel().getColumnIndex("Qty") && txtNoInvoiceJual.getText().length() > 0){
                        Double sisa=invDao.getSisaRetur(
                                fn.udfGetInt(lblIDFaktur.getText()),
                                fn.udfGetInt(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("ProductID"))), 
                                idRetur==null? -1: idRetur);
                        //Double qty=fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Qty")));
                        Double qty=fn.udfGetDouble(((JTextField) text).getText());
                        if(sisa<qty){
                            String msg="Sisa yang bisa diretur tidak mencukupi!\n"
                                    + "Sisa saat ini adalah "+fn.intFmt.format(sisa);
                                    
                            JOptionPane.showMessageDialog(aThis, msg);
                            qty=fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Qty")));
                            return sisa>0? qty: 0;
                        }
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
    
//    private void setDueDate(){
//        lblJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
//                getDueDate(jXDatePicker1.getDate(),
//                fn.udfGetInt(txtTOP.getText()))));
//    }
    
    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void udfLoadItemFromSO(){
        String sQry="select so.customer_id, coalesce(c.nama_relasi,'') as nama_customer, coalesce(c.alamat,'')||coalesce(' - ', k.nama_kota,'') as alamat,  "
                + "coalesce(c.top,0) as top, coalesce(so.so_disc,'') as so_disc, coalesce(so.biaya_lain,0) as biaya_lain  "
                + "from so  "
                + "inner join m_relasi c on c.id_relasi=so.customer_id "
                + "left join m_kota k on k.id=c.id_kota "
                + "where no_so='"+txtNoRetur.getText()+"'";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtCustomer.setText(rs.getString("customer_id"));
                lblCustomer.setText(rs.getString("nama_customer"));
                lblAlamat.setText(rs.getString("alamat"));
//                txtTOP.setText(rs.getString("top"));
                txtDisc.setText(rs.getString("so_disc"));
                //txtBiayaLain.setText(fn.intFmt.format(rs.getDouble("biaya_lain")));
                
                rs.close();
                
                sQry="select d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan, "
                + "d.unit_price, d.disc, d.ppn, coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan,"
                + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total "
                + "from so_det d  "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.no_so='"+txtNoRetur.getText() +"' "
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
                    rs.getDouble("unit_price"),
//                    rs.getString("disc"),
//                    rs.getDouble("ppn"),
                    rs.getDouble("biaya"),
                    rs.getDouble("sub_Total"),
                    getPrevCost(rs.getInt("id_barang")),
                    rs.getString("keterangan"),
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
