package util;

import java.util.ResourceBundle;

public class MyMassageMessage {

	private static ResourceBundle BUNDLE = ResourceBundle.getBundle("resources.messages");

	public static String MESSAGE_ERROR = BUNDLE.getString("msg.error");
	public static String MESSAGE_SUCCESS = BUNDLE.getString("msg.success");
	public static String MESSAGE_WARNING = BUNDLE.getString("msg.warning");
	
	public static String MESSAGE_LOGIN_SUCCESS = BUNDLE.getString("msg.login.success");
	public static String MESSAGE_LOGIN_ERROR = BUNDLE.getString("msg.login.error");
	public static String MESSAGE_LOGIN_FIRST = BUNDLE.getString("msg.login.first");
	public static String MESSAGE_LOGOUT_SUCCESS = BUNDLE.getString("msg.logout.success");
	public static String MESSAGE_ACCESS_DENIED = BUNDLE.getString("msg.access.denied");
	
	public static String MESSAGE_REGISTRATION_DENIED = BUNDLE.getString("msg.registration.denied");
	public static String MESSAGE_REGISTRATION_SUCCESS = BUNDLE.getString("msg.registration.success");
	public static String MESSAGE_REGISTRATION_SUCCESS_BUT = BUNDLE.getString("msg.registration.success.but");
	public static String MESSAGE_REGISTRATION_CANCEL_DENIED = BUNDLE.getString("msg.registration.cancel.denied");
	public static String MESSAGE_REGISTRATION_CANCEL = BUNDLE.getString("msg.registration.cancel");
	
	public static String MESSAGE_EXPORT_NODATA = BUNDLE.getString("msg.export.noData");
	public static String MESSAGE_FILE_EXCEL = BUNDLE.getString("msg.file.excel");
	
	public static String MESSAGE_ERROR_SEND_EMAIL = BUNDLE.getString("msg.error.send.email");
	public static String MESSAGE_SUCCESS_SEND_EMAIL = BUNDLE.getString("msg.success.send.email");
	public static String MESSAGE_EMAIL_NODATA = BUNDLE.getString("msg.email.noData");
}
