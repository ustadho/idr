/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.AccAlatBayar;
import com.ustasoft.pos.domain.Gudang;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class AccAlatBayarDao {
    private final Connection conn;
    
    public AccAlatBayarDao(Connection con){
        this.conn=con;
    }
    
    public boolean save(String kode, String nama, boolean langsungCair, String sOldKode) throws SQLException{
        boolean isNew=false, sukses=false;
        ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                .executeQuery("select * from acc_alat_bayar where kode='"+sOldKode+"'");
        if(!rs.next()){
            rs.moveToInsertRow();
            isNew=true;
        }
        rs.updateString("kode", kode);
        rs.updateString("alat_bayar", nama);
        rs.updateBoolean("langsung_cair", langsungCair);
        if(isNew){
            rs.insertRow();
        }else{
            rs.updateRow();
        }
        
        sukses=true;
        return sukses;
    }
    
    public AccAlatBayar getByKode(String s) throws  SQLException{
        AccAlatBayar hasil=new AccAlatBayar();
        ResultSet rs=conn.createStatement().executeQuery("SELECT kode, alat_bayar, langsung_cair "
                + "  FROM acc_alat_bayar where kode='"+s+"';");
        if(rs.next()){
            hasil.setKode(rs.getString("kode"));
            hasil.setAlatBayar(rs.getString("alat_bayar"));
            hasil.setLangsungCair(rs.getBoolean("langsung_cair"));
        }
        rs.close();
        return hasil;
    }

    public List<AccAlatBayar> cariSemuaData() throws SQLException{
        List<AccAlatBayar> lst=new ArrayList<AccAlatBayar>();
        ResultSet rs=conn.createStatement().executeQuery("SELECT kode, alat_bayar, langsung_cair "
                + "  FROM acc_alat_bayar order by kode");
        while(rs.next()){
            AccAlatBayar a=new AccAlatBayar();
            a.setKode(rs.getString("kode"));
            a.setAlatBayar(rs.getString("alat_bayar"));
            a.setLangsungCair(rs.getBoolean("langsung_cair"));
            lst.add(a);
        }
        rs.close();
        return lst;
    }

    public void simpan(AccAlatBayar k, String sOldKode) throws SQLException{
        save(k.getKode(), k.getAlatBayar(), k.isLangsungCair(), sOldKode);
    }

    public int delete(AccAlatBayar ab) throws SQLException{
        return conn.createStatement().executeUpdate("delete from acc_alat_bayar where kode='"+ab.getKode()+"'");
    }
    
}
