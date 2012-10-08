/*
 * Created on 27.07.2007
 * emonic.test emonic.test FixtureCodeElement.java
 */
package emonic.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;

/**
 * @author bb
 *
 */
public class FixtureCodeElement extends TestCase {
	int c = IDotNetElement.CLASS;
	/**
	 * @param name
	 */
	public FixtureCodeElement(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#CodeElement(org.emonic.base.infostructure.ICodeElementType, java.lang.String, int, int)}.
	 */
	public void testCodeElementICodeElementTypeStringIntInt() {
		CodeElement ele = generateTestElement();
		Assert.assertEquals(c,ele.getCodeType());
		Assert.assertEquals("name",ele.getElementName());
		Assert.assertEquals(10,ele.getOffset());
		Assert.assertEquals(15,ele.getLength());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#CodeElement(org.emonic.base.infostructure.ICodeElementType)}.
	 */
	public void testCodeElementICodeElementType() {
		CodeElement ele = generateTestElement();
		Assert.assertEquals(c,ele.getCodeType());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setSignature(java.lang.String)}.
	 */
	public void testSetSignature() {
		CodeElement ele = generateTestElement();
		ele.setSignature("anything nothing special, we want to get the same back! Signature needs not to be formatted");
		Assert.assertEquals("anything nothing special, we want to get the same back! Signature needs not to be formatted",ele.getSignature());
	}

//	/**
//	 * Test method for {@link org.emonic.base.infostructure.CodeElement#setSignatureAndNorm(java.lang.String)}.
//	 */
//	public void testSetSignatureAndNorm() {
//		CodeElement ele = generateTestElement();
//		ele.setSignatureAndNorm("(int a, int b)");
//		Assert.assertEquals("(int, int)",ele.getSignature());
//	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#addChild(org.emonic.base.codehierarchy.CodeElement)}.
	 */
	public void testAddChild() {
		CodeElement ele = generateTestElement();
		CodeElement c1 = new CodeElement(ele, c, "name", 10,15);
		CodeElement c2 = new CodeElement(ele, c, "name", 10,15);
		ele.addChild(c1);
		ele.addChild(c2);
		IDotNetElement[] children = ele.getChildren();
		assertEquals(c1, children[0]);
		assertEquals(c2, children[1]);
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setElementName(java.lang.String)}.
	 */
	public void testSetElementName() {
		CodeElement ele = generateTestElement();
		ele.setElementName("newname");
		Assert.assertEquals("newname",ele.getElementName());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setLength()}.
	 */
	public void testSetLength() {
		CodeElement ele = generateTestElement();
		ele.setLength(100);
		Assert.assertEquals(100,ele.getLength());	
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setTypeSignature(java.lang.String)}.
	 */
	public void testSetElementType() {
		CodeElement ele = generateTestElement();
		ele.setTypeSignature("newType");
		Assert.assertEquals("newType",ele.getTypeSignature());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setOffset(int)}.
	 */
	public void testSetOffset() {
		CodeElement ele = generateTestElement();
		ele.setOffset(100);
		Assert.assertEquals(100,ele.getOffset());	
	}

	private CodeElement generateTestElement() {
		CodeElement ele = new CodeElement(null, c, "name", 10,15);
		return ele;
	}


	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setAccessType(java.lang.String)}.
	 */
	public void testSetAccessType() {
		CodeElement ele = generateTestElement();
		ele.setAccessType("publicorprotectedoranythingelse");
		Assert.assertEquals("publicorprotectedoranythingelse",ele.getAccessType());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setDerived(java.lang.String)}.
	 */
	public void testSetDerived() {
		CodeElement ele = generateTestElement();
		ele.setDerived("derivedstring");
		Assert.assertEquals("derivedstring",ele.getDerived());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setSource(java.lang.String)}.
	 */
	public void testSetSource() {
		CodeElement ele = generateTestElement();
		ele.setSource("path_to_source");
		Assert.assertEquals("path_to_source",ele.getSource());
	}

	/**
	 * Test method for {@link org.emonic.base.codehierarchy.CodeElement#setNameoffset(int)}.
	 */
	public void testSetNameoffset() {
		CodeElement ele = generateTestElement();
		ele.setNameOffset(101);
		Assert.assertEquals(101,ele.getNameOffset());
	}

}
