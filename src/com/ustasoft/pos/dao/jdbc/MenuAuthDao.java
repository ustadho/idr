/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.component.MenuAuth;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author cak-ust
 */
public class MenuAuthDao {
    private Connection conn;

    public MenuAuthDao(Connection conn) {
        this.conn=conn;
    }
    
    public void setConn(Connection c){
        this.conn=c;
    }
    
    public MenuAuth getMenuByUsername(String sMenu, String sUser) {
        MenuAuth menu=null;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select a.* from m_menu_authorization a "
                + "inner join m_menu_list l on l.id=a.menu_id "
                + "where menu_description='"+sMenu+"' and a.user_name='"+sUser+"'");
            if(rs.next()){
                menu=new MenuAuth();
                menu.setDelete(rs.getBoolean("can_delete"));
                menu.setInsert(rs.getBoolean("can_insert"));
                menu.setUpdate(rs.getBoolean("can_update"));
                menu.setRead(rs.getBoolean("can_read"));
                menu.setPrint(rs.getBoolean("can_print"));
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        return menu;
    }
    
}
