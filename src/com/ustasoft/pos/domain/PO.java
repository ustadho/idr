/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

import java.util.Date;

/**
 *
 * @author cak-ust
 */
public class PO {
        private String noPo;
        private Integer supplierId;
        private Date tanggal;
        private String description;
        private Integer top;
        private String poDisc;
        private Double biayaLain;
        private Date timeIns;
        private String userIns;
        private Date timeUpd;
        private String userUpd;

    public String getNoPo() {
        return noPo;
    }

    public void setNoPo(String noPo) {
        this.noPo = noPo;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getPoDisc() {
        return poDisc;
    }

    public void setPoDisc(String poDisc) {
        this.poDisc = poDisc;
    }

    public Double getBiayaLain() {
        return biayaLain;
    }

    public void setBiayaLain(Double biayaLain) {
        this.biayaLain = biayaLain;
    }

    public Date getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Date timeIns) {
        this.timeIns = timeIns;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Date getTimeUpd() {
        return timeUpd;
    }

    public void setTimeUpd(Date timeUpd) {
        this.timeUpd = timeUpd;
    }

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }
        
        
}
