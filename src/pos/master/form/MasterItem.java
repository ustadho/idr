/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos.master.form;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.ItemKategoriDao;
import com.ustasoft.pos.dao.jdbc.RelasiDao;
import com.ustasoft.pos.dao.jdbc.SatuanDao;
import com.ustasoft.pos.domain.Item;
import com.ustasoft.pos.domain.ItemCoa;
import com.ustasoft.pos.domain.ItemKategori;
import com.ustasoft.pos.domain.Relasi;
import com.ustasoft.pos.domain.Satuan;
import com.ustasoft.pos.domain.view.AccCoaView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import pos.master.list.FrmListItem;

/**
 *
 * @author cak-ust
 */
public class MasterItem extends javax.swing.JDialog {

    private Connection conn;
    private Object srcForm;
    private RelasiDao daoSupplier;
    private ItemDao daoItem;
    private SatuanDao daoSatuan;
    private ItemKategoriDao daoItemKategori;
    private AccCoaDao daoAcc;
    private String sOldKode = "";
    GeneralFunction fn = new GeneralFunction();
    List<ItemKategori> itemKategori;
    List<AccCoaView> akunPersediaan;
    List<AccCoaView> akunPembelian;
    List<AccCoaView> akunReturPembelian;
    List<AccCoaView> akunPenjualan;
    List<AccCoaView> akunReturPenjualan;
    List<AccCoaView> akunHpp;
    List<Relasi> supplier;
    List<Satuan> satuan;
    private JFileChooser m_chooser = new JFileChooser();
    
    private FileFilter fFilter = new FileFilter() {
        public boolean accept(File f) {
            return (f.getName().toLowerCase().endsWith(".jpg")) || (f.isDirectory());
        }

        public String getDescription() {
            return "Image files (*.jpg)";
        }
    };
    private String sFotoFile="";
    private FileInputStream fisFoto;
    
    private void PilihFoto() {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    FileInputStream in = null;
    try {
      m_chooser.setDialogTitle("Pilih foto");
      m_chooser.setFileFilter(fFilter);
      if (this.m_chooser.showOpenDialog(this) != 0) {
        return;
      }
      File fChoosen = this.m_chooser.getSelectedFile();
      in = new FileInputStream(fChoosen);
      ImageIcon myIcon = new ImageIcon(this.m_chooser.getSelectedFile().toString());

        ImageIcon bigImage = new ImageIcon(myIcon.getImage().getScaledInstance(
                lblImage.getWidth(), 
                lblImage.getHeight(), 8));

        sFotoFile = this.m_chooser.getSelectedFile().toString();
        fisFoto = in;
        lblImage.setIcon(bigImage);
      setCursor(new Cursor(0));
      in.close();
    }
    catch (IOException ex) {
      setCursor(new Cursor(0));
      JOptionPane.showMessageDialog(this, ex.getMessage());
    } finally {
      try {
        setCursor(new Cursor(0));
        if (in != null) in.close(); 
      }
      catch (IOException ex) { 
          Logger.getLogger(MasterItem.class.getName()).log(Level.SEVERE, null, ex); 
      }

    }
  }

    /**
     * Creates new form MasterCoa
     */
    public MasterItem(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fn.addKeyListenerInContainer(jPanel2, new MyKeyListener(), txtFocusListener);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                txtNama.requestFocusInWindow();
            }
        });
    }

    public void setSourceForm(Object obj) {
        srcForm = obj;

    }

    public void setConn(Connection con) {
        this.conn = con;
        daoSupplier = new RelasiDao(conn);
        udfInitForm();
    }

    private void udfClear() {
        lblID.setText("");
        txtNama.setText("");
        txtNama.setText("");
        txtBarcode.setText("");
        chkActive.setSelected(true);
    }

    private void udfInitForm() {
        try {
            daoItemKategori = new ItemKategoriDao(conn);
            daoItem = new ItemDao(conn);
            daoSatuan = new SatuanDao(conn);

            cmbKategori.removeAllItems();
            itemKategori = daoItemKategori.cariSemuaKategori();
            for (ItemKategori kat : itemKategori) {
                cmbKategori.addItem(kat.getNamaKategori());
            }

            cmbSatuan.removeAllItems();
            satuan = daoSatuan.cariSemuaData();
            for (Satuan sat : satuan) {
                cmbSatuan.addItem(sat.getSatuan());
            }

            cmbSupplier.removeAllItems();
            daoSupplier = new RelasiDao(conn);
            supplier = daoSupplier.cariSemuaData(1);
            for (Relasi supp : supplier) {
                cmbSupplier.addItem(supp.getNamaRelasi());
            }

            daoAcc = new AccCoaDao(conn);
            cmbAkunPersediaan.removeAllItems();
            akunPersediaan = daoAcc.cariCoaByAccType("20");
            for (AccCoaView lcoa : akunPersediaan) {
                cmbAkunPersediaan.addItem(lcoa.getAcc_name());
            }

            cmbAkunPembelian.removeAllItems();
            akunPembelian = daoAcc.cariSemuaCoa("");
            for (AccCoaView lcoa : akunPembelian) {
                cmbAkunPembelian.addItem(lcoa.getAcc_name());
            }

            cmbAkunReturPembelian.removeAllItems();
            akunReturPembelian = daoAcc.cariSemuaCoa("");
            for (AccCoaView lcoa : akunReturPembelian) {
                cmbAkunReturPembelian.addItem(lcoa.getAcc_name());
            }

            cmbAkunPenjualan.removeAllItems();
            akunPenjualan = daoAcc.cariSemuaCoa("11");
            for (AccCoaView lcoa : akunPenjualan) {
                cmbAkunPenjualan.addItem(lcoa.getAcc_name());
            }
            cmbAkunReturPenjualan.removeAllItems();
            akunReturPenjualan = daoAcc.cariSemuaCoa("");
            for (AccCoaView lcoa : akunReturPenjualan) {
                cmbAkunReturPenjualan.addItem(lcoa.getAcc_name());
            }

            cmbAkunHpp.removeAllItems();
            akunHpp = daoAcc.cariSemuaCoa("");
            for (AccCoaView lcoa : akunHpp) {
                cmbAkunHpp.addItem(lcoa.getAcc_name());
            }


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private boolean udfCekBeforeSave(){
        if (txtNama.getText().length() == 0) {
             JOptionPane.showMessageDialog(this, "Nama Stok harus diisi!", "Simpan Stok", JOptionPane.INFORMATION_MESSAGE);
             txtNama.requestFocus();
             return false;
         }
         if(cmbSatuan.getSelectedIndex()<0){
             JOptionPane.showMessageDialog(this, "Silahkan pilih satuan terlebih dulu!", "Simpan Stok", JOptionPane.INFORMATION_MESSAGE);
             cmbSatuan.requestFocus();
             return false;
         }
         if(txtPLU.getText().trim().length()>0){
             try{
                 ResultSet rs=conn.createStatement().executeQuery("select * from m_item where plu='"+txtPLU.getText()+"' and id<>"+
                         (lblID.getText().equalsIgnoreCase("")? "0": lblID.getText()));
                 if(rs.next()){
                     JOptionPane.showMessageDialog(this, "PLU tersebu sudah digunakan untuk item '"+rs.getString("nama_barang") +"'!", "Simpan Item", JOptionPane.INFORMATION_MESSAGE);
                     txtPLU.requestFocus();
                     rs.close();
                     return false;
                 }
                 rs.close();
             }catch(SQLException se){
                 JOptionPane.showMessageDialog(this, se.getMessage());
             }
         }
         return true;
    }
    
    private void udfSave() {
        try {
            btnSave.requestFocusInWindow();
            if(!udfCekBeforeSave())
                return;
            conn.setAutoCommit(false);
            Integer id = lblID.getText().trim().equalsIgnoreCase("") ? null : Integer.parseInt(lblID.getText());
            Integer idKota = cmbKategori.getSelectedIndex() < 0 ? null : itemKategori.get(cmbKategori.getSelectedIndex()).getId();
            Integer idTipeSupp = cmbSupplier.getSelectedIndex() < 0 ? null : supplier.get(cmbSupplier.getSelectedIndex()).getIdRelasi();
            Item item = new Item();
            item.setId(id);
            item.setPlu(txtPLU.getText());
            item.setBarcode(txtBarcode.getText());
            item.setNamaBarang(txtNama.getText());
            item.setId_kategori(itemKategori.get(cmbKategori.getSelectedIndex()).getId());
            item.setSatuan(cmbSatuan.getSelectedItem().toString());
            item.setIdSatuan(satuan.get(cmbSatuan.getSelectedIndex()).getId());
            item.setId_supp_default(supplier.get(cmbSupplier.getSelectedIndex()).getIdRelasi());
            item.setActive(chkActive.isSelected());
            item.setAccPersediaan(akunPersediaan.get(cmbAkunPersediaan.getSelectedIndex()).getAcc_no());
            item.setAccPembelian(akunPembelian.get(cmbAkunPembelian.getSelectedIndex()).getAcc_no());
            item.setAccReturPembelian(akunReturPembelian.get(cmbAkunReturPembelian.getSelectedIndex()).getAcc_no());
            item.setAccPenjualan(akunPenjualan.get(cmbAkunPenjualan.getSelectedIndex()).getAcc_no());
            item.setAccReturPenjualan(akunReturPenjualan.get(cmbAkunReturPenjualan.getSelectedIndex()).getAcc_no());
            item.setAccHpp(akunHpp.get(cmbAkunHpp.getSelectedIndex()).getAcc_no());
            item.setHargaJual(fn.udfGetDouble(txtHarga.getText()));
            item.setTipe(cmbTipe.getSelectedIndex()==2? "S": cmbTipe.getSelectedItem().toString().substring(0, 1));
            item.setReorder(fn.udfGetInt(txtReorder.getText()));
            daoItem.simpanItem(item, null, lblID.getText().equalsIgnoreCase(""));
            
            daoItem.saveImage(item.getId(), sFotoFile, lblImage.getIcon());
            conn.setAutoCommit(true);
            if (srcForm instanceof FrmListItem) {
                ((FrmListItem) srcForm).tampilkanData(item.getId(), "");
            }
            JOptionPane.showMessageDialog(this, "Simpan Item sukses!");
            this.dispose();
        } catch (SQLException ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(MasterItem.class.getName()).log(Level.SEVERE, null, ex1);
            }
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
                if(e.getSource().equals(txtHarga)){
                    txtHarga.setText(GeneralFunction.intFmt.format(GeneralFunction.udfGetDouble(txtHarga.getText())));
                }
            }
        }
    };

    private void settingCoa() {
        try {
            if (itemKategori == null || itemKategori.size() == 0 || !this.isShowing()) {
                return;
            }
            Integer id = itemKategori.get(cmbKategori.getSelectedIndex()).getId();
            ItemKategori kat = daoItemKategori.cariKategoriByID(id);
            cmbAkunPersediaan.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_persediaan()).getAcc_name());
            cmbAkunPembelian.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_pembelian()).getAcc_name());
            cmbAkunReturPembelian.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_retur_pembelian()).getAcc_name());
            cmbAkunPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_penjualan()).getAcc_name());
            cmbAkunReturPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_retur_penjualan()).getAcc_name());
            cmbAkunHpp.setSelectedItem(daoAcc.cariAccCoaByID(kat.getAcc_hpp()).getAcc_name());
        } catch (Exception ex) {
            Logger.getLogger(MasterItem.class.getName()).log(Level.SEVERE, null, ex);
        }

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
                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5: {
                    udfSave();

                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    dispose();
                    break;
                }

                case KeyEvent.VK_DELETE: {

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblID = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        cmbKategori = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cmbSupplier = new javax.swing.JComboBox();
        txtKeterangan = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cmbAkunPersediaan = new javax.swing.JComboBox();
        cmbAkunPembelian = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        cmbAkunReturPembelian = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cmbAkunPenjualan = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cmbAkunReturPenjualan = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbAkunHpp = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtPLU = new javax.swing.JTextField();
        cmbSatuan = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        cmbTipe = new javax.swing.JComboBox();
        txtReorder = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Stok");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Kategori :");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 320, 20));

        jLabel5.setText("ID :");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        lblID.setEditable(false);
        lblID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblID, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 80, 20));

        chkActive.setSelected(true);
        chkActive.setText("Active");
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 80, -1));

        cmbKategori.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbKategoriItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 270, -1));

        jLabel6.setText("Nama Stok");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel7.setText("Satuan");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        jLabel15.setText("Supplier Utama :");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, 20));

        jPanel2.add(cmbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 310, -1));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKeteranganActionPerformed(evt);
            }
        });
        jPanel2.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 185, 320, 20));

        jLabel17.setText("Barcode");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 90, 20));

        txtBarcode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBarcodeActionPerformed(evt);
            }
        });
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 130, 20));

        jLabel18.setText("Keterangan :");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 90, 20));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Akun-akun", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("Persediaan");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

        jPanel1.add(cmbAkunPersediaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 330, -1));

        jPanel1.add(cmbAkunPembelian, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 55, 330, -1));

        jLabel13.setText("Pembelian");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 90, 20));

        jPanel1.add(cmbAkunReturPembelian, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 330, -1));

        jLabel14.setText("Retur Pembelian");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 90, 20));

        jPanel1.add(cmbAkunPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 105, 330, -1));

        jLabel16.setText("Penjualan");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 90, 20));

        jPanel1.add(cmbAkunReturPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 330, -1));

        jLabel19.setText("Retur Penjualan");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 90, 20));

        jPanel1.add(cmbAkunHpp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 155, 330, -1));

        jLabel20.setText("Harga Pokok");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 90, 20));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 215, 460, 190));

        jButton1.setText("Atur Coa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 85, -1, -1));

        jLabel8.setText("Kode :");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 50, 20));

        txtPLU.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPLU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPLUActionPerformed(evt);
            }
        });
        jPanel2.add(txtPLU, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 110, 20));

        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSatuanItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 110, 20));

        jLabel21.setText("Harga Jual :");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 135, 90, 20));

        txtHarga.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });
        txtHarga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaKeyTyped(evt);
            }
        });
        jPanel2.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 135, 130, 20));

        jLabel22.setText("Tipe :");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 135, 60, 20));

        cmbTipe.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inventory", "Non Inventory", "Jasa" }));
        jPanel2.add(cmbTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 135, 140, -1));

        txtReorder.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtReorder.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReorder.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtReorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReorderActionPerformed(evt);
            }
        });
        txtReorder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtReorderKeyTyped(evt);
            }
        });
        jPanel2.add(txtReorder, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 130, 20));

        jLabel23.setText("Reoder Level : ");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 90, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 410));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Stok");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 440, 30));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_lightning.png"))); // NOI18N
        btnNew.setText("Clear (F2)");
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        getContentPane().add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 455, 110, -1));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup (Esc)");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 455, -1, -1));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disk.png"))); // NOI18N
        btnSave.setText("Simpan (F5)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 455, 120, -1));

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("Image");
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });
        getContentPane().add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 40, 360, 360));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/shape_move_front.png"))); // NOI18N
        jButton2.setText("Choose Image");
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 410, 200, -1));

        jButton3.setText("Clear Image");
        jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jButton3ItemStateChanged(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(763, 410, 90, -1));

        setSize(new java.awt.Dimension(875, 524));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfClear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    }//GEN-LAST:event_formWindowOpened

    private void txtKeteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKeteranganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKeteranganActionPerformed

    private void txtBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBarcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBarcodeActionPerformed

    private void cmbKategoriItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKategoriItemStateChanged
        
    }//GEN-LAST:event_cmbKategoriItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        settingCoa();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtPLUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPLUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPLUActionPerformed

    private void cmbSatuanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSatuanItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSatuanItemStateChanged

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        PilihFoto();
    }//GEN-LAST:event_lblImageMouseClicked

    private void jButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jButton3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        PilihFoto();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        lblImage.setIcon(null);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void txtHargaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHargaKeyTyped

    private void txtReorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReorderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReorderActionPerformed

    private void txtReorderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReorderKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReorderKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MasterItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MasterItem dialog = new MasterItem(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JComboBox cmbAkunHpp;
    private javax.swing.JComboBox cmbAkunPembelian;
    private javax.swing.JComboBox cmbAkunPenjualan;
    private javax.swing.JComboBox cmbAkunPersediaan;
    private javax.swing.JComboBox cmbAkunReturPembelian;
    private javax.swing.JComboBox cmbAkunReturPenjualan;
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JComboBox cmbSupplier;
    private javax.swing.JComboBox cmbTipe;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField lblID;
    private javax.swing.JLabel lblImage;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtPLU;
    private javax.swing.JTextField txtReorder;
    // End of variables declaration//GEN-END:variables

    public void tampilkanItem(Integer id) {
        try {
            Item view = daoItem.cariItemByID(id);
            lblID.setText(view.getId().toString());
            txtNama.setText(view.getNama_barang());
            txtPLU.setText(view.getPlu());
            cmbSatuan.setSelectedItem(daoSatuan.cariSatuanByID(view.getIdSatuan()).getSatuan());
            txtBarcode.setText(view.getBarcode());
            cmbKategori.setSelectedItem(daoItemKategori.cariKategoriByID(view.getId_kategori()).getNamaKategori());
            chkActive.setSelected(view.getActive());
            txtHarga.setText(fn.intFmt.format(view.getHargaJual()));
            txtKeterangan.setText(view.getKeterangan());
            cmbTipe.setSelectedIndex(view.getTipe().equalsIgnoreCase("S")? 2: (view.getTipe().equalsIgnoreCase("N")? 1: 0));
            txtReorder.setText(fn.intFmt.format(view.getReorder()));
            if (view.getId_supp_default() != null) {
                cmbSupplier.setSelectedItem(daoSupplier.cariByID(view.getId_supp_default()).getNamaRelasi());
            }
            ItemCoa coa = daoItem.cariItemCoaByID(id);
            if (view.getAccPersediaan() != null) {
                cmbAkunPersediaan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccPersediaan()).getAcc_name());
            }
            if (view.getAccPembelian() != null) {
                cmbAkunPembelian.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccPembelian()).getAcc_name());
            }
            if (view.getAccReturPembelian() != null) {
                cmbAkunReturPembelian.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccReturPembelian()).getAcc_name());
            }
            if (view.getAccPenjualan() != null) {
                cmbAkunPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccPenjualan()).getAcc_name());
            }
            if (view.getAccReturPenjualan() != null) {
                cmbAkunReturPenjualan.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccReturPenjualan()).getAcc_name());
            }
            if (view.getAccHpp() != null) {
                cmbAkunHpp.setSelectedItem(daoAcc.cariAccCoaByID(view.getAccHpp()).getAcc_name());
            }
            byte[] imgBytes=daoItem.getGambar(view.getId());
            if(imgBytes!=null){
                ImageIcon myIcon = new ImageIcon(imgBytes);
                ImageIcon bigImage = new ImageIcon(myIcon.getImage().getScaledInstance(360, 360, 8));

                lblImage.setIcon(bigImage);
                
            }
        } catch (Exception ex) {
            Logger.getLogger(MasterItem.class.getName()).log(Level.SEVERE, null, ex);

        }

    }
}
