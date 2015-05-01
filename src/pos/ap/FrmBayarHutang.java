/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.ap;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccAlatBayarDao;
import com.ustasoft.pos.dao.jdbc.ApHutangLainDao;
import com.ustasoft.pos.domain.AccAlatBayar;
import com.ustasoft.pos.domain.ApHutangLain;
import com.ustasoft.pos.domain.ApHutangLainAlatBayar;
import com.ustasoft.pos.domain.ApHutangLainAngsuran;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXDatePicker;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmBayarHutang extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    JTable srcTable;
    MyKeyListener kListener=new MyKeyListener();
    private JComboBox cmbAlatBayar= new JComboBox();
    private List<AccAlatBayar> accAlatBayar=new ArrayList<AccAlatBayar>();
    private double totAlatBayar=0;
    private String sTipeRelasi;
    private Object srcForm;
    private boolean isNew;
    
    /**
     * Creates new form FrmEntriHutang
     */
    public FrmBayarHutang() {
        initComponents();
        
        txtNoAp.setEnabled(false);
        TableColumnModel col=tblAlatBayar.getColumnModel();
        for(int i=0; i<tblAlatBayar.getColumnCount(); i++){
            col.getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        col=tblHutang.getColumnModel();
        for(int i=0; i<tblHutang.getColumnCount(); i++){
            col.getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblHutang.getColumn("Nominal").setCellEditor(cEditor);
        tblHutang.getColumn("Keterangan").setCellEditor(cEditor);
        tblAlatBayar.getColumn("No. Warkat").setCellEditor(cEditor);
        tblAlatBayar.getColumn("Keterangan").setCellEditor(cEditor);
        tblAlatBayar.getColumn("Nominal").setCellEditor(cEditor);
        
        tblHutang.getColumn("Tgl. Jt. Tempo").setCellEditor(new myDateCellEditor());
        tblAlatBayar.getColumn("Tgl. Jt. Tempo").setCellEditor(new myDateCellEditor());
        //tblHutang.getColumn("Tgl. Jt Tempo").setCellEditor(new TableComboBoxEditor(((DefaultTableModel)tblAlatBayar.getModel()).getDataVector()));
        tblAlatBayar.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblHutang.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        
        tblHutang.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col=tblHutang.getColumnModel();
                double total=0;
                for(int i=0; i<tblHutang.getRowCount(); i++){
                    total+=tblHutang.getValueAt(i, col.getColumnIndex("Nominal"))==null? 0: 
                            fn.udfGetDouble(tblHutang.getValueAt(i, col.getColumnIndex("Nominal")));
                }
                lblTotalHutang.setText(fn.dFmt.format(total));
            }
        });
        tblAlatBayar.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col=tblAlatBayar.getColumnModel();
                totAlatBayar=0;
                for(int i=0; i<tblAlatBayar.getRowCount(); i++){
                    totAlatBayar+=tblAlatBayar.getValueAt(i, col.getColumnIndex("Nominal"))==null? 0: 
                            fn.udfGetDouble(tblAlatBayar.getValueAt(i, col.getColumnIndex("Nominal")));
                }
                lblTotalAlatBayar.setText(fn.dFmt.format(totAlatBayar));
            }
        });
        cmbAlatBayar.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(cmbAlatBayar.getSelectedIndex()>=0 && accAlatBayar.size() >0 && tblAlatBayar.getSelectedRow() >=0){
                    tblAlatBayar.setValueAt(accAlatBayar.get(cmbAlatBayar.getSelectedIndex()).getKode(), 
                            tblAlatBayar.getSelectedRow(), 
                            tblAlatBayar.getColumnModel().getColumnIndex("KodeAB")
                    );
                }
            }
        });
        tblHutang.setRowHeight(22);
        tblAlatBayar.setRowHeight(22);
    }
    
    public void setConn(Connection c){
        this.conn=c;
    }
    
    private void udfInitForm(){
        try {
            fn.setConn(conn);
            cmbAlatBayar.removeAllItems();
            AccAlatBayarDao alatBayarDao=new AccAlatBayarDao(conn);
            List<AccAlatBayar> alatBayar=alatBayarDao.cariSemuaData();
            for(AccAlatBayar acc:alatBayar){
                accAlatBayar.add(acc);
                cmbAlatBayar.addItem(acc.getAlatBayar());
            }
            tblAlatBayar.getColumn("Alat Bayar").setCellEditor(new DefaultCellEditor(cmbAlatBayar));
            lblRelasiID.setVisible(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        
    }
    
    private void udfClear(){
        txtNoAp.setText("");
        txtRelasi.setText("");
        lblRelasi.setText("");
        txtKeterangan.setText("");
        txtAkunKasBank.setText("");
        lblAkunKasBank.setText("");
        ((DefaultTableModel)tblAlatBayar.getModel()).setNumRows(0);
        ((DefaultTableModel)tblHutang.getModel()).setNumRows(0);
        
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        btnSave.requestFocusInWindow();
        if(txtRelasi.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silahkan isi Kode Relasi terlebih dulu!");
            txtRelasi.requestFocusInWindow();
            return false;
        }
        if(lblTotalHutang.getText().equalsIgnoreCase("0")){
            JOptionPane.showMessageDialog(this, "Masukkan angsuran terlebih dulu!");
            tblHutang.requestFocusInWindow();
            return false;
        }
        for(int i=0; i<tblAlatBayar.getRowCount(); i++){
            if(tblAlatBayar.getValueAt(i, 0)!=null && tblAlatBayar.getValueAt(i, 0).toString().length()>0 && 
                    fn.udfGetDouble(tblAlatBayar.getValueAt(i, tblAlatBayar.getColumnModel().getColumnIndex("Nominal"))) >0){
                break;
            }
            
        }
        if(fn.udfGetDouble(lblTotalAlatBayar.getText()) != fn.udfGetDouble(lblTotalHutang.getText())){
            JOptionPane.showMessageDialog(this, "Total Hutang tidak sama dengan total alat bayar!");
            tblAlatBayar.requestFocusInWindow();
            tblAlatBayar.changeSelection(0, 0, false, false);
        }
        return b;
    }
    
    private void udfSave(){
        if(!udfCekBeforeSave())
            return;
        Timestamp time=fn.getTimeServer();
        ApHutangLainDao dao=new ApHutangLainDao(conn);
        ApHutangLain header=new ApHutangLain();
        header.setNoAp(txtNoAp.getText().length()==0? null: txtNoAp.getText());
        header.setTanggal(jXDatePicker1.getDate());
        header.setKeterangan(txtKeterangan.getText());
        header.setSaldoAw(chkBayar.isSelected());
        header.setIdRelasi(fn.udfGetInt(lblRelasiID.getText()));
        header.setTipeRelasi(this.sTipeRelasi);
        header.setTotal(fn.udfGetDouble(lblTotalHutang.getText()));
        header.setAkunKasBank(txtAkunKasBank.getText());
        header.setUserIns(header.getNoAp()==null? MainForm.sUserName: header.getUserIns());
        header.setTimeIns(header.getNoAp()==null? time: header.getTimeIns());
        header.setUserUpd(header.getNoAp()!=null? MainForm.sUserName: null);
        header.setTimeUpd(header.getNoAp()!=null? time: null);
        header.setTipeRelasi(lblRelasiTipe.getText());
        try{
            conn.setAutoCommit(false);
            dao.simpanHeader(header);
            txtNoAp.setText(header.getNoAp());
            List<ApHutangLainAngsuran>  ang=new ArrayList<ApHutangLainAngsuran>();
            TableColumnModel col=tblHutang.getColumnModel();
            for(int i=0; i<tblHutang.getRowCount(); i++){
                if(fn.udfGetDouble(tblHutang.getValueAt(i, col.getColumnIndex("Nominal"))) > 0){
                    ApHutangLainAngsuran a=new ApHutangLainAngsuran();
                    a.setNoAp(header.getNoAp());
                    a.setAngsuranKe(fn.udfGetInt(tblHutang.getValueAt(i, col.getColumnIndex("Angsuran Ke"))));
                    a.setJtTempo((Date)tblHutang.getValueAt(i, col.getColumnIndex("Tgl. Jt. Tempo")));
                    a.setKeterangan(tblHutang.getValueAt(i, col.getColumnIndex("Keterangan"))==null? "": tblHutang.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                    a.setNominal(fn.udfGetDouble(tblHutang.getValueAt(i, col.getColumnIndex("Nominal"))));
                    ang.add(a);
                }
            }
            col=tblAlatBayar.getColumnModel();
            List<ApHutangLainAlatBayar>  ab=new ArrayList<ApHutangLainAlatBayar>();
            for(int i=0; i<tblAlatBayar.getRowCount(); i++){
                if(tblAlatBayar.getValueAt(i, col.getColumnIndex("Alat Bayar"))!=null && fn.udfGetDouble(tblAlatBayar.getValueAt(i, col.getColumnIndex("Nominal"))) > 0){
                    ApHutangLainAlatBayar a=new ApHutangLainAlatBayar();
                    a.setNoAp(header.getNoAp());
                    a.setKodeBayar(tblAlatBayar.getValueAt(i, col.getColumnIndex("KodeAB")).toString());
                    a.setJtTempo(tblAlatBayar.getValueAt(i, col.getColumnIndex("Tgl. Jt. Tempo"))==null ||tblAlatBayar.getValueAt(i, col.getColumnIndex("Tgl. Jt. Tempo")).toString().equalsIgnoreCase("")? null: 
                            (Date)tblAlatBayar.getValueAt(i, col.getColumnIndex("Tgl. Jt. Tempo")));
                    a.setKeterangan(tblAlatBayar.getValueAt(i, col.getColumnIndex("Keterangan"))==null? "": tblAlatBayar.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
                    a.setNominal(fn.udfGetDouble(tblAlatBayar.getValueAt(i, col.getColumnIndex("Nominal"))));
                    ab.add(a);
                }
            }
            dao.simpanAngsuran(ang);
            dao.simpanAlatBayar(ab);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan transaksi sukses!");
            if(srcForm instanceof FrmEntriHutangHis){
                ((FrmEntriHutangHis)srcForm).udfLoadData(txtNoAp.getText());
                this.dispose();
            }
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage()+"\n"+se.getNextException());
            } catch (SQLException ex) {
                Logger.getLogger(FrmBayarHutang.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void showAp(){
        String sQry="select * from( "
                + "     select h.no_ap, h.tanggal, "
                + "     coalesce(c.nama_relasi,'') as relasi, "
                + "	h.keterangan, coalesce(h.total, 0) as nominal, h.user_ins, h.time_ins, h.user_upd, h.time_upd "
                + "	from ap_hutang_lain h "
                + "	left join m_relasi c on h.id_relasi=c.id_relasi "
                + ")x "
                + "where no_ap='"+txtNoAp.getText()+"' "
                + "order by tanggal desc, no_ap desc";
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        txtNoAp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRelasi = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblRelasi = new javax.swing.JLabel();
        lblRelasiID = new javax.swing.JLabel();
        lblRelasiTipe = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlatBayar = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHutang = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblAkunKasBank = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        txtAkunKasBank = new javax.swing.JTextField();
        lblTotalHutang = new javax.swing.JLabel();
        chkBayar = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblTotalAlatBayar = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Pembayaran Hutang");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Keterangan :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 100, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("No. Bayar");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 100, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Tanggal");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 35, 100, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, 400, 20));
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 35, 120, -1));

        txtNoAp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNoAp, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 120, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Nama Relasi");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 35, 80, 20));

        txtRelasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtRelasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 120, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Kode Relasi");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 80, 20));

        lblRelasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblRelasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 35, 180, 20));

        lblRelasiID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblRelasiID, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 35, 20, 20));

        lblRelasiTipe.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblRelasiTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 15, 50, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 850, 90));

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
                true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAlatBayar);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 850, 90));

        tblHutang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bayar", "No. AP", "Angs. Ke", "Jt. Tempo", "Nominal", "Bayar", "Penyesuaian", "Akun Adj", "Sisa Nominal", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, true, true, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblHutang);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 850, 170));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Total :");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 80, 20));

        lblAkunKasBank.setBackground(new java.awt.Color(255, 255, 255));
        lblAkunKasBank.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAkunKasBank.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAkunKasBank.setOpaque(true);
        jPanel2.add(lblAkunKasBank, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 620, 20));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 850, 5));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Dari Kas/ Bank :");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 110, 20));

        txtAkunKasBank.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtAkunKasBank.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtAkunKasBank, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 100, 20));

        lblTotalHutang.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotalHutang.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalHutang.setText("0");
        lblTotalHutang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblTotalHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, 170, 20));

        chkBayar.setText("Hanya Status Bayar");
        jPanel2.add(chkBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 150, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, -1, 60));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel3.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 80, 30));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel3.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 110, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Total :");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 80, 20));

        lblTotalAlatBayar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotalAlatBayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalAlatBayar.setText("0");
        lblTotalAlatBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(lblTotalAlatBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 170, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 850, 50));

        setBounds(0, 0, 883, 579);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblAkunKasBank;
    private javax.swing.JLabel lblRelasi;
    private javax.swing.JLabel lblRelasiID;
    private javax.swing.JLabel lblRelasiTipe;
    private javax.swing.JLabel lblTotalAlatBayar;
    private javax.swing.JLabel lblTotalHutang;
    private javax.swing.JTable tblAlatBayar;
    private javax.swing.JTable tblHutang;
    private javax.swing.JTextField txtAkunKasBank;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNoAp;
    private javax.swing.JTextField txtRelasi;
    // End of variables declaration//GEN-END:variables

    public void setSrcForm(Object aThis) {
        this.srcForm=aThis;
    }

    public void setIsNew(boolean b) {
        this.isNew=b;
    }

    public void setNoAp(String sNoAp) {
        txtNoAp.setText(sNoAp);
    }

    private void setLocationRelativeTo(Object object) {
        
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
            
//            if(getTableSource()==null)
//                return;
            
            if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("numeric")) {
                fn.keyTyped(evt);

           }

        }
        
        public void keyReleased(KeyEvent evt){
            if(conn==null || fn==null){return;}
            if(evt.getSource().equals(txtRelasi)){
                fn.lookup(evt, new Object[]{lblRelasi, lblRelasiID, lblRelasiTipe}, 
                        "select kode, nama_relasi, id, tipe from vw_lookup_relasi "
                        + "where kode||nama_relasi ilike '%"+txtRelasi.getText()+"%' "
                        + "order by tipe, nama_relasi", txtRelasi.getWidth()+lblRelasi.getWidth()+100, 150);
            }
            else if(evt.getSource().equals(txtAkunKasBank)){
                fn.lookup(evt, new Object[]{lblAkunKasBank}, 
                        "select acc_no as no_akun, acc_name as keterangan from acc_coa "
                        + "where acc_type='01' and acc_no||acc_name ilike '%"+txtAkunKasBank.getText()+"%' "
                        + "order by no_akun", txtAkunKasBank.getWidth()+lblAkunKasBank.getWidth()+19, 100);
            }
        }
        
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!fn.isListVisible()){
                                Component c = findNextFocus();
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    
                    break;
                }
                
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(srcTable) && srcTable.getSelectedRow()>=0){
                        int iRow=srcTable.getSelectedRow();
                        ((DefaultTableModel)srcTable.getModel()).removeRow(iRow);
                        if(srcTable.getRowCount()<0)
                            return;
                        if(srcTable.getRowCount()>iRow)
                            srcTable.setRowSelectionInterval(iRow, iRow);
                        else
                            srcTable.setRowSelectionInterval(0, 0);
                    }
                    break;

                }
                
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

        public Component findNextFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component nextFocus = policy.getComponentAfter(root, c);
                if (nextFocus == null) {
                    nextFocus = policy.getDefaultComponent(root);
                }
                return nextFocus;
            }
            return null;
        }

        public Component findPrevFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component prevFocus = policy.getComponentBefore(root, c);
                if (prevFocus == null) {
                    prevFocus = policy.getDefaultComponent(root);
                }
                return prevFocus;
            }
            return null;
        }
    }
    JXDatePicker jxDatePicker=new JXDatePicker();
    
    public class myDateCellEditor extends AbstractCellEditor implements TableCellEditor{
        private Toolkit toolkit;
        
        @Override
        public Object getCellEditorValue() {
            try{
                tblAlatBayar.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                tblHutang.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                return jxDatePicker.getDate();
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
                toolkit.beep();
                
            }
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            jxDatePicker.setFormats(new String[]{"dd-MM-yyyy"});
            jxDatePicker.addFocusListener(txtFocusListener);
            jxDatePicker.setDate(value==null? new Date(): (Date)value);
            tblAlatBayar.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
            tblHutang.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
            return jxDatePicker;
            
        }
        
    }
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextComponent text = new JTextField() {

            @Override
            public void setFont(Font f) {
                super.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
            }
            
            protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
                if (hasFocus()) {
                    return super.processKeyBinding(ks, e, condition, pressed);
                } else {
                    this.requestFocus();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            processKeyBinding(ks, e, condition, pressed);
                        }
                    });
                    return true;
                }
            }
        };
        ;

        int col, row;
        private NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            srcTable=table;
            col = vColIndex;
            row = rowIndex;
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.setFont(table.getFont());
            if(vColIndex==table.getColumnModel().getColumnIndex("Nominal")){
                text.setName("textEditor");
            }
            if(vColIndex == table.getColumnModel().getColumnIndex("Nominal")){
                text.addKeyListener(kListener);
            }else{
                text.removeKeyListener(kListener);
            }
            if (isSelected) {
            }

            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
//                try {
                //Daouble dVal=Double.parseDouble(value.toString().replace(",",""));
                double dVal = fn.udfGetFloat(value.toString());
                text.setText(nf.format(dVal));
//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            } else {
                text.setText(value == null ? "" : value.toString());
            }
            return text;
        }

        public Object getCellEditorValue() {
            Object retVal = 0;
            try {
                if (col == srcTable.getColumnModel().getColumnIndex("Nominal")) {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                } else {
                    retVal = ((JTextField) text).getText();
                }
                return retVal;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }
        
    }
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;
    
    
    
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
