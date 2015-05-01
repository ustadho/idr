/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

/**
 *
 * @author cak-ust
 */
public class AccAlatBayar {
    private String kode;
    private String alatBayar;
    private boolean langsungCair;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getAlatBayar() {
        return alatBayar;
    }

    public void setAlatBayar(String alatBayar) {
        this.alatBayar = alatBayar;
    }

    public boolean isLangsungCair() {
        return langsungCair;
    }

    public void setLangsungCair(boolean langsungCair) {
        this.langsungCair = langsungCair;
    }
    
}
