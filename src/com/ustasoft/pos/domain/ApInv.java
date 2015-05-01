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
public class ApInv {
    private Integer id;
    private Integer supplierId;
    private String invoiceNo;
    private String description;
    private Date invoiceDate;
    private Integer top;
    private Double invoiceAmount;
    private Double itemAmount;
    private Double paidAmount;
    private Double owing;
    private Integer idGudang;
    private Integer templateId=1;
    private Date timeIns;
    private String userIns;
    private Date timeUpd;
    private String userUpd;
    private String apDisc;
    private Double biayaLain=new Double(0);
    private String noPo;
    private String trxType;
    private Integer returDari;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Double getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(Double itemAmount) {
        this.itemAmount = itemAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getOwing() {
        return owing;
    }

    public void setOwing(Double owing) {
        this.owing = owing;
    }

    public Integer getIdGudang() {
        return idGudang;
    }

    public void setIdGudang(Integer idGudang) {
        this.idGudang = idGudang;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
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

    public String getApDisc() {
        return apDisc;
    }

    public void setApDisc(String apDisc) {
        this.apDisc = apDisc;
    }

    public Double getBiayaLain() {
        return biayaLain;
    }

    public void setBiayaLain(Double biayaLain) {
        this.biayaLain = biayaLain;
    }

    public String getNoPo() {
        return noPo;
    }

    public void setNoPo(String noPo) {
        this.noPo = noPo;
    }

    public String getTrxType() {
        return trxType;
    }

    public void setTrxType(String trxType) {
        this.trxType = trxType;
    }

    public Integer getReturDari() {
        return returDari;
    }

    public void setReturDari(Integer returDari) {
        this.returDari = returDari;
    }
    
    
}
