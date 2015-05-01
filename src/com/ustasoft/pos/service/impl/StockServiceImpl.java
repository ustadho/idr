/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service.impl;

import com.ustasoft.pos.dao.jdbc.ItemHistoryDao;
import com.ustasoft.pos.domain.ItemHistory;
import com.ustasoft.pos.service.StockService;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author cak-ust
 */
public class StockServiceImpl implements StockService{
    private Connection conn;
    ItemHistoryDao daoItemHistory=new ItemHistoryDao();
    @Override
    public ItemHistory cariHistoriTerakhir(Integer idItem) throws Exception{
        ItemHistory hist=new ItemHistory();
        hist=daoItemHistory.cariHistoriTerakhir(idItem);
        return hist;
    }

    @Override
    public void setConn(Connection con) {
        this.conn=con;
        daoItemHistory.setConn(con);
    }
    
}
