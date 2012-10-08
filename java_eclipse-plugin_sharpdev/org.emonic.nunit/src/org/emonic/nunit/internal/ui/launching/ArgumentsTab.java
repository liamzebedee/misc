/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.nunit.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.emonic.debug.core.IDotNetLaunchConfigurationConstants;

public class ArgumentsTab extends AbstractLaunchConfigurationTab {

	private Text runtimeArgsText;

	private void addListeners() {
		runtimeArgsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateDialog();
			}
		});
	}

	private void createFrameworkArgsGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.ArgumentsTab_RuntimeArgumentsGroupLabel);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(1, true));

		runtimeArgsText = new Text(group, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.heightHint = 100;
		runtimeArgsText.setLayoutData(data);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createFrameworkArgsGroup(composite);

		addListeners();

		setControl(composite);
	}

	public String getName() {
		return Messages.ArgumentsTab_TabName;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String runtimeArgs = configuration.getAttribute(
					IDotNetLaunchConfigurationConstants.ATTR_RUNTIME_ARGUMENTS,
					""); //$NON-NLS-1$
			if (!runtimeArgs.equals("")) { //$NON-NLS-1$
				runtimeArgsText.setText(runtimeArgs);
			}
		} catch (CoreException e) {
			// ignored
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				IDotNetLaunchConfigurationConstants.ATTR_RUNTIME_ARGUMENTS,
				runtimeArgsText.getText().trim());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// nothing to do
	}

	private void updateDialog() {
		ILaunchConfigurationDialog dialog = getLaunchConfigurationDialog();
		if (dialog != null) {
			dialog.updateButtons();
			dialog.updateMessage();
		}
	}

}
