/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.dao.jdbc;

import com.ustasoft.pos.domain.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cak-ust
 */

public class UsersDao {
    Connection conn;
    // variabel datasource, diisikan oleh Spring Framework
    // karena diberikan @Autowired
    //@Autowired private DataSource dataSource;
    
    public UsersDao(Connection con){
        this.conn=con;
    }
    
    private String getUserID(){
        String sUserid=null;
        try {
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_user_id()");
            if(rs.next()){
                sUserid=rs.getString(1);
                rs.close();
            }
                
        } catch (SQLException ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sUserid;
    }
    
    public void simpan(Users user) throws Exception {
        String sqlInsert = "";
        if(user.getUserId()==null){
            sqlInsert= "INSERT INTO m_user(user_name, complete_name, pwd, authority, user_id) "
                + "VALUES (?, ?, ?, ?, ?);";
        }else{
            sqlInsert= "UPDATE m_user "
                + "SET user_name=?, complete_name=?, pwd=?, profile=? "
                + " WHERE user_id=?;";
        }
        PreparedStatement ps = conn.prepareStatement(sqlInsert);
        ps.setString(1, user.getUserName());
        ps.setString(2, user.getCompleteName());
        ps.setString(3, user.getPasswd());
        ps.setInt(4, user.getProfile());
        ps.setString(5, user.getUserId()==null? getUserID(): user.getUserId());
        int hasil = ps.executeUpdate();
        
        System.out.println("Jumlah row yang berhasil diinsert/ update : "+hasil);
            
        
    }
    
    public List<Users> cariSemuaData() throws Exception {
        String sql = "select * from m_user order by 2";
        List<Users> hasil = new ArrayList<Users>();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while(rs.next()){
            Users k = new Users();
            k.setUserId(rs.getString("user_id"));
            k.setUserName(rs.getString("user_name"));
            k.setPasswd(rs.getString("pwd"));
            k.setProfile(rs.getInt("profile"));
            hasil.add(k);
        }
        rs.close();
        return hasil;
    }
    
    public Users cariByID(String userId) throws Exception {
        String sql = "select * from m_user where user_id='"+userId+"'";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        Users k = new Users();
        if(rs.next()){
            k.setUserId(rs.getString("user_id"));
            k.setUserName(rs.getString("user_name"));
            k.setCompleteName(rs.getString("complete_name"));
            k.setPasswd(rs.getString("pwd"));
            k.setProfile(rs.getInt("profile"));
        }
        return k;
    }
    
    public int delete(Users k) throws SQLException{
        int hasil=0;
        if(k!=null && k.getUserId()!=null){
            PreparedStatement ps=conn.prepareStatement("delete from m_user where user_id=?");
            ps.setString(1, k.getUserId());
            hasil=ps.executeUpdate();
            ps.close();
        }
        return hasil;
    }
}
