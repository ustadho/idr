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
public class ItemHistory {
    private Integer itemHistId;
    private Integer idBarang;
    private String itemNo;
    private Date txDate;
    private String txType;
    private Integer invoiceid;
    private String description;
    private Double quantity;
    private Double sellingprice;
    private Double itemCost;
    private Double costAvr;
    private Double prevCost;
    private Double prevQty;
    private Integer glPeriod;
    private Integer glYear;
    private Integer warehouseId;
    private Integer fifoHistId;
    private Integer fifoStart;
    private Double qtyControl;

    public Integer getItemHistId() {
        return itemHistId;
    }

    public void setItemHistId(Integer itemHistId) {
        this.itemHistId = itemHistId;
    }

    public Integer getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(Integer idBarang) {
        this.idBarang = idBarang;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public Date getTxDate() {
        return txDate;
    }

    public void setTxDate(Date txDate) {
        this.txDate = txDate;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public Integer getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(Integer invoiceid) {
        this.invoiceid = invoiceid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(Double sellingprice) {
        this.sellingprice = sellingprice;
    }

    public Double getItemCost() {
        return itemCost;
    }

    public void setItemCost(Double itemCost) {
        this.itemCost = itemCost;
    }

    public Double getCostAvr() {
        return costAvr;
    }

    public void setCostAvr(Double costAvr) {
        this.costAvr = costAvr;
    }

    public Double getPrevCost() {
        return prevCost;
    }

    public void setPrevCost(Double prevCost) {
        this.prevCost = prevCost;
    }

    public Double getPrevQty() {
        return prevQty;
    }

    public void setPrevQty(Double prevQty) {
        this.prevQty = prevQty;
    }

    public Integer getGlPeriod() {
        return glPeriod;
    }

    public void setGlPeriod(Integer glPeriod) {
        this.glPeriod = glPeriod;
    }

    public Integer getGlYear() {
        return glYear;
    }

    public void setGlYear(Integer glYear) {
        this.glYear = glYear;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getFifoHistId() {
        return fifoHistId;
    }

    public void setFifoHistId(Integer fifoHistId) {
        this.fifoHistId = fifoHistId;
    }

    public Integer getFifoStart() {
        return fifoStart;
    }

    public void setFifoStart(Integer fifoStart) {
        this.fifoStart = fifoStart;
    }

    public Double getQtyControl() {
        return qtyControl;
    }

    public void setQtyControl(Double qtyControl) {
        this.qtyControl = qtyControl;
    }
    
    
}
