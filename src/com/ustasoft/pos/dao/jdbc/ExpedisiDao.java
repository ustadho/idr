/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Expedisi;
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
//@Repository
public class ExpedisiDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    
    public ExpedisiDao(Connection con){
        this.conn=con;
        
    }
    
    public void simpan(Expedisi k) throws SQLException {
        String sQry = null;
        if(k.getId()==null){
            sQry="INSERT INTO m_expedisi("
                + "nama_expedisi, alamat, id_kota, kontak, telp, fax, hp, email, "
                + "top, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?);";
        }else{
            sQry = "UPDATE m_expedisi "
                + "SET nama_expedisi=?, alamat=?, id_kota=?, kontak=?, telp=?, "
                + "fax=?, hp=?, email=?, top=?, active=? "
                + " WHERE id=?;";
        
        }
        PreparedStatement ps = conn.prepareStatement(sQry, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, k.getNama_expedisi());
        ps.setString(2, k.getAlamat());
        ps.setInt(3, k.getId_kota());
        ps.setString(4, k.getKontak());
        ps.setString(5, k.getTelp());
        ps.setString(6, k.getHp());
        ps.setString(7, k.getFax());
        ps.setString(8, k.getEmail());
        ps.setInt(9, k.getTop());
        ps.setBoolean(10, k.getActive());
        if(k.getId() == null){ // id null artinya record baru
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
        }else{
            ps.setInt(11, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diUpdate : "+hasil);

        }
    }
    
    public List<Expedisi> cariSemuaData() throws Exception {
        String sql = "select * from m_expedisi order by 2";
        List<Expedisi> hasil = new ArrayList<Expedisi>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Expedisi k = new Expedisi();
            k.setId(rs.getInt("id"));
            k.setNama_expedisi(rs.getString("nama_expedisi"));
            k.setAlamat(rs.getString("alamat"));
            k.setId_kota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setHp(rs.getString("hp"));
            k.setFax(rs.getString("fax"));
            k.setEmail(rs.getString("email"));
            k.setTop(rs.getInt("top"));
            k.setActive(rs.getBoolean("active"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Expedisi cariByID(Integer id) throws Exception {
        String sql = "select * from m_expedisi where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Expedisi k = new Expedisi();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNama_expedisi(rs.getString("nama_expedisi"));
            k.setAlamat(rs.getString("alamat"));
            k.setId_kota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setHp(rs.getString("hp"));
            k.setFax(rs.getString("fax"));
            k.setEmail(rs.getString("email"));
            k.setTop(rs.getInt("top"));
            k.setActive(rs.getBoolean("active"));
        }
        return k;
    }
    
    public int delete(Integer id) throws SQLException{
        int hasil=0;
        if(id!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_expedisi where id=?");
            ps.setInt(1, id);
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }

    public List<Expedisi> filterSemuaData(String sCari) throws SQLException{
        String sql = "select * from m_expedisi where nama_expedisi||coalesce(alamat,'') "
                + "ilike '%"+sCari+"%' order by 2";
        List<Expedisi> hasil = new ArrayList<Expedisi>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Expedisi k = new Expedisi();
            k.setId(rs.getInt("id"));
            k.setNama_expedisi(rs.getString("nama_expedisi"));
            k.setAlamat(rs.getString("alamat"));
            k.setId_kota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setHp(rs.getString("hp"));
            k.setFax(rs.getString("fax"));
            k.setEmail(rs.getString("email"));
            k.setTop(rs.getInt("top"));
            k.setActive(rs.getBoolean("active"));
            hasil.add(k);
        }
        return hasil;
    }
}
