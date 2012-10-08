/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *     Harald Krapfenbauer, TU Vienna <krapfenbauer@ict.tuwien.ac.at>
 *                                     - Added remote tab if extension provided
 *******************************************************************************/
package org.emonic.debug.internal.ui.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.emonic.debug.ui.ArgumentsTab;
import org.emonic.debug.ui.EmonicDebugUI;
import org.emonic.debug.ui.MainTab;

public class DotNetLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		AbstractLaunchConfigurationTab remoteTab =
			EmonicDebugUI.getDefault().getRemoteTabProvider();

		if (remoteTab != null) {
			ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
					new MainTab(), remoteTab, new ArgumentsTab(), new EnvironmentTab(),
					new CommonTab()};
			setTabs(tabs);
		} else {
			ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
					new MainTab(), new ArgumentsTab(), new EnvironmentTab(),
					new CommonTab()};
			setTabs(tabs);
		}
	}
}
