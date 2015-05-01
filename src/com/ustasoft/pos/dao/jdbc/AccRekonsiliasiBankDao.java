/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccJurnalDetail;
import com.ustasoft.pos.domain.AccRekonsiliasi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cak-ust
 */
public class AccRekonsiliasiBankDao {
    private Connection conn;
    public AccRekonsiliasiBankDao(Connection c){
        this.conn=c;
    }
    
    public Integer simpan(AccRekonsiliasi x, List<AccJurnalDetail> listJurnal){
        Integer newId=null;
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps=conn.prepareStatement("INSERT INTO acc_reconsile(\n" +
                    "            acc_no, tanggal, saldo_rek_koran, user_ins)\n" +
                    "    VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, x.getAccNo());
            ps.setDate(2, new java.sql.Date(x.getTanggal().getTime()));
            ps.setDouble(3, x.getSaldoRekKoran());
            ps.setString(4, x.getUserIns());
            
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                newId=rs.getInt(1);
            }
            rs.close();
            PreparedStatement psd=conn.prepareStatement("update acc_journal_detail set reconsile_id="+newId+" "
                    + "where acc_no=? and journal_no=?");
            for(AccJurnalDetail d: listJurnal){
                psd.setString(1, d.getAccNo());
                psd.setString(2, d.getJournalNo());
                psd.addBatch();
            }
            psd.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex1) {
                Logger.getLogger(AccRekonsiliasiBankDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            Logger.getLogger(AccRekonsiliasiBankDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return newId;
    }
    
    public double getSaldoBefore(String accNo){
        double saldo=0;
        try {
            ResultSet rs=conn.createStatement().executeQuery("select fn_acc_reconsile_saldo_before('"+accNo+"')");
            if(rs.next()){
                saldo=rs.getDouble(1);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccRekonsiliasiBankDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }
    
    public Date lastReconsile(String accNo){
        Date ret=null;
        try {
            ResultSet rs=conn.createStatement().executeQuery("select max(tanggal) from acc_reconsile where acc_no='"+accNo+"'");
            if(rs.next()){
                ret=rs.getDate(1);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccRekonsiliasiBankDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    } 
}
