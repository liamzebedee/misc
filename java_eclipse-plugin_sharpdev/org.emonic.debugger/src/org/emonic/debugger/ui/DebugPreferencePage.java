package org.emonic.debugger.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.emonic.debugger.DebuggerPlugin;
import org.emonic.debugger.launching.EmonicDebugConstants;

public class DebugPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public DebugPreferencePage () {
		super(GRID);
		setPreferenceStore(DebuggerPlugin.getDefault().getPreferenceStore());
	}
	
	// field editors
	IntegerFieldEditor startingUpTimeout;
	IntegerFieldEditor eventPolling;
	IntegerFieldEditor backtraceThreadsTimeout;
	BooleanFieldEditor useOwnWebserviceLocation;
	StringFieldEditor webserviceLocation;
	IntegerFieldEditor startingPort;
	
	protected void createFieldEditors() {
		Composite composite = getFieldEditorParent();	
		final Shell shell = composite.getShell();

		Link link= new Link(composite, SWT.NONE);
		link.setText("Note that some preferences may be set on the <a>Run/Debug</a> preference page.");
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.debug.ui.DebugPreferencePage", null, null); //$NON-NLS-1$
			}
		});
		link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		
		startingUpTimeout = new IntegerFieldEditor(EmonicDebugConstants.PREF_STARTING_UP_TIMEOUT,
				"Timeout for launching the debugger (ms):",
				composite);
		addField(startingUpTimeout);
		eventPolling = new IntegerFieldEditor(EmonicDebugConstants.PREF_EVENT_POLLING,
				"Event polling interval (ms):",
				composite);
		addField(eventPolling);
		backtraceThreadsTimeout = new IntegerFieldEditor(
				EmonicDebugConstants.PREF_BACKTR_THR_TIMEOUT,
				"Timeout for getting backtrace and thread data (ms):",
				composite);
		addField(backtraceThreadsTimeout);
		useOwnWebserviceLocation = new BooleanFieldEditor(
				EmonicDebugConstants.PREF_USE_OWN_WEBSERVICE,
				"Override the location of the debugger web service with the following (Experts only!)",
				composite);
		addField(useOwnWebserviceLocation);
		webserviceLocation = new StringFieldEditor(
				EmonicDebugConstants.PREF_WEBSERVICE_LOCATION,
				"Location of the debugger web service (Experts only!):",
				composite);
		addField(webserviceLocation);
		startingPort = new IntegerFieldEditor(
				EmonicDebugConstants.PREF_STARTING_PORT,
				"Assign ports for debug web service from port:",
				composite);
		addField(startingPort);
				
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}
}
