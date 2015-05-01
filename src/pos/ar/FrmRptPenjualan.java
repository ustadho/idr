/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.ExpedisiDao;
import com.ustasoft.pos.dao.jdbc.GudangDao;
import com.ustasoft.pos.dao.jdbc.ItemKategoriDao;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.domain.Expedisi;
import com.ustasoft.pos.domain.Gudang;
import com.ustasoft.pos.domain.ItemKategori;
import com.ustasoft.pos.domain.Relasi;
import com.ustasoft.pos.domain.Sales;
import com.ustasoft.pos.service.ReportService;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmRptPenjualan extends javax.swing.JInternalFrame {
    private GeneralFunction fn=new GeneralFunction();
    ReportService service;
    private Connection conn;
    List lstCustomer=new ArrayList();
    List lstSalesman=new ArrayList();
    List lstKategori=new ArrayList();
    List lstGudang=new ArrayList();
    List lstExpedisi=new ArrayList();
    /**
     * Creates new form FrmRptPenjualan
     */
    public FrmRptPenjualan() {
        initComponents();
        AutoCompleteDecorator.decorate(cmbGudang);
        AutoCompleteDecorator.decorate(cmbCustomer);
        AutoCompleteDecorator.decorate(cmbExpedisi);
        AutoCompleteDecorator.decorate(cmbSalesman);
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, null);
        fn.addKeyListenerInContainer(jPanel2, kListener, null);
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }

    private void initForm(){
        try {
            fn.setConn(this.conn);
            service=new ReportService(conn, this);
            
            Timestamp skg=fn.getTimeServer();
            jDateChooserAwal.setDate(skg); jDateChooserAwal.setFormats(MainForm.sDateFormat);
            jDateChooserAkhir.setDate(skg);jDateChooserAkhir.setFormats(MainForm.sDateFormat);
            jList1.setSelectedIndex(0);
            
            cmbGudang.removeAllItems();
            lstGudang.add("");
            cmbGudang.addItem("");
            GudangDao dao=new GudangDao(conn);
            for(Gudang gudang: dao.cariSemuaData()){
                lstGudang.add(gudang.getId());
                cmbGudang.addItem(gudang.getNama_gudang());
            }
            
            cmbCustomer.removeAllItems();
            lstCustomer.add("");
            cmbCustomer.addItem("");
            RelasiDao rel=new RelasiDao(conn);
            for(Relasi relasi: rel.cariSemuaData(0)){
                lstCustomer.add(relasi.getIdRelasi());
                cmbCustomer.addItem(relasi.getNamaRelasi());
            }
            
            cmbSalesman.removeAllItems();
            lstSalesman.add("");
            cmbSalesman.addItem("");
            SalesDao sales=new SalesDao(conn);
            for(Sales s:sales.cariSemuaData()){
                lstSalesman.add(s.getId_sales());
                cmbSalesman.addItem(s.getNama_sales());
            }
            
            cmbKategori.removeAllItems();
            lstKategori.add("");
            cmbKategori.addItem("");
            ItemKategoriDao kat=new ItemKategoriDao(conn);
            for(ItemKategori s:kat.cariSemuaKategori()){
                lstKategori.add(s.getId());
                cmbKategori.addItem(s.getNamaKategori());
            }
            
            cmbExpedisi.removeAllItems();
            lstExpedisi.add("");
            cmbExpedisi.addItem("");
            ExpedisiDao expedisi=new ExpedisiDao(conn);
            for(Expedisi e:expedisi.cariSemuaData()){
                lstExpedisi.add(e.getId());
                cmbExpedisi.addItem(e.getNama_expedisi());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmRptPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class MyKeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent evt) {
            if(evt.getKeyCode()==KeyEvent.VK_DELETE && (
                    evt.getSource()==cmbCustomer ||
                    evt.getSource()==cmbExpedisi ||
                    evt.getSource()==cmbGudang ||
                    evt.getSource()==cmbSalesman
                    ) ){
                ((JComboBox)evt.getSource()).setSelectedIndex(0);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbCustomer = new javax.swing.JComboBox();
        cmbSalesman = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbExpedisi = new javax.swing.JComboBox();
        jDateChooserAkhir = new org.jdesktop.swingx.JXDatePicker();
        jDateChooserAwal = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        cmbKriteria = new javax.swing.JComboBox();
        txtLimit = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Laporan Penjualan");
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

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1.  Penjualan per Tanggal", "2.  Rekap Penjualan per Tanggal", "3.  Rincian Penjualan per Customer", "4.  Rekap Penjualan per Customer", "5.  Detail Penjualan per Salesman", "6.  Rekap Penjualan per Salesman", "7.  Detail Penjualan per Gudang", "8.  Rekap Penjualan per Gudang", "9.  Detail Profit Penjualan per Faktur", "10.Rekap Profit Penjualan per Tanggal", "11.Detail Pengiriman Expedisi per Tanggal", "12.Rekap Pengiriman Expedisi per Tanggal", "13.Penjualan Stok Tertinggi", "14.Penjualan Stok Terendah" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Gudang :");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 80, 20));

        jLabel2.setText("Awal :");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, 20));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 130, -1));

        jLabel3.setText("Sampai :");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 80, 20));

        jLabel4.setText("Pelanggan");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 80, 20));

        cmbCustomer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 220, -1));

        cmbSalesman.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbSalesman, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 95, 220, -1));

        jLabel5.setText("Salesman :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 95, 80, 20));

        jLabel6.setText("Urut berdasar : ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 100, 20));

        cmbExpedisi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbExpedisi, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 145, 220, -1));
        jPanel2.add(jDateChooserAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 45, 165, -1));
        jPanel2.add(jDateChooserAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 165, -1));

        jLabel7.setText("Expedisi :");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 145, 80, 20));

        cmbKriteria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Qty", "Harga", "Profit", "% Profit" }));
        jPanel2.add(cmbKriteria, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, 135, -1));

        txtLimit.setText("50");
        txtLimit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtLimit, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 50, 20));

        jLabel8.setText("Baris");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 170, -1, 20));

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Qty", "Harga", "Profit", "% Profit" }));
        jPanel2.add(cmbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 195, 220, -1));

        jLabel15.setText("Katergori Item : ");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 195, 100, 20));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 330, 225));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        jButton1.setText("Tampilkan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 245, 105, 30));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        jButton2.setText("Tutup");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 245, 90, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        setBounds(0, 0, 646, 334);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfPreview();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm();;
    }//GEN-LAST:event_formInternalFrameOpened

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        int idx=jList1.getSelectedIndex();
        cmbKategori.setEnabled(idx==12||idx==13);
    }//GEN-LAST:event_jList1ValueChanged
    SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
    
    private void udfPreview(){
        HashMap param=new HashMap();
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);
        param.put("tanggal1", fmt.format(jDateChooserAwal.getDate()));
        param.put("tanggal2", fmt.format(jDateChooserAkhir.getDate()));
        param.put("kriteria", cmbKriteria.getSelectedItem().toString());
        param.put("namaGudang", cmbGudang.getSelectedItem().toString());
        param.put("idCustomer", cmbCustomer.getSelectedIndex()==0? null: (Integer)lstCustomer.get(cmbCustomer.getSelectedIndex()));
        param.put("namaCustomer", cmbCustomer.getSelectedIndex()==0? "Semua Pelanggan": cmbCustomer.getSelectedItem().toString());
        param.put("idSales", cmbSalesman.getSelectedIndex()==0? null: (Integer)lstSalesman.get(cmbSalesman.getSelectedIndex()));
        param.put("idGudang", cmbGudang.getSelectedIndex()==0? null: (Integer)lstGudang.get(cmbGudang.getSelectedIndex()));
        param.put("idExpedisi", cmbExpedisi.getSelectedIndex()==0? null: (Integer)lstExpedisi.get(cmbExpedisi.getSelectedIndex()));
        param.put("idKategori", cmbKategori.getSelectedIndex()==0? null: (Integer)lstKategori.get(cmbKategori.getSelectedIndex()));
        param.put("limit", fn.udfGetInt(txtLimit.getText()));

        switch(jList1.getSelectedIndex()){
            case 0:{
                service.udfPreview(param, "PenjualanDetail", null);       break;
            }
            case 1:{
                service.udfPreview(param, "PenjualanRekap", null);        break;
            }
            case 2:{
                service.udfPreview(param, "PenjualanDetailCust", null);   break;
            }
            case 3:{
                service.udfPreview(param, "PenjualanRekapCust", null);    break;
            }
            case 4:{
                service.udfPreview(param, "PenjualanDetailSales", null);    break;
            }
            case 5:{
                service.udfPreview(param, "PenjualanRekapSales", null);    break;
            }
            case 6:{
                service.udfPreview(param, "PenjualanDetailGudang", null);    break;
            }
            case 7:{
                service.udfPreview(param, "PenjualanRekapGudang", null);    break;
            }
            case 8:{
                service.udfPreview(param, "PenjualanDetailProfit", null);    break;
            }
            case 9:{
                service.udfPreview(param, "PenjualanRekapProfitPerTanggal", null);    break;
            }
            case 10:{
                service.udfPreview(param, "PenjualanDetailExpedisi", null);    break;
            }
            case 11:{
                service.udfPreview(param, "PenjualanRekapExpedisi", null);    break;
            }
            case 12:{
                param.put("urut", "DESC");
                service.udfPreview(param, "PenjualanFastSlow", null);    break;
            }
            default:{
                param.put("urut", "ASC");
                service.udfPreview(param, "PenjualanFastSlow", null);    break;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCustomer;
    private javax.swing.JComboBox cmbExpedisi;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JComboBox cmbKriteria;
    private javax.swing.JComboBox cmbSalesman;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtLimit;
    // End of variables declaration//GEN-END:variables
}
