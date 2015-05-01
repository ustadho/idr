/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ap;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.JDesktopImage;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.pos.dao.jdbc.ApInvDao;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.service.ReportService;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
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
public class FrmPembelianHis extends javax.swing.JInternalFrame {
    private ApInvDao invDao=new ApInvDao();
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn = new GeneralFunction();
    private JDesktopImage desktop;
    ReportService reportService;

    /**
     * Creates new form FrmPenerimaanStokHis
     */
    public FrmPembelianHis() {
        initComponents();
        jXDatePicker1.setFormats("dd/MM/yyyy");
        jXDatePicker2.setFormats("dd/MM/yyyy");
        tblHeader.getColumn("IdSupplier").setMinWidth(0);
        tblHeader.getColumn("IdSupplier").setMaxWidth(0);
        tblHeader.getColumn("IdSupplier").setPreferredWidth(0);
        tblHeader.getColumn("ReturDariID").setMinWidth(0);
        tblHeader.getColumn("ReturDariID").setMaxWidth(0);
        tblHeader.getColumn("ReturDariID").setPreferredWidth(0);
        
        
        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow = tblHeader.getSelectedRow();
                Integer id;
                btnBaru.setEnabled(menuAuth != null && menuAuth.canInsert());
                btnEdit.setEnabled(iRow >= 0 && menuAuth != null && menuAuth.canUpdate());
                btnHapus.setEnabled(iRow >= 0 && menuAuth != null && menuAuth.canDelete());
                btnPrint.setEnabled(iRow >= 0 && menuAuth != null && menuAuth.canPrint());
                btnSetRetur.setVisible(iRow>=0 && tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString().equalsIgnoreCase("Retur Pembelian"));
                ((DefaultTableModel) tblDetail.getModel()).setNumRows(0);
                if (conn == null || !aThis.isVisible() || tblHeader.getSelectedRow() < 0) {
                    return;
                }else{
                    id=(Integer)tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"));
                }
                try {
                    
                    btnAdaRetur.setVisible(invDao.adaYangDiretur(id));
                } catch (SQLException ex) {
                    Logger.getLogger(FrmPembelianHis.class.getName()).log(Level.SEVERE, null, ex);
                }
                udfLoadItemDetail();

            }
        });

        for(int i=0; i< tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
    }

    public void setConn(Connection con) {
        this.conn = con;
        invDao.setConn(con);
    }

    public void setDesktop(JDesktopImage d) {
        this.desktop = d;
    }
    MenuAuth menuAuth;

    ;
    private void udfInitForm() {
        aThis = this;
        for (int i = 0; i < tblDetail.getColumnCount(); i++) {
            tblDetail.getColumnModel().getColumn(i).setCellRenderer(new com.ustasoft.component.MyRowRenderer());
        }
//        for (int i = 0; i < tblHeader.getColumnCount(); i++) {
//            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new com.ustasoft.component.MyRowRenderer());
//        }
        udfLoadData(0);
        udfLoadItemDetail();
        reportService = new ReportService(conn, aThis);
        tblHeader.setRowHeight(20);
        tblDetail.setRowHeight(20);

        menuAuth = new MenuAuthDao(conn).getMenuByUsername("Daftar Pembelian", MainForm.sUserName);
        if (menuAuth != null) {
            btnBaru.setEnabled(menuAuth.canInsert());
            btnEdit.setEnabled(menuAuth.canUpdate());
            btnHapus.setEnabled(menuAuth.canDelete());
        }
    }

    private void udfLoadItemDetail() {
        int iRow = tblHeader.getSelectedRow();
        if (iRow < 0) {
            return;
        }
        String sQry = "select d.ap_id, d.id_barang, coalesce(i.plu,'') as plu, i.nama_barang, d.qty, coalesce(i.satuan,'') as satuan, "
                + "d.unit_price, d.disc, d.ppn, coalesce(d.biaya,0) as biaya, coalesce(d.keterangan,'') as keterangan,"
                + "(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100))+coalesce(d.biaya,0) as sub_total "
                + "from ap_inv_det d  "
                + "left join m_item i on i.id=d.id_barang "
                + "where d.ap_id=" + fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"))) + " "
                + "order by d.seq";
        System.out.println(sQry);
        try {
            ((DefaultTableModel) tblDetail.getModel()).setNumRows(0);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            while (rs.next()) {
                ((DefaultTableModel) tblDetail.getModel()).addRow(new Object[]{
                    tblDetail.getRowCount() + 1,
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
            if (tblDetail.getRowCount() > 0) {
                tblDetail.setRowSelectionInterval(0, 0);
                tblDetail.setModel(fn.autoResizeColWidth(tblDetail, ((DefaultTableModel) tblDetail.getModel())).getModel());
            }
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }

    public void udfLoadData(Integer apId) {
//        String sQry = "select id, tanggal, trx_type, nama_supplier, invoice_no, jt_tempo, sub_total, ap_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain as nett, paid_amount, "
//                + "(fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain)-paid_amount as hutang, nama_gudang, description, supplier_id "
//                + "from("
//                + "	select h.id, h.invoice_date as tanggal, coalesce(t.keterangan,'') as trx_type, coalesce(s.nama_relasi,'') as nama_supplier, coalesce(h.invoice_no,'') as invoice_no, h.invoice_date+coalesce(h.top,0) as jt_tempo, "
//                + "	sum(fn_get_Disc_Bertingkat(coalesce(d.qty,0)*coalesce(d.unit_price,0), coalesce(d.disc,''))*(1+coalesce(d.ppn,0)/100)) as sub_total, "
//                + "	coalesce(h.ap_disc,'') as ap_disc , coalesce(h.biaya_lain,0) as biaya_lain, coalesce(h.paid_amount,0) as paid_amount, "
//                + "     coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.description,'') as description, h.supplier_id "
//                + "	from ap_inv h "
//                + "	inner join ap_inv_det d on d.ap_id=h.id "
//                + "     inner join m_item i on i.id=d.id_barang "
//                + "	left join m_relasi s on s.id_relasi=h.supplier_id "
//                + "     left join m_gudang g on g.id=h.id_gudang "
//                + "     left join m_trx_type t on t.kode=h.trx_type "
//                + "	where to_char(h.invoice_date, 'yyyy-MM-dd')>='" + GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate()) + "' "
//                + "	and to_char(h.invoice_date, 'yyyy-MM-dd')<='" + GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate()) + "' "
//                + "     and (coalesce(s.nama_relasi,'')||coalesce(h.invoice_no,'')||coalesce(h.description,'') ilike '%" + txtCari.getText() + "%' "
//                + "     or coalesce(i.nama_barang,'')||coalesce(t.keterangan,'') ilike '%" + txtCari.getText() + "%') "
//                + "	group by h.id, h.invoice_date, coalesce(s.nama_relasi,''), coalesce(h.invoice_no,''), "
//                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ap_disc,''), coalesce(h.biaya_lain,0), "
//                + "     coalesce(g.nama_gudang,''), coalesce(h.description,''), coalesce(t.keterangan,''), h.supplier_id "
//                + ")x "
//                + "order by tanggal, id ";
        String sQry="select * from fn_ap_history_header("
                + "'" + GeneralFunction.yyyymmdd_format.format(jXDatePicker1.getDate()) + "'," 
                +"'"+GeneralFunction.yyyymmdd_format.format(jXDatePicker2.getDate())+"', "
                + "'"+txtCari.getText()+"') as (id bigint, tanggal date, trx_type varchar, nama_supplier varchar, invoice_no varchar, \n" +
                    "jt_tempo date, sub_total double precision, ap_disc varchar, biaya_lain double precision, nett double precision, paid_amount double precision, \n" +
                    "hutang double precision, nama_gudang varchar, description text, supplier_id bigint , retur double precision)";
        
        System.out.println(sQry);
        ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
        try {
            int iRow = 0;
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            while (rs.next()) {
                ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                    rs.getInt("id"), //1
                    rs.getDate("tanggal"), //2
                    rs.getString("trx_type"), //3
                    rs.getString("nama_supplier"), //3
                    rs.getString("invoice_no"), //4
                    rs.getDate("jt_tempo"), //5
                    rs.getDouble("sub_total"), //6
                    rs.getString("ap_disc"), //7    
                    rs.getDouble("biaya_lain"), //8
                    rs.getDouble("nett"), //9
                    rs.getDouble("retur"), //10
                    rs.getDouble("paid_amount"), //11
                    rs.getDouble("hutang"), //12
                    rs.getString("nama_gudang"), //13
                    rs.getString("description"), //14
                    rs.getInt("supplier_id"), //15
                });
                if (apId == rs.getInt("id")) {
                    iRow = tblHeader.getRowCount() - 1;
                }
            }
            rs.close();
            if (tblHeader.getRowCount() > 0) {
                tblHeader.setRowSelectionInterval(iRow, iRow);
                tblHeader.setModel(fn.autoResizeColWidth(tblHeader, ((DefaultTableModel) tblHeader.getModel())).getModel());
            }
        } catch (SQLException se) {
            java.util.logging.Logger.getLogger(MasterCoa.class.getName()).log(java.util.logging.Level.SEVERE, null, se);
        }
    }

    private void udfEdit() {
        int iRow = tblHeader.getSelectedRow();
        if (iRow < 0) {
            return;
        }
        String trxType=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString();
        Integer id = fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")));
        if(trxType.equalsIgnoreCase("Pembelian")){
            FrmPembelian f1 = new FrmPembelian();
            f1.setConn(conn);
            desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
            //f1.tampilkanData(id);
            f1.setIdPembelian(id);
            f1.setSrcForm(aThis);
            f1.setVisible(true);
        }else{
            FrmPembelianRetur f1 = new FrmPembelianRetur();
            f1.setConn(conn);
            desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
            //f1.tampilkanData(id);
            f1.setId(id);
            f1.setSrcForm(aThis);
            f1.setVisible(true);
        }
    }

    private void tampilkanLookupAp() {
        int iRow = tblHeader.getSelectedRow();
        DLgLookupPembelianPerSupplier d1 = new DLgLookupPembelianPerSupplier(JOptionPane.getFrameForComponent(aThis), true);
        d1.setConn(conn);
        Integer idRetur=(Integer) tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"));
        d1.setTitle("Lookup histori pembelian ke supplier '" + tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Supplier")).toString() + "'");
        d1.setIdRelasi((Integer) tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("IdSupplier")));
        d1.setIdRetur(idRetur);
        
        d1.setVisible(true);
        if(d1.getApId()!=null){
            invDao.setReturDari(idRetur, d1.getApId());
            udfRefreshDataPerBaris(idRetur);
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
        btnSetRetur = new javax.swing.JButton();
        btnAdaRetur = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori pembelian barang");
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
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 110, -1));

        jLabel2.setText("Pencarian :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, 80, 20));
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 110, -1));

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
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
        });
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 230, 20));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID#", "Tanggal", "Trx Type", "Supplier", "Invoice#", "Jatuh Tempo", "Sub Total", "Disc", "Biaya Lain", "Nett", "Retur", "Terbayar", "Hutang", "Gudang", "Keterangan", "IdSupplier", "ReturDariID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
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

        btnSetRetur.setText("Set Retur dari");
        btnSetRetur.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSetRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetReturActionPerformed(evt);
            }
        });
        jPanel2.add(btnSetRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 10, 125, -1));

        btnAdaRetur.setForeground(new java.awt.Color(204, 0, 0));
        btnAdaRetur.setText("Ada Item yang diretur");
        btnAdaRetur.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAdaRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdaReturActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdaRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(715, 10, 135, -1));

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
        int iRow = tblHeader.getSelectedRow();
        HashMap param = new HashMap();
        //            String sSubRpt="", sGambar="";
//            sGambar=getClass().getResource("/resources/siloam_bw.jpg").toString();
//            sSubRpt=getClass().getResource("Reports/").toString();
        Integer id = Integer.parseInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")).toString());
        String trxType = tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString();
        param.put("idPembelian", id);
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

//            param.put("sGambar", sGambar);
//            param.put("SUBREPORT_DIR", sSubRpt);
        //param.put("SUBREPORT_DIR", getClass().getResourceAsStream("Reports"));
        if (trxType.toUpperCase().indexOf("RETUR") >= 0) {
            reportService.cetakReturPembelian(param);
        } else {
            reportService.cetakPembelian(param);
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            udfLoadData(0);
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void btnSetReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetReturActionPerformed
        tampilkanLookupAp();
    }//GEN-LAST:event_btnSetReturActionPerformed

    private void btnAdaReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdaReturActionPerformed
        int iRow = tblHeader.getSelectedRow();
        DLgLookupPembelianPerSupplier d1 = new DLgLookupPembelianPerSupplier(JOptionPane.getFrameForComponent(aThis), true);
        d1.setConn(conn);
        Integer apId=(Integer) tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#"));
        d1.setTitle("Lookup histori pembelian ke supplier '" + tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Supplier")).toString() + "'");
        d1.setIdRelasi((Integer) tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("IdSupplier")));
        d1.setApId(apId);
        
        d1.setVisible(true);
    }//GEN-LAST:event_btnAdaReturActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdaRetur;
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSetRetur;
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

    public void udfRefreshDataPerBaris(Integer id) {
        String sQry = "select id, tanggal, nama_supplier, invoice_no, jt_tempo, sub_total, ap_disc, biaya_lain, fn_get_Disc_Bertingkat(sub_total, ap_disc)+biaya_lain as nett, paid_amount, "
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
                + "	where h.id=" + id + " "
                + "	group by h.id, h.invoice_date, coalesce(s.nama_relasi,''), coalesce(h.invoice_no,''), "
                + "     h.invoice_date+coalesce(h.top,0), coalesce(h.ap_disc,''), coalesce(h.biaya_lain,0), "
                + "     coalesce(g.nama_gudang,''), coalesce(h.description,'') "
                + ")x "
                + "order by id ";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            TableColumnModel col = tblHeader.getColumnModel();
            int iRow = tblHeader.getSelectedRow();
            if (rs.next()) {
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
                udfLoadItemDetail();
            }
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        FrmPembelian f1 = new FrmPembelian();
        f1.setConn(conn);
        desktop.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(aThis);
        f1.setIsNew(true);
        f1.setVisible(true);
    }

    private void udfDelete() {
        int iRow = tblHeader.getSelectedRow();
        if (iRow < 0 || !btnHapus.isEnabled()) {
            return;
        }
        if (JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus data pembelian ini?", "Hapus data", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Integer id = fn.udfGetInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")));
            try {
                int i = conn.createStatement().executeUpdate(
                        "delete from ap_inv_det where ap_id=" + id + ";\n"
                        + "delete from ap_inv where id=" + id + ";");
                if (i > 0) {
                    ((DefaultTableModel) tblHeader.getModel()).removeRow(iRow);
                    if (tblHeader.getRowCount() > 0) {
                        iRow = iRow < tblHeader.getRowCount() ? iRow : tblHeader.getRowCount() - 1;
                        tblHeader.setRowSelectionInterval(iRow, iRow);
                        tblHeader.changeSelection(iRow, 0, false, false);
                    }
                    JOptionPane.showMessageDialog(this, "Hapus pembelian sukses!");

                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmPembelianHis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void previewRetur() {
        HashMap param=new HashMap();
        int iRow=tblHeader.getSelectedRow();
        Integer id = Integer.parseInt(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("ID#")).toString());
        String trxType = tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Trx Type")).toString();
        param.put("idPembelian", id);
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);

//            param.put("sGambar", sGambar);
//            param.put("SUBREPORT_DIR", sSubRpt);
        //param.put("SUBREPORT_DIR", getClass().getResourceAsStream("Reports"));
        if (trxType.toUpperCase().indexOf("RETUR") >= 0) {
            reportService.cetakReturPembelian(param);
        }
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dmyFmtHm=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        JCheckBox checkBox = new JCheckBox();
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date || value instanceof Timestamp){
                value=dmyFmtHm.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }else if(value instanceof Boolean){
//                checkBox.setSelected(((Boolean) value).booleanValue());
//                  checkBox.setHorizontalAlignment(JLabel.CENTER);
//                  if (row%2==0){
//                     checkBox.setBackground(w);
//                  }else{
//                     checkBox.setBackground(g);
//                  }
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
               if(table.getValueAt(row, table.getColumnModel().getColumnIndex("Trx Type")).toString().equalsIgnoreCase("Retur Pembelian")){
                    setBackground(Color.YELLOW);
                    setForeground(table.getForeground());
               }
               else if(table.getValueAt(row, table.getColumnModel().getColumnIndex("Trx Type")).toString().equalsIgnoreCase("Pembelian")
                       && fn.udfGetDouble(table.getValueAt(row, table.getColumnModel().getColumnIndex("Retur")))< 0){
                    setBackground(new Color(255,153,153));
                    setForeground(table.getForeground());
               }else{
                   setBackground(table.getBackground());
                    setForeground(table.getForeground());
               }
            }
//            if(value instanceof Boolean){
//                checkBox.setSelected(((Boolean) value).booleanValue());
//                checkBox.setHorizontalAlignment(JLabel.CENTER);
//                
//                if(isSelected){
//                    checkBox.setBackground(table.getSelectionBackground());
//                    checkBox.setForeground(table.getSelectionForeground());
//                }else{
//                   if( row>=0 && (Boolean)table.getValueAt(row, COL_CITO)==true){
//                        checkBox.setBackground(lblPRCito.getBackground());
//                        checkBox.setForeground(table.getForeground());
//                    }else{
//                       if((Boolean)table.getValueAt(row, COL_TAMBAHAN)==true){
//                            checkBox.setBackground(lblPRTambahan.getBackground());
//                        checkBox.setForeground(table.getForeground());
//                       }else{
//                            checkBox.setBackground(table.getBackground());
//                            checkBox.setForeground(table.getForeground());
//                       }
//                    }
//                }
//                return  checkBox;
//            }
            setValue(value);
            return this;
        }
    }
}
