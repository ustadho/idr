/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

import java.awt.Color;
import java.awt.Component;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author cak-ust
 */

public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmtHM=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//    Color w1 = new Color(255,255,255);
//    Color w2 = new Color(240,240,240);
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date || value instanceof Timestamp){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=GeneralFunction.dFmt.format(value);
            }else if(value instanceof Integer){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=GeneralFunction.intFmt.format(value);
            }else if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                if(isSelected){
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                }
                return checkBox;
            }
            
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
//                if(row % 2 == 0) {
//                    setBackground(w1);
//                } else {
//                    setBackground(w2);
//                }
//                
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }
            setValue(value);
            return this;
        }
    }
    
    
