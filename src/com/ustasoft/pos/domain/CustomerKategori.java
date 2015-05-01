/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */

public class CustomerKategori {
    private Integer id;
    private String kategoriCustomer;
    private String ketKategori;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamaKategori() {
        return kategoriCustomer;
    }

    public void setNamaKategori(String namaKategori) {
        this.kategoriCustomer = namaKategori;
    }

    public String getKetKategori() {
        return ketKategori;
    }

    public void setKetKategori(String ketKategori) {
        this.ketKategori = ketKategori;
    }
    
}
