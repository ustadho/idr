/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.domain;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement.DEFAULT;

/**
 *
 * @author cak-ust
 */
public class AccCoa {
    private String acc_no;
    private String acc_name;
    private String sub_acc_of;
    private String acc_type;
    private Boolean active;
    private Double saldo_awal;
    private Date per_tgl;
    private String catatan;
    private Double saldo_rek_koran=new Double(0);
    private Date date_ins;
    private String user_ins;
    private Date date_upd;
    private String user_upd;
    private String curr_id;
    private String acc_group;

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

    public Date getDate_ins() {
        return date_ins;
    }

    public void setDate_ins(Date date_ins) {
        this.date_ins = date_ins;
    }

    public String getUser_ins() {
        return user_ins;
    }

    public void setUser_ins(String user_ins) {
        this.user_ins = user_ins;
    }

    public Date getDate_upd() {
        return date_upd;
    }

    public void setDate_upd(Date date_upd) {
        this.date_upd = date_upd;
    }

    public String getUser_upd() {
        return user_upd;
    }

    public void setUser_upd(String user_upd) {
        this.user_upd = user_upd;
    }

    public String getCurr_id() {
        return curr_id;
    }

    public void setCurr_id(String curr_id) {
        this.curr_id = curr_id;
    }

    public String getAcc_group() {
        return acc_group;
    }

    public void setAcc_group(String acc_group) {
        this.acc_group = acc_group;
    }
    
    
}
