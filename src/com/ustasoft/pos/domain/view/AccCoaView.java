/*
 * To change this template;choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain.view;

import java.util.Date;

/**
 *
 * @author cak-ust
 */
public class AccCoaView {
    private String acc_no;
    private String acc_name;
    private String sub_acc_of;
    private String acc_type;
    private Boolean active;
    private Double saldo_awal;
    private Date per_tgl; 
    private String catatan;
    private Double saldo_rek_koran;
    private String type_name;
    private String group_name;

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public String getAcc_name() {
        return acc_name;
    }

    public void setAcc_name(String acc_name) {
        this.acc_name = acc_name;
    }

    public String getSub_acc_of() {
        return sub_acc_of;
    }

    public void setSub_acc_of(String sub_acc_of) {
        this.sub_acc_of = sub_acc_of;
    }

    public String getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getSaldo_awal() {
        return saldo_awal;
    }

    public void setSaldo_awal(Double saldo_awal) {
        this.saldo_awal = saldo_awal;
    }

    public Date getPer_tgl() {
        return per_tgl;
    }

    public void setPer_tgl(Date per_tgl) {
        this.per_tgl = per_tgl;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public Double getSaldo_rek_koran() {
        return saldo_rek_koran;
    }

    public void setSaldo_rek_koran(Double saldo_rek_koran) {
        this.saldo_rek_koran = saldo_rek_koran;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    
}
