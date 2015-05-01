/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.domain.ApInv;
import com.ustasoft.pos.domain.ArInv;
import com.ustasoft.pos.domain.ArInvDet;
import com.ustasoft.pos.domain.ArSuratJalan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class ArInvDao {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void simpanHeader(ArInv header) throws SQLException{
        String sQry="";
        if(header.getId()==null){
            sQry="INSERT INTO ar_inv("
                    + "id_customer, invoice_no, description, invoice_date, jenis_bayar, top, "
                    + "invoice_amount, item_amount, paid_amount, owing, id_gudang, template_id,  "
                    + "time_ins, user_ins, time_upd, user_upd, ar_disc, biaya_lain, no_so, "
                    + "disiapkan_oleh, diperiksa_oleh, diterima_oleh, id_sales, id_expedisi, "
                    + "ket_pembayaran, trx_type, retur_dari) "
                    + "VALUES (?, ?, ?, ?, ?, ?, " //6
                    + "?, ?, ?, ?, ?, ?, " //12
                    + "?, ?, ?, ?, ?, ?, ?, " //19
                    + "?, ?, ?, ?, ?," //24
                    + "?, ?, ?);"; //27
        }else{
            sQry="UPDATE ar_inv "
                    + "   SET id_customer=?, invoice_no=?, description=?, invoice_date=?, jenis_bayar=?, " //5
                    + "       top=?, invoice_amount=?, item_amount=?, paid_amount=?, owing=?, " //10
                    + "       id_gudang=?, template_id=?, time_ins=?, user_ins=?, time_upd=?, " //15
                    + "       user_upd=?, ar_disc=?, biaya_lain=?, no_so=?, " //19
                    + "       disiapkan_oleh=?, diperiksa_oleh=?, diterima_oleh=?, " //22
                    + "       id_sales=?, id_expedisi=?, ket_pembayaran=?, " //25
                    + "trx_type=?, retur_dari=? "//27
                    + " WHERE id=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, header.getCustomerId());
        ps.setString(2, header.getInvoiceNo());
        ps.setString(3, header.getDescription());
        ps.setDate(4, new java.sql.Date(header.getInvoiceDate().getTime()));
        ps.setString(5, header.getJenisBayar());
        ps.setInt(6, header.getTop());
        ps.setDouble(7, header.getInvoiceAmount());
        ps.setDouble(8, header.getItemAmount());
        ps.setDouble(9, header.getPaidAmount());
        ps.setDouble(10, header.getOwing());
        ps.setInt(11, header.getIdGudang());
        ps.setInt(12, header.getTemplateId());
        ps.setTimestamp(13, new java.sql.Timestamp(header.getTimeIns().getTime()));
        ps.setString(14, header.getUserIns());
        ps.setTimestamp(15, header.getId()==null? null: new java.sql.Timestamp(fn.now()));
        ps.setString(16, header.getId()==null? null: header.getUserUpd());
        ps.setString(17, header.getArDisc());
        ps.setDouble(18, header.getBiayaLain());
        ps.setString(19, header.getNoSo());
        ps.setString(20, header.getDisiapkanOleh());
        ps.setString(21, header.getDiperiksaOleh());
        ps.setString(22, header.getDiterimaOleh());
        ps.setInt(23, header.getIdSales());
        ps.setInt(24, header.getIdExpedisi()==null? new Integer(0): header.getIdExpedisi());
        ps.setString(25, header.getKetPembayaran());
        ps.setString(26, header.getTrxType());
        ps.setInt(27, header.getReturDari());
//        ps.setString(28, header.getNoPol());
        
        if(header.getId()==null){
            int hasil=ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                header.setId(rs.getInt(1));
                System.out.println("ID Baru: +"+header.getId());
            }
            rs.close();
        }else{
            ps.setInt(28, header.getId());
            int hasil=ps.executeUpdate();
            System.out.println("Record yg berhasil diupdate :"+hasil);
        }
        
        ps.close();
    }
    
    public void simpanArInvDetail(List<ArInvDet> list, boolean isNew) throws SQLException{
        String sQry="";
        if(!isNew){
                String sDel="delete from ar_inv_det where ar_id="+list.get(0).getArId() +";";
                System.out.println(sDel);
                int iH=conn.createStatement().executeUpdate(sDel);
                System.out.println(iH+" Item dihapus!");
        }
//        if(isNew){
            sQry="INSERT INTO ar_inv_det("
                + "ar_id, id_barang, qty, unit_price, disc, unit_cost, seq, fifo_qty, ppn, last_cost, biaya, keterangan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
//        }else{
//            sQry="UPDATE ap_inv_det "
//                    + "SET ap_id=?, id_barang=?, qty=?, unit_price=?, disc=?, unit_cost=?, "
//                    + "seq=?, fifo_qty=?, ppn=?, last_cost=?, biaya=?, keterangan=? "
//                    + "WHERE det_id=?;";
//        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        
        int i=1;
//        String sIdBarang="";
        for(ArInvDet det:list){
            ps.setInt(1, det.getArId());
            ps.setInt(2, det.getItemId());
            ps.setDouble(3, det.getQty());
            ps.setDouble(4, det.getUnitPrice());
            ps.setString(5, det.getDisc());
            ps.setDouble(6, det.getUnitCost());
            ps.setInt(7, i);
            ps.setDouble(8, det.getQty());
            ps.setDouble(9, det.getPpn());
            ps.setDouble(10, det.getLastCost());
            ps.setDouble(11, det.getBiaya());
            ps.setString(12, det.getKeterangan());
//            if(det.getDetId()!=null)
//                ps.setInt(13, det.getDetId());
            
            ps.addBatch();
            
//            sIdBarang+=sIdBarang.length()>0? ",": "";
//            sIdBarang+=det.getItemId();
            i++;
        }    
        ps.executeBatch();
        
    }

    public ApInv getInvHeader(Integer id) throws SQLException{
        ApInv inv=new ApInv();
        String sQry="select * from ar_inv where id="+id;
        ResultSet rs=conn.createStatement().executeQuery(sQry);
        if(rs.next()){
            inv.setId(rs.getInt("id"));
            inv.setSupplierId(rs.getInt("supplier_id"));
            inv.setInvoiceNo(rs.getString("invoice_no"));
            inv.setDescription(rs.getString("description"));
            inv.setInvoiceDate(rs.getDate("invoice_date"));
            inv.setTop(rs.getInt("top"));
            inv.setInvoiceAmount(rs.getDouble("invoice_amount"));
            inv.setItemAmount(rs.getDouble("item_amount"));
            inv.setPaidAmount(rs.getDouble("paid_amount"));
            inv.setOwing(rs.getDouble("owing"));
            inv.setIdGudang(rs.getInt("id_gudang"));
            inv.setTemplateId(rs.getInt("template_id"));
            inv.setTimeIns(rs.getTimestamp("time_ins"));
            inv.setUserIns(rs.getString("user_ins"));
            inv.setTimeUpd(rs.getTimestamp("time_upd"));
            inv.setUserUpd(rs.getString("user_upd"));
            inv.setApDisc(rs.getString("ap_disc"));
            inv.setBiayaLain(rs.getDouble("biaya_lain"));
            rs.close();
            return inv;
        }
        rs.close();
        return null;
    }
    
    public String getNoInvoice(String sTipe) throws SQLException{
        String s="select fn_get_no_ar_inv('"+sTipe+"')";
        ResultSet rs=conn.createStatement().executeQuery(s);
        s=null;
        if(rs.next()){
            s=rs.getString(1);
        }
        rs.close();
        return s;
        
    }
    public String getNoSj() throws SQLException{
        String s="select fn_get_no_ar_sj()";
        ResultSet rs=conn.createStatement().executeQuery(s);
        s=null;
        if(rs.next()){
            s=rs.getString(1);
        }
        rs.close();
        return s;
        
    }
    
    
    
    public void updateExpedisi(Integer idPenjualan, Integer idExpedisi) throws SQLException{
        PreparedStatement ps=conn.prepareStatement("update ar_inv set id_expedisi=? where id=?");
        ps.setInt(1, idExpedisi);
        ps.setInt(2, idPenjualan);
        int hasil=ps.executeUpdate();
        ps.close();
        System.out.println("Data yg diupdate : "+hasil);
        
    }
    
    public void simpanSuratJalan(ArSuratJalan sj) throws SQLException{
        String sUpd="";
        String noSj=sj.getNoSj()==null? getNoSj(): sj.getNoSj();
        //sj.setNoSj(noSj);
        if(sj.getNoSj()==null){
            sUpd="INSERT INTO ar_sj("
                    + "ar_id, tgl_sj, kirim_ke, alamat, id_kota, hormat_kami, coli, keterangan, no_sj) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        }else{
            sUpd="UPDATE ar_sj "
                    + "SET ar_id=?, tgl_sj=?, kirim_ke=?, alamat=?, id_kota=?, hormat_kami=?, "
                    + "coli=?, keterangan=? "
                    + "WHERE no_sj=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sUpd);
        ps.setInt(1, sj.getArId());
        ps.setDate(2, new java.sql.Date(sj.getTglSj().getTime()));
        ps.setString(3, sj.getKirimKe());
        ps.setString(4, sj.getAlamat());
        ps.setInt(5, sj.getIdKota());
        ps.setString(6, sj.getHormatKami());
        ps.setDouble(7, sj.getColi());
        ps.setString(8, sj.getKeterangan());
        ps.setString(9, noSj);
        
        int hasil=ps.executeUpdate();
        sj.setNoSj(noSj);
        ps.close();
        System.out.println("Transaksi disimpan/update "+hasil);
        
    }
    
    public Double getQtyTotal(Integer id) throws SQLException{
        Double total=new Double(0);
        ResultSet rs=conn.createStatement().executeQuery("select sum(qty) as total from ar_inv_det where ar_id="+id+"");
        if(rs.next()){
            total=rs.getDouble(1);
        }
        rs.close();
        return total;
    }
    
    public Double getDiskonPembelian(Integer id) throws SQLException{
        Double hasil=new Double(0);
        String sQry="select * from fn_ar_nett("+id+") as (nett double precision, disc_rp double precision, "
                + "ret_nett double precision, ret_disc_rp double precision)";
        ResultSet rs=conn.createStatement().executeQuery(sQry);
        if(rs.next()){
            hasil=rs.getDouble("disc_rp");
        }
        rs.close();
        return hasil;
    }

    public Double getSisaRetur(Integer idFaktur, Integer idBarang, Integer idRetur) throws SQLException{
        double sisa=0;
        String sQry="select sum(qty) "
                + "from ar_inv h "
                + "inner join ar_inv_det d on d.ar_id=h.id "
                + "where  "
                + "(h.id="+idFaktur+ " or ("
                + "h.id<>"+idRetur+" and h.retur_dari="+idFaktur+")) "
                + "and d.id_barang="+idBarang+" ";
        System.out.println(sQry);
        ResultSet rs=conn.createStatement().executeQuery(sQry);
        if(rs.next()){
            sisa=rs.getDouble(1);
        }
        rs.close();
        
        return sisa;
    }

    public boolean isExistsInvoiceNo(String text) {
        boolean b=false;
        String sql="select invoice_no from ar_inv  where invoice_no  ='"+text+"' ";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sql);
            b=rs.next();
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        return b;
    }

    public String cekBeforeDelete(Integer id) {
        String message="";
        try {
            ResultSet rs=conn.createStatement().executeQuery("select * from ar_receipt  r\n" +
                    "inner join ar_receipt_detail d on d.ar_no=r.ar_no\n" +
                    "where d.id_penjualan="+id+" ");
            if(rs.next()){
                message="Saldo ini tidak bisa dihapus karena sudah dilakukan pembayaran.\n"
                        + "Hapus pembayaran terlebih dulu, baru hapus data ini";
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ArInvDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }
    
    public int itemCount(Integer id){
        try {
            ResultSet rs=conn.createStatement().executeQuery("select count(id_barang) "
                    + "from ar_inv_det where ar_id="+id);
            rs.next();
            int count=rs.getInt(1);
            rs.close();
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(ArInvDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public List<ArInv> returList(Integer i){
        List<ArInv> list=new ArrayList<ArInv>();
        try {
            ResultSet rs=conn.createStatement().executeQuery("select * "
                    + "from ar_inv where ar_id="+i);
            while(rs.next()){
                ArInv a=new ArInv();
                a.setId(rs.getInt("id"));
                a.setItemAmount(rs.getDouble("item_amount"));
            }
            int count=rs.getInt(1);
            rs.close();
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(ArInvDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
