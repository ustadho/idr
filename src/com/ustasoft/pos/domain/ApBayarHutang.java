/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author cak-ust
 */
public class ApBayarHutang {
    private String noBayar;
    private Date tanggal;
    private String keterangan;
    private Integer idRelasi;
    private String tipeRelasi;
    private String userIns;
    private Timestamp time_ins;
    private String userUpd;
    private Timestamp timeUpd;

    public String getNoBayar() {
        return noBayar;
    }

    public void setNoBayar(String noBayar) {
        this.noBayar = noBayar;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Integer getIdRelasi() {
        return idRelasi;
    }

    public void setKodeRelasi(Integer idRelasi) {
        this.idRelasi = idRelasi;
    }

    public String getTipeRelasi() {
        return tipeRelasi;
    }

    public void setTipeRelasi(String tipeRelasi) {
        this.tipeRelasi = tipeRelasi;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Timestamp getTimeIns() {
        return time_ins;
    }

    public void setTime_ins(Timestamp time_ins) {
        this.time_ins = time_ins;
    }

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }

    public Timestamp getTimeUpd() {
        return timeUpd;
    }

    public void setTimeUpd(Timestamp timeUpd) {
        this.timeUpd = timeUpd;
    }
    
    
}
