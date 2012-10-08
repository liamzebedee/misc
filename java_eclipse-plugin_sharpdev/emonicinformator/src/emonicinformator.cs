/* (c) Bernhard Brem, 
All rights reserved  */

namespace org.emonic.informator{
	using System;
	using System.Collections;
	using System.IO;
	using System.Text.RegularExpressions;
	using System.Reflection;
	
	///<summary> 
	///The EmonicInformator-Class
	///</summary>
	public class EmonicInformator{
		string dll;
		string cls;
		string[] args;
		private ArrayList AssemblyClasses;
		private ArrayList AssemblyInterfaces;
		private ArrayList AssemblyEnums;
		private ArrayList AssemblyStructures;
		
		
		public EmonicInformator(string[] args) {
			this.args = args;
			dll = ""; 
			cls = ""; 
		}
		
		
		///<summary>
		/// The real run method
		///</summary>
		public void run(){
			// Parse command arguments
			for (int i = 0; i < args.Length; i++){
				string cur = (string) args[i];
				if (cur == "-i" && i+1 < args.Length ){
					dll = args[i+1];
					System.Console.WriteLine("dll: " + dll);
				}
				if (cur == "-g") {
					// Inform about gac and exit
					getGAC();
					return;
				}
				if (cur == "-c" && i+1 < args.Length ){
					cls = args[i+1];
				}
				if (cur == "-h"){
					help();
					return;        
				}                  
			}
			if (dll != "" && cls == ""){
				System.Console.WriteLine("FILE: " + dll );
				informateAboutAssambly(dll);
				return;
			}
			if (dll != "" && cls != ""){
				System.Console.WriteLine("FILE: " + dll );
				System.Console.WriteLine("CLASS: " + cls );
				informateAboutClass(dll,cls);
				return;
			}
			// When we are here we got no sensefull parameters
			help();
		}
		
		private void getGAC(){
			Assembly coreAssembly;
			//System.Console.WriteLine("Get GAC");
			String whole = "";
			coreAssembly = Assembly.GetAssembly(whole.GetType());  
			whole = coreAssembly.CodeBase;
			whole = whole.Replace("file://",""       );
			// Under windows whole starts with file:///c:
			// Declare object variable of type Regex.
			Regex r; 
			// Create a Regex object and define its regular expression.
			r = new Regex("/(\\w:.*)$");
			Match m;
			CaptureCollection cc;
			GroupCollection gc;
			m = r.Match(whole); 
			gc = m.Groups;
			if (gc.Count > 1){
				//System.Console.WriteLine("Hit!");
				cc = gc[1].Captures;
				if (cc.Count > 0) whole=cc[0].Value;         
			}
			
			//System.Console.WriteLine("File: " + whole);
			FileInfo fi = new FileInfo(whole);
			//System.Console.WriteLine("Got " + whole);
			System.Console.WriteLine("LOCATION: " + fi.DirectoryName );
		}
		
		private void help(){
			System.Console.WriteLine("emonicinformator [-g] [-h] [-i dll] -c class] ");
			System.Console.WriteLine("-g: Get GAC-Location");
			System.Console.WriteLine("-i dll: Inform about given dll");
			System.Console.WriteLine("-i dll -c class|interface|enum: Inform about members in dll");
			System.Console.WriteLine("-h: show this");
		}
		
		
		
		
		private void informateAboutAssambly(string fileName){
			try{
				Assembly a = Assembly.LoadFrom(fileName);
				//ArrayList allClasses = getAssemblyClasses(a);
				FillAssemblyMembers(a);
				foreach  (object o in AssemblyClasses){
					String c = (string) o;
					
					// The classes System.String and System.Double are alo available under double and string
					if (c.Equals("System.String")) PrintIndividualAssemblyMember(a,"string","System.String","CLASS");//System.Console.WriteLine ("CLASS: string");
					else if (c.Equals("System.Array")) PrintIndividualAssemblyMember(a,"array","System.Array","CLASS");//System.Console.WriteLine ("CLASS: array");            
					else if (c.Equals("System.Object")) PrintIndividualAssemblyMember(a, "object", "System.Object", "CLASS");
					PrintIndividualAssemblyMember(a,c,c,"CLASS");
					
				}
				// The interfaces
				foreach  (object o in AssemblyInterfaces){
					String c = (string) o;
					PrintIndividualAssemblyMember(a,c,c,"INTERFACE");				
				}
				// The enums
				foreach  (object o in AssemblyEnums){
					String c = (string) o;
					PrintIndividualAssemblyMember(a,c,c,"ENUM");
				}

				foreach (object o in AssemblyStructures) {
					string s = (string) o;
					if (s.Equals("System.Boolean")) PrintIndividualAssemblyMember(a, "bool", "System.Boolean", "STRUCT");
					else if (s.Equals("System.Char")) PrintIndividualAssemblyMember(a, "char", "System.Char", "STRUCT");
					else if (s.Equals("System.SByte")) PrintIndividualAssemblyMember(a, "sbyte", "System.SByte", "STRUCT");
					else if (s.Equals("System.Byte")) PrintIndividualAssemblyMember(a, "byte", "System.Byte", "STRUCT");
					else if (s.Equals("System.Int16")) PrintIndividualAssemblyMember(a, "short", "System.Int16", "STRUCT");
					else if (s.Equals("System.UInt16")) PrintIndividualAssemblyMember(a, "ushort", "System.UInt16", "STRUCT");
					else if (s.Equals("System.Int32")) PrintIndividualAssemblyMember(a, "int", "System.Int32", "STRUCT");
					else if (s.Equals("System.UInt32")) PrintIndividualAssemblyMember(a, "int", "System.UInt32", "STRUCT");
					else if (s.Equals("System.Int64")) PrintIndividualAssemblyMember(a, "long", "System.Int64", "STRUCT");
					else if (s.Equals("System.UInt64")) PrintIndividualAssemblyMember(a, "ulong", "System.UInt64", "STRUCT");
					else if (s.Equals("System.Single")) PrintIndividualAssemblyMember(a, "floate", "System.Single", "STRUCT");
					else if (s.Equals("System.Double")) PrintIndividualAssemblyMember(a, "double", "System.Double", "STRUCT");
					PrintIndividualAssemblyMember(a, s, s, "STRUCT");
				}
			} catch( Exception e )  {
				System.Console.WriteLine( e.Message + " " + e.StackTrace);
			};
			
		}
		
		private void PrintIndividualAssemblyMember(Assembly a, string name, string fullname,string type ){
			// If we have a generic, transform to the generic
			// The name is expressed like this: name'Num_Of_paras
			if (name.LastIndexOf('`') >0 ){
				String whole = name;
				name = whole.Substring(0,name.LastIndexOf('`')) ;
				String end = whole.Substring(whole.LastIndexOf('`') );
				Regex r = new Regex("(\\d+)"); // Decimal value, get it!
				Match m; CaptureCollection cc; GroupCollection gc;
				m = r.Match(end); 
				gc = m.Groups;
				int nr = 0;
				if (gc.Count > 1){
					//System.Console.WriteLine("Hit!");
					cc = gc[1].Captures;
					if (cc.Count > 0) nr= Convert.ToInt32( cc[0].Value);         
				}
				// Add the brackets for the generic
				name+="<";
				for (int i = 1; i < nr; i++) name+=",";
				name+=">";
			} 
			//System.Console.WriteLine ("CLASS: " + c);     
			Type cl = a.GetType(fullname);
			string res = "KIND:" +" " + type+" NAME: "+ name + " ACCESS: ";
			if (cl != null){
				
				if (cl.IsNotPublic){
					res += "PRIVATE";
				} else if (cl.IsPublic) {
					res += "PUBLIC";
				} else {
					res +="PROTECTED";
				}
				res += " INTERFACE: ";
				Type[] myObjectArray= cl.GetInterfaces();
				if (myObjectArray.Length > 0){
					
					for (int index = 0; index < myObjectArray.Length; index++)
					{    
						res += myObjectArray[index];
						if (index  < myObjectArray.Length-1 )res +=  ",";
					}
				}
				res += " BASETYPE: ";
				if (cl.BaseType != null){
					res += cl.BaseType;
				}
				
				System.Console.WriteLine(res);
				
			}
			
			
			
		}
		
		private void FillAssemblyMembers(Assembly asa){
			AssemblyClasses = new ArrayList();
			AssemblyEnums = new ArrayList();
			AssemblyInterfaces = new ArrayList();
			AssemblyStructures = new ArrayList();
			Type[] mytypes = asa.GetTypes();
			foreach( Type t in mytypes)
			{
				string nam = t.FullName;
				// Find a single match in the string.
				Regex r = new Regex("PrivateImplementationDetails");
				Match m = r.Match(nam); 
				if (!m.Success){
					if ( t.IsClass ){ 
						AssemblyClasses.Add(t.FullName);
					} else if ( t.IsEnum ){
						AssemblyEnums.Add( t.FullName );
					} else if ( t.IsInterface ){
						AssemblyInterfaces.Add( t.FullName);
					} else {
						AssemblyStructures.Add(t.FullName);
					}
				}
			}
			
		}
		
		public static void Main(string[] args){
			EmonicInformator ei = new EmonicInformator(args); 
			ei.run();      
		}
		
		private void informateAboutClass(String fileName, String cls ){
			//System.Console.WriteLine("Hallo!");
			try{
				//Exception: In the case of string and  double are org. functions are with great letters at the begin
				String orgcls = cls;
				if (cls.Equals("string")) cls = "System.String";
				else if (cls.Equals("double")) cls = "System.Double";
				else if (cls.Equals("array")) cls = "System.Array";
				else if (cls.Equals("bool")) cls = "System.Boolean";
				else if (cls.Equals("void")) cls = "System.Void";
				else if (cls.Equals("char")) cls = "System.Char";
				else if (cls.Equals("sbyte")) cls = "System.SByte";
				else if (cls.Equals("byte")) cls = "System.Byte";
				else if (cls.Equals("short")) cls = "System.Int16";
				else if (cls.Equals("ushort")) cls = "System.UInt16";
				else if (cls.Equals("int")) cls = "System.Int32";
				else if (cls.Equals("int")) cls = "System.UInt32";
				else if (cls.Equals("long")) cls = "System.Int64";
				else if (cls.Equals("ulong")) cls = "System.UInt64";
				else if (cls.Equals("floate")) cls = "System.Single";
				else if (cls.Equals("double")) cls = "System.Double";
				else if (cls.Equals("object")) cls = "System.Object";	
				
				Assembly a = Assembly.LoadFrom(fileName);
				// For .net 2.0: Transform generics to the kind they are represented by the assemblies
				// First, get the numbers of elements: "." in the <> +1
				// Do we have such a construct?
				if (cls.LastIndexOf('<') >0 && cls.LastIndexOf('>') > cls.LastIndexOf('<')){
					Console.WriteLine("Entering generics" +  cls.LastIndexOf('<') + " " + cls.LastIndexOf('>') );
					
					string sc = cls.Substring(cls.LastIndexOf('<'),cls.LastIndexOf('>')-cls.LastIndexOf('<')+1) ;
					Char[] scChar = sc.ToCharArray();
					Console.WriteLine("generics 1 " + sc);
					int scCount = 0;
					for (int i = 0; i < scChar.Length; i++){
						if ( scChar[i] == ',') scCount++;
					}
					scCount++;
					cls =  cls.Substring(0,cls.LastIndexOf('<')) +  "`" + scCount;
					Console.WriteLine(cls);
				}
				Type cl = a.GetType(cls);
				
				AnalyzeFields(cl, orgcls ); 
				AnalyzeConstructors(cl, orgcls );
				AnalyzeProperties(cl, orgcls );
				AnalyzeMethods(cl, orgcls ); 
			}
			catch (Exception e){
				System.Console.WriteLine(e.ToString());
			}
		}
		
		private string NormalizeType(String type){
			type = type.Trim();
			// System.Console.WriteLine("Searching " + type);
			if (type.Equals("System.Object"))  return "object";
			if (type.Equals("System.String"))  return "string";
			if (type.Equals("System.Char"))  return "char";
			if (type.Equals("System.Double")) return "double";
			if (type.Equals("System.Array")) return "array";
			if (type.Equals("System.Boolean")) return "bool";
			if (type.Equals("System.Void")) return "void";
			if (type.Equals("System.Int32")) return "int";
			if (type.Equals("System.Int16")) return "short";
			if (type.Equals("System.Int64")) return "long";
			if (type.Equals("System.UInt32")) return "uint";
			if (type.Equals("System.UInt16")) return "ushort";
			if (type.Equals("System.UInt64")) return "ulong";
			return type;
		}
		
		private void AnalyzeFields(Type cls,String clsname){
			
			FieldInfo[] fields = cls.GetFields(); 
			for (int i = 0; i< fields.Length; i++){
				System.Reflection.FieldInfo fi = fields[i];
				String res="CLASS:" +  clsname + " FIELD:"+ fi.Name;
				res += " TYPE:" + NormalizeType(fi.FieldType.ToString());
				res+=" ACCESS:";
				if ( fi.IsPrivate ){
					res += "PRIVATE";
				} else if ( fi.IsPublic ){
					res += "PUBLIC";
				}  else {
					res += "PROTECTED";
				} 
				if  ( fi.IsFamily){
					res += "INTERNAL";
				}
				if (fi.IsStatic) res += " STATIC";
				Console.WriteLine(res);
			}
		}
		
		private void AnalyzeConstructors(Type cls,String clsname){
			// Get the constructors
			ConstructorInfo[] cons = cls.GetConstructors();
			for (int i = 0; i< cons.Length; i++){
				String shortname=clsname;
				if ( clsname.IndexOf('.') != -1 ){
					string[] ss= clsname.Split('.');
					shortname=ss[ss.Length-1 ];
				}
				String res =  "CLASS:" +  clsname + " CONSTRUCTOR:"+ shortname  ;//cons[i].Name ; 
				ConstructorInfo constinst = cons[i];
				ParameterInfo[] pars = constinst.GetParameters();
				String ps = " SIGNATURE:("; 
				for (int j = 0; j < pars.Length; j++) 
				{
					ParameterInfo p = pars[j];
					ps += NormalizeType(p.ParameterType.ToString());
					ps += ' ';
					ps += p.Name;
					if (j < pars.Length -1) ps += ","; 
				}
				ps += ") ";
				res += ps;
				res += " ACCESS:";
				if (constinst.IsPrivate){
					res += "PRIVATE";
				} else if ( constinst.IsPublic ){
					res += "PUBLIC";
				}  else {
					res += "PROTECTED";
				} 
				if  ( constinst.IsFamily){
					res += "INTERNAL";
				}
				Console.WriteLine(res);
			}
			
		}
		
		private void AnalyzeProperties(Type cls,String clsname){
			// Get the properties
			PropertyInfo[] paras = cls.GetProperties(BindingFlags.NonPublic|BindingFlags.Public|BindingFlags.Instance|BindingFlags.Static) ;
			for (int i = 0; i< paras.Length; i++){
				
				String res =  "CLASS:" +  clsname + " PROPERTY:"+ paras[i].Name  + " TYPE:" + NormalizeType(paras[i].PropertyType.ToString()); 
				System.Reflection.MethodInfo mi = paras[i].GetGetMethod() ;
				if (mi == null) mi = paras[i].GetSetMethod() ;
				if (mi != null) {
					res += " ACCESS:";
					if ( mi.IsPrivate ){
						res += "PRIVATE";
					} else if (  mi.IsPublic ){
						res += "PUBLIC";
					}  else {
						res += "PROTECTED";
					} 
					if ( mi.IsFamilyAndAssembly ){
						res += "INTERNAL";
					}
					Console.WriteLine(res);
				}
				
			}
		}
		private void AnalyzeMethods(Type cls,String clsname){
			// Get the methods
			MethodInfo[] methods = cls.GetMethods(BindingFlags.NonPublic|BindingFlags.Public|BindingFlags.Instance|BindingFlags.Static);
			for (int i = 0; i< methods.Length; i++) {
				System.Reflection.MethodInfo mi = methods[i];
				string name = mi.Name;
				// skip methods that represents properties
				if (mi.IsSpecialName && (name.StartsWith("get_") || name.StartsWith("set_")))
				{
					continue;
				}
				String res =  "CLASS:" +  clsname + " METHOD:" + name;
				ParameterInfo[] pars = mi.GetParameters();
				String ps = " SIGNATURE:("; 
				for (int j = 0; j < pars.Length; j++) 
				{
					ParameterInfo p=  pars[j];
					ps += NormalizeType(p.ParameterType.ToString());
					ps += ' ';
					ps += p.Name;
					if (j < pars.Length -1) ps += ", "; 
				}
				ps += ") ";
				res += ps;
				res += " TYPE:" + NormalizeType(methods[i].ReturnType.ToString());
				res += " ACCESS:";
				if (mi.IsPublic) {
					res += "PUBLIC";
				} else if  (methods[i].IsPrivate){
					res += "PRIVATE";
				} else {
					res += "PROTECTED";
				}
				if ( mi.IsFamilyAndAssembly) res += "INTERNAL";
				if (mi.IsStatic) res += " STATIC";
				
				Console.WriteLine(res);
			}
		}
	}
} 
