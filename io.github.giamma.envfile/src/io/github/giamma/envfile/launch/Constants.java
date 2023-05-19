package io.github.giamma.envfile.launch;

import java.util.regex.Pattern;

import org.eclipse.debug.internal.core.LaunchConfiguration;

/**
 * Constants used in the plug-in.
 * 
 * @author gromanato
 *
 */
@SuppressWarnings("restriction")
class Constants {

	/**
	 * The id of the custom delegate in the contribution.
	 */
	static final String LAUNCHER_DELEGATE = "envfiles.delegate";
	/**
	 * A regexp pattern used to validate environment variable names.
	 */
	final static Pattern PATTERN = Pattern.compile("[a-zA-Z_]{1,}[a-zA-Z0-9_]*");
	/**
	 * The key used to store in the launch configuration the list of env file names.
	 */
	static final String ATTR_ENV_FILES = "FileEnv.filenames";
	/**
	 * The key used to set in the launch configuration the preferred launcher.
	 */
	static final String ATTR_PREFERRED_LAUNCHERS = LaunchConfiguration.ATTR_PREFERRED_LAUNCHERS;
	/**
	 * The string identifying the 'run' launch mode in the launch configuration.
	 */
	static final String RUN = "[run]";
	/**
	 * The string identifying the 'debug' launch mode in the launch configuration.
	 */
	static final String DEBUG = "[debug]";

	/**
	 * This plug-in identifier.
	 */
	static final String PLUGIN_ID = "com.github.eclipse.envfile";
	/**
	 * The JDT identifier for "Local Java Application" launch configuration type.
	 */
	static final String LOCAL_JAVA_APPLICATION_LAUNCH_TYPE = "org.eclipse.jdt.launching.localJavaApplication";

	private Constants() {
	}

}
