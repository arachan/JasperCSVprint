package jaspercsvdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/**
 *
 * @author yusuke
 *
 * JRXMLとcsvデータを取り込んでプリンタに出力するプログラム
 * 
 * args[0] jrxmlファイル名と出力PDFファイル名
 * args[1] csvデータファイル
 * args[2] プリンタ設定プロパティ
 */
public class JasperCSVdata {


    public static void main(String[] args) throws IOException {
                
        //JRXMLファイル
        //File jrxmlFile = new File(get_currentpath()+"nohinsho_lastPageFooter.jrxml");
        File jrxmlFile = new File(get_currentpath()+args[0]+".jrxml");
        
        //Jasperファイル 印刷が遅いと言われたときの対策
        //String jasperReport=get_currentpath()+args[0]+".jasper";
                
        //PDFファイルの出力先
        //  File pdfFile = new File(get_currentpath()+"nohinsho.pdf");
        File pdfFile = new File(get_currentpath()+args[0]+".pdf");

        //CSVデータソース
        //File csvFile = new File(get_currentpath()+"outfile_n.csv");
        File csvFile = new File(get_currentpath()+args[1]+".csv");

        //Printer設定プロパティf
        Properties printer_settings=new Properties();
        
                
        try {
            
            //Printer設定の読み込み
            printer_settings.loadFromXML(new FileInputStream(get_currentpath()+args[2]+".xml"));
            
            
            //JRXMLファイルのコンパイル 1
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());

            //パラメータの生成
            Map<String, Object> paramMap = new HashMap<String, Object>();

            //データソースの生成
            JRCsvDataSource ds = new JRCsvDataSource(csvFile.getAbsolutePath(), "MS932");
            ds.setUseFirstRowAsHeader(true); //1行目をカラムヘッダーとして扱う

            //データの動的バインド
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramMap, ds);

            //PDF出力
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

            //プリンタ出力用クラス
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

            //出力対象のJasperPrintをセット
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

            ///印刷プリンタをプリンタ名で指定
            HashPrintServiceAttributeSet printAttribute = new HashPrintServiceAttributeSet();
            //printAttribute.add(new PrinterName("Canon LBP3800 LIPSLX", Locale.JAPAN));//
            printAttribute.add(new PrinterName(printer_settings.getProperty("PrinterName"), Locale.JAPAN));
            //用紙設定等
            HashPrintRequestAttributeSet printRequestAttribute = new HashPrintRequestAttributeSet();
            printRequestAttribute.add(getTray(printer_settings.getProperty("MediaTray")));
            printRequestAttribute.add(new Copies(new Integer("3")));
            //printRequestAttribute.add(MediaTray.MIDDLE);
            //printRequestAttribute.add(MediaSizeName.ISO_A4); //MediaTrayとMediaSizeNameは共存できません。

            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printAttribute);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttribute);

            //プリンタに直接印刷
            exporter.exportReport();

        } catch (JRException | UnsupportedEncodingException ex) {
            ex.getMessage();
        }
    }
    
    /*
     * カレントパスを絶対パスで取得するmethod
     * 
     * Thank you for MLTLab
     * 
     * http://www.mltlab.com/wp/archives/293
     */    
     private static String get_currentpath(){

	String cp=System.getProperty("java.class.path");

	String fs=System.getProperty("file.separator");

	String acp=(new File(cp)).getAbsolutePath();

	int p,q;

	for(p=0;(q=acp.indexOf(fs,p))>=0;p=q+1);

	return acp.substring(0,p);

    }
     
     private static MediaTray getTray(String trayname){
 
         /*
          * Trayを選ぶ処理
          */
         switch (trayname){
             case "MediaTray.BOTTOM":
                 return MediaTray.BOTTOM;
             case "MediaTray.ENVELOPE":
                 return MediaTray.ENVELOPE;
             case "MediaTray.LAEGE_CAPACITY":
                     return MediaTray.LARGE_CAPACITY;
             case "MediaTray.MAIN":
                 return MediaTray.MAIN;
             case "MediaTray.MANUAL":
                 return MediaTray.MANUAL;
             case "MediaTray.MIDDLE":
                 return MediaTray.MIDDLE;
             case "MediaTray.SIDE":
                  return MediaTray.SIDE;
             case "MediaTray.TOP":
                 return MediaTray.TOP;
             default:
                 return null;
         }
     }
     
}
