package io.github.giamma.envfile.launch;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType[] types = launchManager.getLaunchConfigurationTypes();
		for (ILaunchConfigurationType aType : types) {
			if (Constants.LOCAL_JAVA_APPLICATION_LAUNCH_TYPE.equals(aType.getIdentifier())) {
				if (!Constants.PLUGIN_ID.equals(aType.getPluginIdentifier())) {
					try {
						setPreferred(aType, ILaunchManager.DEBUG_MODE);
						setPreferred(aType, ILaunchManager.RUN_MODE);
					} catch (CoreException e) {
						return;
					}
				}
			}
		}
	}

	private void setPreferred(ILaunchConfigurationType type, String mode) throws CoreException {
		Set<String> modes = Set.of(mode);
		if (type.getPreferredDelegate(modes)==null) {
			ILaunchDelegate[] delegates = type.getDelegates(modes);
			for (ILaunchDelegate aDelegate : delegates) {
				if (!aDelegate.getPluginIdentifier().equals(Constants.PLUGIN_ID)) {
					type.setPreferredDelegate(modes, aDelegate);
					return;
				}
			}
		}
	}

}
