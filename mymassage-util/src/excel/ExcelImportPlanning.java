package excel;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import util.CustomLogger;

public class ExcelImportPlanning {
	public static List<Massage> exportPlanning(InputStream file) {
		List<Massage> massageList = new ArrayList<Massage>();
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			file.close();
			XSSFSheet worksheet = workbook.getSheetAt(0);
			Row row = worksheet.getRow(1);
			
			// date format
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			// current row number
			int a = 1 ;
			Cell cell = row.getCell(1);
			// while the cells have values
			while(row != null && row.getCell(0) != null && row.getCell(1) != null ) {
				// get the value from the cells
				cell = row.getCell(0);
				Date date = cell.getDateCellValue();
				cell = row.getCell(1);
				String stringHour = cell.getStringCellValue();
				
				String stringDate = dateFormat.format(date);
				
				stringDate += " " + stringHour;
				
				date = dateTimeFormat.parse(stringDate);
				
				Massage massage = new Massage();
				massage.setDate(date);
				massageList.add(massage);
				a++;
				row = worksheet.getRow(a);
			}
		} catch (Exception e) {
			CustomLogger.log("ExcelImport", LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
		}
		return massageList;
	}

}
