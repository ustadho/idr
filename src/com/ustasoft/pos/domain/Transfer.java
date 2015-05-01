/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

import java.sql.Timestamp;

/**
 *
 * @author cak-ust
 */
public class Transfer {
    private Integer id;
    private Timestamp tanggal;
    private String description;
    private Integer fromGudang;
    private Integer toGudang;
    private String userIns;
    private Timestamp timeIns;
    private String userUpd;
    private Timestamp timeUpd;
    private String noBukti;
    
    public Integer getTransferID() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFromGudang() {
        return fromGudang;
    }

    public void setFromGudang(Integer fromGudang) {
        this.fromGudang = fromGudang;
    }

    public Integer getToGudang() {
        return toGudang;
    }

    public void setToGudang(Integer toGudang) {
        this.toGudang = toGudang;
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

    public String getNoBukti() {
        return noBukti;
    }

    public void setNoBukti(String noBukti) {
        this.noBukti = noBukti;
    }
    
    
}
