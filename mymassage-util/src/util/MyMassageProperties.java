package util;

import java.util.ResourceBundle;

public class MyMassageProperties {

	private static ResourceBundle BUNDLE = ResourceBundle.getBundle("resources.mymassage-util");
	
	public static final String PARAM_SYSTEM_USER = BUNDLE.getString("param_system_user");
	public static final String PARAM_JNDI_DATASOURCE = BUNDLE.getString("param_jndi_datasource");
	public static final String PARAM_LOG_SIZE = BUNDLE.getString("param_log_size");
	public static final String PARAM_LOG_FILE1 = BUNDLE.getString("param_log_file1");
	public static final String PARAM_LOG_FILE2 = BUNDLE.getString("param_log_file2");
	
	public static final String LOG_SQL_FILE1 = BUNDLE.getString("log_sql_file1");
	public static final String LOG_SQL_FILE2 = BUNDLE.getString("log_sql_file2");
	
	public static final String PDF_TEMPLATE_REGISTER_DAY_FILE1 = BUNDLE.getString("pdf_template_register_day_file1");
	public static final String PDF_TEMPLATE_REGISTER_DAY_FILE2 = BUNDLE.getString("pdf_template_register_day_file2");
	
	public static final String EXCEL_TEMPLATE_PLANNING_MONTH_FILE1 = BUNDLE.getString("excel_template_planning_month_file1");
	public static final String EXCEL_TEMPLATE_PLANNING_MONTH_FILE2 = BUNDLE.getString("excel_template_planning_month_file2");
	public static final String EXCEL_OUTPUT_DIR1 = BUNDLE.getString("excel_output_dir1");
	public static final String EXCEL_OUTPUT_DIR2 = BUNDLE.getString("excel_output_dir2");
	
	public static final String ROLE_ADMIN = BUNDLE.getString("role_admin");
	public static final String ROLE_GESTIONNAIRE = BUNDLE.getString("role_gestionnaire");
	public static final String ROLE_SECURITAS = BUNDLE.getString("role_securitas");
	public static final String ROLE_COLLABORATEUR = BUNDLE.getString("role_collaborateur");
	
	public static final String PARAMETER_TYPE_SYSTEM = BUNDLE.getString("parameter_type_system");
	public static final String PARAMETER_TYPE_GES = BUNDLE.getString("parameter_type_ges");
	public static final String PARAMETER_ENCODING = BUNDLE.getString("parameter_encoding");
	
	public static final String EMAIL_HOST = BUNDLE.getString("email_host");
	public static final String EMAIL_FROM_EMAIL = BUNDLE.getString("email_from_email");
	public static final String EMAIL_FROM_NAME = BUNDLE.getString("email_from_name");
}