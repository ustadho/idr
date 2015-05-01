/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author cak-ust
 */
public class JTaskPaneContainer extends JXTaskPaneContainer{
    private Image image;
    public JTaskPaneContainer(){
        image=new ImageIcon(getClass().getResource("Hitam.jpg")).getImage();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gd=(Graphics2D)g.create();
        gd.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        gd.setBackground(Color.BLACK);
        gd.dispose();
    }
}
