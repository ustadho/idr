/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service.reminder;

import com.ustasoft.pos.domain.Reminder;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 *
 * @author cak-ust
 */
public class ReminderTreeNode implements TreeNode{
    Reminder reminder;
    private ReminderTreeNode parent;
    private List<TreeNode> children = new ArrayList<TreeNode>();
    private String oldType="";
    private String oldSupplier="";
    
    public ReminderTreeNode(Reminder aReminder, ReminderTreeNode aParent){
        reminder=aReminder;
        parent=aParent;
        if(oldType.equalsIgnoreCase(aReminder.getTipe()) || oldSupplier.equalsIgnoreCase(oldSupplier)){
            children.add(new ReminderTreeNode(aReminder, this));
            oldSupplier=aReminder.getSupplier();
            oldType=aReminder.getTipe();
        }
    }
    
    public Reminder getReminder(){
        return reminder;
    }
    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return new IteratorEnumeration(children.iterator());
    }
    
}
