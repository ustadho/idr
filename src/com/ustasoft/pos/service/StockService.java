/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import com.ustasoft.pos.domain.ItemHistory;
import java.sql.Connection;

/**
 *
 * @author cak-ust
 */
public interface StockService {
    public void setConn(Connection con);
    public ItemHistory cariHistoriTerakhir(Integer idItem) throws Exception;
}
