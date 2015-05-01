/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Opname;
import com.ustasoft.pos.domain.OpnameDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class OpnameDao {
    private Connection conn;
    public OpnameDao(Connection c){
        this.conn=c;
    }
    
    public Integer simpan(Opname o){
        try {
            String sql="INSERT INTO opname(\n" +
                    "            no_bukti, tanggal, id_gudang, value_adj, acc_no, keterangan, \n" +
                    "            dihitung_oleh, user_ins)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, \n" + //6
                    "            ?, ?);"; //7
            
            conn.setAutoCommit(false);
            PreparedStatement ps=conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, o.getNoBukti());
            ps.setDate(2, new java.sql.Date(o.getTanggal().getTime()));
            ps.setInt(3, o.getIdGudang());
            ps.setBoolean(4, o.isValueAdj());
            ps.setString(5, o.getAccNo());
            ps.setString(6, o.getKeterangan());
            ps.setString(7, o.getDihitungOleh());
            ps.setString(8, o.getUserIns());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            Integer id=null;
            if(rs.next()){
                id=rs.getInt(1);
                System.out.println("Dapet id: "+id);
            }
            rs.close();
            String sqlDet="INSERT INTO opname_detail(\n" +
                    "           id_opname, id_barang, qty_sekarang, qty_baru, "
                    + "         hpp_sekarang, hpp_baru)\n" +
                    "    VALUES (?, ?, ?, ?, " //4
                    + "         ?, ?);"; //6
            
            
            PreparedStatement psd=conn.prepareStatement(sqlDet);
            for(OpnameDetail d:o.getOpnameDetail()){
                psd.setInt(1, id);
                psd.setInt(2, d.getIdBarang());
                psd.setDouble(3, d.getQtySekarang());
                psd.setDouble(4, d.getQtyBaru());
                psd.setDouble(5, d.getHppSekarang());
                psd.setDouble(6, d.getHppBaru());
                psd.addBatch();
            }
            psd.executeBatch();
            
            rs=conn.createStatement().executeQuery("select fn_opname_process("+id+", '"+MainForm.sUserName+"');");
            if(rs.next()){
                System.out.println("Proses Opname : "+rs.getString(1));
            }
            rs.close();
            conn.setAutoCommit(true);
            return id;
        } catch (SQLException ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex1) {
                Logger.getLogger(OpnameDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            Logger.getLogger(OpnameDao.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return null;
    }
    
    public boolean noBuktiSudahAda(String s){
        boolean b=false;
        try {
            ResultSet rs=conn.createStatement().executeQuery("select * from opname where no_bukti='"+s+"'");
            if(rs.next()){
                b=true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OpnameDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }
}
