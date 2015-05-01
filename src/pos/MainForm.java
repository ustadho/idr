 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import com.ustasoft.component.FrmInfoPerusahaan;
import com.ustasoft.component.FrmSettingMenu;
import com.ustasoft.component.Images;
import com.ustasoft.component.MenuAuth;
import com.ustasoft.component.MenuTaskPane;
import com.ustasoft.petty_cash.FrmCashFlow;
import com.ustasoft.petty_cash.FrmPettyCashList;
import com.ustasoft.pos.service.FrmRptBankGL;
import com.ustasoft.pos.service.FrmRptHutangPiutang;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import org.flexdock.docking.DockingConstants;
import org.flexdock.view.View;
import org.jdesktop.swingx.JXTaskPane;
import pos.akuntansi.FrmBankRekonsiliasi;
import pos.akuntansi.FrmBuktiKasRM;
import pos.akuntansi.FrmBuktiKasRMHis;
import pos.akuntansi.FrmBukuBank;
import pos.akuntansi.FrmJournalEntryRM;
import pos.akuntansi.FrmJournalEntryRMHis;
import pos.ap.FrmApPayment;
import pos.ap.FrmApPaymentHis;
import pos.ap.FrmEntriHutang;
import pos.ap.FrmEntriHutangHis;
import pos.ap.FrmItemHistoryAP;
import pos.ap.FrmKartuHutangPerSupplier;
import pos.ap.FrmPO;
import pos.ap.FrmPOHis;
import pos.ap.FrmPembelian;
import pos.ap.FrmPembelianHis;
import pos.ap.FrmPembelianRetur;
import pos.ap.FrmRptPembelian;
import pos.ap.FrmSaldoAwalAP;
import pos.ar.FrmArReceiptHis;
import pos.ar.FrmArReceipt;
import pos.ar.FrmItemHistoryAR;
import pos.ar.FrmKartuPiutang;
import pos.ar.FrmPenjualan;
import pos.ar.FrmPenjualanHis;
import pos.ar.FrmPenjualanRetur;
import pos.ar.FrmRptKomisiSalesman;
import pos.ar.FrmRptPenjualan;
import pos.ar.FrmSO;
import pos.ar.FrmSOHis;
import pos.ar.FrmSaldoAwalAR;
import pos.inventory.FrmRptInventory;
import pos.inventory.FrmTransfer;
import pos.inventory.FrmTransferHis;
import pos.master.list.FrmListAlatBayar;
import pos.master.list.FrmListCoa;
import pos.master.list.FrmListCustomer;
import pos.master.list.FrmListExpedisi;
import pos.master.list.FrmListGudang;
import pos.master.list.FrmListItem;
import pos.master.list.FrmListItemKategori;
import pos.master.list.FrmListItemSupplier;
import pos.master.list.FrmListKota;
import pos.master.list.FrmListSales;
import pos.master.list.FrmListSatuan;
import pos.master.list.FrmListSupplier;
import pos.master.list.FrmListRelasiType;
import pos.master.list.FrmListStockType;
import pos.master.list.FrmLokasi;
import pos.inventory.FrmMutasiStok;
import pos.inventory.FrmMutasiStokHis;
import pos.inventory.FrmStokOpname;
import pos.inventory.FrmStokOpnameHis;

/**
 *
 * @author cak-ust
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="classpath*:com/artivisi/**/applicationContext.xml")
public class MainForm extends javax.swing.JFrame {

    public static String sUserName = "admin";
    public static String sToko = "Rejeki Makmur";
    public static String sAlamat1 = "Ruko Tidar Mas Square C-10";
    public static String sAlamat2 = "Surabaya - 60251 - Indonesia";
    public static String sTelp1 = "031-71111262, Fax. 031. 5340025";
    public static String sEmail = "rejeki.makmur@yahoo.com";
    public static String sUserId = "";
    public static String sKota;
    public static String sTelp2;
    public static String sPrinterPos = "U220";
    public static String sTipeUsaha = "TOKO";
    public static final String[] sDateFormat = new String[]{"dd/MM/yyyy"};
    public static Date tglSkg;

    //DBConnection db=new DBConnection("test", "test", this);
    Connection conn;
    DataSource dataSource;
    //JDesktopImage jDesktopImage1=new JDesktopImage();
    private MenuTaskPane panelMenu = null;
//    private it.businesslogic.ireport.gui.JMDIDesktopPane jMDIDesktopPane;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        jScrollPane1.setPreferredSize(new Dimension(180, jScrollPane1.getHeight()));
//        final org.flexdock.docking.defaults.DefaultDockingPort defaultDockingPort = new org.flexdock.docking.defaults.DefaultDockingPort();
//        jPanelMaster.add(defaultDockingPort, BorderLayout.CENTER);
//        jPanelMaster.updateUI();
//        defaultDockingPort.updateUI();
//        //desktop = createDesktopPage();
//        
//        DockingManager.dock(jDesktopImage1, (DockingPort)defaultDockingPort);
//        panelMenu=new MenuTaskPane();
//        View viewMenu = createView("Menu",it.businesslogic.ireport.util.I18n.getString("menu","Menu"), true, true, panelMenu);
//        
//        DockingManager.dock((Dockable)viewMenu, (Dockable)jDesktopImage1, 
//                DockingConstants.SOUTH_REGION, 0.3f);

        setIconImage(new ImageIcon(getClass().getResource("/resources/images/timeline_marker.png")).getImage());
        setExtendedState(MAXIMIZED_BOTH);
    }


    private View createView(String id, String text, boolean closable, boolean pin, Component c) {
        View view = new View(id, text);

        if (closable) {
            view.getTitlebar().addAction(DockingConstants.CLOSE_ACTION);
            if (view.getTitlebar().getActionButton(DockingConstants.CLOSE_ACTION) != null) {
                view.getTitlebar().getActionButton(DockingConstants.CLOSE_ACTION).addActionListener(
                        new ActionListener() {
                    public 

void actionPerformed(ActionEvent e) {
                        View viewx = (View) javax.swing.SwingUtilities.getAncestorOfClass(View.class  

, (java.awt.Component) e.getSource());
                        //closedView( viewx );
                        //System.out.println( "Closed: " + viewx.getPersistentId());
                    }
                });
            }
        }
        if (pin) {
            view.addAction(DockingConstants.PIN_ACTION);
        }



        JPanel p = new JPanel();
        //		p.setBackground(Color.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(new LineBorder(java.awt.Color.GRAY, 1));


        p.add(c, BorderLayout.CENTER);

        view.setContentPane(p);
        return view;
    }

    private View createDesktopPage() {
        String id = "Desktop";
        View view = new View(id, null, null);
        view.setTerritoryBlocked(DockingConstants.CENTER_REGION, true);
        view.setTitlebar(null);
        view.setContentPane(jDesktopImage2);
        view.setSize(1000, 100);
        return view;
    }

//    public void setDataSource(DataSource ds){
//        try {
//            this.dataSource=ds;
//            conn=ds.getConnection();
//        } catch (SQLException ex) {
//            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public boolean isExistsOnDesktop(JInternalFrame form) {
        JInternalFrame ji[] = jDesktopImage2.getAllFrames();

        for (int i = 0; i < ji.length; i++) {
            if (ji[i].getClass().getSimpleName().equalsIgnoreCase(form.getClass().getSimpleName())) {
                try {
                    ji[i].setSelected(true);
                    return true;
                } catch (PropertyVetoException po) {
                }
            }
        }

        return false;
    }

    public boolean isExistsOnDesktop(JInternalFrame form, String sTitle) {
        JInternalFrame ji[] = jDesktopImage2.getAllFrames();

        for (int i = 0; i < ji.length; i++) {
            if (ji[i].getClass().getSimpleName().equalsIgnoreCase(form.getClass().getSimpleName())
                    && ji[i].getTitle().equalsIgnoreCase(sTitle)) {
                try {
                    ji[i].setSelected(true);
                    return true;
                } catch (PropertyVetoException po) {
                }
            }
        }

        return false;
    }

    private void setMenu(MenuAuth mau, JMenuItem mnuItem, boolean can_read, boolean can_insert, boolean can_update, boolean can_delete,
            boolean can_print, boolean can_correction) {
        mau.setRead(can_read);
        mau.setInsert(can_insert);
        mau.setUpdate(can_update);
        mau.setDelete(can_delete);
        mau.setPrint(can_print);
        mau.setKoreksi(can_correction);
        if (mnuItem != null) {
            mnuItem.setVisible(can_read);
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

        jPanelMaster = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTaskPaneContainer2 = new com.ustasoft.component.JTaskPaneContainer();
        tpTransaksi = new org.jdesktop.swingx.JXTaskPane();
        tpList = new org.jdesktop.swingx.JXTaskPane();
        tpMaster = new org.jdesktop.swingx.JXTaskPane();
        tpLaporan = new org.jdesktop.swingx.JXTaskPane();
        jDesktopImage2 = new com.ustasoft.component.JDesktopImage();
        Setting = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        mnuFilePasswordChange = new javax.swing.JMenuItem();
        mnuFileLogoff = new javax.swing.JMenuItem();
        mnuFileExit = new javax.swing.JMenuItem();
        mnuSetting = new javax.swing.JMenu();
        mnuSettingUser = new javax.swing.JMenuItem();
        mnuSettingUserMenu = new javax.swing.JMenuItem();
        mnuSettingInforPerusahaan = new javax.swing.JMenuItem();
        mnuMaster = new javax.swing.JMenu();
        mnuMasterCoa = new javax.swing.JMenuItem();
        mnuMasterGudang = new javax.swing.JMenuItem();
        mnuMasterLokasi = new javax.swing.JMenuItem();
        mnuMasterKota = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuMasterSatuan = new javax.swing.JMenuItem();
        mnuMasterStokKategori = new javax.swing.JMenuItem();
        mnuMasterStok = new javax.swing.JMenuItem();
        mnuMasterStokTrxType = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuMasterRelasiKategori = new javax.swing.JMenuItem();
        mnuMasterSupplier = new javax.swing.JMenuItem();
        mnuMasterSupplierHarga = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuMasterCustomer = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuMasterSalesman = new javax.swing.JMenuItem();
        mnuMasterExpedisi = new javax.swing.JMenuItem();
        mnuMasterAlatBayar = new javax.swing.JMenuItem();
        mnuTrxStok = new javax.swing.JMenu();
        mnuTrxStokPenerimaan = new javax.swing.JMenuItem();
        mnuTrxStokPengeluaran = new javax.swing.JMenuItem();
        mnuTrxStokTransfer = new javax.swing.JMenuItem();
        mnuTrxStokOpname = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuListItemHistoriAr = new javax.swing.JMenuItem();
        mnuListItemHistoriAp = new javax.swing.JMenuItem();
        mnuDaftar = new javax.swing.JMenu();
        mnuDaftarPO = new javax.swing.JMenuItem();
        mnuDaftarPembelian = new javax.swing.JMenuItem();
        mnuDaftarPembayaranSupplier = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuDaftarSO = new javax.swing.JMenuItem();
        mnuDaftarPenjualan = new javax.swing.JMenuItem();
        mnuDaftarPembayaranPelanggan = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnuDaftarPenerimaanStok = new javax.swing.JMenuItem();
        mnuDaftarPengeluaranStok = new javax.swing.JMenuItem();
        mnuDaftarTransfer = new javax.swing.JMenuItem();
        mnuDaftarStokOpname = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnuListKasBank = new javax.swing.JMenu();
        mnuDaftarBKM = new javax.swing.JMenuItem();
        mnuDaftarBKK = new javax.swing.JMenuItem();
        mnuKeuBukuBank = new javax.swing.JMenuItem();
        mnuListBB = new javax.swing.JMenu();
        mnuListBBJurnal = new javax.swing.JMenuItem();
        mnuHutangPiutang = new javax.swing.JMenu();
        mnuEntriHutang = new javax.swing.JMenuItem();
        mnuEntriPiutang = new javax.swing.JMenuItem();
        mnuEntrihutangHistori = new javax.swing.JMenuItem();
        mnuKartuTagihanPiutang = new javax.swing.JMenuItem();
        mnuKartuTagihanHutang = new javax.swing.JMenuItem();
        mnuTrx = new javax.swing.JMenu();
        mnuTrxPenjualan = new javax.swing.JMenu();
        mnuTrxJualSO = new javax.swing.JMenuItem();
        mnuTrxJualFaktur = new javax.swing.JMenuItem();
        mnuTrxJualRetur = new javax.swing.JMenuItem();
        mnuTrxBayarPelanggan = new javax.swing.JMenuItem();
        mnuTrxJualSaldoAwalAr = new javax.swing.JMenuItem();
        mnuTrxPembelian = new javax.swing.JMenu();
        mnuTrxPembelianPO = new javax.swing.JMenuItem();
        mnuTrxPembelianBeli = new javax.swing.JMenuItem();
        mnuTrxPembelianRetur = new javax.swing.JMenuItem();
        mnuTrxPembelianBayarSupplier = new javax.swing.JMenuItem();
        mnuTrxPembelianSaldoAwalAp = new javax.swing.JMenuItem();
        mnuAkuntasi = new javax.swing.JMenu();
        mnuAkuntansiKasBank = new javax.swing.JMenu();
        mnuKeuKasBankMasuk = new javax.swing.JMenuItem();
        mnuKeuKasBankKeluar = new javax.swing.JMenuItem();
        mnuKeuKasBankRekonsiliasi = new javax.swing.JMenuItem();
        mnuKeuJurnal = new javax.swing.JMenuItem();
        mnuUtil = new javax.swing.JMenu();
        mnuUtilLookupItem = new javax.swing.JMenuItem();
        mnuUtilReminder = new javax.swing.JMenuItem();
        mnuRpt = new javax.swing.JMenu();
        mnuRptPenjualan = new javax.swing.JMenuItem();
        mnuRptPembelian = new javax.swing.JMenuItem();
        mnuRptHutangPiutang = new javax.swing.JMenuItem();
        mnuRptPersediaan = new javax.swing.JMenuItem();
        mnuRptKomisiSalesman = new javax.swing.JMenuItem();
        mnuRptKasBankGL = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Rejeki Makmur");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanelMaster.setLayout(new javax.swing.BoxLayout(jPanelMaster, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(jPanelMaster);

        tpTransaksi.setTitle("Transaksi");
        jTaskPaneContainer2.add(tpTransaksi);

        tpList.setTitle("Daftar");
        jTaskPaneContainer2.add(tpList);

        tpMaster.setExpanded(false);
        tpMaster.setTitle("Master");
        jTaskPaneContainer2.add(tpMaster);

        tpLaporan.setTitle("Laporan");
        jTaskPaneContainer2.add(tpLaporan);

        jScrollPane1.setViewportView(jTaskPaneContainer2);

        jSplitPane2.setLeftComponent(jScrollPane1);
        jSplitPane2.setRightComponent(jDesktopImage2);

        getContentPane().add(jSplitPane2);

        jMenu2.setText("File");

        mnuFilePasswordChange.setText("Ubah Password");
        mnuFilePasswordChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFilePasswordChangeActionPerformed(evt);
            }
        });
        jMenu2.add(mnuFilePasswordChange);

        mnuFileLogoff.setText("LogOff");
        mnuFileLogoff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileLogoffActionPerformed(evt);
            }
        });
        jMenu2.add(mnuFileLogoff);

        mnuFileExit.setText("Exit");
        mnuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileExitActionPerformed(evt);
            }
        });
        jMenu2.add(mnuFileExit);

        Setting.add(jMenu2);

        mnuSetting.setText("Setting");

        mnuSettingUser.setText("Pengguna");
        mnuSettingUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSettingUserActionPerformed(evt);
            }
        });
        mnuSetting.add(mnuSettingUser);

        mnuSettingUserMenu.setText("Otorisasi Menu");
        mnuSettingUserMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSettingUserMenuActionPerformed(evt);
            }
        });
        mnuSetting.add(mnuSettingUserMenu);

        mnuSettingInforPerusahaan.setText("Info Perusahaan");
        mnuSettingInforPerusahaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSettingInforPerusahaanActionPerformed(evt);
            }
        });
        mnuSetting.add(mnuSettingInforPerusahaan);

        Setting.add(mnuSetting);

        mnuMaster.setText("Master");

        mnuMasterCoa.setText("COA");
        mnuMasterCoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterCoaActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterCoa);

        mnuMasterGudang.setText("Gudang");
        mnuMasterGudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterGudangActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterGudang);

        mnuMasterLokasi.setText("Lokasi");
        mnuMasterLokasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterLokasiActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterLokasi);

        mnuMasterKota.setText("Kota");
        mnuMasterKota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterKotaActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterKota);
        mnuMaster.add(jSeparator3);

        mnuMasterSatuan.setText("Satuan");
        mnuMasterSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterSatuanActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterSatuan);

        mnuMasterStokKategori.setText("Kategori Stok");
        mnuMasterStokKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterStokKategoriActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterStokKategori);

        mnuMasterStok.setText("Stok");
        mnuMasterStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterStokActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterStok);

        mnuMasterStokTrxType.setText("Tipe Transaksi Stok");
        mnuMasterStokTrxType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterStokTrxTypeActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterStokTrxType);
        mnuMaster.add(jSeparator1);

        mnuMasterRelasiKategori.setText("Kategori Supplier/ Customer");
        mnuMasterRelasiKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterRelasiKategoriActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterRelasiKategori);

        mnuMasterSupplier.setText("Supplier");
        mnuMasterSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterSupplierActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterSupplier);

        mnuMasterSupplierHarga.setText("Harga Supplier");
        mnuMasterSupplierHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterSupplierHargaActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterSupplierHarga);
        mnuMaster.add(jSeparator2);

        mnuMasterCustomer.setText("Customer");
        mnuMasterCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterCustomerActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterCustomer);
        mnuMaster.add(jSeparator4);

        mnuMasterSalesman.setText("Salesman");
        mnuMasterSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterSalesmanActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterSalesman);

        mnuMasterExpedisi.setText("Expedisi");
        mnuMasterExpedisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterExpedisiActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterExpedisi);

        mnuMasterAlatBayar.setText("Alat Bayar");
        mnuMasterAlatBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterAlatBayarActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterAlatBayar);

        Setting.add(mnuMaster);

        mnuTrxStok.setText("Stok");

        mnuTrxStokPenerimaan.setText("Penerimaan Stok");
        mnuTrxStokPenerimaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxStokPenerimaanActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuTrxStokPenerimaan);

        mnuTrxStokPengeluaran.setText("Pengeluaran Stok");
        mnuTrxStokPengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxStokPengeluaranActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuTrxStokPengeluaran);

        mnuTrxStokTransfer.setText("Pindah Gudang");
        mnuTrxStokTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxStokTransferActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuTrxStokTransfer);

        mnuTrxStokOpname.setText("Stok Opname");
        mnuTrxStokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxStokOpnameActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuTrxStokOpname);
        mnuTrxStok.add(jSeparator8);

        mnuListItemHistoriAr.setText("Histori Harga Penjualan per Stok");
        mnuListItemHistoriAr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListItemHistoriArActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuListItemHistoriAr);

        mnuListItemHistoriAp.setText("Histori Harga Pembelian per Stok");
        mnuListItemHistoriAp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListItemHistoriApActionPerformed(evt);
            }
        });
        mnuTrxStok.add(mnuListItemHistoriAp);

        Setting.add(mnuTrxStok);

        mnuDaftar.setText("Daftar");

        mnuDaftarPO.setText("Pesanan Pembelian - PO");
        mnuDaftarPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPOActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPO);

        mnuDaftarPembelian.setText("Pembelian");
        mnuDaftarPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPembelianActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPembelian);

        mnuDaftarPembayaranSupplier.setText("Pembayaran Supplier");
        mnuDaftarPembayaranSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPembayaranSupplierActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPembayaranSupplier);
        mnuDaftar.add(jSeparator5);

        mnuDaftarSO.setText("Pesanan Penjualan - SO");
        mnuDaftarSO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarSOActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarSO);

        mnuDaftarPenjualan.setText("Faktur Penjualan");
        mnuDaftarPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPenjualanActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPenjualan);

        mnuDaftarPembayaranPelanggan.setText("Pembayaran Pelanggan");
        mnuDaftarPembayaranPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPembayaranPelangganActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPembayaranPelanggan);
        mnuDaftar.add(jSeparator6);

        mnuDaftarPenerimaanStok.setText("Penerimaan Stok");
        mnuDaftarPenerimaanStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPenerimaanStokActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPenerimaanStok);

        mnuDaftarPengeluaranStok.setText("Pengeluaran Stok");
        mnuDaftarPengeluaranStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarPengeluaranStokActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarPengeluaranStok);

        mnuDaftarTransfer.setText("Transfer Antar Gudang");
        mnuDaftarTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarTransferActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarTransfer);

        mnuDaftarStokOpname.setText("Stok Opname");
        mnuDaftarStokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarStokOpnameActionPerformed(evt);
            }
        });
        mnuDaftar.add(mnuDaftarStokOpname);
        mnuDaftar.add(jSeparator7);

        mnuListKasBank.setText("Kas & Bank");

        mnuDaftarBKM.setText("Bukti Kas Masuk");
        mnuDaftarBKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarBKMActionPerformed(evt);
            }
        });
        mnuListKasBank.add(mnuDaftarBKM);

        mnuDaftarBKK.setText("Bukti Kas Keluar");
        mnuDaftarBKK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDaftarBKKActionPerformed(evt);
            }
        });
        mnuListKasBank.add(mnuDaftarBKK);

        mnuKeuBukuBank.setText("Buku Bank");
        mnuKeuBukuBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuBukuBankActionPerformed(evt);
            }
        });
        mnuListKasBank.add(mnuKeuBukuBank);

        mnuDaftar.add(mnuListKasBank);

        mnuListBB.setText("Buku Besar");

        mnuListBBJurnal.setText("Jurnal Umum");
        mnuListBBJurnal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListBBJurnalActionPerformed(evt);
            }
        });
        mnuListBB.add(mnuListBBJurnal);

        mnuDaftar.add(mnuListBB);

        Setting.add(mnuDaftar);

        mnuHutangPiutang.setText("Hutang Piutang");

        mnuEntriHutang.setText("Entri Hutang");
        mnuEntriHutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEntriHutangActionPerformed(evt);
            }
        });
        mnuHutangPiutang.add(mnuEntriHutang);

        mnuEntriPiutang.setText("Entri Piutang");
        mnuEntriPiutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEntriPiutangActionPerformed(evt);
            }
        });
        mnuHutangPiutang.add(mnuEntriPiutang);

        mnuEntrihutangHistori.setText("Histori Entri Hutang");
        mnuEntrihutangHistori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEntrihutangHistoriActionPerformed(evt);
            }
        });
        mnuHutangPiutang.add(mnuEntrihutangHistori);

        mnuKartuTagihanPiutang.setText("Kartu Tagihan Piutang Pelanggan");
        mnuKartuTagihanPiutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKartuTagihanPiutangActionPerformed(evt);
            }
        });
        mnuHutangPiutang.add(mnuKartuTagihanPiutang);

        mnuKartuTagihanHutang.setText("Kartu Tagihan Hutang Supplier");
        mnuKartuTagihanHutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKartuTagihanHutangActionPerformed(evt);
            }
        });
        mnuHutangPiutang.add(mnuKartuTagihanHutang);

        Setting.add(mnuHutangPiutang);

        mnuTrx.setText("Transaksi");

        mnuTrxPenjualan.setText("Penjualan");

        mnuTrxJualSO.setText("Pesanan Penjualan (SO)");
        mnuTrxJualSO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxJualSOActionPerformed(evt);
            }
        });
        mnuTrxPenjualan.add(mnuTrxJualSO);

        mnuTrxJualFaktur.setText("Faktur Penjualan");
        mnuTrxJualFaktur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxJualFakturActionPerformed(evt);
            }
        });
        mnuTrxPenjualan.add(mnuTrxJualFaktur);

        mnuTrxJualRetur.setText("Retur Penjualan");
        mnuTrxJualRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxJualReturActionPerformed(evt);
            }
        });
        mnuTrxPenjualan.add(mnuTrxJualRetur);

        mnuTrxBayarPelanggan.setText("Penerimaan Pelanggan");
        mnuTrxBayarPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxBayarPelangganActionPerformed(evt);
            }
        });
        mnuTrxPenjualan.add(mnuTrxBayarPelanggan);

        mnuTrxJualSaldoAwalAr.setText("Saldo Awal Piutang Penjualan");
        mnuTrxJualSaldoAwalAr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxJualSaldoAwalArActionPerformed(evt);
            }
        });
        mnuTrxPenjualan.add(mnuTrxJualSaldoAwalAr);

        mnuTrx.add(mnuTrxPenjualan);

        mnuTrxPembelian.setText("Pembelian");

        mnuTrxPembelianPO.setText("Pesanan Pembelian (PO)");
        mnuTrxPembelianPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianPOActionPerformed(evt);
            }
        });
        mnuTrxPembelian.add(mnuTrxPembelianPO);

        mnuTrxPembelianBeli.setText("Pembelian");
        mnuTrxPembelianBeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianBeliActionPerformed(evt);
            }
        });
        mnuTrxPembelian.add(mnuTrxPembelianBeli);

        mnuTrxPembelianRetur.setText("Retur Pembelian");
        mnuTrxPembelianRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianReturActionPerformed(evt);
            }
        });
        mnuTrxPembelian.add(mnuTrxPembelianRetur);

        mnuTrxPembelianBayarSupplier.setText("Pembayaran Supplier");
        mnuTrxPembelianBayarSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianBayarSupplierActionPerformed(evt);
            }
        });
        mnuTrxPembelian.add(mnuTrxPembelianBayarSupplier);

        mnuTrxPembelianSaldoAwalAp.setText("Saldo Awal Hutang Pembelian");
        mnuTrxPembelianSaldoAwalAp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianSaldoAwalApActionPerformed(evt);
            }
        });
        mnuTrxPembelian.add(mnuTrxPembelianSaldoAwalAp);

        mnuTrx.add(mnuTrxPembelian);

        Setting.add(mnuTrx);

        mnuAkuntasi.setText("Akuntansi");

        mnuAkuntansiKasBank.setText("Kas & Bank");

        mnuKeuKasBankMasuk.setText("Bukti Kas Masuk");
        mnuKeuKasBankMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuKasBankMasukActionPerformed(evt);
            }
        });
        mnuAkuntansiKasBank.add(mnuKeuKasBankMasuk);

        mnuKeuKasBankKeluar.setText("Bukti Kas Keluar");
        mnuKeuKasBankKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuKasBankKeluarActionPerformed(evt);
            }
        });
        mnuAkuntansiKasBank.add(mnuKeuKasBankKeluar);

        mnuKeuKasBankRekonsiliasi.setText("Rekonsiliasi Bank");
        mnuKeuKasBankRekonsiliasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuKasBankRekonsiliasiActionPerformed(evt);
            }
        });
        mnuAkuntansiKasBank.add(mnuKeuKasBankRekonsiliasi);

        mnuAkuntasi.add(mnuAkuntansiKasBank);

        mnuKeuJurnal.setText("Jurnal Umum");
        mnuKeuJurnal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuJurnalActionPerformed(evt);
            }
        });
        mnuAkuntasi.add(mnuKeuJurnal);

        Setting.add(mnuAkuntasi);

        mnuUtil.setText("Utilisasi");

        mnuUtilLookupItem.setText("Lookup Item");
        mnuUtilLookupItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUtilLookupItemActionPerformed(evt);
            }
        });
        mnuUtil.add(mnuUtilLookupItem);

        mnuUtilReminder.setText("Pengingat");
        mnuUtilReminder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUtilReminderActionPerformed(evt);
            }
        });
        mnuUtil.add(mnuUtilReminder);

        Setting.add(mnuUtil);

        mnuRpt.setText("Laporan");

        mnuRptPenjualan.setText("Penjualan");
        mnuRptPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPenjualanActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPenjualan);

        mnuRptPembelian.setText("Pembelian");
        mnuRptPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPembelianActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPembelian);

        mnuRptHutangPiutang.setText("Hutang - Piutang");
        mnuRptHutangPiutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptHutangPiutangActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptHutangPiutang);

        mnuRptPersediaan.setText("Laporan Stok");
        mnuRptPersediaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPersediaanActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPersediaan);

        mnuRptKomisiSalesman.setText("Komisi Salesman");
        mnuRptKomisiSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptKomisiSalesmanActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptKomisiSalesman);

        mnuRptKasBankGL.setText("Kas/ Bank & Buku Besar");
        mnuRptKasBankGL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptKasBankGLActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptKasBankGL);

        Setting.add(mnuRpt);

        mnuHelp.setText("Help");

        jMenuItem1.setText("Panduan Pengguna");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuHelp.add(jMenuItem1);

        jMenuItem2.setText("Tentang Program");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        mnuHelp.add(jMenuItem2);

        Setting.add(mnuHelp);

        setJMenuBar(Setting);

        setSize(new java.awt.Dimension(930, 835));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuMasterStokKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterStokKategoriActionPerformed
        if (isExistsOnDesktop(new FrmListItemKategori(conn))) {
            return;
        }

        FrmListItemKategori k = new FrmListItemKategori(conn);
        jDesktopImage2.add(k);
        k.setBounds(0, 0, k.getWidth(), k.getHeight());
        k.setVisible(true);
    }//GEN-LAST:event_mnuMasterStokKategoriActionPerformed

    private void mnuMasterCoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterCoaActionPerformed
        if (isExistsOnDesktop(new FrmListCoa(conn))) {
            return;
        }
        FrmListCoa coa = new FrmListCoa(conn);
        jDesktopImage2.add(coa);
        coa.setBounds(0, 0, coa.getWidth(), coa.getHeight());
        coa.setVisible(true);
    }//GEN-LAST:event_mnuMasterCoaActionPerformed

    private void mnuMasterKotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterKotaActionPerformed
        if (isExistsOnDesktop(new FrmListKota(conn))) {
            return;
        }
        FrmListKota kota = new FrmListKota(conn);
        jDesktopImage2.add(kota);
        kota.setBounds(0, 0, kota.getWidth(), kota.getHeight());
        kota.setVisible(true);
    }//GEN-LAST:event_mnuMasterKotaActionPerformed

    private void mnuMasterSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterSupplierActionPerformed
        if (isExistsOnDesktop(new FrmListSupplier())) {
            return;
        }
        FrmListSupplier supp = new FrmListSupplier();
        jDesktopImage2.add(supp);
        supp.initForm(conn);
        supp.setBounds(0, 0, supp.getWidth(), supp.getHeight());
        supp.setVisible(true);
    }//GEN-LAST:event_mnuMasterSupplierActionPerformed

    private void mnuMasterRelasiKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterRelasiKategoriActionPerformed
        if (isExistsOnDesktop(new FrmListRelasiType(conn))) {
            return;
        }
        FrmListRelasiType supp = new FrmListRelasiType(conn);
        jDesktopImage2.add(supp);
        supp.setBounds(0, 0, supp.getWidth(), supp.getHeight());
        supp.setVisible(true);
    }//GEN-LAST:event_mnuMasterRelasiKategoriActionPerformed

    private void mnuMasterStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterStokActionPerformed
        if (isExistsOnDesktop(new FrmListItem())) {
            return;
        }
        FrmListItem supp = new FrmListItem();
        supp.setConn(conn);
        jDesktopImage2.add(supp);
        supp.setBounds(0, 0, supp.getWidth(), supp.getHeight());
        supp.setVisible(true);
    }//GEN-LAST:event_mnuMasterStokActionPerformed

    private void mnuMasterSupplierHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterSupplierHargaActionPerformed
        if (isExistsOnDesktop(new FrmListItemSupplier())) {
            return;
        }
        FrmListItemSupplier supp = new FrmListItemSupplier();
        supp.setConn(conn);
        jDesktopImage2.add(supp);
        supp.setBounds(0, 0, supp.getWidth(), supp.getHeight());
        supp.setVisible(true);
    }//GEN-LAST:event_mnuMasterSupplierHargaActionPerformed

    private void mnuMasterCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterCustomerActionPerformed
        if (isExistsOnDesktop(new FrmListCustomer())) {
            return;
        }
        FrmListCustomer supp = new FrmListCustomer();
        jDesktopImage2.add(supp);
        supp.initForm(conn);
        supp.setBounds(0, 0, supp.getWidth(), supp.getHeight());
        supp.setVisible(true);

    }//GEN-LAST:event_mnuMasterCustomerActionPerformed

    private void mnuMasterGudangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterGudangActionPerformed
        if (isExistsOnDesktop(new FrmListGudang(conn))) {
            return;
        }
        FrmListGudang gudang = new FrmListGudang(conn);
        jDesktopImage2.add(gudang);
        gudang.setBounds(0, 0, gudang.getWidth(), gudang.getHeight());
        gudang.setVisible(true);
    }//GEN-LAST:event_mnuMasterGudangActionPerformed

    private void mnuMasterSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterSalesmanActionPerformed
        if (isExistsOnDesktop(new FrmListSales())) {
            return;
        }
        FrmListSales sales = new FrmListSales();
        jDesktopImage2.add(sales);
        sales.setBounds(0, 0, sales.getWidth(), sales.getHeight());
        sales.initForm(conn);
        sales.setVisible(true);
    }//GEN-LAST:event_mnuMasterSalesmanActionPerformed

    private void mnuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuFileExitActionPerformed

    private void mnuMasterSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterSatuanActionPerformed
        if (isExistsOnDesktop(new FrmListSatuan())) {
            return;
        }
        FrmListSatuan sales = new FrmListSatuan();
        jDesktopImage2.add(sales);
        sales.setBounds(0, 0, sales.getWidth(), sales.getHeight());
        sales.initForm(conn);
        sales.setVisible(true);
    }//GEN-LAST:event_mnuMasterSatuanActionPerformed

    private void mnuMasterExpedisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterExpedisiActionPerformed
        if (isExistsOnDesktop(new FrmListExpedisi())) {
            return;
        }
        FrmListExpedisi sales = new FrmListExpedisi();
        jDesktopImage2.add(sales);
        sales.setBounds(0, 0, sales.getWidth(), sales.getHeight());
        sales.initForm(conn);
        sales.setVisible(true);
    }//GEN-LAST:event_mnuMasterExpedisiActionPerformed

    private void mnuTrxPembelianBeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianBeliActionPerformed
        if (isExistsOnDesktop(new FrmPembelian())) {
            return;
        }
        FrmPembelian f1 = new FrmPembelian();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuTrxPembelianBeliActionPerformed

    private void mnuTrxStokPenerimaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxStokPenerimaanActionPerformed
        udfLoadTrxMutasi("IN");
    }//GEN-LAST:event_mnuTrxStokPenerimaanActionPerformed

    private void mnuDaftarPengeluaranStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPengeluaranStokActionPerformed
        udfLoadMutasiHistory("OUT");
    }//GEN-LAST:event_mnuDaftarPengeluaranStokActionPerformed

    private void mnuDaftarPembelianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPembelianActionPerformed
        if (isExistsOnDesktop(new FrmPembelianHis())) {
            return;
        }
        FrmPembelianHis f1 = new FrmPembelianHis();
        jDesktopImage2.add(f1);
        f1.setDesktop(jDesktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuDaftarPembelianActionPerformed

    private void mnuTrxPembelianPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianPOActionPerformed
        if (isExistsOnDesktop(new FrmPO())) {
            return;
        }
        FrmPO f1 = new FrmPO();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setIsNew(true);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuTrxPembelianPOActionPerformed

    private void mnuDaftarPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPOActionPerformed
        if (isExistsOnDesktop(new FrmPOHis())) {
            return;
        }
        FrmPOHis f1 = new FrmPOHis();
        jDesktopImage2.add(f1);
        f1.setDesktop(jDesktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuDaftarPOActionPerformed

    private void mnuTrxJualSOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxJualSOActionPerformed
        if (isExistsOnDesktop(new FrmSO())) {
            return;
        }
        FrmSO f1 = new FrmSO();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setIsNew(true);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuTrxJualSOActionPerformed

    private void mnuTrxJualFakturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxJualFakturActionPerformed
        if (isExistsOnDesktop(new FrmPenjualan())) {
            return;
        }
        FrmPenjualan f1 = new FrmPenjualan();
        jDesktopImage2.add(f1);
        f1.setTitle("Faktur Penjualan");
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setSrcForm(this);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuTrxJualFakturActionPerformed
    public static String sU = "test";

    private void mnuUtilLookupItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUtilLookupItemActionPerformed
        if (sTipeUsaha.equalsIgnoreCase("BENGKEL")) {
            DlgLookupItemBengkel d = new DlgLookupItemBengkel(this, true);
            d.setConn(conn);
            d.setVisible(true);
        } else {
            DlgLookupItem d = new DlgLookupItem(null, false);
            d.setConn(conn);
            d.setVisible(true);
        }
    }//GEN-LAST:event_mnuUtilLookupItemActionPerformed

    private void mnuDaftarSOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarSOActionPerformed
        if (isExistsOnDesktop(new FrmSOHis())) {
            return;
        }
        FrmSOHis f1 = new FrmSOHis();
        jDesktopImage2.add(f1);
        f1.setDesktop(jDesktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuDaftarSOActionPerformed
    public static String sP = "test";
    private void mnuSettingUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSettingUserActionPerformed
        if (isExistsOnDesktop(new FrmUserManagement())) {
            return;
        }
        FrmUserManagement f1 = new FrmUserManagement();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuSettingUserActionPerformed

    private void mnuDaftarPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPenjualanActionPerformed
        if (isExistsOnDesktop(new FrmPenjualanHis())) {
            return;
        }
        FrmPenjualanHis f1 = new FrmPenjualanHis();
        jDesktopImage2.add(f1);
        f1.setDesktop(jDesktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuDaftarPenjualanActionPerformed

    private void mnuRptPersediaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPersediaanActionPerformed
        if (isExistsOnDesktop(new FrmRptInventory())) {
            return;
        }
        FrmRptInventory f1 = new FrmRptInventory();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuRptPersediaanActionPerformed

    private void mnuTrxStokTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxStokTransferActionPerformed
        if (isExistsOnDesktop(new FrmTransfer())) {
            return;
        }
        FrmTransfer f1 = new FrmTransfer();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuTrxStokTransferActionPerformed

    private void mnuRptPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPenjualanActionPerformed
        udfLoadRptPenjualan();
    }//GEN-LAST:event_mnuRptPenjualanActionPerformed

    private void mnuTrxJualReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxJualReturActionPerformed
        udfLoadReturJual();
    }//GEN-LAST:event_mnuTrxJualReturActionPerformed

    private void mnuTrxBayarPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxBayarPelangganActionPerformed
        udfLoadBayarPelanggan();
    }//GEN-LAST:event_mnuTrxBayarPelangganActionPerformed

    private void mnuRptPembelianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPembelianActionPerformed
        udfLoadRptPembelian();
    }//GEN-LAST:event_mnuRptPembelianActionPerformed

    private void mnuDaftarTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarTransferActionPerformed
        udfLoadTransferHistory();
    }//GEN-LAST:event_mnuDaftarTransferActionPerformed

    private void mnuTrxPembelianReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianReturActionPerformed
        udfLoadReturPembelian();
    }//GEN-LAST:event_mnuTrxPembelianReturActionPerformed

    private void mnuDaftarPenerimaanStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPenerimaanStokActionPerformed
        udfLoadMutasiHistory("IN");
    }//GEN-LAST:event_mnuDaftarPenerimaanStokActionPerformed

    private void mnuTrxStokPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxStokPengeluaranActionPerformed
        udfLoadTrxMutasi("OUT");
    }//GEN-LAST:event_mnuTrxStokPengeluaranActionPerformed

    private void mnuMasterAlatBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterAlatBayarActionPerformed
        udfLoadAlatBayar();
    }//GEN-LAST:event_mnuMasterAlatBayarActionPerformed

    private void mnuKeuKasBankKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuKasBankKeluarActionPerformed
        udfLoadPettyCash("K");
    }//GEN-LAST:event_mnuKeuKasBankKeluarActionPerformed

    private void mnuKeuKasBankMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuKasBankMasukActionPerformed
        udfLoadPettyCash("M");
    }//GEN-LAST:event_mnuKeuKasBankMasukActionPerformed

    private void mnuEntriPiutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEntriPiutangActionPerformed
        udfLoadEntriPiutang();
    }//GEN-LAST:event_mnuEntriPiutangActionPerformed

    private void mnuEntriHutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEntriHutangActionPerformed
        udfLoadEntriHutang();
        
    }//GEN-LAST:event_mnuEntriHutangActionPerformed

    private void mnuEntrihutangHistoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEntrihutangHistoriActionPerformed
        udfLoadHistoriEntriHutang();
    }//GEN-LAST:event_mnuEntrihutangHistoriActionPerformed

    private void mnuSettingUserMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSettingUserMenuActionPerformed
        udfLoadMenuAuth();
    }//GEN-LAST:event_mnuSettingUserMenuActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void mnuFileLogoffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileLogoffActionPerformed
        logOff();
        LoginUser login = new LoginUser();
        login.setConn(conn);
        login.setSrcForm(this);
        login.setAlwaysOnTop(true);
        login.setVisible(true);
    }//GEN-LAST:event_mnuFileLogoffActionPerformed

    private void mnuMasterLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterLokasiActionPerformed
        udfLoadLokasi();
    }//GEN-LAST:event_mnuMasterLokasiActionPerformed

    private void mnuSettingInforPerusahaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSettingInforPerusahaanActionPerformed
        udfLoadSettingPerusahaan();
    }//GEN-LAST:event_mnuSettingInforPerusahaanActionPerformed

    private void mnuMasterStokTrxTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterStokTrxTypeActionPerformed
        udfLoadMasterStockType();
    }//GEN-LAST:event_mnuMasterStokTrxTypeActionPerformed

    private void mnuFilePasswordChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFilePasswordChangeActionPerformed
        FrmPasswordChange f1=new FrmPasswordChange(this, true);
        f1.setConn(conn);
        f1.setTitle("Ubah Password");
        f1.setMainMenu(this);
        f1.setUsername(MainForm.sUserName);
        f1.setVisible(true);
    }//GEN-LAST:event_mnuFilePasswordChangeActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(null, "Anda Yakin untuk keluar?", "Rejeki Makmur", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                conn.close();
            

} catch (SQLException ex) {
                Logger.getLogger(MainForm.class  

.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        } else {
            this.setVisible(true);
            //main(null);
        }
    }//GEN-LAST:event_formWindowClosing

    private void mnuDaftarPembayaranPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPembayaranPelangganActionPerformed
        udfLoadDaftarPembayaranPelanggan();
    }//GEN-LAST:event_mnuDaftarPembayaranPelangganActionPerformed

    private void mnuTrxPembelianBayarSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianBayarSupplierActionPerformed
        udfLoadBayarSupplier();
    }//GEN-LAST:event_mnuTrxPembelianBayarSupplierActionPerformed

    private void mnuDaftarPembayaranSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarPembayaranSupplierActionPerformed
        udfLoadDaftarBayarSupplier();
    }//GEN-LAST:event_mnuDaftarPembayaranSupplierActionPerformed

    private void mnuUtilReminderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUtilReminderActionPerformed
        udfLoadReminder();
    }//GEN-LAST:event_mnuUtilReminderActionPerformed

    private void mnuListItemHistoriArActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListItemHistoriArActionPerformed
        udfLoadListItemHistoryAr();
    }//GEN-LAST:event_mnuListItemHistoriArActionPerformed

    private void mnuListItemHistoriApActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListItemHistoriApActionPerformed
        udfLoadListItemHistoryAp();
    }//GEN-LAST:event_mnuListItemHistoriApActionPerformed

    private void mnuKeuJurnalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuJurnalActionPerformed
        udfLoadJurnalUmum();
    }//GEN-LAST:event_mnuKeuJurnalActionPerformed

    private void mnuListBBJurnalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListBBJurnalActionPerformed
        udfLoadJurnalUmumHis();
    }//GEN-LAST:event_mnuListBBJurnalActionPerformed

    private void mnuDaftarBKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarBKMActionPerformed
        udfLoadBKHis("M");
    }//GEN-LAST:event_mnuDaftarBKMActionPerformed

    private void mnuDaftarBKKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarBKKActionPerformed
        udfLoadBKHis("K");
    }//GEN-LAST:event_mnuDaftarBKKActionPerformed

    private void mnuKeuBukuBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuBukuBankActionPerformed
        udfLoadBukuBank();
    }//GEN-LAST:event_mnuKeuBukuBankActionPerformed

    private void mnuKeuKasBankRekonsiliasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuKasBankRekonsiliasiActionPerformed
        udfLoadRekonsiliasi();
    }//GEN-LAST:event_mnuKeuKasBankRekonsiliasiActionPerformed

    private void mnuRptKomisiSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptKomisiSalesmanActionPerformed
        udfLoadRptKomisiSalesman();
    }//GEN-LAST:event_mnuRptKomisiSalesmanActionPerformed

    private void mnuKartuTagihanPiutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKartuTagihanPiutangActionPerformed
        udfLoadKartuPiutang();
    }//GEN-LAST:event_mnuKartuTagihanPiutangActionPerformed

    private void mnuKartuTagihanHutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKartuTagihanHutangActionPerformed
        udfLoadKartuHutang();
    }//GEN-LAST:event_mnuKartuTagihanHutangActionPerformed

    private void mnuRptHutangPiutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptHutangPiutangActionPerformed
        udfLoadRptHutangPiutang();
    }//GEN-LAST:event_mnuRptHutangPiutangActionPerformed

    private void mnuRptKasBankGLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptKasBankGLActionPerformed
        udfLoadRptBBGL();
    }//GEN-LAST:event_mnuRptKasBankGLActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        new DLgAbout(this, true).setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
//        File tmp = File.createTempFile("Print", ".pdf");
        String sDir=System.getProperties().getProperty("user.dir");
        System.out.println(sDir);
//        String outFileName = getClass().getResource(sDir+"\"Doc\"Manual.pdf").toString();
        String outFileName = sDir+"\\Doc\\Manual.pdf";
        System.out.println(outFileName);
        Runtime rt = Runtime.getRuntime();
            try {

                String sPdf = "\"Manual.pdf\"";

                rt.exec(new String[]{"cmd", "/c", outFileName.replace("file:/", "")});
            } catch (Exception e) {
                e.printStackTrace();
            }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mnuTrxJualSaldoAwalArActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxJualSaldoAwalArActionPerformed
        udfLoadSaldoAwalAr();
    }//GEN-LAST:event_mnuTrxJualSaldoAwalArActionPerformed

    private void mnuTrxPembelianSaldoAwalApActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianSaldoAwalApActionPerformed
        udfLoadSaldoAwalAp();
    }//GEN-LAST:event_mnuTrxPembelianSaldoAwalApActionPerformed

    private void mnuTrxStokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxStokOpnameActionPerformed
        udfLoadStokOpname();
    }//GEN-LAST:event_mnuTrxStokOpnameActionPerformed

    private void mnuDaftarStokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDaftarStokOpnameActionPerformed
        udfLoadDaftarStokOpname();
    }//GEN-LAST:event_mnuDaftarStokOpnameActionPerformed

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
            java.util.logging.Logger.getLogger(MainForm.class  

.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 

catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class  

.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 

catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class  

.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 

catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class  

.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar Setting;
    public static com.ustasoft.component.JDesktopImage jDesktopImage2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanelMaster;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JSplitPane jSplitPane2;
    private com.ustasoft.component.JTaskPaneContainer jTaskPaneContainer2;
    private javax.swing.JMenu mnuAkuntansiKasBank;
    private javax.swing.JMenu mnuAkuntasi;
    private javax.swing.JMenu mnuDaftar;
    private javax.swing.JMenuItem mnuDaftarBKK;
    private javax.swing.JMenuItem mnuDaftarBKM;
    private javax.swing.JMenuItem mnuDaftarPO;
    private javax.swing.JMenuItem mnuDaftarPembayaranPelanggan;
    private javax.swing.JMenuItem mnuDaftarPembayaranSupplier;
    private javax.swing.JMenuItem mnuDaftarPembelian;
    private javax.swing.JMenuItem mnuDaftarPenerimaanStok;
    private javax.swing.JMenuItem mnuDaftarPengeluaranStok;
    private javax.swing.JMenuItem mnuDaftarPenjualan;
    private javax.swing.JMenuItem mnuDaftarSO;
    private javax.swing.JMenuItem mnuDaftarStokOpname;
    private javax.swing.JMenuItem mnuDaftarTransfer;
    private javax.swing.JMenuItem mnuEntriHutang;
    private javax.swing.JMenuItem mnuEntriPiutang;
    private javax.swing.JMenuItem mnuEntrihutangHistori;
    private javax.swing.JMenuItem mnuFileExit;
    private javax.swing.JMenuItem mnuFileLogoff;
    private javax.swing.JMenuItem mnuFilePasswordChange;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuHutangPiutang;
    private javax.swing.JMenuItem mnuKartuTagihanHutang;
    private javax.swing.JMenuItem mnuKartuTagihanPiutang;
    private javax.swing.JMenuItem mnuKeuBukuBank;
    private javax.swing.JMenuItem mnuKeuJurnal;
    private javax.swing.JMenuItem mnuKeuKasBankKeluar;
    private javax.swing.JMenuItem mnuKeuKasBankMasuk;
    private javax.swing.JMenuItem mnuKeuKasBankRekonsiliasi;
    private javax.swing.JMenu mnuListBB;
    private javax.swing.JMenuItem mnuListBBJurnal;
    private javax.swing.JMenuItem mnuListItemHistoriAp;
    private javax.swing.JMenuItem mnuListItemHistoriAr;
    private javax.swing.JMenu mnuListKasBank;
    private javax.swing.JMenu mnuMaster;
    private javax.swing.JMenuItem mnuMasterAlatBayar;
    private javax.swing.JMenuItem mnuMasterCoa;
    private javax.swing.JMenuItem mnuMasterCustomer;
    private javax.swing.JMenuItem mnuMasterExpedisi;
    private javax.swing.JMenuItem mnuMasterGudang;
    private javax.swing.JMenuItem mnuMasterKota;
    private javax.swing.JMenuItem mnuMasterLokasi;
    private javax.swing.JMenuItem mnuMasterRelasiKategori;
    private javax.swing.JMenuItem mnuMasterSalesman;
    private javax.swing.JMenuItem mnuMasterSatuan;
    private javax.swing.JMenuItem mnuMasterStok;
    private javax.swing.JMenuItem mnuMasterStokKategori;
    private javax.swing.JMenuItem mnuMasterStokTrxType;
    private javax.swing.JMenuItem mnuMasterSupplier;
    private javax.swing.JMenuItem mnuMasterSupplierHarga;
    private javax.swing.JMenu mnuRpt;
    private javax.swing.JMenuItem mnuRptHutangPiutang;
    private javax.swing.JMenuItem mnuRptKasBankGL;
    private javax.swing.JMenuItem mnuRptKomisiSalesman;
    private javax.swing.JMenuItem mnuRptPembelian;
    private javax.swing.JMenuItem mnuRptPenjualan;
    private javax.swing.JMenuItem mnuRptPersediaan;
    private javax.swing.JMenu mnuSetting;
    private javax.swing.JMenuItem mnuSettingInforPerusahaan;
    private javax.swing.JMenuItem mnuSettingUser;
    private javax.swing.JMenuItem mnuSettingUserMenu;
    private javax.swing.JMenu mnuTrx;
    private javax.swing.JMenuItem mnuTrxBayarPelanggan;
    private javax.swing.JMenuItem mnuTrxJualFaktur;
    private javax.swing.JMenuItem mnuTrxJualRetur;
    private javax.swing.JMenuItem mnuTrxJualSO;
    private javax.swing.JMenuItem mnuTrxJualSaldoAwalAr;
    private javax.swing.JMenu mnuTrxPembelian;
    private javax.swing.JMenuItem mnuTrxPembelianBayarSupplier;
    private javax.swing.JMenuItem mnuTrxPembelianBeli;
    private javax.swing.JMenuItem mnuTrxPembelianPO;
    private javax.swing.JMenuItem mnuTrxPembelianRetur;
    private javax.swing.JMenuItem mnuTrxPembelianSaldoAwalAp;
    private javax.swing.JMenu mnuTrxPenjualan;
    private javax.swing.JMenu mnuTrxStok;
    private javax.swing.JMenuItem mnuTrxStokOpname;
    private javax.swing.JMenuItem mnuTrxStokPenerimaan;
    private javax.swing.JMenuItem mnuTrxStokPengeluaran;
    private javax.swing.JMenuItem mnuTrxStokTransfer;
    private javax.swing.JMenu mnuUtil;
    private javax.swing.JMenuItem mnuUtilLookupItem;
    private javax.swing.JMenuItem mnuUtilReminder;
    private org.jdesktop.swingx.JXTaskPane tpLaporan;
    private org.jdesktop.swingx.JXTaskPane tpList;
    private org.jdesktop.swingx.JXTaskPane tpMaster;
    private org.jdesktop.swingx.JXTaskPane tpTransaksi;
    // End of variables declaration//GEN-END:variables

    public void setConn(Connection connection) {
        this.conn = connection;
    }

    private void udfLoadReturJual() {
        if (isExistsOnDesktop(new FrmPenjualanRetur())) {
            return;
        }
        FrmPenjualanRetur f1 = new FrmPenjualanRetur();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadBayarPelanggan() {
        if (isExistsOnDesktop(new FrmArReceipt())) {
            return;
        }
        FrmArReceipt f1 = new FrmArReceipt();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadRptPembelian() {
        if (isExistsOnDesktop(new FrmRptPembelian())) {
            return;
        }
        FrmRptPembelian f1 = new FrmRptPembelian();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadRptPenjualan() {
        if (isExistsOnDesktop(new FrmRptPenjualan())) {
            return;
        }
        FrmRptPenjualan f1 = new FrmRptPenjualan();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadTransferHistory() {
        if (isExistsOnDesktop(new FrmTransferHis())) {
            return;
        }
        FrmTransferHis f1 = new FrmTransferHis();
        jDesktopImage2.add(f1);
        f1.setDesktop(jDesktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadReturPembelian() {
        if (isExistsOnDesktop(new FrmPembelianRetur())) {
            return;
        }
        FrmPembelianRetur f1 = new FrmPembelianRetur();
        jDesktopImage2.add(f1);
        //f1.setDesktop(jDesktopImage1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadMutasiHistory(String s) {
        if (s.equalsIgnoreCase("IN")) {
            if (isExistsOnDesktop(new FrmMutasiStokHis(), "Penerimaan Stok")) {
                return;
            }
            FrmMutasiStokHis f1 = new FrmMutasiStokHis();
            jDesktopImage2.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setTitle("Penerimaan Stok");
            f1.setInOut("IN");
            f1.setVisible(true);
        } else {
            if (isExistsOnDesktop(new FrmMutasiStokHis(), "Pengeluaran Stok")) {
                return;
            }
            FrmMutasiStokHis f1 = new FrmMutasiStokHis();
            jDesktopImage2.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setTitle("Pengeluaran Stok");
            f1.setInOut("OUT");
            f1.setVisible(true);
        }
    }

    private void udfLoadTrxMutasi(String iN) {
        if (iN.equalsIgnoreCase("IN")) {
            if (isExistsOnDesktop(new FrmMutasiStok(), "Penerimaan Stok")) {
                return;
            }
            FrmMutasiStok f1 = new FrmMutasiStok();
            jDesktopImage2.add(f1);
            f1.setInOut("IN");
            f1.setTitle("Penerimaan Stok");
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setVisible(true);
        } else {
            if (isExistsOnDesktop(new FrmMutasiStok(), "Pengeluaran Stok")) {
                return;
            }
            FrmMutasiStok f1 = new FrmMutasiStok();
            jDesktopImage2.add(f1);
            f1.setInOut("OUT");
            f1.setTitle("Pengeluaran Stok");
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setVisible(true);
        }
    }

    private void udfLoadAlatBayar() {
        if (isExistsOnDesktop(new FrmListAlatBayar(conn))) {
            return;
        }
        FrmListAlatBayar f1 = new FrmListAlatBayar(conn);
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
    }

    public void udfLoadPettyCash(String mk) {
        FrmBuktiKasRM f1 = new FrmBuktiKasRM();
        String sTitle = mk.equalsIgnoreCase("K") ? "Keluar" : "Masuk";
        if (isExistsOnDesktop(f1, "Bukti Kas " + sTitle)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setTitle("Bukti Kas " + sTitle);
        f1.setFlag(mk);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadPettyCashList() {
        FrmPettyCashList fRpt = new FrmPettyCashList();
        if (isExistsOnDesktop(fRpt)) {
            fRpt.dispose();
            return;
        }
        fRpt.setConn(conn);
        fRpt.setMainForm(this);
        fRpt.setDesktopPane(jDesktopImage2);
        fRpt.setVisible(true);
        fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
        jDesktopImage2.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            fRpt.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadCashFlow() {
        FrmCashFlow fRpt = new FrmCashFlow();
        if (isExistsOnDesktop(fRpt)) {
            fRpt.dispose();
            return;
        }
        fRpt.setConn(conn);
        fRpt.setVisible(true);
        fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
        jDesktopImage2.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            fRpt.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadEntriHutang() {
        FrmEntriHutang f1 = new FrmEntriHutang();
        if (isExistsOnDesktop(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }
    
    private void udfLoadEntriPiutang() {
        FrmEntriHutang f1 = new FrmEntriHutang();
        if (isExistsOnDesktop(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadHistoriEntriHutang() {
        FrmEntriHutangHis f1 = new FrmEntriHutangHis();
        if (isExistsOnDesktop(f1)) {
            f1.dispose();
            return;
        }
        f1.setDesktop(jDesktopImage2);
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadMenuAuth() {
        FrmSettingMenu f1 = new FrmSettingMenu();
        if (isExistsOnDesktop(f1)) {
            f1.dispose();
            return;
        }
        f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    public void udfSetUserMenu() {
        try {
            logOff();
            String sQry = "select menu_description, "
                    + "coalesce(can_insert,false) as can_insert, "
                    + "coalesce(can_update, false) as can_update, "
                    + "coalesce(can_delete, false) as can_delete, "
                    + "coalesce(can_read, false) as can_read, "
                    + "coalesce(can_print, false) as can_print, "
                    + "coalesce(can_correction, false) as can_correction "
                    + "from m_menu_authorization auth "
                    + "inner join m_menu_list list on list.id=auth.menu_id "
                    + "where user_name='" + sUserName + "' and module_name='POS'";

            ResultSet rs = conn.createStatement().executeQuery(sQry);

            MenuAuth mau = new MenuAuth();
            while (rs.next()) {
                if (rs.getString("menu_description").equalsIgnoreCase("Master Gudang")) {
                    mnuMasterGudang.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Lokasi")) {
                    mnuMasterLokasi.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Coa")) {
                    mnuMasterCoa.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Kota")) {
                    mnuMasterKota.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Satuan")) {
                    mnuMasterSatuan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Kategori Stok")) {
                    mnuMasterStokKategori.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Stok")) {
                    mnuMasterStok.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Tipe Transaksi Stok")) {
                    mnuMasterStokTrxType.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Supplier")) {
                    mnuMasterSupplier.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Kategori Relasi")) {
                    mnuMasterRelasiKategori.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Harga Supplier")) {
                    mnuMasterSupplierHarga.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Customer")) {
                    mnuMasterCustomer.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Salesman")) {
                    mnuMasterSalesman.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Expedisi")) {
                    mnuMasterExpedisi.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Alat Bayar")) {
                    mnuMasterAlatBayar.setVisible(rs.getBoolean("can_read"));
                }

                mnuMaster.setVisible(mnuMasterGudang.isVisible() || mnuMasterCoa.isVisible() || mnuMasterKota.isVisible()
                        || mnuMasterSatuan.isVisible() || mnuMasterStokKategori.isVisible() || mnuMasterStok.isVisible()
                        || mnuMasterSupplier.isVisible() || mnuMasterRelasiKategori.isVisible()
                        || mnuMasterSupplierHarga.isVisible()
                        || mnuMasterCustomer.isVisible() || mnuMasterSalesman.isVisible()
                        || mnuMasterExpedisi.isVisible() || mnuMasterAlatBayar.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Daftar PO")) {
                    mnuDaftarPO.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Pembelian")) {
                    mnuDaftarPembelian.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Pembayaran Supplier")) {
                    mnuDaftarPembayaranSupplier.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar SO")) {
                    mnuDaftarSO.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Penjualan")) {
                    mnuDaftarPenjualan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Pembayaran Pelanggan")) {
                    mnuDaftarPembayaranPelanggan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Transfer")) {
                    mnuDaftarTransfer.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Penerimaan Stok")) {
                    mnuDaftarPenerimaanStok.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Pengeluaran Stok")) {
                    mnuDaftarPengeluaranStok.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Stok Opname")) {
                    mnuDaftarStokOpname.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Daftar Pembayaran Pelanggan")) {
                    mnuDaftarPembayaranPelanggan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Histori Penjualan per Stok")) {
                    mnuListItemHistoriAr.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Histori Pembelian per Stok")) {
                    mnuListItemHistoriAp.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Histori Jurnal Umum")) {
                    mnuListBBJurnal.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Histori Kas/ Bank Masuk")) {
                    mnuDaftarBKM.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Histori Kas/ Bank Keluar")) {
                    mnuDaftarBKK.setVisible(rs.getBoolean("can_read"));
                }
                
                mnuDaftar.setVisible(mnuDaftarPO.isVisible() || mnuDaftarPembelian.isVisible()|| mnuDaftarPembayaranSupplier.isVisible()
                        || mnuDaftarSO.isVisible() || mnuDaftarPenjualan.isVisible()
                        || mnuDaftarTransfer.isVisible() || mnuDaftarPenerimaanStok.isVisible()
                        || mnuDaftarPengeluaranStok.isVisible()|| mnuDaftarPembayaranPelanggan.isVisible()
                        ||mnuListItemHistoriAr.isVisible()||mnuListItemHistoriAp.isVisible()
                        ||mnuDaftarBKK.isVisible()||mnuDaftarBKM.isVisible()||mnuListBBJurnal.isVisible()
                        ||mnuDaftarStokOpname.isVisible()
                        
                );
                jSeparator7.setVisible(mnuListBB.isVisible() && mnuListKasBank.isVisible());
                
                if (rs.getString("menu_description").equalsIgnoreCase("Entri Hutang")) {
                    mnuEntriHutang.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Entri Piutang")) {
                    mnuEntriPiutang.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Entri Hutang History")) {
                    mnuEntrihutangHistori.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Kartu Tagihan Pelanggan")) {
                    mnuKartuTagihanPiutang.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Kartu Hutang Supplier")) {
                    mnuKartuTagihanHutang.setVisible(rs.getBoolean("can_read"));
                }
                
                mnuHutangPiutang.setVisible(mnuEntriHutang.isVisible() || mnuEntriPiutang.isVisible() || mnuEntrihutangHistori.isVisible()
                ||mnuKartuTagihanHutang.isVisible() || mnuKartuTagihanPiutang.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Pesanan Pembelian - PO")) {
                    mnuTrxPembelianPO.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Transaksi Pembelian")) {
                    mnuTrxPembelianBeli.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Transaksi Retur Pembelian")) {
                    mnuTrxPembelianRetur.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Pembayaran Supplier")) {
                    mnuTrxPembelianBayarSupplier.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Saldo Awal Hutang Pembelian")) {
                    mnuTrxPembelianSaldoAwalAp.setVisible(rs.getBoolean("can_read"));
                }
                mnuTrxPembelian.setVisible(mnuTrxPembelianPO.isVisible() || mnuTrxPembelianBeli.isVisible() || 
                        mnuTrxPembelianRetur.isVisible()||mnuTrxPembelianBayarSupplier.isVisible()||mnuTrxPembelianSaldoAwalAp.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Penerimaan Stok")) {
                    mnuTrxStokPenerimaan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Pengeluaran Stok")) {
                    mnuTrxStokPengeluaran.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Transfer Stok")) {
                    mnuTrxStokTransfer.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Stok Opname")) {
                    mnuTrxStokOpname.setVisible(rs.getBoolean("can_read"));
                }
                mnuTrxStok.setVisible(mnuTrxStokPenerimaan.isVisible() || mnuTrxStokPengeluaran.isVisible() || 
                        mnuTrxStokTransfer.isVisible()||mnuTrxStokOpname.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Pesanan Penjualan - SO")) {
                    mnuTrxJualSO.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Transaksi Penjualan")) {
                    mnuTrxJualFaktur.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Transaksi Retur Penjualan")) {
                    mnuTrxJualRetur.setVisible(rs.getBoolean("can_read"));
                }

                if (rs.getString("menu_description").equalsIgnoreCase("Transaksi Pembayaran Pelanggan")) {
                    mnuTrxBayarPelanggan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Saldo Awal Piutang Penjualan")) {
                    mnuTrxJualSaldoAwalAr.setVisible(rs.getBoolean("can_read"));
                }
                mnuTrxPenjualan.setVisible(mnuTrxJualFaktur.isVisible() || mnuTrxJualSO.isVisible()
                        || mnuTrxJualRetur.isVisible() || mnuTrxBayarPelanggan.isVisible());

                mnuTrx.setVisible(mnuTrxPenjualan.isVisible() || mnuTrxPembelian.isVisible() || mnuTrxStok.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Setting User")) {
                    mnuSettingUser.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Setting Otorisasi Menu")) {
                    mnuSettingUserMenu.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Informasi Perusahan")) {
                    mnuSettingInforPerusahaan.setVisible(rs.getBoolean("can_read"));
                }
                mnuSetting.setVisible(mnuSettingUser.isVisible() || mnuSettingUserMenu.isVisible() || mnuSettingInforPerusahaan.isVisible());
                
                if (rs.getString("menu_description").equalsIgnoreCase("Lookup Item")) {
                    mnuUtilLookupItem.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Pengingat")) {
                    mnuUtilReminder.setVisible(rs.getBoolean("can_read"));
                }
                mnuUtil.setVisible(mnuUtilLookupItem.isVisible() || mnuUtilReminder.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Persediaan")) {
                    mnuRptPersediaan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Penjualan")) {
                    mnuRptPenjualan.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Pembelian")) {
                    mnuRptPembelian.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Komisi Sales")) {
                    mnuRptKomisiSalesman.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Hutang - Piutang")) {
                    mnuRptHutangPiutang.setVisible(rs.getBoolean("can_read"));
                }
                mnuRpt.setVisible(mnuRptPembelian.isVisible() || mnuRptPenjualan.isVisible() || 
                        mnuRptPersediaan.isVisible()||mnuRptKomisiSalesman.isVisible()||
                        mnuRptHutangPiutang.isVisible());

                if (rs.getString("menu_description").equalsIgnoreCase("Bukti Kas Keluar")) {
                    mnuKeuKasBankKeluar.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Bukti Kas Masuk")) {
                    mnuKeuKasBankMasuk.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Rekonsiliasi Bank")) {
                    mnuKeuKasBankRekonsiliasi.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Jurnal Umum")) {
                    mnuKeuJurnal.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Buku Bank")) {
                    mnuKeuBukuBank.setVisible(rs.getBoolean("can_read"));
                }
                if (rs.getString("menu_description").equalsIgnoreCase("Laporan Kas/ Bank & Bukut Besar")) {
                    mnuRptKasBankGL.setVisible(rs.getBoolean("can_read"));
                }
                mnuAkuntansiKasBank.setVisible(mnuKeuKasBankKeluar.isVisible() || mnuKeuKasBankMasuk.isVisible() 
                        || mnuKeuKasBankRekonsiliasi.isVisible() ||mnuKeuBukuBank.isVisible());
                mnuAkuntasi.setVisible(mnuAkuntansiKasBank.isVisible() || mnuKeuJurnal.isVisible());
            }
            udfAddActionTransaksi();
            udfAddActionList();
            udfAddActionMaster();
            udfAddActionReport();

            rs.close();
//            rs=conn.createStatement().executeQuery("select photo from m_user where username='"+sUserName+"'");
//            if(rs.next()){
//                byte[] imgBytes = rs.getBytes("photo");
//
//                if(imgBytes!=null){
//                    javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(imgBytes);
//                    javax.swing.ImageIcon bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
//                                       (lblPhoto.getWidth(), lblPhoto.getHeight(), Image.SCALE_REPLICATE));
//
//                    lblPhoto.setIcon(bigImage);
//                    imgBytes=null;
//                }else{
//                    lblPhoto.setIcon(null);
//                }
//            }

        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    private void logOff() {
        JMenu jMenu;
        for (int i = 0; i < Setting.getMenuCount(); i++) {
            jMenu = (JMenu) Setting.getComponent(i);
            if (!(jMenu.getText().equals("File")||jMenu.getText().equals("Help"))) {
                jMenu.setVisible(false);
            } else {
                continue;
            }
            for (int y = 0; y < jMenu.getItemCount(); y++) {
                //System.out.println("menu "+jMenu.getText());
                if (jMenu.getMenuComponent(y).getClass().getName().equalsIgnoreCase("javax.swing.JMenuItem")) {
                    ((JMenuItem) jMenu.getMenuComponent(y)).setVisible(false);
                } else if (jMenu.getMenuComponent(y).getClass().getName().equalsIgnoreCase("javax.swing.JMenu")) {
                    
                    JMenu jMenuSub = (JMenu) jMenu.getMenuComponent(y);
                    //System.out.println("Ini adalah jMenu dengan panjang "+jMenuSub.getItemCount());
                    for (int z = 0; z < jMenuSub.getItemCount(); z++) {
                        System.out.println("z: "+z+" menu id: "+jMenuSub.getItem(z).getText());
                        jMenuSub.getItem(z).setVisible(false);
                    }
                }
            }
        }
        
        for(int i=0; i<jTaskPaneContainer2.getComponentCount(); i++){
            if(jTaskPaneContainer2.getComponent(i) instanceof JXTaskPane){
                ((JXTaskPane)jTaskPaneContainer2.getComponent(i)).removeAll();
                ((JXTaskPane)jTaskPaneContainer2.getComponent(i)).setExpanded(false);
            }
        }
    }

    private void udfInitForm() {
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT coalesce(nama_toko,'') as nama_toko, "
                    + "coalesce(alamat1,'') as alamat1, "
                    + "coalesce(alamat2,'') as alamat2, "
                    + "coalesce(kota,'') as kota, "
                    + "coalesce(telp1,'') as telp1, "
                    + "coalesce(telp2,'') as telp2,"
                    + "coalesce(email,'') as email, "
                    + "coalesce(tipe_printer_pos,'') as tipe_printer_pos, "
                    + "coalesce(tipe_usaha,'') as tipe_usaha "
                    + "FROM m_setting;");
            if (rs.next()) {
                sToko = rs.getString("nama_toko");
                sAlamat1 = rs.getString("alamat1");
                sAlamat2 = rs.getString("alamat2");
                sKota = rs.getString("kota");
                sTelp1 = rs.getString("telp1");
                sTelp2 = rs.getString("telp2");
                sEmail = rs.getString("email");
                sPrinterPos = rs.getString("tipe_printer_pos");
                sTipeUsaha = rs.getString("tipe_usaha");
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select count(*) from fn_reminder_list('') as (urit int, tipe varchar, id_relasi bigint, nama varchar, id_trx bigint, invoice_no varchar, \n" +
                    "invoice_date date, invoice_amount double precision, owing double precision, src varchar)");
            if(rs.next() && rs.getInt(1) > 0){
                FrmReminder f1=new FrmReminder();
                jDesktopImage2.add(f1);
                f1.setConn(conn);
                f1.setVisible(true);
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select current_date");
            rs.next();
            tglSkg=rs.getDate(1);
            rs.close();
            
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfSetUserMenu();
    }

    private void udfLoadLokasi() {
        FrmLokasi f1 = new FrmLokasi();
        if (isExistsOnDesktop(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopImage2.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfAddActionTransaksi() {
        tpTransaksi.removeAll();
//        if (menuItem.canRead()) {
        tpTransaksi.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Penjualan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuTrxJualFakturActionPerformed(null);
            }
        });
        tpTransaksi.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuTrxPembelianBeliActionPerformed(null);
            }
        });
        tpTransaksi.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Retur Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Retur Penjualan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuTrxJualReturActionPerformed(e);
            }
        });
        tpTransaksi.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Retur Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Retur Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuTrxPembelianReturActionPerformed(e);
            }
        });
        tpTransaksi.setExpanded(true);
    }
    
    private void udfAddActionList() {
        tpList.removeAll();
        tpList.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Penjualan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }
            public void actionPerformed(ActionEvent e) {
                mnuDaftarPenjualanActionPerformed(e);
            }
        });
        tpList.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }
            public void actionPerformed(ActionEvent e) {
                mnuDaftarPembelianActionPerformed(e);
            }
        });
        tpList.add(new JPopupMenu.Separator());
        tpList.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembayaran Pelanggan");
                putValue(Action.SHORT_DESCRIPTION, "Daftar Pembayaran Pelanggan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }
            public void actionPerformed(ActionEvent e) {
                mnuDaftarPembayaranPelangganActionPerformed(e);
            }
        });
        tpList.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembayaran Supplier");
                putValue(Action.SHORT_DESCRIPTION, "Daftar Pembayaran Hutang Supplier");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }
            public void actionPerformed(ActionEvent e) {
                mnuDaftarPembayaranSupplierActionPerformed(e);
            }
        });
        tpList.setExpanded(true);
    }
    private void udfAddActionMaster() {
        tpMaster.removeAll();
        
        if(mnuMasterCoa.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "COA");
                putValue(Action.SHORT_DESCRIPTION, "COA");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterCoaActionPerformed(e);
            }
        });
        if(mnuMasterGudang.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Gudang");
                putValue(Action.SHORT_DESCRIPTION, "Gudang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterGudangActionPerformed(e);
            }
        });
        if(mnuMasterLokasi.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Lokasi");
                putValue(Action.SHORT_DESCRIPTION, "Lokasi");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterLokasiActionPerformed(e);
            }
        });
        if(mnuMasterKota.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Kota");
                putValue(Action.SHORT_DESCRIPTION, "Kota");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterKotaActionPerformed(e);
            }
        });
        if(mnuMasterCoa.isVisible() || mnuMasterKota.isVisible()||mnuMasterGudang.isVisible()||mnuMasterLokasi.isVisible()){
            tpMaster.add(new JPopupMenu.Separator());
        }
        
        if(mnuMasterSatuan.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Satuan");
                putValue(Action.SHORT_DESCRIPTION, "Satuan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterSatuanActionPerformed(e);
            }
        });
        if(mnuMasterStokKategori.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Kategori Stok");
                putValue(Action.SHORT_DESCRIPTION, "Kategori Stok");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterStokKategoriActionPerformed(e);
            }
        });
        if(mnuMasterStok.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Stok");
                putValue(Action.SHORT_DESCRIPTION, "Stok");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterStokActionPerformed(e);
            }
        });
        if(mnuMasterRelasiKategori.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Kategori Relasi");
                putValue(Action.SHORT_DESCRIPTION, "Kategori Relasi");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterRelasiKategoriActionPerformed(e);
            }
        });
        if(mnuMasterRelasiKategori.isVisible()||mnuMasterStok.isVisible()||mnuMasterStokKategori.isVisible()||mnuMasterSatuan.isVisible()){
            tpMaster.add(new JPopupMenu.Separator());
        }
        
        if(mnuMasterSupplier.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Supplier");
                putValue(Action.SHORT_DESCRIPTION, "Master Supplier");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterSupplierActionPerformed(e);
            }
        });
        if(mnuMasterSupplierHarga.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Harga Supplier");
                putValue(Action.SHORT_DESCRIPTION, "Master Harga Supplier");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterSupplierHargaActionPerformed(e);
            }
        });
        if(mnuMasterSupplierHarga.isVisible()||mnuMasterSupplier.isVisible()){
            tpMaster.add(new JPopupMenu.Separator());
        }
        
        if(mnuMasterCustomer.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Customer");
                putValue(Action.SHORT_DESCRIPTION, "Master Customer");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterCustomerActionPerformed(e);
            }
        });
        if(mnuMasterCustomer.isVisible()){
            tpMaster.add(new JPopupMenu.Separator());
        }
        
        if(mnuMasterSalesman.isVisible())
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Salesman");
                putValue(Action.SHORT_DESCRIPTION, "Master Salesman");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterSalesmanActionPerformed(e);
            }
        });
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Expedisi");
                putValue(Action.SHORT_DESCRIPTION, "Master Expedisi");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterExpedisiActionPerformed(e);
            }
        });
        tpMaster.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Alat Bayar");
                putValue(Action.SHORT_DESCRIPTION, "Master Alat Bayar");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuMasterAlatBayarActionPerformed(e);
            }
        });
        tpMaster.setExpanded(false);
    }

    private void udfAddActionReport(){
        tpLaporan.removeAll();
        if(mnuRptPenjualan.isVisible())
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Laporan Penjualan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPenjualanActionPerformed(e);
            }
        });
        if(mnuRptPembelian.isVisible())
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Laporan pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPembelianActionPerformed(e);
            }
        });
        if(mnuRptHutangPiutang.isVisible())
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Hutang & Piutang");
                putValue(Action.SHORT_DESCRIPTION, "Laporan Hutang & Piurang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptHutangPiutangActionPerformed(e);
            }
        });
        if(mnuRptPersediaan.isVisible())    
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Persediaan");
                putValue(Action.SHORT_DESCRIPTION, "Laporan persediaan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPersediaanActionPerformed(e);
            }
        });
        if(mnuRptKomisiSalesman.isVisible())    
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Komisi Salesman");
                putValue(Action.SHORT_DESCRIPTION, "Laporan Komisi Salesman");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptKomisiSalesmanActionPerformed(e);
            }
        });
        if(mnuRptKomisiSalesman.isVisible())    
        tpLaporan.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Kas/ Bank & GL");
                putValue(Action.SHORT_DESCRIPTION, "Laporan Kas/ Bank & Buku Besar");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptKasBankGLActionPerformed(e);
            }
        });
        
        tpLaporan.setExpanded(true);
    }
    
    private void udfLoadSettingPerusahaan() {
        if (isExistsOnDesktop(new FrmInfoPerusahaan())) {
                return;
            }
            FrmInfoPerusahaan f1 = new FrmInfoPerusahaan();
            jDesktopImage2.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setVisible(true);
    }

    private void udfLoadMasterStockType() {
        if (isExistsOnDesktop(new FrmListStockType())) {
            return;
        }
        FrmListStockType f1 = new FrmListStockType();
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.initForm(conn);
        f1.setVisible(true);
    }

    private void udfLoadDaftarPembayaranPelanggan() {
        if (isExistsOnDesktop(new FrmArReceiptHis())) {
            return;
        }
        FrmArReceiptHis f1 = new FrmArReceiptHis();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
    }

    private void udfLoadBayarSupplier() {
        if (isExistsOnDesktop(new FrmApPayment())) {
            return;
        }
        FrmApPayment f1 = new FrmApPayment();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
    }

    private void udfLoadDaftarBayarSupplier() {
        if (isExistsOnDesktop(new FrmApPaymentHis())) {
            return;
        }
        FrmApPaymentHis f1 = new FrmApPaymentHis();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
    }

    private void udfLoadReminder() {
        if (isExistsOnDesktop(new FrmReminder())) {
            return;
        }
        FrmReminder f1 = new FrmReminder();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
    }

    private void udfLoadSaldoAwalAr() {
        if (isExistsOnDesktop(new FrmSaldoAwalAR())) {
            return;
        }
        FrmSaldoAwalAR f1=new FrmSaldoAwalAR();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }
    private void udfLoadSaldoAwalAp() {
        if (isExistsOnDesktop(new FrmSaldoAwalAP())) {
            return;
        }
        FrmSaldoAwalAP f1=new FrmSaldoAwalAP();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadListItemHistoryAr() {
        if (isExistsOnDesktop(new FrmItemHistoryAR())) {
            return;
        }
        FrmItemHistoryAR f1=new FrmItemHistoryAR();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadListItemHistoryAp() {
        if (isExistsOnDesktop(new FrmItemHistoryAP())) {
            return;
        }
        FrmItemHistoryAP f1=new FrmItemHistoryAP();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadJurnalUmum() {
        if (isExistsOnDesktop(new FrmJournalEntryRM())) {
            return;
        }
        FrmJournalEntryRM f1=new FrmJournalEntryRM();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadJurnalUmumHis() {
        if (isExistsOnDesktop(new FrmJournalEntryRMHis())) {
            return;
        }
        FrmJournalEntryRMHis f1=new  FrmJournalEntryRMHis();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadBKHis(String m) {
        String mk=m.equalsIgnoreCase("M")? "Masuk": "Keluar";
        if (isExistsOnDesktop(new FrmBuktiKasRMHis(), "Histori Bukti Kas "+mk)) {
            return;
        }
        FrmBuktiKasRMHis f1=new  FrmBuktiKasRMHis();
        f1.setConn(conn);
        f1.setFlag(m);
        f1.setTitle("Histori Bukti Kas "+mk);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadBukuBank() {
        if (isExistsOnDesktop(new FrmBukuBank())) {
            return;
        }
        FrmBukuBank f1=new  FrmBukuBank();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadRekonsiliasi() {
        if (isExistsOnDesktop(new FrmBankRekonsiliasi())) {
            return;
        }
        FrmBankRekonsiliasi f1=new  FrmBankRekonsiliasi();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadRptKomisiSalesman() {
        if (isExistsOnDesktop(new FrmRptKomisiSalesman())) {
            return;
        }
        FrmRptKomisiSalesman f1=new  FrmRptKomisiSalesman();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadKartuPiutang() {
        if (isExistsOnDesktop(new FrmKartuPiutang())) {
            return;
        }
        FrmKartuPiutang f1=new  FrmKartuPiutang();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadRptHutangPiutang() {
        if (isExistsOnDesktop(new FrmRptHutangPiutang())) {
            return;
        }
        FrmRptHutangPiutang f1=new  FrmRptHutangPiutang();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadKartuHutang() {
            if (isExistsOnDesktop(new FrmKartuHutangPerSupplier())) {
            return;
        }
        FrmKartuHutangPerSupplier f1=new  FrmKartuHutangPerSupplier();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadRptBBGL() {
        if (isExistsOnDesktop(new FrmRptBankGL())) {
            return;
        }
        FrmRptBankGL f1=new  FrmRptBankGL();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadStokOpname() {
        if (isExistsOnDesktop(new FrmStokOpname())) {
            return;
        }
        FrmStokOpname f1=new  FrmStokOpname();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }

    private void udfLoadDaftarStokOpname() {
        if (isExistsOnDesktop(new FrmStokOpnameHis())) {
            return;
        }
        FrmStokOpnameHis f1=new  FrmStokOpnameHis();
        f1.setConn(conn);
        jDesktopImage2.add(f1);
        f1.setVisible(true);
    }
}
