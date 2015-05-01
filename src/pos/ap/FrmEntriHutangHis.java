/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ap;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.JDesktopImage;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.service.ReportService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pos.MainForm;
import pos.master.form.MasterCoa;

/**
 *
 * @author cak-ust
 */
public class FrmEntriHutangHis extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktop;
    ReportService reportService;
    MenuAuth menuAuth;
    
    /**
     * Creates new form FrmPenerimaanStokHis
     */
    public FrmEntriHutangHis() {
        initComponents();
        
        
        
    }

    public void setConn(Connection con){
        this.conn=con;
    }
    
    public void setDesktop(JDesktopImage d){
        this.desktop=d;
    }
    
    private void udfInitForm(){
        aThis=this;
        for(int i=0; i<tblHutang.getColumnCount(); i++){
            tblHutang.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        for(int i=0; i<tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        for(int i=0; i<tblAlatBayar.getColumnCount(); i++){
            tblAlatBayar.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        tblAlatBayar.getColumn("KodeAB").setMinWidth(0);
        tblAlatBayar.getColumn("KodeAB").setMaxWidth(0);
        tblAlatBayar.getColumn("KodeAB").setPreferredWidth(0);
        
        jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
        jXDatePicker2.setFormats(new String[]{"dd/MM/yyyy"});
        
        reportService =new ReportService(conn, aThis);
        menuAuth= new MenuAuthDao(conn).getMenuByUsername("Entri Hutang History", MainForm.sUserName);
        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblHeader.getSelectedRow();
                btnBaru.setEnabled(menuAuth.canInsert());
                btnEdit.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canUpdate());
                btnHapus.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canDelete());
                btnPrint.setEnabled(iRow>=0 && menuAuth!=null && menuAuth.canPrint());
                
                ((DefaultTableModel)tblHutang.getModel()).setNumRows(0);
                if(conn==null ||!aThis.isVisible()||tblHeader.getSelectedRow()<0)
                    return;
                udfLoadAngsuran();
                udfLoadAlatBayar();        
            }
        });
        
        tblHeader.setRowHeight(20);
        udfLoadData("");
        udfLoadAngsuran();
        udfLoadAlatBayar();
        
    }
    
    private void udfLoadAngsuran(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String sQry="select angsuran_Ke, jt_tempo, nominal, keterangan "
                + "from ap_hutang_lain_angsuran "
                + "where no_ap='"+tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. AP")).toString() +"' "
                + "order by angsuran_ke";
        try{
            ((DefaultTableModel)tblHutang.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHutang.getModel()).addRow(new Object[]{
                    rs.getInt("angsuran_ke"), 
                    rs.getDate("jt_tempo"), 
                    rs.getDouble("nominal"),
                    rs.getString("keterangan")
                });
            }
            if(tblHutang.getRowCount()>0){
                tblHutang.setRowSelectionInterval(0, 0);
//                tblHutang.setModel(fn.autoResizeColWidth(tblHutang, ((DefaultTableModel)tblHutang.getModel())).getModel());
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }
    
    private void udfLoadAlatBayar(){
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0)
            return;
        String sQry="select coalesce(ab.alat_bayar,'') as alat_bayar, d.kode_bayar, coalesce(d.no_warkat,'') as no_warkat, jt_tempo, coalesce(d.nominal, 0) as nominal, coalesce(d.keterangan,'') as keterangan "
                + "from ap_hutang_lain_alat_bayar d "
                + "left join acc_alat_bayar ab on ab.kode=d.kode_bayar "
                + "where d.no_ap='"+tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. AP")).toString() +"' "
                + "order by coalesce(ab.alat_bayar,'')";
        try{
            ((DefaultTableModel)tblAlatBayar.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblAlatBayar.getModel()).addRow(new Object[]{
                    rs.getString("alat_bayar"),
                    rs.getString("no_warkat"),
                    rs.getDate("jt_tempo"), 
                    rs.getDouble("nominal"),
                    rs.getString("keterangan"),
                    rs.getString("kode_bayar")
                });
            }
            if(tblAlatBayar.getRowCount()>0){
                tblAlatBayar.setRowSelectionInterval(0, 0);
//                tblAlatBayar.setModel(fn.autoResizeColWidth(tblAlatBayar, ((DefaultTableModel)tblAlatBayar.getModel())).getModel());
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }
    
    public void udfLoadData(String sNoAp){
        String sQry="select * from( "
                + "     select h.no_ap, h.tanggal, "
                + "     coalesce(c.nama_relasi,'') as relasi, "
                + "	h.keterangan, coalesce(h.total, 0) as nominal, h.user_ins, h.time_ins, h.user_upd, h.time_upd "
                + "	from ap_hutang_lain h "
                + "	left join m_relasi c on h.id_relasi=c.id_relasi "
                + ")x "
                + "where to_char(tanggal, 'yyyy-MM-dd')>='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate()) +"' "
                + "and to_char(tanggal, 'yyyy-MM-dd')<='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate()) +"' "
                + "and no_ap||relasi ilike '%%' "
                + "order by tanggal desc, no_ap desc";
        
        System.out.println(sQry);
        ((DefaultTableModel)tblHeader.getModel()).setNumRows(0);
        try{
            int iRow=0;
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
                    rs.getString("no_ap"),          //1
                    rs.getDate("tanggal"),          //2
                    rs.getString("relasi"),         //3
                    rs.getString("keterangan"),     //3
                    rs.getDouble("nominal"),        //6
                    rs.getString("user_ins"),       //7    
                    rs.getTimestamp("time_ins"),    //8
                    rs.getString("user_upd"),       //7    
                    rs.getTimestamp("time_upd"),    //8
                });
                if(sNoAp.equalsIgnoreCase(rs.getString("no_ap"))){
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
        String sNoAp=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. AP")).toString();
        FrmEntriHutang f1=new FrmEntriHutang();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        //f1.tampilkanData(id);
        f1.setNoAp(sNoAp);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHutang = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        lblTotalHutang = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAlatBayar = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Entri Hutang");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 920, 60));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. AP", "Tanggal", "Relasi", "Keterangan", "Nominal", "User Ins", "Tgl. Insert", "User Upd", "Tgl. Update"
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 920, 170));

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

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 920, 40));

        tblHutang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", null, null, null},
                {"2", null, null, null},
                {"3", null, null, null},
                {"4", null, null, null},
                {"5", null, null, null},
                {"6", null, null, null}
            },
            new String [] {
                "Angsuran Ke", "Tgl. Jt. Tempo", "Nominal", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblHutang);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 920, 130));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Total :");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 370, 80, 20));

        lblTotalHutang.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotalHutang.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalHutang.setText("0");
        lblTotalHutang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblTotalHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 370, 170, 20));

        tblAlatBayar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Alat Bayar", "No. Warkat", "Tgl. Jt. Tempo", "Nominal", "Keterangan", "KodeAB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblAlatBayar);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 920, 80));

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
        cetakPO();
    }//GEN-LAST:event_btnPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblTotalHutang;
    private javax.swing.JTable tblAlatBayar;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTable tblHutang;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

    public void udfRefreshDataPerBaris(Integer id) {
        String sQry="select id, tanggal, nama_supplier, invoice_no, jt_tempo, sub_total, ap_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain as nett, paid_amount, "
                + "(fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description "
                + "from("
                + "	select h.id, h.invoice_date as tanggal, coalesce(s.nama_relasi,'') as nama_supplier, coalesce(h.invoice_no,'') as invoice_no, h.invoice_date+coalesce(h.top,0) as jt_tempo, "
                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
                + "	coalesce(h.ap_disc,'') as ap_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
                + "     coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description "
                + "	from ap_inv h "
                + "	inner join ap_inv_det d on d.ap_id=h.id "
                + "	left join m_relasi s on s.id_relasi=h.supplier_id "
                + "     left join m_gudang g on g.id=h.id_gudang "
                + "	where h.id="+id+" "
                + "	group by h.id, h.invoice_date, coalesce(s.nama_supplier,''), coalesce(h.invoice_no,''), "
                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ap_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(g.nama_gudang,''), coalesce(h.description,'') "
                + ")x "
                + "order by id ";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            TableColumnModel col=tblHeader.getColumnModel();
            int iRow=tblHeader.getSelectedRow();
            if(rs.next()){
                tblHeader.setValueAt(rs.getDate("tanggal"), iRow, col.getColumnIndex("Tanggal"));
                tblHeader.setValueAt(rs.getString("nama_supplier"), iRow, col.getColumnIndex("Supplier"));
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
                udfLoadAngsuran();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        FrmEntriHutang f1=new FrmEntriHutang();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(aThis);
        f1.setIsNew(true);
        f1.setVisible(true);
    }

    private boolean udfCekBeforeDelete(){
//        try {
//            int iRow=tblHeader.getSelectedRow();
//            String sNoAp=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. AP")).toString();
//                
//            ResultSet rs=conn.createStatement().executeQuery("select * from ap_inv where no_po='"+sNoAp+"'");
//            if(rs.next()){
//                rs.close();
//                JOptionPane.showMessageDialog(this, "Nomor PO ini tidak bisa dihapus karena telah dipakai di Penerimaan barang!");
//                return false;
//            }
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(FrmEntriHutangHis.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return true;
    }
    
    private void udfDelete() {
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0 ||!btnHapus.isEnabled()||!udfCekBeforeDelete()){return;}
        if(JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus Entry Hutang ini?", "Hapus data", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            String sNoAp=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. AP")).toString();
            try {
                int i=conn.createStatement().executeUpdate(
                          "delete from ap_hutang_lain_angsuran where no_ap='"+sNoAp+"';\n"
                        + "delete from ap_hutang_lain_alat_bayar where no_ap='"+sNoAp+"';\n"
                        + "delete from ap_hutang_lain where no_ap='"+sNoAp+"';");
                if(i>0){
                    ((DefaultTableModel)tblHeader.getModel()).removeRow(iRow);
                    if(tblHeader.getRowCount()>0){
                        iRow=iRow<tblHeader.getRowCount()? iRow: tblHeader.getRowCount()-1;
                        tblHeader.setRowSelectionInterval(iRow, iRow);
                        tblHeader.changeSelection(iRow, 0, false, false);
                    }
                    JOptionPane.showMessageDialog(this, "Hapus Entri Hutang sukses!");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmEntriHutangHis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void cetakPO() {
        int iRow=tblHeader.getSelectedRow();
        HashMap param=new HashMap();
        param.put("noPo", tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("No. PO")).toString());
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);
        reportService.cetakPO(param);
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        JCheckBox checkBox = new JCheckBox();
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
//            if((column==0)||(column==1)||(column==2)||(column==3)||(column==6)||(column==7)||(column==9)){
//                JTextField jt= new JTextField();
//                setHorizontalAlignment(jt.LEFT);
//            }
//            
            
            if(value instanceof Float||value instanceof Double||value instanceof Integer){
                setValue(fn.intFmt.format(value));
                setHorizontalAlignment(jLabel1.RIGHT);
            }else{
                setValue(value);
            }
            
            
            if(column%2==0){
                g = g1;
                w = w1;
                h = h1;
            }else{
                g = g2;
                w = w2;
                h = h2;
            }
            
             if (value instanceof Boolean) { // Boolean
                  checkBox.setSelected(((Boolean) value).booleanValue());
                  checkBox.setHorizontalAlignment(JLabel.CENTER);
                  if (row%2==0){
                     checkBox.setBackground(w);
                  }else{
                     checkBox.setBackground(g);
                  }
 
                  if (isSelected){
                        checkBox.setBackground(new Color(248,255,167));//51,102,255));
                    }
                  
                  return checkBox;
            }else if(value instanceof Date){
                setValue(fn.ddMMyy_format.format(value));
            }
            
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            if(isSelected){
                //setBackground(new Color(248,255,167));//[174,212,254]
                setBackground(table.getSelectionBackground());
                setForeground(table.getForeground());
            }
            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }
            setFont(new Font("Tahoma", 0,12));
             if (value instanceof Boolean) { // Boolean
                  checkBox.setSelected(((Boolean) value).booleanValue());
                  checkBox.setHorizontalAlignment(JLabel.CENTER);

                  return checkBox;
            }
            return this;
        }
    }
    Color g1 = new Color(230,243,255);//[251,251,235]
    Color g2 = new Color(219,238,255);//[247,247,218]

    Color w1 = new Color(255,255,255);
    Color w2 = new Color(250,250,250);

    Color h1 = new Color(255,240,240);
    Color h2 = new Color(250,230,230);

    Color g;
    Color w;
    Color h;
}
