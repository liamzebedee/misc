/*
 * Created on Aug 26, 2005
 * emonic org.emonic.base.editors CSharpSourceViewer.java
 */
package org.emonic.base.editors;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.preferences.DefaultPrefsSetter;

/**
 * @author bb
 * 
 */
public class CSharpSourceViewer extends ProjectionViewer implements
		ISourceViewer {

	public static final int SHOW_OUTLINE = 51;
	
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String TABC = "\t"; //$NON-NLS-1$
	
	private IInformationPresenter outlinePresenter;

	/**
	 * @param parent
	 * @param ruler
	 * @param overviewRuler
	 * @param showsAnnotationOverview
	 * @param styles
	 */
	public CSharpSourceViewer(Composite parent, IVerticalRuler ruler,
			IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
			int styles) {
		super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		setHoverControlCreator(new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent);
			}
		});
	}

	public void configure(SourceViewerConfiguration configuration) {
		super.configure(configuration);
		if (configuration instanceof EMonoCSharpConfiguration) {
			outlinePresenter = ((EMonoCSharpConfiguration) configuration).getOutlinePresenter(this);
			outlinePresenter.install(this);
		}
	}
	
	public void unconfigure() {
		super.unconfigure();
		if (outlinePresenter != null) {
			outlinePresenter.uninstall();
		}
	}
	
	public void doOperation(int operation) {
		if (operation == SHOW_OUTLINE) {
			if (outlinePresenter != null) {
				outlinePresenter.showInformation();
			}
		} else {
			super.doOperation(operation);
		}
	}

	protected void customizeDocumentCommand(DocumentCommand command) {

		if (TABC.equals( command.text )) {
			// int line = getDocument().getLineOfOffset(command.offset);
			// int lineStartOffset = getDocument().getLineOffset(line);
			// int column = command.offset - lineStartOffset;
			command.text = getTabSpace();
		}
		super.customizeDocumentCommand(command);
	}

	protected String getTabSpace() {
		boolean usespace = true;
		int c = -1;
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		c = prefs.getInt(DefaultPrefsSetter.TABSPACE);
		usespace = prefs.getBoolean(DefaultPrefsSetter.USESPACEASTABS);
		String res = TABC;
		if (usespace) {
			if (c < 1) {
				c = 3;
			}
			res = EMPTYSTRING;
			for (int i = 0; i < c; i++) {
				res += ' ';
			}
		}
		// System.out.println("Berechnung TabSpace: " +c + "ergibr: \"" + res +
		// "\"");
		return res;
	}

}
