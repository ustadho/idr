/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import com.ustasoft.pos.domain.Item;
import com.ustasoft.pos.domain.ItemCoa;
import com.ustasoft.pos.domain.Relasi;
import com.ustasoft.pos.domain.view.ItemSupplierView;
import java.sql.Connection;
import java.util.List;
import pos.master.form.MasterItem;

/**
 *
 * @author cak-ust
 */
public interface MasterService {
    public void setConn(Connection conn);
    public void simpanItem(Item item);
    public Item cariItemByID(Integer id);
    public void simpanItemCoa(ItemCoa coa, boolean insert);
    public void simpanItemSupplier(Relasi supp);
    public List<ItemSupplierView> tampilkanItemSupplier(Integer id);
    public MasterItem cariItemHistoriTerakhir();
}
