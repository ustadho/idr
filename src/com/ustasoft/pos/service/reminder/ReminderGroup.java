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
public class ReminderGroup {
    private String group;
    private Double total;
    List<ReminderHeader> lstReminderHeaders=new  ArrayList<ReminderHeader>();
    
    public ReminderGroup(String g, List<ReminderHeader> headers, Double t){
        this.group=g;
        this.lstReminderHeaders=headers;
        this.total=t;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<ReminderHeader> getLstReminderHeaders() {
        return lstReminderHeaders;
    }

    public void setLstReminderHeaders(List<ReminderHeader> lstReminderHeaders) {
        this.lstReminderHeaders = lstReminderHeaders;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    
}
