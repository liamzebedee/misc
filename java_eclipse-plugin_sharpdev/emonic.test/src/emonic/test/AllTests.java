/*
 * Created on 28.07.2007
 * emonic.test emonic.test AllTests.java
 */
package emonic.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for emonic.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(FixtureCodeElement.class);
		suite.addTestSuite(FixtureFileDocument.class);
		suite.addTestSuite(FixtureCSharpCodeParser.class);
		suite.addTestSuite(FixtureCSharpFileManipulator.class);
		suite.addTestSuite(NAntBuildBuildFileManipulatorTest.class);
		suite.addTestSuite(MSBuildBuildFileManipulatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
