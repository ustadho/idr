/*
 * PrintKwtUM.java
 *
 * Created on November 14, 2006, 11:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import java.awt.print.PrinterJob;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.print.*;
import javax.print.attribute.*;
import javax.swing.JOptionPane;
import pos.MainForm;

/**
 *
 * @author root
 */
public class PrintClosing {
    private static PropertyResourceBundle resources;
    private Connection conn;
    private String sCloseId;
    private SimpleDateFormat clockFormat;
    private String nama_unit = "", username = "";
    private Boolean okCpy;
    private String usr_trx;
    private boolean printUlang = false;

    /**
     * Creates a new instance of PrintKwtUM
     */
    public PrintClosing(Connection newCon, String id, String sUser, PrintService service, boolean b) {
        conn = newCon;
        this.printUlang = b;
        this.sCloseId = id;
        usr_trx = sUser;
        printFile(saveToTmpFile(), service);
    }

    private File saveToTmpFile() {
        try {
            File temp = File.createTempFile("closing", ".tmp");
//        File fileT = new File("C:/KWT/");
//        File temp = File.createTempFile("Penjualan", ".tmp",fileT);
            // Delete temp file when program exits.
            //temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            out.write(resetPrn());
            out.write(draft());
            out.write(condenced());
            out.write(cpi20());
            out.write(Line_Space_1_per_6());

            NumberFormat nFormat = new DecimalFormat("#,##0");

            ResultSet rs = conn.createStatement().executeQuery(
                    "select * from fn_acc_closing_print('"+sCloseId+"') as (close_id varchar, tanggal date, "
                    + "user_ins varchar, time_ins timestamp without time zone, keterangan varchar, "
                    + "tunai_Aktual double precision, "
                    + "tunai_selisih double precision, kredit_aktual double precision, kredit_selisih double precision) ");

            String sNoKoreksi = "";
            int i = 1;
            int qty = 0;
            double totAktual=  0, totSelisih = 0;
            String no;
            while (rs.next()) {
                //         1         2         3         4         5         6         7         8         9         10       11        12        13        14
                //12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                //               KWITANSI                 |
                if (i == 1) {
                    out.write(MainForm.sToko);
                    out.newLine();      //7
                    out.write(MainForm.sAlamat1);
                    out.newLine();      //8                                 No: 123456789012
                    out.write(MainForm.sTelp1);
                    out.newLine();      //8                                 No: 123456789012
                    out.newLine();      //9
                    out.write("          CLOSING TRANSAKSI");
                    out.newLine();      //11
                    out.write("No. : " + rs.getString("close_id") + "   " + rataKanan("(" + new SimpleDateFormat("dd/MM/yyyy hh:MM").format(rs.getTimestamp("time_ins")) + ")", 18));
                    out.newLine();      //12
                    out.write("Tgl.: " + new SimpleDateFormat("dd/MM/yy").format(rs.getDate("tanggal")) );
                    out.newLine();
                    out.write("User: " + rs.getString("user_ins"));
                    out.newLine();      //11
                    out.newLine();
                    out.write("========================================"); out.newLine();  //12 
                    out.write("  KETERANGAN        AKTUAL     SELISIH  "); out.newLine();  //12 
                    out.write("----------------------------------------"); out.newLine();  //12 
                    
                    
                }
                i++;
                out.write(padString(rs.getString("keterangan"), 19)+" "+
                                    rataKanan(nFormat.format(rs.getDouble("tunai_aktual")), 9) + " "+
                                    rataKanan(nFormat.format(rs.getDouble("tunai_selisih")), 9));
                out.newLine();
                totAktual+=rs.getDouble("tunai_aktual"); 
                totSelisih+=rs.getDouble("tunai_selisih"); 
                //out.write(padString(rs.getString("nama_item"),30));         out.newLine();
            }

            totAktual = roundUp(Math.abs(totAktual), 50d) * (totAktual > 0 ? 1 : -1);
            out.write("---------------------------------------+");out.newLine();      //19
            out.write(padString("  TOTAL ", 19)+" "+
                                    rataKanan(nFormat.format(totAktual), 9) + " "+
                                    rataKanan(nFormat.format(totSelisih), 9));
            rs.close();



            int s = 0;
            out.newLine();
            out.newLine();
//        out.newLine();
//        out.newLine();
//        out.newLine();
//        out.newLine();
//        out.newLine();
//        out.newLine();
//        out.newLine();
            out.write(printCutPaper());
            if (!printUlang) {
                out.write(drawKick());
            }
            out.close();

            return temp.getCanonicalFile();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
        return null;
    }

    private String printCutPaper() {
        String str;
        str = String.valueOf((char) 29) + String.valueOf((char) 'V') + String.valueOf((char) 66) + String.valueOf((char) 0);

//    str = String.valueOf((char)29) +String.valueOf((char)'i');
        return str;
    }

    private String drawKick() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 112) + String.valueOf((char) 0) + String.valueOf((char) 60) + String.valueOf((char) 120);
        return str;
    }

    private Double roundUp(Double dNum, Double dUp) {
        Double ret = dNum;
        if (dNum == null) {
            dNum = 0.0;
        }
        Double sisa = dNum % dUp;
        if (sisa > 0) {
            ret = (dNum - sisa) + dUp;
        }
        return ret;
    }

    private String rataKanan(String sTeks, int panjang) {
        String newText;

        newText = space(panjang - sTeks.length()) + sTeks;

        return newText;
    }

    private String padString(String sTeks, int panjang) {
        String newText;
        String jmSpace = "";
        if (sTeks.length() > panjang) {
            newText = sTeks.trim().substring(0, panjang);
        } else {
            newText = sTeks.trim();
        }


        for (int i = 0; i < (panjang - sTeks.trim().length()); i++) {
            newText = newText + " ";
        }

        return newText;
    }

    private String space(int iSpc) {
        String s = "";
        for (int i = 1; i <= iSpc; i++) {
            s = s + " ";
        }
        return s;
    }

    private String tengah(String sStr) {
        String s = "";
        int iTengah = (80 - sStr.length()) / 2;
        s = s + space(iTengah) + sStr;
        return s;
    }

    private String copyString(String sTeks, int panjang) {
        String newString = "";
        for (int y = 1; y <= panjang; y++) {
            newString = sTeks + newString;
        }
        return newString;
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

    public void setUser(String sUser) {
        username = sUser;
    }

    private String resetPrn() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 64);
        return str;
    }

    private String draft() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 48);
        return str;
    }

    private String LQ() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 49);
        return str;
    }

    private String bold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 69);
        return str;
    }

    private String cancelBold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 70);
        return str;
    }

    private String italic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 52);
        return str;
    }

    private String cancelItalic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 53);
        return str;
    }

    private String underLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 49);
        return str;
    }

    private String cancelUnderLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 48);
        return str;
    }

    private String cpi10() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 80);
        return str;
    }

    private String cpi12() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 77);
        return str;
    }

    private String cpi15() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 103);
        return str;
    }

    private String cpi20() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 77) + String.valueOf((char) 15);
        return str;
    }

    private String condenced() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 15);
        return str;
    }

    private String cancelCondenced() {
        String str;
        str = String.valueOf((char) 18);
        return str;
    }

    private String loadFront() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 25) + String.valueOf((char) 70);
        return str;
    }

    private String DoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 71);
        return str;

    }

    private String CancelDoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 72);
        return str;

    }

    private String Space_1_per_36() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 51) + String.valueOf((char) 5);
        return str;

    }

    private String Space_1_per_72() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 51) + String.valueOf((char) 45);
        return str;

    }

    private String Line_Space_1_per_8() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 48);
        return str;

    }

    private String Line_Space_1_per_6() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 50);
        return str;

    }

    private String doubleWide() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 14);
        return str;

    }

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

    public static void main(String[] args) {
        //SysConfig sc=new SysConfig();
        Connection conn;
        String url = "jdbc:postgresql://localhost/" + resources.getString("database");
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
//            for(i=0;i<services.length;i++){
//                if(services[i].getName().equalsIgnoreCase(sc.getValue("printer_kwt"))){
        try {
            conn = DriverManager.getConnection(url, "test", "test");
            PrintClosing pn = new PrintClosing(conn, "", "admin", services[0], true);
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
//                    break;
//                }
//            }





    }
}
