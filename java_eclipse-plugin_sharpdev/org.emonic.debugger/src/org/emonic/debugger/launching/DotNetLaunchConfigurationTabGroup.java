/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Bernhard Brem - initial implementation
 *      Harald Krapfenbauer - added tabs from org.emonic.debug.ui launch configuration
 *                            and a source lookup tab
 */


package org.emonic.debugger.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.emonic.debug.ui.ArgumentsTab;
import org.emonic.debug.ui.EmonicDebugUI;
import org.emonic.debug.ui.MainTab;

public class DotNetLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		AbstractLaunchConfigurationTab remoteTab =
			EmonicDebugUI.getDefault().getRemoteTabProvider();
		
		if (remoteTab != null) {
			ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
					new MainTab(),
					remoteTab,
					new ArgumentsTab(),
					new EnvironmentTab(),
					new CommonTab(),
					new SourceLookupTab()};
			setTabs(tabs);
		} else {
			ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
					new MainTab(),
					new ArgumentsTab(),
					new EnvironmentTab(),
					new CommonTab(),
					new SourceLookupTab()};
			setTabs(tabs);
		}
	}
}
