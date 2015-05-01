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
import com.ustasoft.component.TextEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
//import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
//import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.decorator.HighlighterFactory;
//import org.syntax.jedit.tokenmarker.TeXTokenMarker;

/**
 *
 * @author ustadho
 */
public class FrmJournalEntry extends javax.swing.JFrame {
    private Connection conn;
    DefaultTableModel myModel;
    String sDate="";
    GeneralFunction fn=new GeneralFunction();
    private MyKeyListener kListener=new MyKeyListener();
    private boolean koreksi=false;
    private String sKode="";

    /** Creates new form FrmJournalEntry */
    public FrmJournalEntry() {
        initComponents();
        //tblAccount.setAutoStartEditOnKeyStroke(true);
        tblAccount.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
        tblAccount.getColumnModel().getColumn(2).setCellEditor(new MyTableCellEditor());
        tblAccount.getColumnModel().getColumn(3).setCellEditor(new MyTableCellEditor());
//        tblAccount.getColumnModel().getColumn(5).setCellEditor(new MyTableCellEditor());
//        tblAccount.getColumnModel().getColumn(6).setCellEditor(new MyTableCellEditor());
        tblAccount.getModel().addTableModelListener(new MyTableModelListener(tblAccount));

        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
//        tblAccount.addKeyListener(kListener);
        jXDatePicker1.setFormats("dd-MM-yyyy");
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    public void setKoreksi(boolean con){
        this.koreksi=con;
    }

    public void setKode(String str){
        this.sKode=str;
    }
    private void udfClear(){
        try {
            txtNoVoucher.setText("");
            txtCariVoucher.setText("");
            txtDesc.setText("");
            jXDatePicker1.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(sDate));

            myModel=(DefaultTableModel)tblAccount.getModel();
            myModel.setNumRows(0);
            myModel.setNumRows(300);
            tblAccount.setModel(myModel);

        } catch (ParseException ex) {
            Logger.getLogger(FrmJournalEntry.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void udfInitForm(){
        tblAccount.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        
        fn.setConn(conn);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date,'yyyy-MM-dd')");
            if(rs.next()){
                sDate=rs.getString(1);

            }
            rs.close();
            udfClear();

            if (koreksi) {
                txtCariVoucher.setText(sKode);
                udfLoadItem();
            }
        }catch(SQLException se){

        }

//        setUpSportColumn(tblAccount, tblAccount.getColumnModel().getColumn(0));
    }

    public void setUpSportColumn(JTable table,
                                 TableColumn sportColumn) {
        sportColumn.setCellEditor(new TextEditor(tblAccount, conn,"acc_no,acc_name ","acc_coa"));
    }

    private void udfSave() {
        String noJournal="";
        noJournal = txtNoVoucher.getText();
        if(GeneralFunction.udfGetDouble(lblDebit.getText())==0 && GeneralFunction.udfGetDouble(lblKredit.getText())==0){
            JOptionPane.showMessageDialog(this, "Jurnal masil nol", "Information", JOptionPane.INFORMATION_MESSAGE);
            tblAccount.requestFocus();
            return;
        }
        if(GeneralFunction.udfGetDouble(lblDebit.getText()) != GeneralFunction.udfGetDouble(lblKredit.getText())){
            JOptionPane.showMessageDialog(this, "Jumlah Debet harus sama dengan Kredit", "Information", JOptionPane.INFORMATION_MESSAGE);
            tblAccount.requestFocus();
            return;
        }
        try{
            //ResultSet rs=conn.createStatement().executeQuery("select fn_acc_get_journal_no('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', 'JU')");
            ResultSet rs=conn.createStatement().executeQuery("select * from acc_journal where upper(journal_no) ='"+ txtNoVoucher.getText().trim().toUpperCase() +"'");
            if(rs.next()) {
                JOptionPane.showMessageDialog(null, "No Voucher/Jurnal Sudah Ada!!!");
                if (JOptionPane.showConfirmDialog(null, "Apakah Anda Akan Update???", "Konfirmasi", JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION){
                    Integer Deldetail=conn.createStatement().executeUpdate("delete from acc_journal_detail where journal_no='"+ txtNoVoucher.getText() +"'" );
                    Integer Delheader=conn.createStatement().executeUpdate("delete from acc_journal where journal_no='"+ txtNoVoucher.getText() +"'" );

                }else{
                     noJournal = txtNoVoucher.getText();
                      return;
                }
               
            }
            rs.close();


            System.out.println("No. "+noJournal);

            String sInsH="insert into acc_journal(journal_no, tanggal, description, unit, tipe) " +
                    "values('"+noJournal+"', '"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"'," +
                    "'"+ txtDesc.getText()+"', '"+(cmbUnit.getSelectedIndex()==0? "SP": "RT")+"', 'JRE'); ";
            String sDet="";
            for(int i=0; i< tblAccount.getRowCount(); i++){
                if(tblAccount.getValueAt(i, 0)!=null ){
                    if (!tblAccount.getValueAt(i, 0).toString().trim().equalsIgnoreCase(""))
                      
                             sDet=sDet+  "Insert into acc_journal_detail(journal_no, acc_no, debit, credit, memo) " +
                             "select '"+noJournal+"', '"+tblAccount.getValueAt(i, 0).toString()+"', " +
                             GeneralFunction.udfGetDouble(tblAccount.getValueAt(i, 2)==null?"0":tblAccount.getValueAt(i, 2))+ ", " +
                             GeneralFunction.udfGetDouble(tblAccount.getValueAt(i, 3)==null?"0":tblAccount.getValueAt(i, 3))+ ", " +
                             "'"+(tblAccount.getValueAt(i, 4)==null? "":tblAccount.getValueAt(i, 4).toString())+"'; ";
                }



            }
            System.out.println(sInsH+sDet);
            int iIns=conn.createStatement().executeUpdate(sInsH+sDet);
            if (iIns==1)
            JOptionPane.showMessageDialog(this, "Simpan Sukses");
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
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
            dKredit=0;
            dDebet=0;
            for (int i=0; i<myModel.getRowCount(); i++){
                if(tblAccount.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Debit"))!=null)
                    dDebet+=GeneralFunction.udfGetDouble(myModel.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Debit")));
                if(tblAccount.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Credit"))!=null)
                    dKredit+=GeneralFunction.udfGetDouble(myModel.getValueAt(i, tblAccount.getColumnModel().getColumnIndex("Credit")));
            }
            lblDebit.setText(fn.dFmt.format(dDebet));
            lblKredit.setText(fn.dFmt.format(dKredit));
        }
    }

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);


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
        jLabel1 = new javax.swing.JLabel();
        txtNoVoucher = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDesc = new javax.swing.JTextArea();
        cmbUnit = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCariVoucher = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAccount = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblDebit = new javax.swing.JLabel();
        lblKredit = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

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

        jPanel1.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Description"); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 80, 20));

        txtNoVoucher.setName("txtNoVoucher"); // NOI18N
        jPanel1.add(txtNoVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 150, -1));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("Jurnal Entry"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 0, 230, 30));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 35, 110, -1));

        jLabel3.setText("Tanggal"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 80, 20));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtDesc.setColumns(20);
        txtDesc.setRows(5);
        txtDesc.setName("txtDesc"); // NOI18N
        jScrollPane1.setViewportView(txtDesc);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 670, 40));

        cmbUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Simpan Pinjam", "Retail" }));
        cmbUnit.setName("cmbUnit"); // NOI18N
        jPanel1.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 35, 170, -1));

        jLabel4.setText("No. Voucher"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        jLabel7.setText("Unit"); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 35, 30, 20));

        txtCariVoucher.setName("txtCariVoucher"); // NOI18N
        txtCariVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCariVoucherFocusLost(evt);
            }
        });
        txtCariVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariVoucherKeyPressed(evt);
            }
        });
        jPanel1.add(txtCariVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 120, -1));

        jLabel14.setText("Cari No Vucher"); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabel14PropertyChange(evt);
            }
        });
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 100, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 110));

        jScrollPane2.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Account No.", "Account Name", "Debit", "Credit", "Keterangan", "Rate", "Prime Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAccount.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblAccount.setCellSelectionEnabled(true);
        tblAccount.setName("tblAccount"); // NOI18N
        tblAccount.setSurrendersFocusOnKeystroke(true);
        tblAccount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblAccountKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblAccount);
        tblAccount.getColumnModel().getColumn(1).setPreferredWidth(250);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 128, 770, 300));

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
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 30, 70, -1));

        btnHapus.setText("Hapus"); // NOI18N
        btnHapus.setName("btnHapus"); // NOI18N
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 70, -1));

        jButton3.setText("Simpan"); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 70, -1));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Credit"); // NOI18N
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 160, 22));

        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 22));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Debit"); // NOI18N
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, 22));

        lblDebit.setBackground(new java.awt.Color(255, 255, 255));
        lblDebit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDebit.setText("0"); // NOI18N
        lblDebit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        lblDebit.setName("lblDebit"); // NOI18N
        lblDebit.setOpaque(true);
        jPanel2.add(lblDebit, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 150, 22));

        lblKredit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKredit.setText("0"); // NOI18N
        lblKredit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        lblKredit.setName("lblKredit"); // NOI18N
        lblKredit.setOpaque(true);
        jPanel2.add(lblKredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 160, 22));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("TOTAL"); // NOI18N
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true)));
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 22));

        jButton4.setText("Clear"); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, 70, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 770, 60));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-797)/2, (screenSize.height-534)/2, 797, 534);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (txtNoVoucher.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No Voucher atau No Journal Harus Diisi");
            return;           
        }
        udfSave();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
            try {
                if (JOptionPane.showConfirmDialog(null, "Apakah Anda Yakin akan Menghapus???", "Konfirmasi", JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION){
                    Integer Deldetail = conn.createStatement().executeUpdate("delete from acc_journal_detail where journal_no='" + txtNoVoucher.getText() + "'");
                    Integer Delheader=conn.createStatement().executeUpdate("delete from acc_journal where journal_no='"+ txtNoVoucher.getText() +"'" );
                    udfClear();
                }

            } catch (SQLException ex) {
                Logger.getLogger(FrmJournalEntry.class.getName()).log(Level.SEVERE, null, ex);
            }



}//GEN-LAST:event_btnHapusActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formWindowClosed

    private void txtCariVoucherFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCariVoucherFocusLost

}//GEN-LAST:event_txtCariVoucherFocusLost

    private void txtCariVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariVoucherKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            myModel.setRowCount(0);
            myModel.setRowCount(300);
            udfLoadItem();
        }
}//GEN-LAST:event_txtCariVoucherKeyPressed

    private void jLabel14PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabel14PropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_jLabel14PropertyChange

    private void tblAccountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblAccountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE){
           myModel.removeRow(tblAccount.getSelectedRow());
        }
    }//GEN-LAST:event_tblAccountKeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        udfClear();
    }//GEN-LAST:event_jButton4ActionPerformed

     public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmJournalEntry().setVisible(true);
            }
        });
    }
private void udfLoadItem(){

        try{
                String sQry="select journal_no, tanggal, description, multi_curr, unit, tipe, source_no from acc_journal" +
                        " where upper(journal_no)='"+ txtCariVoucher.getText().toUpperCase() +"' ";

//                System.out.println(sQry);
                ResultSet rs=conn.createStatement().executeQuery(sQry);
                int row=0;
                while(rs.next()){
                    //jLabel4.setText(txtCariVoucher.getText());
                    txtNoVoucher.setText(rs.getString("journal_no"));
                    jXDatePicker1.setDate(rs.getDate("tanggal"));
                    txtDesc.setText(rs.getString("description"));
                    cmbUnit.setSelectedItem(rs.getString("unit").equals("SP")?0:1);

                    row = row +1;

                   
//                    lblTerbilang.setText( udf dFmt.format(rs.getDouble("biaya_materai")));
                }

//                 System.out.println("Biaya : "+totBiaya );

                rs.close();

                if (row==0) {
                    JOptionPane.showMessageDialog(null, "Data Tidak Ditemukan");
                    return;
                }

                double totJmlDebet = 0;
                double totJmlCredit = 0;
                String sQry1="select journal_no, d.acc_no, acc_name, debit, credit, rate, memo, prime_amount from acc_journal_detail d left join acc_coa a using(acc_no) " +
                            " where journal_no ='"+ txtNoVoucher.getText() +"' order by serial_no";
//                 System.out.println(sQry1);
                 ResultSet rs1=conn.createStatement().executeQuery(sQry1);

                 myModel.setRowCount(0);
                 myModel.setRowCount(300);
                 int iRow=0;
                 while(rs1.next()){

                     myModel.setValueAt(rs1.getString("acc_no"), iRow, 0);
                     myModel.setValueAt(rs1.getString("acc_name"), iRow, 1);
                     myModel.setValueAt((rs1.getDouble("debit")), iRow, 2);
                     myModel.setValueAt((rs1.getDouble("credit")), iRow, 3);
                     myModel.setValueAt(rs1.getString("memo"), iRow, 4);
                     myModel.setValueAt((rs1.getDouble("rate")), iRow, 5);
                     myModel.setValueAt((rs1.getDouble("prime_amount")), iRow, 6);
                     iRow = iRow + 1;

                     totJmlDebet = totJmlDebet + (rs1.getDouble("debit"));
                     totJmlCredit = totJmlCredit + (rs1.getDouble("credit"));
                 }
                 rs1.close();

                 lblDebit.setText(fn.dFmt.format(totJmlDebet));
                 lblKredit.setText(fn.dFmt.format(totJmlCredit));
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblDebit;
    private javax.swing.JLabel lblKredit;
    private org.jdesktop.swingx.JXTable tblAccount;
    private javax.swing.JTextField txtCariVoucher;
    private javax.swing.JTextArea txtDesc;
    private javax.swing.JTextField txtNoVoucher;
    // End of variables declaration//GEN-END:variables

public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=new JTextField("");

        JLabel label =new JLabel("");

        int col, row;

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
                            fn.lookup(evt, new Object[]{label}, sQry, tblAccount.getColumn(1).getWidth()+tblAccount.getColumn(2).getWidth()+20, 200);
                                        
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
                //try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    //Number dVal = nf.parse(value.toString());
                    text.setText(GeneralFunction.dFmt.format(value));


//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
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
                    o=(retVal);

                    if((col==2||col==3) && (Double)retVal>0) tblAccount.setValueAt(0, row, (col==2? 3:2));

//                    //udfSetSubTotal(row);
//                    myModel.setValueAt( GeneralFunction.udfGetDouble(((JxtgetValueAt(row, tblAccount.getColumnModel().getColumnIndex("Harga")).toString()),
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


    public class MyKeyListener extends KeyAdapter {
        double sisa=0;

        @Override
         public void keyTyped(KeyEvent e) {
//            if(!e.getSource().equals(txtNoAnggota))
//                keyNumeric(e);

        }
        @Override
        public void keyReleased(KeyEvent e) {
          //if(e.getSource().equals(txtAngsuran))

        }
        @Override
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
//                if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
//                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            c.requestFocus();
                        }else
                            fn.lstRequestFocus();

                        break;
//                }else{
//                    if(lst.isVisible()) lst.requestFocus();
//                }
            }
            case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
            }
            case KeyEvent.VK_INSERT: {  //Bayar
                tblAccount.requestFocusInWindow();
                tblAccount.requestFocus();
                tblAccount.changeSelection(0, 0, false, false);
                break;
            }

            case KeyEvent.VK_ESCAPE: {
                dispose();
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

//    private TableCellEditor createExampleEditor() {
//        JTextField combo = new JTextField() {
//            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
//                    int condition, boolean pressed) {
//                boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
//
////                if (!retValue && isStartingCellEdit() && editor != null) {
////                    // this is where the magic happens
////                    // not quite right; sets the value, but doesn't advance the
////                    // cursor position for AC
////                    editor.setItem(String.valueOf(ks.getKeyChar()));
////                }
//
//                return retValue;
//            }
//
//            private boolean isStartingCellEdit() {
//                JTable table = (JTable) SwingUtilities.getAncestorOfClass(
//                        JTable.class, this);
//
//                return table != null
//                        && table.isFocusOwner()
//                        && !Boolean.FALSE.equals((Boolean) table
//                                .getClientProperty("JTable.autoStartsEdit"));
//            }
//        };
//        //AutoCompleteDecorator.decorate(combo);
//
//        return new JXTable.NumberEditor();
//    }

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
