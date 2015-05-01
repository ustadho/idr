/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Area;
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

public class AreaDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public AreaDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(Area k) throws SQLException {
        String sqlInsert = "INSERT INTO m_area(nama) "
                + "VALUES (?);";
        String sqlUpdate = "UPDATE m_area SET nama=? "
                + " WHERE id=?;";
        if(k.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, k.getNama());
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
            ps.setString(1, k.getNama());
            ps.setInt(4, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Area> cariSemuaData() throws Exception {
        String sql = "select * from m_area order by 2";
        List<Area> hasil = new ArrayList<Area>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Area k = new Area();
            k.setId(rs.getInt("id"));
            k.setNama(rs.getString("nama"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public List<Area> FilterArea(String filter) throws Exception {
        String sql = "select * from m_area "
                + "where nama ilike '%"+filter+"%' order by 2";
        List<Area> hasil = new ArrayList<Area>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Area k = new Area();
            k.setId(rs.getInt("id"));
            k.setNama(rs.getString("nama"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Area cariByID(Integer id) throws Exception {
        String sql = "select * from m_area where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Area k = new Area();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNama(rs.getString("nama"));
            
        }
        return k;
    }
    
    public int delete(int k) throws SQLException{
        int hasil=0;
        PreparedStatement ps=conn.prepareStatement("delete from m_area where id=?");
        ps.setInt(1, k);
        hasil=ps.executeUpdate();
        ps.close();
        return hasil;
    }
}
