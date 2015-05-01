/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Gudang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cak-ust
 */

public class GudangDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public GudangDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(Gudang k) throws Exception {
        String sqlInsert = "INSERT INTO m_gudang(nama_gudang, lokasi, keterangan) "
                + "    VALUES (?, ?, ?);";
        String sqlUpdate = "UPDATE m_gudang "
                + "SET nama_gudang=?, lokasi=?, keterangan=? "
                + "WHERE id=?;";
        if(k.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, k.getNama_gudang());
            ps.setString(2, k.getLokasi());
            ps.setString(3, k.getKeterangan());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : "+idBaru);
                k.setId(idBaru);
                
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, k.getNama_gudang());
            ps.setString(2, k.getLokasi());
            ps.setString(3, k.getKeterangan());
            ps.setInt(4, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Gudang> cariSemuaData() throws Exception {
        String sql = "select * from m_gudang order by 2";
        List<Gudang> hasil = new ArrayList<Gudang>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Gudang k = new Gudang();
            k.setId(rs.getInt("id"));
            k.setNama_gudang(rs.getString("nama_gudang"));
            k.setLokasi(rs.getString("lokasi"));
            k.setKeterangan(rs.getString("keterangan"));
            hasil.add(k);
        }
        rs.close();
        return hasil;
    }
    
    public Gudang cariByID(Integer id) throws Exception {
        String sql = "select * from m_gudang where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Gudang k = new Gudang();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNama_gudang(rs.getString("nama_gudang"));
            k.setLokasi(rs.getString("lokasi"));
            k.setKeterangan(rs.getString("keterangan"));
            
        }
        return k;
    }
    
    public int delete(Gudang k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getId()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_gudang where id=?");
            ps.setInt(1, k.getId());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
