/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.ApInv;
import com.ustasoft.pos.domain.Transfer;
import com.ustasoft.pos.domain.TransferDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class TransferDao {
    private Connection conn;
    
    public void setConn(Connection con){
        this.conn=con;
    }
    
    private String getNoTransfer(String sTgl){
        String sno=null;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_transfer('"+sTgl+"')");
            if(rs.next()){
                sno=rs.getString(1);
            }
            rs.close();
        }catch(SQLException se){
            System.err.println("Error :"+se.getMessage());
        }
        
        return  sno;
    }
    
    public void simpanHeader(Transfer header) throws SQLException{
        String sQry="", sNoBukti="";
        if(header.getTransferID()==null){
            sQry="INSERT INTO transfer("
                    + "tanggal, description, from_gudang, to_gudang, user_ins, "
                    + "time_ins, user_upd, time_upd, no_bukti) "
                    + "VALUES (?, ?, ?, ?, ?, ?,  "
                    + "?, ?, ?);";
        }else{
            sQry="UPDATE transfer "
                    + "SET tanggal=?, description=?, from_gudang=?, to_gudang=?, "
                    + "user_ins=?, time_ins=?, user_upd=?, time_upd=?, no_bukti=? "
                    + " WHERE id=?;";
        }
        sNoBukti=header.getNoBukti()==null? getNoTransfer(new SimpleDateFormat("yyyy-MM-dd").format(header.getTanggal())): 
                header.getNoBukti();
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, new java.sql.Date(header.getTanggal().getTime()));
        ps.setString(2, header.getDescription());
        ps.setInt(3, header.getFromGudang());
        ps.setInt(4, header.getToGudang());
        ps.setString(5, header.getUserIns());
        ps.setDate(6, new java.sql.Date(header.getTimeIns().getTime()));
        ps.setString(7, header.getTransferID()==null? null: header.getUserUpd());
        ps.setTimestamp(8, header.getTransferID()==null? null: new java.sql.Timestamp(header.getTimeUpd().getTime()));
        ps.setString(9, sNoBukti);
        //ps.setInt(9, header.getTransferID());
        
        if(header.getTransferID()==null){
            //header.setId(getTransferID(new SimpleDateFormat("yyyy-MM-dd").format(header.getTanggal())));
            int hasil=ps.executeUpdate();
            
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                header.setId(rs.getInt(1));
                header.setNoBukti(sNoBukti);
                System.out.println("ID Baru: +"+header.getTransferID());
            }
        }else{
            ps.setInt(10, header.getTransferID());
            int hasil=ps.executeUpdate();
            System.out.println("Record yg berhasil diupdate :"+hasil);
        }
        
        ps.close();
    }
    
    public void simpanTransferDetail(List<TransferDetail> list, boolean isNew) throws SQLException{
        String sQry="";
        if(!isNew){
                String sDel="delete from transfer_detail where id_transfer="+list.get(0).getTransferID() +";";
                System.out.println(sDel);
                int iH=conn.createStatement().executeUpdate(sDel);
                System.out.println(iH+" Item dihapus!");
        }
//        if(isNew){
            sQry="INSERT INTO transfer_detail(id_transfer, id_barang, qty, seq, ket_item) "
                    + "VALUES (?, ?, ?, ?, ?);";
//        }else{
//            sQry="UPDATE ap_inv_det "
//                    + "SET ap_id=?, id_barang=?, qty=?, unit_price=?, disc=?, unit_cost=?, "
//                    + "seq=?, fifo_qty=?, ppn=?, last_cost=?, biaya=?, keterangan=? "
//                    + "WHERE det_id=?;";
//        }
        PreparedStatement ps=conn.prepareStatement(sQry);
        
        int i=1;
//        String sIdBarang="";
        for(TransferDetail det:list){
            ps.setInt(1, det.getTransferID());
            ps.setInt(2, det.getIdBarang());
            ps.setDouble(3, det.getQty());
            ps.setInt(4, i);
            ps.setString(5, det.getKetItem());
            ps.addBatch();
            i++;
        }    
        int hasil[]=ps.executeBatch();
        System.out.println("Record yang diinsert adalah: "+hasil.length);
        
    }

    
    
}
