/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */

public class ItemKategori {
    private Integer id;
    private String namaKategori;
    private String ketKategori;
    private String acc_persediaan;
    private String acc_pembelian;
    private String acc_retur_pembelian;
    private String acc_penjualan;
    private String acc_retur_penjualan;
    private String acc_hpp;

    public String getAcc_persediaan() {
        return acc_persediaan;
    }

    public void setAcc_persediaan(String acc_persediaan) {
        this.acc_persediaan = acc_persediaan;
    }

    public String getAcc_pembelian() {
        return acc_pembelian;
    }

    public void setAcc_pembelian(String acc_pembelian) {
        this.acc_pembelian = acc_pembelian;
    }

    public String getAcc_retur_pembelian() {
        return acc_retur_pembelian;
    }

    public void setAcc_retur_pembelian(String acc_retur_pembelian) {
        this.acc_retur_pembelian = acc_retur_pembelian;
    }

    public String getAcc_penjualan() {
        return acc_penjualan;
    }

    public void setAcc_penjualan(String acc_penjualan) {
        this.acc_penjualan = acc_penjualan;
    }

    public String getAcc_retur_penjualan() {
        return acc_retur_penjualan;
    }

    public void setAcc_retur_penjualan(String acc_retur_penjualan) {
        this.acc_retur_penjualan = acc_retur_penjualan;
    }

    public String getAcc_hpp() {
        return acc_hpp;
    }

    public void setAcc_hpp(String acc_hpp) {
        this.acc_hpp = acc_hpp;
    }
            
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getKetKategori() {
        return ketKategori;
    }

    public void setKetKategori(String ketKategori) {
        this.ketKategori = ketKategori;
    }
    
}
