/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ar;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.JDesktopImage;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.service.PrintTrxService;
import com.ustasoft.pos.service.ReportService;
import java.awt.Component;
import java.awt.event.KeyEvent;
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
import javax.swing.table.TableColumnModel;
import pos.MainForm;
import pos.ap.*;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class FrmPenjualanHis extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktop;
    ReportService reportService;
    MenuAuth menuAuth;
    
    /**
     * Creates new form FrmPenerimaanStokHis
     */
    public FrmPenjualanHis() {
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
        udfLoadData(0);
        udfLoadItemDetail();
        reportService =new ReportService(conn, aThis);
        tblHeader.setRowHeight(20);
        tblDetail.setRowHeight(20);
        
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Daftar Penjualan", MainForm.sUserName);
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
        String sQry="select d.ar_id, d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan, "
                + "d.unit_price, d.disc, d.ppn, coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan,"
                + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total, "
                + "coalesce(h.close_id,'')<>'' as sudah_closing "
                + "from ar_inv_det d "
                + "inner join ar_inv h on h.id=d.ar_id "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.ar_id="+fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"))) +" "
                + "order by d.seq";
        System.out.println(sQry);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            
            boolean ada=false, sudahClosing=false;
            while(rs.next()){
                ada=true;
                sudahClosing=rs.getBoolean("sudah_closing");
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
            btnEdit.setEnabled(ada && menuAuth!=null && menuAuth.canUpdate());
            btnHapus.setEnabled(ada && menuAuth!=null && menuAuth.canDelete());
            if(tblDetail.getRowCount()>0){
                
                tblDetail.setRowSelectionInterval(0, 0);
                tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel)tblDetail.getModel())).getModel());
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }
    
    public void udfLoadData(Integer apId){
        String sQry="select distinct id, trx_type, tanggal, nama_customer, invoice_no, nama_sales, jt_tempo, sub_total, ar_disc, biaya_lain, "
                + "fn_get_Disc_Bertingkat(sub_total, ar_disc)+biaya_lain as nett, paid_amount, "
                + "(fn_get_Disc_Bertingkat(sub_total, ar_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description, nama_expedisi, "
                + "nama_area "
                + "from(	"
                + "	select h.id, coalesce(t.keterangan,'') as trx_type, h.invoice_date as tanggal, coalesce(c.nama_relasi,'') as nama_customer, coalesce(h.invoice_no,'') as invoice_no,      "
                + "	coalesce(s.nama_sales,'') as nama_sales, h.invoice_date+coalesce(h.top,0) as jt_tempo, 	"
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, 	"
                + "	coalesce(h.ar_disc,'') as ar_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount,      "
                + "	coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description,"
                + "     coalesce(ex.nama_expedisi,'') as nama_expedisi, coalesce(area.nama,'') as nama_area 	"
                + "	from ar_inv h 	inner join ar_inv_det d on d.ar_id=h.id 	"
                + "     inner join m_item i on i.id=d.id_barang "
                + "	left join m_relasi c on c.id_relasi=h.id_customer      "
                + "     left join m_kota k on k.id=c.id_kota "
                + "     left join m_area area on area.id=k.id_area "
                + "	left join m_salesman s on s.id_sales=h.id_sales      "
                + "	left join m_gudang g on g.id=h.id_gudang 	"
                + "     left join m_trx_type t on t.kode=h.trx_type "
                + "     left join m_expedisi ex on ex.id=h.id_expedisi "
                + "	where to_char(h.invoice_date, 'yyyy-MM-dd')>='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate()) +"' 	"
                + "	and to_char(h.invoice_date, 'yyyy-MM-dd')<='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate()) +"'      "
                + "	and (coalesce(c.nama_relasi,'')||h.invoice_no ||coalesce(h.description,'') ilike '%"+txtCari.getText()+"%' "
                + "     or coalesce(i.nama_barang,'') ilike '%"+txtCari.getText()+"%' or coalesce(area.nama,'') ilike '%"+txtCari.getText()+"%') "
                + "      "
                + "	group by h.id, h.invoice_date, coalesce(c.nama_relasi,''), coalesce(h.invoice_no,''),     "
                + "	 h.invoice_date+coalesce(h.top,0), coalesce(h.ar_disc,''), coalesce(h.biaya_lain,0),      "
                + "	 coalesce(g.nama_gudang,''), coalesce(h.description,'') ,coalesce(s.nama_sales,''),"
                + "     coalesce(t.keterangan,''), coalesce(ex.nama_expedisi,''), coalesce(area.nama,'') "
                + " )x order by id ";
        System.out.println(sQry);
        ((DefaultTableModel)tblHeader.getModel()).setNumRows(0);
        try{
            int iRow=0;
            double total=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
                    rs.getInt("id"),        //1
                    rs.getString("trx_type"),  //2
                    rs.getDate("tanggal"),  //3
                    rs.getString("nama_customer"),  //4
                    rs.getString("invoice_no"),     //5
                    rs.getString("nama_sales"),     //6
                    rs.getDate("jt_tempo"),         //7
                    rs.getDouble("sub_total"),      //8
                    rs.getString("ar_disc"),        //9    
                    rs.getDouble("biaya_lain"),     //10
                    rs.getDouble("nett"),           //11
                    rs.getDouble("paid_amount"),    //12
                    rs.getDouble("hutang"),         //13
                    rs.getString("nama_expedisi"),    //14
                    rs.getString("description"),    //15
                    rs.getString("nama_gudang"),    //16
                    rs.getString("nama_area"),    //16
                    
                });
                total+=rs.getDouble("nett");
                if(apId==rs.getInt("id")){
                    iRow=tblHeader.getRowCount()-1;
                }
            }
            rs.close();
            lblTotal.setText(GeneralFunction.intFmt.format(total));
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
        Integer id=fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")));
        FrmPenjualan f1=new FrmPenjualan();
        f1.setConn(conn);
        f1.setTitle("Edit Faktur Penjualan");
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.tampilkanData(id);
        f1.setIdPenjualan(id);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnKeluar = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnPrint1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Penjualan barang");
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
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 115, -1));

        jLabel2.setText("Pencarian :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(455, 20, 95, 20));
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 120, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/arrow_refresh.png"))); // NOI18N
        jButton1.setText("Tampilkan");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(785, 20, 115, -1));

        jLabel3.setText("Sampai :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 60, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCariKeyTyped(evt);
            }
        });
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 20, 195, 20));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID#", "Trx Type", "Tanggal", "Pelanggan", "Invoice#", "Salesman", "Jatuh Tempo", "Sub Total", "Disc", "Biaya Lain", "Nett", "Terbayar", "Hutang", "Expedisi", "Keterangan", "Gudang", "Area"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        btnPrint.setText("Preview");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 105, -1));

        btnPrint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/page.png"))); // NOI18N
        btnPrint1.setText("Surat Jalan");
        btnPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrint1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrint1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 120, -1));

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
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE))
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

        setBounds(0, 0, 952, 543);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfLoadData(0);
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
        udfPreview();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrint1ActionPerformed
        udfSuratJalan();
    }//GEN-LAST:event_btnPrint1ActionPerformed

    private void txtCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariKeyTyped

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            udfLoadData(0);
    }//GEN-LAST:event_txtCariKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnPrint1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetail;
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
                udfLoadItemDetail();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        FrmPenjualan f1=new FrmPenjualan();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(aThis);
        f1.setIsNew(true);
        f1.setVisible(true);
    }

    private void udfDelete() {
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0 ||!btnHapus.isEnabled()){return;}
        if(JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus data penjualan ini?", "Hapus data", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            Integer id=fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")));
            try {
                int i=conn.createStatement().executeUpdate(
                          "delete from ar_inv_det where ar_id="+id+";\n"
                        + "delete from ar_inv where id="+id+";");
                if(i>0){
                    ((DefaultTableModel)tblHeader.getModel()).removeRow(iRow);
                    if(tblHeader.getRowCount()>0){
                        iRow=iRow<tblHeader.getRowCount()? iRow: tblHeader.getRowCount()-1;
                        tblHeader.setRowSelectionInterval(iRow, iRow);
                        tblHeader.changeSelection(iRow, 0, false, false);
                    }
                    JOptionPane.showMessageDialog(this, "Hapus penjualan sukses!");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmPembelianHis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void udfPreview() {
        int iRow=tblHeader.getSelectedRow();
        Integer id=Integer.parseInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")).toString());    
        //reportService.cetakPenjualanLX300(id);
        
        if(!MainForm.sTipeUsaha.equalsIgnoreCase("BENGKEL")){
            HashMap param=new HashMap();
            String trxType=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString();    

            param.put("toko", MainForm.sToko);
            param.put("alamat1", MainForm.sAlamat1);
            param.put("alamat2", MainForm.sAlamat2);
            param.put("telp", MainForm.sTelp1);
            param.put("email", MainForm.sEmail);
            if(trxType.toUpperCase().indexOf("RETUR") >=0){
                param.put("idRetur", id);
                reportService.cetakReturPenjualan(param);
            }else{
                param.put("idPenjualan", id);
                reportService.previewPenjualan(param);
                
            }
        }else{
            PrintTrxService service=new PrintTrxService();
            service.setConn(conn);
            service.printJualLX300(id);
        }
    }
}
