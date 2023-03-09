package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import model.LogSeverityEnum;
import model.LogTypeEnum;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * @author yvco1
 * 
 */

public class CustomAbstractSessionLog extends AbstractSessionLog implements SessionLog {

	/**
	 * Méthode qui log la requête SQL lors d'un insert ou un update fait par
	 * l'entityManager.
	 */
	@Override
	public void log(SessionLogEntry sessionLogEntry) {
		String fileLog = null;
		if (new File(MyMassageProperties.LOG_SQL_FILE1).exists()) {
			fileLog = MyMassageProperties.LOG_SQL_FILE1;
		} else {
			fileLog = MyMassageProperties.LOG_SQL_FILE2;
		}
		if (fileLog != null) {
			BufferedWriter bufferedWriter = null;
			try {
				bufferedWriter = new BufferedWriter(new FileWriter(fileLog, true));
				if (sessionLogEntry.getMessage().contains("INSERT") || sessionLogEntry.getMessage().contains("UPDATE") || sessionLogEntry.getMessage().contains("SELECT")) {
					try {
						bufferedWriter.write(LoroDate.getStringFromDate(new Date(), LoroDate.FORMAT_TIMESTAMP) + " : " + sessionLogEntry.getMessage());
						bufferedWriter.newLine();
					} catch (IOException e) {
						CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
					}
				}
			} catch (IOException e) {
				CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
			} finally {
				try {
					bufferedWriter.close();
				} catch (Exception e) {
					CustomLogger.log(this.getClass().getSimpleName(), LogSeverityEnum.ERROR, LogTypeEnum.SYSTEM, e, null);
				}
			}
		}
	}
}