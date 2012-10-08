package emonic.test;

import org.eclipse.core.resources.IProject;
import org.emonic.base.filemanipulators.NetProjectManipulator;

import junit.framework.TestCase;

public class FixtureProjecthandling extends TestCase {
	
	private IProject project;

	protected void setUp() throws Exception {
		super.setUp();
        project=TestUtils.createDefaultProject("nanttestproject");
        	
	}

	public void testProjectHandling(){
		org.emonic.base.filemanipulators.NetProjectManipulator projectMani = new NetProjectManipulator(project);
		assertEquals(TestUtils.DEFAULTSRCDIR, projectMani.getSrcDir());
		assertEquals(TestUtils.DEFAULTBINDIR,projectMani.getBinDir());
		assertEquals(TestUtils.DEFAULTCOPYRIGHT,projectMani.getCopyRight());
		assertEquals(TestUtils.DEFAULTNAMESPACE,projectMani.getNamespace());
		assertEquals(TestUtils.DEFAULTBUILDMECHANISM,projectMani.getBuildMechanism());
		assertEquals(TestUtils.DEFAULTBUILDFILE,projectMani.getBuildFile());
		
	}
	
	protected void tearDown() throws Exception {
		TestUtils.deleteProject("nanttestproject");
		super.tearDown();
	}
}
