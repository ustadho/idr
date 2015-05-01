/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ustasoft.pos.service.reminder;

import java.util.List;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 *
 * @author ustadho
 */
public class ReminderModel extends AbstractTreeTableModel{

    private final static String[] COLUMN_NAMES = {"Keterangan", "Total", "ID", "Tipe"};
    private List<ReminderGroup> lstReminder;
    
    public ReminderModel(List<ReminderGroup> lst) {
        super(new Object());
        this.lstReminder = lst;
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof ReminderIsi;
    }
    
    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof ReminderHeader) {
            ReminderHeader header = (ReminderHeader) parent;
            return header.getListIsi().size();
        }else if(parent instanceof ReminderGroup){
            ReminderGroup group=(ReminderGroup)parent;
            return group.getLstReminderHeaders().size();
        }
        return lstReminder.size();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof ReminderHeader) {
            ReminderHeader dept = (ReminderHeader) parent;
            return dept.getListIsi().get(index);
        }else if(parent instanceof ReminderGroup){
            ReminderGroup group=(ReminderGroup)parent;
            return group.getLstReminderHeaders().get(index);
        }
        return lstReminder.get(index);
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        ReminderHeader dept = (ReminderHeader) parent;
        ReminderIsi emp = (ReminderIsi) child;
        return dept.getListIsi().indexOf(emp);
    }
    
    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof ReminderGroup){
            ReminderGroup group=(ReminderGroup)node;
            switch(column){
                case 0:
                    return group.getGroup();
                case 1:
                    return group.getTotal();
                default:
                    return "";
            }
        }
        if (node instanceof ReminderHeader) {
            ReminderHeader header = (ReminderHeader) node;
            switch (column) {
                case 0:
                    return header.getKeterangan();
                case 1:
                    return header.getTotal();
            }
        } else if (node instanceof ReminderIsi) {
            ReminderIsi isi = (ReminderIsi) node;
            switch (column) {
                case 0:
                    return isi.getKeterangan();
                case 1:
                    return isi.getAmount();
                case 2:
                    return isi.getId();
                case 3:
                    return isi.getTipe();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object o, Object o1, int i) {
//        this.setValueAt(o, o1, i);
        if(o instanceof ReminderHeader){
            this.setValueAt((ReminderHeader)o, (ReminderHeader)o1, i);
        }
        else if(o instanceof ReminderIsi){
            this.setValueAt((ReminderIsi)o, (ReminderIsi)o1, i);
        }
        else if(o instanceof ReminderGroup){
            this.setValueAt((ReminderGroup)o, (ReminderGroup)o1, i);
        }
    }

}
