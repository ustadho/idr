/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.RelasiKategori;
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

public class RelasiKategoriDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public RelasiKategoriDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(RelasiKategori k) throws Exception {
        String sqlInsert = "INSERT INTO m_relasi_kategori(nama_kategori, keterangan) VALUES (?, ?);";
        String sqlUpdate = "UPDATE m_relasi_kategori SET nama_kategori=?, keterangan=? "
                + " WHERE id=?;";
        if(k.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getKetKategori());
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
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getKetKategori());
            ps.setInt(3, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<RelasiKategori> cariSemuaKategori() throws Exception {
        String sql = "select * from m_relasi_kategori order by 2";
        List<RelasiKategori> hasil = new ArrayList<RelasiKategori>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            RelasiKategori k = new RelasiKategori();
            k.setId(rs.getInt("id"));
            k.setNamaKategori(rs.getString("nama_kategori"));
            k.setKetKategori(rs.getString("keterangan"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public RelasiKategori cariKategoriByID(Integer id) throws Exception {
        String sql = "select * from m_relasi_kategori where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        RelasiKategori k = new RelasiKategori();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNamaKategori(rs.getString("nama_kategori"));
            k.setKetKategori(rs.getString("keterangan"));
            
        }
        return k;
    }
    
    public int delete(RelasiKategori k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getId()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_relasi_kategori where id=?");
            ps.setInt(1, k.getId());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
