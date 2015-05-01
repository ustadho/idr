/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmJournalEntry.java
 *
 * Created on Apr 3, 2009, 4:45:27 PM
 */

package pos.akuntansi;

import com.ustasoft.component.GeneralFunction;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.JXTable;
import pos.MainForm;

/**
 *
 * @author ustadho
 */
public class FrmBuktiKas extends javax.swing.JFrame {
    private Connection conn;
    DefaultTableModel myModel;
    String sDate="";
    private String sFlag;
    private MyKeyListener kListener=new MyKeyListener();
    private boolean bSaved=false;
    private boolean koreksi=false;
    private String sNoBukti="", sOldBukti="";
    private FrmHistoriKasBank frmListKasBank;
    GeneralFunction fn=new GeneralFunction();

    /** Creates new form FrmJournalEntry */
    public FrmBuktiKas() {
        initComponents();
        tblAccount.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
        tblAccount.getColumnModel().getColumn(2).setCellEditor(new MyNumberCellEditor());
        
        tblAccount.getModel().addTableModelListener(new MyTableModelListener(tblAccount));
        txtDepositTo.addKeyListener(kListener);
        txtDepositTo.addFocusListener(txtFoculListener);
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        for(int i=0;i<jPanel3.getComponentCount();i++){
            Component c = jPanel3.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        //tblAccount.addKeyListener(kListener);
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }

        jXDatePicker1.setFormats("dd-MM-yyyy");
        jDateCheque.setFormats("dd-MM-yyyy");
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    public void setKoreksi(boolean con){
        this.koreksi=con;
    }

    public void setKode(String str){
        this.sNoBukti=str;
        lblNoBukti.setText(str);
        //txtCariVoucher.setText(str);
    }

    public void setFlag(String sFlag) {
        this.sFlag=sFlag;

        lblBuktiKas.setText("Bukti Kas/ Bank "+(sFlag.equalsIgnoreCase("K")? "Keluar": "Masuk"));
        jLabel5.setText(sFlag.equalsIgnoreCase("K")? "Paid From": "Deposit To");
        jPanel3.setVisible(sFlag.equalsIgnoreCase("K"));
        setTitle(lblBuktiKas.getText());
    }

    private void udfClear(){
        try {
            bSaved=false;
            lblNoBukti.setText("");
            txtDepositTo.setText("");
            lblDepositTo.setText("");
            txtNoVoucher.setText("");
            txtDesc.setText("");
            jXDatePicker1.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(sDate));
            txtAmount.setText("0");
            lblTotal.setText("0");
            lblTerbilang.setText("Nol");
            txtRate.setText("1");
            btnPreview.setEnabled(bSaved);

            myModel=(DefaultTableModel)tblAccount.getModel();
            myModel.setNumRows(0);
            myModel.setNumRows(50);
            tblAccount.setModel(myModel);
            //myModel.addRow(new Object[]{"", "", 0, ""});
        } catch (ParseException ex) {
            Logger.getLogger(FrmBuktiKas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void udfInitForm(){
        //tblAccount.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        fn.setConn(conn);
        lblOleh1.setText(sFlag.equalsIgnoreCase("M")? "Disetor Oleh" : "Diterima Oleh");
        lblOleh2.setText(sFlag.equalsIgnoreCase("M")? "Diterima Oleh" : "Disetor Oleh");
        txtOleh2.setText(MainForm.sUserName);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date,'yyyy-MM-dd')");
            if(rs.next()){
                sDate=rs.getString(1);

            }
            rs.close();
            if(sNoBukti.length()==0) udfClear();

            if (koreksi) {
                //lblNoBukti.setText(sNoBukti);
                udfLoadBK();
            }
        }catch(SQLException se){

        }
    }

    private void udfPreview() {
        try {
            HashMap reportParam = new HashMap();
            reportParam.put("nama_koperasi", MainForm.sToko);
            reportParam.put("alamat", MainForm.sAlamat1);
            reportParam.put("telp", MainForm.sTelp1);
            reportParam.put("no_voucher", txtNoVoucher.getText());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/"+
                    (sFlag.equalsIgnoreCase("K")? "BuktiKasKeluar":"")+".jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
            print.setOrientation(jasperReport.getOrientationValue());
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                return;
            }
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            Logger.getLogger(FrmBuktiKas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void udfPreview2() {
    String sNoBukti=lblNoBukti.getText();
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

    private void udfSave()  {
        if(txtDepositTo.getText().trim().equalsIgnoreCase("")||lblDepositTo.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isikan Akun yang akan dideposit terlebih dulu!");
            txtDepositTo.requestFocus();
            return;
        }
        if(GeneralFunction.udfGetDouble(lblTotal.getText())==0 ){
            JOptionPane.showMessageDialog(this, "Detail Account masil nol", "Information", JOptionPane.INFORMATION_MESSAGE);
            tblAccount.requestFocus();
            return;
        }
        if(GeneralFunction.udfGetDouble(lblTotal.getText()) != GeneralFunction.udfGetDouble(txtAmount.getText())){
            JOptionPane.showMessageDialog(this, "Jumlah Debet harus sama dengan Kredit", "Information", JOptionPane.INFORMATION_MESSAGE);
            tblAccount.requestFocus();
            return;
        }
        if(txtNoVoucher.getText().trim().length()==0){
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk mengosongo No. Voucher?", "Confirmation", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                txtNoVoucher.requestFocus();
                return;
            }
        }
        try{
//               ResultSet rsBukti=conn.createStatement().executeQuery("select * from acc_bukti_kas where no_bukti='"+ txtNoVoucher.getText() +"'" );
//               if(!rsBukti.next()){
//                    ResultSet rs=conn.createStatement().executeQuery("select fn_acc_get_bukti_kas_no('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', " +
//                            "'"+sFlag+"')");
//                    if(rs.next()) txtNoVoucher.setText(rs.getString(1));
//                    rs.close();
//               }else{
//                    Integer Deldetail=conn.createStatement().executeUpdate("delete from acc_bukti_kas_detail where no_bukti='"+ txtNoVoucher.getText() +"'" );
//                    Integer Delheader=conn.createStatement().executeUpdate("delete from acc_bukti_kas where no_bukti='"+ txtNoVoucher.getText() +"'" );
//               }
//            rsBukti.close();
            
             ResultSet rsBukti=conn.createStatement().executeQuery("select * from acc_bukti_kas where no_bukti='"+ lblNoBukti.getText() +"'" );
               if(!rsBukti.next()){
                    ResultSet rs=conn.createStatement().executeQuery("select fn_acc_get_bukti_kas_no('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', " +
                            "'"+sFlag+"')");
                    if(rs.next()) lblNoBukti.setText(rs.getString(1));
                    rs.close();
               }else{
                    Integer Deldetail=conn.createStatement().executeUpdate("delete from acc_bukti_kas_detail where no_bukti='"+ lblNoBukti.getText() +"'" );
                    Integer Delheader=conn.createStatement().executeUpdate("delete from acc_bukti_kas where no_bukti='"+ lblNoBukti.getText() +"'" );
               }
            rsBukti.close();

            conn.setAutoCommit(false);
            String sInsH="insert into acc_bukti_kas(no_bukti, acc_no, rate, tanggal, memo, amount, flag, no_cek, " +
                    "unit, tgl_cek, payee, no_voucher, tipe, diterima_oleh, dibayar_oleh, diketahui_oleh) " +
                    "values('"+lblNoBukti.getText()+"', '"+txtDepositTo.getText()+"', "+GeneralFunction.udfGetDouble(txtRate.getText())+", " +
                    "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"'," +
                    "'"+txtDesc.getText()+"', "+GeneralFunction.udfGetDouble(txtAmount.getText())+" , '"+sFlag+"', " +
                    (jPanel3.isVisible()? "'"+txtNoCek.getText()+"'": "''") +"," +
                    "'"+(cmbUnit.getSelectedIndex()==0? "SP": "RT")+"'," +
                    (jPanel3.isVisible()? "'"+new SimpleDateFormat("yyyy-MM-dd").format(jDateCheque.getDate())+"'": "null") +"," +
                    "'', '"+ txtNoVoucher.getText() +"', " +
                    "'BK"+sFlag+"', " +
                    "'"+(sFlag.equalsIgnoreCase("M")? txtOleh2.getText(): txtOleh1.getText())+"', " +
                    "'"+(sFlag.equalsIgnoreCase("M")? txtOleh1.getText(): txtOleh2.getText())+"', " +
                    "'"+txtDiketahuiOleh.getText()+"'); ";

            String sDet="";
            for(int i=0; i< tblAccount.getRowCount(); i++){
                if(tblAccount.getValueAt(i, 0)!=null ){
                    if (!tblAccount.getValueAt(i, 0).toString().trim().equalsIgnoreCase(""))
                             sDet=sDet+  "Insert into acc_bukti_kas_detail(no_bukti, acc_no, amount, memo) " +
                             "select '"+lblNoBukti.getText()+"', '"+tblAccount.getValueAt(i, 0).toString()+"', " +
                             GeneralFunction.udfGetDouble(tblAccount.getValueAt(i, 2).toString())+ ", " +
                             "'"+ (tblAccount.getValueAt(i, 3)==null ? "": tblAccount.getValueAt(i, 3).toString())+"'; ";
                }
            }

            System.out.println(sInsH+sDet);
            int iIns=conn.createStatement().executeUpdate(sInsH+sDet);
            conn.setAutoCommit(true);

            if(frmListKasBank!=null) frmListKasBank.udfFilter(lblNoBukti.getText());
            JOptionPane.showMessageDialog(this, "Simpan Bukti Kas "+(sFlag.equalsIgnoreCase("M")? "Masuk": "Keluar")+" Sukses");
            bSaved=true;
            btnPreview.setEnabled(bSaved);
            //udfClear();

        }catch(SQLException se){
            try {
                JOptionPane.showMessageDialog(this, se.getMessage());
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(FrmBuktiKas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void setFormList(FrmHistoriKasBank aThis) {
        frmListKasBank=aThis;
    }

    public class MyTableModelListener implements TableModelListener {
        JTable table;
        double dKredit=0, dDebet=0;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        MyTableModelListener(JTable table) {
            this.table = table;
        }

        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();

            int mColIndex = e.getColumn();

            double dTotal=0;
            //if(mColIndex==3){
                for (int i=0; i<myModel.getRowCount(); i++){
                    if(tblAccount.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Amount"))!=null) 
                        dTotal+=GeneralFunction.udfGetDouble(myModel.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Amount")));
                }
                lblTotal.setText(GeneralFunction.dFmt.format(dTotal));
                txtAmount.setText(lblTotal.getText());
                String sTerbilang="";
                try{
                    ResultSet rs=conn.createStatement().executeQuery("select uang("+dTotal+"::bigint)");
                    if(rs.next()){
                        sTerbilang=rs.getString(1).trim();
                        if(sTerbilang.length()>1)
                            lblTerbilang.setText(sTerbilang.substring(0, 1)+ sTerbilang.substring(1, sTerbilang.length()).toLowerCase()+" rupiah");
                        else
                            lblTerbilang.setText("Nol rupiah");
                    }
                    rs.close();

                }catch(SQLException se){
                    JOptionPane.showMessageDialog(FrmBuktiKas.this, "Model Listener"+se.getMessage());
                }
            //}
        }
    }

    public class MyKeyListener extends KeyAdapter {
        double sisa=0;

         public void keyTyped(KeyEvent e) {
            if(e.getSource().equals(txtAmount))
                GeneralFunction.keyTyped(e);

        }
        public void keyReleased(KeyEvent e) {
          //if(e.getSource().equals(txtAngsuran))


        }
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_ENTER : {

                    break;
                }

            case KeyEvent.VK_F5: {  //Bayar
                udfSave();
                break;
            }
            case KeyEvent.VK_DOWN: {  //Bayar
                Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                if((ct.getClass().getSimpleName().equalsIgnoreCase("JXTABLE"))||(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")) )
                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            c.requestFocus();
                        }else
                            evt.consume();
                            fn.lstRequestFocus();

                        
                }else{
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            c.requestFocus();
                        }else
                            evt.consume();
                            fn.lstRequestFocus();

                }
                break;
            }
            case KeyEvent.VK_ESCAPE: {
                dispose();
                break;
            }
          }
    }



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

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);

           if(c.equals(txtAmount) || c.equals(ustTextField) ){
                ((JTextField)e.getSource()).setSelectionStart(0);
               ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
            }

           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            //c.setForeground(fHitam);
        }
   };
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTerbilang = new javax.swing.JLabel();
        txtNoVoucher = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDesc = new javax.swing.JTextArea();
        cmbUnit = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        lblBuktiKas = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtRate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        chkVoidCheque = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jDateCheque = new org.jdesktop.swingx.JXDatePicker();
        jLabel13 = new javax.swing.JLabel();
        txtNoCek = new javax.swing.JTextField();
        lblOleh1 = new javax.swing.JLabel();
        txtOleh1 = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblOleh2 = new javax.swing.JLabel();
        txtOleh2 = new javax.swing.JTextField();
        lblOleh3 = new javax.swing.JLabel();
        txtDiketahuiOleh = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDepositTo = new javax.swing.JTextField();
        lblDepositTo = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAccount = new javax.swing.JTable();
        lblNoBukti = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTerbilang.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTerbilang.setName("lblTerbilang"); // NOI18N
        jPanel1.add(lblTerbilang, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 135, 510, 30));

        txtNoVoucher.setName("txtNoVoucher"); // NOI18N
        txtNoVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoVoucherKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 110, -1));

        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 60, 20));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 35, 110, -1));

        jLabel3.setText("Tanggal"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 80, 20));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtDesc.setColumns(20);
        txtDesc.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtDesc.setRows(5);
        txtDesc.setName("txtDesc"); // NOI18N
        jScrollPane1.setViewportView(txtDesc);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 85, 370, 40));

        cmbUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Simpan Pinjam", "Retail" }));
        cmbUnit.setName("cmbUnit"); // NOI18N
        jPanel1.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 35, 170, -1));

        jLabel4.setText("No. Voucher"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel4PropertyChange(evt);
            }
        });
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        lblBuktiKas.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblBuktiKas.setText("Bukti Kas"); // NOI18N
        lblBuktiKas.setName("lblBuktiKas"); // NOI18N
        jPanel1.add(lblBuktiKas, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 250, 30));

        jLabel6.setText("Rate"); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel6PropertyChange(evt);
            }
        });
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 60, 20));

        txtRate.setEditable(false);
        txtRate.setName("txtRate"); // NOI18N
        txtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRateKeyReleased(evt);
            }
        });
        jPanel1.add(txtRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 20, -1));

        jLabel7.setText("Description"); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 80, 20));

        txtAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmount.setName("txtAmount"); // NOI18N
        txtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAmountKeyReleased(evt);
            }
        });
        jPanel1.add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 120, 22));

        jLabel8.setText("Amount :"); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 50, 20));

        jLabel9.setName("jLabel9"); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 80, 20));

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        chkVoidCheque.setText("Void Cheque"); // NOI18N
        chkVoidCheque.setName("chkVoidCheque"); // NOI18N
        chkVoidCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVoidChequeActionPerformed(evt);
            }
        });
        jPanel3.add(chkVoidCheque, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 150, 20));

        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel11PropertyChange(evt);
            }
        });
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 80, 20));

        jDateCheque.setName("jDateCheque"); // NOI18N
        jPanel3.add(jDateCheque, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 45, 120, -1));

        jLabel13.setText("No. Cek"); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel13PropertyChange(evt);
            }
        });
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, 20));

        txtNoCek.setName("txtNoCek"); // NOI18N
        txtNoCek.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoCekKeyReleased(evt);
            }
        });
        jPanel3.add(txtNoCek, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 180, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, 280, 70));

        lblOleh1.setText("Diterima Oleh"); // NOI18N
        lblOleh1.setName("lblOleh1"); // NOI18N
        lblOleh1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblOleh1PropertyChange(evt);
            }
        });
        jPanel1.add(lblOleh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 80, 20));

        txtOleh1.setName("txtOleh1"); // NOI18N
        txtOleh1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOleh1KeyReleased(evt);
            }
        });
        jPanel1.add(txtOleh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 370, -1));

        lblTotal.setBackground(new java.awt.Color(255, 255, 255));
        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        lblTotal.setName("lblTotal"); // NOI18N
        lblTotal.setOpaque(true);
        jPanel1.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 140, 110, 22));

        jLabel10.setText("TOTAL :"); // NOI18N
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, 50, 22));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 770, 170));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setText("Close"); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 70, 30));

        jButton1.setText("Clear"); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 80, 30));

        jButton3.setText("Simpan"); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 80, 30));

        btnPreview.setText("Preview"); // NOI18N
        btnPreview.setEnabled(false);
        btnPreview.setName("btnPreview"); // NOI18N
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jPanel2.add(btnPreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 100, 30));

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblOleh2.setText("Kasir"); // NOI18N
        lblOleh2.setName("lblOleh2"); // NOI18N
        lblOleh2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblOleh2PropertyChange(evt);
            }
        });
        jPanel4.add(lblOleh2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 100, 20));

        txtOleh2.setName("txtOleh2"); // NOI18N
        txtOleh2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOleh2KeyReleased(evt);
            }
        });
        jPanel4.add(txtOleh2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 250, -1));

        lblOleh3.setText("Diketahui Oleh"); // NOI18N
        lblOleh3.setName("lblOleh3"); // NOI18N
        lblOleh3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblOleh3PropertyChange(evt);
            }
        });
        jPanel4.add(lblOleh3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, 100, 20));

        txtDiketahuiOleh.setName("txtDiketahuiOleh"); // NOI18N
        txtDiketahuiOleh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiketahuiOlehKeyReleased(evt);
            }
        });
        jPanel4.add(txtDiketahuiOleh, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 25, 250, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 770, 60));

        jLabel5.setText("Deposit To"); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel5PropertyChange(evt);
            }
        });
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 20));

        txtDepositTo.setName("txtDepositTo"); // NOI18N
        txtDepositTo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDepositToFocusLost(evt);
            }
        });
        txtDepositTo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDepositToKeyReleased(evt);
            }
        });
        getContentPane().add(txtDepositTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 80, 21));

        lblDepositTo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        lblDepositTo.setName("lblDepositTo"); // NOI18N
        getContentPane().add(lblDepositTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 450, 21));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Account No.", "Account Name", "Amount", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAccount.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblAccount.setName("tblAccount"); // NOI18N
        tblAccount.getTableHeader().setReorderingAllowed(false);
        tblAccount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblAccountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblAccountKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(tblAccount);
        tblAccount.getColumnModel().getColumn(1).setPreferredWidth(350);
        tblAccount.getColumnModel().getColumn(3).setPreferredWidth(120);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 217, 770, 160));

        lblNoBukti.setName("lblNoBukti"); // NOI18N
        getContentPane().add(lblNoBukti, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 120, 20));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-800)/2, (screenSize.height-481)/2, 800, 481);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void txtNoVoucherKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoVoucherKeyReleased
        //jLabel4.setText(txtNoVoucher.getText());
    }//GEN-LAST:event_txtNoVoucherKeyReleased

    private void jLabel4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel4PropertyChange
//        lblTerbilang.setText(jLabel4.getText());
    }//GEN-LAST:event_jLabel4PropertyChange

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel5PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel5PropertyChange

    private void txtDepositToFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepositToFocusLost
        //        if(!lst.isVisible() && !txtNoAnggota.getText().equalsIgnoreCase("") && isNew)
        //        if(!lst.isVisible() && !txtNoAnggota.getText().equalsIgnoreCase("") && isNew)
        //            txtPinjamanKe.setText(dFmt.format(getPinjamanKe()));
}//GEN-LAST:event_txtDepositToFocusLost

    private void txtDepositToKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepositToKeyReleased
        String sQry = "select acc_no, coalesce(acc_name,'') as acc_name " +
                    "from acc_coa where acc_type='01' " +
                    "and acc_no not in(select distinct sub_acc_of from acc_coa where acc_type='01' and sub_acc_of is not null) " +
                    "and acc_no||coalesce(acc_name,'') " +
                    "iLike '%" + txtDepositTo.getText() + "%' order by acc_no ";
        fn.lookup(evt, new Object[]{lblDepositTo}, sQry, txtDepositTo.getWidth()+lblDepositTo.getWidth()+18, 150);
}//GEN-LAST:event_txtDepositToKeyReleased

    private void jLabel6PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel6PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel6PropertyChange

    private void txtRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRateKeyReleased

    private void txtAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAmountKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtAmountKeyReleased

    private void jLabel11PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel11PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel11PropertyChange

    private void jLabel13PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel13PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13PropertyChange

    private void txtNoCekKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoCekKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoCekKeyReleased

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        udfPreview2();
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfClear();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formWindowClosed

    private void lblOleh1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblOleh1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblOleh1PropertyChange

    private void txtOleh1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOleh1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOleh1KeyReleased

    private void lblOleh2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblOleh2PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblOleh2PropertyChange

    private void txtOleh2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOleh2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOleh2KeyReleased

    private void lblOleh3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblOleh3PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblOleh3PropertyChange

    private void txtDiketahuiOlehKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiketahuiOlehKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiketahuiOlehKeyReleased

    private void tblAccountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblAccountKeyPressed
        
    }//GEN-LAST:event_tblAccountKeyPressed

    private void tblAccountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblAccountKeyTyped
       if(fn.isListVisible() && evt.getKeyCode()==KeyEvent.VK_DOWN ){
            evt.consume();
        }
    }//GEN-LAST:event_tblAccountKeyTyped

    private void chkVoidChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVoidChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkVoidChequeActionPerformed

     public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                       new FrmBuktiKas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPreview;
    private javax.swing.JCheckBox chkVoidCheque;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private org.jdesktop.swingx.JXDatePicker jDateCheque;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblBuktiKas;
    private javax.swing.JLabel lblDepositTo;
    private javax.swing.JLabel lblNoBukti;
    private javax.swing.JLabel lblOleh1;
    private javax.swing.JLabel lblOleh2;
    private javax.swing.JLabel lblOleh3;
    private javax.swing.JLabel lblTerbilang;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblAccount;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtDepositTo;
    private javax.swing.JTextArea txtDesc;
    private javax.swing.JTextField txtDiketahuiOleh;
    private javax.swing.JTextField txtNoCek;
    private javax.swing.JTextField txtNoVoucher;
    private javax.swing.JTextField txtOleh1;
    private javax.swing.JTextField txtOleh2;
    private javax.swing.JTextField txtRate;
    // End of variables declaration//GEN-END:variables
    private NumberFormat numFormat=GeneralFunction.dFmt;
    final JTextField ustTextField = new JTextField() {
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
     public class MyNumberCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
//        JTextField text=new JTextField("");
//        ustTextField
        int col, row;

        private NumberFormat  nf=NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //ustTextField.setEditable(isCellCanEdit);
           //ustTextField.setEnabled(btnGenerate.getText().equalsIgnoreCase("Generate"));
           col=vColIndex;
           row=rowIndex;
           ustTextField.setBackground(new Color(0,255,204));
           ustTextField.addFocusListener(txtFoculListener);
           //if(col==jTable1.getColumnModel().getColumnIndex("Minus")||col==jTable1.getColumnModel().getColumnIndex("Ditagihkan")){
                ustTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                      if (col!=0) {
                          char c = evt.getKeyChar();
                          if (!((c >= '0' && c <= '9')) &&
                                (c != KeyEvent.VK_BACK_SPACE) &&
                                (c != KeyEvent.VK_DELETE) &&
                                (c != KeyEvent.VK_ENTER)) {
                                getToolkit().beep();
                                evt.consume();
                                return;
                          }
                       }
                    }
                });
                ustTextField.addFocusListener(txtFoculListener);
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            ustTextField.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                //try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    //Number dVal = nf.parse(value.toString());
                    ustTextField.setText(nf.format((Double)value));


//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }else
                ustTextField.setText(value==null? "":value.toString());
           return ustTextField;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                return GeneralFunction.udfGetDouble(ustTextField.getText());
                //return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=new JTextField("");

        JLabel label =new JLabel("");

        int col, row;

        private NumberFormat  nf=NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           label.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    myModel.setValueAt(label.getText(), row, 1);
                }
            });

           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           if(col==0||col==2||col==3||col==5||col==6){
                text.addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                      if (col!=0) {
                          char c = evt.getKeyChar();
                          if (!((c >= '0' && c <= '9')) &&
                                (c != KeyEvent.VK_BACK_SPACE) &&
                                (c != KeyEvent.VK_DELETE) &&
                                (c != KeyEvent.VK_ENTER)) {
                                getToolkit().beep();
                                evt.consume();
                                return;
                          }
                       }
                    }
                    @Override
                   public void keyPressed(java.awt.event.KeyEvent evt){
                        if (fn.isListVisible() && evt.getKeyCode()==KeyEvent.VK_DOWN) {
                            fn.lstRequestFocus();
                        }
                   }
                    @Override
                   public void keyReleased(java.awt.event.KeyEvent evt) {

                        if(col==0){
                            String sQry = "select a.acc_no, a.acc_name " +
                                                "from acc_coa a " +
                                                "where acc_no not in(select distinct sub_acc_of from acc_coa where sub_acc_of is not null) " +
                                                "and (a.acc_no||coalesce(a.acc_name,'')) iLike '%" + ((JTextField)evt.getSource()).getText() + "%' " +
                                                "order by acc_no ";
                            fn.lookup(evt, null, sQry, 100, 100);
                        }
                   }
                });
               }
//           }


           //component.selectAll();
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    Number dVal = nf.parse(value.toString());
                    text.setText(nf.format(dVal));


                } catch (java.text.ParseException ex) {
                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            if(col==2||col==3){
                try {
                    //retVal = Integer.parseInt(((JTextField)component).getText().replace(",",""));
                    retVal = GeneralFunction.udfGetDouble(((JTextField)text).getText());
                    o=nf.format(retVal);

                    if((col==2||col==3) && (Double)retVal>0) tblAccount.setValueAt(0, row, (col==2? 3:2));

//                    //udfSetSubTotal(row);
//                    myModel.setValueAt( GeneralFunction.udfGetDouble(((JTextField)component).getText()) *
//                        GeneralFunction.udfGetDouble(myModel.getValueAt(row, tblAccount.getColumnModel().getColumnIndex("Harga")).toString()),
//                        row, tblAccount.getColumnModel().getColumnIndex("Sub Total"));

                    return o;
                } catch (Exception e) {
                    toolkit.beep();
                    retVal=0;
                }
            }else{
                retVal=(Object)text.getText();
                //JOptionPane.showMessageDialog(null, "Kode Barang : "+retVal.toString());

                if(retVal.toString().trim().length()>0){
                    //retVal=udfSetBarang(retVal.toString(), row);
                }
//                  System.out.println("Masuk1");
            }
            return retVal;
        }

    }


    private TableCellEditor createExampleEditor() {
        JTextField combo = new JTextField() {
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                    int condition, boolean pressed) {
                boolean retValue = super.processKeyBinding(ks, e, condition, pressed);

//                if (!retValue && isStartingCellEdit() && editor != null) {
//                    // this is where the magic happens
//                    // not quite right; sets the value, but doesn't advance the
//                    // cursor position for AC
//                    editor.setItem(String.valueOf(ks.getKeyChar()));
//                }

                return retValue;
            }

            private boolean isStartingCellEdit() {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(
                        JTable.class, this);

                return table != null
                        && table.isFocusOwner()
                        && !Boolean.FALSE.equals((Boolean) table
                                .getClientProperty("JTable.autoStartsEdit"));
            }
        };
        //AutoCompleteDecorator.decorate(combo);

        return new JXTable.NumberEditor();
    }

    private void udfLoadBK(){
        try{
                String sQry="select no_bukti, b.acc_no, acc_name, rate, tanggal, " +
                        "coalesce(memo,'') as memo, coalesce(amount,0) as amount, coalesce(flag, '') as flag, " +
                        "coalesce(batal, false), coalesce(no_cek,'') as no_cek, " +
                        "coalesce(unit,'') as unit, tgl_cek, payee, coalesce(no_voucher,'') as no_voucher," +
                        "coalesce(diketahui_oleh, '') as diketahui_oleh, coalesce(diterima_oleh,'') as diterima_oleh, " +
                        "coalesce(dibayar_oleh,'') as dibayar_oleh " +
                        "from acc_bukti_kas b left join acc_coa a on b.acc_no = a.acc_no " +
                        "where no_bukti='"+ lblNoBukti.getText().toUpperCase() +"' ";

                bSaved=true;
                System.out.println(sQry);
                ResultSet rs=conn.createStatement().executeQuery(sQry);
                int row=0;
                while(rs.next()){
                    //jLabel4.setText(txtCariVoucher.getText());
                    txtNoVoucher.setText(rs.getString("no_voucher"));
                    txtRate.setText(String.valueOf(rs.getDouble("rate")));
                    txtDepositTo.setText(rs.getString("acc_no"));
                    lblDepositTo.setText(rs.getString("acc_name"));
                    jXDatePicker1.setDate(rs.getDate("tanggal"));
                    txtDesc.setText(rs.getString("memo"));
                    txtAmount.setText(GeneralFunction.dFmt.format(rs.getDouble("amount")));
                    jDateCheque.setDate(rs.getDate("tgl_cek"));
                    cmbUnit.setSelectedItem(rs.getString("unit").equals("SP")?0:1);
                    sFlag=rs.getString("flag");
                    txtNoCek.setText(rs.getString("no_cek"));
                    txtDiketahuiOleh.setText(rs.getString("diketahui_oleh"));
                    txtOleh1.setText(sFlag.equalsIgnoreCase("M")? rs.getString("dibayar_oleh") : rs.getString("diterima_oleh"));
                    txtOleh2.setText(sFlag.equalsIgnoreCase("K")? rs.getString("dibayar_oleh") : rs.getString("diterima_oleh"));
                    chkVoidCheque.setSelected(rs.getDate("tgl_cek")==null?false:true);
                    btnPreview.setEnabled(true);
                    row = row +1;

                    String sTerbilang="";
                    try{
                        ResultSet rs1=conn.createStatement().executeQuery("select uang("+rs.getDouble("amount")+"::bigint)");
                        if(rs1.next()){
                            sTerbilang=rs1.getString(1).trim();
                            if(sTerbilang.length()>1)
                                lblTerbilang.setText(sTerbilang.substring(0, 1)+ sTerbilang.substring(1, sTerbilang.length()).toLowerCase()+" rupiah");
                            else
                                lblTerbilang.setText("Nol rupiah");
                        }
                        rs1.close();

                    }catch(SQLException se){
                        JOptionPane.showMessageDialog(FrmBuktiKas.this, "Model Listener"+se.getMessage());
                    }

//                    lblTerbilang.setText( udf dFmt.format(rs.getDouble("biaya_materai")));
                }

//                 System.out.println("Biaya : "+totBiaya );

                rs.close();

                if (row==0) return;

                double totJml = 0;
                String sQry1="select no_bukti, d.acc_no, acc_name, amount, coalesce(memo,'') as memo, kode_dept, kode_project, serial_no, source_no, tipe " +
                            " from acc_bukti_kas_detail d left join acc_coa a on d.acc_no = a.acc_no   " +
                            " where no_bukti ='"+ lblNoBukti.getText() +"'";
                 System.out.println(sQry1);
                 ResultSet rs1=conn.createStatement().executeQuery(sQry1);
                 myModel=(DefaultTableModel)tblAccount.getModel();
                 myModel.setRowCount(0);
                 myModel.setRowCount(50);
                 int iRow=0;
                 while(rs1.next()){

                     myModel.setValueAt(rs1.getString("acc_no"), iRow, 0);
                     myModel.setValueAt(rs1.getString("acc_name"), iRow, 1);
                     myModel.setValueAt(rs1.getDouble("amount"), iRow, 2);
                     myModel.setValueAt(rs1.getString("memo"), iRow, 3);
                     iRow = iRow + 1;

                     totJml = totJml + (rs1.getDouble("amount"));
                 }
                 rs1.close();

                 lblTotal.setText(GeneralFunction.dFmt.format(totJml));
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }


    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
