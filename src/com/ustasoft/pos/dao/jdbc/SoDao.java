/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.SO;
import com.ustasoft.pos.domain.SODet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class SoDao {
    private Connection conn;
    
    public void setConn(Connection con){
        this.conn=con;
    }
    
    private String getNoSo(Date tgl) {
        String hasil=null;
        try{
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_so('"+new SimpleDateFormat("yyyy-MM-dd").format(tgl) +"')");
        if(rs.next()){
            hasil=rs.getString(1);
        }
        rs.close();
        }catch(SQLException se){
            System.out.println("Error getNoSO"+se.getMessage());
        }
        
        return hasil;
    }
    
    public void simpanHeader(SO header) throws SQLException{
        String sQry="";
        if(header.getNoSo()==null || header.getNoSo().equalsIgnoreCase("")){
            sQry="INSERT INTO so(customer_id, tanggal, description, top, so_disc, biaya_lain, "
                + "time_ins, user_ins, time_upd, user_upd, no_so) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,  "
                + "?, ?, ?, ?);";
        }else{
            sQry="UPDATE po "
                    + "SET customer_id=?, tanggal=?, description=?, top=?, so_disc=?, "
                    + "biaya_lain=?, time_ins=?, user_ins=?, time_upd=?, user_upd=? "
                    + " WHERE no_so=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        String sNoSo=header.getNoSo()==null||header.getNoSo().length()==0? getNoSo(header.getTanggal()): header.getNoSo();
        ps.setInt(1, header.getSupplierId());
        ps.setDate(2, new java.sql.Date(header.getTanggal().getTime()));
        ps.setString(3, header.getDescription());
        ps.setInt(4, header.getTop());
        ps.setString(5, header.getSoDisc());
        ps.setDouble(6, header.getBiayaLain());
        ps.setTimestamp(7, new java.sql.Timestamp(header.getTimeIns().getTime()));
        ps.setString(8, header.getUserIns());
        ps.setTimestamp(9, header.getNoSo()==null? null: new java.sql.Timestamp(header.getTimeUpd().getTime()));
        ps.setString(10, header.getNoSo()==null? null: header.getUserUpd());
        ps.setString(11,  sNoSo);
        int hasil=ps.executeUpdate();
        header.setNoSo(sNoSo);
        System.out.println("Record yg berhasil diupdate :"+hasil);
        
        
        ps.close();
    }
    
    public void simpanSODetail(List<SODet> list, boolean isNew) throws SQLException{
        String sQry="";
        if(!isNew){
            int i=conn.createStatement().executeUpdate("delete from so_det where no_so='"+list.get(0).getNoSo() +"' ");
            System.out.println(i+" Record dihapus terlebih dulu!");
            
        }
        sQry="INSERT INTO so_det(id_barang, qty, unit_price, disc, ppn, biaya, keterangan, no_so) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?); ";
//        else{
//            
//            sQry="UPDATE po_det SET id_barang=?, qty=?, unit_price=?, disc=?, ppn=?, biaya=?, "
//                    + "keterangan=? "
//                    + "WHERE no_po=?;";
//        }
        PreparedStatement ps=conn.prepareStatement(sQry);
        
        int i=1;
        for(SODet det:list){
            ps.setInt(1, det.getIdBarang());
            ps.setDouble(2, det.getQty());
            ps.setDouble(3, det.getUnitPrice());
            ps.setString(4, det.getDisc());
            ps.setDouble(5, det.getPpn());
            ps.setDouble(6, det.getBiaya());
            ps.setString(7, det.getKeterangan());
            ps.setString(8, det.getNoSo());
            
            ps.addBatch();
            i++;
        }    
        ps.executeBatch();
    }

}
