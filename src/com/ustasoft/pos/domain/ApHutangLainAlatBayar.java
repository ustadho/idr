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
public class ApHutangLainAlatBayar {
    private String noAp;
    private String kodeBayar;
    private Date jtTempo;
    private Double nominal;
    private String keterangan;

    public String getNoAp() {
        return noAp;
    }

    public void setNoAp(String noAp) {
        this.noAp = noAp;
    }

    public String getKodeBayar() {
        return kodeBayar;
    }

    public void setKodeBayar(String kodeBayar) {
        this.kodeBayar = kodeBayar;
    }

    public Date getJtTempo() {
        return jtTempo;
    }

    public void setJtTempo(Date jtTempo) {
        this.jtTempo = jtTempo;
    }

    public Double getNominal() {
        return nominal;
    }

    public void setNominal(Double nominal) {
        this.nominal = nominal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    
}
