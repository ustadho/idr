/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Kota;
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

public class KotaDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public KotaDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(Kota k) throws SQLException {
        String sqlInsert = "INSERT INTO m_Kota(nama_kota, provinsi, negara, id_area) "
                + "VALUES (?, ?, ?);";
        String sqlUpdate = "UPDATE m_Kota SET nama_kota=?, provinsi=?, negara=?, "
                + "id_area=? "
                + " WHERE id=?;";
        if(k.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, k.getNamaKota());
            ps.setString(2, k.getProvinsi());
            ps.setString(3, k.getNegara());
            ps.setInt(4, k.getIdArea());
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
            ps.setString(1, k.getNamaKota());
            ps.setString(2, k.getProvinsi());
            ps.setString(3, k.getNegara());
            ps.setInt(4, k.getIdArea());
            ps.setInt(5, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Kota> cariSemuaData() throws Exception {
        String sql = "select * from m_Kota order by 2";
        List<Kota> hasil = new ArrayList<Kota>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Kota k = new Kota();
            k.setId(rs.getInt("id"));
            k.setNamaKota(rs.getString("nama_kota"));
            k.setProvinsi(rs.getString("provinsi"));
            k.setNegara(rs.getString("negara"));
            k.setIdArea(rs.getInt("id_area"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public List<Kota> FilterKota(String filter) throws Exception {
        String sql = "select * from m_Kota "
                + "where nama_kota||coalesce(provinsi,'')||coalesce(negara,'') ilike '%"+filter+"%' order by 2";
        List<Kota> hasil = new ArrayList<Kota>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Kota k = new Kota();
            k.setId(rs.getInt("id"));
            k.setNamaKota(rs.getString("nama_kota"));
            k.setProvinsi(rs.getString("provinsi"));
            k.setNegara(rs.getString("negara"));
            k.setIdArea(rs.getInt("id_area"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Kota cariByID(Integer id) throws Exception {
        String sql = "select * from m_Kota where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Kota k = new Kota();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNamaKota(rs.getString("nama_kota"));
            k.setProvinsi(rs.getString("provinsi"));
            k.setNegara(rs.getString("negara"));
            k.setIdArea(rs.getInt("id_area"));
        }
        return k;
    }
    
    public int delete(int k) throws SQLException{
        int hasil=0;
        PreparedStatement ps=conn.prepareStatement("delete from m_Kota where id=?");
        ps.setInt(1, k);
        hasil=ps.executeUpdate();
        ps.close();
        return hasil;
    }
}
