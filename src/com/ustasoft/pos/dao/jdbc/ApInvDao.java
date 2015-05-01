/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.pos.domain.ApInv;
import com.ustasoft.pos.domain.ArInvDet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class ApInvDao {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void simpanHeader(ApInv header) throws SQLException{
        String sQry="";
        if(header.getId()==null){
            sQry="INSERT INTO ap_inv("
                    + "supplier_id, invoice_no, description, invoice_date, top, "
                    + "invoice_amount, item_amount, paid_amount, owing, id_gudang, template_id,  "
                    + "time_ins, user_ins, time_upd, user_upd, ap_disc, biaya_lain, no_po, trx_type, retur_dari) "
                    + "VALUES (?, ?, ?, ?, ?,  "
                    + "?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?);";
        }else{
            sQry="UPDATE ap_inv "
                    + "   SET supplier_id=?, invoice_no=?, description=?, invoice_date=?, "
                    + "       top=?, invoice_amount=?, item_amount=?, paid_amount=?, owing=?, "
                    + "       id_gudang=?, template_id=?, time_ins=?, user_ins=?, time_upd=?, "
                    + "       user_upd=?, ap_disc=?, biaya_lain=?, no_po=?, trx_type=?, retur_dari=? "
                    + " WHERE id=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, header.getSupplierId());
        ps.setString(2, header.getInvoiceNo());
        ps.setString(3, header.getDescription());
        ps.setDate(4, new java.sql.Date(header.getInvoiceDate().getTime()));
        ps.setInt(5, header.getTop());
        ps.setDouble(6, header.getInvoiceAmount());
        ps.setDouble(7, header.getItemAmount());
        ps.setDouble(8, header.getPaidAmount());
        ps.setDouble(9, header.getOwing());
        ps.setInt(10, header.getIdGudang());
        ps.setInt(11, header.getTemplateId());
        ps.setTimestamp(12, new java.sql.Timestamp(fn.now()));
        ps.setString(13, MainForm.sUserName);
        ps.setTimestamp(14, header.getId()==null? null: new java.sql.Timestamp(fn.now()));
        ps.setString(15, header.getId()==null? null: header.getUserUpd());
        ps.setString(16, header.getApDisc());
        ps.setDouble(17, header.getBiayaLain());
        ps.setString(18, header.getNoPo());
        ps.setString(19, header.getTrxType());
        if(header.getReturDari()==null){
            ps.setNull(20, java.sql.Types.INTEGER);
        }else{
            ps.setInt(20, header.getReturDari());
        }
        
        
        if(header.getId()==null){
            int hasil=ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                header.setId(rs.getInt(1));
                System.out.println("ID Baru: +"+header.getId());
            }
            rs.close();
        }else{
            ps.setInt(21, header.getId());
            int hasil=ps.executeUpdate();
            System.out.println("Record yg berhasil diupdate :"+hasil);
        }
        
        ps.close();
    }
    
    public void simpanApInvDetail(List<ArInvDet> list, boolean isNew) throws SQLException{
        String sQry="";
        if(!isNew){
                String sDel="delete from ap_inv_det where ap_id="+list.get(0).getArId() +";";
                System.out.println(sDel);
                int iH=conn.createStatement().executeUpdate(sDel);
                System.out.println(iH+" Item dihapus!");
        }
//        if(isNew){
            sQry="INSERT INTO ap_inv_det("
                + "ap_id, id_barang, qty, unit_price, disc, unit_cost, seq, fifo_qty, ppn, last_cost, biaya, keterangan) "
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
        String sQry="select * from ap_inv where id="+id;
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

    public String getNoInvoice(String tipe) throws SQLException{
        String sNo="";
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_ap_inv('"+tipe+"')");
        if(rs.next()){
            sNo=rs.getString(1);
        }
        rs.close();
        return sNo;
    }

    public Double getSisaRetur(Integer idFaktur, Integer idBarang, Integer idRetur) throws SQLException{
        double sisa=0;
        String sQry="select sum(qty) "
                + "from ap_inv h "
                + "inner join ap_inv_det d on d.ap_id=h.id "
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
    
    public boolean adaYangDiretur(Integer id) throws SQLException{
        boolean b=false;
        ResultSet rs=conn.createStatement().executeQuery("select * from ap_inv where retur_dari="+id);
        b=rs.next();
        rs.close();
        return b;
    }

    public void setReturDari(Integer idRetur, Integer apId) {
        try {
            int i=conn.createStatement().executeUpdate(
                    "update ap_inv set retur_dari="+apId+", "
                            + "user_upd='"+MainForm.sUserName+"', "
                            + "time_upd=now() where id="+idRetur+"");
        } catch (SQLException ex) {
            Logger.getLogger(ApInvDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
