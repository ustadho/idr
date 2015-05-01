/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.ApBayarHutang;
import com.ustasoft.pos.domain.ApHutangLainAlatBayar;
import com.ustasoft.pos.domain.ApHutangLainAngsuran;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class ApBayarHutangDao {
    private final Connection conn;
    public ApBayarHutangDao(Connection c){
        this.conn=c;
    }
    
    private String getNoBayar() throws SQLException{
        ResultSet rs=conn.createStatement().executeQuery("select select fn_ap_get_no_bayar()");
        
        if(rs.next()){
            String no=rs.getString(1);
            rs.close();
            return no;
        }
        rs.close();
        
        return null;
    }
    
    public void simpanHeader(ApBayarHutang header) throws SQLException{
        String sQry="";
        if(header.getNoBayar()==null){
            sQry="INSERT INTO ap_bayar_hutang(" +
                "            no_bayar, tanggal, keterangan, kode_relasi, tipe_relasi, user_ins, " +
                "            time_ins, user_upd, time_upd)" +
                "    VALUES (?, ?, ?, ?, ?, ?, " +
                "            ?, ?, ?);";
        }else{
            sQry="UPDATE ap_bayar_hutang" +
                "   SET tanggal=?, keterangan=?, kode_relasi=?, tipe_relasi=?, \n" +
                "       user_ins=?, time_ins=?, user_upd=?, time_upd=?\n" +
                " WHERE no_bayar=?;";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, new java.sql.Date(header.getTanggal().getTime()));
        ps.setInt(2, header.getIdRelasi());
        ps.setString(4, header.getKeterangan());
        ps.setTimestamp(9, header.getNoBayar()!=null? null: new java.sql.Timestamp(header.getTimeIns().getTime()));
        ps.setString(10, header.getTipeRelasi());
        
        if(header.getNoBayar()==null){
            String sNo=getNoBayar();
            ps.setString(11, sNo);
            
            int hasil=ps.executeUpdate();
            header.setNoBayar(sNo);
            System.out.println("Record yg berhasil diinsert :"+hasil);
        }else{
            ps.setString(11, header.getNoBayar());
            int hasil=ps.executeUpdate();
            System.out.println("Record yg berhasil diupdate :"+hasil);
        }
        
        ps.close();
    }
    
    public void simpanAngsuran(List<ApHutangLainAngsuran> ab) throws SQLException{
        int i=conn.createStatement().executeUpdate(
                "delete from ap_hutang_lain_angsuran where no_ap='"+ab.get(0).getNoAp() +"';");
        String qry="INSERT INTO ap_hutang_lain_angsuran("
                + "no_ap, angsuran_ke, jt_tempo, nominal, keterangan) "
                + "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement ps=conn.prepareStatement(qry);
        for(ApHutangLainAngsuran h:ab){
            ps.setString(1, h.getNoAp());
            ps.setInt(2, h.getAngsuranKe());
            ps.setDate(3, new java.sql.Date(h.getJtTempo().getTime()));
            ps.setDouble(4, h.getNominal());
            ps.setString(5, h.getKeterangan());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    public void simpanAlatBayar(List<ApHutangLainAlatBayar> ab) throws SQLException{
        int i=conn.createStatement().executeUpdate("delete from ap_hutang_lain_alat_bayar where no_ap='"+ab.get(0).getNoAp() +"';");
        String qry="INSERT INTO ap_hutang_lain_alat_bayar("
                    + "no_ap, kode_bayar, jt_tempo, nominal, keterangan) "
                    + "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement ps=conn.prepareStatement(qry);
        for(ApHutangLainAlatBayar h:ab){
            ps.setString(1, h.getNoAp());
            ps.setString(2, h.getKodeBayar());
            ps.setDate(3, h.getJtTempo()==null? null: new java.sql.Date(h.getJtTempo().getTime()));
            ps.setDouble(4, h.getNominal());
            ps.setString(5, h.getKeterangan());
            ps.addBatch();
        }
        ps.executeBatch();
    }
    
    
}
