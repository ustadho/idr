/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ap;

import pos.ar.*;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.JDesktopImage;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.service.ReportService;
import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import pos.MainForm;
import pos.ap.*;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class FrmApPaymentHis extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktop;
    ReportService reportService;
    
    /**
     * Creates new form FrmPenerimaanStokHis
     */
    public FrmApPaymentHis() {
        initComponents();
        jXDatePicker1.setFormats(MainForm.sDateFormat);
        jXDatePicker2.setFormats(MainForm.sDateFormat);
    }

    public void setConn(Connection con){
        this.conn=con;
    }
    
    public void setDesktop(JDesktopImage d){
        this.desktop=d;
    }
    
    MenuAuth menuAuth;
    private void udfInitForm(){
        aThis=this;
        
        for(int i=0; i<tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new com.ustasoft.component.MyRowRenderer());
        }
        udfLoadData("");
        reportService =new ReportService(conn, aThis);
        tblHeader.setRowHeight(20);
        
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Daftar Pembayaran Pelanggan", MainForm.sUserName);
        if(menuAuth!=null){
            btnBaru.setEnabled(menuAuth.canInsert());
            btnEdit.setEnabled(menuAuth.canUpdate());
            btnHapus.setEnabled(menuAuth.canDelete());
        }
    }
    
    public void udfLoadData(String arNo){
        String sQry="select * from fn_ap_payment_list("
                + "'"+GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate())+"', "
                + "'"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate())+"', "
                + "'"+txtCari.getText()+"') as (ap_no varchar, tanggal date, nama_supplier varchar, \n" +
                    "check_amount double precision, no_bukti varchar, keterangan text, acc_name varchar, no_penjualan text) ";
        System.out.println(sQry);
        ((DefaultTableModel)tblHeader.getModel()).setNumRows(0);
        try{
            int iRow=0;
            double total=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
                    rs.getString("ap_no"),        //1
                    rs.getDate("tanggal"),  //2
                    rs.getString("nama_supplier"),  //3
                    rs.getDouble("check_amount"),      //4
                    rs.getString("no_bukti"),        //5  
                    rs.getString("keterangan"),    //6
                    rs.getString("acc_name"),    //7
                    rs.getString("no_penjualan"),    //8
                    
                });
                total+=rs.getDouble("check_amount");
                if(arNo.equalsIgnoreCase(rs.getString("ar_no"))){
                    iRow=tblHeader.getRowCount()-1;
                }
            }
            lblTotal.setText(GeneralFunction.intFmt.format(total));
            rs.close();
            if(tblHeader.getRowCount()>0){
                tblHeader.setRowSelectionInterval(iRow, iRow);
                tblHeader.setModel(fn.autoResizeColWidth(tblHeader, ((DefaultTableModel)tblHeader.getModel())).getModel());
            }
        }catch(SQLException se){
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, se);
        }
    }
    
    private void udfEdit(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String arNo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. Trx")).toString();
        FrmApPayment f1=new FrmApPayment();
        f1.setConn(conn);
        f1.setTitle("Faktu Penjualan - Edit");
        MainForm.jDesktopImage2.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setArReceiptNo(arNo);
        f1.setSrcForm(aThis);
        f1.setVisible(true);
    }
    
    private void udfSuratJalan(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        Integer id=fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")));
        FrmSJ f1=new FrmSJ();
        f1.setConn(conn);
        f1.setTitle("Surat Jalan");
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        //f1.tampilkanData(id);
        f1.setIdPenjualan(id);
        f1.setSrcForm(aThis);
        f1.setVisible(true);
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
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnKeluar = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Pembayaran Supplier");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Mulai :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 60, 20));
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 20, 125, -1));

        jLabel2.setText("Pencarian :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, 70, 20));
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 125, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/arrow_refresh.png"))); // NOI18N
        jButton1.setText("Tampilkan");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 20, 120, -1));

        jLabel3.setText("Sampai :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 60, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 230, 20));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Trx", "Tanggal", "Supplier", "Total Pembayaran", "No. Bukti", "Keterangan", "Akun", "No. Pembelian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblHeader);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        jPanel2.add(btnKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 10, 90, -1));

        btnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        btnBaru.setText("Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruActionPerformed(evt);
            }
        });
        jPanel2.add(btnBaru, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, -1));

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/pencil.png"))); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel2.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 90, -1));

        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 90, -1));

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 90, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Total : ");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 60, 20));

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0");
        jPanel2.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 130, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        setBounds(0, 0, 952, 543);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfLoadData("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        udfNew();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        udfDelete();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        int iRow=tblHeader.getSelectedRow();
        HashMap param=new HashMap();
        //            String sSubRpt="", sGambar="";
//            sGambar=getClass().getResource("/resources/siloam_bw.jpg").toString();
//            sSubRpt=getClass().getResource("Reports/").toString();
        Integer id=Integer.parseInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")).toString());    
        String trxType=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString();    
        
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

//            param.put("sGambar", sGambar);
//            param.put("SUBREPORT_DIR", sSubRpt);
            //param.put("SUBREPORT_DIR", getClass().getResourceAsStream("Reports"));
        if(trxType.toUpperCase().indexOf("RETUR") >=0){
            param.put("idRetur", id);
            reportService.cetakReturPenjualan(param);
        }else{
            param.put("idPenjualan", id);
            reportService.previewPenjualan(param);
        }
            
    }//GEN-LAST:event_btnPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

    public void udfRefreshDataPerBaris(Integer id) {
        String sQry="select id, tanggal, nama_customer, invoice_no, jt_tempo, sub_total, ar_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ar_disc)+biaya_lain as nett, paid_amount, "
                + "(fn_get_Disc_Bertingkat(sub_total, ar_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description "
                + "from("
                + "	select h.id, h.invoice_date as tanggal, coalesce(s.nama_relasi,'') as nama_customer, coalesce(h.invoice_no,'') as invoice_no,"
                + "     coalesce(s.nama_sales,'') as nama_sales, h.invoice_date+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.ar_disc,'') as ar_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
                + "     coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description "
                + "	from ap_inv h "
                + "	inner join ap_inv_det d on d.ap_id=h.id "
                + "	left join m_relasi c on c.id_relasi=h.id_customer "
                + "     left join m_gudang g on g.id=h.id_gudang "
                + "     left join m_salesman s on s.id_sales=h.id_sales "
                + "	where h.id="+id+" "
                + "	group by h.id, h.invoice_date, coalesce(s.nama_relasi,''), coalesce(h.invoice_no,''), "
                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ar_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(g.nama_gudang,''), coalesce(h.description,'') "
                + ")x "
                + "order by id ";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            TableColumnModel col=tblHeader.getColumnModel();
            int iRow=tblHeader.getSelectedRow();
            if(rs.next()){
                tblHeader.setValueAt(rs.getDate("tanggal"), iRow, col.getColumnIndex("Tanggal"));
                tblHeader.setValueAt(rs.getString("nama_customer"), iRow, col.getColumnIndex("Customer"));
                tblHeader.setValueAt(rs.getString("invoice_no"), iRow, col.getColumnIndex("Invoice#"));
                tblHeader.setValueAt(rs.getString("nama_sales"), iRow, col.getColumnIndex("Salesman"));
                tblHeader.setValueAt(rs.getDate("jt_tempo"), iRow, col.getColumnIndex("Jatuh Tempo"));
                tblHeader.setValueAt(rs.getDouble("sub_total"), iRow, col.getColumnIndex("Sub Total"));
                tblHeader.setValueAt(rs.getString("ar_disc"), iRow, col.getColumnIndex("Disc"));
                tblHeader.setValueAt(rs.getDouble("biaya_lain"), iRow, col.getColumnIndex("Biaya Lain"));
                tblHeader.setValueAt(rs.getDouble("nett"), iRow, col.getColumnIndex("Nett"));
                tblHeader.setValueAt(rs.getDouble("paid_amount"), iRow, col.getColumnIndex("Terbayar"));
                tblHeader.setValueAt(rs.getDouble("hutang"), iRow, col.getColumnIndex("Hutang"));
                tblHeader.setValueAt(rs.getString("nama_gudang"), iRow, col.getColumnIndex("Gudang"));
                tblHeader.setValueAt(rs.getString("description"), iRow, col.getColumnIndex("Keterangan"));
                
                rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        FrmApPayment f1=new FrmApPayment();
        f1.setConn(conn);
        MainForm.jDesktopImage2.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(aThis);
        f1.setIsNew(true);
        f1.setVisible(true);
    }

    private void udfDelete() {
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0 ||!btnHapus.isEnabled()){return;}
        if(JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus data pembayaran ini?", "Hapus data", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            String arNo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. Trx")).toString();
            try {
                int i=conn.createStatement().executeUpdate(
                          "delete from ar_receipt_detail where ar_no='"+arNo+"';\n"
                        + "delete from ar_receipt where ar_no='"+arNo+"';");
                if(i>0){
                    ((DefaultTableModel)tblHeader.getModel()).removeRow(iRow);
                    if(tblHeader.getRowCount()>0){
                        iRow=iRow<tblHeader.getRowCount()? iRow: tblHeader.getRowCount()-1;
                        tblHeader.setRowSelectionInterval(iRow, iRow);
                        tblHeader.changeSelection(iRow, 0, false, false);
                    }
                    JOptionPane.showMessageDialog(this, "Hapus pembayarn sukses!");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmPembelianHis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
