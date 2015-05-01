/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Satuan;
import com.ustasoft.pos.domain.TrxType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cak-ust
 */

public class TrxTypeDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public TrxTypeDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(TrxType tipe, boolean isNew) throws Exception {
        String sqlInsert = "INSERT INTO m_trx_type(kode, keterangan, in_out, planned, akun) VALUES (?, ?, ?, ?, ?);";
        String sqlUpdate = "UPDATE m_trx_type SET keterangan=?, in_out=?, planned=?, akun=? "
                + " WHERE kode=?;";
        if(isNew){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, tipe.getKode());
            ps.setString(2, tipe.getKeterangan());
            ps.setString(3, tipe.getInOut());
            ps.setBoolean(4, tipe.isPlanned());
            ps.setString(5, tipe.getAkun());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
        } else { // record lama, update saja
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, tipe.getKeterangan());
            ps.setString(2, tipe.getInOut());
            ps.setBoolean(3, tipe.isPlanned());
            ps.setString(4, tipe.getAkun());
            ps.setString(5, tipe.getKode());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<TrxType> cariSemuaData() throws Exception {
        String sql = "select * from m_trx_type order by 2";
        List<TrxType> hasil = new ArrayList<TrxType>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            TrxType k = new TrxType();
            k.setKode(rs.getString("kode"));
            k.setKeterangan(rs.getString("keterangan"));
            k.setInOut(rs.getString("in_out"));
            k.setPlanned(rs.getBoolean("planned"));
            k.setAkun(rs.getString("acc_no"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public TrxType cariByKode(String kode) throws Exception {
        String sql = "select * from m_trx_type where kode='"+kode+"'";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        TrxType k = new TrxType();
        if(rs.next()){
            k.setKode(rs.getString("kode"));
            k.setKeterangan(rs.getString("keterangan"));
            k.setInOut(rs.getString("in_out"));
            k.setPlanned(rs.getBoolean("planned"));
            k.setAkun(rs.getString("acc_no"));
            
        }
        return k;
    }
    
    public int delete(TrxType k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getKode()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_trx_type where kode=?");
            ps.setString(1, k.getKode());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
