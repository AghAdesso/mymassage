package util;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * @author yvco1
 *
 */
public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {
 
    public void customize(Session session) {
        SessionLog aCustomLogger = new CustomAbstractSessionLog();
        aCustomLogger.setLevel(3);
        session.setSessionLog(aCustomLogger);
    }
}