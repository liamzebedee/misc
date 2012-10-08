package lessur.util.shader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * A managed shader implementation in LWJGL
 * 
 * Supports 2 Shader Implementations
 * - OpenGL 2
 * - ARB Extensions
 * 
 * @author liamzebedee
 * Work based on SlickShader by Chronocide (Jeremy Klix) and
 * on a lighting example by Ciano, which implemented Shaders
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
public class ShaderManager {
	private static ShaderManager shaderManager;
	public static Boolean hasShaders = null;
	private boolean has_opengl2, has_arb;
	private Map<String, Integer> shaderMap = new HashMap<String, Integer>();
	private Map<Integer, Set<Integer>> shaderToPrograms = new HashMap<Integer, Set<Integer>>(); //for every shader lists all the programs that use it
	private Map<Integer, Set<Integer>> programToShaders = new HashMap<Integer, Set<Integer>>(); //for every program lists the shaders it uses
	protected static final int loggingBRIEF = 128;
	protected static final int loggingMODERATE = 512;
	protected static final int loggingVERBOSE = 1024; 
	private static int logging = loggingMODERATE;

	/**
	 * Gets the shader manager singleton
	 * @return The shader manager
	 */
	public static ShaderManager getInstance() {
		if(shaderManager==null) shaderManager = new ShaderManager();
		return shaderManager;
	}

	/**
	 * Checks if the platform supports the 2 offered shader implementations, through OpenGL2 or ARB extensions
	 */
	public static boolean does_support_shaders() {
		if (hasShaders == null) {
			try {
				getInstance();
				hasShaders = true;
			} catch(Exception e) {
				hasShaders = false;
			}
		}
		return hasShaders;
	}

	private ShaderManager() {
		hasShaders = null;
		findShaderType();
	}

	/**
	 * Get's the OpenGL version
	 * @return A list with the GL version numbers. I.e. [2, 1, 0]
	 */
	public int getGLVersion() {
		return Integer.parseInt(Character.toString(GL11.glGetString(GL11.GL_VERSION).charAt(0)));
	}

	/**
	 * Finds the shader implementations supported, OpenGL2 and ARB Extensions
	 * @return True if any implementations are supported, false if not
	 */
	public boolean findShaderType() {
		if (getGLVersion() >= 2) {
			System.out.println("We have shader support through opengl 2.0");
			has_opengl2 = true;
			has_arb = true;
			return true;
		}
		else if(ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)>0) {
			System.out.println("We have shader support through ARB extensions");
			has_opengl2 = false;
			has_arb = true;
			return true;
		}
		else {
			has_arb = false;
			has_opengl2 = false;
			return false;
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform1(int program, String name, float value) {
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform1f(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform1fARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform1i(int program, String name, int value) {
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform1i(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform1iARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform2(int program, String name, FloatBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform2(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform2ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform2i(int program, String name, IntBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform2(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform2ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform3(int program, String name, FloatBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform3(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform3ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform3i(int program, String name, IntBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform3(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform3ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform4(int program, String name, FloatBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform4(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform4ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniform4i(int program, String name, IntBuffer value){
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform4(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform4ARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniformFloat(int program, String name, float value) {
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			GL20.glUniform1f(loc, value);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			ARBShaderObjects.glUniform1fARB(loc, value);
		}
	}

	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniformFloatArray(int program, String name, float values[]) {
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			if(values.length==1) GL20.glUniform1f(loc, values[0]);
			if(values.length == 2) GL20.glUniform2f(loc, values[0], values[1]);
			else if(values.length == 3) GL20.glUniform3f(loc, values[0], values[1], values[2]);
			else if(values.length == 4) GL20.glUniform4f(loc, values[0], values[1], values[2], values[3]);
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			if(values.length==1) ARBShaderObjects.glUniform1fARB(loc, values[0]);
			if(values.length == 2) ARBShaderObjects.glUniform2fARB(loc, values[0], values[1]);
			else if(values.length == 3) ARBShaderObjects.glUniform3fARB(loc, values[0], values[1], values[2]);
			else if(values.length == 4) ARBShaderObjects.glUniform4fARB(loc, values[0], values[1], values[2], values[3]);
		}
	}
	
	/**
	 * 
	 * @param program ID/index of the program
	 * @param name Name of the variable
	 * @param value Value to set the variable
	 */
	public void setUniformFloatArray(int program, String name, boolean transpose, float[][] matrix) {
		FloatBuffer matBuffer = matrixPrepare(matrix);
		if(has_opengl2) {
			int loc = GL20.glGetUniformLocation(program, name);
			checkVariableLocation(loc, name);
			switch(matrix.length){
			case 2:
				GL20.glUniformMatrix2(loc, transpose, matBuffer);
				break;
			case 3: 
				GL20.glUniformMatrix3(loc, transpose, matBuffer);
				break;
			case 4: 
				GL20.glUniformMatrix4(loc, transpose, matBuffer);
				break;
			}
		}
		else if(has_arb) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
			checkVariableLocation(loc, name);
			switch(matrix.length){
			case 2:
				ARBShaderObjects.glUniformMatrix2ARB(loc, transpose, matBuffer);
				break;
			case 3: 
				ARBShaderObjects.glUniformMatrix3ARB(loc, transpose, matBuffer);
				break;
			case 4: 
				ARBShaderObjects.glUniformMatrix4ARB(loc, transpose, matBuffer);
				break;
			}
		}
	}
	
	private FloatBuffer matrixPrepare(float[][] matrix){
		// Check argument validity
		if(matrix==null){
			throw new IllegalArgumentException("The matrix may not be null");
		}
		int row = matrix.length;
		if(row<2){
			throw new IllegalArgumentException("The matrix must have at least 2 rows.");
		}
		int col = matrix[0].length;
		if(col!=row){
			throw new IllegalArgumentException("The matrix must have an equal number of rows and columns.");
		}
		float[] unrolled = new float[row*col];

		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				unrolled[i*col+j] = matrix[i][j];
			}
		}
		return FloatBuffer.wrap(unrolled);
	}

	/**
	 * Get's the location of a uniform variable
	 * @param program The ID/index of the program
	 * @param name The name of the variable
	 * @return The location of the variable, or -1 if it isn't found
	 */
	public int getUniformVariableLocation(int program, String name){
		CharSequence param = new StringBuffer(name);
		if(has_opengl2){
			return GL20.glGetUniformLocation(program, param);
		}
		else if(has_arb){
			return ARBShaderObjects.glGetUniformLocationARB(program, param);
		}
		return -1;
	}

	private void checkVariableLocation(int loc, String name){
		if(loc != -1) return;
		System.err.println("Location for variable " + name +
				" could not be found.\nGLSL may remove " +
				"any variable that does not contribute " +
				"to an output. Check and ensure " +
				"that " + name + " is an active variable.\n");
	}

	/**
	 * Creates a program, which can be used to attach shaders to
	 * @return An int representing the program index/id
	 */
	public int createProgram(){
		int program = -1;
		if(has_opengl2) {
			program = GL20.glCreateProgram();
		}
		else if(has_arb) {
			program = ARBShaderObjects.glCreateProgramObjectARB();
		}
		return program;
	}

	/**
	 * Attach's the shader to the program, and links the program
	 * @throws Exception 
	 */
	public void attachAndLinkShader(int program, int shaderID) throws Exception{
		if(has_opengl2){
			GL20.glAttachShader(program, shaderID);
			GL20.glLinkProgram(program);
		}
		else if(has_arb){
			ARBShaderObjects.glAttachObjectARB(program, shaderID);
			ARBShaderObjects.glLinkProgramARB(program);
		}
		if(!linkedSuccessfully(program)){
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("Linking Error\n");
			errorMessage.append(getProgramInfoLog(program));
			errorMessage.append("\n\n");
			throw new Exception(errorMessage.toString());
		}
	}

	/**
	 * Starts use of the program
	 * @param program The index/id of the program
	 */
	public void startUse(int program) {
		if(has_opengl2) {
			GL20.glUseProgram(program);
		}
		else if(has_arb){
			ARBShaderObjects.glUseProgramObjectARB(program);
		}
		else System.out.println("No shader support, can't enable");
	}

	/**
	 * Reverts to the fixed program
	 */
	public void endUse(){
		if(has_opengl2) {
			GL20.glUseProgram(0);
		}
		else if(has_arb){
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
	}

	/**
	 * Fetches a shader id for a given fragment shader, generating
	 * a new id if necessary
	 * @param fragmentShaderFilePath The path to the fragment shader
	 * @returns shaderID for a given fragment shader
	 */
	public int getFragmentShaderID(String fragmentShaderFilePath) throws Exception{
		Integer id = shaderMap.get(fragmentShaderFilePath);
		if(id == null){
			if(has_opengl2){
				id = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
				GL20.glShaderSource(id, getProgramCode(fragmentShaderFilePath));
				GL20.glCompileShader(id);
				shaderMap.put(fragmentShaderFilePath, id);
			}
			else if(has_arb){
				id = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
				ARBShaderObjects.glShaderSourceARB(id, getProgramCode(fragmentShaderFilePath));
				ARBShaderObjects.glCompileShaderARB(id);
				shaderMap.put(fragmentShaderFilePath, id);
			}
			StringBuilder errorMessage = new StringBuilder();
			//Check for errors with shader
			if(!compiledSuccessfully(id)){
				errorMessage.append("Fragment Shader ");
				errorMessage.append(fragmentShaderFilePath);
				errorMessage.append(" failed to compile.\n");
				errorMessage.append(getShaderInfoLog(id));
				errorMessage.append("\n\n");
				errorMessage.insert(0, "Could not compile shader.\n");
				throw new Exception(errorMessage.toString());
			}
		}
		return id;
	}

	/**
	 * Fetches a shader id for a given vertex shader, generating
	 * a new id if necessary.
	 * @param fragmentFileName The path to the vertex shader
	 * @returns shaderID The shader ID of the [new] fragment shader
	 */
	public int getVertexShaderID(String vertexShaderFilePath) throws Exception {
		Integer id = shaderMap.get(vertexShaderFilePath);
		if(id==null){
			if(has_opengl2){
				id = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
				GL20.glShaderSource(id, getProgramCode(vertexShaderFilePath));
				GL20.glCompileShader(id);
				shaderMap.put(vertexShaderFilePath, id);
			}
			else if(has_arb){
				id = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
				ARBShaderObjects.glShaderSourceARB(id, getProgramCode(vertexShaderFilePath));
				ARBShaderObjects.glCompileShaderARB(id);
				shaderMap.put(vertexShaderFilePath, id);
			}
			StringBuilder errorMessage = new StringBuilder();
			//Check for errors with shader
			if(!compiledSuccessfully(id)){
				errorMessage.append("Vertex Shader ");
				errorMessage.append(vertexShaderFilePath);
				errorMessage.append(" failed to compile.\n");
				errorMessage.append(getShaderInfoLog(id));
				errorMessage.append("\n\n");
				errorMessage.insert(0, "Could not compile shader.\n");
				throw new Exception(errorMessage.toString());
			}
		}
		return id;
	}

	/**
	 * Returns true if the shader compiled successfully
	 * @param shaderID
	 * @return true if the shader compiled successfully
	 */
	private boolean compiledSuccessfully(int shaderID){
		if(has_opengl2){
			return GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;
		}
		else if(has_arb){
			return ARBShaderObjects.glGetObjectParameterfARB(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;
		}
		return false;
	}

	/**
	 * Returns true if the shader program linked successfully.</br>
	 * @return true if the shader program linked successfully.</br>
	 */
	private boolean linkedSuccessfully(int programID){
		if(has_opengl2){
			int test = GL20.glGetShader(programID, GL20.GL_LINK_STATUS);
			return true;
		}
		else if(has_arb){
			float test = ARBShaderObjects.glGetObjectParameterfARB(programID, GL20.GL_LINK_STATUS);
			return true;
		}
		return false;
	}

	/**
	 * Gets the shader info log
	 * @param shaderID
	 * @return The shader info log or an empty string if it cannot be found
	 */
	private String getShaderInfoLog(int shaderID){
		if(has_opengl2){
			return GL20.glGetShaderInfoLog(shaderID, logging).trim();
		}
		else if(has_arb){
			return ARBShaderObjects.glGetInfoLogARB(shaderID, logging).trim(); // TODO
		}
		return "";
	}

	/**
	 * Gets the program info log
	 * @param shaderID
	 * @return The program info log or an empty string if it cannot be found
	 */
	private String getProgramInfoLog(int programID) {
		if(has_opengl2){
			return GL20.glGetProgramInfoLog(programID, logging).trim();
		}
		else if(has_arb){
			return ARBShaderObjects.glGetInfoLogARB(programID, logging).trim(); // TODO
		}
		return "";
	}

	/**
	 * Link a shader that the shader program depends on to operate.</br>
	 * @param programID The id/index of the program
	 * @param shaderID The id/index of the shader
	 */
	public void createProgramShaderDependancy(int programID, int shaderID) {
		if(!shaderMap.containsValue(shaderID)){
			throw new IllegalArgumentException("Cannot link a shader " +
					"id that does not exist.");
		}

		//Add Shader to list of shaders used by program
		Set<Integer> shaders = programToShaders.get(programID);
		if(shaders==null){
			shaders = new HashSet<Integer>();
			programToShaders.put(programID, shaders);
		}
		shaders.add(shaderID);

		//Add program to list of programs used by Shader
		Set<Integer> programs = shaderToPrograms.get(shaderID);
		if(programs==null){
			programs = new HashSet<Integer>();
			shaderToPrograms.put(shaderID, programs);
		}
		programs.add(programID);
	}

	/**
	 * Removes a program and all shaders associated with it
	 * @param The id/index of the program
	 */
	public void removeProgram(int programID) {
		Set<Integer> shaders = programToShaders.get(programID);
		if(shaders==null){
			throw new IllegalArgumentException("The programID " +
					programID +
					"does not exist");
		}
		//detach Shaders from program
		for(int id : shaders){
			if(has_opengl2){
				GL20.glDetachShader(programID, id); 
			}
			else if(has_arb){
				ARBShaderObjects.glDetachObjectARB(programID, id);
			}
		}
		//Delete unused shaders
		for(int id : shaders){
			Set<Integer> progs = shaderToPrograms.get(id);
			progs.remove(programID);
			if(progs.isEmpty()){
				if(has_opengl2){
					GL20.glDeleteShader(id);
				}
				shaderToPrograms.remove(id);
			}
		}
		//Delete Program
		if(has_opengl2){
			GL20.glDeleteProgram(programID);
		}
		else if(has_arb){
			ARBShaderObjects.glDeleteObjectARB(programID);
		}
		programToShaders.remove(programID);
	}

	/**
	 * Gets the program code from the file <tt>filename</tt> and puts
	 * it into a <tt>ByteBuffer</tt>.</br>
	 * @param filename the full name of the file.
	 * @return a ByteBuffer containing the program code.
	 * @throws Exception 
	 */
	private ByteBuffer getProgramCode(String filename) throws Exception {
		InputStream fileInputStream = null;
		byte[] shaderCode = null;

		String cpRef = filename.replace('\\', '/');
		fileInputStream = ShaderManager.class.getClassLoader().getResourceAsStream(cpRef);
		if (fileInputStream == null) {
			File file = new File(cpRef);
			fileInputStream = new FileInputStream(file);
		}
		DataInputStream dataStream = new DataInputStream(fileInputStream);
		try {
			dataStream.readFully(shaderCode = new byte[fileInputStream.available()]);
			fileInputStream.close();
			dataStream.close();
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}

		ByteBuffer shaderPro = BufferUtils.createByteBuffer(shaderCode.length);
		shaderPro.put(shaderCode);
		shaderPro.flip();
		return shaderPro;
	}


	/**
	 * Sets the number of characters to be returned when printing
	 * errors.</br>  Suggested values are the constants
	 * <tt>loggingBRIEF</tt>, <tt>loggingMODERATE</tt>, and <tt>loggingVERBOSE</tt>.</br>
	 * @param detailLevel number of characters to display for error
	 *                    messages.
	 */
	public static void setLoggingDetail(int detailLevel){
		logging = detailLevel;
	}
}
