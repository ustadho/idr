/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class AccBuktiKas {
    String noBukti;
    String accNo;
    double rate;
    private Date tanggal;
    private String memo;
    private double amount;
    private String flag;
    private boolean batal;
    private String noCek;
    private Date tglCek;
    private String payee;
    private String noVoucher;
    private String tipe;
    private String diketahuiOleh;
    private String diterimaOleh;
    private String dibayarOleh;
    private String terimaKepada;
    private List<AccBuktiKasDetail> buktiKasDetail=new ArrayList<AccBuktiKasDetail>();
    private String userIns;
    private Date timeIns;
    private String userUpd;
    private Date timeUpd;
    
    public AccBuktiKas() {
    }
    public String getNoBukti() {
        return noBukti;
    }

    public void setNoBukti(String noBukti) {
        this.noBukti = noBukti;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isBatal() {
        return batal;
    }

    public void setBatal(boolean batal) {
        this.batal = batal;
    }

    public String getNoCek() {
        return noCek;
    }

    public void setNoCek(String noCek) {
        this.noCek = noCek;
    }

    public Date getTglCek() {
        return tglCek;
    }

    public void setTglCek(Date tglCek) {
        this.tglCek = tglCek;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getNoVoucher() {
        return noVoucher;
    }

    public void setNoVoucher(String noVoucher) {
        this.noVoucher = noVoucher;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getDiketahuiOleh() {
        return diketahuiOleh;
    }

    public void setDiketahuiOleh(String diketahuiOleh) {
        this.diketahuiOleh = diketahuiOleh;
    }

    public String getDiterimaOleh() {
        return diterimaOleh;
    }

    public void setDiterimaOleh(String diterimaOleh) {
        this.diterimaOleh = diterimaOleh;
    }

    public String getDibayarOleh() {
        return dibayarOleh;
    }

    public void setDibayarOleh(String dibayarOleh) {
        this.dibayarOleh = dibayarOleh;
    }

    public String getTerimaKepada() {
        return terimaKepada;
    }

    public void setTerimaKepada(String terimaKepada) {
        this.terimaKepada = terimaKepada;
    }

    public List<AccBuktiKasDetail> getBuktiKasDetail() {
        return buktiKasDetail;
    }

    public void setBuktiKasDetail(List<AccBuktiKasDetail> buktiKasDetail) {
        this.buktiKasDetail = buktiKasDetail;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Date getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Date timeIns) {
        this.timeIns = timeIns;
    }

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }

    public Date getTimeUpd() {
        return timeUpd;
    }

    public void setTimeUpd(Date timeUpd) {
        this.timeUpd = timeUpd;
    }
    
    
}
