package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yvco1 Classe de gestion des dates et de leur format
 * 
 */
public class LoroDate {

	public static final int CDC_ROOT_DAY = 0; // !! on commence a zero !
	public static final int CDC_ROOT_MONTH = Calendar.JULY; // !! on commence a
															// zero !
	public static final int CDC_ROOT_YEAR = 1994;
	public static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	public static final String FORMAT_TIMESTAMP = "dd.MM.yyyy HH:mm:ss";
	public static final String FORMAT_TIMESTAMP_FILE = "yyyyMMdd_HHmmss";
	public static final String FORMAT_DATE = "dd.MM.yyyy";
	
	public static void main(String[] args) {
		System.out.println("6712 : " + LoroDate.getCdcNow());
		System.out.println("6711 : " + LoroDate.getCdcFromDate(LoroDate.getDateFromString("13.11.2012")));
		System.out.println("13.11.2012 : " + LoroDate.getStringFromDate(LoroDate.getDateFromString("13.11.2012"), LoroDate.FORMAT_DATE));
		System.out.println("14.11.2012 : " + LoroDate.getStringFromDate(new Date(), LoroDate.FORMAT_DATE));
		System.out.println("14.11.2012 : " + LoroDate.getDateFromCDC(6712));
		System.out.println(LoroDate.getDayOfWeekFromCdc(6655));
		System.out.println(LoroDate.getDayOfMonthFromCdc(6655));
	}

	public static Date getDateFromString(String ddMMyyyy) {
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(LoroDate.FORMAT_DATE);
			d = sdf.parse(ddMMyyyy);
		} catch (Exception e) {
			d = new Date();
		}
		return d;
	}

	public static int getMonthFromDate(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.MONTH) + 1;
	}
	
	public static String getMonthStringFromMonthInt(int month){
		String m = "";
		switch (month) {
		case 1:
			m = "janvier";
			break;
		case 2:
			m = "février";
			break;
		case 3:
			m = "mars";
			break;
		case 4:
			m = "avril";
			break;
		case 5:
			m = "mai";
			break;
		case 6:
			m = "juin";
			break;
		case 7:
			m = "juillet";
			break;
		case 8:
			m = "août";
			break;
		case 9:
			m = "septembre";
			break;
		case 10:
			m = "octobre";
			break;
		case 11:
			m = "novembre";
			break;
		case 12:
			m = "décembre";
			break;
		default:
			m = "";
			break;
		}
		return m;
	}

	public static String getMonthStringFromDate(Date d) {
		int month = LoroDate.getMonthFromDate(d);
		return getMonthStringFromMonthInt(month);
	}

	public static int getYearFromDate(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.YEAR);
	}
	
	public static int getYearNow(){
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.YEAR);
	}
	
	public static int getMonthNow(){
		Date d = new Date();
		return LoroDate.getMonthFromDate(d);
	}

	public static Date getDateFromCDC(int cdc) {
		Calendar cal = Calendar.getInstance();
		cal.set(CDC_ROOT_YEAR, CDC_ROOT_MONTH, CDC_ROOT_DAY);
		// Calendar c = Calendar.getInstance();
		Long l = (long) cdc * (long) DAY_IN_MILLIS;
		return new Date(cal.getTimeInMillis() + l);
	}

	public static int getCdcNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(LoroDate.FORMAT_DATE);
		Date d = null;
		try {
			d = sdf.parse(LoroDate.getDateNowString(LoroDate.FORMAT_DATE));
		} catch (Exception e) {
			d = new Date();
		}
		return getCdcFromDate(d);
	}

	public static int getCdcFromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setMinimalDaysInFirstWeek(1);
		Calendar cal = Calendar.getInstance();
		cal.set(CDC_ROOT_YEAR, CDC_ROOT_MONTH, CDC_ROOT_DAY);
		long diff = Math.abs(c.getTime().getTime() - cal.getTime().getTime());
		return (int) (diff / (DAY_IN_MILLIS)) + 1;
	}

	public static int getDayOfMonthFromCdc(int cdc) {
		Date d = LoroDate.getDateFromCDC(cdc);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfWeekFromCdc(int cdc) {
		Date d = LoroDate.getDateFromCDC(cdc);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (i == 0) {
			i = 7;
		}
		return i;
	}

	public static String getDayStringFromCdc(int cdc) {
		String day = "";
		switch (LoroDate.getDayOfWeekFromCdc(cdc)) {
		case 1:
			day = "lundi";
			break;
		case 2:
			day = "mardi";
			break;
		case 3:
			day = "mercredi";
			break;
		case 4:
			day = "jeudi";
			break;
		case 5:
			day = "vendredi";
			break;
		case 6:
			day = "samedi";
			break;
		case 7:
			day = "dimanche";
			break;
		default:
			day = "";
			break;

		}
		return day;
	}

	public static String getDateNowString(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}

	public static String getStringFromDate(Date date, String format) {
		Calendar c = null;
		c = Calendar.getInstance();
		c.setTime(date);
		c.setMinimalDaysInFirstWeek(1);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(c.getTime());
	}

	public static String getStringDateFromCdc(int cdc, String format) {
		Calendar c = null;
		c = Calendar.getInstance();
		c.setTime(LoroDate.getDateFromCDC(cdc));
		c.setMinimalDaysInFirstWeek(1);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(c.getTime());
	}

	public static int getCurrentMondayCdc(int cdc) {
		int dayCdc = LoroDate.getDayOfWeekFromCdc(cdc);
		return cdc - (dayCdc - 1);
	}

	public static int getLastMondayCdc(int cdc) {
		return LoroDate.getCurrentMondayCdc(cdc) - 7;
	}

	public static String getStringDateForXML(String date) {
		String[] x = date.split("\\.");
		return x[2] + "-" + x[1] + "-" + x[0];
	}
	
	public String getPrintDateNow() {
		return LoroDate.getDateNowString(LoroDate.FORMAT_TIMESTAMP);
	}

}
