/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class AccJurnal {
    private String journalNo;
    private Date tanggal;
    private String description;
    private boolean multiCurr;
    private boolean isSaldoAwal;
    private boolean isClosed;
    private Timestamp dateIns;
    private String userIns;
    private Timestamp dateUpd;
    private String userUpd;
    private String tipe;
    private String sourceNo;
    private boolean isKoreksi;
    private String koreksiDari;
    private double total;
    private List<AccJurnalDetail> listJurnal;

    public String getJournalNo() {
        return journalNo;
    }

    public void setJournalNo(String journalNo) {
        this.journalNo = journalNo;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMultiCurr() {
        return multiCurr;
    }

    public void setMultiCurr(boolean multiCurr) {
        this.multiCurr = multiCurr;
    }

    public boolean isIsSaldoAwal() {
        return isSaldoAwal;
    }

    public void setIsSaldoAwal(boolean isSaldoAwal) {
        this.isSaldoAwal = isSaldoAwal;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Date getDateIns() {
        return dateIns;
    }

    public void setDateIns(Timestamp dateIns) {
        this.dateIns = dateIns;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Timestamp getDateUpd() {
        return dateUpd;
    }

    public void setDateUpd(Timestamp dateUpd) {
        this.dateUpd = dateUpd;
    }

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public boolean isIsKoreksi() {
        return isKoreksi;
    }

    public void setIsKoreksi(boolean isKoreksi) {
        this.isKoreksi = isKoreksi;
    }

    public String getKoreksiDari() {
        return koreksiDari;
    }

    public void setKoreksiDari(String koreksiDari) {
        this.koreksiDari = koreksiDari;
    }

    public List<AccJurnalDetail> getListJurnal() {
        return listJurnal;
    }

    public void setListJurnal(List<AccJurnalDetail> listJurnal) {
        this.listJurnal = listJurnal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    
}
