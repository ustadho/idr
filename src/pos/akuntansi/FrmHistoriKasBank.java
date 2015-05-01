/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package pos.akuntansi;

import com.ustasoft.component.GeneralFunction;
import java.awt.Component;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.border.DropShadowBorder;
import pos.MainForm;


/**
 *
 * @author  oestadho
 */
public class FrmHistoriKasBank extends javax.swing.JInternalFrame {
    //private DropShadowBorder dsb = new DropShadowBorder(UIManager.getColor("Control"), 0, 8, .5f, 12, false, true, true, true);
    DefaultTableModel myModel;
    private DropShadowBorder dsb=new DropShadowBorder();
    //private DropShadowBorder dsb = new DropShadowBorder(new Color(255,240,240), 0, 8, .5f, 12, false, true, true, true);
    
    private Connection conn;
    private JDesktopPane jDesktop;
    private ArrayList lstResort=new ArrayList();
    private String sFlag;
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    public void setFlag(String s){
        this.sFlag=s;
    }
    
    /** Creates new form ListAkun */
    public FrmHistoriKasBank() {
        initComponents();
        masterTable.getColumn("Tanggal").setCellRenderer(new MyRowRenderer());
        
        panelHeader.setBorder(dsb);
        jScrollPane1.setBorder(dsb);
    }

    public void setDesktopPane(JDesktopPane jDesktopPane1) {
        jDesktop =jDesktopPane1;
    }

    
    private void udfInitForm(){
        myModel=(DefaultTableModel)masterTable.getModel();
        masterTable.setModel(myModel);
        myModel.setNumRows(0);
        jDateChooser1.setDate(MainForm.tglSkg);
        jDateChooser2.setDate(MainForm.tglSkg);
        setTitle("Bukti Kas/ Bank "+ (sFlag.equalsIgnoreCase("M")? "Masuk": "Keluar") );
        txtNama.setText("");
        udfFilter("");
    }
    
    public void udfFilter(String sNo){
        if(myModel==null) return;
        myModel.setNumRows(0);
        String s="select no_bukti, tanggal, coalesce(amount,0) as amount, coalesce(memo,'') as keterangan " +
                "from acc_bukti_kas " +
                "where to_char(tanggal, 'yyyy-MM-dd')>='"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser1.getDate()) +"' " +
                "and to_char(tanggal, 'yyyy-MM-dd')<='"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser2.getDate()) +"' " +
                "and coalesce(batal, false)=false " +
                "and flag='"+sFlag+"' " +
                "and coalesce(memo,'') ilike '%"+txtNama.getText()+"%' order by tanggal ";
        try {
            ResultSet rs = conn.createStatement().executeQuery(s);
            int iSelected=0;
            while(rs.next()){
                myModel.addRow(new Object[]{
                    rs.getString("no_bukti"),
                    rs.getDate("tanggal"),
                    rs.getString("keterangan"),
                    rs.getDouble("amount")
                });
                if(sNo.equalsIgnoreCase(rs.getString("no_bukti")))
                    iSelected=myModel.getRowCount()-1;
            }
            if(myModel.getRowCount()>0) masterTable.setRowSelectionInterval(iSelected, iSelected);
            //cmbTipeAkun.setSelectedIndex(-1);
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }

    private void udfDelete(){
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus bukti kas bank tersebut", "Confir", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try {
                String sNoBukti = masterTable.getValueAt(masterTable.getSelectedRow(), 0).toString();
                conn.setAutoCommit(false);
                int i = conn.createStatement().executeUpdate("delete from acc_bukti_kas where no_bukti='" + sNoBukti + "'");
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, "Delete data sukses!");
                udfFilter("");
            } catch (SQLException ex) {
                Logger.getLogger(FrmHistoriKasBank.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean udfCekNumeric(String str){
        boolean flag = true;
        char[] stringArray;
        stringArray = str.toCharArray();
        for(int index=0; index < stringArray.length; index++){
//            System.out.print(stringArray[index]);
            if (!((stringArray[index] >= '0' && stringArray[index] <= '9')) &&
                  stringArray[index] != '-' && stringArray[index] != ' '){
                flag = false;
          }
        }
        return flag;
    }

    

    private void udfLoadBuktiKasBank(){
        FrmBuktiKas fJournal=new FrmBuktiKas();
        fJournal.setConn(conn);
        fJournal.setKoreksi(true);
        fJournal.setFlag(sFlag);
        fJournal.setKoreksi(true);
        fJournal.setKode(masterTable.getValueAt(masterTable.getSelectedRow(), 0).toString());
        fJournal.setVisible(true);
    }

    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblUbah = new org.jdesktop.swingx.JXHyperlink();
        hypLinkEdit = new org.jdesktop.swingx.JXHyperlink();
        hypLinkDelete = new org.jdesktop.swingx.JXHyperlink();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        btnLoad = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Bukti Jurnal Umum"); // NOI18N
        setName("Form"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        panelHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelHeader.setName("panelHeader"); // NOI18N

        jXHyperlink5.setMnemonic('R');
        jXHyperlink5.setName("jXHyperlink5"); // NOI18N
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNama.setName("txtNama"); // NOI18N
        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNamaKeyReleased(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 240, -1));

        jLabel3.setText("Cari "); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 20));

        jButton1.setText("Tampilkan"); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 120, -1));

        lblUbah.setMnemonic('U');
        lblUbah.setText("Ubah"); // NOI18N
        lblUbah.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUbah.setName("lblUbah"); // NOI18N
        lblUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblUbahActionPerformed(evt);
            }
        });

        hypLinkEdit.setText("Baru"); // NOI18N
        hypLinkEdit.setFont(new java.awt.Font("Tahoma", 1, 11));
        hypLinkEdit.setName("hypLinkEdit"); // NOI18N
        hypLinkEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypLinkEditActionPerformed(evt);
            }
        });

        hypLinkDelete.setText("Delete"); // NOI18N
        hypLinkDelete.setFont(new java.awt.Font("Tahoma", 1, 11));
        hypLinkDelete.setName("hypLinkDelete"); // NOI18N
        hypLinkDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypLinkDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hypLinkEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hypLinkDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hypLinkEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUbah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hypLinkDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        masterTable.setAutoCreateRowSorter(true);
        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Tanggal", "Description", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class
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
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.setName("masterTable"); // NOI18N
        masterTable.getTableHeader().setReorderingAllowed(false);
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);

        jXTitledPanel1.setTitle("Filter"); // NOI18N
        jXTitledPanel1.setName("jXTitledPanel1"); // NOI18N

        jDateChooser1.setDateFormatString("dd-MM-yyyy"); // NOI18N
        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jLabel1.setText("Tanggal Awal"); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText("Tanggal Akhir"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jDateChooser2.setDateFormatString("dd-MM-yyyy"); // NOI18N
        jDateChooser2.setName("jDateChooser2"); // NOI18N

        btnLoad.setText("Load"); // NOI18N
        btnLoad.setName("btnLoad"); // NOI18N
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXTitledPanel1Layout = new javax.swing.GroupLayout(jXTitledPanel1.getContentContainer());
        jXTitledPanel1.getContentContainer().setLayout(jXTitledPanel1Layout);
        jXTitledPanel1Layout.setHorizontalGroup(
            jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jXTitledPanel1Layout.setVerticalGroup(
            jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLoad)
                .addContainerGap(317, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

}//GEN-LAST:event_formInternalFrameClosed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    udfInitForm();
}//GEN-LAST:event_jXHyperlink5ActionPerformed

private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
    udfFilter("");
}//GEN-LAST:event_txtNamaKeyReleased

private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
    if(evt.getClickCount()==2){
        udfUpdate();
    }
}//GEN-LAST:event_masterTableMouseClicked

private void lblUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblUbahActionPerformed
    if (masterTable.getSelectedRow()>=0)   udfLoadBuktiKasBank();
}//GEN-LAST:event_lblUbahActionPerformed

private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
    udfFilter("");
}//GEN-LAST:event_btnLoadActionPerformed

private void hypLinkDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypLinkDeleteActionPerformed
    udfDelete();
}//GEN-LAST:event_hypLinkDeleteActionPerformed

private void hypLinkEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypLinkEditActionPerformed
    udfNewKasBank();
}//GEN-LAST:event_hypLinkEditActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    udfPreview2();
}//GEN-LAST:event_jButton1ActionPerformed

private void udfPreview2() {
    if(masterTable.getSelectedRow()<0) return;
    String sNoBukti=masterTable.getValueAt(masterTable.getSelectedRow(), 0).toString();
//    String sSubReport=getClass().getResource("/Reports/").toString();
//    sSubReport=sSubReport.substring(0, 1).equalsIgnoreCase("/")? sSubReport.substring(1, sSubReport.length()): sSubReport;
//    sSubReport=sSubReport.replace("file:/", "");
    //System.out.println(sSubReport);
        try {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            HashMap reportParam = new HashMap();
            reportParam.put("no_bukti", sNoBukti);
            reportParam.put("logo", getClass().getResource("/akuntansi/resources/LogoKopegtel.JPG").toString());
            reportParam.put("SUBREPORT_DIR", getClass().getResource("/akuntansi/Reports/").toString());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/BuktiKas.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
            print.setOrientation(OrientationEnum.PORTRAIT);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                return;
            }
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            Logger.getLogger(FrmBuktiKas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoad;
    private org.jdesktop.swingx.JXHyperlink hypLinkDelete;
    private org.jdesktop.swingx.JXHyperlink hypLinkEdit;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private org.jdesktop.swingx.JXHyperlink lblUbah;
    private org.jdesktop.swingx.JXTable masterTable;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private void udfNew() {
//        FrmItemMaster fMaster=new FrmItemMaster();
//        fMaster.setTitle("Item baru");
//        fMaster.setConn(conn);
//        fMaster.setKodeBarang("XXXXXXXXXXXXXXXXXXXXXXXXXX");
//        fMaster.setIsNew(true);
//        fMaster.setSrcTable(masterTable);
//        fMaster.setSrcModel(myModel);
//        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        fMaster.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fMaster.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
        }

    private void udfSetSaldoAwal() {
        int i=masterTable.getSelectedRow();
        if(i>=0){
//            DlgSaldoAwal d1=new DlgSaldoAwal(JOptionPane.getFrameForComponent(this), false);
//            d1.setConn(conn);
//            d1.setNoAnggota(masterTable.getValueAt(i, 0).toString());
//            d1.setTitle("Saldo awal anggota : "+masterTable.getValueAt(i, 0).toString()+" - "+masterTable.getValueAt(i, 1).toString());
//            d1.setVisible(true);
        }
    }

    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
//            FrmItemMaster fMaster=new FrmItemMaster();
//            fMaster.setTitle("Update Item / Barang");
//            //fMaster.settDesktopPane(jDesktop);
//            fMaster.setConn(conn);
//            fMaster.setSrcModel(myModel);
//            fMaster.setSrcTable(masterTable);
//            fMaster.setKodeBarang(masterTable.getValueAt(iRow, 0).toString());
//            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//            jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//            fMaster.setVisible(true);
//            try{
//                //fMaster.setMaximum(true);
//                fMaster.setSelected(true);
//            } catch(PropertyVetoException PO){
//
//            }
            
        }
    }

    private void udfNewKasBank() {
        FrmBuktiKas f=new FrmBuktiKas();
        f.setConn(conn);
        f.setFlag(sFlag);
        f.setFormList(this);
        f.setVisible(true);
    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                value=GeneralFunction.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }
}
