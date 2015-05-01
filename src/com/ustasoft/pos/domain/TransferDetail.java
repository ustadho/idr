/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class TransferDetail {
    private Integer transferID;
    private Integer idBarang;
    private Double qty; 
    private Integer seq;
    private Integer serialNo;
    private String ketItem;

    public Integer getTransferID() {
        return transferID;
    }

    public void setTransferID(Integer idTransfer) {
        this.transferID = idTransfer;
    }

    public Integer getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(Integer idBarang) {
        this.idBarang = idBarang;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public String getKetItem() {
        return ketItem;
    }

    public void setKetItem(String ketItem) {
        this.ketItem = ketItem;
    }
    
    
}
