/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pos.inventory;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.service.ReportService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.type.OrientationEnum;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmStokOpnameHis extends javax.swing.JInternalFrame {
    private Connection conn;
    ReportService service;
    
    /**
     * Creates new form FrmStokOpnameHis
     */
    public FrmStokOpnameHis() {
        initComponents();
        tblDetail.getColumn("IdBarang").setMinWidth(0);
        tblDetail.getColumn("IdBarang").setMaxWidth(0);
        tblDetail.getColumn("IdBarang").setPreferredWidth(0);
        
        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                int iRow=jXTable1.getSelectedRow();
                btnPrint.setEnabled(iRow>=0);
                if(conn==null || iRow < 0)
                    return;
                Integer idOpname=GeneralFunction.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("ID")));
                
                String sql="select d.id_barang, coalesce(i.plu,'') as plu, coalesce(i.nama_barang,'') as nama_barang,coalesce(s.satuan,'') as satuan,\n" +
                            "coalesce(d.qty_sekarang,0) as qty_sekarang, coalesce(d.qty_baru,0) as qty_baru, \n" +
                            "coalesce(d.hpp_sekarang, 0) as hpp_sekarang, coalesce(d.hpp_baru,0) as hpp_baru, coalesce(d.ket, '') as ket \n" +
                            "from opname_detail d\n" +
                            "inner join m_item i on i.id=d.id_barang\n "
                            + "left join m_satuan s on s.id=i.id_satuan " +
                            "where d.id_opname="+idOpname+"\n" +
                            "order by d.seq";
                System.out.println(sql);
                try {
                    ResultSet rs=conn.createStatement().executeQuery(sql);
                    while(rs.next()){
                        ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                            rs.getString("plu"),
                            rs.getString("nama_barang"),
                            rs.getString("satuan"),
                            rs.getDouble("qty_sekarang"),
                            rs.getDouble("qty_baru"),
                            rs.getDouble("hpp_sekarang"),
                            rs.getDouble("hpp_baru"),
                            rs.getString("ket"),
                            rs.getInt("id_barang")
                        });
                        
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FrmStokOpnameHis.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }

    public void setConn(Connection c){
        this.conn=c;
        service=new ReportService(conn, this);
    }
    
    
    private void tampilkan(){
        String sql="select distinct o.tanggal, o.no_bukti, coalesce(g.nama_gudang,'') as nama_gudang, "
                + "coalesce(o.keterangan,'') as keterangan, coalesce(o.dihitung_oleh,'') as dihitung_oleh, o.id \n" +
                    "from opname o\n" +
                    "inner join opname_detail d on d.id_opname=o.id\n" +
                    "inner join m_gudang g on g.id=o.id_gudang\n" +
                    "inner join m_item i on i.id=d.id_barang\n" +
                    "where ((coalesce(o.keterangan,'')||coalesce(o.dihitung_oleh,'')) ilike '%"+jTextField1.getText()+"%'\n" +
                    "or coalesce(i.nama_barang,'') ilike '%"+jTextField1.getText()+"%' )\n" +
                    "and to_char(o.tanggal, 'yyyy-MM-dd')>='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate() )+"' \n" +
                    "and to_char(o.tanggal, 'yyyy-MM-dd')<='"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate() )+"'";

        System.out.println(sql);
        ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
        try {
            ResultSet rs=conn.createStatement().executeQuery(sql);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getDate("tanggal"),
                    rs.getString("no_bukti"),
                    rs.getString("nama_gudang"),
                    rs.getString("keterangan"),
                    rs.getString("dihitung_oleh"),
                    rs.getInt("id")
                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                GeneralFunction.adjustColumnResizer(tblDetail);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmStokOpnameHis.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        btnTampilkan = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Stok Opname");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        jLabel1.setText("Mulai : ");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 60, 20);
        jPanel1.add(jXDatePicker1);
        jXDatePicker1.setBounds(75, 10, 105, 22);
        jPanel1.add(jXDatePicker2);
        jXDatePicker2.setBounds(250, 10, 105, 22);

        jLabel2.setText("Pencarian : ");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(370, 10, 70, 20);

        jLabel3.setText("Sampai : ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(185, 10, 60, 20);

        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(jTextField1);
        jTextField1.setBounds(450, 10, 200, 22);

        btnTampilkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/find.png"))); // NOI18N
        btnTampilkan.setText("Tampilkan");
        btnTampilkan.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });
        jPanel1.add(btnTampilkan);
        btnTampilkan.setBounds(650, 8, 100, 25);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setEnabled(false);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrint);
        btnPrint.setBounds(755, 8, 95, 25);

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "No. Opname", "Gudang", "Keterangan", "Dihitung Oleh", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jXTable1);

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Satuan", "Qty Sekarang", "Qty Baru", "Hpp Sekarang", "Hpp Baru", "Keterangan", "IdBarang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, true, true, false
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        setBounds(0, 0, 892, 434);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        tampilkan();
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        int iRow=jXTable1.getSelectedRow();
        HashMap param=new HashMap();
        param.put("idOpname", GeneralFunction.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("ID"))));
        service.udfPreview(param, "OpnameQty", OrientationEnum.PORTRAIT);
    }//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JTable tblDetail;
    // End of variables declaration//GEN-END:variables

    private void initForm() {
        jXDatePicker1.setFormats(MainForm.sDateFormat);
        jXDatePicker2.setFormats(MainForm.sDateFormat);
        tampilkan();
    }
}
