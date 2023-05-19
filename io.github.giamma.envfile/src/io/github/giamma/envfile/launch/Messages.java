package io.github.giamma.envfile.launch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
	public static String EnvFileTab_Add_button;
	public static String EnvFileTab_Envifornment_files_title;
	public static String EnvFileTab_Environment_File_title;
	public static String EnvFileTab_Environment_files_hint;
	public static String EnvFileTab_ERROR_DIALOG_TITLE;
	public static String EnvFileTab_INVALID_FILE_MSG;
	public static String EnvFileTab_Remove_button;
	public static String EnvFileTab_tab_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
