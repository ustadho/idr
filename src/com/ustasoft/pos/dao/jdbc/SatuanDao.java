/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Satuan;
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

public class SatuanDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public SatuanDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(Satuan satuan) throws Exception {
        String sqlInsert = "INSERT INTO m_satuan(satuan, keterangan) VALUES (?, ?);";
        String sqlUpdate = "UPDATE m_satuan SET satuan=?, keterangan=? "
                + " WHERE id=?;";
        if(satuan.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, satuan.getSatuan());
            ps.setString(2, satuan.getKeterangan());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : "+idBaru);
                satuan.setId(idBaru);
                
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, satuan.getSatuan());
            ps.setString(2, satuan.getKeterangan());
            ps.setInt(3, satuan.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Satuan> cariSemuaData() throws Exception {
        String sql = "select * from m_satuan order by 2";
        List<Satuan> hasil = new ArrayList<Satuan>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Satuan k = new Satuan();
            k.setId(rs.getInt("id"));
            k.setSatuan(rs.getString("satuan"));
            k.setKeterangan(rs.getString("keterangan"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Satuan cariSatuanByID(Integer id) throws Exception {
        String sql = "select * from m_satuan where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Satuan k = new Satuan();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setSatuan(rs.getString("satuan"));
            k.setKeterangan(rs.getString("keterangan"));
            
        }
        return k;
    }
    
    public int delete(Satuan k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getId()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_satuan where id=?");
            ps.setInt(1, k.getId());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
