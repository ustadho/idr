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
public class ArInv {
    private Integer id;
    private Integer customerId;
    private String invoiceNo;
    private String description;
    private Date invoiceDate;
    private String jenisBayar;
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
    private String arDisc;
    private Double biayaLain=new Double(0);
    private String noSo;
    private String disiapkanOleh;
    private String diperiksaOleh;
    private String diterimaOleh;
    private Integer idSales;
    private Integer idExpedisi=new Integer(0);
    private String ketPembayaran;
    private String trxType;
    private Integer returDari=new Integer(0);
    private String noPol;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer id) {
        this.customerId = id;
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

    public String getArDisc() {
        return arDisc;
    }

    public void setArDisc(String apDisc) {
        this.arDisc = apDisc;
    }

    public Double getBiayaLain() {
        return biayaLain;
    }

    public void setBiayaLain(Double biayaLain) {
        this.biayaLain = biayaLain;
    }

    public String getNoSo() {
        return noSo;
    }

    public void setNoSo(String noSo) {
        this.noSo = noSo;
    }

    public String getJenisBayar() {
        return jenisBayar;
    }

    public void setJenisBayar(String jenisBayar) {
        this.jenisBayar = jenisBayar;
    }

    public String getDisiapkanOleh() {
        return disiapkanOleh;
    }

    public void setDisiapkanOleh(String disiapkanOleh) {
        this.disiapkanOleh = disiapkanOleh;
    }

    public String getDiperiksaOleh() {
        return diperiksaOleh;
    }

    public void setDiperiksaOleh(String diperiksaOleh) {
        this.diperiksaOleh = diperiksaOleh;
    }

    public String getDiterimaOleh() {
        return diterimaOleh;
    }

    public void setDiterimaOleh(String diterimaOleh) {
        this.diterimaOleh = diterimaOleh;
    }

    public Integer getIdSales() {
        return idSales;
    }

    public void setIdSales(Integer idSales) {
        this.idSales = idSales;
    }

    public Integer getIdExpedisi() {
        return idExpedisi;
    }

    public void setIdExpedisi(Integer idExpedisi) {
        this.idExpedisi = idExpedisi;
    }

    public String getKetPembayaran() {
        return ketPembayaran;
    }

    public void setKetPembayaran(String ketPembayaran) {
        this.ketPembayaran = ketPembayaran;
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

    public String getNoPol() {
        return noPol;
    }

    public void setNoPol(String noPol) {
        this.noPol = noPol;
    }

    
}
