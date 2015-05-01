/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class OpnameDetail {
    Integer idOpname;
    Integer idBarang;
    Double qtySekarang;
    Double qtyBaru;
    Double hppSekarang;
    Double hppBaru;
    String ket;
    Integer seq;

    public Integer getIdOpname() {
        return idOpname;
    }

    public void setIdOpname(Integer idOpname) {
        this.idOpname = idOpname;
    }

    public Integer getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(Integer idBarang) {
        this.idBarang = idBarang;
    }

    public Double getQtySekarang() {
        return qtySekarang;
    }

    public void setQtySekarang(Double qtySekarang) {
        this.qtySekarang = qtySekarang;
    }

    public Double getQtyBaru() {
        return qtyBaru;
    }

    public void setQtyBaru(Double qtyBaru) {
        this.qtyBaru = qtyBaru;
    }

    public Double getHppSekarang() {
        return hppSekarang;
    }

    public void setHppSekarang(Double hppSekarang) {
        this.hppSekarang = hppSekarang;
    }

    public Double getHppBaru() {
        return hppBaru;
    }

    public void setHppBaru(Double hppBaru) {
        this.hppBaru = hppBaru;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    
}
