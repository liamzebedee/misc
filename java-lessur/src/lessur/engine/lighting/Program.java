package lessur.engine.lighting;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.util.Log;

/**
 * ShaderManager singleton class
 * Based on work by Ciano [http://slick.javaunlimited.net/viewtopic.php?f=3&t=3937]
 * 
 * @author liamzebedee
 */
public abstract class Program {
	/** Program ID */
	public int programID;
	/** Types of shaders */
	public enum ShaderType { FRAGMENT, VERTEX, GEOMETRY }
	/** A mapping between all variables and their id's. More efficient than asking GL each time */
	public HashMap<String, Integer> variables = new HashMap<String, Integer>();

	/**
	 * Enables this program
	 * 
	 * @author liamzebedee
	 */
	public abstract void enable();

	/**
	 * Disables this program
	 * 
	 * @author liamzebedee
	 */
	public abstract void disable();

	public abstract void setUniform1f(String name, float value);
	public abstract void setUniformf(String name, float values[]);
	public abstract void setUniform1i(String name, int value);

	/**
	 * Attach a non-existing shader
	 * 
	 * @author liamzebedee
	 * @param shaderType The type of the shader
	 * @param source The source of the shader code
	 * @return The shader's ID
	 */
	public abstract int attachShader(ShaderType shaderType, String source);

	/**
	 * Attaches an existing shader
	 * 
	 * @author liamzebedee
	 * @param shaderID The ID of the shader to attach
	 * @return The ID of the shader
	 */
	public abstract int attachShader(int shaderID);

	/**
	 * Detaches a shader from this program
	 * 
	 * @author liamzebedee
	 * @param shaderID The ID of the shader to detach
	 * @return
	 */
	public abstract int detachShader(int shaderID);

	/**
	 * Links the program etc.
	 * 
	 * @author liamzebedee
	 */
	public abstract void finaliseProgram();

	/**
	 * Creates a new program
	 * 
	 * @author liamzebedee
	 * @param vertexShaderSource The vertex shader source, as a String
	 * @param fragmentShaderSource The fragment shader source, as a String
	 * @return The new Shader object
	 */
	public static Program create() {
		if (GLContext.getCapabilities().OpenGL20) {
			OpenGL2Program shader = new OpenGL2Program();
			return shader;
		}
		else if (GLContext.getCapabilities().GL_ARB_shader_objects) {
			ARBProgram shader = new ARBProgram();
			return shader;
		}
		else {
			Log.info("Can't create program. No shader support!");
			return null;
		}
	}

}

class OpenGL2Program extends Program {
	public OpenGL2Program(){
		this.programID = GL20.glCreateProgram();
	}

	@Override
	public void enable() { GL20.glUseProgram(programID); }

	@Override
	public void disable() { GL20.glUseProgram(0); }

	@Override
	public void setUniform1f(String name, float value) {
		GL20.glUniform1f(variables.get(name), value);
	}

	@Override
	public void setUniformf(String name, float[] values) {
		int loc = variables.get(name);
		if(values.length == 2) GL20.glUniform2f(loc, values[0], values[1]);
		else if(values.length == 3) GL20.glUniform3f(loc, values[0], values[1], values[2]);
		else if(values.length == 4) GL20.glUniform4f(loc, values[0], values[1], values[2], values[3]);
	}

	@Override
	public void setUniform1i(String name, int value) {
		GL20.glUniform1i(variables.get(name), value);
	}

	@Override
	public int attachShader(ShaderType shaderType, String source) {
		switch(shaderType){
		case VERTEX:
			int v_shader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
			GL20.glShaderSource(v_shader, source);
			GL20.glCompileShader(v_shader);
			GL20.glAttachShader(programID, v_shader);
			
			int vertexVariables = GL20.glGetProgram(programID, GL20.GL_ACTIVE_UNIFORMS);
			for(int i = 0; i < vertexVariables; i++)  {
				String variableName = GL20.glGetActiveUniform(programID, i, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
			    int variableLocation = GL20.glGetUniformLocation(programID, variableName);
			    variables.put(variableName, variableLocation);
				Log.info("Variable '"+variableName+"' processed...");
			}
			return v_shader;
		case FRAGMENT:
			int f_shader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
			GL20.glShaderSource(f_shader, source);
			GL20.glCompileShader(f_shader);
			GL20.glAttachShader(programID, f_shader);
			
			int fragmentVariables = GL20.glGetProgram(programID, GL20.GL_ACTIVE_UNIFORMS);
			Log.debug(fragmentVariables+" frag vars found");
			for(int i = 0; i < fragmentVariables; i++)  {
				String variableName = GL20.glGetActiveUniform(programID, i, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
			    int variableLocation = GL20.glGetUniformLocation(programID, variableName);
			    variables.put(variableName, variableLocation);
				Log.info("Variable '"+variableName+"' processed...");
			}
			return f_shader;
		}
		return -1;
	}

	@Override
	public void finaliseProgram() {
		// Link the program
		GL20.glLinkProgram(programID);
	}

	@Override
	public int detachShader(int shaderID) {
		GL20.glDetachShader(programID, shaderID);
		return shaderID;
	}

	@Override
	public int attachShader(int shaderID) {
		GL20.glAttachShader(programID, shaderID);
		return shaderID;
	}

}

class ARBProgram extends Program {
	public ARBProgram() {
		programID = ARBShaderObjects.glCreateProgramObjectARB();
	}

	@Override
	public void enable() { ARBShaderObjects.glUseProgramObjectARB(programID); }

	@Override
	public void disable() { ARBShaderObjects.glUseProgramObjectARB(0); }

	@Override
	public void setUniform1f(String name, float value) {
		ARBShaderObjects.glUniform1fARB(variables.get(name), value);
	}

	@Override
	public void setUniformf(String name, float[] values) {
		int loc = variables.get(name);
		if(values.length == 2) ARBShaderObjects.glUniform2fARB(loc, values[0], values[1]);
		else if(values.length == 3) ARBShaderObjects.glUniform3fARB(loc, values[0], values[1], values[2]);
		else if(values.length == 4) ARBShaderObjects.glUniform4fARB(loc, values[0], values[1], values[2], values[3]);
	}

	@Override
	public void setUniform1i(String name, int value) {
		ARBShaderObjects.glUniform1iARB(variables.get(name), value);
	}

	@Override
	public int attachShader(ShaderType shaderType, String source) {
		switch(shaderType){
		case VERTEX:
			int v_shader = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
			ARBShaderObjects.glShaderSourceARB(v_shader, source);
			ARBShaderObjects.glCompileShaderARB(v_shader);
			ARBShaderObjects.glAttachObjectARB(programID, v_shader);
			
			int vertexVariables = ARBShaderObjects.glGetObjectParameteriARB(programID, GL20.GL_ACTIVE_UNIFORMS);
			int vertexMaxVariableLength = ARBShaderObjects.glGetObjectParameteriARB(programID, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
			for (int i = 0; i < vertexVariables; i++) {
				String variableName = ARBShaderObjects.glGetActiveUniformARB(programID, i, vertexMaxVariableLength);
				int variableLocation = ARBShaderObjects.glGetUniformLocationARB(programID, variableName);
				variables.put(variableName, variableLocation);
				Log.info("Variable '"+variableName+"' processed...");
			}
			return v_shader;
		case FRAGMENT:
			int f_shader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			ARBShaderObjects.glShaderSourceARB(f_shader, source);
			ARBShaderObjects.glCompileShaderARB(f_shader);
			ARBShaderObjects.glAttachObjectARB(programID, f_shader);
			
			int fragmentVariables = ARBShaderObjects.glGetObjectParameteriARB(programID, GL20.GL_ACTIVE_UNIFORMS);
			int fragmentMaxVariableLength = ARBShaderObjects.glGetObjectParameteriARB(programID, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
			for (int i = 0; i < fragmentVariables; i++) {
				String variableName = ARBShaderObjects.glGetActiveUniformARB(programID, i, fragmentMaxVariableLength);
				int variableLocation = ARBShaderObjects.glGetUniformLocationARB(programID, variableName);
				variables.put(variableName, variableLocation);
				Log.info("Variable '"+variableName+"' processed...");
			}
			return f_shader;
		}
		return -1;
	}

	@Override
	public void finaliseProgram() {
		// Link the program
		ARBShaderObjects.glLinkProgramARB(programID);
	}

	@Override
	public int detachShader(int shaderID) {
		ARBShaderObjects.glDeleteObjectARB(shaderID);
		return shaderID;
	}

	@Override
	public int attachShader(int shaderID) {
		ARBShaderObjects.glAttachObjectARB(programID, shaderID);
		return shaderID;
	}

}


