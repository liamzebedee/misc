/*
 * Created on 09.03.2007
 * emonic.base org.emonic.base.perspective PerspectiveFactory.java
 */
package org.emonic.base.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.emonic.base.views.ObjectBrowserView;
import org.emonic.base.wizards.NewCSharpClassWizard;
import org.emonic.base.wizards.NewCSharpInterfaceWizard;
//import org.emonic.base.wizards.newprojectwizard.NewDotNetProjectResourceWizard;

public class PerspectiveFactory implements IPerspectiveFactory {

	private static final String ID_NEWTXTFILE = "org.eclipse.ui.editors.wizards.UntitledTextFileWizard"; //$NON-NLS-1$
	private static final String ID_NEWFILE = "org.eclipse.ui.wizards.new.file"; //$NON-NLS-1$
	private static final String ID_NEWFOLDER = "org.eclipse.ui.wizards.new.folder"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT,
				0.25f, editorArea); //$NON-NLS-1$
		topLeft.addView("org.eclipse.ui.navigator.ProjectExplorer");
		topLeft.addView(IPageLayout.ID_RES_NAV);
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM,
				0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM,
				0.72f, editorArea); //$NON-NLS-1$
		outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		outputfolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		outputfolder.addView(IPageLayout.ID_TASK_LIST);
		outputfolder.addView(IPageLayout.ID_BOOKMARKS);
		outputfolder.addView(IProgressConstants.PROGRESS_VIEW_ID);
		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(IDebugUIConstants.ID_RUN_LAUNCH_GROUP);
		layout.addShowViewShortcut(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP);
		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
		layout.addShowViewShortcut(ObjectBrowserView.VIEW_ID);

		// new actions - csharp project creation wizard
		layout.addNewWizardShortcut(NewCSharpClassWizard.WIZARD_ID);
		layout.addNewWizardShortcut(NewCSharpInterfaceWizard.ID);
		layout.addNewWizardShortcut(ID_NEWFOLDER);//$NON-NLS-1$
		layout.addNewWizardShortcut(ID_NEWFILE);//$NON-NLS-1$
		layout.addNewWizardShortcut(ID_NEWTXTFILE);//$NON-NLS-1$

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet("org.emonic.ui.csharpElementCreationActionSet");
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
	}


}
