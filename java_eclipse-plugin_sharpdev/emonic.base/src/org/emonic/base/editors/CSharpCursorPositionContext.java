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
 *      Dominik Ertl - Implementation
 */
package org.emonic.base.editors;

import java.util.HashMap;

import org.emonic.base.codecompletion.MetadataElements;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.ILocalVariable;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.ISourceRange;
import org.emonic.base.codehierarchy.ISourceReference;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.codehierarchy.IType;

/**
 * This class is inititated with an ISourceUnit and parses
 * the context of the current cursor position
 * @author dertl
 *
 */
public class CSharpCursorPositionContext {
	
	private ISourceUnit sourceUnit = null;
	private int cursorPosition;
	private boolean isInType;
	private boolean isInMethod;
	private boolean isStatic;
	private HashMap methodSignatureElements;
	private HashMap methodVariables;
	private String typename;
	private IType itype;
	
//	private CSharpCursorPositionContext(){};
	
	public CSharpCursorPositionContext(ISourceUnit srcUnit, int curPos){
		this.sourceUnit = srcUnit;
		this.cursorPosition = curPos;
		this.isInMethod = false;
		this.isInType = false;
		this.isStatic = false;
		this.methodSignatureElements = new HashMap();
		this.methodVariables = new HashMap();
		this.typename = "<nousefultype>";
		calculateActualContext();
	}
	
	/**
	 * check if cursor position is within a type and/or method
	 */
	private void calculateActualContext(){
		IType[] types = this.sourceUnit.getTypes();
		ISourceRange typesrcRangeToCheck = null;

		for(int i=0; i<types.length;i++){
			if (types[i] instanceof ISourceReference){
				typesrcRangeToCheck = ((ISourceReference)types[i]).getSourceRange();
				int typeoffset = typesrcRangeToCheck.getValidOffset();
				//check if curserPosition is in type
				if((cursorPosition >= typeoffset) &&
						(cursorPosition <= (typeoffset + typesrcRangeToCheck.getValidLength()))){

					this.isInType = true;									
					//save type
					this.itype = types[i];
					//save typename
					this.typename = types[i].getElementName();

					//check if curserPosition is in method
					IMethod[] methods = types[i].getMethods();
					for(int j=0;j<methods.length;j++){
						if (methods[j] instanceof ISourceReference){
							ISourceRange methodsrcRangeToCheck = ((ISourceReference)methods[j]).getSourceRange();
							int methodoffset = methodsrcRangeToCheck.getValidOffset();
							if((cursorPosition >= (methodoffset)) &&
									(cursorPosition <= (methodoffset + methodsrcRangeToCheck.getValidLength()))){

								this.isInMethod = true;
								//check if method is static
								if(Flags.isMethodStatic(methods[j].getFlags()) ){
									this.isStatic = true;
								}

								//fill methodSignatureElements
								fillMethodSignatureElements(methods[j]);
								//fill methodVariables
								fillVariables(methods[j]);

							}												
						}
					}
				}
			}
		}
	}
	
	/**
	 * extract variables out of method
	 * @param method
	 */
	private void fillVariables(IMethod method) {
		int numVariables = method.getLocalVars().length;
		ILocalVariable[] vars = method.getLocalVars();
		StringBuffer metadata = null;
		
		for(int i=0; i< numVariables;i++){
			metadata = new StringBuffer();
			metadata.append(vars[i].getElementName()+ " ");
			metadata.append(MetadataElements.IDotNetElement);
			metadata.append(IDotNetElement.VARIABLE + " ");
			metadata.append(MetadataElements.Type);
			metadata.append(vars[i].getTypeSignature()); //.getElementType());

			this.methodVariables.put(vars[i].getElementName(), metadata.toString());
		}
	}

	/**
	 * extract signature parameters out of method
	 * @param method
	 */
	private void fillMethodSignatureElements(IMethod method) {
		int numParams = method.getNumberOfParameters();
		String[] paramNames = method.getParameterNames();
		String[] paramTypes = method.getParameterTypes(); //FIXME needed?
		StringBuffer metadata = null;
		for(int i=0; i < numParams; i++){
			metadata = new StringBuffer();
			metadata.append(paramNames[i]+ " ");
			metadata.append(MetadataElements.IDotNetElement);
			metadata.append(IDotNetElement.VARIABLE + " ");
			metadata.append(MetadataElements.Type);
			metadata.append(paramTypes[i]);
			
			this.methodSignatureElements.put(paramNames[i], metadata.toString());
		}
		
	}

	/**
	 * indicates if current curser position is in context of method
	 * @return
	 */
	public boolean isInMethod(){
		return this.isInMethod;
	}
	
	/**
	 * indicates if current cursor position is in context of type
	 */
	public boolean isInType(){
		return this.isInType;
	}
	
	/**
	 * indicates if current cursor position ist in context of static Method OR static type
	 */
	public boolean isStatic(){
		return this.isStatic;
	}
	
	/**
	 * if context is inside a method
	 * return signature elements of the method
	 * @return
	 */
	public HashMap getMethodSignatureElements(){
		return this.methodSignatureElements;
	}
	
	/**
	 * if context inside a method
	 * their variables are available
	 * @return
	 */
	public HashMap getVariablesOfMethod(){
		return this.methodVariables;
	}

	/**
	 * 
	 * @return
	 */
	public String getTypeName(){
		return this.typename;
	}
	
	/**
	 * return itype
	 * @return
	 */
	public IType getIType(){
		return this.itype;
	}
	
}
