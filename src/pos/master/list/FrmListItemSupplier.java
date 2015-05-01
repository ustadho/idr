/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.list;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.ItemKategoriDao;
import com.ustasoft.pos.dao.jdbc.MenuAuthDao;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.domain.Item;
import com.ustasoft.pos.domain.ItemSupplier;
import com.ustasoft.pos.domain.view.ItemSupplierView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import pos.MainForm;
import pos.master.form.MasterItem;

/**
 *
 * @author cak-ust
 */
public class FrmListItemSupplier extends javax.swing.JInternalFrame {

    MyKeyListener kListener = new MyKeyListener();
    private Connection conn;
    ItemDao itemDao;
    ItemKategoriDao itemKategoriDao;
    RelasiDao supplierDao;
    private GeneralFunction fn = new GeneralFunction();
    private Component aThis;

    /**
     * Creates new form ItemKategori
     */
    public FrmListItemSupplier() {
        initComponents();
       final  TableColumnModel col=tblSupplier.getColumnModel();
        tblSupplier.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int iRow=tblSupplier.getSelectedRow();
                if(e.getColumn()==col.getColumnIndex("Disc")||
                        e.getColumn()==col.getColumnIndex("PPn")||
                        e.getColumn()==col.getColumnIndex("Harga")
                        ){
                    double  nett=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Harga")));
                            nett=fn.getDiscBertingkat(nett, tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc")).toString());
                            nett=nett*(1+fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPn")))/100);
                            
                            tblSupplier.setValueAt(nett, iRow, col.getColumnIndex("Nett"));
                }
                tblSupplier.setModel((DefaultTableModel) fn.autoResizeColWidth(tblSupplier, (DefaultTableModel) tblSupplier.getModel()).getModel());
            }
        });
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    int iRow=tblItem.getSelectedRow();
                    if(iRow<0)
                        return;
                    Integer id=Integer.valueOf(tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("ID")).toString());
                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(0);
                    List<ItemSupplierView> supp=itemDao.tampilkanSupplier(id);
                    double  nett=0;
                    for(ItemSupplierView iv:supp){
                        nett=fn.getDiscBertingkat(iv.getHarga(), iv.getDisc())
                                *(1+iv.getPpn()/100);
                            
                        ((DefaultTableModel)tblSupplier.getModel()).addRow(new Object[]{
                            iv.getId_supplier(), 
                            iv.getNama_supplier(), 
                            iv.getHarga(), 
                            iv.getDisc(), 
                            iv.getPpn(), 
                            nett
                        });
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FrmListItemSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }

    public void initForm(Connection con) {
        aThis = this;
        this.conn = con;
        
        for (int i = 0; i < tblItem.getColumnCount(); i++) {
            tblItem.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        tblItem.setRowHeight(22);
        tblSupplier.setRowHeight(22);

        jScrollPane1.addKeyListener(kListener);
        jScrollPane2.addKeyListener(kListener);
        tblItem.addKeyListener(kListener);
        tblSupplier.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, null);
        fn.addKeyListenerInContainer(jPanel2, kListener, null);
        txtCari.addKeyListener(kListener);
        btnRefresh.addKeyListener(kListener);

        MyTableCellEditor cEditor = new MyTableCellEditor();
        tblSupplier.getColumn("Harga").setCellEditor(cEditor);
        tblSupplier.getColumn("Disc").setCellEditor(cEditor);
        tblSupplier.getColumn("PPn").setCellEditor(cEditor);

        itemDao = new ItemDao(conn);
        itemKategoriDao = new ItemKategoriDao(conn);
        supplierDao = new RelasiDao(conn);
        tampilkanData(0, "");
        
        MenuAuth menuAuth= new MenuAuthDao(conn).getMenuByUsername("Master Kategori Stok", MainForm.sUserName);
        if(menuAuth!=null){
            btnSave.setEnabled(menuAuth.canInsert() || menuAuth.canUpdate());
        }
    }

    private void udfSave() {
        if(tblSupplier.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada supplier yang akan disimpan!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            List<ItemSupplier> list=new ArrayList<ItemSupplier>();
            for(int i=0; i<tblSupplier.getRowCount(); i++){
                ItemSupplier item=new ItemSupplier();
                item.setId_barang(Integer.valueOf(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("ID")).toString()));
                item.setId_supplier(Integer.valueOf(tblSupplier.getValueAt(i, tblSupplier.getColumnModel().getColumnIndex("Kode")).toString()));
                item.setHarga(GeneralFunction.udfGetDouble(tblSupplier.getValueAt(i, tblSupplier.getColumnModel().getColumnIndex("Harga"))));
                item.setDisc(tblSupplier.getValueAt(i, tblSupplier.getColumnModel().getColumnIndex("Disc")).toString());
                item.setPpn(GeneralFunction.udfGetDouble(tblSupplier.getValueAt(i, tblSupplier.getColumnModel().getColumnIndex("PPn"))));
                list.add(item);
            }
            itemDao.simpanSupplier(list);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan master harga supplier sukses!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage()+"\n"+ex.getNextException());
        }
    }

    private void setLocationRelativeTo(Object object) {
        
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextComponent text = new JTextField() {
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
            col = vColIndex;
            row = rowIndex;
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.addKeyListener(kListener);
            text.setFont(table.getFont());
            text.setName("textEditor");
            text.removeKeyListener(kListener);
//           AbstractDocument doc = (AbstractDocument)text.getDocument();
//           doc.setDocumentFilter(null);
//           doc.setDocumentFilter(new FixedSizeFilter(iText));

            text.removeKeyListener(kListener);
            text.addKeyListener(kListener);

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
                if (col == tblSupplier.getColumnModel().getColumnIndex("Harga")
                        || col == tblSupplier.getColumnModel().getColumnIndex("PPn")) {
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

    private void udfNew() {
        MasterItem master = new MasterItem(JOptionPane.getFrameForComponent(this), true);
        master.setConn(conn);
        master.setSourceForm(this);
        master.setVisible(true);
    }

    private void udfEdit() {
        int iRow = tblItem.getSelectedRow();
        if (iRow < 0) {
            return;
        }
        Integer id = Integer.valueOf(tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("ID")).toString());
        try {
            MasterItem master = new MasterItem(JOptionPane.getFrameForComponent(this), true);
            master.setConn(conn);
            master.setSourceForm(this);
            master.tampilkanItem(id);
            master.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }
    private FocusListener txtFocusListener = new FocusListener() {
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField) {
                ((JTextField) e.getSource()).setBackground(Color.YELLOW);
                if ((e.getSource() instanceof JTextField && ((JTextField) e.getSource()).getName() != null && ((JTextField) e.getSource()).getName().equalsIgnoreCase("textEditor"))) {
                    ((JTextField) e.getSource()).setSelectionStart(0);
                    ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());
                }
            }
        }

        public void focusLost(FocusEvent e) {
            if (e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")
                    || e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")) {
                ((JTextField) e.getSource()).setBackground(Color.WHITE);
            }
        }
    };

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {

//            if(getTableSource()==null)
//                return;

            if (evt.getSource() instanceof JTextField
                    && ((JTextField) evt.getSource()).getName() != null
                    && ((JTextField) evt.getSource()).getName().equalsIgnoreCase("textEditor")) {
                fn.keyTyped(evt);

            }

        }

        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if (!(evt.getSource() instanceof JTable)) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_F2: {
                    udfNew();
                    break;
                }
                case KeyEvent.VK_F3: {
                    udfEdit();
                    break;
                }
                case KeyEvent.VK_INSERT: {
                    DlgLookup d1 = new DlgLookup(JOptionPane.getFrameForComponent(aThis), true);
                    String sSupplier = "";
                    int iCol=tblSupplier.getColumnModel().getColumnIndex("ID");
                    for (int i = 0; i < tblSupplier.getRowCount(); i++) {
                        sSupplier += (sSupplier.length() == 0 ? "" : ",") + "'" + tblSupplier.getValueAt(i, 0).toString() + "'";
                    }

                    String s = "select * from ("
                            + "select coalesce(kode,'') as kode_supplier, coalesce(nama_relasi,'') as nama_supplier, id_relasi::varchar as id from "
                            + "m_relasi sp "
                            + "where is_supp=true and active=true "+
                            (sSupplier.length() > 0 ? "and id_relasi::varchar not in(" + sSupplier + ")" : "") 
                            + " order by 2) x ";

                    System.out.println(s);
//                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(tblSupplier.getRowCount()+1);
//                    tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
                    d1.setTitle("Lookup Supplier");
                    d1.udfLoad(conn, s, "(kode_supplier||nama_supplier)", null);

                    d1.setVisible(true);

                    //System.out.println("Kode yang dipilih" +d1.getKode());
                    if (d1.getKode().length() > 0) {
                        TableColumnModel col = d1.getTable().getColumnModel();
                        JTable tbl = d1.getTable();
                        int iRow = tbl.getSelectedRow();

                        ((DefaultTableModel) tblSupplier.getModel()).addRow(new Object[]{
                                    tbl.getValueAt(iRow, col.getColumnIndex("kode_supplier")).toString(),
                                    tbl.getValueAt(iRow, col.getColumnIndex("nama_supplier")).toString(),
                                    0,
                                    "0",
                                    0,
                                    0, 
                                    tbl.getValueAt(iRow, col.getColumnIndex("id")).toString()
                                });

                        tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount() - 1, tblSupplier.getRowCount() - 1);
                        tblSupplier.requestFocusInWindow();
                        tblSupplier.setModel((DefaultTableModel) fn.autoResizeColWidth(tblSupplier, (DefaultTableModel) tblSupplier.getModel()).getModel());
                        tblSupplier.changeSelection(tblSupplier.getRowCount() - 1, tblSupplier.getColumnModel().getColumnIndex("No"), false, false);

                    }
                    break;
                }
                case KeyEvent.VK_DELETE: {
                    if (evt.getSource().equals(tblSupplier)) {
                        udfDeleteSupplier();
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

    public void tampilkanData(Integer id, String sCari) {
        try {
            ((DefaultTableModel) tblItem.getModel()).setNumRows(0);
            List<Item> list = itemDao.tampilkanData(sCari);
            int i = 0;
            for (Item item : list) {
                ((DefaultTableModel) tblItem.getModel()).addRow(new Object[]{
                    item.getNama_barang(),
                    itemKategoriDao.cariKategoriByID(item.getId_kategori()).getNamaKategori(),
                    item.getSatuan(),
                    supplierDao.cariByID(item.getId_supp_default()).getNamaRelasi(),
                    item.getActive(),
                    item.getId()
                });
                if (id == item.getId()) {
                    i = tblItem.getRowCount() - 1;
                }
            }
            if (tblItem.getRowCount() > 0) {
                tblItem.setRowSelectionInterval(i, i);
                tblItem.setModel(fn.autoResizeColWidth(tblItem, ((DefaultTableModel) tblItem.getModel())).getModel());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmListItemSupplier.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void udfDeleteSupplier() {
        int iRow = tblSupplier.getSelectedRow();
        if (iRow <= 0) {
            return;
        }
        try {
            if (JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus Supplier "
                    + "'" + tblSupplier.getValueAt(iRow, tblSupplier.getColumnModel().getColumnIndex("Nama Sullier")) + "'", "Hapus Harga", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }

            itemDao.delete(Integer.parseInt(tblSupplier.getValueAt(iRow, tblSupplier.getColumnModel().getColumnIndex("ID")).toString()));
            ((DefaultTableModel) tblSupplier.getModel()).removeRow(tblSupplier.getSelectedRow());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
        tblItem = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Item - Supplier");
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

        tblItem.setAutoCreateRowSorter(true);
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Item", "Kategori", "Satuan", "Supplier Default", "Active", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
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
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblItem);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 610, 260));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informasi Supplier"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Supplier", "Harga", "Disc", "PPn", "Nett", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupplier.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSupplierKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblSupplier);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 590, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 610, 120));

        jLabel2.setText("Pencarian :");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });
        getContentPane().add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 440, 22));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 80, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("<html>\n<b>Insert : </b> Tambah Supplier\n</html>");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 355, 20));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(497, 10, 100, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 610, 40));

        setBounds(0, 0, 640, 510);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtCari.setText("");
        tampilkanData(0, "");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilkanData(0, txtCari.getText());
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm(conn);
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            ((DefaultTableModel)tblSupplier.getModel()).removeRow(tblSupplier.getSelectedRow());
        }
    }//GEN-LAST:event_tblSupplierKeyPressed

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        tampilkanData(0, txtCari.getText());
    }//GEN-LAST:event_txtCariKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
