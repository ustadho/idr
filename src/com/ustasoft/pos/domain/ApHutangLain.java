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
public class ApHutangLain {
    private String noAp;
    private Date tanggal;
    private Integer idRelasi;
    private Boolean saldoAw;
    private String keterangan;
    private String akunDebet;
    private String akunKasBank;
    private Double total;
    private String userIns;
    private Timestamp timeIns;
    private String tipeRelasi;
    private Timestamp timeUpd;
    private String userUpd;
    
    public String getNoAp() {
        return noAp;
    }

    public void setNoAp(String noAp) {
        this.noAp = noAp;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Integer getIdRelasi() {
        return idRelasi;
    }

    public void setIdRelasi(Integer idRelasi) {
        this.idRelasi = idRelasi;
    }

    public Boolean getSaldoAw() {
        return saldoAw;
    }

    public void setSaldoAw(Boolean saldoAw) {
        this.saldoAw = saldoAw;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getAkunDebet() {
        return akunDebet;
    }

    public void setAkunDebet(String akunDebet) {
        this.akunDebet = akunDebet;
    }

    public String getAkunKasBank() {
        return akunKasBank;
    }

    public void setAkunKasBank(String akunKasBank) {
        this.akunKasBank = akunKasBank;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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

    public String getTipeRelasi() {
        return tipeRelasi;
    }

    public void setTipeRelasi(String tipeRelasi) {
        this.tipeRelasi = tipeRelasi;
    }

    public void setUserUpd(String string) {
        this.userUpd=string;
    }
    
    public void setTimeUpd(Timestamp timeIns) {
        this.timeUpd = timeIns;
    }
}
