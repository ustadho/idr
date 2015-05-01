/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain.view;

/**
 *
 * @author cak-ust
 */
public class ItemSupplierView {
    private Integer id_barang;
    private Integer id_supplier;
    private String nama_supplier;
    private Double harga;
    private String disc;
    private Double ppn;
    private Double nett;
    private Integer prioritas;

    public Integer getId_barang() {
        return id_barang;
    }

    public void setId_barang(Integer id_barang) {
        this.id_barang = id_barang;
    }

    public Integer getId_supplier() {
        return id_supplier;
    }

    public void setId_supplier(Integer id_supplier) {
        this.id_supplier = id_supplier;
    }

    public String getNama_supplier() {
        return nama_supplier;
    }

    public void setNama_supplier(String nama_supplier) {
        this.nama_supplier = nama_supplier;
    }

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public Double getPpn() {
        return ppn;
    }

    public void setPpn(Double ppn) {
        this.ppn = ppn;
    }

    public Double getNett() {
        return nett;
    }

    public void setNett(Double nett) {
        this.nett = nett;
    }

    public void setPrioritas(Integer prior){
        this.prioritas=prior;
    }
    
    public Object getPrioritas() {
        return prioritas;
    }
    
    
    
    
}
