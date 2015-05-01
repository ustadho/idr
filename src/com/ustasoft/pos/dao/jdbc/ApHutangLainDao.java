/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.ApHutangLain;
import com.ustasoft.pos.domain.ApHutangLainAlatBayar;
import com.ustasoft.pos.domain.ApHutangLainAngsuran;
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
public class ApHutangLainDao {
    private final Connection conn;
    public ApHutangLainDao(Connection c){
        this.conn=c;
    }
    
    private String getNoAp(Date tgl) throws SQLException{
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_ap_lain('"+new SimpleDateFormat("yyyy-MM-dd").format(tgl)+"')");
        
        if(rs.next()){
            String no=rs.getString(1);
            rs.close();
            return no;
        }
        rs.close();
        
        return null;
    }
    
    public void simpanHeader(ApHutangLain header) throws SQLException{
        String sQry="";
        if(header.getNoAp()==null){
            sQry="INSERT INTO ap_hutang_lain("
                    + "tanggal, id_relasi, is_saldo_aw, keterangan, akun_debet, "
                    + "akun_kas_bank, total, user_ins, time_ins, tipe_relasi, no_ap) "
                    + "VALUES (?, ?, ?, ?, ?, ?,  "
                    + "?, ?, ?, ?, ?);";
        }else{
            sQry="UPDATE ap_hutang_lain "
                    + "SET tanggal=?, id_relasi=?, is_saldo_aw=?, keterangan=?, "
                    + "akun_debet=?, akun_kas_bank=?, total=?, user_ins=?, time_ins=?, tipe_relasi=? "
                    + " WHERE no_ap=?";
        }
        PreparedStatement ps=conn.prepareStatement(sQry, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, new java.sql.Date(header.getTanggal().getTime()));
        ps.setInt(2, header.getIdRelasi());
        ps.setBoolean(3, header.getSaldoAw());
        ps.setString(4, header.getKeterangan());
        ps.setString(5, header.getAkunDebet());
        ps.setString(6, header.getAkunKasBank());
        ps.setDouble(7, header.getTotal());
        ps.setString(8, header.getNoAp()!=null? header.getUserIns(): header.getUserIns());
        ps.setTimestamp(9, header.getNoAp()!=null? null: new java.sql.Timestamp(header.getTimeIns().getTime()));
        ps.setString(10, header.getTipeRelasi());
        
        if(header.getNoAp()==null){
            String sNo=getNoAp(header.getTanggal());
            ps.setString(11, sNo);
            
            int hasil=ps.executeUpdate();
            header.setNoAp(sNo);
            System.out.println("Record yg berhasil diinsert :"+hasil);
        }else{
            ps.setString(11, header.getNoAp());
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
