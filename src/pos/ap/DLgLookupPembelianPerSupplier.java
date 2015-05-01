/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pos.ap;

import com.ustasoft.component.GeneralFunction;
import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class DLgLookupPembelianPerSupplier extends java.awt.Dialog {
    private Connection conn;
    private Integer idRelasi;
    private GeneralFunction fn=new GeneralFunction();
    private Component aThis;
    private Integer apId;
    private Integer idRetur;
    
    /**
     * Creates new form DLgLookupNota
     */
    public DLgLookupPembelianPerSupplier(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblHeader.getSelectedRow();
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                if(conn==null ||!aThis.isVisible()||tblHeader.getSelectedRow()<0)
                    return;
                udfLoadItemDetail();
                        
            }
        });
        setLocationRelativeTo(null);
    }
    
    public void setConn(Connection c){
        this.conn=c;
        fn.setConn(conn);
    }
    
    public void setIdRelasi(Integer id){
        this.idRelasi=id;
    }
    
    public Integer getSelectedId(){
        return this.apId;
    }
    
    public void udfLoadData(){
        String sQry="";
        if(apId==null){
            sQry="select id, tanggal, trx_type, nama_supplier, invoice_no, jt_tempo, sub_total, ap_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain as nett, paid_amount, "
                + "(fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description "
                + "from("
                + "	select h.id, h.invoice_date as tanggal, coalesce(t.keterangan,'') as trx_type, coalesce(s.nama_relasi,'') as nama_supplier, coalesce(h.invoice_no,'') as invoice_no, h.invoice_date+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.ap_disc,'') as ap_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
                + "     coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description "
                + "	from ap_inv h "
                + "	inner join ap_inv_det d on d.ap_id=h.id "
                + "     inner join m_item i on i.id=d.id_barang "
                + "	left join m_relasi s on s.id_relasi=h.supplier_id "
                + "     left join m_gudang g on g.id=h.id_gudang "
                + "     left join m_trx_type t on t.kode=h.trx_type "
                + "	where h.supplier_id="+idRelasi+" and h.trx_type='PBL' "
                + "     and coalesce(h.invoice_no,'')||coalesce(h.description,'') ilike '%"+txtCari.getText()+"%' "
                + "     and d.ap_id in(select ap_id from ap_inv_det d "
                + "     where d.id_barang in(select id_barang from ap_inv_det where ap_id="+this.idRetur+") ) "
                + "	group by h.id, h.invoice_date, coalesce(s.nama_relasi,''), coalesce(h.invoice_no,''), "
                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ap_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(g.nama_gudang,''), coalesce(h.description,''), coalesce(t.keterangan,'') "
                + ")x "
                + "order by tanggal, id ";
        }else{
            btnPilih.setVisible(false);
            sQry="select id, tanggal, trx_type, nama_supplier, invoice_no, jt_tempo, sub_total, ap_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain as nett, paid_amount, "
                + "(fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description "
                + "from("
                + "	select h.id, h.invoice_date as tanggal, coalesce(t.keterangan,'') as trx_type, coalesce(s.nama_relasi,'') as nama_supplier, coalesce(h.invoice_no,'') as invoice_no, h.invoice_date+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.ap_disc,'') as ap_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
                + "     coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description "
                + "	from ap_inv h "
                + "	inner join ap_inv_det d on d.ap_id=h.id "
                + "     inner join m_item i on i.id=d.id_barang "
                + "	left join m_relasi s on s.id_relasi=h.supplier_id "
                + "     left join m_gudang g on g.id=h.id_gudang "
                + "     left join m_trx_type t on t.kode=h.trx_type "
                + "	where retur_dari ="+apId+" "
                + "	group by h.id, h.invoice_date, coalesce(s.nama_relasi,''), coalesce(h.invoice_no,''), "
                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ap_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(g.nama_gudang,''), coalesce(h.description,''), coalesce(t.keterangan,'') "
                + ")x "
                + "order by tanggal, id ";
        }
        System.out.println(sQry);
        ((DefaultTableModel)tblHeader.getModel()).setNumRows(0);
        try{
            int iRow=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
                    rs.getInt("id"),        //1
                    rs.getDate("tanggal"),  //2
                    rs.getString("trx_type"),  //3
                    rs.getString("nama_supplier"),  //3
                    rs.getString("invoice_no"),     //4
                    rs.getDate("jt_tempo"),         //5
                    rs.getDouble("sub_total"),      //6
                    rs.getString("ap_disc"),        //7    
                    rs.getDouble("biaya_lain"),     //8
                    rs.getDouble("nett"),           //9
                    rs.getDouble("paid_amount"),    //10
                    rs.getDouble("hutang"),         //11
                    rs.getString("nama_gudang"),    //12
                    rs.getString("description"),    //13
                });
                if(apId!=null && apId==rs.getInt("id")){
                    iRow=tblHeader.getRowCount()-1;
                }
            }
            rs.close();
            if(tblHeader.getRowCount()>0){
                tblHeader.setRowSelectionInterval(iRow, iRow);
                tblHeader.setModel(fn.autoResizeColWidth(tblHeader, ((DefaultTableModel)tblHeader.getModel())).getModel());
            }
        }catch(SQLException se){
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, se);
        }
    }

    private void udfLoadItemDetail(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String sQry="select d.ap_id, d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan, "
                + "d.unit_price, d.disc, d.ppn, coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan,"
                + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total "
                + "from ap_inv_det d  "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.ap_id="+fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"))) +" "
                + "order by d.seq";
        System.out.println(sQry);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount()+1, 
                    rs.getInt("id_barang"), 
                    rs.getString("plu"), 
                    rs.getString("nama_barang"), 
                    rs.getDouble("qty"),
                    rs.getString("satuan"),
                    rs.getDouble("unit_price"),
                    rs.getString("disc"),
                    rs.getDouble("ppn"),
                    rs.getDouble("biaya"),
                    rs.getDouble("sub_Total"),
                    rs.getString("keterangan")
                });
            }
            if(tblDetail.getRowCount()>0){
                tblDetail.setRowSelectionInterval(0, 0);
                tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnPilih = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID#", "Tanggal", "Trx Type", "Supplier", "Invoice#", "Jatuh Tempo", "Sub Total", "Disc", "Biaya Lain", "Nett", "Terbayar", "Hutang", "Gudang", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ProductID", "Kode", "Nama Barang", "Qty", "Satuan", "Harga", "Disc", "PPn", "Biaya", "Sub Total", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblDetail);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Pencarian");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 10, 90, 25));
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 10, 280, 25));

        jButton1.setText("Cari");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 110, 25));

        btnPilih.setText("Pilih");
        btnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihActionPerformed(evt);
            }
        });
        jPanel1.add(btnPilih, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, 150, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        setBounds(0, 0, 699, 431);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfLoadData();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihActionPerformed
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        this.apId=(Integer)tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"));
        this.dispose();
    }//GEN-LAST:event_btnPilihActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        aThis=this;
        udfLoadData();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DLgLookupPembelianPerSupplier dialog = new DLgLookupPembelianPerSupplier(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPilih;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

    void setIdRetur(Integer integer) {
        this.idRetur=integer;
    }

    public Integer getApId() {
        return apId;
    }

    public void setApId(Integer apId) {
        this.apId = apId;
    }
    
    
}
