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
public class ArSuratJalan {
    private String noSj;
    private Integer arId;
    private Date tglSj;
    private String kirimKe;
    private String alamat;
    private Integer idKota;
    private String hormatKami;
    private Double coli;
    private String keterangan;

    public String getNoSj() {
        return noSj;
    }

    public void setNoSj(String noSj) {
        this.noSj = noSj;
    }

    public Integer getArId() {
        return arId;
    }

    public void setArId(Integer arId) {
        this.arId = arId;
    }

    public Date getTglSj() {
        return tglSj;
    }

    public void setTglSj(Date tglSj) {
        this.tglSj = tglSj;
    }

    public String getKirimKe() {
        return kirimKe;
    }

    public void setKirimKe(String kirimKe) {
        this.kirimKe = kirimKe;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Integer getIdKota() {
        return idKota;
    }

    public void setIdKota(Integer idKota) {
        this.idKota = idKota;
    }

    
    public String getHormatKami() {
        return hormatKami;
    }

    public void setHormatKami(String hormatKami) {
        this.hormatKami = hormatKami;
    }

    public Double getColi() {
        return coli;
    }

    public void setColi(Double coli) {
        this.coli = coli;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    
    
}
