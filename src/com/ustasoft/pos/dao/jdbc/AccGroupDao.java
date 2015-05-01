/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccGroup;
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
public class AccGroupDao {
    Connection conn;
    public AccGroupDao(Connection conn){
        this.conn=conn;
    }
    
    public void simpan(AccGroup g) throws Exception {
        String sqlInsert = "INSERT INTO acc_group(type_id, type_name, catatan, group_id, group_name)"
                + "    VALUES (?, ?, ?, ?, ?);";
        String sqlUpdate = "UPDATE acc_group "
                + "   SET type_id=?, type_name=?, catatan=?, group_id=?, group_name=? "
                + " where type_id=?";
        if(g.getType_id() == null){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, g.getType_id());
            ps.setString(2, g.getType_name());
            ps.setString(3, g.getCatatan());
            ps.setString(4, g.getGroup_id());
            ps.setString(5, g.getGroup_name());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            if(hasil>0){
                System.out.println("Satu record berhasil disimpan!");
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, g.getType_id());
            ps.setString(2, g.getType_name());
            ps.setString(3, g.getCatatan());
            ps.setString(4, g.getGroup_id());
            ps.setString(5, g.getGroup_name());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<AccGroup> cariSemuaAccGroup() throws Exception {
        String sql = "select * from acc_group order by 1";
        List<AccGroup> hasil = new ArrayList<AccGroup>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            AccGroup k = new AccGroup();
            k.setType_id(rs.getString("type_id"));
            k.setType_name(rs.getString("type_name"));
            k.setCatatan(rs.getString("catatan"));
            k.setGroup_id(rs.getString("group_id"));
            k.setGroup_name(rs.getString("group_name"));
            hasil.add(k);
        }
        return hasil;
    }
    
    public AccGroup cariAccGroupByID(String id) throws Exception {
        String sql = "select * from acc_group where type_id='"+id+"'";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        AccGroup k = new AccGroup();
        if(rs.next()){
            k.setType_id(rs.getString("type_id"));
            k.setType_name(rs.getString("type_name"));
            k.setCatatan(rs.getString("catatan"));
            k.setGroup_id(rs.getString("group_id"));
            k.setGroup_name(rs.getString("group_name"));
            
        }
        return k;
    }
    
    public int delete(AccGroup g) throws SQLException{
        int hasil=0;
        if(g!=null && g.getType_id()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from acc_group where type_id=?");
            ps.setString(1, g.getType_id());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
