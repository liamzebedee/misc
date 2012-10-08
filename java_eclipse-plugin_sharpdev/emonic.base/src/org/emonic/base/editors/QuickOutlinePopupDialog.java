/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.infostructure.CSharpCodeParser;
import org.emonic.base.views.SourceElementContentProvider;
import org.emonic.base.views.SourceElementLabelProvider;

public class QuickOutlinePopupDialog extends PopupDialog implements
		IInformationControl {

	private TreeViewer treeViewer;

	private ViewerFilter filter;

	private Text fFilterText;

	private ISourceViewer sourceViewer;

	private CSharpCodeParser parser;

	private String text = ""; //$NON-NLS-1$

	public QuickOutlinePopupDialog(Shell parent, ISourceViewer sourceViewer) {
		super(parent, SWT.RESIZE, true, true, true, true, null, null);
		this.sourceViewer = sourceViewer;
		parser = new CSharpCodeParser(sourceViewer.getDocument(), "");
		setInfoText("Press 'Esc' to exit the dialog.");
		// Create all controls early to preserve the life cycle of the original
		// implementation.
		create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		// Create the tree viewer
		createUIWidgetTreeViewer(parent);
		// Add listeners to the tree viewer
		createUIListenersTreeViewer();
		// Return the tree
		return treeViewer.getControl();
	}

	/**
	 * @param parent
	 */
	private void createUIWidgetTreeViewer(Composite parent) {
		// Create the tree
		Tree widget = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		// Configure the layout
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = widget.getItemHeight() * 12;
		widget.setLayoutData(data);
		// Create the tree viewer
		treeViewer = new TreeViewer(widget);
		// Add the name pattern filter
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		treeViewer.setContentProvider(new SourceElementContentProvider());
		SourceElementLabelProvider labelProvider = new SourceElementLabelProvider();
		labelProvider.setShowSigVars(true);
		treeViewer.setLabelProvider(labelProvider);
		filter = new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				CodeElement node = (CodeElement) element;
				if (node.getElementName().toLowerCase().startsWith(text)) {
					return true;
				} else {
					return checkChildrenName(node.getChildren());
				}
			}
		};
		treeViewer.addFilter(filter);
	}

	private boolean checkChildrenName(IDotNetElement[] children) {
		for (int i = 0; i < children.length; i++) {
			if (children[i].getElementName().toLowerCase().startsWith(text)) {
				return true;
			} else if (children[i] instanceof IParent &&
					checkChildrenName(((IParent) children[i]).getChildren())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private void createUIListenersTreeViewer() {
		// Get the underlying tree widget
		final Tree tree = treeViewer.getTree();

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				goToSelection();
			}
		});

		tree.addMouseMoveListener(new MouseMoveListener()	 {
			TreeItem fLastItem= null;
			public void mouseMove(MouseEvent e) {
				if (tree.equals(e.getSource())) {
					Object o= tree.getItem(new Point(e.x, e.y));
					if (o instanceof TreeItem) {
						if (!o.equals(fLastItem)) {
							fLastItem= (TreeItem)o;
							tree.setSelection(new TreeItem[] { fLastItem });
						} else if (e.y < tree.getItemHeight() / 4) {
							// Scroll up
							Point p= tree.toDisplay(e.x, e.y);
							Item item= treeViewer.scrollUp(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem= (TreeItem)item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						} else if (e.y > tree.getBounds().height - tree.getItemHeight() / 4) {
							// Scroll down
							Point p= tree.toDisplay(e.x, e.y);
							Item item= treeViewer.scrollDown(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem= (TreeItem)item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						}
					}
				}
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.button == 1 && tree.equals(e.getSource())) {
					Object o= tree.getItem(new Point(e.x, e.y));
					TreeItem selection= tree.getSelection()[0];
					if (selection.equals(o)) {
						goToSelection();
					}
				}
			}
		});
		
		// Handle key events
		tree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case 0x0D:
					goToSelection();
				case 0x1B:
					dispose();
					break;
				}
			}
		});
	}

	private void goToSelection() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		CodeElement segment = (CodeElement) selection.getFirstElement();
		if (segment != null) {
			int start = segment.getNameOffset();
			int nameLength = segment.getNameLength();
			if (nameLength == 0) {
				int length = segment.getLength();
				sourceViewer.setSelectedRange(start, length);
				sourceViewer.revealRange(start, length);
			} else {
				sourceViewer.setSelectedRange(start, nameLength);
				sourceViewer.revealRange(start, nameLength);
			}
		}
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#addDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		getShell().addFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint() {
		// Return the shell's size
		// Note that it already has the persisted size if persisting is enabled.
		return getShell().getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#dispose()
	 */
	public void dispose() {
		close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#isFocusControl()
	 */
	public boolean isFocusControl() {
		return treeViewer.getControl().isFocusControl()
				|| fFilterText.isFocusControl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#removeDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		getShell().removeFocusListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color background) {
		applyBackgroundColor(background, getContents());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setFocus()
	 */
	public void setFocus() {
		getShell().forceFocus();
		fFilterText.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setForegroundColor(Color foreground) {
		applyForegroundColor(foreground, getContents());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setInformation(java.lang.String)
	 */
	public void setInformation(String information) {
		// Ignore, see IInformationControlExtension2
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setLocation(org.eclipse.swt.graphics.Point)
	 */
	public void setLocation(Point location) {
		/*
		 * If the location is persisted, it gets managed by PopupDialog - fine.
		 * Otherwise, the location is computed in Window#getInitialLocation,
		 * which will center it in the parent shell / main monitor, which is
		 * wrong for two reasons: - we want to center over the editor / subject
		 * control, not the parent shell - the center is computed via the
		 * initalSize, which may be also wrong since the size may have been
		 * updated since via min/max sizing of
		 * AbstractInformationControlManager. In that case, override the
		 * location with the one computed by the manager. Note that the call to
		 * constrainShellSize in PopupDialog.open will still ensure that the
		 * shell is entirely visible.
		 */
		if ((getPersistBounds() == false) || (getDialogSettings() == null)) {
			getShell().setLocation(location);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		getShell().setSize(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setSizeConstraints(int,
	 *      int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// Ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			treeViewer.setInput(parser.parseDocument());
			open();
		} else {
			getShell().setVisible(false);
		}
	}

	protected IDialogSettings getDialogSettings() {
		return EMonoPlugin.getDefault().getDialogSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension#hasContents()
	 */
	public boolean hasContents() {
		if ((treeViewer == null) || (treeViewer.getInput() == null)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		// Input comes from PDESourceInfoProvider.getInformation2()
		// The input should be a model object of some sort
		// Turn it into a structured selection and set the selection in the tree
		if (input != null) {
			treeViewer.setSelection(new StructuredSelection(input));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#createTitleControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createTitleControl(Composite parent) {
		// Applies only to dialog title - not body. See createDialogArea
		// Create the text widget
		createUIWidgetFilterText(parent);
		// Add listeners to the text widget
		createUIListenersFilterText();
		// Return the text widget
		return fFilterText;
	}

	/**
	 * @param parent
	 * @return
	 */
	private void createUIWidgetFilterText(Composite parent) {
		// Create the widget
		fFilterText = new Text(parent, SWT.NONE);
		// Set the font
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		// Create the layout
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		fFilterText.setLayoutData(data);
	}

	private Object selectFirstMatch(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			CodeElement element = (CodeElement) items[i].getData();
			if (element.getElementName().toLowerCase().startsWith(text)) {
				return element;
			} else {
				Object match = selectFirstMatch(items[i].getItems());
				if (match != null) {
					return match;
				}
			}
		}
		return null;
	}

	private void createUIListenersFilterText() {
		// Handle key events
		fFilterText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) {
					goToSelection();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					// Down key was pressed
					treeViewer.getTree().setFocus();
				} else if (e.keyCode == SWT.ARROW_UP) {
					// Up key was pressed
					treeViewer.getTree().setFocus();
				} else if (e.character == 0x1B) {
					// Escape key was pressed
					dispose();
				}
			}
		});

		// Handle text modify events
		fFilterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				text = fFilterText.getText().toLowerCase();
				treeViewer.refresh(false);
				treeViewer.expandAll();
				Tree tree = treeViewer.getTree();

				Object match = selectFirstMatch(tree.getItems());
				if (match == null) {
					treeViewer.setSelection(StructuredSelection.EMPTY, true);
				} else {
					treeViewer.setSelection(new StructuredSelection(match),
							true);
				}
			}
		});
	}

}
