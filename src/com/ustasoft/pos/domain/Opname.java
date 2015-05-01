/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class Opname {
    Integer id;
    String noBukti;
    Date tanggal;
    Integer idGudang;
    boolean valueAdj;
    String accNo;
    String keterangan;
    String dihitungOleh;
    String userIns;
    Timestamp timeIns;
    List<OpnameDetail> opnameDetail=new ArrayList<OpnameDetail>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNoBukti() {
        return noBukti;
    }

    public void setNoBukti(String noBukti) {
        this.noBukti = noBukti;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Integer getIdGudang() {
        return idGudang;
    }

    public void setIdGudang(Integer idGudang) {
        this.idGudang = idGudang;
    }

    public boolean isValueAdj() {
        return valueAdj;
    }

    public void setValueAdj(boolean valueAdj) {
        this.valueAdj = valueAdj;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getDihitungOleh() {
        return dihitungOleh;
    }

    public void setDihitungOleh(String dihitungOleh) {
        this.dihitungOleh = dihitungOleh;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Timestamp getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Timestamp timeIns) {
        this.timeIns = timeIns;
    }

    public List<OpnameDetail> getOpnameDetail() {
        return opnameDetail;
    }

    public void setOpnameDetail(List<OpnameDetail> opnameDetail) {
        this.opnameDetail = opnameDetail;
    }
    
    
}
