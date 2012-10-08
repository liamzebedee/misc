/*******************************************************************************
 * Copyright (c) emonic.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************
 * The values of the flags are the same of the edu.arizona.cs.mbel2.signature-
 * package, so it is easier to exchange the flags
 *
 *
 *********************************************************************************/
package org.emonic.base.codehierarchy;

public class Flags {

	public static final int PRIVATE_METHOD = 1;

	public static final int INTERNAL_METHOD = 3;

	public static final int PROTECTED_METHOD = 4;

	public static final int PROTECTED_INTERNAL_METHOD = 5;

	public static final int PUBLIC_METHOD = 6;

	public static final int STATIC_METHOD = 16;

	public static final int PRIVATE_FIELD = 1;

	public static final int INTERNAL_FIELD = 3;

	public static final int PROTECTED_FIELD = 4;

	public static final int PROTECTED_INTERNAL_FIELD = 5;

	public static final int PUBLIC_FIELD = 6;

	public static final int STATIC_FIELD = 16;

	/**
	 * Readonly property flag for a field. See the ECMA-335 specification for
	 * more details.
	 */
	public static final int INITONLY_FIELD = 32;

	public static final int PUBLIC_TYPE = 1;

	public static final int CLASS_TYPE = 0;

	public static final int INTERFACE_TYPE = 32;

	public static final int STRUCT_TYPE = 8;

	private static final int METHOD_ACCESS_MASK = 7;

	private static final int TYPE_VISIBILITY_MASK = 7;

	private static final int TYPE_CLASS_SEMANTICS_MASK = 32;

	public static boolean isMethodPrivate(int flags) {
		return (flags & METHOD_ACCESS_MASK) == PRIVATE_METHOD;
	}

	public static boolean isMethodInternal(int flags) {
		return (flags & METHOD_ACCESS_MASK) == INTERNAL_METHOD;
	}

	public static boolean isMethodProtected(int flags) {
		return (flags & METHOD_ACCESS_MASK) == PROTECTED_METHOD;
	}

	public static boolean isMethodProtectedInternal(int flags) {
		return (flags & METHOD_ACCESS_MASK) == PROTECTED_INTERNAL_METHOD;
	}

	public static boolean isMethodPublic(int flags) {
		return (flags & METHOD_ACCESS_MASK) == PUBLIC_METHOD;
	}

	public static boolean isMethodStatic(int flags) {
		flags = flags - (flags % 0x10);
		flags = flags % 0x100;
		flags = flags % 0x80;
		return (flags & STATIC_METHOD) != 0;
	}

	public static boolean isFieldPublic(int flags) {
		return (flags & PUBLIC_FIELD) != 0;
	}

	public static boolean isFieldStatic(int flags) {
		return (flags & STATIC_FIELD) != 0;
	}

	public static boolean isTypePublic(long flags) {
		return (flags & TYPE_VISIBILITY_MASK) == PUBLIC_TYPE;
	}

	public static boolean isTypeClass(long flags) {
		return (flags & TYPE_CLASS_SEMANTICS_MASK) == CLASS_TYPE;
	}

	public static boolean isTypeInterface(long flags) {
		return (flags & TYPE_CLASS_SEMANTICS_MASK) == INTERFACE_TYPE;
	}

	public static boolean isTypeStruct(long flags) {
		flags = flags - (flags % 0x18);
		flags = flags % 0x100;
		flags = flags % 0x10;
		return flags == 8;
	}

}
