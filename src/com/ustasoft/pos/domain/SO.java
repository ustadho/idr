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
public class SO {
        private String noSo;
        private Integer supplierId;
        private Date tanggal;
        private String description;
        private Integer top;
        private String soDisc;
        private Double biayaLain;
        private Date timeIns;
        private String userIns;
        private Date timeUpd;
        private String userUpd;
        private Integer salesId;

    public String getNoSo() {
        return noSo;
    }

    public void setNoSo(String noSo) {
        this.noSo = noSo;
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

    public String getSoDisc() {
        return soDisc;
    }

    public void setSoDisc(String soDisc) {
        this.soDisc = soDisc;
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

    public Integer getSalesId() {
        return salesId;
    }

    public void setSalesId(Integer salesId) {
        this.salesId = salesId;
    }
        
    
}
