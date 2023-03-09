package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import model.LogSeverityEnum;
import model.LogTypeEnum;

/**
 * @author yvco1
 * 
 */

public class CustomLogger {

	/**
	 * Méthode qui log en base de données en se connectant avec le DataSource.
	 * 
	 * @param cl
	 *            Classe concernée par le log
	 * @param type
	 *            Type de log
	 * @param content
	 *            Message à loguer
	 * @param user
	 *            Utilisateur concerné par lelog
	 */
	public static void log(String className, LogSeverityEnum severity, LogTypeEnum typeEnum, Exception ex, String user) {
		if (user == null) {
			user = MyMassageProperties.PARAM_SYSTEM_USER;
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String content = sw.toString();

		content = content.replaceAll("'", "\\\\'");
		CustomLogger.finalLog(className, severity, typeEnum, content, user);
	}

	public static void log(String className, LogSeverityEnum severity, LogTypeEnum typeEnum, String s, String user) {
		if (user == null) {
			user = MyMassageProperties.PARAM_SYSTEM_USER;
		}

		String content = s.replaceAll("'", "\\\\'");
		CustomLogger.finalLog(className, severity, typeEnum, content, user);
	}

	private static void finalLog(String className, LogSeverityEnum severity, LogTypeEnum typeEnum, String content, String user) {
		Context c = null;
		DataSource d = null;
		Connection con = null;
		Statement statement = null;
		try {
			// DataBase
			c = new InitialContext();
			d = (DataSource) c.lookup(MyMassageProperties.PARAM_JNDI_DATASOURCE);
			con = d.getConnection();
			statement = con.createStatement();
			//content ne doit dépasser 1000 car
			content = "[" + className + "] : " + content;
			String oldContent = "";
			boolean cut = false;
			if (content.length() > 995){
				oldContent = content;
				content = content.substring(0, 995) + "...";
				cut = true;
			}
			//TODO utiliser le save et le bean logEJB !
			String sql = "INSERT INTO log VALUES (NULL , NOW(), '" + typeEnum + "', '" + severity + "', '" + content + "', '" + user + "')";
			statement.executeUpdate(sql);
			//TODO en fonction de la severity envoyer un email
			/*if (typeEnum != LogTypeEnum.CONNECTION) {
				// Mail
				InternetAddress[] internetAddresses = new InternetAddress[2];
				internetAddresses[0] = new InternetAddress("yves.collet@loro.ch");
				internetAddresses[1] = new InternetAddress("christina.hauenstein@loro.ch");
				// CustomLogger.sendMail("Botira DEV", type + "', '[" + cl +
				// "] " + user + " : " + content, internetAddresses);
			}*/
			if (cut){
				writeToFile(className, typeEnum, oldContent, user);
			}
		} catch (Exception e) {
			// Si le log en base de données a échoué, log dans un fichier.
			writeToFile(CustomLogger.class.getSimpleName(), LogTypeEnum.APPLICATION, "erreur lors du log en base de données : " + e, null);
			writeToFile(CustomLogger.class.getSimpleName(), typeEnum, content, user);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				// rien
			}
		}
	}

	/**
	 * Méthod qui log dans un fichier texte.
	 * 
	 * @param cl
	 *            Classe concernée par le log
	 * @param type
	 *            Type de log
	 * @param content
	 *            Message à loguer
	 * @param user
	 *            Utilisateur concerné par le log
	 */
	static void writeToFile(String className, LogTypeEnum type, String texte, String user) {
		String date = "";
		try {
			FileWriter fw = null;
			if (new File(MyMassageProperties.PARAM_LOG_FILE1).exists()) {
				fw = new FileWriter(MyMassageProperties.PARAM_LOG_FILE1, true);
			} else {
				fw = new FileWriter(MyMassageProperties.PARAM_LOG_FILE2, true);
			}
			BufferedWriter output = new BufferedWriter(fw);
			try {
				date = new SimpleDateFormat(LoroDate.FORMAT_TIMESTAMP).format(Calendar.getInstance().getTime());
			} catch (Exception e) {
				date = "xx.xx.xxxx";
			}

			output.write(user + "|" + date + "|" + type + "|[" + className + "] : " + texte);
			output.newLine();
			output.flush();
			output.close();
		} catch (IOException ioe) {

		}
	}

	

}