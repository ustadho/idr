/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.ItemHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author cak-ust
 */
public class ItemHistoryDao {
    private Connection conn;
    public void setConn(Connection con){
        this.conn=con;
    }
    
    public ItemHistory cariHistoriTerakhir(Integer id) throws Exception{
        ItemHistory item=new ItemHistory();
        ResultSet rs=conn.createStatement().executeQuery("select * from item_history where id_barang="+id+" "
                + "order by txdate desc, itemhistid desc limit 1");
        if(rs.next()){
            item.setItemHistId(rs.getInt("itemhistid"));
            item.setIdBarang(rs.getInt("id_barang"));
            item.setItemNo(rs.getString("itemno"));
            item.setTxDate(rs.getDate("txdate"));
            item.setTxType(rs.getString("txtype"));
            item.setInvoiceid(rs.getInt("invoiceid"));
            item.setDescription(rs.getString("description"));
            item.setQuantity(rs.getDouble("quantity"));
            item.setSellingprice(rs.getDouble("sellingprice"));
            item.setItemCost(rs.getDouble("item_cost"));
            item.setCostAvr(rs.getDouble("costavr"));
            item.setPrevCost(rs.getDouble("prevcost"));
            item.setPrevQty(rs.getDouble("prevqty"));
            item.setGlPeriod(rs.getInt("glperiod"));
            item.setGlYear(rs.getInt("glyear"));
            item.setWarehouseId(rs.getInt("warehouseid"));
            item.setFifoHistId(rs.getInt("fifohistid"));
            item.setFifoStart(rs.getInt("fifostart"));
            item.setQtyControl(rs.getDouble("qtycontrol"));
        }
        rs.close();
        return item;
    }
    
    private int simpanItemHistory(ItemHistory item) throws SQLException{
        String sQry="";
        if(item.getItemHistId()==null){
            sQry="INSERT INTO itemhistory("
                    + "id_barang, itemno, txdate, txtype, invoiceid, description, "
                    + "quantity, sellingprice, item_cost, costavr, prevcost, prevqty, "
                    + "glperiod, glyear, warehouseid, fifohistid, fifostart, qtycontrol)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?);";
        }else{
            sQry="UPDATE itemhistory "
                    + "SET id_barang=?, itemno=?, txdate=?, txtype=?, invoiceid=?, "
                    + "description=?, quantity=?, sellingprice=?, item_cost=?, costavr=?, "
                    + "prevcost=?, prevqty=?, glperiod=?, glyear=?, warehouseid=?, fifohistid=?, "
                    + "fifostart=?, qtycontrol=? "
                    + "WHERE itemhistid=?";
        }
        PreparedStatement ps = conn.prepareStatement(sQry, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, item.getIdBarang());
        ps.setString(2, item.getItemNo());
        ps.setDate(3, new java.sql.Date(item.getTxDate().getTime()));
        ps.setString(4, item.getTxType());
        ps.setInt(5, item.getInvoiceid());
        ps.setString(6, item.getDescription());
        ps.setDouble(7, item.getQuantity());
        ps.setDouble(8, item.getSellingprice());
        ps.setDouble(9, item.getItemCost());
        ps.setDouble(10, item.getCostAvr());
        ps.setDouble(11, item.getPrevCost());
        ps.setDouble(12, item.getPrevQty());
        ps.setInt(13, item.getGlPeriod());
        ps.setInt(14, item.getGlYear());
        ps.setInt(15, item.getWarehouseId());
        ps.setInt(16, item.getFifoHistId());
        ps.setInt(17, item.getFifoStart());
        ps.setDouble(18, item.getQtyControl());

        if (item.getItemHistId() == null) { // id null artinya record baru
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : " + hasil);
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Integer idBaru = rs.getInt(1);
                System.out.println("ID record baru : " + idBaru);
                item.setItemHistId(idBaru);
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else {
            ps.setInt(9, item.getItemHistId());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diUpdate : " + hasil);
        }
        return 0;
    }
}
