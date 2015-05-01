/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class Item {

    private Integer id;
    private String plu;
    private String barcode;
    private String nama_barang;
    private Integer id_kategori;
    private String satuan;
    private Integer id_supp_default;
    private Boolean active;
    private String keterangan;
    private String accPersediaan;
    private String accPembelian;
    private String accReturPembelian;
    private String accPenjualan;
    private String accReturPenjualan;
    private String accHpp;
    private Integer idSatuan;
    private Double hargaJual;
    private String tipe;
    private Integer reorder;

    public Double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(Double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public Integer getIdSatuan() {
        return idSatuan;
    }

    public void setIdSatuan(Integer idSatuan) {
        this.idSatuan = idSatuan;
    }

    public String getAccPersediaan() {
        return accPersediaan;
    }

    public void setAccPersediaan(String accPersediaan) {
        this.accPersediaan = accPersediaan;
    }

    public String getAccPembelian() {
        return accPembelian;
    }

    public void setAccPembelian(String accPembelian) {
        this.accPembelian = accPembelian;
    }

    public String getAccReturPembelian() {
        return accReturPembelian;
    }

    public void setAccReturPembelian(String accReturPembelian) {
        this.accReturPembelian = accReturPembelian;
    }

    public String getAccPenjualan() {
        return accPenjualan;
    }

    public void setAccPenjualan(String accPenjualan) {
        this.accPenjualan = accPenjualan;
    }

    public String getAccReturPenjualan() {
        return accReturPenjualan;
    }

    public void setAccReturPenjualan(String accReturPenjualan) {
        this.accReturPenjualan = accReturPenjualan;
    }

    public String getAccHpp() {
        return accHpp;
    }

    public void setAccHpp(String accHpp) {
        this.accHpp = accHpp;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNamaBarang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public Integer getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(Integer id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Integer getId_supp_default() {
        return id_supp_default;
    }

    public void setId_supp_default(Integer id_supp_default) {
        this.id_supp_default = id_supp_default;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public Integer getReorder() {
        return reorder;
    }

    public void setReorder(Integer reorder) {
        this.reorder = reorder;
    }
    
    
}
