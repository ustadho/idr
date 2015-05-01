/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Sales;
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
//@Repository
public class SalesDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    
    public SalesDao(Connection con){
        this.conn=con;
        
    }
    
    public void simpan(Sales k) throws SQLException {
        String sqlInsert = "INSERT INTO m_salesman("
                + "nama_sales, alamat, id_kota, telp, fax, hp, email, "
                + "no_npwp, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,  "
                + "?, ?);";
        String sqlUpdate = "UPDATE m_salesman "
                + "SET nama_sales=?, alamat=?, id_kota=?, telp=?, fax=?, "
                + "hp=?, email=?, no_npwp=?, active=? "
                + "WHERE id_sales=?;";
        
        if(k.getId_sales() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, k.getNama_sales());
            ps.setString(2, k.getAlamat());
            ps.setInt(3, k.getId_kota());
            ps.setString(4, k.getTelp());
            ps.setString(5, k.getFax());
            ps.setString(6, k.getHp());
            ps.setString(7, k.getEmail());
            ps.setString(8, k.getNo_npwp());
            ps.setBoolean(9, k.getActive());
            //ps.setInt(11, k.getId_supplier());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : "+idBaru);
                k.setId_sales(idBaru);
                
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, k.getNama_sales());
            ps.setString(2, k.getAlamat());
            ps.setInt(3, k.getId_kota());
            ps.setString(4, k.getTelp());
            ps.setString(5, k.getFax());
            ps.setString(6, k.getHp());
            ps.setString(7, k.getEmail());
            ps.setString(8, k.getNo_npwp());
            ps.setBoolean(9, k.getActive());
            ps.setInt(10, k.getId_sales());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Sales> cariSemuaData() throws Exception {
        String sql = "select * from m_salesman where active order by 2";
        List<Sales> hasil = new ArrayList<Sales>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Sales k = new Sales();
            k.setId_sales(rs.getInt("id_sales"));
            k.setNama_sales(rs.getString("nama_sales"));
            k.setAlamat(rs.getString("alamat"));
            k.setId_kota(rs.getInt("id_kota"));
            k.setTelp(rs.getString("telp"));
            k.setFax(rs.getString("fax"));
            k.setHp(rs.getString("hp"));
            k.setEmail(rs.getString("email"));
            k.setNo_npwp(rs.getString("no_npwp"));
            k.setActive(rs.getBoolean("active"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Sales cariByID(Integer id) throws Exception {
        String sql = "select * from m_salesman where id_sales="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Sales k = new Sales();
        if(rs.next()){
            k.setId_sales(rs.getInt("id_sales"));
            k.setNama_sales(rs.getString("nama_sales"));
            k.setAlamat(rs.getString("alamat"));
            k.setId_kota(rs.getInt("id_kota"));
            k.setTelp(rs.getString("telp"));
            k.setFax(rs.getString("fax"));
            k.setHp(rs.getString("hp"));
            k.setEmail(rs.getString("email"));
            k.setNo_npwp(rs.getString("no_npwp"));
            k.setActive(rs.getBoolean("active"));
        }
        return k;
    }
    
    public int delete(Integer id) throws SQLException{
        int hasil=0;
        if(id!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_salesman "
                    + "where id_sales=?");
            ps.setInt(1, id);
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
