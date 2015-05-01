/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */

public class RelasiKategori {
    private Integer id;
    private String kategoriSupplier;
    private String ketKategori;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamaKategori() {
        return kategoriSupplier;
    }

    public void setNamaKategori(String namaKategori) {
        this.kategoriSupplier = namaKategori;
    }

    public String getKetKategori() {
        return ketKategori;
    }

    public void setKetKategori(String ketKategori) {
        this.ketKategori = ketKategori;
    }
    
}
