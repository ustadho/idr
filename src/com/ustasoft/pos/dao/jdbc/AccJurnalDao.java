/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccJurnal;
import com.ustasoft.pos.domain.AccJurnalDetail;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author cak-ust
 */
public class AccJurnalDao {
    private Connection conn;
    private Component aThis;
    
    public AccJurnalDao(Connection c, Component t){
        this.conn=c;
        this.aThis=t;
    }
        
    public void simpanJurnal(AccJurnal j){
        try {
            conn.setAutoCommit(false);
            
            PreparedStatement ps=conn.prepareStatement("INSERT INTO acc_journal(\n" + 
                    "            journal_no, tanggal, description, multi_curr, is_saldo_awal, \n" + //5
                    "            user_ins, tipe, source_no, total) \n" + //8
                    "    VALUES (?, ?, ?, ?, ?, \n" +
                    "            ?, ?, ?, ?)");
            ps.setString(1, j.getJournalNo());
            ps.setDate(2, new java.sql.Date(j.getTanggal().getTime()));
            ps.setString(3, j.getDescription());
            ps.setBoolean(4, j.isMultiCurr());
            ps.setBoolean(5, j.isIsSaldoAwal());
            ps.setString(6, j.getUserIns());
            ps.setString(7, j.getTipe());
            ps.setString(8, j.getSourceNo());
            ps.setDouble(9, j.getTotal());
            
            String sqd="INSERT INTO acc_journal_detail(\n" +
                        "            journal_no, acc_no, debit, credit, rate, prime_amount, memo, \n" +
                        "            kode_dept, kode_project)\n" +
                        "    VALUES (?, ?, ?, ?, ?, ?, ?, \n" + //7
                    "            ?, ?);";
            PreparedStatement psd=conn.prepareStatement(sqd);
            
            for(AccJurnalDetail d: j.getListJurnal()){
                psd.setString(1, d.getJournalNo());
                psd.setString(2, d.getAccNo());
                psd.setDouble(3, d.getDebit());
                psd.setDouble(4, d.getCredit());
                psd.setDouble(5, d.getRate());
                psd.setDouble(6, d.getPrimeAmount());
                psd.setString(7, d.getMemo());
                psd.setString(8, d.getKodeDept());
                psd.setString(9, d.getKodeProject());
                psd.addBatch();
            }
            ps.executeUpdate();
            psd.executeBatch();
            conn.setAutoCommit(true);
            
            JOptionPane.showMessageDialog(aThis, "Simpan jurnal sukses!");
        } catch (SQLException ex) {
            Logger.getLogger(AccJurnalDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isExistsJournalNo(String sNo){
        try {
            ResultSet rs=conn.createStatement().executeQuery("select * from acc_journal where journal_no='"+sNo+"'");
            if(rs.next()){
                rs.close();
                return true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccJurnalDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public AccJurnal getByJurnalNo(String s){
        AccJurnal result=null;
        ResultSet rs;
        try {
            String sql="select * from acc_journal where journal_no='"+s+"'";
            System.out.println(sql);
            rs = conn.createStatement().executeQuery(sql);
            if(rs.next()){
                result=new AccJurnal();
                result.setJournalNo(rs.getString("journal_no"));
                result.setTanggal(rs.getDate("tanggal"));
                result.setDescription(rs.getString("description"));
                result.setMultiCurr(rs.getBoolean("multi_curr"));
                result.setIsSaldoAwal(rs.getBoolean("is_saldo_awal"));
                result.setIsClosed(rs.getBoolean("is_closed"));
                result.setDateIns(rs.getTimestamp("date_ins"));
                result.setUserIns(rs.getString("user_ins"));
                result.setDateUpd(rs.getTimestamp("date_upd"));
                result.setUserUpd(rs.getString("user_upd"));
                result.setTipe(rs.getString("tipe"));
                result.setSourceNo(rs.getString("source_no"));
                result.setIsKoreksi(rs.getBoolean("is_koreksi"));
                result.setKoreksiDari(rs.getString("koreksi_dari"));
                result.setTotal(rs.getDouble("total"));
                rs.close();
                List<AccJurnalDetail> lstD=new ArrayList<AccJurnalDetail>();
                rs=conn.createStatement().executeQuery("select * from acc_journal_detail where journal_no='"+s+"' "
                        + "order by serial_no");
                while(rs.next()){
                    AccJurnalDetail d=new AccJurnalDetail();
                    d.setJournalNo(rs.getString("journal_no"));
                    d.setAccNo(rs.getString("acc_no"));
                    d.setDebit(rs.getDouble("debit"));
                    d.setCredit(rs.getDouble("credit"));
                    d.setRate(rs.getDouble("rate"));
                    d.setPrimeAmount(rs.getDouble("prime_amount"));
                    d.setMemo(rs.getString("memo"));
                    d.setKodeDept(rs.getString("kode_dept"));
                    d.setSerialNo(rs.getInt("serial_no"));
                    
                    lstD.add(d);
                }
                rs.close();
                result.setListJurnal(lstD);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AccJurnalDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public boolean isExists(String journalNo){
        try {
            ResultSet rs=conn.createStatement().executeQuery("select * from acc_journal where journal_no='"+journalNo+"'");
            if(rs.next()){
                rs.close();
                return true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccJurnalDao.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false; 
    }
    
    
}
