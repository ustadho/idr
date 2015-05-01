/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import pos.ap.*;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import pos.MainForm;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class FrmSOHis extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktop;
    ReportService reportService;
    
    /**
     * Creates new form FrmPenerimaanStokHis
     */
    public FrmSOHis() {
        initComponents();
        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblHeader.getSelectedRow();
                btnEdit.setEnabled(iRow>=0);
                btnHapus.setEnabled(iRow>=0);
                btnPrint.setEnabled(iRow>=0);
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                if(conn==null ||!aThis.isVisible()||tblHeader.getSelectedRow()<0)
                    return;
                udfLoadItemDetail();
                        
            }
        });
    }

    public void setConn(Connection con){
        this.conn=con;
    }
    
    public void setDesktop(JDesktopImage d){
        this.desktop=d;
    }
    
    private void udfInitForm(){
        aThis=this;
        for(int i=0; i<tblDetail.getColumnCount(); i++){
            tblDetail.getColumnModel().getColumn(i).setCellRenderer(new com.ustasoft.component.MyRowRenderer());
        }
        for(int i=0; i<tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new com.ustasoft.component.MyRowRenderer());
        }
        udfLoadData("");
        udfLoadItemDetail();
        reportService =new ReportService(conn, aThis);
        tblHeader.setRowHeight(20);
        tblDetail.setRowHeight(20);
        
        MenuAuth menuAuth= new MenuAuthDao(conn).getMenuByUsername("Daftar SO", MainForm.sUserName);
        if(menuAuth!=null){
            btnBaru.setEnabled(menuAuth.canInsert());
            btnEdit.setEnabled(menuAuth.canUpdate());
            btnHapus.setEnabled(menuAuth.canDelete());
        }
    }
    
    private void udfLoadItemDetail(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String sQry="select d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan, "
                + "d.unit_price, d.disc, d.ppn, coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan,"
                + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total "
                + "from so_det d  "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.no_so='"+tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. SO")).toString() +"' "
                + "order by d.seq";
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
    
    public void udfLoadData(String sNoSo){
        String sQry="select no_so, tanggal, nama_customer, jt_tempo, sub_total, so_disc, biaya_lain, "
                + "fn_get_Disc_Bertingkat(sub_total, so_disc)+biaya_lain as nett, description "
                + "from("
                + "	select h.no_so, h.tanggal as tanggal, coalesce(s.nama_relasi,'') as nama_customer, h.tanggal+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.so_disc,'') as so_disc , coalesce(h.biaya_lain,0) as biaya_lain, "
                + "     coalesce(h.description,'') as description "
                + "	from so h "
                + "	inner join so_det d on d.no_so=h.no_so "
                + "	left join m_relasi s on s.id_relasi=h.customer_id "
                + "	where to_char(h.tanggal, 'yyyy-MM-dd')>='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate()) +"' "
                + "	and to_char(h.tanggal, 'yyyy-MM-dd')<='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate()) +"' "
                + "     and coalesce(s.nama_relasi,'')||coalesce(h.description,'') ilike '%"+txtCari.getText()+"%' "
                + "	group by h.no_so, h.tanggal, coalesce(s.nama_relasi,''), "
                + "     h.tanggal+coalesce(h.top,0), coalesce(h.so_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(h.description,'') "
                + ")x "
                + "order by no_so ";
        
        System.out.println(sQry);
        ((DefaultTableModel)tblHeader.getModel()).setNumRows(0);
        try{
            int iRow=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
                    rs.getString("no_so"),        //1
                    rs.getDate("tanggal"),  //2
                    rs.getString("nama_customer"),  //3
                    rs.getDate("jt_tempo"),         //5
                    rs.getDouble("sub_total"),      //6
                    rs.getString("so_disc"),        //7    
                    rs.getDouble("biaya_lain"),     //8
                    rs.getDouble("nett"),           //9
                    rs.getString("description"),    //13
                });
                if(sNoSo.equalsIgnoreCase(rs.getString("no_so"))){
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
    
    private void udfEdit(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String sNoSo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. SO")).toString();
        FrmSO f1=new FrmSO();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        //f1.tampilkanData(id);
        f1.setNoPo(sNoSo);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnKeluar = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Pesanan Penjualan");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Mulai :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 60, 20));
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, -1, -1));

        jLabel2.setText("Pencarian :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, 70, 20));
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/arrow_refresh.png"))); // NOI18N
        jButton1.setText("Tampilkan");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 100, -1));

        jLabel3.setText("Sampai :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 60, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 230, 20));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. SO", "Tanggal", "Pelanggan", "Jatuh Tempo", "Sub Total", "Disc", "Biaya Lain", "Nett", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        jPanel2.add(btnKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, 90, -1));

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-952)/2, (screenSize.height-543)/2, 952, 543);
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
        cetakSO();
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

    public void udfRefreshDataPerBaris(String sNoSo) {
        String sQry="select no_so, tanggal, nama_supplier, jt_tempo, sub_total, so_disc, biaya_lain, "
                + "fn_get_Disc_Bertingkat(sub_total, so_disc)+biaya_lain as nett, description "
                + "from("
                + "	select h.no_so, h.tanggal as tanggal, coalesce(s.nama_relasi,'') as nama_supplier, h.tanggal+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.so_disc,'') as so_disc , coalesce(h.biaya_lain,0) as biaya_lain, "
                + "     coalesce(h.description,'') as description "
                + "	from so h "
                + "	inner join so_det d on d.no_so=h.no_so "
                + "	left join m_relasi s on s.id_relasi=h.supplier_id "
                + "	where h.no_so='"+sNoSo+"' "
                + "     and coalesce(s.nama_supplier,'')||coalesce(h.description,'') ilike '%"+txtCari.getText()+"%' "
                + "	group by h.no_so, h.tanggal, coalesce(s.nama_relasi,''), "
                + "     h.tanggal+coalesce(h.top,0), coalesce(h.so_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(h.description,'') "
                + ")x "
                + "order by no_so ";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            TableColumnModel col=tblHeader.getColumnModel();
            int iRow=tblHeader.getSelectedRow();
            if(rs.next()){
                tblHeader.setValueAt(rs.getDate("tanggal"), iRow, col.getColumnIndex("Tanggal"));
                tblHeader.setValueAt(rs.getString("nama_supplier"), iRow, col.getColumnIndex("Pelanggan"));
                tblHeader.setValueAt(rs.getString("invoice_no"), iRow, col.getColumnIndex("Invoice#"));
                tblHeader.setValueAt(rs.getDate("jt_tempo"), iRow, col.getColumnIndex("Jatuh Tempo"));
                tblHeader.setValueAt(rs.getDouble("sub_total"), iRow, col.getColumnIndex("Sub Total"));
                tblHeader.setValueAt(rs.getString("ap_disc"), iRow, col.getColumnIndex("Disc"));
                tblHeader.setValueAt(rs.getDouble("biaya_lain"), iRow, col.getColumnIndex("Biaya Lain"));
                tblHeader.setValueAt(rs.getDouble("nett"), iRow, col.getColumnIndex("Nett"));
                tblHeader.setValueAt(rs.getDouble("paid_amount"), iRow, col.getColumnIndex("Terbayar"));
                tblHeader.setValueAt(rs.getDouble("hutang"), iRow, col.getColumnIndex("Hutang"));
                tblHeader.setValueAt(rs.getString("nama_gudang"), iRow, col.getColumnIndex("Gudang"));
                tblHeader.setValueAt(rs.getString("description"), iRow, col.getColumnIndex("Keterangan"));
                
                rs.close();
                udfLoadItemDetail();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        FrmSO f1=new FrmSO();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(aThis);
        f1.setIsNew(true);
        f1.setVisible(true);
    }

    private boolean udfCekBeforeDelete(){
        try {
            int iRow=tblHeader.getSelectedRow();
            String sNoPo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. PO")).toString();
                
            ResultSet rs=conn.createStatement().executeQuery("select * from ap_inv where no_po='"+sNoPo+"'");
            if(rs.next()){
                rs.close();
                JOptionPane.showMessageDialog(this, "Nomor PO ini tidak bisa dihapus karena telah dipakai di Penerimaan barang!");
                return false;
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPOHis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private void udfDelete() {
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0 ||!btnHapus.isEnabled()||!udfCekBeforeDelete()){return;}
        if(JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus data pembelian ini?", "Hapus data", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            String sNoPo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. PO")).toString();
            try {
                int i=conn.createStatement().executeUpdate(
                          "delete from po_Det where no_po='"+sNoPo+"';\n"
                        + "delete from po where no_po='"+sNoPo+"';");
                if(i>0){
                    ((DefaultTableModel)tblHeader.getModel()).removeRow(iRow);
                    if(tblHeader.getRowCount()>0){
                        iRow=iRow<tblHeader.getRowCount()? iRow: tblHeader.getRowCount()-1;
                        tblHeader.setRowSelectionInterval(iRow, iRow);
                        tblHeader.changeSelection(iRow, 0, false, false);
                    }
                    JOptionPane.showMessageDialog(this, "Hapus PO sukses!");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmPOHis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void cetakSO() {
        int iRow=tblHeader.getSelectedRow();
        HashMap param=new HashMap();
        param.put("noSo", tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. SO")).toString());
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);
        reportService.cetakSO(param);
    }
}
