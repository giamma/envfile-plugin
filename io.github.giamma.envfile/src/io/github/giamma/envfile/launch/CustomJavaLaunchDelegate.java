package io.github.giamma.envfile.launch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

public class CustomJavaLaunchDelegate extends JavaLaunchDelegate {

	@Override
	public String[] getEnvironment(ILaunchConfiguration configuration) throws CoreException {
		List<String> environment = new ArrayList<>();
		String[] baseEnv = super.getEnvironment(configuration);
		if (baseEnv!=null) {
			environment.addAll(Arrays.asList(baseEnv));
		}
		
		List<String> paths = configuration.getAttribute(Constants.ATTR_ENV_FILES, (List<String>)null);
		if (paths!=null) {
			Map<String, String> map;
			try {
				map = EnvFiles.parseFiles(paths);
				for (String key : map.keySet()) {
					environment.add(key + "=" + map.get(key));
				}
			} catch (IOException e) {
				throw new CoreException(Status.error("Error processing environment file", e));
			}
		}
		return environment.isEmpty()? null : environment.toArray(new String[environment.size()]);
	}
}
