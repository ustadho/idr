/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class AccJurnalDetail {
    private String journalNo;
    private String accNo;
    private double debit;
    private double credit;
    private double rate;
    private double primeAmount;
    private String memo;
    private String kodeDept;
    private String kodeProject;
    private Integer serialNo;

    public String getJournalNo() {
        return journalNo;
    }

    public void setJournalNo(String journalNo) {
        this.journalNo = journalNo;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getPrimeAmount() {
        return primeAmount;
    }

    public void setPrimeAmount(double primeAmount) {
        this.primeAmount = primeAmount;
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

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }
    
    
}
