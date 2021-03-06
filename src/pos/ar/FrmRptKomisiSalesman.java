/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AreaDao;
import com.ustasoft.pos.dao.jdbc.KotaDao;
import com.ustasoft.pos.dao.jdbc.SalesDao;
import com.ustasoft.pos.domain.Area;
import com.ustasoft.pos.domain.Kota;
import com.ustasoft.pos.domain.Sales;
import com.ustasoft.pos.service.ReportService;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmRptKomisiSalesman extends javax.swing.JInternalFrame {

    private GeneralFunction fn = new GeneralFunction();
    ReportService service;
    private Connection conn;
    List lstCustomer = new ArrayList();
    List lstSalesman = new ArrayList();
    List lstKota = new ArrayList();
    List lstGudang = new ArrayList();
    List lstExpedisi = new ArrayList();

    /**
     * Creates new form FrmRptPenjualan
     */
    public FrmRptKomisiSalesman() {
        initComponents();
    }

    public void setConn(Connection con) {
        this.conn = con;
    }

    private void initForm() {
        try {
            fn.setConn(this.conn);
            service = new ReportService(conn, this);

            Timestamp skg = fn.getTimeServer();
            jDateChooserAwal.setDate(skg);
            jDateChooserAwal.setFormats(MainForm.sDateFormat);
            jDateChooserAkhir.setDate(skg);
            jDateChooserAkhir.setFormats(MainForm.sDateFormat);
            jList1.setSelectedIndex(0);

            cmbSalesman.removeAllItems();
            lstSalesman.add("");
            cmbSalesman.addItem("");
            SalesDao sales = new SalesDao(conn);
            for (Sales s : sales.cariSemuaData()) {
                lstSalesman.add(s.getId_sales());
                cmbSalesman.addItem(s.getNama_sales());
            }

        } catch (Exception ex) {
            Logger.getLogger(FrmRptKomisiSalesman.class.getName()).log(Level.SEVERE, null, ex);
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
        cmbSalesman = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jDateChooserAkhir = new org.jdesktop.swingx.JXDatePicker();
        jDateChooserAwal = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        cmbKota = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Laporan Komisi Penjualan");
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
            String[] strings = { "1. Komisi Sales per Faktur", "2. Komisi Sales per Tanggal", "3. Komisi Sales per Faktur - Per Kota Customer", "4. Komisi Sales per Tanggal - Per Kota Customer", "5. Komisi Sales per Faktur - Per Area Customer", "6. Komisi Sales per Tanggal - Per Area Customer" };
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

        jLabel2.setText("Awal :");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 70, 20));

        jLabel3.setText("Sampai :");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, 70, 20));

        jPanel2.add(cmbSalesman, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 75, 220, -1));

        jLabel5.setText("Salesman :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 70, 20));
        jPanel2.add(jDateChooserAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 45, 165, -1));
        jPanel2.add(jDateChooserAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 165, -1));

        jLabel6.setText("Kota : ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 70, 20));

        jPanel2.add(cmbKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 220, -1));

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

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        try {
            cmbKota.removeAllItems();
            if (jList1.getSelectedIndex() == 2 || jList1.getSelectedIndex() == 3) {
                //by Kota
                KotaDao kd = new KotaDao(conn);
                lstKota.add("");
                cmbKota.addItem("");
                for (Kota s : kd.cariSemuaData()) {
                    lstKota.add(s.getId());
                    cmbKota.addItem(s.getNamaKota());
                }
                jLabel6.setText("Kota");
            }
            else if (jList1.getSelectedIndex() == 4 || jList1.getSelectedIndex() == 5) {
                //by Kota
                AreaDao kd = new AreaDao(conn);
                lstKota.add("");
                cmbKota.addItem("");
                for (Area s : kd.cariSemuaData()) {
                    lstKota.add(s.getId());
                    cmbKota.addItem(s.getNama());
                }
                jLabel6.setText("Area");
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmRptKomisiSalesman.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jList1ValueChanged
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    private void udfPreview() {
        HashMap param = new HashMap();
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);
        param.put("tanggal1", fmt.format(jDateChooserAwal.getDate()));
        param.put("tanggal2", fmt.format(jDateChooserAkhir.getDate()));
        param.put("idSales", cmbSalesman.getSelectedIndex() == 0 ? null : (Integer) lstSalesman.get(cmbSalesman.getSelectedIndex()));
        param.put("idKota", cmbKota.getSelectedIndex() == 0 || cmbKota.getSelectedIndex()<0 ? null : (Integer) lstKota.get(cmbKota.getSelectedIndex()));
        param.put("idArea", cmbKota.getSelectedIndex() == 0 || cmbKota.getSelectedIndex()<0 ? null : (Integer) lstKota.get(cmbKota.getSelectedIndex()));

        switch (jList1.getSelectedIndex()) {
            case 0: {
                service.udfPreview(param, "KomisiPerFaktur", null);
                break;
            }
            case 1: {
                service.udfPreview(param, "KomisiPerTanggal", null);
                break;
            }
            case 2: {
                service.udfPreview(param, "KomisiPerFakturKota", null);
                break;
            }
            case 3: {
                service.udfPreview(param, "KomisiPerTanggalKota", null);
                break;
            }
            case 4: {
                service.udfPreview(param, "KomisiPerFakturArea", null);
                break;
            }
            case 5: {
                service.udfPreview(param, "KomisiPerTanggalArea", null);
                break;
            }
            default: {
                break;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbKota;
    private javax.swing.JComboBox cmbSalesman;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateChooserAwal;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
