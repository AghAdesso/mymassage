package pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRConcurrentSwapFile;
import net.sf.jasperreports.engine.util.JRLoader;
import util.CustomLogger;
import util.MyMassageProperties;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "serial" })
public class PDFGenerator implements Serializable {

	public static void printReport(HttpServletResponse response, PDFRegistration[] activiteRecord, String nomReportACharger) throws IOException {
		JRDataSource dataSource = new JRBeanArrayDataSource(activiteRecord);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        File reportFile = null;
    	
        try {
	        reportFile = new File(nomReportACharger);
        } catch (Exception e){
        	CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, e.toString(), MyMassageProperties.PARAM_SYSTEM_USER );
        }
       
        byte[] bytes = null;
        Map parameters = new HashMap();
        
	    try {
	        bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters,  dataSource);
	    } catch (JRException e) {
	    	CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, e.toString(), MyMassageProperties.PARAM_SYSTEM_USER);
	    }
	    try {
	    	response.setContentType("application/pdf");
	        response.setContentLength(bytes.length);
	        servletOutputStream.write(bytes, 0, bytes.length);
	        servletOutputStream.flush();
	        servletOutputStream.close();
	        FacesContext.getCurrentInstance().responseComplete();
	    } catch(Exception e) {
	    	CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION,e.toString(), MyMassageProperties.PARAM_SYSTEM_USER );
	    }
	}
	
	
	public static String exportReport(PDFRegistration[] activiteRecord, String nomReportACharger) throws IOException {
        JRDataSource dataSource = 
            new JRBeanArrayDataSource(activiteRecord);
        File reportFile = null;
        try {
	        reportFile = 
	            new File(nomReportACharger);
        } catch (Exception e){
        	CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION, e.toString(), MyMassageProperties.PARAM_SYSTEM_USER);
        }
        byte[] bytes = null;
        Map parameters = new HashMap();
        String fileName = "";
	    try {
	    	JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream(reportFile.getAbsolutePath()));
	    	JRConcurrentSwapFile jrSwapFile;
	    	if (new File(MyMassageProperties.EXCEL_OUTPUT_DIR1).exists()) {
	    		jrSwapFile = new JRConcurrentSwapFile(MyMassageProperties.EXCEL_OUTPUT_DIR1 + "/" ,30,2);
	    		fileName = MyMassageProperties.EXCEL_OUTPUT_DIR1 + "/massage_du_" + activiteRecord[0].getDateSelected().replaceAll("\\.", "_") + ".pdf";
	    	} else {
	    		jrSwapFile = new JRConcurrentSwapFile(MyMassageProperties.EXCEL_OUTPUT_DIR2 + "/" ,30,2);
	    		fileName = MyMassageProperties.EXCEL_OUTPUT_DIR2 + "/massage_du_" + activiteRecord[0].getDateSelected().replaceAll("\\.", "_") + ".pdf";
	    	}
		    JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(2,jrSwapFile,true);
		    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		    JasperPrint jpPrintObj = JasperFillManager.fillReport(jasperReport,parameters,dataSource);
		    
		    JasperExportManager.exportReportToPdfFile(jpPrintObj, fileName);	    
	    } catch (JRException e) {
	    	CustomLogger.log("Export PDF", LogSeverityEnum.ERROR, LogTypeEnum.APPLICATION,  e.toString(), MyMassageProperties.PARAM_SYSTEM_USER);
	    	return null;
	    }
	    return fileName;
	}

}
