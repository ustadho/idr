/*
 * TextEditorOk.java
 *
 * Created on December 20, 2005, 11:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.ustasoft.component;

import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.DefaultCellEditor;
import java.awt.Component;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;



/**
 *
 * @author Administrator
 */
public class TextEditor extends DefaultCellEditor{
JTextField txt; 
Integer kk;
boolean okAdd,okEdit;
Connection con;    
private ListCH1 lst;
private JLabel lbl;
private JTable tbl;
private String sFieldShow,sTable,sKdFind,sSql,sExFil;
private Integer posx,posy,tblX,tblY,iRow;


    /** Creates a new instance of TextEditorOk */
    public TextEditor(JTable table,Connection newCon,String sFieldToBeShown,String sNameTable) {
         super(new JTextField());
         kk=0;
         
         sFieldShow=sFieldToBeShown;
         sTable=sNameTable;
         con=newCon;
         sExFil="";
         lst = new ListCH1();
         lst.setVisible(false);
         lst.setSize(200,400);
         ListCH1.con = newCon;
         lbl=new JLabel();
         txt = (JTextField)getComponent();
         tbl=table;

         txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyReleased(evt);
            }
        });

        txt.addAncestorListener(new AncestorListener() {
            @Override
			public void ancestorAdded(AncestorEvent event) {
                                                   //make sure combobox handles key events
				txt.requestFocusInWindow();

			}
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
		});
        txt.requestFocus();
    }

    public TextEditor(JTable table,Connection newCon,String sFieldToBeShown,String sNameTable,Integer xx,Integer yy) {
         super(new JTextField());
         kk=0;
         
         sFieldShow=sFieldToBeShown;
         sTable=sNameTable;
         posx=xx;posy=yy;
         con=newCon;
         sExFil="";
         lst = new ListCH1();
         lst.setVisible(false);
         lst.setSize(200,400);
         ListCH1.con = newCon;
         lbl=new JLabel();
         txt = (JTextField)getComponent();
         tbl=table;
         
         txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyReleased(evt);
            }
        });
        txt.addAncestorListener(new AncestorListener() {
            @Override
			public void ancestorAdded(AncestorEvent event) {
                                                   //make sure combobox handles key events
				txt.requestFocusInWindow();

			}
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
		});
        txt.requestFocus();
    }
    
    public TextEditor(JTable table,Connection newCon,String sFieldToBeShown,String sNameTable,String sExpFilter,Integer xx,Integer yy) {
         super(new JTextField());
         kk=0;
         lst = new ListCH1();
         lst.setVisible(false);
         lst.setSize(200,400);
         ListCH1.con = newCon;
         sFieldShow=sFieldToBeShown;
         sTable=sNameTable;
         posx=xx;posy=yy;
         con=newCon;
         sExFil=sExpFilter;
         lbl=new JLabel();
         tbl=table;
         
         txt = (JTextField)getComponent();
         txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyReleased(evt);
            }
        });
        txt.addAncestorListener(new AncestorListener() {
            @Override
			public void ancestorAdded(AncestorEvent event) {
                                                   //make sure combobox handles key events
				txt.requestFocusInWindow();

			}
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
		});
        txt.requestFocus();
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        JTextField txt1 =
            (JTextField)super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
        if (value==null) 
            value="";
        txt1.setText(value.toString().trim());
        
       Rectangle rect=table.getCellRect(row, column, true);
       tblY=table.getLocationOnScreen().y+rect.getLocation().y;
       tblX=table.getLocationOnScreen().x+rect.getLocation().x;
       iRow=row;
       txt1.getCaret().setVisible(true);
       //txt1.setCaretPosition(0);
       txt1.requestFocus();
       return txt1;
    }
    
    @Override
    public Object getCellEditorValue() {
        JTextField txt1 = (JTextField)getComponent();
        Object o = txt1.getText().trim();
        return o;
    }

    @Override
    public boolean stopCellEditing() {
        JTextField txt1 = (JTextField)getComponent();
        Object o = txt1.getText();
    
        try{
            sSql="select "+sFieldShow+" from "+sTable+" where "+sFieldShow.substring(0, sFieldShow.indexOf(","))+"='"+o.toString()+"'";
//            System.out.println("AAA "+sSql);
            Statement stmtfind=con.createStatement();
            ResultSet rsfind=stmtfind.executeQuery(sSql);
            if (!rsfind.next()){
                return false;
            }else {
//                lbl.setText(rsfind.getString(2));
//                System.out.println(lbl.getText());
            }
            stmtfind.close();
            rsfind.close();
        }catch(SQLException se){}
        lst.dispose();
        return super.stopCellEditing();
    }
    
public String getLabel(){
    return lbl.toString().trim();
}

public String getKode(){
    return txt.getText().trim();
}
    
private void txtKeyReleased(java.awt.event.KeyEvent evt) {                                   
    try
    {
    String sCari = txt.getText();                        
           switch(evt.getKeyCode()) {           
               case java.awt.event.KeyEvent.VK_ENTER :{
                   if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {                            
                            txt.setText(obj[0].toString());
                            lbl.setText(obj[1].toString());
                            System.out.println(lbl.getText());
                            lst.setVisible(false);
                        }               
                   }
                   break;
               }
               case java.awt.event.KeyEvent.VK_DOWN: {               
                   if (lst.isVisible()){                 
                       lst.setFocusableWindowState(true);                       
                       lst.setVisible(true);
                       lst.requestFocus();
                   }   
                   break;
               }
               default : {
                   String sText=sFieldShow;
                   String sWhere="";
                   while (sText.contains(",")){
                       sWhere=sWhere+sText.substring(0, sText.indexOf(","))+" like '%"+sCari.trim().toUpperCase()+"%' OR ";
                       sText=sText.substring(sText.indexOf(",")+1, sText.length());
                   }
                   sWhere=sWhere+sText+" like '%"+sCari.trim().toUpperCase()+"%'";
                   if (!sExFil.trim().equals("")){
                   sWhere="("+sWhere+") and "+sExFil;}
              /*      sSql="select "+sFieldShow+" from "+sTable+" where "+sFieldShow.substring(0, sFieldShow.indexOf(","))+
                            " like '%"+sCari.trim().toUpperCase()+"%' OR "+sFieldShow.substring(sFieldShow.indexOf(",")+1, sFieldShow.length())+
                            " like '%"+sCari.trim().toUpperCase()+"%' order by "+sFieldShow.substring(0, sFieldShow.indexOf(","));
                */
                    sSql="select "+sFieldShow+" from "+sTable+" where "+sWhere+" order by "+sFieldShow.substring(0, sFieldShow.indexOf(","));
                    lst.setSQuery(sSql);
                    //Integer txtY=txt.getY()+tblY;
                    //Integer txtY=tblY;
                    lst.setBounds(tblX+txt.getX(), tblY.intValue()+txt.getHeight(), 200,200);
                    lst.setFocusableWindowState(false);
                    lst.setTxtCari(txt);
                    lst.setLblDes(new javax.swing.JLabel[]{lbl});
                    lst.setTable(tbl);
                    lst.setIRow(iRow);
                    //lst.setLblDes(lbl);
                    lst.setColWidth(0, 75);
                    lst.setColWidth(1, 200);
                    lst.setVisible(true); 
                    break;
                }
            }
    }catch(SQLException se){}
}    
}
