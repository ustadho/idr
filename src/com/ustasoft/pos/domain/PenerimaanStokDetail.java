/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class PenerimaanStokDetail {
    private Integer id;
    private Integer idBarang;
    private Double qty;
    private Double harga;
    private Integer incId;
    private String ketItem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public Integer getIncId() {
        return incId;
    }

    public void setIncId(Integer incId) {
        this.incId = incId;
    }

    public String getKetItem() {
        return ketItem;
    }

    public void setKetItem(String ketItem) {
        this.ketItem = ketItem;
    }
    
    
}
