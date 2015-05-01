/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccCoa;
import com.ustasoft.pos.domain.view.AccCoaView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class AccCoaDao {
    Connection conn;
    public AccCoaDao(Connection conn){
        this.conn=conn;
    }
    
    public void simpan(AccCoa g, String sOld) throws SQLException {
        String sqlInsert = "INSERT INTO acc_coa("
                + "acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl, "
                + "catatan, saldo_rek_koran, date_ins, user_ins, "
                + "curr_id, acc_group)VALUES (?, ?, ?, ?, ?, ?, ?,  "
                + "?, ?, now(), ?, "
                + "?, ?);";
        
        if(sOld.length() == 0){ // id null artinya record baru
            PreparedStatement ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, g.getAcc_no());
            ps.setString(2, g.getAcc_name());
            ps.setString(3, g.getSub_acc_of());
            ps.setString(4, g.getAcc_type());
            ps.setBoolean(5, g.getActive());
            ps.setDouble(6, g.getSaldo_awal());
            ps.setDate(7, g.getPer_tgl()==null? null: new java.sql.Date(g.getPer_tgl().getTime()));
            ps.setString(8, g.getCatatan());
            ps.setDouble(9, g.getPer_tgl()==null? 0 :g.getSaldo_rek_koran());
            ps.setString(10, MainForm.sUserName);
            ps.setString(11, g.getCurr_id());
            ps.setString(12, g.getAcc_group());
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diinsert : "+hasil);
            
            if(hasil>0){
                System.out.println("Satu record berhasil disimpan!");
            } else {
                System.out.println("Tidak menghasilkan ID baru");
            }
        } else { // record lama, update saja
            String sqlUpdate = "UPDATE acc_coa "
                        + "SET acc_no=?, acc_name=?, sub_acc_of=?, acc_type=?, active=?, saldo_awal=?, "
                        + "per_tgl=?, catatan=?, saldo_rek_koran=?, "
                        + "date_upd=now(), user_upd=?, curr_id=?, acc_group=? "
                        + " WHERE acc_no=?;";
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, g.getAcc_no());
            ps.setString(2, g.getAcc_name());
            ps.setString(3, g.getSub_acc_of());
            ps.setString(4, g.getAcc_type());
            ps.setBoolean(5, g.getActive());
            ps.setDouble(6, g.getSaldo_awal());
            ps.setDate(7, g.getPer_tgl()==null? null: new java.sql.Date(g.getPer_tgl().getTime()));
            ps.setString(8, g.getCatatan());
            ps.setDouble(9, g.getSaldo_rek_koran());
            ps.setString(10, MainForm.sUserName);
            ps.setString(11, g.getCurr_id());
            ps.setString(12, g.getAcc_group());
            ps.setString(13, sOld);
            int hasil = ps.executeUpdate();
            System.out.println("Jumlah row yang berhasil diupdate : "+hasil);
        }
        
    }
    
    public List<AccCoaView> cariSemuaCoa(String sCriteria) throws Exception {
        sCriteria=sCriteria.length()>0? "where acc_no || acc_name || coalesce(t.type_name,'') "
                + "ilike '%"+sCriteria+"%' ": "";
        String sql = "SELECT acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl,  "
                + "coalesce(acc_coa.catatan,'') as catatan, saldo_rek_koran, coalesce(t.type_name,'') as type_name, coalesce(t.group_name,'') as group_name "
                + "FROM acc_coa  "
                + "left join acc_group t on t.type_id=acc_coa.acc_type "
                + sCriteria+" "
                + "order by acc_no";
        List<AccCoaView> hasil = new ArrayList<AccCoaView>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            AccCoaView coa = new AccCoaView();
            coa.setAcc_no(rs.getString("acc_no"));
            coa.setAcc_name(rs.getString("acc_name"));
            coa.setSub_acc_of(rs.getString("sub_acc_of"));
            coa.setAcc_type(rs.getString("acc_type"));
            coa.setActive(rs.getBoolean("active"));
            coa.setSaldo_awal(rs.getDouble("saldo_awal"));
            coa.setPer_tgl(rs.getDate("per_tgl"));
            coa.setCatatan(rs.getString("catatan"));
            coa.setSaldo_rek_koran(rs.getDouble("saldo_rek_koran"));
            coa.setType_name(rs.getString("type_name"));
            coa.setGroup_name(rs.getString("group_name"));
            hasil.add(coa);
        }
        return hasil;
    }
    
    public List<AccCoaView> cariCoaByAccType(String accType) throws Exception {
        String sql = "SELECT acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl,  "
                + "coalesce(acc_coa.catatan,'') as catatan, saldo_rek_koran, coalesce(t.type_name,'') as type_name, coalesce(t.group_name,'') as group_name "
                + "FROM acc_coa  "
                + "left join acc_group t on t.type_id=acc_coa.acc_type "
                + "where acc_coa.acc_type ='"+accType+"' "
                + "order by acc_no";
        List<AccCoaView> hasil = new ArrayList<AccCoaView>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            AccCoaView coa = new AccCoaView();
            coa.setAcc_no(rs.getString("acc_no"));
            coa.setAcc_name(rs.getString("acc_name"));
            coa.setSub_acc_of(rs.getString("sub_acc_of"));
            coa.setAcc_type(rs.getString("acc_type"));
            coa.setActive(rs.getBoolean("active"));
            coa.setSaldo_awal(rs.getDouble("saldo_awal"));
            coa.setPer_tgl(rs.getDate("per_tgl"));
            coa.setCatatan(rs.getString("catatan"));
            coa.setSaldo_rek_koran(rs.getDouble("saldo_rek_koran"));
            coa.setType_name(rs.getString("type_name"));
            coa.setGroup_name(rs.getString("group_name"));
            hasil.add(coa);
        }
        return hasil;
    }
    
    public List<AccCoaView> listKasBank() {
        List<AccCoaView> hasil = new ArrayList<AccCoaView>();
        try {
            String sql = "SELECT acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl,  "
                    + "coalesce(acc_coa.catatan,'') as catatan, saldo_rek_koran, coalesce(t.type_name,'') as type_name, coalesce(t.group_name,'') as group_name "
                    + "FROM acc_coa  "
                    + "left join acc_group t on t.type_id=acc_coa.acc_type "
                    + "where acc_coa.acc_type in('10', '03') "
                    + "and acc_no not in(select distinct coalesce(sub_acc_of,'') from acc_coa) "
                    + "order by acc_no";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while(rs.next()){
                AccCoaView coa = new AccCoaView();
                coa.setAcc_no(rs.getString("acc_no"));
                coa.setAcc_name(rs.getString("acc_name"));
                coa.setSub_acc_of(rs.getString("sub_acc_of"));
                coa.setAcc_type(rs.getString("acc_type"));
                coa.setActive(rs.getBoolean("active"));
                coa.setSaldo_awal(rs.getDouble("saldo_awal"));
                coa.setPer_tgl(rs.getDate("per_tgl"));
                coa.setCatatan(rs.getString("catatan"));
                coa.setSaldo_rek_koran(rs.getDouble("saldo_rek_koran"));
                coa.setType_name(rs.getString("type_name"));
                coa.setGroup_name(rs.getString("group_name"));
                hasil.add(coa);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AccCoaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    public List<AccCoaView> listBiaya() {
        List<AccCoaView> hasil = new ArrayList<AccCoaView>();
        try {
            String sql = "SELECT acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl,  "
                    + "coalesce(acc_coa.catatan,'') as catatan, saldo_rek_koran, coalesce(t.type_name,'') as type_name, coalesce(t.group_name,'') as group_name "
                    + "FROM acc_coa  "
                    + "left join acc_group t on t.type_id=acc_coa.acc_type "
                    + "where t.group_id='5' "
                    + "and acc_no not in(select distinct coalesce(sub_acc_of,'') from acc_coa) "
                    + "order by acc_no";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while(rs.next()){
                AccCoaView coa = new AccCoaView();
                coa.setAcc_no(rs.getString("acc_no"));
                coa.setAcc_name(rs.getString("acc_name"));
                coa.setSub_acc_of(rs.getString("sub_acc_of"));
                coa.setAcc_type(rs.getString("acc_type"));
                coa.setActive(rs.getBoolean("active"));
                coa.setSaldo_awal(rs.getDouble("saldo_awal"));
                coa.setPer_tgl(rs.getDate("per_tgl"));
                coa.setCatatan(rs.getString("catatan"));
                coa.setSaldo_rek_koran(rs.getDouble("saldo_rek_koran"));
                coa.setType_name(rs.getString("type_name"));
                coa.setGroup_name(rs.getString("group_name"));
                hasil.add(coa);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AccCoaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    
    public AccCoaView cariAccCoaByID(String accNo){
        AccCoaView coa = new AccCoaView();
        try {
            String sql = "SELECT acc_no, acc_name, sub_acc_of, acc_type, active, saldo_awal, per_tgl,  "
                    + "coalesce(acc_coa.catatan,'') as catatan, saldo_rek_koran, coalesce(t.type_name,'') as type_name, "
                    + "coalesce(t.group_name,'') as group_name, coalesce(acc_coa.active, false) as active "
                    + "FROM acc_coa  "
                    + "left join acc_group t on t.type_id=acc_coa.acc_type "
                    + "where acc_no='"+accNo+"' "
                    + "order by acc_no";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            if(rs.next()){
                coa.setAcc_no(rs.getString("acc_no"));
                coa.setAcc_name(rs.getString("acc_name"));
                coa.setSub_acc_of(rs.getString("sub_acc_of"));
                coa.setAcc_type(rs.getString("acc_type"));
                coa.setActive(rs.getBoolean("active"));
                coa.setSaldo_awal(rs.getDouble("saldo_awal"));
                coa.setPer_tgl(rs.getDate("per_tgl"));
                coa.setCatatan(rs.getString("catatan"));
                coa.setSaldo_rek_koran(rs.getDouble("saldo_rek_koran"));
                coa.setType_name(rs.getString("type_name"));
                coa.setGroup_name(rs.getString("group_name"));
                coa.setActive(rs.getBoolean("active"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccCoaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return coa;
    }
    
    public int delete(AccCoa g) throws SQLException{
        int hasil=0;
        if(g!=null && g.getAcc_no()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from acc_coa "
                    + "where acc_no=?");
            ps.setString(1, g.getAcc_no());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
