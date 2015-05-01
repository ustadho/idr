/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.ItemKategori;
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

public class ItemKategoriDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public ItemKategoriDao(Connection con){
        this.conn=con;
    }
    
    public void simpan(ItemKategori k) throws Exception {
        String sqlInsert = "INSERT INTO m_item_kategori("
                + "nama_kategori, keterangan, acc_persediaan, acc_pembelian, "
                + "acc_retur_pembelian, acc_penjualan, acc_retur_penjualan, acc_hpp) "
                + "VALUES (?, ?, ?, ?,  "
                + "?, ?, ?, ?);";
        String sqlUpdate = "UPDATE m_item_kategori "
                + "SET nama_kategori=?, keterangan=?, acc_persediaan=?, acc_pembelian=?, "
                + "acc_retur_pembelian=?, acc_penjualan=?, acc_retur_penjualan=?, "
                + "acc_hpp=? "
                + "WHERE id=?;";
        if(k.getId() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getKetKategori());
            ps.setString(3, k.getAcc_persediaan());
            ps.setString(4, k.getAcc_pembelian());
            ps.setString(5, k.getAcc_retur_pembelian());
            ps.setString(6, k.getAcc_penjualan());
            ps.setString(7, k.getAcc_retur_penjualan());
            ps.setString(8, k.getAcc_hpp());
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
            ps.setString(3, k.getAcc_persediaan());
            ps.setString(4, k.getAcc_pembelian());
            ps.setString(5, k.getAcc_retur_pembelian());
            ps.setString(6, k.getAcc_penjualan());
            ps.setString(7, k.getAcc_retur_penjualan());
            ps.setString(8, k.getAcc_hpp());
            ps.setInt(9, k.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<ItemKategori> cariSemuaKategori() throws Exception {
        String sql = "select * from m_item_kategori order by 2";
        List<ItemKategori> hasil = new ArrayList<ItemKategori>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            ItemKategori k = new ItemKategori();
            k.setId(rs.getInt("id"));
            k.setNamaKategori(rs.getString("nama_kategori"));
            k.setKetKategori(rs.getString("keterangan"));
            k.setAcc_persediaan(rs.getString("acc_persediaan"));
            k.setAcc_pembelian(rs.getString("acc_pembelian"));
            k.setAcc_retur_pembelian(rs.getString("acc_retur_pembelian"));
            k.setAcc_penjualan(rs.getString("acc_penjualan"));
            k.setAcc_retur_penjualan(rs.getString("acc_retur_penjualan"));
            k.setAcc_hpp(rs.getString("acc_hpp"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public ItemKategori cariKategoriByID(Integer id) throws Exception {
        String sql = "select * from m_item_kategori where id="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        ItemKategori k = new ItemKategori();
        if(rs.next()){
            k.setId(rs.getInt("id"));
            k.setNamaKategori(rs.getString("nama_kategori"));
            k.setKetKategori(rs.getString("keterangan"));
            k.setAcc_persediaan(rs.getString("acc_persediaan"));
            k.setAcc_pembelian(rs.getString("acc_pembelian"));
            k.setAcc_retur_pembelian(rs.getString("acc_retur_pembelian"));
            k.setAcc_penjualan(rs.getString("acc_penjualan"));
            k.setAcc_retur_penjualan(rs.getString("acc_retur_penjualan"));
            k.setAcc_hpp(rs.getString("acc_hpp"));
        }
        return k;
    }
    
    public int delete(ItemKategori k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getId()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_item_kategori where id=?");
            ps.setInt(1, k.getId());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
