package lessur.util.shader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Managed implementation for a shader framework in LWJGL OpenGL
 * Work based on SlickShader by Chronocide (Jeremy Klix)
 * @author liamzebedee
 */
/*
 * Licensed under GPLv3 to Liam Edwards-Playne (liamzebedee)
 * 
 * JShader
 * Copyright (C) 2012 Liam Edwards-Playne

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Shader {
	private static final int NOT_LOADED = -1;
	private static final String ERR_LOCATION =
			"Warning: variable %s could not be found. " +
					"Ensure the name is spelled correctly\n";
	private ShaderManager shaderManager;
	/**
	 * ID/index of the <tt>Shader</tt>.  A Shader may have programID of 
	 * -1 only before construction is completed, or
	 * after the <tt>Shader</tt> is deleted
	 */
	private int programID = NOT_LOADED;
	/**
	 * A list of variables used by the shader, scanned from the source
	 */
	private ArrayList<String> shaderVariables = new ArrayList<String>();

	private Shader(ShaderManager manager, String vertexShader, String fragmentShader) throws Exception{
		try{
			this.shaderManager = manager;
			programID = shaderManager.createProgram();
			// Vertex Shader
			int vertexShaderID = shaderManager.getVertexShaderID(vertexShader);
			shaderManager.createProgramShaderDependancy(programID, vertexShaderID);
			scanSource(vertexShader);
			// Fragment Shader
			int fragmentShaderID = shaderManager.getFragmentShaderID(fragmentShader);
			shaderManager.createProgramShaderDependancy(programID, fragmentShaderID);
			scanSource(fragmentShader);
			// Attach and Link
			shaderManager.attachAndLinkShader(programID, vertexShaderID);
			shaderManager.attachAndLinkShader(programID, fragmentShaderID);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Factory method to create a new Shader by loading them from files
	 * @param vertexFile Vertex shader file
	 * @param fragmentFile Fragment shader file
	 * @return The newly created Shader
	 * @throws Exception
	 */
	public static Shader loadShader(File vertexFile, File fragmentFile) throws Exception {
		String v = vertexFile.getAbsolutePath();
		String f = fragmentFile.getAbsolutePath();
		return new Shader(ShaderManager.getInstance(), v, f);
	}

	/**
	 * Factory method to create a new Shader by loading the shader data from String objects
	 * @param vertexShaderData Fragment Shader data as a String
	 * @param fragmentShaderData Vertex Shader data as a String
	 * @return The newly created Shader
	 * @throws Exception
	 */
	public static Shader loadShader(String vertexShaderData, String fragmentShaderData) throws Exception {
		File f1 = null;
		File f2 = null;
		try {
			f1 = File.createTempFile("vertexShader", ".vrt");
			FileWriter fstream1 = new FileWriter(f1.getAbsolutePath());
			BufferedWriter out1 = new BufferedWriter(fstream1);
			out1.write(vertexShaderData);
			out1.flush();

			f2 = File.createTempFile("fragmentShader", ".frg");
			FileWriter fstream2 = new FileWriter(f1.getAbsolutePath());
			BufferedWriter out2 = new BufferedWriter(fstream2);
			out2.write(vertexShaderData);
			out2.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Shader(ShaderManager.getInstance(), f1.getAbsolutePath(), f2.getAbsolutePath());
	}

	/**
	 * Starts the use of the shader
	 */
	public void startUse(){
		if(programID == NOT_LOADED){
			throw new IllegalStateException("Cannot start shader; this" +
					" Shader has been deleted");
		}
		endUse(); //Not sure why this is necessary but it is.
		shaderManager.startUse(this.programID);
	}

	/**
	 * Force's the fixed shader therefore ending the use of the current shader
	 */
	public void endUse(){
		shaderManager.endUse();
	}

	/**
	 * Deletes this shader and unloads all free resources
	 */
	public void deleteShader(){
		shaderManager.removeProgram(programID);
		programID = NOT_LOADED;
	}

	/**
	 * Returns true if this <tt>Shader</tt> has been deleted.</br>
	 * @return true if this <tt>Shader</tt> has been deleted.</br>
	 */
	public boolean isDeleted(){
		if(programID == NOT_LOADED){
			return true;
		}
		return false;
	}

	//UNIFORM SETTERS  

	public void setUniformInt(String name, int value){
		setUniformIntArray(name, new int[]{value});
	}

	public void setUniformIntArray(String name, int[] values){
		if(!shaderVariables.contains(name)) {
			printError(name);
		}
		IntBuffer fb = BufferUtils.createIntBuffer(values.length);
		fb.put(values);
		fb.flip();
		switch(values.length){
		case 1: shaderManager.setUniform1(programID, name, values[0]); break;
		case 2: shaderManager.setUniform2i(programID, name, fb); break;
		case 3: shaderManager.setUniform3i(programID, name, fb); break;
		case 4: shaderManager.setUniform4i(programID, name, fb); break;
		}
	}

	public void setUniformFloat(String name, float value){
		setUniformFloatArray(name, new float[]{value});
	}
	
	public void setUniformFloatArray(String name, float[] values){
		if(!shaderVariables.contains(name)) {
			printError(name);
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(values.length);
		fb.put(values);
		fb.flip();
		switch(values.length){
		case 1: shaderManager.setUniform1(programID, name, values[0]); break;
		case 2: shaderManager.setUniform2(programID, name, fb); break;
		case 3: shaderManager.setUniform3(programID, name, fb); break;
		case 4: shaderManager.setUniform4(programID, name, fb); break;
		}
	}
	
	public void setUniformMatrix(String name, boolean transpose, float[][] matrix){
		if(!shaderVariables.contains(name)) {
			printError(name);
		}
		shaderManager.setUniformFloatArray(this.programID, name, transpose, matrix);
	}
	
	private void printError(String varName){
		System.err.printf(ERR_LOCATION, varName);
	}

	private void scrapeVariables(String varLine){
		ShaderVariable.Qualifier qualifier = null;
		ShaderVariable.VariableType type = null;
		String name = "";
		int vecSize = 1; // if a vector the
		int size = 1; //If array size of array

		String str;
		Scanner scanner = new Scanner(varLine);
		scanner.useDelimiter("[\\s,]++");

		//Determine qualifier
		qualifier = ShaderVariable.Qualifier.fromString(scanner.next());

		//Determine type
		str = scanner.next();
		if(str.equals("float")){
			type = ShaderVariable.VariableType.FLOAT;
		}else if(str.matches("[u]?int|sampler[123]D")){
			type = ShaderVariable.VariableType.INTEGER;
		}else if(str.equals("bool")){
			type = ShaderVariable.VariableType.BOOLEAN;
		}else if(str.matches("[bdiu]?vec[234]")){
			char c = str.charAt(0);
			switch(c){
			case 'b':
				type = ShaderVariable.VariableType.BOOLEAN; break;
			case 'd':
				type = ShaderVariable.VariableType.DOUBLE; break;
			case 'i':
			case 'u':
				type = ShaderVariable.VariableType.INTEGER; break;
			case 'v':
				type = ShaderVariable.VariableType.FLOAT; break;
			}

			str = str.substring(str.length()-1);
			vecSize = Integer.parseInt(str);
		}


		//Determine variable names
		while(scanner.hasNext("[\\w_]+[\\w\\d_]*(\\[\\d+\\])?")){
			name = scanner.next();
			if(name.contains("]")){
				String sub = name.substring(name.indexOf('[')+1, name.length()-1);
				size = Integer.parseInt(sub);
				name = name.substring(0, name.indexOf('[')).trim();
			}
			shaderVariables.add(name);
		}
	}

	private void scanSource(String filename) throws FileNotFoundException{
		InputStream fileInputStream;
		String cpRef = filename.replace('\\', '/');
		fileInputStream = ShaderManager.class.getClassLoader().getResourceAsStream(cpRef);
		if (fileInputStream == null) {
			File file = new File(cpRef);
			fileInputStream = new FileInputStream(file);
		}
		Scanner scanner = new Scanner(fileInputStream);
		scanner.useDelimiter(";|\\{|\\}");
		while(scanner.hasNext()){
			while(scanner.hasNext("\\s*?(uniform|attribute|varying).*")){
				scrapeVariables(scanner.next().trim());
			}
			scanner.next();
		}
		printVariables();
	}
	
	public void printVariables(){
		System.out.println("Variables for Shader: ");
		for(String varName : shaderVariables){
			System.out.print(varName+", ");
		}
		System.out.println("\n");
	}
}