/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
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

/**
 *
 * @author cak-ust
 */
public class TestPrint220 {
    public TestPrint220(){
        
    }
    
    public void print(){
        PrinterJob job = PrinterJob.getPrinterJob();
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        printFile(saveToTmpFile(), services[4]);
    }
    
    private File saveToTmpFile() {
        try {
            File temp = File.createTempFile("kwt", ".tmp");
//        File fileT = new File("C:/KWT/");
//        File temp = File.createTempFile("Penjualan", ".tmp",fileT);
            // Delete temp file when program exits.
            //temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(setDoubleHeight());
            out.write("Terima Kasih");
            out.write(cancelDouble());
            out.newLine();
            out.write("Terima Kasih");
            out.close();
            return temp.getCanonicalFile();
        } catch (IOException io) {
            System.err.println(io.getMessage());
//        } catch (SQLException se) {
//            System.err.println(se.getMessage());
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
    
    private String resetPrn()
  {
    String str = String.valueOf('\033') + String.valueOf('@');
    return str;
  }

  private String draft()
  {
    String str = String.valueOf('\033') + String.valueOf('0');
    return str;
  }

  private String LQ()
  {
    String str = String.valueOf('\033') + String.valueOf('1');
    return str;
  }

  private String bold()
  {
    String str = String.valueOf('\033') + String.valueOf('E');
    return str;
  }

  private String cancelBold()
  {
    String str = String.valueOf('\033') + String.valueOf('F');
    return str;
  }

  private String setDoubleHeight()
  {
    String str = String.valueOf('\033') + String.valueOf("!") + String.valueOf("8");

    return str;
  }

  private String cancelDouble()
  {
    String str = String.valueOf('\033') + String.valueOf('!') + String.valueOf('\000');
    return str;
  }

  private String setDoubleWidth()
  {
    String str = String.valueOf('\033') + String.valueOf('!') + String.valueOf(' ');
    return str;
  }

  private String italic()
  {
    String str = String.valueOf('\033') + String.valueOf('4');
    return str;
  }

  private String cancelItalic()
  {
    String str = String.valueOf('\033') + String.valueOf('5');
    return str;
  }

  private String underLine()
  {
    String str = String.valueOf('\033') + String.valueOf('-') + String.valueOf('1');
    return str;
  }

  private String cancelUnderLine()
  {
    String str = String.valueOf('\033') + String.valueOf('-') + String.valueOf('0');
    return str;
  }

  private String cpi10()
  {
    String str = String.valueOf('\033') + String.valueOf('P');
    return str;
  }

  private String cpi12()
  {
    String str = String.valueOf('\033') + String.valueOf('M');
    return str;
  }

  private String cpi15()
  {
    String str = String.valueOf('\033') + String.valueOf('g');
    return str;
  }

  private String cpi17()
  {
    String str = String.valueOf('\033') + String.valueOf('P') + String.valueOf('\017');
    return str;
  }

  private String cpi20()
  {
    String str = String.valueOf('\033') + String.valueOf('M') + String.valueOf('\017');
    return str;
  }

  private String condenced()
  {
    String str = String.valueOf('\033') + String.valueOf('\017');
    return str;
  }

  private String condensed_24()
  {
    String str = String.valueOf('\017');
    return str;
  }

  private String cancelCondenced()
  {
    String str = String.valueOf('\022');
    return str;
  }

  private String loadFront()
  {
    String str = String.valueOf('\033') + String.valueOf('\031') + String.valueOf('F');
    return str;
  }

  private String DoubleStrike()
  {
    String str = String.valueOf('\033') + String.valueOf('G');
    return str;
  }

  private String cutter()
  {
    String str = String.valueOf('\033') + String.valueOf("i");
    return str;
  }

  private String CancelDoubleStrike()
  {
    String str = String.valueOf('\033') + String.valueOf('H');
    return str;
  }

  private String PitchPoint10()
  {
    String str = String.valueOf('\033') + String.valueOf('X') + String.valueOf(new StringBuilder().append('$').append(String.valueOf('\000')).toString()) + String.valueOf('\025');
    return str;
  }

  private String NoPitchPoint()
  {
    String str = String.valueOf('\033') + String.valueOf('X') + String.valueOf('\000');
    return str;
  }

  private String Space_1_per_36()
  {
    String str = String.valueOf('\033') + String.valueOf('3') + String.valueOf('\005');
    return str;
  }

  private String Space_1_per_72()
  {
    String str = String.valueOf('\033') + String.valueOf('3') + String.valueOf('-');
    return str;
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
    
    public static void main(String[] args) {
        new TestPrint220().print();
    }
}
