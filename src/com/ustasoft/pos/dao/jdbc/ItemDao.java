/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.HargaItem;
import com.ustasoft.pos.domain.Item;
import com.ustasoft.pos.domain.ItemCoa;
import com.ustasoft.pos.domain.ItemSupplier;
import com.ustasoft.pos.domain.view.ItemSupplierView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import pos.ar.FrmPenjualan;

/**
 *
 * @author cak-ust
 */
public class ItemDao {

    private Connection conn;

    public ItemDao(Connection c) {
        this.conn = c;
    }

    public void simpanItem(Item item, ItemCoa coa, boolean insert) throws SQLException {
        String sqlInsert = "INSERT INTO m_item("
                + "plu, barcode, nama_barang, id_kategori, satuan, id_supp_default, active, keterangan, "
                + "acc_persediaan, acc_pembelian, acc_retur_pembelian, "
                + "acc_penjualan, acc_retur_penjualan, acc_hpp, id_satuan, harga_jual, tipe, reorder)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        if (item.getId() == null) { // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getPlu());
            ps.setString(2, item.getBarcode());
            ps.setString(3, item.getNama_barang());
            ps.setInt(4, item.getId_kategori());
            ps.setString(5, item.getSatuan());
            ps.setInt(6, item.getId_supp_default());
            ps.setBoolean(7, item.getActive());
            ps.setString(8, item.getKeterangan());
            ps.setString(9, item.getAccPersediaan());
            ps.setString(10, item.getAccPembelian());
            ps.setString(11, item.getAccReturPembelian());
            ps.setString(12, item.getAccPenjualan());
            ps.setString(13, item.getAccReturPenjualan());
            ps.setString(14, item.getAccHpp());
            ps.setInt(15, item.getIdSatuan());
            ps.setDouble(16, item.getHargaJual());
            ps.setString(17, item.getTipe());
            ps.setInt(18, item.getReorder());

            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : " + hasil);

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Integer idBaru = rs.getInt(1);
                item.setId(idBaru);
//                coa.setId_barang(idBaru);

                System.out.println("ID record baru : " + idBaru);


            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            String sqlUpdate = "UPDATE m_item "
                    + "SET plu=?, barcode=?, nama_barang=?, id_kategori=?, satuan=?, "
                    + "id_supp_default=?, active=?, keterangan=?, acc_persediaan=?, "
                    + "acc_pembelian=?, acc_retur_pembelian=?, acc_penjualan=?, acc_retur_penjualan=?, "
                    + "acc_hpp=?, id_satuan=?, harga_jual=?, tipe=? , reorder=? "
                    + "WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, item.getPlu());
            ps.setString(2, item.getBarcode());
            ps.setString(3, item.getNama_barang());
            ps.setInt(4, item.getId_kategori());
            ps.setString(5, item.getSatuan());
            ps.setInt(6, item.getId_supp_default());
            ps.setBoolean(7, item.getActive());
            ps.setString(8, item.getKeterangan());
            ps.setString(9, item.getAccPersediaan());
            ps.setString(10, item.getAccPembelian());
            ps.setString(11, item.getAccReturPembelian());
            ps.setString(12, item.getAccPenjualan());
            ps.setString(13, item.getAccReturPenjualan());
            ps.setString(14, item.getAccHpp());
            ps.setInt(15, item.getIdSatuan());
            ps.setDouble(16, item.getHargaJual());
            ps.setString(17, item.getTipe());
            ps.setInt(18, item.getReorder());
            ps.setInt(19, item.getId());
            
//            coa.setId_barang(item.getId());
            int hasil = ps.executeUpdate();

            System.out.println("Jumlah row yang berhasil diupdate : " + hasil);
        }
        //simpanCoa(coa, !adaItemAccount(item.getId()));
    }

    public List<Item> tampilkanData(String sCari) throws Exception {
        String sql = "select * from m_item "
                + "where id::varchar ilike '%"+sCari+"%' "
                + "or nama_barang ilike '%"+sCari+"%' "
                + "or plu = '"+sCari+"' "
                + "order by upper(nama_barang)";
        System.out.println(sql);
        List<Item> hasil = new ArrayList<Item>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setPlu(rs.getString("plu"));
            item.setBarcode(rs.getString("barcode"));
            item.setNamaBarang(rs.getString("nama_barang"));
            item.setId_kategori(rs.getInt("id_kategori"));
            item.setSatuan(rs.getString("satuan"));
            item.setId_supp_default(rs.getInt("id_supp_default"));
            item.setActive(rs.getBoolean("active"));
            item.setAccPersediaan(rs.getString("acc_persediaan"));
            item.setAccPembelian(rs.getString("acc_Pembelian"));
            item.setAccReturPembelian(rs.getString("acc_retur_Pembelian"));
            item.setAccPenjualan(rs.getString("acc_Penjualan"));
            item.setAccReturPenjualan(rs.getString("acc_retur_penjualan"));
            item.setAccHpp(rs.getString("acc_hpp"));
            item.setIdSatuan(rs.getInt("id_satuan"));
            item.setHargaJual(rs.getDouble("harga_jual"));
            item.setTipe(rs.getString("tipe"));
            item.setReorder(rs.getInt("reorder"));
            hasil.add(item);
        }
        return hasil;
    }

    public Item cariItemByID(Integer id) throws Exception {
        String sql = "select * from m_item where id=" + id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Item item = new Item();
        if (rs.next()) {
            item.setId(rs.getInt("id"));
            item.setPlu(rs.getString("plu"));
            item.setBarcode(rs.getString("barcode"));
            item.setNamaBarang(rs.getString("nama_barang"));
            item.setId_kategori(rs.getInt("id_kategori"));
            item.setSatuan(rs.getString("satuan"));
            item.setId_supp_default(rs.getInt("id_supp_default"));
            item.setKeterangan(rs.getString("keterangan"));
            item.setActive(rs.getBoolean("active"));
            item.setAccPersediaan(rs.getString("acc_persediaan"));
            item.setAccPembelian(rs.getString("acc_Pembelian"));
            item.setAccReturPembelian(rs.getString("acc_retur_Pembelian"));
            item.setAccPenjualan(rs.getString("acc_Penjualan"));
            item.setAccReturPenjualan(rs.getString("acc_retur_penjualan"));
            item.setAccHpp(rs.getString("acc_hpp"));
            item.setIdSatuan(rs.getInt("id_satuan"));
            item.setHargaJual(rs.getDouble("harga_jual"));
            item.setTipe(rs.getString("tipe"));
            item.setReorder(rs.getInt("reorder"));
        }
        return item;
    }

    public int delete(Integer id) throws SQLException {
        int hasil = 0;
        if (id != null) {
            PreparedStatement ps = conn.prepareStatement("delete from m_item where id=?");
            ps.setInt(1, id);
            hasil = ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }

    public byte[] getImage(Integer id) throws Exception {
        byte[] imgBytes = null;

        ResultSet rs = conn.createStatement().executeQuery("select gambar from m_item_image where id=" + id);
        if (rs.next()) {
            imgBytes = rs.getBytes("gambar");
        }
        return imgBytes;
    }

    public void saveImage(Integer id, String sFotoFile, Icon iconImage) throws SQLException {
        FileInputStream fis;
        boolean isNew = false;

        ResultSet rs = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                .executeQuery("select * from m_item_image where id=" + id);

        if (!rs.next()) {
            isNew = true;
            rs.moveToInsertRow();
        }
        rs.updateInt("id", id);
        
        if (sFotoFile.length() > 0) {
            try {
                File file = new File(sFotoFile);
                fis = new FileInputStream(file);
                rs.updateBinaryStream("gambar", fis, (int) file.length());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if(iconImage==null){
                rs.updateBinaryStream("gambar", null, 0);
            }else{
//                byte[] imgBytes=iconImage.
//                if(imgBytes!=null){
//                    ImageIcon myIcon = new ImageIcon(imgBytes);
//                    ImageIcon bigImage = new ImageIcon(myIcon.getImage().getScaledInstance(360, 360, 8));
//
//                    lblImage.setIcon(bigImage);
//
//                }
            }
        }

        if (isNew) {
            rs.insertRow();

        } else {
            rs.updateRow();
        }
    }

    public byte[] getGambar(Integer id) throws SQLException{
        byte[] result=null;
        ResultSet rs=conn.createStatement().executeQuery("select gambar from m_item_image where id="+id);
        if(rs.next()){
            result=rs.getBytes("gambar");
        }
        rs.close();
        return result;
    }
    
    public void simpanCoa(ItemCoa coa, boolean insert) throws SQLException {
        String sqlInsert = "INSERT INTO m_item_account("
                + "id_barang, acc_persediaan, acc_pembelian, acc_retur_pembelian, "
                + "acc_penjualan, acc_retur_penjualan, acc_hpp) "
                + "VALUES (?, ?, ?, ?,  "
                + "?, ?, ?);";

        if (insert) { // id null artinya record baru
            PreparedStatement ps = null;
//            ResultSet rs = ps.getGeneratedKeys();
//            if(rs.next()){
//                System.out.println("ID record baru : "+coa.getId_barang());
//            } else {
//                System.out.println("Tidak menghasilkan ID baru");
//            }
            ps = conn.prepareStatement(sqlInsert);
            ps.setInt(1, coa.getId_barang());
            ps.setString(2, coa.getAcc_persediaan());
            ps.setString(3, coa.getAcc_pembelian());
            ps.setString(4, coa.getAcc_retur_pembelian());
            ps.setString(5, coa.getAcc_penjualan());
            ps.setString(6, coa.getAcc_retur_penjualan());
            ps.setString(7, coa.getAcc_hpp());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : " + hasil);

        } else { // record lama, update saja
            String sqlUpdate = "UPDATE m_item_account "
                    + "SET acc_persediaan=?, acc_pembelian=?, acc_retur_pembelian=?, "
                    + "acc_penjualan=?, acc_retur_penjualan=?, acc_hpp=? "
                    + " WHERE id_barang=?;";
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);

            ps.setString(1, coa.getAcc_persediaan());
            ps.setString(2, coa.getAcc_pembelian());
            ps.setString(3, coa.getAcc_retur_pembelian());
            ps.setString(4, coa.getAcc_penjualan());
            ps.setString(5, coa.getAcc_retur_penjualan());
            ps.setString(6, coa.getAcc_hpp());
            ps.setInt(7, coa.getId_barang());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : " + hasil);
        }
    }

    public ItemCoa cariItemCoaByID(Integer id) throws Exception {
        String sql = "select * from m_item_account where id_barang=" + id;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        ItemCoa coa = new ItemCoa();
        if (rs.next()) {
            coa.setId_barang(rs.getInt("id_barang"));
            coa.setAcc_persediaan(rs.getString("acc_persediaan"));
            coa.setAcc_pembelian(rs.getString("acc_pembelian"));
            coa.setAcc_retur_pembelian(rs.getString("acc_retur_pembelian"));
            coa.setAcc_penjualan(rs.getString("acc_penjualan"));
            coa.setAcc_retur_penjualan(rs.getString("acc_retur_penjualan"));
            coa.setAcc_hpp(rs.getString("acc_hpp"));

        }
        return coa;
    }

    public void simpanSupplier(List<ItemSupplier> is) throws SQLException {
        if (is.isEmpty()) {
            return;
        }

        conn.setAutoCommit(false);

        String sqlInsert =
                "Delete from m_item_supplier where id_barang=?; \n";
        PreparedStatement ps = conn.prepareStatement(sqlInsert);
        ps.setInt(1, is.get(0).getId_barang());
        ps.executeUpdate();

        sqlInsert =
                "INSERT INTO m_item_supplier(id_barang, id_supplier, harga, disc, ppn, prioritas) "
                + "VALUES (?, ?, ?, ?, ?, ?);";
        int i = 1;
        PreparedStatement ps2 = conn.prepareStatement(sqlInsert);
        for (ItemSupplier item : is) {
            ps2.setInt(1, item.getId_barang());
            ps2.setInt(2, item.getId_supplier());
            ps2.setDouble(3, item.getHarga());
            ps2.setString(4, item.getDisc());
            ps2.setDouble(5, item.getPpn());
            ps2.setInt(6, i);
            i++;
            ps2.addBatch();
        }
        int hasil[] = ps2.executeBatch();
        conn.setAutoCommit(true);
        System.out.println("Jumlah row yang berhasil diinsert : " + hasil.length);

    }

    public List<ItemSupplierView> tampilkanSupplier(Integer id) throws SQLException {
        List<ItemSupplierView> view = new ArrayList<ItemSupplierView>();
        String sQry = "select id_barang, coalesce(i.prioritas,1) as prioritas, "
                + "i.id_supplier, coalesce(s.nama_relasi,'') as nama_supplier, i.harga, coalesce(i.disc, '0') as disc, i.ppn,"
                + "0 as net "
                + "from m_item_supplier i  "
                + "left join m_relasi s on i.id_supplier=s.id_relasi "
                + "where i.id_barang=" + id+ " "
                + "order by coalesce(i.prioritas,1)";
        ResultSet rs = conn.createStatement().executeQuery(sQry);
        while (rs.next()) {
            ItemSupplierView v = new ItemSupplierView();
            v.setPrioritas(rs.getInt("prioritas"));
            v.setId_barang(rs.getInt("id_barang"));
            v.setId_supplier(rs.getInt("id_supplier"));
            v.setNama_supplier(rs.getString("nama_supplier"));
            v.setHarga(rs.getDouble("harga"));
            v.setDisc(rs.getString("disc"));
            v.setPpn(rs.getDouble("PPn"));
            view.add(v);
        }
        rs.close();

        return view;
    }

    public ItemSupplierView cariSupplierById(Integer idBarang, Integer idSupp) throws SQLException {
        ItemSupplierView v = new ItemSupplierView();
        String sQry = "select id_barang, coalesce(i.prioritas,1) as prioritas, "
                + "i.id_supplier, coalesce(s.nama_relasi,'') as nama_relasi, i.harga, "
                + "coalesce(i.disc, '0') as disc, i.ppn, 0 as net "
                + "from m_item_supplier i  "
                + "left join m_relasi s on i.id_supplier=s.id_relasi "
                + "where i.id_barang=" + idBarang+" and i.id_supplier="+idSupp;
        ResultSet rs = conn.createStatement().executeQuery(sQry);
        while (rs.next()) {
            v.setId_barang(rs.getInt("id_barang"));
            v.setId_supplier(rs.getInt("id_supplier"));
            v.setNama_supplier(rs.getString("nama_relasi"));
            v.setHarga(rs.getDouble("harga"));
            v.setDisc(rs.getString("disc"));
            v.setPpn(rs.getDouble("PPn"));
            
        }
        rs.close();

        return v;
    }
    
    private boolean adaItemAccount(Integer id) throws SQLException {
        boolean b = false;
        ResultSet rs = conn.createStatement().executeQuery("select * from m_item_account where id_barang=" + id);
        b = rs.next();
        rs.close();
        return b;
    }
    
    public Double getSaldo(Integer idBarang, Integer idGudang){
        Double hasil=new Double(0);
        try{
            String sQry="select fn_get_saldo_item_gudang("+idBarang+","+idGudang+")";
            System.out.println(sQry);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                hasil=rs.getDouble(1);
            }
            rs.close();
            return hasil;
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        return new Double(0);
    }
    
    public HargaItem getHargaJual(Integer idItem, Integer idCust, Integer idSales) throws SQLException{
        HargaItem harga=new HargaItem();
        ResultSet rs=conn.createStatement().executeQuery(
                "select * from fn_get_harga_jual("+idItem+", "+idCust+", "+idSales+") as ("
                + "price double precision, disc text, ppn double precision)");
        if(rs.next()){
            
            harga.setPrice(rs.getDouble("price"));
            harga.setDisc(rs.getString("disc"));
            harga.setPpn(rs.getDouble("ppn"));
        }
        rs.close();
        
        return harga;
    }

    public Double getPrevCost(int id) {
        Double hasil=new Double(0);
        try {
            String sQry="select fn_item_hist_get_prevcost_sales("+id+")";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                hasil=rs.getDouble(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    public Double getPrevCostAvg(int id) {
        Double hasil=new Double(0);
        try {
            String sQry="select fn_item_get_last_cost_avg("+id+")";
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                hasil=rs.getDouble(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }

    public void setConn(Connection conn) {
        this.conn=conn;
    }
}
