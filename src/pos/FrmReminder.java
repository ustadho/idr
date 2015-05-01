/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.service.reminder.ReminderGroup;
import com.ustasoft.pos.service.reminder.ReminderHeader;
import com.ustasoft.pos.service.reminder.ReminderIsi;
import com.ustasoft.pos.service.reminder.ReminderModel;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import pos.ap.FrmApPayment;
import pos.ap.FrmPembelian;
import pos.ar.FrmArReceipt;
import pos.ar.FrmPenjualan;
import pos.ar.FrmSaldoAwalAR;

/**
 *
 * @author faheem
 */
public class FrmReminder extends javax.swing.JInternalFrame {
    private Connection conn;
    List<ReminderGroup> root = null;
    JPopupMenu menu = new JPopupMenu();

    /**
     * Creates new form FrmReminder
     */
    public FrmReminder() {
        initComponents();
        JMenuItem menuDetailTrx = new JMenuItem("Detail Transaksi");
        menuDetailTrx.setFont(new Font(menuDetailTrx.getFont().getFontName(), Font.BOLD, menuDetailTrx.getFont().getSize()));
        menuDetailTrx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int iRow = jXTreeTable1.getSelectedRow();
                if (iRow < 0 || jXTreeTable1.getValueAt(iRow, 3) == null || !(jXTreeTable1.getValueAt(iRow, 2) instanceof Integer)) {
                    return;
                }
                Integer id = (Integer) jXTreeTable1.getValueAt(iRow, 2);
                String src = jXTreeTable1.getValueAt(iRow, 3).toString();
                detailTrx(id, src);
            }
        });
        menu.add(menuDetailTrx);
        JMenuItem menuBayar = new JMenuItem("Bayar");
        menuBayar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int iRow = jXTreeTable1.getSelectedRow();
                if (iRow < 0 || jXTreeTable1.getValueAt(iRow, 3) == null || !(jXTreeTable1.getValueAt(iRow, 2) instanceof Integer)) {
                    return;
                }
                Integer id = (Integer) jXTreeTable1.getValueAt(iRow, 2);
                String src = jXTreeTable1.getValueAt(iRow, 3).toString();
                bayar(id, src);
            }
        });
        menu.add(menuBayar);
        JMenuItem menuRefresh = new JMenuItem("Refresh");
        menuRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        menu.add(menuRefresh);
        
        jXTreeTable1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                //if (evt.isPopupTrigger()) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    int iRow = jXTreeTable1.getSelectedRow();
                    if (iRow < 0 || evt.getClickCount() != 2 || jXTreeTable1.getValueAt(iRow, 3) == null || !(jXTreeTable1.getValueAt(iRow, 2) instanceof Integer)) {
                        return;
                    }
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
//                    if(jXTreeTable1.getValueAt(jXTreeTable1.getSelectedRow(), 2).toString().length()==0)
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        
    }

    public void setConn(Connection c) {
        this.conn = c;
    }
    GeneralFunction fn = new GeneralFunction();

    public void refresh() {
        root = new ArrayList<ReminderGroup>();
        loadJthTempo("AR");
        loadJthTempo("AP");
        ReminderModel myModel = new ReminderModel(root);
        jXTreeTable1.setTreeTableModel(myModel);
        jXTreeTable1.getColumn(1).setCellRenderer(new MyRowRenderer());
        jXTreeTable1.getColumn(0).setPreferredWidth(500);
        jXTreeTable1.setHighlighters(HighlighterFactory.createAlternateStriping());
        jXTreeTable1.getColumn(2).setMaxWidth(0);
        jXTreeTable1.getColumn(2).setMinWidth(0);
        jXTreeTable1.getColumn(2).setPreferredWidth(0);
        jXTreeTable1.getColumn(3).setMaxWidth(0);
        jXTreeTable1.getColumn(3).setMinWidth(0);
        jXTreeTable1.getColumn(3).setPreferredWidth(0);
        
    }

    private void loadJthTempo(String tipe) {
        try {
            String sOldRelasi = "", sTipe = "";
            String sql = "select * from fn_reminder_list('" + tipe + "') as (urut int, tipe varchar, id_relasi bigint, nama varchar, id_trx bigint, invoice_no varchar, \n"
                    + "invoice_date date, invoice_amount double precision, owing double precision, src varchar)";
            ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
            double totSupp = 0, totTipe = 0;
            List<ReminderIsi> isi = new ArrayList<ReminderIsi>();
            List<ReminderHeader> header = new ArrayList<ReminderHeader>();
            while (rs.next()) {
                isi.add(new ReminderIsi(rs.getString("src"), rs.getString("invoice_no") + " / " + fn.ddMMyy_format.format(rs.getDate("invoice_date")),
                        rs.getDouble("owing"), rs.getInt("id_trx")));
                totSupp += rs.getDouble("owing");
                totTipe += rs.getDouble("owing");
                sTipe = rs.getString("tipe");
                sOldRelasi = rs.getString("nama");
                if (rs.next() && !sOldRelasi.equalsIgnoreCase("") && !sOldRelasi.equalsIgnoreCase(rs.getString("nama"))) {
                    rs.previous();
                    header.add(new ReminderHeader(rs.getString("nama"), totSupp, isi));
                    totSupp = 0;
                    isi = new ArrayList<ReminderIsi>();

                } else {
                    rs.previous();
                }

            }
            rs.close();
            header.add(new ReminderHeader(sOldRelasi, totSupp, isi));
            ReminderGroup group = new ReminderGroup(sTipe, header, totTipe);

            root.add(group);
        } catch (SQLException ex) {
            Logger.getLogger(FrmReminder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void udfEditAR(Integer id) {
        FrmPenjualan f1 = new FrmPenjualan();
        f1.setConn(conn);
        f1.setTitle("Faktur Penjualan - Edit");
        MainForm.jDesktopImage2.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.tampilkanData(id);
        f1.setIdPenjualan(id);
        f1.setSrcForm(this);
        f1.setVisible(true);
    }
    private void udfEditAP(Integer id) {
        FrmPembelian f1 = new FrmPembelian();
        f1.setConn(conn);
        f1.setTitle("Faktur Pembelian - Edit");
        MainForm.jDesktopImage2.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.tampilkanData(id);
        f1.setIdPembelian(id);
        f1.setSrcForm(this);
        f1.setVisible(true);
    }

    private void udfEditARSaldo(Integer id) {
        FrmSaldoAwalAR f1=new FrmSaldoAwalAR();
        f1.setConn(conn);
        f1.setIdAr(id);
        MainForm.jDesktopImage2.add(f1, JLayeredPane.DEFAULT_LAYER);
        f1.setSrcForm(this);
        f1.setVisible(true);
    }

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
            if (value instanceof Double || value instanceof Integer || value instanceof Float) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                value = new DecimalFormat("#,##0").format(value);
            }

            setValue(value);
            return this;
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

        jScrollPane3 = new javax.swing.JScrollPane();
        jXTreeTable1 = new org.jdesktop.swingx.JXTreeTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pengingat");
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

        jXTreeTable1.setAutoCreateRowSorter(true);
        jXTreeTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTreeTable1.getTableHeader().setReorderingAllowed(false);
        jXTreeTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jXTreeTable1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jXTreeTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        refresh();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jXTreeTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jXTreeTable1MouseClicked
        int iRow = jXTreeTable1.getSelectedRow();
        if (iRow < 0 || evt.getClickCount() != 2 || jXTreeTable1.getValueAt(iRow, 3) == null || !(jXTreeTable1.getValueAt(iRow, 2) instanceof Integer)) {
            return;
        }
        Integer id = (Integer) jXTreeTable1.getValueAt(iRow, 2);
        String src = jXTreeTable1.getValueAt(iRow, 3).toString();

        detailTrx(id, src);
    }//GEN-LAST:event_jXTreeTable1MouseClicked

    private void detailTrx(Integer id, String src) {

        if (src.equalsIgnoreCase("PJL")) {
            udfEditAR(id);
        }
        else if (src.equalsIgnoreCase("SAR")) {
            udfEditARSaldo(id);
        }
        else if (src.equalsIgnoreCase("PBL")) {
            udfEditAP(id);
        }
    }

    private void bayar(Integer id, String src) {
        if (src.equalsIgnoreCase("PJL") || src.equalsIgnoreCase("SAR")) {
            FrmArReceipt f1 = new FrmArReceipt();
            MainForm.jDesktopImage2.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setJtTempo(true);
            f1.setId(id);
            f1.setSrcForm(this);
            f1.setVisible(true);
        }else if (src.equalsIgnoreCase("PBL")){
            FrmApPayment f1 = new FrmApPayment();
            MainForm.jDesktopImage2.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setConn(conn);
            f1.setJtTempo(true);
            f1.setId(id);
            f1.setSrcForm(this);
            f1.setVisible(true);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTreeTable jXTreeTable1;
    // End of variables declaration//GEN-END:variables
}
