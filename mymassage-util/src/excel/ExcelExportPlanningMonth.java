package excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import model.LogSeverityEnum;
import model.LogTypeEnum;
import model.Massage;
import util.CustomLogger;
import util.MyMassageProperties;

public class ExcelExportPlanningMonth {
	public static String exportPlanning(Calendar monthCalendar, List<Massage> massageList){
		String excelFileOutput = null;
		try {
			// get the template file
			FileInputStream fis = null;
			if (new File(MyMassageProperties.EXCEL_TEMPLATE_PLANNING_MONTH_FILE1).exists()) {
				fis = new FileInputStream(MyMassageProperties.EXCEL_TEMPLATE_PLANNING_MONTH_FILE1);
			} else {
				fis = new FileInputStream(MyMassageProperties.EXCEL_TEMPLATE_PLANNING_MONTH_FILE2);
			}
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			fis.close();
	        
	        // add the days and the reservation
			ExcelExportPlanningMonth.writeData(workbook, monthCalendar, massageList);
	        //sauvegarde du nouveau fichiers xlsx
	        if (new File(MyMassageProperties.EXCEL_OUTPUT_DIR1).exists()){
	        	excelFileOutput = MyMassageProperties.EXCEL_OUTPUT_DIR1 + "/plannning_month" + System.currentTimeMillis() + ".xlsx";
	        } else {
	        	excelFileOutput = MyMassageProperties.EXCEL_OUTPUT_DIR2 + "/plannning_month" + System.currentTimeMillis() + ".xlsx";
	        }
	        FileOutputStream out = new FileOutputStream(excelFileOutput);
	        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
	        workbook.write(out);
	        out.close();
		} catch (Exception e){
			CustomLogger.log("ExcelExport", LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
		}
		return excelFileOutput;
	}
	
	private static void writeData(XSSFWorkbook workbook, Calendar monthCalendar, List<Massage> massageList){
		// background grey style
		CellStyle styleBackgroundGey = workbook.createCellStyle();
		styleBackgroundGey.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleBackgroundGey.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		styleBackgroundGey.setBorderRight((short) BorderStyle.THIN.ordinal());
		styleBackgroundGey.setRightBorderColor(IndexedColors.BLACK.getIndex());
		
		CellStyle styleBackgroundGeyBottom = workbook.createCellStyle();
		styleBackgroundGeyBottom.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleBackgroundGeyBottom.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		styleBackgroundGeyBottom.setBorderRight((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyBottom.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleBackgroundGeyBottom.setBorderBottom((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyBottom.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		
		CellStyle styleBackgroundGeyLeftBottom = workbook.createCellStyle();
		styleBackgroundGeyLeftBottom.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleBackgroundGeyLeftBottom.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		styleBackgroundGeyLeftBottom.setBorderLeft((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyLeftBottom.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleBackgroundGeyLeftBottom.setBorderRight((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyLeftBottom.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleBackgroundGeyLeftBottom.setBorderBottom((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyLeftBottom.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		
		CellStyle styleBackgroundGeyLeft = workbook.createCellStyle();
		styleBackgroundGeyLeft.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		styleBackgroundGeyLeft.setFillPattern((short) FillPatternType.SOLID_FOREGROUND.ordinal());
		styleBackgroundGeyLeft.setBorderRight((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyLeft.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleBackgroundGeyLeft.setBorderLeft((short) BorderStyle.THIN.ordinal());
		styleBackgroundGeyLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		
		// set the first letter of the month in uppercase
		String month = new SimpleDateFormat("MMMMM").format(monthCalendar.getTime());
		String s1 = month.substring(0,1).toUpperCase();
		month = s1 + month.substring(1);
		String year = new SimpleDateFormat("yyyy").format(monthCalendar.getTime());
		// set the name of worksheet
		workbook.setSheetName(0, month + " Tableau");
		XSSFSheet worksheet = workbook.getSheetAt(0);
		worksheet.getHeader().setLeft(month + " " + year);
		// set the month and the year
		Row row = worksheet.getRow(1);
		Cell cell = row.getCell(1);
		cell.setCellValue(month + " " + year);
		
		// initalize variables 
		int weekOfTheMonth = 1;
		Row rowDay = worksheet.getRow(weekOfTheMonth * 3);
		Row rowMassage = worksheet.getRow((weekOfTheMonth * 3) + 2);
		String firstAppointment = "";
		String lastAppointment = "";
		SimpleDateFormat planningFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat planningFormatWithoutZeros = new SimpleDateFormat("HH:");
		int cellNumber = 0;
		boolean weekend = false;
		
		// get the last day of the month
		int numberOfDays = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		boolean plusOne = false;
		for (int i = 1; i <= numberOfDays; i++ ) {
			monthCalendar.set(Calendar.DAY_OF_MONTH, i);
			weekOfTheMonth = monthCalendar.get(Calendar.WEEK_OF_MONTH);
			//TODO rajouter pour éviter que ce soit à 0
			if (weekOfTheMonth == 0) plusOne = true;
			if (plusOne) weekOfTheMonth++;
			// get the row to add the text
			rowDay = worksheet.getRow(weekOfTheMonth * 3);
			rowMassage = worksheet.getRow((weekOfTheMonth * 3) + 2);
			// get the day of the week
			int dayOfTheWeek = monthCalendar.get(Calendar.DAY_OF_WEEK);
			switch (dayOfTheWeek) {
			// sunday
			case 1:
				weekend = true;
				break;
			// monday 
			case 2:
				cellNumber = 0;
				break;
			// tuesday
			case 3:
				cellNumber = 1;
				break;
			// wednesday
			case 4:
				cellNumber = 2;
				break;
			// thursday
			case 5:
				cellNumber = 3;
				break;
			// friday
			case 6:
				cellNumber = 4;
				break;
			// saturday
			case 7:
				weekend = true;
				break;	
			}
			// if isn't the weekend set the cells value
			if(!weekend) {
				// set the day
				Cell cellDay = rowDay.getCell(cellNumber);
				cellDay.setCellValue(i);
				
				for(int a = 0; a < massageList.size(); a++) {
					Calendar calendarMassage = Calendar.getInstance();
					calendarMassage.setTime(massageList.get(a).getDate());
					int dayOfMonth = calendarMassage.get(Calendar.DAY_OF_MONTH);
					if(i == dayOfMonth) {
						// if the first appointment of the date is not set, set it
						if(firstAppointment.equals("")) {
							if(calendarMassage.get(Calendar.MINUTE) == 0) {
								firstAppointment = planningFormatWithoutZeros.format(calendarMassage.getTime());
								// replace the : by h
								firstAppointment = firstAppointment.substring(0,2) + "h"; 
							} else {
								firstAppointment = planningFormat.format(calendarMassage.getTime());
								// replace the : by h
								firstAppointment = firstAppointment.substring(0,2) + "h" + firstAppointment.substring(3);
							}
							//par défaut on met xxx
							if (lastAppointment.equals(""))
								lastAppointment = "xxx";
							
						} else {				
							if(calendarMassage.get(Calendar.MINUTE) == 0) {
								lastAppointment = planningFormatWithoutZeros.format(calendarMassage.getTime());
								// replace the : by h
								lastAppointment = lastAppointment.substring(0,2) + "h"; 
							} else {
								lastAppointment = planningFormat.format(calendarMassage.getTime());
								// replace the : by h
								lastAppointment = lastAppointment.substring(0,2) + "h" + lastAppointment.substring(3);
							}
						}
					} 
				}
				if(!lastAppointment.equals("") && !firstAppointment.equals("")) {
					// set the cell value
					Cell cellMassage = rowMassage.getCell(cellNumber);
					if (lastAppointment.equals("xxx"))
						cellMassage.setCellValue("Massage assis: " + firstAppointment);
					else
						cellMassage.setCellValue("Massage assis: " + firstAppointment + "-" + lastAppointment);
				}
			}
			
			/* Background to grey part */
			// if it's the first day of the month
			if(i == 1) {
				// if the first day of the month is not a monday, saturday and sunday
				if(dayOfTheWeek != 7 && dayOfTheWeek != 1 && dayOfTheWeek != 2) {
					
					// get the middle row
					Row middleRow = worksheet.getRow((weekOfTheMonth * 3) + 1);
					for(int a = 0; a <= (dayOfTheWeek - 3); a++) {
						Cell cellDay = rowDay.getCell(a);
						Cell cellMiddle = middleRow.getCell(a);
						Cell cellMassage = rowMassage.getCell(a);
						// empty the cell value 
						cellMiddle.setCellValue("");
						if(a == 0) {
							cellDay.setCellStyle(styleBackgroundGeyLeft);
							cellMassage.setCellStyle(styleBackgroundGeyLeftBottom);							
							cellMiddle.setCellStyle(styleBackgroundGeyLeft);
						} else {
							cellDay.setCellStyle(styleBackgroundGey);
							cellMassage.setCellStyle(styleBackgroundGeyBottom);							
							cellMiddle.setCellStyle(styleBackgroundGey);
						}								
					}
				}
			// else if it's the last day of the month
			} else if(i == numberOfDays) {
				// if it isn't the weekend and friday 
				if(dayOfTheWeek != 6 && dayOfTheWeek != 7 && dayOfTheWeek != 1) {
					Row middleRow = worksheet.getRow((weekOfTheMonth * 3) + 1);
					for(int a = dayOfTheWeek; a <= 5; a++) {
						Cell cellDay = rowDay.getCell((a-1));
						Cell cellMiddle = middleRow.getCell((a-1));
						Cell cellMassage = rowMassage.getCell((a-1));
						cellDay.setCellStyle(styleBackgroundGey);
						cellMassage.setCellStyle(styleBackgroundGeyBottom);
						// empty the cell value 
						cellMiddle.setCellValue("");
						cellMiddle.setCellStyle(styleBackgroundGey);
					}
				}
			}
			// reset values
			weekend = false;
			firstAppointment = "";
			lastAppointment = "";
		}		
	}
}
