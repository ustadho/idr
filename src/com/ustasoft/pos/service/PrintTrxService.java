/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class PrintTrxService {
    private static PropertyResourceBundle resources;
    private Connection conn;
    JRExporter exporter = new JRPrintServiceExporter(); 
    PrintService printService[];
    static {
        try {
            String sDir = System.getProperties().getProperty("user.dir");
            resources = new PropertyResourceBundle(new FileInputStream(new File(sDir + "/setting.properties")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (MissingResourceException mre) {
            System.err.println("setting.properties not found");
            System.exit(1);
        }
    }
    
    public void setConn(Connection c){
        this.conn=c;
    }
    
    public void printJualLX300(Integer idPenjualan) {
        PrinterJob job = PrinterJob.getPrinterJob();
        //SysConfig sy=new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i = 0;
        for (i = 0; i < services.length; i++) {
            if (services[i].getName().equalsIgnoreCase(resources.getString("printer_kwt"))) {
                break;
            }
        }
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try {
            PrintFakturJual pn = new PrintFakturJual(conn, idPenjualan, MainForm.sUserName, services[i]);
        } catch (java.lang.ArrayIndexOutOfBoundsException ie) {
            JOptionPane.showMessageDialog(null, "Printer tidak ditemukan!");
        }
    }
    
    public void printClosingTrx(String idPenjualan) {
        PrinterJob job = PrinterJob.getPrinterJob();
        //SysConfig sy=new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i = 0;
        for (i = 0; i < services.length; i++) {
            if (services[i].getName().equalsIgnoreCase(resources.getString("printer_kwt"))) {
                break;
            }
        }
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try {
            PrintClosing pn = new PrintClosing(conn, idPenjualan, MainForm.sUserName, services[i], false);
        } catch (java.lang.ArrayIndexOutOfBoundsException ie) {
            JOptionPane.showMessageDialog(null, "Printer tidak ditemukan!");
        }
    }
}
