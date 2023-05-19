package io.github.giamma.envfile.launch;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * 
 * Utility class for dealing with env files.
 * 
 * @author gromanato
 *
 */
final class EnvFiles {

	private EnvFiles() {

	}

	/**
	 * Parses multiple env files end returns one map containing the environment
	 * variables. This is used by the delegate that creates the launch.
	 * 
	 * @param paths the list of paths.
	 * @return a map in which each entry represents an environment variable,
	 *         possibly empty but never null;
	 * @throws IOException if the file is not an env file or contains invalid values
	 */
	static Map<String, String> parseFiles(List<String> paths) throws IOException {
		Map<String, String> variables = new HashMap<>();

		for (String string : paths) {
			Map<String, String> map;
			map = parseFile(string);
			variables.putAll(map);
		}

		return variables;

	}

	/**
	 * Parses one env files end returns one map containing the environment
	 * variables. This is used by the UI to validate the files selected by the user
	 * and immediately report invalid files, hence it throws the exception.
	 * 
	 * @param path the path to the env file.
	 * @return a map in which each entry represents an environment variable.
	 * @throws IOException if the file is not an env file or contains invalid values
	 */
	static Map<String, String> parseFile(String path) throws IOException {
		Map<String, String> variables = new HashMap<>();
		try (FileReader r = new FileReader(path)) {
			Properties p = new Properties();
			try {
				p.load(r);
			} catch (Exception e) {
				throw new IOException(e);
			}

			Set<Entry<Object, Object>> it = p.entrySet();
			for (Entry<Object, Object> entry : it) {
				String key = entry.getKey().toString();
				if (!Constants.PATTERN.matcher(key).matches()) {
					throw new IOException("Invalid key: " + key + " in file: " + path);
				} else {
					variables.put(key, entry.getValue().toString());
				}
			}
		}
		return variables;
	}

}
