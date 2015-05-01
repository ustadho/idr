/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Relasi;
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
public class RelasiDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    
    public RelasiDao(Connection con){
        this.conn=con;
        
    }
    
    public String getKode() throws SQLException{
        String no="";
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_kode_customer();");
        if(rs.next()){
            no=rs.getString(1);
        }
        rs.close();
        return no;
    }
    
    public void simpan(Relasi k) throws SQLException {
        String sqlInsert = "INSERT INTO m_relasi("
                + "tipe_relasi, nama_relasi, alamat, id_kota, kontak, telp, fax, hp, "
                + "email, acc_no, top, no_npwp, kode, id_kategori, is_cust, is_supp) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?);";
        String sqlUpdate = "UPDATE m_relasi "
                + "SET tipe_relasi=?, nama_relasi=?,  alamat=?, id_kota=?, kontak=?,  "
                + "telp=?, fax=?, hp=?, email=?, acc_no=?, top=?, no_npwp=?, kode=?, "
                + "id_kategori=?, is_cust=?, is_supp=? "
                + " WHERE id_relasi=?;";
        
        PreparedStatement ps = conn.prepareStatement(k.getIdRelasi()== null? sqlInsert: sqlUpdate, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, k.getTipeRelasi());
        ps.setString(2, k.getNamaRelasi());
        ps.setString(3, k.getAlamat());
        ps.setInt(4, k.getIdKota()==null? 0: k.getIdKota());
        ps.setString(5, k.getKontak());
        ps.setString(6, k.getTelp());
        ps.setString(7, k.getFax());
        ps.setString(8, k.getHp());
        ps.setString(9, k.getEmail());
        ps.setString(10, k.getAccNo());
        ps.setInt(11, k.getTop()==null? 0: k.getTop());
        ps.setString(12, k.getNoNpwp());
        ps.setString(13, k.getKode());  
        ps.setInt(14, k.getIdKategori());  
        ps.setBoolean(15, k.isCust());    
        ps.setBoolean(16, k.isSupp());    
        if(k.getIdRelasi()== null){ // id null artinya record baru
            
            //ps.setInt(11, k.getId_supplier());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : "+idBaru);
                k.setIdRelasi(idBaru);
                
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            ps.setInt(17, k.getIdRelasi());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<Relasi> cariSemuaData(int tipeRelasi) throws Exception {
        String sql = "select * from m_relasi "
                + "where case when "+tipeRelasi+"=-1 then true=true else tipe_relasi="+tipeRelasi+" end order by tipe_relasi, upper(nama_relasi)";
        List<Relasi> hasil = new ArrayList<Relasi>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Relasi k = new Relasi();
            k.setIdRelasi(rs.getInt("id_relasi"));
            k.setTipeRelasi(rs.getInt("tipe_relasi"));
            k.setNamaRelasi(rs.getString("nama_relasi"));
            k.setAlamat(rs.getString("alamat"));
            k.setIdKota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setFax(rs.getString("fax"));
            k.setHp(rs.getString("hp"));
            k.setEmail(rs.getString("email"));
            k.setAccNo(rs.getString("acc_no"));
            k.setTop(rs.getInt("top"));
            k.setNoNpwp(rs.getString("no_npwp"));
            k.setKode(rs.getString("kode"));
            k.setCust(rs.getBoolean("is_cust"));
            k.setSupp(rs.getBoolean("is_supp"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public List<Relasi> filterSemuaData(int tipeRelasi, String search) throws Exception {
//        String sql = "select * from m_relasi where tipe_relasi="+tipeRelasi+" "
//                + "and  order by 2";
        String sQry="select s.* "
                + "from m_relasi s "
                + "left join m_kota kota on kota.id=s.id_kota "
                + "where tipe_relasi="+tipeRelasi+" and id_relasi::varchar||coalesce(nama_relasi,'')||"
               + "coalesce(alamat,'')||' - '||coalesce(kota.nama_kota,'') "
                + "ilike '%"+search+"%' order by 2" ;
        
        List<Relasi> hasil = new ArrayList<Relasi>();
        ResultSet rs = conn.createStatement().executeQuery(sQry);
        while(rs.next()){
            Relasi k = new Relasi();
            k.setIdRelasi(rs.getInt("id_relasi"));
            k.setTipeRelasi(rs.getInt("tipe_relasi"));
            k.setNamaRelasi(rs.getString("nama_relasi"));
            k.setAlamat(rs.getString("alamat"));
            k.setIdKota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setFax(rs.getString("fax"));
            k.setHp(rs.getString("hp"));
            k.setEmail(rs.getString("email"));
            k.setAccNo(rs.getString("acc_no"));
            k.setTop(rs.getInt("top"));
            k.setNoNpwp(rs.getString("no_npwp"));
            k.setKode(rs.getString("kode"));
            k.setCust(rs.getBoolean("is_cust"));
            k.setSupp(rs.getBoolean("is_supp"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public Relasi cariByID(Integer id) throws Exception {
        String sql = "select * from m_relasi where id_relasi="+id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Relasi k = new Relasi();
        if(rs.next()){
            k.setIdRelasi(rs.getInt("id_relasi"));
            k.setTipeRelasi(rs.getInt("tipe_relasi"));
            k.setNamaRelasi(rs.getString("nama_relasi"));
            k.setAlamat(rs.getString("alamat"));
            k.setIdKota(rs.getInt("id_kota"));
            k.setKontak(rs.getString("kontak"));
            k.setTelp(rs.getString("telp"));
            k.setFax(rs.getString("fax"));
            k.setHp(rs.getString("hp"));
            k.setEmail(rs.getString("email"));
            k.setAccNo(rs.getString("acc_no"));
            k.setTop(rs.getInt("top"));
            k.setNoNpwp(rs.getString("no_npwp"));
            k.setKode(rs.getString("kode"));
            k.setActive(rs.getBoolean("active"));
            k.setCust(rs.getBoolean("is_cust"));
            k.setSupp(rs.getBoolean("is_supp"));
            k.setIdKategori(rs.getInt("id_kategori"));
        }
        return k;
    }
    
    public int delete(Integer id) throws SQLException{
        int hasil=0;
        if(id!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_relasi "
                    + "where id_relasi=?");
            ps.setInt(1, id);
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }

    public List<Relasi> filter(String sCari) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
