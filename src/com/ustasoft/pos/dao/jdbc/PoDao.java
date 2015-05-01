/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.PO;
import com.ustasoft.pos.domain.PODet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.springframework.core.env.JOptCommandLinePropertySource;

/**
 *
 * @author cak-ust
 */
public class PoDao {
    private Connection conn;
    
    public void setConn(Connection con){
        this.conn=con;
    }
    
    private String getNoPo(Date tgl) {
        String hasil=null;
        try{
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_po('"+new SimpleDateFormat("yyyy-MM-dd").format(tgl) +"')");
        if(rs.next()){
            hasil=rs.getString(1);
        }
        rs.close();
        }catch(SQLException se){
            System.out.println("Error getNoPO"+se.getMessage());
        }
        
        return hasil;
    }
    
    public void simpanHeader(PO header) throws SQLException{
        String sQry="";
        if(header.getNoPo()==null || header.getNoPo().equalsIgnoreCase("")){
            sQry="INSERT INTO po(supplier_id, tanggal, description, top, po_disc, biaya_lain, "
                + "time_ins, user_ins, time_upd, user_upd, no_po) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,  "
                + "?, ?, ?, ?);";
        }else{
            sQry="UPDATE po "
                    + "SET supplier_id=?, tanggal=?, description=?, top=?, po_disc=?, "
                    + "biaya_lain=?, time_ins=?, user_ins=?, time_upd=?, user_upd=? "
                    + " WHERE no_po=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        String sNoPo=header.getNoPo()==null||header.getNoPo().length()==0? getNoPo(header.getTanggal()): header.getNoPo();
        ps.setInt(1, header.getSupplierId());
        ps.setDate(2, new java.sql.Date(header.getTanggal().getTime()));
        ps.setString(3, header.getDescription());
        ps.setInt(4, header.getTop());
        ps.setString(5, header.getPoDisc());
        ps.setDouble(6, header.getBiayaLain());
        ps.setTimestamp(7, new java.sql.Timestamp(header.getTimeIns().getTime()));
        ps.setString(8, header.getUserIns());
        ps.setTimestamp(9, header.getNoPo()==null? null: new java.sql.Timestamp(header.getTimeUpd().getTime()));
        ps.setString(10, header.getNoPo()==null? null: header.getUserUpd());
        ps.setString(11,  sNoPo);
        int hasil=ps.executeUpdate();
        header.setNoPo(sNoPo);
        System.out.println("Record yg berhasil diupdate :"+hasil);
        
        
        ps.close();
    }
    
    public void simpanPODetail(List<PODet> list, boolean isNew) throws SQLException{
        String sQry="";
        if(!isNew){
            int i=conn.createStatement().executeUpdate("delete from po_det where no_po='"+list.get(0).getNoPo() +"' ");
            System.out.println(i+" Record dihapus terlebih dulu!");
            
        }
        sQry="INSERT INTO po_det(id_barang, qty, unit_price, disc, ppn, biaya, keterangan, no_po) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?); ";
//        else{
//            
//            sQry="UPDATE po_det SET id_barang=?, qty=?, unit_price=?, disc=?, ppn=?, biaya=?, "
//                    + "keterangan=? "
//                    + "WHERE no_po=?;";
//        }
        PreparedStatement ps=conn.prepareStatement(sQry);
        
        int i=1;
        for(PODet det:list){
            ps.setInt(1, det.getIdBarang());
            ps.setDouble(2, det.getQty());
            ps.setDouble(3, det.getUnitPrice());
            ps.setString(4, det.getDisc());
            ps.setDouble(5, det.getPpn());
            ps.setDouble(6, det.getBiaya());
            ps.setString(7, det.getKeterangan());
            ps.setString(8, det.getNoPo());
            
            ps.addBatch();
            i++;
        }    
        ps.executeBatch();
    }

}
