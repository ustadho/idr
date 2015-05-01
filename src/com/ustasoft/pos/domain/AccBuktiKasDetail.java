/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

import java.util.Date;


/**
 *
 * @author cak-ust
 */
public class AccBuktiKasDetail {
    private String noBukti;
    private String accNo;
    private double amount;
    private String memo;
    private String kodeDept;
    private String kodeProject;
    private int serialNo;
    private Date timeIns;
    private String sourceNo;
    private String tipe;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getKodeDept() {
        return kodeDept;
    }

    public void setKodeDept(String kodeDept) {
        this.kodeDept = kodeDept;
    }

    public String getKodeProject() {
        return kodeProject;
    }

    public void setKodeProject(String kodeProject) {
        this.kodeProject = kodeProject;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public Date getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Date timeIns) {
        this.timeIns = timeIns;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
    
    
}
