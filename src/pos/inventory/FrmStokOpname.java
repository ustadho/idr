/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pos.inventory;

import com.ustasoft.component.DlgLookup;
import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.dao.jdbc.AccCoaDao;
import com.ustasoft.pos.dao.jdbc.GudangDao;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.dao.jdbc.OpnameDao;
import com.ustasoft.pos.domain.Gudang;
import com.ustasoft.pos.domain.Opname;
import com.ustasoft.pos.domain.OpnameDetail;
import com.ustasoft.pos.domain.view.AccCoaView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.SwingConstants;
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
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmStokOpname extends javax.swing.JInternalFrame {
    private Connection conn;
    MyKeyListener kListener=new MyKeyListener();
    List<Gudang> listGudang=new ArrayList<Gudang>();
    List<AccCoaView> listCoa=new ArrayList<AccCoaView>();
    ItemDao itemDao;
    private OpnameDao opnameDao;
    /**
     * Creates new form FrmSO
     */
    public FrmStokOpname() {
        initComponents();
        chkValueAdjItemStateChanged(null);
        tblDetail.getColumn("IdBarang").setMinWidth(0);
        tblDetail.getColumn("IdBarang").setMaxWidth(0);
        tblDetail.getColumn("IdBarang").setPreferredWidth(0);
        tblDetail.getColumn("Qty Baru").setCellRenderer(new MyRowRenderer());
        tblDetail.getColumn("Hpp Baru").setCellRenderer(new MyRowRenderer());
        
        jXDatePicker1.setFormats(MainForm.sDateFormat);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        txtKeterangan.addKeyListener(kListener);
        tblDetail.addKeyListener(kListener);
        jScrollPane1.addKeyListener(kListener);
        AutoCompleteDecorator.decorate(cmbAkun);
        tblDetail.getModel().addTableModelListener(new MyTableModelListener(tblDetail));
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblDetail.getColumn("Qty Baru").setCellEditor(cEditor);
        tblDetail.getColumn("Hpp Baru").setCellEditor(cEditor);
        tblDetail.getColumn("Keterangan").setCellEditor(cEditor);
    }

    public void setConn(Connection c){
        this.conn=c;
        fn.setConn(conn);
        itemDao=new ItemDao(c);
    }
    
    private void initForm(){
        try {
            tblDetail.setRowHeight(20);
            opnameDao=new OpnameDao(conn);
            tblDetail.getColumn("Kode").setPreferredWidth(80);
            tblDetail.getColumn("Nama Barang").setPreferredWidth(200);
            listGudang=new GudangDao(conn).cariSemuaData();
            cmbGudang.removeAllItems();
            for(Gudang g: listGudang){
                cmbGudang.addItem(g.getNama_gudang());
            }
            
            listCoa=new AccCoaDao(conn).listBiaya();
            cmbAkun.removeAllItems();
            for(AccCoaView v: listCoa){
                cmbAkun.addItem(v.getAcc_name());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrmStokOpname.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private boolean bolehSimpan(){
        btnSave.requestFocusInWindow();
        if(txtNoBukti.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silahkan masukkan nomor bukti terlebih dulu!");
            txtNoBukti.requestFocus();
            return false;
        }
        if(jXDatePicker1.getDate()==null){
            JOptionPane.showMessageDialog(this, "Silahkan masukkan tangal terlebih dulu!");
            jXDatePicker1.requestFocus();
            return false;
        }
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Daftar barang yang diopname masih kosong!");
            tblDetail.requestFocus();
            return false;
        }
        if(cmbAkun.getSelectedIndex()<0){
            JOptionPane.showMessageDialog(this, "Silahkan pilih nomor akun terlebih dulu!");
            txtAkun.requestFocus();
            return false;
        }
        if(opnameDao.noBuktiSudahAda(txtNoBukti.getText())){
            JOptionPane.showMessageDialog(this, "No. Bukti sudah pernah dipakai. Silahkan isi dengan nomor lain!");
            txtNoBukti.requestFocus();
            return false;
        }
        Double qtySkg, saldo;
        int colSaldoSkg=tblDetail.getColumnModel().getColumnIndex("Qty Sekarang");
        int colIdBarang=tblDetail.getColumnModel().getColumnIndex("IdBarang");
        for (int i = 0; i < tblDetail.getRowCount(); i++) {
            saldo=itemDao.getSaldo(fn.udfGetInt(tblDetail.getValueAt(i, colIdBarang)), 
                    listGudang.get(cmbGudang.getSelectedIndex()).getId());
            qtySkg=fn.udfGetDouble(tblDetail.getValueAt(i, colSaldoSkg));
            
            if(!qtySkg.equals(saldo)){
                if(JOptionPane.showConfirmDialog(this, "Saldo sekarang untuk item: '"+tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Nama Barang"))+"' tidak sama dengan saldo komputer. Apakah akan diupdate?\n"
                        + "Saldo sekarang: "+qtySkg+"\n"
                        + "Saldo komputer: "+saldo+" ", "Saldo Beda", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    tblDetail.setValueAt(saldo, i, colSaldoSkg);
                }else{
                    return false;
                }
            }
        }
        return true;
    }
    private void save(){
        if(!bolehSimpan())
            return;
        
        Opname o=new Opname();
        o.setNoBukti(txtNoBukti.getText());
        o.setTanggal(jXDatePicker1.getDate());
        o.setAccNo(listCoa.get(cmbAkun.getSelectedIndex()).getAcc_no());
        o.setKeterangan(txtKeterangan.getText());
        o.setDihitungOleh(txtDihitungOleh.getText());
        o.setIdGudang(listGudang.get(cmbGudang.getSelectedIndex()).getId());
        o.setUserIns(MainForm.sUserName);
        
        TableColumnModel col=tblDetail.getColumnModel();
        for (int i = 0; i < tblDetail.getRowCount(); i++) {
            OpnameDetail d=new OpnameDetail();
            d.setIdBarang(fn.udfGetInt(tblDetail.getValueAt(i, col.getColumnIndex("IdBarang"))));
            d.setQtySekarang(fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty Sekarang"))));
            d.setQtyBaru(fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty Baru"))));
            d.setHppSekarang(fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Hpp Sekarang"))));
            d.setHppBaru(fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Hpp Baru"))));
            d.setKet(tblDetail.getValueAt(i, col.getColumnIndex("Keterangan")).toString());
            o.getOpnameDetail().add(d);
        }
        Integer id=opnameDao.simpan(o);
        if(id==null){
            JOptionPane.showMessageDialog(this, "Simpan opname gagal!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Data Opname tersimpan");
        System.out.println("Opname tersimpan dengan id: "+id);
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
        txtAkun = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        txtNoBukti = new javax.swing.JTextField();
        cmbAkun = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txtDihitungOleh = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        chkValueAdj = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblTotalItem = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Stok Opname");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        jLabel1.setText("Keterangan  :");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 95, 110, 20);

        txtAkun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtAkun);
        txtAkun.setBounds(125, 70, 85, 22);

        jLabel2.setText("No. Bukti : ");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 10, 85, 20);
        jPanel1.add(jXDatePicker1);
        jXDatePicker1.setBounds(125, 35, 160, 22);

        jLabel3.setText("Tanggal : ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 35, 85, 20);

        txtNoBukti.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtNoBukti.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNoBukti);
        txtNoBukti.setBounds(125, 10, 160, 22);

        cmbAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAkun.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAkunItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbAkun);
        cmbAkun.setBounds(215, 70, 420, 22);

        jLabel4.setText("Akun Penyesuaian : ");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 70, 110, 20);

        txtKeterangan.setColumns(20);
        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtKeterangan.setRows(5);
        jScrollPane2.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(125, 95, 510, 35);

        jLabel5.setText("Gudang : ");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(390, 10, 85, 20);

        txtDihitungOleh.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtDihitungOleh);
        txtDihitungOleh.setBounds(475, 35, 160, 22);

        jLabel6.setText("Dihitung Oleh : ");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(390, 35, 85, 20);

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbGudang);
        cmbGudang.setBounds(475, 10, 160, 22);

        chkValueAdj.setText("Penyesuaian Harga Pokok Persediaan");
        chkValueAdj.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkValueAdjItemStateChanged(evt);
            }
        });
        chkValueAdj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkValueAdjActionPerformed(evt);
            }
        });
        jPanel1.add(chkValueAdj);
        chkValueAdj.setBounds(125, 130, 370, 23);

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Satuan", "Qty Sekarang", "Qty Baru", "Hpp Sekarang", "Hpp Baru", "Keterangan", "IdBarang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblDetail);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0");
        jPanel2.add(lblTotal);
        lblTotal.setBounds(500, 5, 130, 20);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Total : ");
        jPanel2.add(jLabel7);
        jLabel7.setBounds(430, 5, 65, 20);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/accept.png"))); // NOI18N
        btnSave.setText("Simpan");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(btnSave);
        btnSave.setBounds(430, 30, 100, 30);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cancel.png"))); // NOI18N
        btnClose.setText("Tutup");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose);
        btnClose.setBounds(535, 30, 100, 30);
        jPanel2.add(lblTotalItem);
        lblTotalItem.setBounds(10, 5, 255, 20);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        setBounds(0, 0, 679, 426);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        initForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void cmbAkunItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAkunItemStateChanged
        if(cmbAkun.getSelectedIndex()>=0){
            txtAkun.setText(listCoa.get(cmbAkun.getSelectedIndex()).getAcc_no());
        }
    }//GEN-LAST:event_cmbAkunItemStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void chkValueAdjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkValueAdjActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkValueAdjActionPerformed

    private void chkValueAdjItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkValueAdjItemStateChanged
        tblDetail.getColumn("Hpp Sekarang").setMinWidth(chkValueAdj.isSelected()? 100: 0);
        tblDetail.getColumn("Hpp Sekarang").setMaxWidth(chkValueAdj.isSelected()? 100: 0);
        tblDetail.getColumn("Hpp Sekarang").setPreferredWidth(chkValueAdj.isSelected()? 100: 0);
        
        tblDetail.getColumn("Hpp Baru").setMinWidth(chkValueAdj.isSelected()? 100: 0);
        tblDetail.getColumn("Hpp Baru").setMaxWidth(chkValueAdj.isSelected()? 100: 0);
        tblDetail.getColumn("Hpp Baru").setPreferredWidth(chkValueAdj.isSelected()? 100: 0);
    }//GEN-LAST:event_chkValueAdjItemStateChanged

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkValueAdj;
    private javax.swing.JComboBox cmbAkun;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalItem;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtAkun;
    private javax.swing.JTextField txtDihitungOleh;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtNoBukti;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
            
//            if(getTableSource()==null)
//                return;
            
            if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {
                fn.keyTyped(evt);

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
//                    udfClear();
                    break;
                }
                case KeyEvent.VK_F5:{
//                    simpan();
                    
                    break;
                }
                case KeyEvent.VK_INSERT:{
                    udfLookupItem();
                    
                    break;
                }
//                case KeyEvent.VK_ESCAPE:{
//                    dispose();
//                    break;
//                }
                
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow()>=0){
                        int iRow=tblDetail.getSelectedRow();
                        if(iRow<0)
                            return;
                        
                        ((DefaultTableModel)tblDetail.getModel()).removeRow(iRow);
                        if(tblDetail.getRowCount()<0)
                            return;
                        if(tblDetail.getRowCount()>iRow)
                            tblDetail.setRowSelectionInterval(iRow, iRow);
                        else if(tblDetail.getRowCount()>0)
                            tblDetail.setRowSelectionInterval(0, 0);
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
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField ){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }else if(e.getSource() instanceof JXDatePicker){
                ((JXDatePicker)e.getSource()).setBackground(Color.YELLOW);
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField||e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;

    private void udfLookupItem(){
//        
        DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(this), true);
        String sItem="";
        int colKode=tblDetail.getColumnModel().getColumnIndex("IdBarang");
        for(int i=0; i< tblDetail.getRowCount(); i++){
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblDetail.getValueAt(i, colKode).toString()+"'";
        }
        String s="select * from("
                + "select coalesce(plu,'') as kode, coalesce(nama_barang,'') as nama_barang,"
                + "coalesce(satuan,'') as satuan, id\n" +
                    "from m_item\n" +
                    "where active\n" +
                (sItem.length()>0? "and id::varchar not in("+sItem+")":"")+" "+
                    "order by coalesce(nama_barang,'')  "
                + ")x " ;
        
        System.out.println(s);
        d1.setTitle("Lookup Barang");
        JTable tbl=d1.getTable();
        d1.udfLoad(conn, s, "(x.kode||x.nama_barang)", null);
        d1.setVisible(true);

        if(d1.getKode().length()>0){
                TableColumnModel col=d1.getTable().getColumnModel();
                
                int iRow = tbl.getSelectedRow();
                Integer idBarang=fn.udfGetInt(tbl.getValueAt(iRow, col.getColumnIndex("id")).toString());
                Double saldo=itemDao.getSaldo(idBarang, listGudang.get(cmbGudang.getSelectedIndex()).getId());
                Double preCost=itemDao.getPrevCostAvg(idBarang);
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                    tbl.getValueAt(iRow, col.getColumnIndex("satuan")).toString(),
                    saldo,
                    0, 
                    preCost,
                    preCost,
                    "",
                    idBarang
                });

                tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                tblDetail.changeSelection(tblDetail.getRowCount()-1, tblDetail.getColumnModel().getColumnIndex("Qty Baru"), false, false);
                tblDetail.requestFocusInWindow();
                
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
                if (col == tblDetail.getColumnModel().getColumnIndex("Qty Baru") || col == tblDetail.getColumnModel().getColumnIndex("Hpp Baru")) {
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
    
    public class MyTableModelListener implements TableModelListener {
        JTable table;

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
            int colHpp=tblDetail.getColumnModel().getColumnIndex("Hpp Sekarang");
            int colHppNew=tblDetail.getColumnModel().getColumnIndex("Hpp Baru");
            int colKomp=tblDetail.getColumnModel().getColumnIndex("Qty Sekarang");
            int colReal=tblDetail.getColumnModel().getColumnIndex("Qty Baru");
            for (int i=0; i<tblDetail.getRowCount(); i++){
                dTotal+= (fn.udfGetDouble(tblDetail.getValueAt(i, colReal))*fn.udfGetDouble(tblDetail.getValueAt(i, colHppNew)))-
                         (fn.udfGetDouble(tblDetail.getValueAt(i, colKomp))*fn.udfGetDouble(tblDetail.getValueAt(i, colHpp)))
                        ;
            }
            lblTotal.setText(fn.dFmt.format(dTotal));
            lblTotalItem.setText("<html>Jml Item: <b>"+tblDetail.getRowCount()+"</html>");
        }
    }
 
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                if(column==tblDetail.getColumnModel().getColumnIndex("Qty Baru")||column==tblDetail.getColumnModel().getColumnIndex("Hpp Baru")){
                    setBackground(new Color(204,204,255));
                }else{
                    setBackground(table.getBackground());
                }
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
            
            setValue(value);
            return this;
        }
    }
    
    private GeneralFunction fn=new GeneralFunction();
}
