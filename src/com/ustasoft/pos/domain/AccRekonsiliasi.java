/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author cak-ust
 */
public class AccRekonsiliasi {
    private Integer id;
    private String accNo;
    private Date tanggal;
    private Double saldoRekKoran = new Double(0);
    private String userIns;
    private Timestamp timeIns;
    private String userUpd;
    private Timestamp timeUpd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Double getSaldoRekKoran() {
        return saldoRekKoran;
    }

    public void setSaldoRekKoran(Double saldoRekKoran) {
        this.saldoRekKoran = saldoRekKoran;
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
