/*
 * To change this template, choose Tools │ Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.PrintESC;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class PrintFakturJualGarisNyambung {

    private Connection conn;
    PrintESC printESC = new PrintESC();

    public PrintFakturJualGarisNyambung(Connection c, Integer id, String userName, PrintService service) {
        try {
            this.conn = c;

            String sQry = "select * from fn_rpt_print_penjualan("+id+") as (id bigint, no_faktur varchar, tgl_faktur varchar, no_so varchar, nama_gudang varchar, nama_customer varchar, \n" +
                        "alamat_customer varchar, kota_customer varchar, telp_hp varchar, nama_expedisi varchar, jt_tempo varchar, ket_faktur text, ket_pembayaran text, status_bayar text, \n" +
                        "nama_sales varchar, ar_disc varchar, ar_disc_rp double precision, nett double precision, plu varchar, nama_barang varchar, qty double precision, satuan varchar, unit_price double precision, disc varchar, ppn double precision, \n" +
                        "biaya double precision, sub_total double precision, keterangan varchar, disiapkan_oleh varchar, dibuat_oleh varchar, diperiksa_oleh varchar, diterima_oleh varchar)";
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            //printFile(saveToTmpFile(rs), service);
            printFile(saveToTmpFile(rs), service);
        } catch (SQLException ex) {
            Logger.getLogger(PrintFakturJualGarisNyambung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File saveToTmpFile(ResultSet rs) {
        try {
            File temp = File.createTempFile("FAKTUR", ".tmp");
    //        File temp = File.createTempFile("GoodReceipt", ".tmp",fileT);
    //        File temp = File.createTempFile("GoodReceipt", ".tmp",fileT);

            // Delete temp file when program exits.
//            temp.deleteOnExit();
            
            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            int i=0;
            int row=0;
            double total=0;
            double nett=0;
            String sKetPembayaran="";
            String disiapkanOleh="";
            String fakturOleh="";
            String diperiksaOleh="";
            String tandaTerima="";
            String sDisc="";
            while(rs.next()){
                if(i==0){
                    sKetPembayaran=rs.getString("ket_pembayaran");
                    disiapkanOleh=rs.getString("disiapkan_oleh");
                    fakturOleh=rs.getString("dibuat_oleh");
                    diperiksaOleh=rs.getString("diperiksa_oleh");
                    tandaTerima=rs.getString("diterima_oleh");
                    sDisc=rs.getString("ar_disc");
                    nett=rs.getDouble("nett");
                    out.write(printESC.bold());
                    out.write(MainForm.sToko);out.newLine();out.newLine();
                    out.write(printESC.cancelBold());
                    out.write(printESC.cpi15());
                    out.write(printESC.draft());
                    out.write(printESC.padString(MainForm.sAlamat1, 71));out.newLine();
                    out.write(printESC.padString(MainForm.sAlamat2, 70)+                              "┌"+printESC.copyString("─", 42)+"┐");  out.newLine();
                    out.write(printESC.padString(MainForm.sTelp1, 70)+printESC.bold()+                 "│                  FAKTUR                  │"+printESC.cancelBold());out.newLine();
                    out.write("┌"+ printESC.copyString("─", 54)+"┐              ├"+printESC.copyString("─", 42)+"┤");out.newLine();
                    out.write("│ Nama Toko : "+printESC.padString(rs.getString("nama_customer"), 41)+"│"+printESC.space(14)+ "│ No. Faktur     :"+printESC.padString(rs.getString("no_faktur"), 25)+"│"); out.newLine();
                    out.write("│ Alamat    : "+printESC.padString(rs.getString("alamat_customer"), 41)+"│"+printESC.space(14)+"│ Tanggal Faktur :"+printESC.padString(rs.getString("tgl_faktur"), 25)+"│"); out.newLine();
                    out.write("│ Kota      : "+printESC.padString(rs.getString("kota_customer"), 41)+"│"+printESC.space(14)+"│ Tgl. Jth Tempo :"+printESC.padString(rs.getString("jt_tempo"), 25)+"│"); out.newLine();
                    out.write("│ Telp/ HP  : "+printESC.padString(rs.getString("telp_hp"), 41)+"│"      +printESC.space(14)+"│ Status         :"+printESC.padString(rs.getString("status_bayar"), 25)+"│"); out.newLine();
                    out.write("│ Expedisi  : "+printESC.padString(rs.getString("nama_expedisi"), 41)+"│"+printESC.space(14)+"│ Salesman       :"+printESC.padString(rs.getString("nama_sales"), 25)+"│"); out.newLine();
                    out.write("└"+printESC.copyString("─", 54)+"┘              └"+printESC.copyString("─", 42)+"┘");out.newLine();
                    out.write("┌───┬"+printESC.copyString("─", 42)+"┬───────┬──────────┬────────────────┬────────────┬────────────────┐");out.newLine();
                    out.write("│NO │ NAMA BARANG                              │  QTY  │  SATUAN  │      HARGA     │ BIAYA LAIN │      JUMLAH    │");out.newLine();
                    out.write("├───┼"+printESC.copyString("─", 42)+"┼"+printESC.copyString("─", 7)+
                                "┼"+printESC.copyString("─", 10)+"┼"+printESC.copyString("─", 16)+
                                "┼"+printESC.copyString("─", 12)+"┼"+printESC.copyString("─", 16)+"│");out.newLine();
                }
                i++;
                row++;
                out.write("│"+printESC.padString( String.valueOf(i), 3)+"│ "+
                         printESC.padString(rs.getString("nama_barang"), 41)+"│"+
                         printESC.rataKanan(GeneralFunction.intFmt.format(rs.getInt("qty")), 7)+"│"+
                         printESC.padString(rs.getString("satuan"), 10)+"│"+
                         printESC.rataKanan(GeneralFunction.dFmt.format(rs.getInt("unit_price")), 16)+"│"+
                         printESC.rataKanan(GeneralFunction.dFmt.format(rs.getInt("biaya")), 12)+"│"+
                         printESC.rataKanan(GeneralFunction.dFmt.format(rs.getInt("sub_total")), 16)+"│"
                         );out.newLine();
                
                total+=rs.getDouble("sub_total");
            }
            for(int j=row; j<=12; j++){
                out.write("│"+printESC.space(3)+"│ "+
                         printESC.space(41)+"│"+
                         printESC.space(7)+"│"+
                         printESC.space(10)+"│"+
                         printESC.space(16)+"│"+
                         printESC.space(12)+"│"+
                         printESC.space(16)+"│"
                         );out.newLine();
            }
            out.write("└───┴"+printESC.copyString("─", 42)+"┴───────┴──────────┴────────────────┴────────────┴────────────────┘");out.newLine();
            String sKetPembayaranTemp=sKetPembayaran.substring(0, sKetPembayaran.indexOf("\n"));
            sKetPembayaran=sKetPembayaran.substring(sKetPembayaran.indexOf("\n")+1, sKetPembayaran.length());
            System.out.println("sKetBayar : "+sKetPembayaran);
            
            out.write(printESC.padString(sKetPembayaranTemp, 42)+printESC.space(34)+ " Sub Total       "+printESC.rataKanan(GeneralFunction.dFmt.format(total), 20));out.newLine();
            sKetPembayaranTemp=sKetPembayaran.substring(0, sKetPembayaran.indexOf("\n"));
            sKetPembayaran=sKetPembayaran.substring(sKetPembayaran.indexOf("\n")+1, sKetPembayaran.length());
            
            out.write(printESC.padString(sKetPembayaranTemp, 42)+printESC.space(34)+ " Disc "+ printESC.padString(sDisc, 15));
            out.newLine();
            sKetPembayaranTemp=sKetPembayaran.length()==0? "": sKetPembayaran.substring(0, sKetPembayaran.indexOf("\n"));
            sKetPembayaran=sKetPembayaran.substring(sKetPembayaran.indexOf("\n")+1, sKetPembayaran.length());
            
            out.write(printESC.padString(sKetPembayaranTemp, 42)+printESC.space(34)+" ═════════════════════════════════════");out.newLine();
            sKetPembayaranTemp=sKetPembayaran.length()==0? "": sKetPembayaran.substring(0, sKetPembayaran.length());
            out.write(printESC.padString(sKetPembayaranTemp, 42)+printESC.space(34)+printESC.bold()+ " Total           "+printESC.rataKanan(GeneralFunction.dFmt.format(nett),20)+printESC.cancelBold());out.newLine();
            out.write("┌"+printESC.copyString("─", 32)+"┬"+printESC.copyString("─", 28)+"┬"+ 
                    printESC.copyString("─", 27)+"┬"+
                    printESC.copyString("─", 22)+"┐");out.newLine();
            out.write("│ Barang disiapkan oleh :        │ Faktur dibuat oleh :       │ Diperiksa oleh :          │ Tanda Terima :       │");out.newLine();
            out.write("│                                │                            │                           │                      │");out.newLine();
            out.write("│                                │                            │                           │                      │");out.newLine();
            out.write("│ "+ printESC.padString(disiapkanOleh, 31)+"│ "+
                            printESC.padString(fakturOleh, 27)+"│ "+
                            printESC.padString(diperiksaOleh, 26)+"│ "+
                            printESC.padString(tandaTerima, 21)+"│");out.newLine();
            out.write("└"+printESC.copyString("─", 32)+"┴"+printESC.copyString("─", 28)+"┴"+ 
                    printESC.copyString("─", 27)+"┴"+
                    printESC.copyString("─", 22)+"┘");out.newLine();
            out.newLine();
            out.newLine();
            out.close();   
            return temp.getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(PrintFakturJualGarisNyambung.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PrintFakturJualGarisNyambung.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void printFile(File fileToPrint, PrintService service) {
//int yesNo = JOptionPane.showConfirmDialog(this,"Siapkan Printer",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
//if(yesNo == JOptionPane.YES_OPTION){
        try {
// Open the text file
            FileInputStream fs = new FileInputStream(fileToPrint);

// Find the default service
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
//PrintService service = PrintServiceLookup.lookupDefaultPrintService();

// Create the print job
            DocPrintJob job = service.createPrintJob();
            Doc doc = new SimpleDoc(fs, flavor, null);

// Monitor print job events
// See "Determining When a Print Job Has Finished"
// for the implementation of PrintJobWatcher
// PrintJobWatcher pjDone = new PrintJobWatcher(job);

// Print it
            job.print(doc, null);

// Wait for the print job to be done
// pjDone.waitForDone();

// It is now safe to close the input stream
            fs.close();
        } catch (PrintException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //SysConfig sc=new SysConfig();
        Connection conn;
        String url = "jdbc:postgresql://localhost/RM";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i = 0;
        for (i = 0; i < services.length; i++) {
            System.out.println("Index printer : "+i+" :"+services[i].getName());
            if (services[i].getName().equalsIgnoreCase("printer")) {
                break;
            }
        }

        try {
            conn = DriverManager.getConnection(url, "test", "test");
            PrintFakturJualGarisNyambung pn = new PrintFakturJualGarisNyambung(conn, 2, "admin", services[4]);
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
}
