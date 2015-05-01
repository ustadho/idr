/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class Sales {
    Integer id_sales;
    String nama_sales;
    String alamat;
    Integer id_kota;
    String telp;
    String fax;
    String hp;
    String email;
    String no_npwp;
    Boolean active;
    
    public Integer getId_sales() {
        return id_sales;
    }

    public void setId_sales(Integer id_sales) {
        this.id_sales = id_sales;
    }

    public String getNama_sales() {
        return nama_sales;
    }

    public void setNama_sales(String nama_sales) {
        this.nama_sales = nama_sales;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
   
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Integer getId_kota() {
        return id_kota;
    }

    public void setId_kota(Integer id_kota) {
        this.id_kota = id_kota;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNo_npwp() {
        return no_npwp;
    }

    public void setNo_npwp(String no_npwp) {
        this.no_npwp = no_npwp;
    }
    
    
}
