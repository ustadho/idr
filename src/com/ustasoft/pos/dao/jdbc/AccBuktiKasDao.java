/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccBuktiKas;
import com.ustasoft.pos.domain.AccBuktiKasDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cak-ust
 */
public class AccBuktiKasDao {
    private Connection conn;
    
    public AccBuktiKasDao(Connection c){
        this.conn=c;
    }
    
    public boolean existsKode(String s){
        
        try {
            ResultSet rs=conn.createStatement().executeQuery("select no_bukti from acc_bukti_kas where no_bukti='"+s+"'");
            if(rs.next()){
                boolean b=true;
                rs.close();
                return b;
            }
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(AccBuktiKasDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public void simpan(AccBuktiKas x){
        String header="INSERT INTO acc_bukti_kas("
                + "no_bukti, acc_no, rate, tanggal, memo, amount, flag, batal, no_cek, " //9
                + "tgl_cek, payee, no_voucher, tipe, diketahui_oleh, diterima_oleh, " //15
                + "dibayar_oleh, terima_kepada, user_ins) " //17
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, "
                + "?, ?, ?);";
        
        
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps=conn.prepareStatement(header);
            ps.setString(1, x.getNoBukti());
            ps.setString(2, x.getAccNo());
            ps.setDouble(3, x.getRate());
            ps.setDate(4, new java.sql.Date(x.getTanggal().getTime()));
            ps.setString(5, x.getMemo());
            ps.setDouble(6, x.getAmount());
            ps.setString(7, x.getFlag());
            ps.setBoolean(8, x.isBatal());
            ps.setString(9, x.getNoCek());
            ps.setDate(10, new java.sql.Date(x.getTglCek().getTime()));
            ps.setString(11, x.getPayee());
            ps.setString(12, x.getNoVoucher());
            ps.setString(13, x.getTipe());
            ps.setString(14, x.getDiketahuiOleh());
            ps.setString(15, x.getDiterimaOleh());
            ps.setString(16, x.getDibayarOleh());
            ps.setString(17, x.getTerimaKepada());
            ps.setString(18, x.getUserIns());
            
            String detail="INSERT INTO acc_bukti_kas_detail(\n" +
                    "no_bukti, acc_no, amount, memo, kode_dept, kode_project, \n" + //6
                    "source_no, tipe)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, \n" +
                    "?, ?);";

            PreparedStatement psd=conn.prepareStatement(detail);
            for(AccBuktiKasDetail d: x.getBuktiKasDetail()){
                psd.setString(1, d.getNoBukti());
                psd.setString(2, d.getAccNo());
                psd.setDouble(3, d.getAmount());
                psd.setString(4, d.getMemo());
                psd.setString(5, d.getKodeDept());
                psd.setString(6, d.getKodeProject());
                psd.setString(7, d.getSourceNo());
                psd.setString(8, d.getTipe());
                psd.addBatch();
            }
            
            ps.executeUpdate();
            psd.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            System.err.println("Error simpan: "+ex.getMessage());
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                
            } catch (SQLException ex1) {
                Logger.getLogger(AccBuktiKasDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(AccBuktiKasDao.class.getName()).log(Level.SEVERE, null, ex.getNextException());
        }
        
    }
    
    public void update(String oldNo, AccBuktiKas x){
        String h="UPDATE acc_bukti_kas\n" +
                "   SET no_bukti=?, acc_no=?, rate=?, tanggal=?, memo=?, amount=?, flag=?, \n" + //7
                "       batal=?, no_cek=?, tgl_cek=?, payee=?, no_voucher=?, tipe=?, \n" + //13
                "       diketahui_oleh=?, diterima_oleh=?, dibayar_oleh=?, terima_kepada=?, " //17
                + "     user_upd=?, time_upd=now()\n" +
                " WHERE no_bukti='"+oldNo+"';";
        try {
            conn.setAutoCommit(false);
            
            PreparedStatement ps=conn.prepareStatement(h);
            ps.setString(1, x.getNoBukti());
            ps.setString(2, x.getAccNo());
            ps.setDouble(3, x.getRate());
            ps.setDate(4, new java.sql.Date(x.getTanggal().getTime()));
            ps.setString(5, x.getMemo());
            ps.setDouble(6, x.getAmount());
            ps.setString(7, x.getFlag());
            ps.setBoolean(8, x.isBatal());
            ps.setString(9, x.getNoCek());
            ps.setDate(10, new java.sql.Date(x.getTglCek().getTime()));
            ps.setString(11, x.getPayee());
            ps.setString(12, x.getNoVoucher());
            ps.setString(13, x.getTipe());
            ps.setString(14, x.getDiketahuiOleh());
            ps.setString(15, x.getDiterimaOleh());
            ps.setString(16, x.getDibayarOleh());
            ps.setString(17, x.getTerimaKepada());
            ps.setString(18, x.getUserUpd());
            
            String detail="INSERT INTO acc_bukti_kas_detail(\n" +
                    "no_bukti, acc_no, amount, memo, kode_dept, kode_project, \n" + //6
                    "source_no, tipe)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, \n" +
                    "?, ?);";

            PreparedStatement psd=conn.prepareStatement(detail);
            for(AccBuktiKasDetail d: x.getBuktiKasDetail()){
                psd.setString(1, d.getNoBukti());
                psd.setString(2, d.getAccNo());
                psd.setDouble(3, d.getAmount());
                psd.setString(4, d.getMemo());
                psd.setString(5, d.getKodeDept());
                psd.setString(6, d.getKodeProject());
                psd.setString(7, d.getSourceNo());
                psd.setString(8, d.getTipe());
                psd.addBatch();
            }
            
            ps.executeUpdate();
            String sDel="delete from acc_bukti_kas_detail where no_bukti='"+x.getNoBukti() +"';";
            System.out.println(sDel);
            int iH=conn.createStatement().executeUpdate(sDel);
            System.out.println(iH+" Item dihapus!");

            psd.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Next Error: "+ex.getNextException());
                
            } catch (SQLException ex1) {
                Logger.getLogger(AccBuktiKasDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            Logger.getLogger(AccBuktiKasDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
