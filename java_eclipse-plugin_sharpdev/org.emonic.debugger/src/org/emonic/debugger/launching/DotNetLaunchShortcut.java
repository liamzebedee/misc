/*******************************************************************************
 * Copyright (c) 2007-2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *     Harald Krapfenbauer <krapfenbauer@ict.tuwien.ac.at>
 *         - Adaptation to debug mode
 *******************************************************************************/

package org.emonic.debugger.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.framework.IDotNetDebugConstants;
import org.emonic.debug.core.IDotNetLaunchConfigurationConstants;
import org.emonic.debugger.DebuggerPlugin;

public class DotNetLaunchShortcut implements ILaunchShortcut {

	public static final String ID_DOT_NET_APPLICATION_DEBUG = DebuggerPlugin.PLUGIN_ID
			+ ".dotNetApplication";

	private IEditorPart editor;

	private Shell getShell() {
		if (editor == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				Shell shell = windows[i].getShell();
				if (shell != null) {
					return shell;
				}
			}
			return null;
		} else {
			return editor.getSite().getShell();
		}
	}

	private ILaunchConfiguration findLaunchConfiguration(IFile file,
			ILaunchConfigurationType configType) {
		List candidateConfigs = Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs = getLaunchManager()
					.getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList(configs.length);
			String path = file.getProjectRelativePath().toString();
			String projectName = file.getProject().getName();
			for (int i = 0; i < configs.length; i++) {
				if (configs[i]
						.getAttribute(
								IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
								"") //$NON-NLS-1$
						.equals(path)) {
					if (configs[i]
							.getAttribute(
									IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME,
									"") //$NON-NLS-1$
							.equals(projectName)) {
						candidateConfigs.add(configs[i]);
					}
				}
			}
		} catch (CoreException e) {
			// ignored, we'll just assume that there were no matching configs
		}

		int candidateCount = candidateConfigs.size();
		if (candidateCount == 0) {
			// no matching configurations could be found, we'll create and
			// return a new one
			return createConfiguration(file, configType);
		} else if (candidateCount == 1) {
			// if there's only one, we'll just return it
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else {
			IDebugModelPresentation labelProvider = DebugUITools
					.newDebugModelPresentation();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					getShell(), labelProvider);
			dialog.setElements(candidateConfigs.toArray());
			dialog.setMultipleSelection(false);
			if (dialog.open() == Window.OK) {
				return (ILaunchConfiguration) dialog.getFirstResult();
			} else {
				return null;
			}
		}
	}

	private ILaunchConfiguration createConfiguration(IFile file,
			ILaunchConfigurationType type) {
		try {
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
					null, getLaunchManager()
							.generateUniqueLaunchConfigurationNameFrom(
									file.getName()));
			
			workingCopy.setAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_EXECUTABLE_NAME,
					file.getProjectRelativePath().toString());
			workingCopy.setAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_PROJECT_NAME, file
							.getProject().getName());
			workingCopy.setAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_FRAMEWORK_ID,
					new InstanceScope().getNode(EMonoPlugin.PLUGIN_ID).get(
							IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL,
							null));
			workingCopy.setAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					file.getParent().getRawLocation().toOSString());
			return workingCopy.doSave();
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	private void launch(IFile file) {
		ILaunchConfigurationType type = DebugPlugin
				.getDefault()
				.getLaunchManager()
				.getLaunchConfigurationType(
						ID_DOT_NET_APPLICATION_DEBUG);
		if (type != null) {
			ILaunchConfiguration config = findLaunchConfiguration(file, type);
			if (config != null) {
				DebugUITools.launch(config, ILaunchManager.DEBUG_MODE);
			}
		}
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();
			if (element instanceof IFile) {
				editor = null;
				launch((IFile) element);
			} else if (element instanceof IAdaptable) {
				IFile file = (IFile) ((IAdaptable) element)
						.getAdapter(IFile.class);
				if (file != null) {
					editor = null;
					launch(file);
				}
			}
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file != null) {
			this.editor = editor;
			launch(file);
		}
	}

}
