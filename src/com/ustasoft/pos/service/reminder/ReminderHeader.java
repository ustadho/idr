/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service.reminder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cak-ust
 */
public class ReminderHeader {
    private String keterangan;
    private Double total;
    private List<ReminderIsi> listIsi=new ArrayList<ReminderIsi>();
    
    public ReminderHeader(String keterangan, Double total, List<ReminderIsi> lst){
        this.keterangan=keterangan;
        this.total=total;
        this.listIsi=lst;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ReminderIsi> getListIsi() {
        return listIsi;
    }

    public void setListIsi(List<ReminderIsi> listIsi) {
        this.listIsi = listIsi;
    }
    
    
}
