/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class ArInvDet {
     private  Integer arId;
        private  Integer itemId;
        private Double qty;
        private Double unitPrice;
        private String disc;
        private Double unitCost=new Double(0);
        private Integer seq;
        private Double fifoQty;
        private Double ppn;
        private Integer detId;
        private Double lastCost;
        private Double biaya;
        private String keterangan;

    public Double getPpn() {
        return ppn;
    }

    public void setPpn(Double ppn) {
        this.ppn = ppn;
    }

    public Integer getArId() {
        return arId;
    }

    public void setArId(Integer arId) {
        this.arId = arId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Double getFifoQty() {
        return fifoQty;
    }

    public void setFifoQty(Double fifoQty) {
        this.fifoQty = fifoQty;
    }

    public Integer getDetId() {
        return detId;
    }

    public void setDetId(Integer detId) {
        this.detId = detId;
    }

    public Double getLastCost() {
        return lastCost;
    }

    public void setLastCost(Double lastCost) {
        this.lastCost = lastCost;
    }

    public Double getBiaya() {
        return biaya;
    }

    public void setBiaya(Double biaya) {
        this.biaya = biaya;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
        
        
}
