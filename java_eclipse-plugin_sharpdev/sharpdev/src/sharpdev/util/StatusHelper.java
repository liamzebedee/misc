package sharpdev.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import sharpdev.SharpdevPlugin;

public class StatusHelper {
	public static final Status StatusOK = new Status(IStatus.OK, SharpdevPlugin.PLUGIN_ID, IStatus.OK, "Ok.", null);
	
	public static Status makeStatus(int errorLevel, String message, Throwable e) {
		return new Status(errorLevel, SharpdevPlugin.PLUGIN_ID, errorLevel, message, e);
	}
}
