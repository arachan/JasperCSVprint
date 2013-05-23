package jaspercsvdata;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
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
 */
public class JasperCSVdata {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        //JRXMLファイル
        File jrxmlFile = new File(get_currentpath()+"nohinsho_lastPageFooter.jrxml");

        //PDFファイルの出力先
        File pdfFile = new File(get_currentpath()+"nohinsho.pdf");

        //CSVデータソース
        File csvFile = new File(get_currentpath()+"outfile_n.csv");

        try {     
            
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
            printAttribute.add(new PrinterName("Canon LBP3800 LIPSLX", Locale.JAPAN));

            //用紙設定等
            HashPrintRequestAttributeSet printRequestAttribute = new HashPrintRequestAttributeSet();
            printRequestAttribute.add(MediaTray.MIDDLE);
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
}
