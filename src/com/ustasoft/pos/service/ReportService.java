/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.pos.service;

import com.ustasoft.component.OSValidator;
import com.ustasoft.pos.dao.jdbc.ArInvDao;
import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import pos.MainForm;

/**
 *
 * @author cak-ust
 */
public class ReportService {
    private ArInvDao invDao=new ArInvDao();
    private final Connection conn;
    private Component aThis;
    JRExporter exporter = new JRPrintServiceExporter();
    private static PropertyResourceBundle resources;
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

    public ReportService(Connection con, Component comp) {
        this.conn = con;
        aThis = comp;
        invDao.setConn(con);
        String printerName = resources.getString("printer_kwt");

        //then find the Printer Service   
        AttributeSet aset = new HashAttributeSet();
        aset.add(new PrinterName(printerName, null));
        printService = PrintServiceLookup.lookupPrintServices(null, aset);

        if (printService == null) {
            //error message          
            JOptionPane.showMessageDialog(null, "No Printer Attached / Shared to the server");
        }
    }

    public void udfPreview(HashMap reportParam, String sReport, OrientationEnum orientationEnumValue) {
        try {
            if (aThis != null) {
                aThis.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            }
            JasperReport jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("reports/" + sReport + ".jasper"));
            jasperReport.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
//            JasperPrintManager.printReport(print,false);

            if (aThis != null) {
                aThis.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(aThis, "Report tidak ditemukan!");
                return;
            }
            if (orientationEnumValue != null) {
                print.setOrientation(orientationEnumValue);
            }
            JasperViewer.viewReport(print, false);
        } catch (JRException je) {
            System.out.println(je.getMessage());
            aThis.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void previewInPdf(HashMap reportParam, String sReport, OrientationEnum orientationEnumValue) {
        try {
            sReport = getClass().getResource("reports/" + sReport + ".jasper").toURI().getPath();
            File tmp = File.createTempFile("Print", ".pdf");
            String outFileName = tmp.toString();
            System.out.println("Report : " + outFileName);
            System.out.println("Output : " + sReport);
            JasperPrint print = JasperFillManager.fillReport(sReport, reportParam, conn);

            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Report tidak ditemukan!");
                return;
            }
            JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName.toString());
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.exportReport();
            System.out.println("Created file: " + outFileName);
            Runtime rt = Runtime.getRuntime();
            try {

                String sPdf = "\"" + outFileName + "\"";

                rt.exec(new String[]{"cmd", "/c", outFileName});
            } catch (Exception e) {
                e.printStackTrace();
            }


            //setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        } catch (JRException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void previewInWord2(HashMap reportParam, String sReport, OrientationEnum orientationEnumValue) {
        try {
            JasperReport jasperReport = null;
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("reports/" + sReport + ".jasper"));
            jasperReport.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");

            File tmp = File.createTempFile("Print", ".doc");
            String outFileName = tmp.toString();
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);

            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Report tidak ditemukan!");
                return;
            }
            aThis.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
            //JRExporter exporter = new JRDocxExporter();
            JRRtfExporter exporter = new JRRtfExporter();
            //JRDocxExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName.toString());
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.exportReport();
            System.out.println("Created file: " + outFileName);
            Runtime rt = Runtime.getRuntime();
            try {
                if(OSValidator.isWindows())
                    rt.exec(new String[]{"cmd", "/c", outFileName});
                else if(OSValidator.isUnix())
                    rt.exec("libreoffice "+ outFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tmp.deleteOnExit();
            aThis.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (JRException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    JasperReport jasperReportmkel = null;

    public void previewInWord(HashMap reportParam, String sReport, OrientationEnum orientationEnumValue) {
        try {
            jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("reports/" + sReport + ".jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReportmkel, reportParam, conn);

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
                    printService[0].getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.TRUE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
            exporter.exportReport();
        } catch (JRException ex) {
            Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cetakPembelian(HashMap reportParam) {
        udfPreview(reportParam, "Pembelian", null);
//        previewInWord(reportParam, "Pembelian", null);
    }

    public void cetakPO(HashMap reportParam) {
        udfPreview(reportParam, "PO", null);
    }

    public void cetakSO(HashMap reportParam) {
        udfPreview(reportParam, "SO", null);
    }

    public void previewPenjualan(HashMap param) {
        //udfPreview(param, "FakturPenjualan", null);
        //previewInWord(param, "FakturPenjualan", null);
        //previewInPdf(param, "FakturPenjualan", null);
        int itemCount=invDao.itemCount((Integer)param.get("idPenjualan"));
        System.out.println("itemCount: "+itemCount);
        param.put("IS_IGNORE_PAGINATION", itemCount<=9);
        
        previewInWord2(param, "FakturPenjualan", null);
    }

    public void cetakPenjualanLX300(Integer id) {
        PrintTrxService p = new PrintTrxService();
        p.setConn(conn);
        p.printJualLX300(id);
    }

    public void cetakSuratJalan(HashMap param) {
        //udfPreview(param, "SuratJalan", OrientationEnum.PORTRAIT);
        previewInWord2(param, "SuratJalan", OrientationEnum.PORTRAIT);
    }

    public void cetakReturPenjualan(HashMap param) {
        //udfPreview(param, "ReturPenjualan", null);
        previewInWord2(param, "ReturPenjualan", null);
    }

    public void cetakReturPembelian(HashMap param) {
        udfPreview(param, "PembelianRetur", null);
    }

    public void cetakKartuPiutang(HashMap param) {
        udfPreview(param, "KartuPiutang", null);
    }

    public void cetakKartuHutang(HashMap param) {
        udfPreview(param, "KartuHutang", null);
    }
    
    public void previewBuktiKas(String sNoBukti) {
        HashMap param=new HashMap();
        
        param.put("toko", MainForm.sToko);
        param.put("alamat1", MainForm.sAlamat1);
        param.put("alamat2", MainForm.sAlamat2);
        param.put("telp", MainForm.sTelp1);
        param.put("email", MainForm.sEmail);
        param.put("no_bukti", sNoBukti);
        param.put("logo", getClass().getResource("/resources/RM.jpg").toString());
        param.put("SUBREPORT_DIR", getClass().getResource("/com/ustasoft/pos/service/reports/").toString());
        //udfPreview(param, "BuktiKas", OrientationEnum.PORTRAIT);
        previewInWord2(param, "BuktiKas", OrientationEnum.PORTRAIT);
    }
    
    public void udfPreviewNeraca(String sTahun, String sBulan,Component aThis){
        JTable tabel=getSkontroTable();
        String s="select * from fn_acc_rpt_neraca('"+sBulan+"', '"+sTahun+"') as (groups text, tipe text, " +
                "acc_no varchar, acc_name varchar, lyear double precision, nyear double precision)";
        ((DefaultTableModel)tabel.getModel()).setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            int i=0, jmlBaris=0, jmlAktiva=0;
            while(rs.next()){
                jmlBaris++;
                if(rs.getString("groups").equalsIgnoreCase("Aktiva")){
                    ((DefaultTableModel)tabel.getModel()).addRow(new Object[]{
                        jmlBaris, rs.getString("acc_no"), rs.getString("acc_name"),
                        rs.getDouble("lYear"),rs.getDouble("nYear")
                    });
                }else{
                    if(jmlAktiva==0) {
                        jmlAktiva=tabel.getRowCount();
                        i=0;
                        jmlBaris=0;
                    }
                    if(jmlAktiva<=i)
                        ((DefaultTableModel)tabel.getModel()).addRow(new Object[]{
                            jmlBaris, null, null, null, null, rs.getString("acc_no"), rs.getString("acc_name"),
                            rs.getDouble("lYear"), rs.getDouble("nYear")
                        });
                    else{
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getString("acc_no"), i, 5);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getString("acc_name"), i, 6);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getDouble("lYear"), i, 7);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getDouble("nYear"), i, 8);
                    }
                    i++;
                }
            }
            HashMap reportParam=new HashMap();
            JasperReport jasperReport = null;
            reportParam.put("namaToko", MainForm.sToko);
            reportParam.put("alamat", MainForm.sAlamat1);
            reportParam.put("telp", MainForm.sTelp1);
            reportParam.put("tahun", sTahun);

            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("reports/AccNeracaWithJTable.jasper"));
            JasperPrint print = JasperFillManager.fillReport(
                    jasperReport,
                    reportParam, new JRTableModelDataSource((DefaultTableModel)tabel.getModel()));

            print.setOrientation(jasperReport.getOrientationValue());
            aThis.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(aThis, "Data tidak ditemukan");
                return;
            }
            JasperViewer.viewReport(print, false);

        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }catch (JRException ex) {
            JOptionPane.showMessageDialog(aThis, ex.getMessage());
        }

    }

    private JTable getSkontroTable(){
        JTable jTable1=new JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nomor", "AccNo_A", "AccName_A", "lYear_A", "nYear_A", "AccNo_P", "AccName_P", "lYear_P", "nYear_P"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        return jTable1;
    }

    void udfPreviewNeraca(HashMap param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
