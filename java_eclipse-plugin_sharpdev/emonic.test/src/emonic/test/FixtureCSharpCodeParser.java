/*
 * Created on 28.07.2007
 * emonic.test emonic.test FixtureCSharpCodeParser.java
 */
package emonic.test;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.filemanipulators.FileDocument;
import org.emonic.base.infostructure.CSharpCodeParser;

/**
 * @author bb
 *
 */
public class FixtureCSharpCodeParser extends TestCase {

/////////////////////////////////////////////////////////////////////////////////////////////////
// These are the c#-files tested in the test.
// Don't modify them without knowing exactly what you are doing
// Better: Add new files for additional tests!
/////////////////////////////////////////////////////////////////////////////////////////////////
	private static final String TEST1 = 
		"using System;\nnamespace bla{\n   class abc{\n      static void Main(string[] args){\n          int a = 1;\n         Console.WriteLine(a);\n       }\n   }\n}\n";
	//   01234567890123 456789012345678 90123456789012 345678901234567890123456789012345678901 234567890123456789012 3456789012345678901234567890123 456789012 34567 89 
	//   0         1          2          3         4          5         6         7         8          9        10         11        12        13         14
	
	private static final String TEST2 =
"/*     (c) all rights reserved  */\n"+
"using System.Collections;\n"+
"using System.Reflection;\n"+
"namespace bugs_by_number{\n"+
"\n"+
"  class bug_1544239{\n"+
"\n"+
"    public void simpler(string fileName){\n"+
"      ArrayList a = new ArrayList(); \n"+
"      foreach  (object o in  a){\n"+
"        System.Console.WriteLine (\"CLASS: \"+ o.ToString()     );\n"+
"      }\n"+
"    }\n"+
"// The following is invisible if a bug is triggered! See bug 1566239 in emonic bug tracker!\n"+
"    public static void Main(string[]  args){\n"+
"\n"+
"    }\n"+
"  }\n"+
"}";
	
		
	private static final String TEST3=
		"using System;\nnamespace bla{\n   class abc{\n      static void Main(string[] args){\n//Comment           \n\"String abc\"   /*2lines     \ncomment  */   }\n   }\n}\n";
	//   01234567890123 456789012345678 90123456789012 345678901234567890123456789012345678901 234567890123456789012 3 45678901234 56789012345678901 2345678901234567 89012 345 
	//   0         1          2          3         4          5         6         7         8          9        10          11         12        13         14         15
		
	private static final String TEST4=
	"/*=============================================================\n"+
	"(c) all rights reserved\n"+
	"================================================================*/\n"+
    "\n"+
	"namespace bugs_by_number{\n"+
	"class bug_1757977cs{int s;\n"+
	"public void v() {int a;\n"+
	"}\n"+
	"}\n"+
	"}\n";

	private static final String TEST5=
	"struct MyStruct : System.IDisposable {\n"+
	"\tpublic void Dispose() {\n" +
	"\t}\n"+
	"}\n";
	

	private static final String TEST6=
	"namespace xsptest{\n"+
    "    class AdamControler:IControler{\n"+
    "     }\n"+
    "}\n";

	private static final String TEST7=
	"namespace xsptest{\n"+
	"    class AdamControler<abc> : Deriv1, Deriv2 {\n"+
	"     }\n"+
	"}\n";
	
	private static final String TEST8=
		"namespace xsptest{\n"+
	    "    interface AdamControler: Deriv1, Deriv2  {\n"+
	    "     }\n"+
	    "}\n";
	
	private static final String TEST9 =
		"class SpacesTemplate <        Object       > {\n"+
	    "}\n";
	
	private static final String TEST10 = 
		"class Bug {\r\n" +
		"\t// This is a comment\r\n"+
		"\tobject hidden;\r\n"+
		"}";
	
	private static final String TEST11 =
		 "class Bug_1787456{\n"+
	     "  public void Query (string prompt) {\n"+
		 "		System.Console.WriteLine (\" (y or n) \");\n"+
		 "	}\n"+ 
		 "	public void PrintInstruction (string line)\n"+	
		 "  {\n"+
		 "    System.Console.WriteLine (\"\");\n"+
		 "	}\n"+
	     "}\n";

	private static final String TEST12 =
		 "public interface InterfaceMethodParent{\n"+
	     "  public void Method();\n" +
	     "}\n";

	private static final String TEST13 =
		 "public class FieldNotNew{\n"+
	     "  private System.Object o = new System.Object();" +
	     "}\n";

	private static final String TEST14 =
		 "public class LocalVariableInConstructor{\n"+
	     "    public LocalVariableInConstructor() {\n" +
	     "        int i;\n" +  
	     "    }\n" +
	     "}\n";

	private static final String TEST15 =
		 "public class TwoVariables {\n"+
	     "    public void X() {\n" +
	     "        int i;\n" +  
	     "        int j;\n" +
	     "    }\n" +
	     "}\n";
	
	private static final String TEST16 =
	//   012345678911234
		"class ClassA {\n" +
	//	 5
		"}";
	
	private static final String TEST17 =
	//   012345678911234
		"class ClassA {\n" +
	//	 56
		"}\t";
	
	private static final String TEST18 =
		"public class TestClass\n" +
		"{\n" + "\tpublic void X() {\n" + "\t\tint i = (int) 32;\n" + "\t\tint j;\n" + "\t}\n"+
		"}";
	
	private static final String TEST19 =
		"public class TestClass\n" +
		"{\n" + "\tpublic void X() {\n" + "\t\tobject o;\n" + "\t}\n"+
		"}";
	
	private static final String TEST20 =
	//   0123456789112345678921
		"public class ClassA {\n" +
	//	 2
		"}";
	
	private static final String TEST21 =
		"struct T {}\r\n";
	
	private static final String TEST22 =
		"public class ConstructorDestructor\n"+
		"{\n"+ 
			"public ConstructorDestructor() {\n"+ 
			"}\n"+
			"~ConstructorDestructor() {\n"+
			"}\n"+
		"}\n";

	private static final String TEST23 =
		"class ServiceInterpreter:Mono.Debugger.Frontend.Interpreter{\n" +
			"\tpublic  ServiceInterpreter(DebuggerConfiguration config,\n" +
					"\t\tDebuggerOptions options):base(true,config,options){\n" +
			"\t}\n" +
		"}\n";
		
	private static final String TEST24 =
		"namespace Mono.Debugger.Frontend\n" +
		"{\n" +
		"\tclass ClassOne\n"+
		"\t{\n" +
		"\t\tvoid X()\n" +
		"\t\t{\n"+
		"\t\t\tStringBuilder sb = new StringBuilder ();\n" + 
		"\t\t\tsb.Append (\";\");\n"+
		"\t\t\tsb.Append (\"}\");\n"+
		"\t\t}\n"+
		"\t}\n"+
		"\tclass ClassTwo\n"+
		"\t{\n" + 
		"\t}\n" +
		"}";
	
	private static final String TEST25 =
		"class ErrorClass\n" +
		"{\n" + 
		"\tvoid X()\n" +
		"\t{\n" +
		"\t\tStringBuilder sb = new StringBuilder ();\n" +
		"\t\tsb.Append(\"{\");\n" + 
		"\t\tsb.Append(\"set;\");\n" +
		"\t\tsb.Append(\"};\");\n" +
		"\t\treturn sb.ToString ();\n" +
		"\t}\n" +
		"\tvoid V()\n" +
		"\t\t{\n" +
		"\t\t}\n" +
		"}";
	
	private static final String TEST26 =
		"class AnotherError\n" +
		"{\n" +
		"\tvoid V()\n" +
		"\t{\n" +
		"\t\tthrow new Exception(\"doesn't\");\n" +
		"\t}\n" +
		"\tvoid X() {}\n" +
		"}\n" +
		"// \"";
	
	private static final String TEST27 =
		"class AnotherError\n" +
		"{\n" +
		"\tvoid V()\n" +
		"\t{\n" +
		"\t\tthrow new Exception(\"doesn't\");\n" +
		"\t}\n" +
		"\tvoid X() {}\n" +
		"}\n" +
		"// \"\n";
	
	private static final String TEST28 =
		"class AnotherError\n" +
		"{\n" +
		"\tvoid V()\n" +
		"\t{\n" +
		"\t\tthrow new Exception(\"doesn't\");\n" +
		"\t}\n" +
		"\tvoid X() {}\n" +
		"}\n" +
		"// '";
	
	private static final String TEST29 =
		"class AnotherError\n" +
		"{\n" +
		"\tvoid V()\n" +
		"\t{\n" +
		"\t\tthrow new Exception(\"doesn't\");\n" +
		"\t}\n" +
		"\tvoid X() {}\n" +
		"}\n" +
		"// '\n";
	
	private static final String TEST30 =
		"\n" +
		"using System;";
	
	private static final String TEST31 =
		"class ClassOne\n" +
		"{\n" +
		"\tstring X() {\n" +
		"\t\treturn '\"';\n" +
		"\t}\n" +
		"}\n" +
		"// '\n" +
		"class ClassTwo {}";
	
	private static final String TEST32 =
		"class ClassOne\n" +
		"{\n" +
		"\tstring X() {\n" +
		"\t\treturn '\"';\n" +
		"\t}\n" +
		"\t// '\n" +
		"void V() {}\n" +
		"}";
	
	private static final String TEST33 =
		"class ClassOne {\n" +
		"\tvoid V() {\n" +
		"\t\treturn '\"';\n" +
		"\t}\n" +
		"\tvoid X(){}\n" +
		"}\n" +
		"// \"";
	
	private static final String TEST34 =
		"class ClassOne {\n" +
		"\tenum Kind { Mult, Plus, Minus, Div };\n" +
		"\tClassOne() {}\n" +
		"}\n" +
		"class ClassTwo {}";
	
	private static final String TEST35 =
		"using System\n" +
		".\n" +
		"Xml;";

	private static final String TEST36 =
		"using ST\n\n" +
		"=\n\n" +
		"System      .\n" +
		"Threading\n" + ";";

	private static final String TEST37 =
		"class ParseGenerics\n" +
		"{\n" +	
		"\tList<object> o;\n" + 
		"\tList <object> i;\n" +
		"\tprivate List <string> s;\n" +
		"\tpublic List <    string> j = null;\n" +
		"\tList<object>[] genarray;\n" +
		"\tList<object>[   ] genarraySkip;\n" +
		"\tList  <object>      [] genarraySpace;\n" +
		"\tList<object>   [ ] genarraySpaceSkip;\n" +
		"\tobject list;\n" +
		"\tobject[] array;\n" +
		"\tobject[  ] skip;\n" +
		"\tobject [] whitespace;\n" +
		"\tobject [      ] more;\n" +
		"\tobject [][] arrTwo;\n" +
		"\tList<object>[][] genTwo;\n" +
		"\tMap<object, object> m;\n" +
		"\tMap<object,object> nospace;\n" +
		"\tobject[,] comma;\n" +
		"}";
	
	private static final String TEST38 =
		"#region\n" +
		"using System;\n" +
		"#endregion";
	
	private static final String TEST39 =
		"#region\n" +
		"using System;\n" +
		"#endregion\n" +
		"namespace NamespaceDeclaration\n" +
		"{\n" +
		"}";
	
	private static final String TEST40 =
		"class Class\n" +
		"{\n" +
		"\tpublic void V()\n" +
		"\t{\n" +
		"\t\tint i;\n" +
		"\t\tint[] j;\n" +
		"\t\tint    [] k;\n" +
		"\t\tint[,] l;\n" +
		"\t\tint[][] m;\n" +
		"\t\tint  [,   ] n;\n" +
		"\t\tint[]  [, ,, ] p;\n" +
		"\t\tint assignment = 0;\n" +
		"\t}\n" +
		"}";

	private static final String TEST41 =
		"class ParseGenericsVariables\n" +
		"{\n" +	
		"\tpublic void V()\n" +
		"\t{\n" +
		"\t\tList<object> o;\n" + 
		"\t\tList <object> i;\n" +
		"\t\tList<object>[] genarray;\n" +
		"\t\tList<object>[   ] genarraySkip;\n" +
		"\t\tList  <object>      [] genarraySpace;\n" +
		"\t\tList<object>   [ ] genarraySpaceSkip;\n" +
		"\t\tobject list;\n" +
		"\t\tobject[] array;\n" +
		"\t\tobject[  ] skip;\n" +
		"\t\tobject [] whitespace;\n" +
		"\t\tobject [      ] more;\n" +
		"\t\tobject [][] arrTwo;\n" +
		"\t\tList<object>[][] genTwo;\n" +
		"\t\tMap<object, object> m;\n" +
		"\t\tMap<object,object> nospace;\n" +
		"\t\tobject[,] comma;\n" +
		"\t}\n" +
		"}";

///////////////////////////////////////////////////////////////////////////////////////////////
// End tested c#-files
///////////////////////////////////////////////////////////////////////////////////////////////
	
	private IProject project;

	/**
	 * @param name
	 */
	public FixtureCSharpCodeParser(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		buildTestProject();
	}
 
	private void buildTestProject() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("project-csparsetest");

		if (!project.exists()) {
			project.create(null);
			project.open(null);
		} else if (!project.isOpen()) {
			project.open(null);
		}
		
		IPath p1 =new Path("test1.cs");
		IFile tstFile=project.getFile(p1);
		ByteArrayInputStream s = new  ByteArrayInputStream(TEST1.getBytes());
		if (tstFile.exists()) {
			tstFile.setContents(s, true, false, null);	
		} else {
			tstFile.create(s, true, null);
		}
		
		IPath p2 =new Path("test2.cs");
		IFile tst2File=project.getFile(p2);
		ByteArrayInputStream s2 = new  ByteArrayInputStream(TEST2.getBytes());
		if (tst2File.exists()) {
			tst2File.setContents(s2, true, false, null);	
		} else {
			tst2File.create(s2, true, null);
		}
		
		IPath p3 =new Path("test3.cs");
		IFile tst3File=project.getFile(p3);
		ByteArrayInputStream s3 = new  ByteArrayInputStream(TEST3.getBytes());
		if (tst3File.exists()) {
			tst3File.setContents(s3, true, false, null);	
		} else {
			tst3File.create(s3, true, null);
		}
		
		IPath p4 =new Path("test4.cs");
		IFile tst4File=project.getFile(p4);
		ByteArrayInputStream s4 = new  ByteArrayInputStream(TEST4.getBytes());
		if (tst4File.exists()) {
			tst4File.setContents(s4, true, false, null);	
		} else {
			tst4File.create(s4, true, null);
		}

		IPath p5 =new Path("test5.cs");
		IFile tst5File=project.getFile(p5);
		ByteArrayInputStream s5 = new  ByteArrayInputStream(TEST5.getBytes());
		if (tst5File.exists()) {
			tst5File.setContents(s5, true, false, null);	
		} else {
			tst5File.create(s5, true, null);
		}
		
		IPath p6 =new Path("test6.cs");
		IFile tst6File=project.getFile(p6);
		ByteArrayInputStream s6 = new  ByteArrayInputStream(TEST6.getBytes());
		if (tst6File.exists()) {
			tst6File.setContents(s6, true, false, null);	
		} else {
			tst6File.create(s6, true, null);
		}
		
		IPath p7 =new Path("test7.cs");
		IFile tst7File=project.getFile(p7);
		ByteArrayInputStream s7 = new  ByteArrayInputStream(TEST7.getBytes());
		if (tst7File.exists()) {
			tst7File.setContents(s7, true, false, null);	
		} else {
			tst7File.create(s7, true, null);
		}
		
		IPath p8 =new Path("test8.cs");
		IFile tst8File=project.getFile(p8);
		ByteArrayInputStream s8 = new  ByteArrayInputStream(TEST8.getBytes());
		if (tst8File.exists()) {
			tst8File.setContents(s8, true, false, null);	
		} else {
			tst8File.create(s8, true, null);
		}
		
		IPath p9 =new Path("test9.cs");
		IFile tst9File=project.getFile(p9);
		ByteArrayInputStream s9 = new  ByteArrayInputStream(TEST9.getBytes());
		if (tst9File.exists()) {
			tst9File.setContents(s9, true, false, null);	
		} else {
			tst9File.create(s9, true, null);
		}
		
		IPath p10 =new Path("test10.cs");
		IFile tst10File=project.getFile(p10);
		ByteArrayInputStream s10 = new  ByteArrayInputStream(TEST10.getBytes());
		if (tst10File.exists()) {
			tst10File.setContents(s10, true, false, null);	
		} else {
			tst10File.create(s10, true, null);
		}
		
		IPath p11 =new Path("test11.cs");
		IFile tst11File=project.getFile(p11);
		ByteArrayInputStream s11 = new  ByteArrayInputStream(TEST11.getBytes());
		if (tst11File.exists()) {
			tst11File.setContents(s11, true, false, null);	
		} else {
			tst11File.create(s11, true, null);
		}

		IPath p12 =new Path("test12.cs");
		IFile tst12File=project.getFile(p12);
		ByteArrayInputStream s12 = new  ByteArrayInputStream(TEST12.getBytes());
		if (tst12File.exists()) {
			tst12File.setContents(s12, true, false, null);	
		} else {
			tst12File.create(s12, true, null);
		}

		IPath p13 =new Path("test13.cs");
		IFile tst13File=project.getFile(p13);
		ByteArrayInputStream s13 = new  ByteArrayInputStream(TEST13.getBytes());
		if (tst13File.exists()) {
			tst13File.setContents(s13, true, false, null);	
		} else {
			tst13File.create(s13, true, null);
		}

		IPath p14 =new Path("test14.cs");
		IFile tst14File=project.getFile(p14);
		ByteArrayInputStream s14 = new  ByteArrayInputStream(TEST14.getBytes());
		if (tst14File.exists()) {
			tst14File.setContents(s14, true, false, null);	
		} else {
			tst14File.create(s14, true, null);
		}

		IPath p15 =new Path("test15.cs");
		IFile tst15File=project.getFile(p15);
		ByteArrayInputStream s15 = new  ByteArrayInputStream(TEST15.getBytes());
		if (tst15File.exists()) {
			tst15File.setContents(s15, true, false, null);	
		} else {
			tst15File.create(s15, true, null);
		}

		IPath p16 =new Path("test16.cs");
		IFile tst16File=project.getFile(p16);
		ByteArrayInputStream s16 = new ByteArrayInputStream(TEST16.getBytes());
		if (tst16File.exists()) {
			tst16File.setContents(s16, true, false, null);	
		} else {
			tst16File.create(s16, true, null);
		}

		IPath p17 =new Path("test17.cs");
		IFile tst17File=project.getFile(p17);
		ByteArrayInputStream s17 = new ByteArrayInputStream(TEST17.getBytes());
		if (tst17File.exists()) {
			tst17File.setContents(s17, true, false, null);	
		} else {
			tst17File.create(s17, true, null);
		}

		IPath p18 =new Path("test18.cs");
		IFile tst18File=project.getFile(p18);
		ByteArrayInputStream s18 = new ByteArrayInputStream(TEST18.getBytes());
		if (tst18File.exists()) {
			tst18File.setContents(s18, true, false, null);	
		} else {
			tst18File.create(s18, true, null);
		}

		IPath p19 =new Path("test19.cs");
		IFile tst19File=project.getFile(p19);
		ByteArrayInputStream s19 = new ByteArrayInputStream(TEST19.getBytes());
		if (tst19File.exists()) {
			tst19File.setContents(s19, true, false, null);	
		} else {
			tst19File.create(s19, true, null);
		}

		IPath p20 =new Path("test20.cs");
		IFile tst20File=project.getFile(p20);
		ByteArrayInputStream s20 = new ByteArrayInputStream(TEST20.getBytes());
		if (tst20File.exists()) {
			tst20File.setContents(s20, true, false, null);	
		} else {
			tst20File.create(s20, true, null);
		}

		IPath p21 =new Path("test21.cs");
		IFile tst21File=project.getFile(p21);
		ByteArrayInputStream s21 = new ByteArrayInputStream(TEST21.getBytes());
		if (tst21File.exists()) {
			tst21File.setContents(s21, true, false, null);	
		} else {
			tst21File.create(s21, true, null);
		}
		
		IPath p22 = new Path("test22.cs");
		IFile tst22File = project.getFile(p22);
		ByteArrayInputStream s22 = new ByteArrayInputStream(TEST22.getBytes());
		if (tst22File.exists()) {
			tst22File.setContents(s22, true, false, null);	
		} else {
			tst22File.create(s22, true, null);
		}
		
		IPath p23 = new Path("test23.cs");
		IFile tst23File = project.getFile(p23);
		ByteArrayInputStream s23 = new ByteArrayInputStream(TEST23.getBytes());
		if (tst23File.exists()) {
			tst23File.setContents(s23, true, false, null);
		} else {
			tst23File.create(s23, true, null);
		}
		
		IPath p24 = new Path("test24.cs");
		IFile tst24File = project.getFile(p24);
		ByteArrayInputStream s24 = new ByteArrayInputStream(TEST24.getBytes());
		if (tst24File.exists()) {
			tst24File.setContents(s24, true, false, null);
		} else {
			tst24File.create(s24, true, null);
		}
		
		IPath p25 = new Path("test25.cs");
		IFile tst25File = project.getFile(p25);
		ByteArrayInputStream s25 = new ByteArrayInputStream(TEST25.getBytes());
		if (tst25File.exists()) {
			tst25File.setContents(s25, true, false, null);
		} else {
			tst25File.create(s25, true, null);
		}
		
		IPath p26 = new Path("test26.cs");
		IFile tst26File = project.getFile(p26);
		ByteArrayInputStream s26 = new ByteArrayInputStream(TEST26.getBytes());
		if (tst26File.exists()) {
			tst26File.setContents(s26, true, false, null);
		} else {
			tst26File.create(s26, true, null);
		}
		
		IPath p27 = new Path("test27.cs");
		IFile tst27File = project.getFile(p27);
		ByteArrayInputStream s27 = new ByteArrayInputStream(TEST27.getBytes());
		if (tst27File.exists()) {
			tst27File.setContents(s27, true, false, null);
		} else {
			tst27File.create(s27, true, null);
		}
		
		IPath p28 = new Path("test28.cs");
		IFile tst28File = project.getFile(p28);
		ByteArrayInputStream s28 = new ByteArrayInputStream(TEST28.getBytes());
		if (tst28File.exists()) {
			tst28File.setContents(s28, true, false, null);
		} else {
			tst28File.create(s28, true, null);
		}
		
		IPath p29 = new Path("test29.cs");
		IFile tst29File = project.getFile(p29);
		ByteArrayInputStream s29 = new ByteArrayInputStream(TEST29.getBytes());
		if (tst29File.exists()) {
			tst29File.setContents(s29, true, false, null);
		} else {
			tst29File.create(s29, true, null);
		}
		
		IPath p30 = new Path("test30.cs");
		IFile tst30File = project.getFile(p30);
		ByteArrayInputStream s30 = new ByteArrayInputStream(TEST30.getBytes());
		if (tst30File.exists()) {
			tst30File.setContents(s30, true, false, null);
		} else {
			tst30File.create(s30, true, null);
		}
		
		IPath p31 = new Path("test31.cs");
		IFile tst31File = project.getFile(p31);
		ByteArrayInputStream s31 = new ByteArrayInputStream(TEST31.getBytes());
		if (tst31File.exists()) {
			tst31File.setContents(s31, true, false, null);
		} else {
			tst31File.create(s31, true, null);
		}
		
		IPath p32 = new Path("test32.cs");
		IFile tst32File = project.getFile(p32);
		ByteArrayInputStream s32 = new ByteArrayInputStream(TEST32.getBytes());
		if (tst32File.exists()) {
			tst32File.setContents(s32, true, false, null);
		} else {
			tst32File.create(s32, true, null);
		}
		
		IPath p33 = new Path("test33.cs");
		IFile tst33File = project.getFile(p33);
		ByteArrayInputStream s33 = new ByteArrayInputStream(TEST33.getBytes());
		if (tst33File.exists()) {
			tst33File.setContents(s33, true, false, null);
		} else {
			tst33File.create(s33, true, null);
		}
		
		IPath p34 = new Path("test34.cs");
		IFile tst34File = project.getFile(p34);
		ByteArrayInputStream s34 = new ByteArrayInputStream(TEST34.getBytes());
		if (tst34File.exists()) {
			tst34File.setContents(s34, true, false, null);
		} else {
			tst34File.create(s34, true, null);
		}

		IPath p35 = new Path("test35.cs");
		IFile tst35File = project.getFile(p35);
		ByteArrayInputStream s35 = new ByteArrayInputStream(TEST35.getBytes());
		if (tst35File.exists()) {
			tst35File.setContents(s35, true, false, null);
		} else {
			tst35File.create(s35, true, null);
		}

		IPath p36 = new Path("test36.cs");
		IFile tst36File = project.getFile(p36);
		ByteArrayInputStream s36 = new ByteArrayInputStream(TEST36.getBytes());
		if (tst36File.exists()) {
			tst36File.setContents(s36, true, false, null);
		} else {
			tst36File.create(s36, true, null);
		}

		IPath p37 = new Path("test37.cs");
		IFile tst37File = project.getFile(p37);
		ByteArrayInputStream s37 = new ByteArrayInputStream(TEST37.getBytes());
		if (tst37File.exists()) {
			tst37File.setContents(s37, true, false, null);
		} else {
			tst37File.create(s37, true, null);
		}

		IPath p38 = new Path("test38.cs");
		IFile tst38File = project.getFile(p38);
		ByteArrayInputStream s38 = new ByteArrayInputStream(TEST38.getBytes());
		if (tst38File.exists()) {
			tst38File.setContents(s38, true, false, null);
		} else {
			tst38File.create(s38, true, null);
		}

		IPath p39 = new Path("test39.cs");
		IFile tst39File = project.getFile(p39);
		ByteArrayInputStream s39 = new ByteArrayInputStream(TEST39.getBytes());
		if (tst39File.exists()) {
			tst39File.setContents(s39, true, false, null);
		} else {
			tst39File.create(s39, true, null);
		}

		IPath p40 = new Path("test40.cs");
		IFile tst40File = project.getFile(p40);
		ByteArrayInputStream s40 = new ByteArrayInputStream(TEST40.getBytes());
		if (tst40File.exists()) {
			tst40File.setContents(s40, true, false, null);
		} else {
			tst40File.create(s40, true, null);
		}

		IPath p41 = new Path("test41.cs");
		IFile tst41File = project.getFile(p41);
		ByteArrayInputStream s41 = new ByteArrayInputStream(TEST41.getBytes());
		if (tst41File.exists()) {
			tst41File.setContents(s41, true, false, null);
		} else {
			tst41File.create(s41, true, null);
		}
	}
	
	private IDocument setUpTestDocument(String fn) {
		Path p1 =new Path(fn);
		IFile tstFile=project.getFile(p1);
		IDocument fd = new FileDocument(tstFile,true);
		return fd;
	}

	/**
	 * Test method for {@link org.emonic.base.infostructure.CSharpCodeParser#parseDocument(org.eclipse.core.resources.IFile)}.
	 */
	public void testParseDocument1() {
		IDocument doc = setUpTestDocument("test1.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test1.cs").getFullPath().toOSString());
	    ISourceUnit root = cscp.parseDocument();
	    // Document sane?
	    for (int i = 0; i < TEST1.length();i++){
	    	try {
				assertEquals(TEST1.charAt(i),doc.getChar(i));
			} catch (BadLocationException e) {
				fail("Document shorter then string after "+ i + " characters!");
			}
	    }
	    Assert.assertEquals(IDotNetElement.ROOT,root.getElementType());
	    IDotNetElement[] children = root.getChildren();
	    // Root of test1.c must have 2 children: The using and the namespace
	    Assert.assertEquals(2, children.length);
	    CodeElement uc = (CodeElement) children[0];
	    Assert.assertEquals(IDotNetElement.USING_CONTAINER,uc.getElementType());
	    CodeElement u = (CodeElement) uc.getChildren()[0];
	    Assert.assertEquals(IDotNetElement.USING,u.getElementType());
	    Assert.assertEquals(0,u.getOffset());
	    assertEquals(6, u.getNameOffset());
	    Assert.assertEquals(13,u.getLength());
	    CodeElement n = (CodeElement) children[1];
	    Assert.assertEquals(IDotNetElement.NAMESPACE,n.getElementType());
	    Assert.assertEquals(14,n.getOffset());
	    // Code folding expects that the length of a type = }+1 => length is 2 to large
	    Assert.assertEquals(148-14+1,n.getLength());
	    try {
			char c = doc.getChar(n.getOffset()+n.getLength()-1);
			assertEquals('}',c);
		} catch (BadLocationException e) {
			fail("End of namespace out of doc!");
		}
		// The namespace must have one child: the class abc
		children = n.getChildren();
	    Assert.assertEquals(1, children.length);
	    CodeElement cl = (CodeElement) children[0];
	    Assert.assertEquals(cl.getElementType(),  IDotNetElement.CLASS );
	    Assert.assertEquals("abc",cl.getElementName());
	    Assert.assertEquals(32,cl.getOffset());
	    Assert.assertEquals(38,cl.getNameOffset());
	    Assert.assertEquals(3,cl.getNameLength());
	    // +2: Necessary for code folding
	    Assert.assertEquals(146-32+1,cl.getLength());

	    // The class must have 1 child: The main method
		children = cl.getChildren();
	    Assert.assertEquals(1, children.length);
	    CodeElement mm = (CodeElement) children[0];
	    Assert.assertEquals(mm.getElementType(),IDotNetElement.METHOD );
	    Assert.assertEquals("Main",mm.getElementName());
	    Assert.assertEquals(49,mm.getOffset());
	    Assert.assertEquals(61,mm.getNameOffset());
	    Assert.assertEquals(4,mm.getNameLength());
	    //+2: necessary for code folding
	    Assert.assertEquals(141-49+1,mm.getLength());
	    Assert.assertEquals("(string[]args)",mm.getSignature().replaceAll("\\s",""));
	    Assert.assertEquals(cl,mm.getParent());
	    // The main method must have one child: the var a
	    // This might change in future: May be we recognize Writline(a) as something. So we don't test on
	    // the number of children
	    children = mm.getChildren();
	    assertEquals(1, children.length);
	    
	    CodeElement v = (CodeElement) children[0];
	    Assert.assertEquals(v.getElementType() ,IDotNetElement.VARIABLE);
	    Assert.assertEquals("a",v.getElementName());
	    Assert.assertEquals(92,v.getOffset());
	    Assert.assertEquals(96,v.getNameOffset());
	    Assert.assertEquals(1,v.getNameLength());
	    // The var is valid till the end of the method; it is not valid at '}'
	    Assert.assertEquals(140-92,v.getLength());
	    Assert.assertEquals(mm,v.getParent());
	    
	}
	
	public void testParseDocument2() {
		IDocument doc = setUpTestDocument("test2.cs");
	
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test2.cs").getFullPath().toOSString());
	    ISourceUnit root = cscp.parseDocument() ;
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(3, children.length);
	    
	    assertEquals(IDotNetElement.COMMENT, children[0].getElementType());
	    
	    assertEquals(IDotNetElement.USING_CONTAINER, children[1].getElementType());
	    
	    assertEquals(IDotNetElement.NAMESPACE, children[2].getElementType());
	    assertEquals("bugs_by_number", children[2].getElementName());
	    
	    children = ((IParent) children[2]).getChildren();
	    assertEquals(1, children.length);
	    
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("bug_1544239", children[0].getElementName());

	    children = ((IParent) children[0]).getChildren();
	    assertEquals(3, children.length);
	    
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("simpler", children[0].getElementName());
	    assertEquals("void", ((CodeElement) children[0]).getTypeSignature());
	    
	    assertEquals(IDotNetElement.COMMENT, children[1].getElementType());
	    
	    assertEquals(IDotNetElement.METHOD, children[2].getElementType());
	    assertEquals("Main", children[2].getElementName());
	    assertEquals("void", ((CodeElement) children[2]).getTypeSignature());
	    assertEquals("(string[] args)", ((CodeElement) children[2]).getSignature());
	    assertEquals(true, ((CodeElement) children[2]).staticMod);
	}
	/**
	 * Test method for {@link org.emonic.base.infostructure.CSharpCodeParser#checkForString(int)}.
	 */
	public void testCheckForString() {
		IDocument doc = setUpTestDocument("test3.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test3.cs").getFullPath().toOSString());
	    Assert.assertEquals(0,cscp.checkForString(0,false));
	    Assert.assertEquals(102,cscp.checkForString(102,false));
	    Assert.assertEquals(115,cscp.checkForString(103,false));
	    try {
	    	char c = doc.getChar(114);
			Assert.assertEquals('"',c);
		} catch (BadLocationException e) {
		    Assert.fail();
		}
	    
	}

	/**
	 * Test method for {@link org.emonic.base.infostructure.CSharpCodeParser#checkForCommentedOut(int)}.
	 */
	public void testCheckForCommentedOut() {
		IDocument doc = setUpTestDocument("test3.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test3.cs").getFullPath().toOSString());
	    Assert.assertEquals(0,cscp.checkForString(0,false));
	    Assert.assertEquals(81,cscp.checkForCommentedOut(81,false));
	    Assert.assertEquals(102,cscp.checkForCommentedOut(82,false));
	    Assert.assertEquals(117,cscp.checkForCommentedOut(117,false));
	    char c;
		try {
			c = doc.getChar(142);
			Assert.assertEquals('/',c);
		} catch (BadLocationException e) {
			Assert.fail();
		}
		
		Assert.assertEquals(143,cscp.checkForCommentedOut(118,false));
	    
	}
    
	public void testParseDocument4() {
		IDocument doc = setUpTestDocument("test4.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test4.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // Root of test1.c must have 2 children: The comment and the namespace
	    assertEquals(2, children.length);
	    
	    assertEquals(IDotNetElement.COMMENT, children[0].getElementType());
	    
	    assertEquals(IDotNetElement.NAMESPACE, children[1].getElementType());
	    
	    // The namespace must have 1 code element: the class
	    children = ((IParent) children[1]).getChildren();
	    assertEquals(1, children.length);
	    
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("bug_1757977cs", children[0].getElementName());

	    // The class must have a field and a method
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(2, children.length);
	    
	    assertEquals(IDotNetElement.FIELD, children[0].getElementType());
	    assertEquals("s", children[0].getElementName()); 
	    assertEquals("int", ((CodeElement) children[0]).getTypeSignature());
	    
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	    Assert.assertEquals("v", children[1].getElementName()); 
	    assertEquals("void", ((CodeElement) children[1]).getTypeSignature());

	    // The method must have 1 variable
	    children = ((IParent) children[1]).getChildren();
	    assertEquals(1, children.length);
	    Assert.assertEquals(IDotNetElement.VARIABLE, children[0].getElementType());
	    Assert.assertEquals("a", children[0].getElementName());
	    Assert.assertEquals("int", ((CodeElement) children[0]).getTypeSignature());
	}
    
	public void testParseDocument5() {
		IDocument doc = setUpTestDocument("test5.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test5.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // there should be one children, the struct
	    assertEquals(1, children.length);
	    
	    assertEquals("MyStruct", children[0].getElementName());
	    assertEquals(IDotNetElement.STRUCT, children[0].getElementType());
	    assertEquals("System.IDisposable", ((CodeElement) children[0]).getDerived().trim());
	    
	    children = ((IParent) children[0]).getChildren();
	    // there should only be one child, the Dispose() method
	    assertEquals(1, children.length);
	    
	    assertEquals("Dispose", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("()", ((CodeElement) children[0]).getSignature());
	    assertEquals("public", ((CodeElement) children[0]).getAccessType());
	}
	
	public void testParseDocument6() {
		IDocument doc = setUpTestDocument("test6.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test6.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the namespace
	    assertEquals(1, children.length);
	    
	    assertEquals("xsptest", children[0].getElementName());
	    assertEquals(IDotNetElement.NAMESPACE, children[0].getElementType());
	   
	    
	    children = ((IParent) children[0]).getChildren();
	    // there should only be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("AdamControler", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("IControler", ((CodeElement) children[0]).getDerived());
	}
	
	public void testParseDocument7() {
		IDocument doc = setUpTestDocument("test7.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test7.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the namespace
	    assertEquals(1, children.length);
	    
	    assertEquals("xsptest", children[0].getElementName());
	    assertEquals(IDotNetElement.NAMESPACE, children[0].getElementType());
	   
	    children = ((IParent) children[0]).getChildren();
	    // there should only be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("AdamControler<abc>", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("Deriv1,Deriv2", ((CodeElement) children[0]).getDerived().replaceAll("\\s",""));
	
	}
	
	public void testParseDocument8() {
		IDocument doc = setUpTestDocument("test8.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test8.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the namespace
	    assertEquals(1, children.length);
	    
	    assertEquals("xsptest", children[0].getElementName());
	    assertEquals(IDotNetElement.NAMESPACE, children[0].getElementType());
	   

	    children = ((IParent) children[0]).getChildren();
	    // there should only be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("AdamControler", children[0].getElementName());
	    assertEquals(IDotNetElement.INTERFACE, children[0].getElementType());
	    assertEquals("Deriv1,Deriv2", ((CodeElement) children[0]).getDerived().replaceAll("\\s",""));
	}
	
	public void testParseDocument9() {
		IDocument doc = setUpTestDocument("test9.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test9.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("SpacesTemplate<Object>", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	}
	
	public void testParseDocument10() {
		IDocument doc = setUpTestDocument("test10.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test10.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("Bug", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // there should be two children, the comment and the field
	    assertEquals(2, children.length);

	    assertEquals(IDotNetElement.COMMENT, children[0].getElementType());
	    
	    assertEquals(IDotNetElement.FIELD, children[1].getElementType());
	    assertEquals("object", ((CodeElement) children[1]).getTypeSignature());
	    assertEquals("hidden", children[1].getElementName());
	}
	
	public void testParseDocument11() {
		IDocument doc = setUpTestDocument("test11.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test11.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("Bug_1787456", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());

	    children = ((IParent) children[0]).getChildren();
	    // there should be two children, the two methods
	    assertEquals(2, children.length);
	    
	    assertEquals("Query", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("PrintInstruction", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	    
	}
	
	public void testParseDocument12() {
		IDocument doc = setUpTestDocument("test12.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test12.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the interface
	    assertEquals(1, children.length);
	    
	    IDotNetElement theInterface = children[0];
	    assertEquals("InterfaceMethodParent", theInterface.getElementName());
	    assertEquals(IDotNetElement.INTERFACE, theInterface.getElementType());

	    children = ((IParent) theInterface).getChildren();
	    // there should be one children, the method
	    assertEquals(1, children.length);

	    assertEquals("Method", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals(theInterface, children[0].getParent());
	}
	
	public void testParseDocument13() {
		IDocument doc = setUpTestDocument("test13.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test13.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("FieldNotNew", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	   
	    children = ((IParent) children[0]).getChildren();
	    // there should be two children, the two methods
	    assertEquals(1, children.length);
	    
	    assertEquals("o", children[0].getElementName());
	    assertEquals(IDotNetElement.FIELD, children[0].getElementType());
	    assertFalse(((CodeElement) children[0]).newMod);
	}
	
	public void testParseDocument14() {
		IDocument doc = setUpTestDocument("test14.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test14.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("LocalVariableInConstructor", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());

	    children = ((IParent) children[0]).getChildren();
	    // there should be one children, the constructor;
	    assertEquals(1, children.length);
	    
	    CodeElement constructor = (CodeElement) children[0];
	    assertEquals("LocalVariableInConstructor", constructor.getElementName());
	    assertEquals("()", constructor.getSignature());
	    assertEquals("()", constructor.getNormedSignature());
	    assertEquals(IDotNetElement.CONSTRUCTOR, constructor.getElementType());
	    
	    children = constructor.getChildren();
	    assertEquals(1, children.length);
	    
	    CodeElement localVariable = (CodeElement) children[0];
	    assertEquals(IDotNetElement.VARIABLE, localVariable.getElementType());
	    assertEquals("i", localVariable.getElementName());
	    assertEquals("int", localVariable.getTypeSignature());
	}

	public void testParseDocument15() {
		IDocument doc = setUpTestDocument("test15.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test15.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    
	    assertEquals("TwoVariables", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());

	    children = ((IParent) children[0]).getChildren();
	    // there should be one children, the constructor;
	    assertEquals(1, children.length);
	    
	    CodeElement method = (CodeElement) children[0];
	    assertEquals("X", method.getElementName());
	    assertEquals("()", method.getSignature());
	    assertEquals("()", method.getNormedSignature());
	    assertEquals(IDotNetElement.METHOD, method.getElementType());
	    
	    children = method.getChildren();
	    assertEquals(2, children.length);
	    
	    CodeElement localVariable = (CodeElement) children[0];
	    assertEquals(IDotNetElement.VARIABLE, localVariable.getElementType());
	    assertEquals("i", localVariable.getElementName());
	    assertEquals("int", localVariable.getTypeSignature());
	    
	    localVariable = (CodeElement) children[1];
	    assertEquals(IDotNetElement.VARIABLE, localVariable.getElementType());
	    assertEquals("j", localVariable.getElementName());
	    assertEquals("int", localVariable.getTypeSignature());
	}

	public void testParseDocument16() {
		IDocument doc = setUpTestDocument("test16.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test16.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(1, children.length);
	    
	    CodeElement a = (CodeElement) children[0];
	    assertEquals(0, a.getOffset());
	    assertEquals(16, a.getLength());
	    
	    assertEquals(14, a.getValidOffset());
	    assertEquals(1, a.getValidLength());
	}

	public void testParseDocument17() {
		IDocument doc = setUpTestDocument("test17.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test17.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(1, children.length);
	    
	    CodeElement a = (CodeElement) children[0];
	    assertEquals(0, a.getOffset());
	    assertEquals(16, a.getLength());
	    
	    assertEquals(14, a.getValidOffset());
	    assertEquals(1, a.getValidLength());
	}

	public void testParseDocument18() {
		IDocument doc = setUpTestDocument("test18.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test18.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(1, children.length);
	    assertEquals("TestClass", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(1, children.length);
	    assertEquals("X", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(2, children.length);
	    
	    assertEquals("i", children[0].getElementName());
	    assertEquals("j", children[1].getElementName());
	}

	public void testParseDocument19() {
		IDocument doc = setUpTestDocument("test19.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test19.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(1, children.length);
	    assertEquals("TestClass", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(1, children.length);
	    assertEquals("X", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.VARIABLE, children[0].getElementType());
	    assertEquals("o", children[0].getElementName());
	}

	public void testParseDocument20() {
		IDocument doc = setUpTestDocument("test20.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test20.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    assertEquals(1, children.length);
	    
	    CodeElement a = (CodeElement) children[0];
	    assertEquals(0, a.getOffset());
	    assertEquals(23, a.getLength());
	    
	    assertEquals(21, a.getValidOffset());
	    assertEquals(1, a.getValidLength());
	}

	public void testParseDocument21() {
		IDocument doc = setUpTestDocument("test21.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test20.cs").getFullPath().toOSString());
	    cscp.parseDocument();
	}
	
	public void testParseDocument22() {
		IDocument doc = setUpTestDocument("test22.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test22.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    assertEquals("ConstructorDestructor", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());

	    children = ((IParent) children[0]).getChildren();
	    // there should be two children, the constructor and destructor
	    assertEquals(2, children.length);
	    
	    CodeElement constructor = (CodeElement) children[0];
	    assertEquals("ConstructorDestructor", constructor.getElementName());
	    assertEquals("()", constructor.getSignature());
	    assertEquals("()", constructor.getNormedSignature());
	    assertEquals(IDotNetElement.CONSTRUCTOR, constructor.getElementType());
	    
	    CodeElement destructor = (CodeElement) children[1];
	    assertEquals("ConstructorDestructor", destructor.getElementName());
	    assertEquals("()", destructor.getSignature());
	    assertEquals("()", destructor.getNormedSignature());
	    assertEquals(IDotNetElement.DESTRUCTOR, destructor.getElementType());
	}
	
	// tests constructor that calls the base constructor
	public void testParseDocument23() {
		IDocument doc = setUpTestDocument("test23.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test23.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());

	    IDotNetElement[] children = root.getChildren();
	    // there should be one child, the class
	    assertEquals(1, children.length);
	    assertEquals("ServiceInterpreter", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("Mono.Debugger.Frontend.Interpreter", ((CodeElement)children[0]).getDerived());

	    children = ((IParent) children[0]).getChildren();
	    // there should be one child, the constructor
	    assertEquals(1, children.length);
	    
	    CodeElement constructor = (CodeElement) children[0];
	    assertEquals("ServiceInterpreter", constructor.getElementName());
	    assertEquals("(DebuggerConfiguration config, DebuggerOptions options)",
	    		constructor.getSignature());
	    assertEquals("(DebuggerConfiguration, DebuggerOptions)",
	    		constructor.getNormedSignature());
	    assertEquals(IDotNetElement.CONSTRUCTOR, constructor.getElementType());
	}
	
	public void testParseDocument24() {
		IDocument doc = setUpTestDocument("test24.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test24.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the namespace
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.NAMESPACE, children[0].getElementType());
	    assertEquals("Mono.Debugger.Frontend", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two classes
	    assertEquals(2, children.length);
	    assertEquals("ClassOne", children[0].getElementName());
	    assertEquals("ClassTwo", children[1].getElementName());
	}
	
	public void testParseDocument25() {
		IDocument doc = setUpTestDocument("test25.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test25.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the namespace
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("ErrorClass", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two classes
	    assertEquals(2, children.length);
	    assertEquals("X", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("V", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	}
	
	public void testParseDocument26() {
		IDocument doc = setUpTestDocument("test26.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test26.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the class
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("AnotherError", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two methods
	    assertEquals(2, children.length);
	    assertEquals("V", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	}
	
	public void testParseDocument27() {
		IDocument doc = setUpTestDocument("test27.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test27.cs").getFullPath().toOSString());
	    ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the class
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("AnotherError", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two methods
	    assertEquals(2, children.length);
	    assertEquals("V", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	}
	
	public void testParseDocument28() {
		IDocument doc = setUpTestDocument("test28.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test28.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the class
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("AnotherError", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two methods
	    assertEquals(2, children.length);
	    assertEquals("V", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	}
	
	public void testParseDocument29() {
		IDocument doc = setUpTestDocument("test29.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test29.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the class
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("AnotherError", children[0].getElementName());
	    
	    children = ((IParent) children[0]).getChildren();
	    // two children, the two methods
	    assertEquals(2, children.length);
	    assertEquals("V", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[1].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	}
	
	public void testParseDocument30() {
		IDocument doc = setUpTestDocument("test30.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test30.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one child, the class
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.USING_CONTAINER, children[0].getElementType());
	    
	    children = ((IParent) children[0]).getChildren();
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.USING, children[0].getElementType());
	    assertEquals("System", children[0].getElementName());
	}
	
	public void testParseDocument31() {
		IDocument doc = setUpTestDocument("test31.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test31.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // three children, ClassOne, the comment, and ClassTwo
	    assertEquals(3, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("ClassOne", children[0].getElementName());
	    assertEquals(IDotNetElement.COMMENT, children[1].getElementType());
	    assertEquals(IDotNetElement.CLASS, children[2].getElementType());
	    assertEquals("ClassTwo", children[2].getElementName());
	    
	    IParent classOne = (IParent) children[0];
	    IParent comment = (IParent) children[1];
	    IParent classTwo = (IParent) children[2];
	    assertTrue(classOne.hasChildren());
	    assertFalse(comment.hasChildren());
	    assertFalse(classTwo.hasChildren());
	    
	    children = classOne.getChildren();
	    // the one X() method
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[0].getElementName());
	}
	
	public void testParseDocument32() {
		IDocument doc = setUpTestDocument("test32.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test32.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // one class, ClassOne
	    assertEquals(1, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("ClassOne", children[0].getElementName());
	    
	    IParent classOne = (IParent) children[0];
	    assertTrue(classOne.hasChildren());
	    
	    children = classOne.getChildren();
	    // three children, X(), the comment, and V()
	    assertEquals(3, children.length);
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("X", children[0].getElementName());
	    assertEquals(IDotNetElement.COMMENT, children[1].getElementType());
	    assertEquals(IDotNetElement.METHOD, children[2].getElementType());
	    assertEquals("V", children[2].getElementName());
	}
	
	public void testParseDocument33() {
		IDocument doc = setUpTestDocument("test33.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test33.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // two children, the class and the comment
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("ClassOne", children[0].getElementName());
	    assertEquals(IDotNetElement.COMMENT, children[1].getElementType());
	    
	    IParent classOne = (IParent) children[0];
	    assertTrue(classOne.hasChildren());
	    assertFalse(((IParent) children[1]).hasChildren());
	    
	    children = classOne.getChildren();
	    // two methods
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.METHOD, children[0].getElementType());
	    assertEquals("V", children[0].getElementName());
	    assertEquals(IDotNetElement.METHOD, children[1].getElementType());
	    assertEquals("X", children[1].getElementName());
	}
	
	public void testParseDocument34() {
		IDocument doc = setUpTestDocument("test34.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile("test34.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
	    assertEquals(IDotNetElement.ROOT, root.getElementType());
	    
	    IDotNetElement[] children = root.getChildren();
	    // two children, the two classes
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.CLASS, children[0].getElementType());
	    assertEquals("ClassOne", children[0].getElementName());
	    assertEquals(IDotNetElement.CLASS, children[1].getElementType());
	    assertEquals("ClassTwo", children[1].getElementName());
	    
	    IParent classOne = (IParent) children[0];
	    assertTrue(classOne.hasChildren());
	    assertFalse(((IParent) children[1]).hasChildren());
	    
	    children = classOne.getChildren();
	    // two, enum and constructor
	    assertEquals(2, children.length);
	    assertEquals(IDotNetElement.ENUM, children[0].getElementType());
	    assertEquals("Kind", children[0].getElementName());
	    assertEquals(IDotNetElement.CONSTRUCTOR, children[1].getElementType());
	    assertEquals("ClassOne", children[1].getElementName());
	}

	public void testParseDocument35() {
		IDocument doc = setUpTestDocument("test35.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test35.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());

		IDotNetElement[] children = root.getChildren();
		// two children, the two classes
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.USING_CONTAINER, children[0]
				.getElementType());

		IDotNetElement container = children[0];
		children = ((IParent) container).getChildren();
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.USING, children[0].getElementType());
		assertEquals("System.Xml", children[0].getElementName());
	}

	public void testParseDocument36() {
		IDocument doc = setUpTestDocument("test36.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test36.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());

		IDotNetElement[] children = root.getChildren();
		// two children, the two classes
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.USING_CONTAINER, children[0]
				.getElementType());

		IDotNetElement container = children[0];
		children = ((IParent) container).getChildren();
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.USING, children[0].getElementType());
		assertEquals("ST (System.Threading)", children[0].getElementName());
	}

	public void testParseDocument37() {
		IDocument doc = setUpTestDocument("test37.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test37.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());

		IDotNetElement[] children = root.getChildren();
		// one children, the class
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.CLASS, children[0]
				.getElementType());
		assertEquals("ParseGenerics", children[0].getElementName());

		children = ((IParent) children[0]).getChildren();
		assertEquals(18, children.length);
		
		CodeElement field = (CodeElement) children[0];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("o", field.getElementName());
		assertEquals("List<object>", field.getTypeSignature());
		
		field = (CodeElement) children[1];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("i", field.getElementName());
		assertEquals("List<object>", field.getTypeSignature());
		
		field = (CodeElement) children[2];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("s", field.getElementName());
		assertEquals("List<string>", field.getTypeSignature());
		assertEquals("private", field.getAccessType());
		
		field = (CodeElement) children[3];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("j", field.getElementName());
		assertEquals("List<string>", field.getTypeSignature());
		assertEquals("public", field.getAccessType());
		
		field = (CodeElement) children[4];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("genarray", field.getElementName());
		assertEquals("List<object>[]", field.getTypeSignature());
		
		field = (CodeElement) children[5];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("genarraySkip", field.getElementName());
		assertEquals("List<object>[]", field.getTypeSignature());
		
		field = (CodeElement) children[6];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("genarraySpace", field.getElementName());
		assertEquals("List<object>[]", field.getTypeSignature());
		
		field = (CodeElement) children[7];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("genarraySpaceSkip", field.getElementName());
		assertEquals("List<object>[]", field.getTypeSignature());
		
		field = (CodeElement) children[8];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("list", field.getElementName());
		assertEquals("object", field.getTypeSignature());
		
		field = (CodeElement) children[9];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("array", field.getElementName());
		assertEquals("object[]", field.getTypeSignature());
		
		field = (CodeElement) children[10];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("skip", field.getElementName());
		assertEquals("object[]", field.getTypeSignature());
		
		field = (CodeElement) children[11];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("whitespace", field.getElementName());
		assertEquals("object[]", field.getTypeSignature());
		
		field = (CodeElement) children[12];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("more", field.getElementName());
		assertEquals("object[]", field.getTypeSignature());
		
		field = (CodeElement) children[13];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("arrTwo", field.getElementName());
		assertEquals("object[][]", field.getTypeSignature());
		
		field = (CodeElement) children[14];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("genTwo", field.getElementName());
		assertEquals("List<object>[][]", field.getTypeSignature());
		
		field = (CodeElement) children[15];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("m", field.getElementName());
		assertEquals("Map<object, object>", field.getTypeSignature());
		
		field = (CodeElement) children[16];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("nospace", field.getElementName());
		assertEquals("Map<object, object>", field.getTypeSignature());
		
		field = (CodeElement) children[17];
		assertEquals(IDotNetElement.FIELD, field.getElementType());
		assertEquals("comma", field.getElementName());
		assertEquals("object[,]", field.getTypeSignature());
	}

	public void testParseDocument38() {
		IDocument doc = setUpTestDocument("test38.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test38.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());
		
		IDotNetElement[] children = root.getChildren();
		assertEquals(1, children.length);
		
		CodeElement element = (CodeElement) children[0];
		assertEquals(IDotNetElement.USING_CONTAINER, element.getElementType());
		children = element.getChildren();
		assertEquals(1, children.length);
		
		element = (CodeElement) children[0];
		assertEquals(IDotNetElement.USING, element.getElementType());
		assertEquals("System", element.getElementName());
	}

	public void testParseDocument39() {
		IDocument doc = setUpTestDocument("test39.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test39.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());
		
		IDotNetElement[] children = root.getChildren();
		assertEquals(2, children.length);
		
		CodeElement element = (CodeElement) children[0];
		assertEquals(IDotNetElement.USING_CONTAINER, element.getElementType());
		IDotNetElement[] usingChildren = element.getChildren();
		assertEquals(1, usingChildren.length);
		
		element = (CodeElement) usingChildren[0];
		assertEquals(IDotNetElement.USING, element.getElementType());
		assertEquals("System", element.getElementName());
		
		element = (CodeElement) children[1];
		assertEquals(IDotNetElement.NAMESPACE, element.getElementType());
		assertEquals("NamespaceDeclaration", element.getElementName());
	}

	public void testParseDocument40() {
		IDocument doc = setUpTestDocument("test40.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test40.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());
		
		IDotNetElement[] children = root.getChildren();
		assertEquals(1, children.length);
		
		CodeElement element = (CodeElement) children[0];
		assertEquals(IDotNetElement.CLASS, element.getElementType());
		assertEquals("Class", element.getElementName());
		children = element.getChildren();
		assertEquals(1, children.length);
		
		element = (CodeElement) children[0];
		assertEquals(IDotNetElement.METHOD, element.getElementType());
		assertEquals("V", element.getElementName());
		children = element.getChildren();
		assertEquals(8, children.length);
		
		assertEquals(IDotNetElement.VARIABLE, children[0].getElementType());
		assertEquals("i", children[0].getElementName());
		assertEquals("int", ((CodeElement) children[0]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[1].getElementType());
		assertEquals("j", children[1].getElementName());
		assertEquals("int[]", ((CodeElement) children[1]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[2].getElementType());
		assertEquals("k", children[2].getElementName());
		assertEquals("int[]", ((CodeElement) children[2]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[3].getElementType());
		assertEquals("l", children[3].getElementName());
		assertEquals("int[,]", ((CodeElement) children[3]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[4].getElementType());
		assertEquals("m", children[4].getElementName());
		assertEquals("int[][]", ((CodeElement) children[4]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[5].getElementType());
		assertEquals("n", children[5].getElementName());
		assertEquals("int[,]", ((CodeElement) children[5]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[6].getElementType());
		assertEquals("p", children[6].getElementName());
		assertEquals("int[][,,,]", ((CodeElement) children[6]).getTypeSignature());

		assertEquals(IDotNetElement.VARIABLE, children[6].getElementType());
		assertEquals("assignment", children[7].getElementName());
		assertEquals("int", ((CodeElement) children[7]).getTypeSignature());
	}

	public void testParseDocument41() {
		IDocument doc = setUpTestDocument("test41.cs");
		CSharpCodeParser cscp = new CSharpCodeParser(doc, project.getFile(
				"test41.cs").getFullPath().toOSString());
		ISourceUnit root = cscp.parseDocument();
		assertEquals(IDotNetElement.ROOT, root.getElementType());

		IDotNetElement[] children = root.getChildren();
		// one children, the class
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.CLASS, children[0]
				.getElementType());
		assertEquals("ParseGenericsVariables", children[0].getElementName());
		
		children = ((IParent) children[0]).getChildren();
		assertEquals(1, children.length);
		assertEquals(IDotNetElement.METHOD, children[0].getElementType());
		assertEquals("V", children[0].getElementName());
		children = ((IParent) children[0]).getChildren();
		assertEquals(16, children.length);
		
		CodeElement var = (CodeElement) children[0];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("o", var.getElementName());
		assertEquals("List<object>", var.getTypeSignature());
		
		var = (CodeElement) children[1];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("i", var.getElementName());
		assertEquals("List<object>", var.getTypeSignature());
		
		var = (CodeElement) children[2];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("genarray", var.getElementName());
		assertEquals("List<object>[]", var.getTypeSignature());
		
		var = (CodeElement) children[3];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("genarraySkip", var.getElementName());
		assertEquals("List<object>[]", var.getTypeSignature());
		
		var = (CodeElement) children[4];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("genarraySpace", var.getElementName());
		assertEquals("List<object>[]", var.getTypeSignature());
		
		var = (CodeElement) children[5];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("genarraySpaceSkip", var.getElementName());
		assertEquals("List<object>[]", var.getTypeSignature());
		
		var = (CodeElement) children[6];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("list", var.getElementName());
		assertEquals("object", var.getTypeSignature());
		
		var = (CodeElement) children[7];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("array", var.getElementName());
		assertEquals("object[]", var.getTypeSignature());
		
		var = (CodeElement) children[8];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("skip", var.getElementName());
		assertEquals("object[]", var.getTypeSignature());
		
		var = (CodeElement) children[9];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("whitespace", var.getElementName());
		assertEquals("object[]", var.getTypeSignature());
		
		var = (CodeElement) children[10];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("more", var.getElementName());
		assertEquals("object[]", var.getTypeSignature());
		
		var = (CodeElement) children[11];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("arrTwo", var.getElementName());
		assertEquals("object[][]", var.getTypeSignature());
		
		var = (CodeElement) children[12];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("genTwo", var.getElementName());
		assertEquals("List<object>[][]", var.getTypeSignature());
		
		var = (CodeElement) children[13];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("m", var.getElementName());
		assertEquals("Map<object, object>", var.getTypeSignature());
		
		var = (CodeElement) children[14];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("nospace", var.getElementName());
		assertEquals("Map<object, object>", var.getTypeSignature());
		
		var = (CodeElement) children[15];
		assertEquals(IDotNetElement.VARIABLE, var.getElementType());
		assertEquals("comma", var.getElementName());
		assertEquals("object[,]", var.getTypeSignature());
	}
}
