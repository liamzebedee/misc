/*******************************************************************************
 * Copyright (c) 2001, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************
 * Parts of the code are derivrd from plugins standing under the CPL-license 
 * (improve c#-plugin http://www.improve-technologies.com/alpha/esharp,
 * epic-plugin https://sourceforge.net/projects/e-p-i-c/). You can get the code
 * under the original license from their homepage.
 *******************************************************************************
 * Contributors:
 *   Bernhard Brem - Initial implementation
 *******************************************************************************/
package org.emonic.base.actions;
        
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.editors.CSharpEditor;
             
public class DefaultCSharpSourceAction extends Action {

    private CSharpEditor editor;

	public DefaultCSharpSourceAction(CSharpEditor editor,String id, String label)
    {
        this.editor = editor;
        //setId(getCSharpActionId());
        setId(id);
        setText(label);
        
    }
	
	public DefaultCSharpSourceAction(CSharpEditor editor,String id, String label,String imgName )
    {
        this.editor = editor;
        //setId(getCSharpActionId());
        setId(id);
        setText(label);
        Image imge = EMonoPlugin.imageDescriptorFromPlugin(
    	imgName).createImage();
        setImageDescriptor(ImageDescriptor.createFromImage(imge));
    }
	
	

	public final void run()
    {
		DefaultCSharpSourceDelegate delegate = new DefaultCSharpSourceDelegate(); 
        delegate.setActiveEditor(this,editor);
        delegate.run(this);
    }

	public void dispose() {
		
		
	}

}
