/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service.reminder;

/**
 *
 * @author cak-ust
 */
public class ReminderIsi {
    private String tipe;
    private String keterangan;
    private Double amount;
    private Integer id;
    
    public ReminderIsi(String t, String k, Double am, Integer i){
        this.tipe=t;
        this.keterangan=k;
        this.amount=am;
        this.id=i;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    
}
