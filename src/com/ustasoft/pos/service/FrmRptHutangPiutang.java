/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import pos.ar.*;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.domain.Relasi;
import com.ustasoft.pos.domain.Sales;
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
public class FrmRptHutangPiutang extends javax.swing.JInternalFrame {
    private GeneralFunction fn=new GeneralFunction();
    ReportService service;
    private Connection conn;
    List lstCustomer=new ArrayList();
    List lstSalesman=new ArrayList();
    List lstGudang=new ArrayList();
    List lstExpedisi=new ArrayList();
    /**
     * Creates new form FrmRptPenjualan
     */
    public FrmRptHutangPiutang() {
        initComponents();
        AutoCompleteDecorator.decorate(cmbCustomer);
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
            
            cmbCustomer.removeAllItems();
            lstCustomer.add("");
            cmbCustomer.addItem("");
            RelasiDao rel=new RelasiDao(conn);
            for(Relasi relasi: rel.cariSemuaData(-1)){
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
            
        } catch (Exception ex) {
            Logger.getLogger(FrmRptPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class MyKeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent evt) {
            if(evt.getKeyCode()==KeyEvent.VK_DELETE && (
                    evt.getSource()==cmbCustomer ||
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbCustomer = new javax.swing.JComboBox();
        cmbSalesman = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jDateChooserAkhir = new org.jdesktop.swingx.JXDatePicker();
        jDateChooserAwal = new org.jdesktop.swingx.JXDatePicker();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Laporan Hutang - Piutang");
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
            String[] strings = { "1. Tanda Terima Nota Tagihan", "2. Kartu Piutang per Pelanggan per Tanggal", "3. Kartu Hutang per Supplier per Tanggal", "4. Detail Penerimaan Piutang Per Pelanggan", "5. Detail Pembayaran Hutang  Per Supplier" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Awal :");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 70, 20));

        jLabel3.setText("Sampai :");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, 70, 20));

        jLabel4.setText("Pelanggan");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 70, 20));

        cmbCustomer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 220, -1));

        cmbSalesman.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        jPanel2.add(cmbSalesman, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 95, 220, -1));

        jLabel5.setText("Salesman :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 95, 70, 20));
        jPanel2.add(jDateChooserAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 45, 165, -1));
        jPanel2.add(jDateChooserAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 165, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 330, 200));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        jButton1.setText("Tampilkan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 225, 105, 30));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        jButton2.setText("Tutup");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 225, 90, 30));

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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        setBounds(0, 0, 646, 316);
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
        param.put("idCustomer", cmbCustomer.getSelectedIndex()==0? null: (Integer)lstCustomer.get(cmbCustomer.getSelectedIndex()));
        param.put("idSupplier", cmbCustomer.getSelectedIndex()==0? null: (Integer)lstCustomer.get(cmbCustomer.getSelectedIndex()));
        param.put("idPelanggan", cmbCustomer.getSelectedIndex()==0? null: (Integer)lstCustomer.get(cmbCustomer.getSelectedIndex()));
        param.put("namaCustomer", cmbCustomer.getSelectedIndex()==0? "Semua Pelanggan": cmbCustomer.getSelectedItem().toString());
        param.put("namaPelanggan", cmbCustomer.getSelectedIndex()==0? "Semua Pelanggan": cmbCustomer.getSelectedItem().toString());
        param.put("namaSupplier", cmbCustomer.getSelectedIndex()==0? "Semua Pelanggan": cmbCustomer.getSelectedItem().toString());
        param.put("idSales", cmbSalesman.getSelectedIndex()==0? null: (Integer)lstSalesman.get(cmbSalesman.getSelectedIndex()));
        

        switch(jList1.getSelectedIndex()){
            case 0:{
                service.udfPreview(param, "KartuPiutangLandscape", null);    break;
            }
            case 1:{
                service.cetakKartuPiutang(param);break;
            }
            case 2:{
                service.cetakKartuHutang(param);break;
            }
            case 3:{
                service.udfPreview(param, "ArReceiptDetail", null);    break;
            }
            case 4:{
                service.udfPreview(param, "ApPaymentDetail", null);    break;
            }
            
            default:{
                break;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCustomer;
    private javax.swing.JComboBox cmbSalesman;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAwal;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
