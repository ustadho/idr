/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmApPayment.java
 *
 * Created on 09 Jan 11, 19:57:31
 */
package pos.ap;

import pos.ar.*;
import com.ustasoft.component.ColumnResizer;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccAlatBayarDao;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.domain.AccAlatBayar;
import com.ustasoft.pos.domain.view.AccCoaView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import pos.FrmReminder;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmApPayment extends javax.swing.JInternalFrame {

    private Connection conn;
    private GeneralFunction fn = new GeneralFunction();
    MyKeyListener kListener = new MyKeyListener();
    MyTableCellEditor cEditor = new MyTableCellEditor();
    private JFormattedTextField jFDate1;
    private boolean isKoreksi = false;
    private Integer arId = null;
    List<AccCoaView> coaList=new ArrayList<AccCoaView>();
    private Component srcForm;
    private String arReceiptNo="";
    private boolean jtTempo=false;
    private boolean isNew;
    private List<AccAlatBayar> listAlatBayar;

    /**
     * Creates new form FrmArReceipt
     */
    public FrmApPayment() {
        initComponents();
        jTable1.getColumn("Bayar").setCellEditor(cEditor);
        jTable1.getColumn("Diskon").setCellEditor(cEditor);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        jTable1.addKeyListener(kListener);
        jTable1.getTableHeader().setFont(jTable1.getFont());
        jTable1.setRowHeight(20);

        jTable1.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.DELETE || e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE) {
                    lblTotBayar.setText("0");
                    lblTotJual.setText("0");
                    lblTotTerbayar.setText("0");
                    lblTotSisa.setText("0");
                    lblTotDiskon.setText("0");

                    if (e.getColumn() == jTable1.getColumnModel().getColumnIndex("Pilih")) {
                        boolean yes = (Boolean) jTable1.getValueAt(jTable1.getSelectedRow(), e.getColumn());
                        jTable1.setValueAt(yes ? fn.udfGetDouble(jTable1.getValueAt(jTable1.getSelectedRow(), jTable1.getColumnModel().getColumnIndex("Sisa"))) : 0,
                                jTable1.getSelectedRow(), jTable1.getColumnModel().getColumnIndex("Bayar"));
                    }
                    double dNilaiJual = 0, dTerbayar = 0, dSisa = 0, dBayar = 0, dDiskon = 0;
                    TableColumnModel col = jTable1.getColumnModel();

                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        dNilaiJual += fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Nilai Pembelian")));
                        dTerbayar += fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Terbayar")));
                        dSisa += fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Sisa")));
                        dBayar += fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar")));
                        dDiskon += fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon")));
                    }
                    lblTotJual.setText(fn.intFmt.format(dNilaiJual));
                    lblTotTerbayar.setText(fn.intFmt.format(dTerbayar));
                    lblTotSisa.setText(fn.intFmt.format(dSisa));
                    lblTotBayar.setText(fn.intFmt.format(dBayar));
                    lblTotDiskon.setText(fn.intFmt.format(dDiskon));
                }
            }
        });
        jTable1.getColumn("Tanggal").setCellRenderer(new MyRowRenderer());
    }

    public void setConn(Connection con) {
        this.conn = con;
        fn.setConn(conn);
    }

    public void setKoreksi(boolean b) {
        isKoreksi = b;
    }

    private void udfInitForm() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("select current_date as tgl2 ");
            if (rs.next()) {
                jXDatePicker1.setDate(rs.getDate(1));
                jXDatePicker1.setFormats("dd/MM/yyyy");
            }

            rs.close();
            AccCoaDao coaDao=new AccCoaDao(conn);
            coaList= coaDao.listKasBank();
            cmbAkun.removeAllItems();
            for(AccCoaView l:coaList){
                cmbAkun.addItem(l.getAcc_name()+" - "+l.getAcc_no());
            }
            AccAlatBayarDao coaAB=new AccAlatBayarDao(conn);
            listAlatBayar=coaAB.cariSemuaData();
            cmbAlatBayar.removeAllItems();
            for (AccAlatBayar l : listAlatBayar) {
                cmbAlatBayar.addItem(l.getAlatBayar());
            }
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(FrmArReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((arId == null && arReceiptNo==null)) {
            udfNew();
        }
        txtNoTrx.setEnabled(isKoreksi);
        jTable1.getColumn("IdPembelian").setMinWidth(0);
        jTable1.getColumn("IdPembelian").setMaxWidth(0);
        jTable1.getColumn("IdPembelian").setPreferredWidth(0);
        btnNew.setVisible(arReceiptNo.equalsIgnoreCase("") &&  arId==null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (isKoreksi) {
                    txtNoTrx.requestFocusInWindow();
                }else if(arId !=null || !arReceiptNo.equalsIgnoreCase("")){
                    jTable1.requestFocusInWindow();
                } else {
                    txtSupplier.requestFocusInWindow();
                }
            }
        });
        if(!arReceiptNo.equalsIgnoreCase("")){
            loadDetail(arReceiptNo);
        }
    }

    private void udfLoadAP() {
        try {
            ((DefaultTableModel) jTable1.getModel()).setNumRows(0);
            String sql="select * from fn_ap_load_outstanding_supp(" + txtSupplier.getText() + ", "+jtTempo+") "
                    + "as (id bigint, keterangan varchar, invoice varchar, invoice_date date, invoice_amount double precision, "
                    + "paid_amount double precision, owing double precision) "
                    + "order by id";
            System.out.println(sql);
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{
                    rs.getString("invoice"),
                    rs.getDate("invoice_date"),
                    rs.getDouble("invoice_amount"),
                    rs.getDouble("paid_amount"),
                    rs.getDouble("owing"),
                    arId != null && arId == rs.getInt("id") ? rs.getDouble("owing"): 0 ,
                    0,
                    arId != null && arId == rs.getInt("id"),
                    rs.getInt("id")
                });
                if(arId !=null && arId == rs.getInt("id")){
                    jTable1.changeSelection(jTable1.getRowCount()-1, 5, false, false);
                }
            }
            ColumnResizer.adjustColumnPreferredWidths(jTable1);
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        txtNoTrx.setText("");
        txtSupplier.setText("");
        txtNamaSupplier.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel) jTable1.getModel()).setNumRows(0);

//        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Cancel.png"))); // NOI18N
//        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);
//        btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        txtSupplier.requestFocus();
    }

    public void setId(Integer id) {
        this.arId = id;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select i.supplier_id, r.nama_relasi\n"
                    + "from ap_inv i \n"
                    + "inner join m_Relasi r on r.id_relasi=i.supplier_id\n"
                    + "where i.id=" + id);
            if (rs.next()) {
                txtSupplier.setText(rs.getString("supplier_id"));
                txtNamaSupplier.setText(rs.getString("nama_relasi"));
                udfLoadAP();
                
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmArReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadDetail(String arNo){
        this.arReceiptNo=arNo;
        String sql="select h.payment_no, h.tanggal, h.id_relasi, coalesce(r.nama_relasi,'') as nama_supplier, \n" +
                    "coalesce(h.acc_no,'') as acc_no, coalesce(coa.acc_name,'') as acc_name, coalesce(h.check_no,'') as no_bukti, "
                +   "coalesce(check_amount,0) as amount, \n" +
                    "coalesce(h.memo,'') as memo\n" +
                    "from ap_payment h\n" +
                    "left join m_relasi r on r.id_relasi=h.id_relasi\n" +
                    "left join acc_coa coa on coa.acc_no=h.acc_no\n" +
                    "where h.payment_no='"+arNo+"'";
        try {
            ResultSet rs=conn.createStatement().executeQuery(sql);
            if(rs.next()){
                txtNoTrx.setText(rs.getString("payment_no"));
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                txtSupplier.setText(rs.getString("id_relasi"));
                txtNamaSupplier.setText(rs.getString("nama_supplier"));
                cmbAkun.setSelectedItem(rs.getString("acc_name")+" - "+rs.getString("acc_no"));
                txtNoBukti.setText(rs.getString("no_bukti"));
                txtTotalPembayaran.setText(GeneralFunction.intFmt.format(rs.getDouble("amount")));
                txtKeterangan.setText(rs.getString("memo"));
                txtNamaSupplier.setEnabled(false);
                rs.close();
                sql="select i.invoice_no, i.invoice_date, coalesce(i.invoice_amount, 0) as invoice_amount, \n" +
                    "coalesce(i.paid_amount,0) as paid_amount, coalesce(i.owing, 0) as owing,\n" +
                    "d.bayar, d.discount, d.ap_id, d.serial_no \n" +
                    "from ap_payment_detail d\n" +
                    "inner join ap_inv i on i.id=d.ap_id\n" +
                    "where d.payment_no='"+arNo+"' order by d.serial_no";
                ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sql);
                while(rs.next()){
                    ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                        rs.getString("invoice_no"), 
                        rs.getDate("invoice_date"),
                        rs.getDouble("invoice_amount"), 
                        rs.getDouble("paid_amount"), 
                        rs.getDouble("owing"), 
                        rs.getDouble("bayar"), 
                        rs.getDouble("discount"), 
                        true, 
                        rs.getInt("ap_id"),
                        rs.getInt("serial_no")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmArReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void setSrcForm(Component aThis) {
        this.srcForm=aThis;
    }

    public void setArReceiptNo(String arNo) {
        this.arReceiptNo=arNo;
    }

    public void setJtTempo(boolean b) {
        this.jtTempo=b;
    }

    void setIsNew(boolean b) {
        this.isNew=b;
    }

    
    
    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent evt) {
        }

        @Override
        public void keyTyped(KeyEvent evt) {
            if(evt.getSource().equals(txtTotalPembayaran))
                GeneralFunction.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_F4: {
                    udfNew();
                    break;
                }
                case KeyEvent.VK_F5: {
                    if(!txtNoTrx.getText().equalsIgnoreCase(""))
                        udfInsert();
                    else
                        udfUpdate();
                        
                    break;
                }
                case KeyEvent.VK_F9: {
//                    if(tblDetail.getRowCount()==0) return;
//                    ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
//                        tblHeader.getRowCount()+1, "T", 0
//                    });
//                    tblHeader.requestFocusInWindow();
//                    tblHeader.requestFocus();
//                    tblHeader.setRowSelectionInterval(tblHeader.getRowCount()-1, tblHeader.getRowCount()-1);
//                    tblHeader.changeSelection(tblHeader.getRowCount()-1, 1, false, false);
                    break;
                }
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable)) {
                        if (!fn.isListVisible()) {
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(table.getSelectedColumn()==0){
//                                    //table.setValueAt(((JTextField)evt.getSource()).getText(), table.getSelectedRow(), 0);
//                                    //table.changeSelection(table.getSelectedRow(), 2, false, false);
//                                    //table.setColumnSelectionInterval(2, 2);
//                                }
//                            }

                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            if (c.isEnabled()) {
                                c.requestFocus();
                            } else {
                                c = findNextFocus();
                                if (c != null) {
                                    c.requestFocus();
                                };
                            }
                        } else {
                            fn.lstRequestFocus();
                        }
                    } else {
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
//                    if(ct instanceof JTable){
//                        //if(((JTable)ct).getSelectedRow()==0){
//                            Component c = findNextFocus();
//                            if (c==null) return;
//                            if(c.isEnabled())
//                                c.requestFocus();
//                            else{
//                                c = findNextFocus();
//                                if (c!=null) c.requestFocus();;
//                            }
//                        //}
//                    }else{
                    if (!(ct instanceof JTable) && !fn.isListVisible()) {
                        Component c = findNextFocus();
                        if (c == null) {
                            return;
                        }
                        if (c.isEnabled()) {
                            c.requestFocus();
                        } else {
                            c = findNextFocus();
                            if (c != null) {
                                c.requestFocus();
                            };
                        }
                    } else {
                        fn.lstRequestFocus();
                    }
                    break;
                    //}
                }

                case KeyEvent.VK_UP: {
                    if (ct instanceof JTable) {
                        if (((JTable) ct).getSelectedRow() == 0) {
                            Component c = findPrevFocus();
                            if (c == null) {
                                return;
                            }
                            if (c.isEnabled()) {
                                c.requestFocus();
                            } else {
                                c = findNextFocus();
                                if (c != null) {
                                    c.requestFocus();
                                };
                            }
                        }
                    } else {
                        Component c = findPrevFocus();
                        if (c == null) {
                            return;
                        }
                        if (c.isEnabled()) {
                            c.requestFocus();
                        } else {
                            c = findNextFocus();
                            if (c != null) {
                                c.requestFocus();
                            };
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(jTable1) && jTable1.getSelectedRow()>=0){
                        int iRow[]= jTable1.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= jTable1.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(jTable1.convertRowIndexToModel(iRow[0]));
                            iRow = jTable1.getSelectedRows();
                        }
                        jTable1.clearSelection();

                        if(jTable1.getRowCount()>0 && rowPalingAtas<jTable1.getRowCount()){
                            jTable1.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(jTable1.getRowCount()>0)
                                jTable1.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                jTable1.requestFocus();
                        }
                        if(jTable1.getSelectedRow()>=0)
                            jTable1.changeSelection(jTable1.getSelectedRow(), 0, false, false);

                        if(jTable1.getCellEditor()!=null)
                            jTable1.getCellEditor().stopCellEditing();
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

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextField text = ustTextField;
        int col, row;

        public Component getTableCellEditorComponent(JTable tblDetail, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row = rowIndex;
            col = vColIndex;
            text = ustTextField;
            text.setName("textEditor");

            text.addKeyListener(kListener);

            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.setFont(tblDetail.getFont());
//           text.setVisible(!lookupItem.isVisible());
//            if(lookupItem.isVisible()){
//                return null;
//            }
            text.setText(value == null ? "" : value.toString());

            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
                text.setText(fn.dFmt.format(value));
            } else {
                text.setText(value == null ? "" : value.toString());
            }
            return text;
        }

        public Object getCellEditorValue() {
            Object o = "";//=component.getText();
            Object retVal = 0;
            try {
                if (jTable1.getSelectedColumn() == 0) {
                    retVal = ((JTextField) text).getText();

                } else {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                }
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }

        public boolean isVisible() {
            return text.isVisible();
        }
    }
    JTextField ustTextField = new JTextField() {
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
    private FocusListener txtFocusListener = new FocusListener() {
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField) {
                ((JTextField) e.getSource()).setBackground(Color.YELLOW);
                if ((e.getSource() instanceof JTextField || (((JTextField) e.getSource()).getName() != null && ((JTextField) e.getSource()).getName().equalsIgnoreCase("textEditor")))) {
                    ((JTextField) e.getSource()).setSelectionStart(0);
                    ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());
                }
            }
        }

        public void focusLost(FocusEvent e) {
            if (e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")
                    || e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")) {
                ((JTextField) e.getSource()).setBackground(Color.WHITE);
                if (e.getSource().equals(txtSupplier) && txtSupplier.getText().length() > 0 && arReceiptNo.equalsIgnoreCase("")) {
                    udfLoadAP();
                }

            }
        }
    };
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());

            }
            JCheckBox checkBox = new JCheckBox();
            if (value instanceof Date) {
                value = dmy.format(value);
            }
            if (value instanceof Timestamp) {
                value = dmy.format(value);
            } else if (value instanceof Double || value instanceof Integer || value instanceof Float) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                value = fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }

    private String getMessageBeforeSave() {
        String sMessage = "";
        btnSave.requestFocus();
        if (txtSupplier.getText().trim().length() == 0) {
            if (!txtSupplier.isFocusOwner()) {
                txtSupplier.requestFocus();
            }
            return "Silakan isi Pelanggan terlebih dulu!";
        }
        if (txtNoTrx.getText().equalsIgnoreCase("") && jTable1.getRowCount() == 0) {
            jTable1.requestFocusInWindow();

            return "Tabel pembayaran pelanggan masih kosong!";
        }
        if(cmbAkun.getSelectedIndex()<0){
            cmbAkun.requestFocus();
            return "Silahkan pilih akun terlebih dulu!";
        }
        if (txtNoTrx.getText().equalsIgnoreCase("") && fn.udfGetDouble(lblTotBayar.getText()) + fn.udfGetDouble(lblTotDiskon.getText()) == 0) {
            jTable1.requestFocusInWindow();
            jTable1.changeSelection(0, 5, false, false);
            return "Total Nota yang dibayar masih kosong!";
        }
        if (txtNoTrx.getText().equalsIgnoreCase("") && fn.udfGetDouble(txtTotalPembayaran.getText()) <=0) {
            txtTotalPembayaran.requestFocus();
            return "Masukkan Total Pembayaran terlebih dulu!";
        }

        return sMessage;
    }

    private void udfInsert() {
        String sMsg = getMessageBeforeSave();
        if (sMsg.length() > 0) {
            JOptionPane.showMessageDialog(this, sMsg);
            return;
        }
        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            ResultSet rs = conn.createStatement().executeQuery("select fn_get_ap_no('" + ymd.format(jXDatePicker1.getDate()) + "')");
            if (rs.next()) {
                txtNoTrx.setText(rs.getString(1));
            } else {
                rs.close();
                return;
            }
            conn.setAutoCommit(false);
            Integer idPembelian;
            TableColumnModel col = jTable1.getColumnModel();
            String sQry = "INSERT INTO ap_payment("
                    + "payment_no, tanggal, id_relasi, alat_bayar, rate, " //4
                    + "memo, acc_no, check_no, tgl_cek, date_ins, "//8
                    + "user_ins, check_amount)    VALUES ("
                    + "?, ?, ?, ?, 1, " //4
                    + "?, ?, ?, ?, now()," //8
                    + "?, ?);\n";
            PreparedStatement ps=conn.prepareStatement(sQry);
            ps.setString(1, txtNoTrx.getText());
            ps.setDate(2, new java.sql.Date(jXDatePicker1.getDate().getTime()));
            ps.setInt(3, fn.udfGetInt(txtSupplier.getText()));
            ps.setString(4, listAlatBayar.get(cmbAlatBayar.getSelectedIndex()).getKode());
            ps.setString(5, txtKeterangan.getText());
            ps.setString(6, coaList.get(cmbAkun.getSelectedIndex()).getAcc_no());
            ps.setString(7, txtNoBukti.getText());
            ps.setDate(8,  jXDateJatuhTempo.getDate()==null? null: new java.sql.Date(jXDateJatuhTempo.getDate().getTime()));
            ps.setString(9, MainForm.sUserName);
            ps.setDouble(10, GeneralFunction.udfGetDouble(txtTotalPembayaran.getText()));
            ps.executeUpdate();
            
            sQry="";
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                if (GeneralFunction.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar"))) > 0) {
                    idPembelian = fn.udfGetInt(jTable1.getValueAt(i, col.getColumnIndex("IdPembelian")));
                    sQry += "INSERT INTO ap_payment_detail(payment_no, ap_id , "
                            + "bayar, discount, "
                            + "date_ins) VALUES ("
                            + "'" + txtNoTrx.getText() + "', "
                            + idPembelian + ", "
                            + fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar"))) + ", " + fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon"))) + ", "
                            + "now());\n";
                }
            }
            System.out.println(sQry);
            conn.createStatement().executeUpdate(sQry);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan pembayaran supplier sukses!");
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            if(arId ==null && arReceiptNo.equalsIgnoreCase(""))
                udfNew();
            else{
                if(srcForm instanceof FrmReminder){
                    ((FrmReminder)srcForm).refresh();
                }
                this.dispose();
            }
            
        } catch (SQLException se) {
            try {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                System.out.println("Error: " + se.getMessage());
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());

            } catch (SQLException ex) {
                Logger.getLogger(FrmApPayment.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    private void udfUpdate() {
        String sMsg = getMessageBeforeSave();
        if (sMsg.length() > 0) {
            JOptionPane.showMessageDialog(this, sMsg);
            return;
        }
        
        try {
            conn.setAutoCommit(false);
            if(jTable1.getRowCount()==0 && JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus data pembayaran supplier ini?", "Hapus AP", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                conn.createStatement().executeUpdate("delete from ap_payment where payment_no='"+txtNoTrx.getText()+"'");
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, "Delete pembayaran sukses!");
                if(srcForm instanceof FrmApPaymentHis){
                    ((FrmApPaymentHis)srcForm).udfLoadData("");
                }
                return;
            }
            PreparedStatement ps=conn.prepareStatement("update ap_payment "
                    + "set tanggal=?, " //1
                    + "id_relasi=?, " //2
                    + "check_no=?, " //3
                    + "tgl_cek=?, " //4
                    + "memo=?, " //5
                    + "check_amount=?, " //6
                    + "void_check=?, " //7
                    + "date_upd=now(), " 
                    + "user_upd=?, " //8
                    + "acc_no=?, " //9
                    + "alat_bayar=? " //10
                    + "where payment_no=?"); //11
            ps.setDate(1, new java.sql.Date(jXDatePicker1.getDate().getTime()));
            ps.setInt(2, fn.udfGetInt(txtSupplier.getText()));
            ps.setString(3, txtNoBukti.getText());
            ps.setDate(4, jXDateJatuhTempo.getDate()==null? null: new java.sql.Date(jXDateJatuhTempo.getDate().getTime()));
            ps.setString(5, txtKeterangan.getText());
            ps.setDouble(6, fn.udfGetDouble(txtTotalPembayaran.getText()));
            ps.setBoolean(7, false);
            ps.setString(8, MainForm.sUserName);
            ps.setString(9, coaList.get(cmbAkun.getSelectedIndex()).getAcc_no());
            ps.setString(10, listAlatBayar.get(cmbAlatBayar.getSelectedIndex()).getKode());
            ps.setString(11, txtNoTrx.getText());
            ps.executeUpdate();
            
            TableColumnModel col=jTable1.getColumnModel();
            PreparedStatement psd=conn.prepareStatement("UPDATE ap_payment_detail\n" +
                "   SET ap_id=?, bayar=?, discount=? \n" +
                " WHERE serial_no=?;");
            String in="";
            for(int i=0; i<jTable1.getRowCount(); i++){
                psd.setInt(1, (Integer)jTable1.getValueAt(i, col.getColumnIndex("IdPembelian")));
                psd.setDouble(2, (Double)jTable1.getValueAt(i, col.getColumnIndex("Bayar")));
                psd.setDouble(3, (Double)jTable1.getValueAt(i, col.getColumnIndex("Diskon")));
                psd.setInt(4, (Integer)jTable1.getValueAt(i, col.getColumnIndex("IdPaymentDet")));
                psd.addBatch();
                in+= (in.length()>0? ", ": "") +
                    jTable1.getValueAt(i, col.getColumnIndex("IdPaymentDet")).toString();
            }
            
            psd.executeBatch();
            System.out.println("In: "+in);
            if(in.length() > 0){
                int c=conn.createStatement().executeUpdate("delete from ap_payment_detail where "
                        + "payment_no='"+txtNoTrx.getText()+"' "
                        + (in.length()>0? "and serial_no not in("+in+")" : "") );
                
            }
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Update data sukses!");
            if(srcForm instanceof FrmApPaymentHis){
                ((FrmApPaymentHis)srcForm).udfLoadData(txtNoTrx.getText());
            }
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(FrmApPayment.class.getName()).log(Level.SEVERE, null, ex);
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
        txtSupplier = new javax.swing.JTextField();
        txtNamaSupplier = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbAkun = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        txtNoBukti = new javax.swing.JTextField();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel9 = new javax.swing.JLabel();
        txtTotalPembayaran = new javax.swing.JTextField();
        btnCheck = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jXDateJatuhTempo = new org.jdesktop.swingx.JXDatePicker();
        cmbAlatBayar = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblTotBayar = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        lblTotDiskon = new javax.swing.JLabel();
        lblTotSisa = new javax.swing.JLabel();
        lblTotTerbayar = new javax.swing.JLabel();
        lblTotJual = new javax.swing.JLabel();
        lblTotal6 = new javax.swing.JLabel();
        lblTotal7 = new javax.swing.JLabel();
        lblTotal8 = new javax.swing.JLabel();
        lblTotal9 = new javax.swing.JLabel();
        lblTotal10 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pembayaran Hutang Supplier");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Kas/ Bank : ");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSupplier.setName("txtSupplier"); // NOI18N
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 90, 20));

        txtNamaSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaSupplier.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaSupplier.setEnabled(false);
        txtNamaSupplier.setName("txtNamaSupplier"); // NOI18N
        jPanel1.add(txtNamaSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 35, 510, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("No. Transaksi");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoTrx.setEnabled(false);
        txtNoTrx.setName("txtNoTrx"); // NOI18N
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 140, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Tanggal");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 90, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Keterangan");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 135, 90, 20));

        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 135, 605, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Supplier :");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAkun.setName("cmbAkun"); // NOI18N
        jPanel1.add(cmbAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 415, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Alat Bayar : ");
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtNoBukti.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNoBukti.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoBukti.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoBukti.setName("txtNoBukti"); // NOI18N
        jPanel1.add(txtNoBukti, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 150, 20));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 10, 155, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Total Pembayaran : ");
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 85, 130, 20));

        txtTotalPembayaran.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtTotalPembayaran.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTotalPembayaran.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTotalPembayaran.setName("txtTotalPembayaran"); // NOI18N
        jPanel1.add(txtTotalPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 85, 120, 20));

        btnCheck.setText("..");
        btnCheck.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCheck.setName("btnCheck"); // NOI18N
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        jPanel1.add(btnCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 85, 25, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Jatuh Tempo :");
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, 90, 20));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("No. Cek/ Giro : ");
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 90, 20));

        jXDateJatuhTempo.setName("jXDateJatuhTempo"); // NOI18N
        jPanel1.add(jXDateJatuhTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 110, 145, -1));

        cmbAlatBayar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAlatBayar.setName("cmbAlatBayar"); // NOI18N
        cmbAlatBayar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAlatBayarItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbAlatBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 260, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Pembelian", "Tanggal", "Nilai Pembelian", "Terbayar", "Sisa", "Bayar", "Diskon", "Pilih", "IdPembelian", "IdPaymentDet"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        lblTotBayar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotBayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotBayar.setText("0");
        lblTotBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotBayar.setName("lblTotBayar"); // NOI18N

        lblTotal1.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal1.setText("Diskon");
        lblTotal1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal1.setName("lblTotal1"); // NOI18N
        lblTotal1.setOpaque(true);

        lblTotDiskon.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotDiskon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotDiskon.setText("0");
        lblTotDiskon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotDiskon.setName("lblTotDiskon"); // NOI18N

        lblTotSisa.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotSisa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotSisa.setText("0");
        lblTotSisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotSisa.setName("lblTotSisa"); // NOI18N

        lblTotTerbayar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotTerbayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotTerbayar.setText("0");
        lblTotTerbayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotTerbayar.setName("lblTotTerbayar"); // NOI18N

        lblTotJual.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotJual.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotJual.setText("0");
        lblTotJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotJual.setName("lblTotJual"); // NOI18N

        lblTotal6.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotal6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal6.setText("TOTAL  ");
        lblTotal6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTotal6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal6.setName("lblTotal6"); // NOI18N
        lblTotal6.setOpaque(true);

        lblTotal7.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal7.setText("Terbayar");
        lblTotal7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal7.setName("lblTotal7"); // NOI18N
        lblTotal7.setOpaque(true);

        lblTotal8.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal8.setText("Sisa");
        lblTotal8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal8.setName("lblTotal8"); // NOI18N
        lblTotal8.setOpaque(true);

        lblTotal9.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal9.setText("Bayar");
        lblTotal9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal9.setName("lblTotal9"); // NOI18N
        lblTotal9.setOpaque(true);

        lblTotal10.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal10.setText("Nilai Pembelian");
        lblTotal10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal10.setName("lblTotal10"); // NOI18N
        lblTotal10.setOpaque(true);

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.setName("btnNew"); // NOI18N
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 75, 30));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setFocusable(false);
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 10, 85, 30));

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnCancel.setText("Keluar");
        btnCancel.setFocusable(false);
        btnCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 80, 30));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("Pembayaran Supplier");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTotal6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotJual, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotTerbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(7, 7, 7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblTotJual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblTotTerbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblTotSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblTotBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblTotDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        setBounds(0, 0, 743, 457);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        fn.lookup(evt, new Object[]{txtNamaSupplier},
                "select id_relasi::varchar as id, coalesce(nama_relasi,'') as nama_customer "
                + "from m_relasi "
                + "where is_cust=false and id_relasi::varchar||coalesce(nama_relasi,'') ilike '%" + txtSupplier.getText() + "%'",
                txtSupplier.getWidth() + txtNamaSupplier.getWidth() + 20, 200);
}//GEN-LAST:event_txtSupplierKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(txtNoTrx.getText().equalsIgnoreCase(""))
            udfInsert();
        else
            udfUpdate();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (btnCancel.getText().equalsIgnoreCase("cancel")) {
            if (getTitle().indexOf("Revision") > 0) {
                dispose();
            }
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Exit.png"))); // NOI18N
        } else {
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        txtTotalPembayaran.setText(lblTotBayar.getText());
    }//GEN-LAST:event_btnCheckActionPerformed

    private void cmbAlatBayarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAlatBayarItemStateChanged
        if(cmbAlatBayar.getSelectedIndex()>0 && listAlatBayar.size() > 0){
            setJatuhTempo();
        }
    }//GEN-LAST:event_cmbAlatBayarItemStateChanged

    private void setJatuhTempo() {
        if(listAlatBayar.get(cmbAlatBayar.getSelectedIndex()).isLangsungCair()){
//            jXDateJatuhTempo.setDate(jXDatePicker1.getDate());
            jXDateJatuhTempo.setDate(null);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JComboBox cmbAlatBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private org.jdesktop.swingx.JXDatePicker jXDateJatuhTempo;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblTotBayar;
    private javax.swing.JLabel lblTotDiskon;
    private javax.swing.JLabel lblTotJual;
    private javax.swing.JLabel lblTotSisa;
    private javax.swing.JLabel lblTotTerbayar;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal10;
    private javax.swing.JLabel lblTotal6;
    private javax.swing.JLabel lblTotal7;
    private javax.swing.JLabel lblTotal8;
    private javax.swing.JLabel lblTotal9;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNamaSupplier;
    private javax.swing.JTextField txtNoBukti;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTotalPembayaran;
    // End of variables declaration//GEN-END:variables
}
