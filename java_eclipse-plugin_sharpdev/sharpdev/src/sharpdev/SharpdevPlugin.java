package sharpdev;

import javax.swing.DefaultBoundedRangeModel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.emonic.base.codecompletion.CCAdministration;
import org.emonic.base.codecompletion.jobs.AssemblyShallowExtractorJob;
import org.emonic.base.codecompletion.jobs.SourceShallowExtractorJob;
import org.emonic.base.documentation.IDocumentationParser;
import org.osgi.framework.BundleContext;

import sharpdev.core.code.assist.completion.CCExecutionListener;
import sharpdev.util.DefaultPreferences;
import sharpdev.util.StatusHelper;

/**
 * The activator class controls the plug-in life cycle
 */
public class SharpdevPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "sharpdev"; //$NON-NLS-1$

	// The shared instance
	private static SharpdevPlugin plugin;
	
	// 
	
	/**
	 * The constructor
	 */
	public SharpdevPlugin() {
		super();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		DefaultPreferences.initializeDefaults();
		
		
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(ID_EXTENSION_POINT_DOCUMENTATION);
		if (extensionPoint != null) {
			IConfigurationElement[] configs= extensionPoint.getConfigurationElements();
			int length = configs.length;
			parsers = new IDocumentationParser[length];
			for (int i= 0; i < length; i++) {
				parsers[i] = (IDocumentationParser) configs[i].
						createExecutableExtension("class"); //$NON-NLS-1$
			}
		}

		//read in assemblies from GAC
		AssemblyShallowExtractorJob gacExtractJob = new AssemblyShallowExtractorJob("Extract GAC assemblies for code completion", null);
		gacExtractJob.schedule(1000);

		//read in from opened projects
		//assemblies and src-files
		IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for(int i = 0; i< project.length; i++){
			//only add open projects
			if(project[i].isOpen()){
				//add project to cc
				CCAdministration.getInstance().addProject(project[i]);

				//read in sources from project
				SourceShallowExtractorJob srcProjExtractJob = 
						new SourceShallowExtractorJob("Extract project code files", project[i] );
				srcProjExtractJob.schedule(1000);

				//read in assemblies from project
				AssemblyShallowExtractorJob dllProjExtractJob =
						new AssemblyShallowExtractorJob("Extract project assemblies...", project[i]);
				dllProjExtractJob.schedule(1000);
			}
		}
		
		
		// Run this in WorkbenchJob so it only can run after Workbench is created
		WorkbenchJob job = new WorkbenchJob("Initialize and attach command service execution listener") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// Initialize and attach command service execution listener
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
				
				if (commandService != null) {
					CCExecutionListener ccExecutionListener = new CCExecutionListener();
					commandService.addExecutionListener( ccExecutionListener );
				}
				return StatusHelper.StatusOK;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SharpdevPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
