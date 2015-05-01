/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.PenerimaanStok;
import com.ustasoft.pos.domain.PenerimaanStokDetail;
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
public class StockDao {

    private Connection conn;

    public StockDao(Connection con) {
        this.conn = con;
    }

    public void setConn(Connection con) {
        this.conn = con;
    }

    public List<TrxType> cariSemuaStokTipeByInOut(String inOut) throws Exception {
        List<TrxType> stok = new ArrayList<TrxType>();
        ResultSet rs = conn.createStatement().executeQuery("select * from m_trx_type "
                + "where in_out='" + inOut + "' and planned=true order by 2");
        while (rs.next()) {
            TrxType s = new TrxType();
            s.setKode(rs.getString("kode"));
            s.setKeterangan(rs.getString("keterangan"));
            s.setInOut(rs.getString("in_out"));
            s.setPlanned(rs.getBoolean("planned"));
            stok.add(s);
        }
        rs.close();
        return stok;
    }

    public List<TrxType> cariSemuaStokTipeUnplanedByInOut(String inOut) throws Exception {
        List<TrxType> stok = new ArrayList<TrxType>();
        ResultSet rs = conn.createStatement().executeQuery("select * from m_trx_type "
                + "where in_out='" + inOut + "' and planned<>true order by 2");
        while (rs.next()) {
            TrxType s = new TrxType();
            s.setKode(rs.getString("kode"));
            s.setKeterangan(rs.getString("keterangan"));
            s.setInOut(rs.getString("in_out"));
            s.setPlanned(rs.getBoolean("planned"));
            stok.add(s);
        }
        rs.close();
        return stok;
    }
    
    public void simpanMutasiStokHeader(PenerimaanStok header) throws SQLException {
        String sQry = "";
        if (header.getId() == null) { //insert
            sQry = "INSERT INTO mutasi_stok("
                    + "trx_type, id_gudang, tanggal, description, user_ins, time_ins, "
                    + "user_upd, time_upd, reff_no) "
                    + "VALUES (?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?);";
        } else {
            sQry = "UPDATE mutasi_stok "
                    + "   SET trx_type=?, id_gudang=?, tanggal=?, description=?, user_ins=?, "
                    + "       time_ins=?, user_upd=?, time_upd=?, reff_no=? "
                    + " WHERE id=?;";
        }
        PreparedStatement ps = conn.prepareStatement(sQry, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, header.getTrxType());
        ps.setInt(2, header.getIdGudang());
        ps.setDate(3, new java.sql.Date(header.getTanggal().getTime()));
        ps.setString(4, header.getDescription());
        ps.setString(5, header.getUserIns());
        ps.setDate(6, new java.sql.Date(header.getTimeIns().getTime()));
        ps.setString(7, header.getUserUpd());
        ps.setDate(8, new java.sql.Date(header.getTimeUpd().getTime()));
        ps.setString(9, header.getReffNo());

        if (header.getId() == null) { // id null artinya record baru
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : " + hasil);

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : " + idBaru);
                header.setId(idBaru);

            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else {
            ps.setInt(9, header.getId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diUpdate : " + hasil);

        }
    }

    public void simpanMutasiStokDetail(List<PenerimaanStokDetail> detail) throws SQLException {
        String sQry = "";
        sQry = "INSERT INTO mutasi_stok_det("
                    + "id, id_barang, qty, harga, ket_item) "
                    + "VALUES (?, ?, ?, ?, ?);";
        
        PreparedStatement ps = conn.prepareStatement(sQry, PreparedStatement.RETURN_GENERATED_KEYS);
        for(PenerimaanStokDetail d:detail){
            ps.setInt(1, d.getId());
            ps.setInt(2, d.getIdBarang());
            ps.setDouble(3, d.getQty());
            ps.setDouble(4, d.getHarga());
            ps.setString(5, d.getKetItem());
            ps.addBatch();
        }
        
        int iRow[]=ps.executeBatch();
        System.out.println(iRow.length+" Baris sudah diinsert!");
    }

    public String getNoBuktiPenerimaan(String sKode) throws SQLException{
        String nomor=null;
        ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_mutasi_stok('"+sKode+"')");
        if(rs.next()){
            nomor=rs.getString(1);
        }
        rs.close();
        return nomor;
    }
}
