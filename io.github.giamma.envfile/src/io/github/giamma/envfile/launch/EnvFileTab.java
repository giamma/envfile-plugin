package io.github.giamma.envfile.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;

/**
 * A custom tab for the {@link ILaunchConfigurationDialog} which allows
 * selecting one or more text files containing environment variables that will
 * be set in the launch configuration, in addition to those already defined in
 * the operating system environment, before launching the Java program.
 * <p>
 * The text files are expected to be equivalent to Java properties file with the
 * extra requirement that keys must be valid environment variables and as such
 * must match the regexp <code>[a-zA-Z_]{1,}[a-zA-Z0-9_]*</code>
 * <p>
 * A file is rejected if it is not in the expected format or if it contains at
 * least a key that does not match the aforementioned regexp.
 * <p>
 * This tab adds to the launch configuration the list of user specified
 * environment files. Later {@link CustomJavaLaunchDelegate}, a subtype of
 * {@link ILaunchDelegate} will ensure that those files are used to populate the
 * environment when the Java process is created. When two or more delegates
 * exist for the same launch type, Eclipse would normally prompt the user to
 * select one for every launch configuration created. To circumvent this
 * behavior this plug-in defines an early starup, {@link Startup}, which upon
 * activation sets as default the standard JDT launch delegate. The custom
 * delegate will be enabled in the
 * {@link #performApply(ILaunchConfigurationWorkingCopy)} method if and only at
 * least one env file was specified by the user.
 * 
 * @author gromanato
 *
 */
public class EnvFileTab extends AbstractLaunchConfigurationTab {

	private List files;

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		setControl(container);

		Label label = new Label(container, SWT.LEAD);
		label.setLayoutData((GridDataFactory.swtDefaults().grab(true, false).span(2, 1).create()));
		label.setText(Messages.EnvFileTab_Envifornment_files_title);

		files = new List(container, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		GC gc = new GC(files);
		gc.setFont(files.getFont());
		int width = Dialog.convertWidthInCharsToPixels(gc.getFontMetrics(), 45);
		gc.dispose();

		files.setLayoutData(
				GridDataFactory.fillDefaults().grab(false, false).hint(width, files.getItemHeight() * 5).create());

		Composite buttonContainer = new Composite(container, SWT.NONE);
		buttonContainer.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		buttonContainer.setLayoutData(GridDataFactory.fillDefaults().create());

		Button addButton = new Button(buttonContainer, SWT.CENTER | SWT.PUSH);
		addButton.setText(Messages.EnvFileTab_Add_button);
		addButton.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		addButton.addSelectionListener(SelectionListener.widgetSelectedAdapter((e) -> handleAddPressed()));

		Button removeButton = new Button(buttonContainer, SWT.CENTER | SWT.PUSH);
		removeButton.setText(Messages.EnvFileTab_Remove_button);
		removeButton.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		removeButton.addSelectionListener(SelectionListener.widgetSelectedAdapter((e) -> handleRemovePressed()));

		Label message = new Label(container, SWT.LEAD | SWT.WRAP);
		message.setLayoutData(GridDataFactory.swtDefaults().create());
		message.setText(
				Messages.EnvFileTab_Environment_files_hint);

	}

	private void handleAddPressed() {
		FileDialog dlg = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.OPEN | SWT.MULTI);
		dlg.setText(Messages.EnvFileTab_Environment_File_title);
		dlg.setFilterExtensions(new String[] { "*.env", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
		String path = dlg.open();
		if (path != null) {
			for (String aFile : dlg.getFileNames()) {
				aFile = dlg.getFilterPath() + File.separator + aFile;
				try {
					EnvFiles.parseFile(aFile);
				} catch (IOException e) {
					ErrorDialog.openError(getShell(), Messages.EnvFileTab_ERROR_DIALOG_TITLE, Messages.bind(Messages.EnvFileTab_INVALID_FILE_MSG, aFile), 
							Status.error(e.getMessage(), e));
					return;
				}
			}
			for (String aFile : dlg.getFileNames()) {
				files.add(dlg.getFilterPath() + File.separator + aFile);
			}
			updateLaunchConfigurationDialog();
		}
	}

	private void handleRemovePressed() {
		int[] selection = files.getSelectionIndices();
		if (selection != null) {
			files.remove(selection);
			updateLaunchConfigurationDialog();
		}
	};

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			java.util.List<String> values = configuration.getAttribute(Constants.ATTR_ENV_FILES, new ArrayList<String>());
			files.setItems(values.toArray(new String[values.size()]));
		} catch (CoreException e) {
			// ignore
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (files.getItemCount() > 0) {
			java.util.List<String> paths = Arrays.asList(files.getItems());
			configuration.setAttribute(Constants.ATTR_ENV_FILES, paths); // save settings for launcher
			Map<String, String> launchers = new HashMap<>();
			launchers.put(Constants.DEBUG, Constants.LAUNCHER_DELEGATE);
			launchers.put(Constants.RUN, Constants.LAUNCHER_DELEGATE);
			configuration.setAttribute(Constants.ATTR_PREFERRED_LAUNCHERS, launchers);
		} else {
			configuration.setAttribute(Constants.ATTR_ENV_FILES, (List) null); // save settings for launcher
			configuration.setAttribute(Constants.ATTR_PREFERRED_LAUNCHERS, (Map<String, String>) null);
		}
	}

	@Override
	public String getName() {
		return Messages.EnvFileTab_tab_title;
	}

}
