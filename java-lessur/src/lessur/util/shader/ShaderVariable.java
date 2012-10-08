package lessur.util.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;


import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class used to keep track of variables associated with this
 * shader
 * Work based on SlickShader by Chronocide (Jeremy Klix)
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
class ShaderVariable {
	public enum Qualifier{
		ATTRIBUTE("attribute"), UNIFORM("uniform"), VARYING("varying");
		private static final Map<String, ShaderVariable.Qualifier> stringToEnum =
				new HashMap<String, ShaderVariable.Qualifier>();
		static{
			for(ShaderVariable.Qualifier qual : values()){
				stringToEnum.put(qual.toString(),qual);
			}
		}
		private String name;
		Qualifier(String name){
			this.name = name;
		}
		public String toString(){
			return name;
		}
		public static Qualifier fromString(String token){
			return stringToEnum.get(token);
		}
	}
	public enum VariableType{
		BOOLEAN("boolean"), DOUBLE("double"), FLOAT("float"),
		INTEGER("integer"), VEC2("vec2"), VEC3("vec3");
		private String name;
		VariableType(String name){
			this.name = name;
		}
		public String toString(){
			return name;
		}
	}
	private static final String TYPE_WARN =
			"Warning!\nProblem setting %s variable. " +
					"Expected type %s but got type %s instead.\n";
	private static final String QUAL_WARN =
			"Warning!\nProblem setting %s variable. " +
					"Expected qualifier %s but got %s instead.\n";
	private ShaderVariable.Qualifier qualifier = null;
	private ShaderVariable.VariableType type = null;
	private int vecSize; //size of vector
	private int size; //size of array non arrays are size 1
	private int programID;
	/**Set true if GLSL has removed this unused variable*/
	private boolean isCulled = false;
	String name = "";
	ShaderManager shaderManager;

	ShaderVariable(int programID, String name,
			ShaderVariable.Qualifier qual,
			ShaderVariable.VariableType type,
			int vecSize,
			int size, 
			ShaderManager shaderManager){
		this.programID = programID;
		this.name = name;

		this.qualifier = qual;
		this.type      = type;
		if(vecSize<1){
			throw new IllegalArgumentException("size of elements must be greater than 0");
		}
		this.vecSize = vecSize;
		if(size<1){
			throw new IllegalArgumentException("number of elements must be greater than 0");
		}
		this.size = size;
	}

	public String toString(){
		return name;
	}

	public boolean equals(Object obj){
		if(obj instanceof ShaderVariable){
			return this.toString().equals(obj.toString());
		}
		return false;
	}

	public int hashCode(){
		return name.hashCode();
	}

	void setUniformValue(boolean[] vals){
		if(this.type!=VariableType.BOOLEAN){
			System.err.printf(TYPE_WARN, this.name, this.type, VariableType.BOOLEAN);
		}
		if(this.qualifier!=Qualifier.UNIFORM){
			System.err.printf(QUAL_WARN, this.name, this.qualifier, Qualifier.UNIFORM);
		}
		if(vals.length!=vecSize){
			throw new AssertionError("Incorrect number of arguments.");
		}

		IntBuffer fb = BufferUtils.createIntBuffer(vals.length);
		for(boolean b : vals){
			fb.put(b ? 1 : 0);
		}
		fb.flip();
		switch(vecSize){
		case 1: shaderManager.setUniform1(programID, name, fb.get()); break;
		case 2: shaderManager.setUniform2i(programID, name, fb); break;
		case 3: shaderManager.setUniform3i(programID, name, fb); break;
		case 4: shaderManager.setUniform4i(programID, name, fb); break;
		}
	}

	void setUniformValue(float[] vals){
		if(this.type!=VariableType.FLOAT){
			System.err.printf(TYPE_WARN, this.name, this.type, VariableType.FLOAT);
		}
		if(this.qualifier!=Qualifier.UNIFORM){
			System.err.printf(QUAL_WARN, this.name, this.qualifier, Qualifier.UNIFORM);
		}
		if(vals.length!=vecSize*size){
			throw new AssertionError("Incorrect number of values.\n" +
					"Expected " + vecSize*size + " vlaues but got " +
					vals.length + " values instead.");
		}

		FloatBuffer fb = BufferUtils.createFloatBuffer(vals.length);
		fb.put(vals);
		fb.flip();
		switch(vecSize){
		case 1: shaderManager.setUniform1(programID, name, vals[0]); break;
		case 2: shaderManager.setUniform2(programID, name, fb); break;
		case 3: shaderManager.setUniform3(programID, name, fb); break;
		case 4: shaderManager.setUniform4(programID, name, fb); break;
		}
	}

	void setUniformValue(int[] vals){
		if(this.type!=VariableType.INTEGER){
			System.err.printf(TYPE_WARN, this.type, VariableType.INTEGER);
		}
		if(this.qualifier!=Qualifier.UNIFORM){
			System.err.printf(QUAL_WARN, this.name, this.qualifier, Qualifier.UNIFORM);
		}
		if(vals.length!=vecSize*size){
			throw new AssertionError("Incorrect number of values.\n" +
					"Expected " + vecSize*size + " vlaues but got " +
					vals.length + " values instead.");
		}

		IntBuffer fb = BufferUtils.createIntBuffer(vals.length);
		fb.put(vals);
		fb.flip();
		switch(vecSize){
		case 1: shaderManager.setUniform1(programID, name, fb.get()); break;
		case 2: shaderManager.setUniform2i(programID, name, fb); break;
		case 3: shaderManager.setUniform3i(programID, name, fb); break;
		case 4: shaderManager.setUniform4i(programID, name, fb); break;
		}
	}

	void setUniformValue(Vector2f[] vals){
		if(this.type!=VariableType.VEC2){
			System.err.printf(TYPE_WARN, this.type, VariableType.VEC2);
		}
		if(this.qualifier!=Qualifier.UNIFORM){
			System.err.printf(QUAL_WARN, this.name, this.qualifier, Qualifier.UNIFORM);
		}
		if(vals.length!=vecSize*size){
			throw new AssertionError("Incorrect number of values.\n" +
					"Expected " + vecSize*size + " vlaues but got " +
					vals.length + " values instead.");
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(vals.length*2);

		for(Vector2f vector : vals){
			fb.put(vector.x);
			fb.put(vector.y);
		}
		fb.rewind();
		fb.flip();

		switch(vecSize){
		case 1: shaderManager.setUniform1(programID, name, fb.get()); break;
		case 2: shaderManager.setUniform2(programID, name, fb); break;
		case 3: shaderManager.setUniform3(programID, name, fb); break;
		case 4: shaderManager.setUniform4(programID, name, fb); break;
		}
	}

	void setUniformValue(Vector3f[] vals){
		if(this.type!=VariableType.VEC3){
			System.err.printf(TYPE_WARN, this.type, VariableType.VEC3);
		}
		if(this.qualifier!=Qualifier.UNIFORM){
			System.err.printf(QUAL_WARN, this.name, this.qualifier, Qualifier.UNIFORM);
		}
		if(vals.length!=vecSize*size){
			throw new AssertionError("Incorrect number of values.\n" +
					"Expected " + vecSize*size + " vlaues but got " +
					vals.length + " values instead.");
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(vals.length*3);

		for(Vector3f vector : vals){
			fb.put(vector.x);
			fb.put(vector.y);
			fb.put(vector.z);
		}
		fb.rewind();
		fb.flip();

		switch(vecSize){
		case 1: shaderManager.setUniform1(programID, name, fb.get()); break;
		case 2: shaderManager.setUniform2(programID, name, fb); break;
		case 3: shaderManager.setUniform3(programID, name, fb); break;
		case 4: shaderManager.setUniform4(programID, name, fb); break;
		}
	}
	
	void setUniformMatrix(String name, boolean transpose, float[][] matrix){
		shaderManager.setUniformFloatArray(this.programID, name, transpose, matrix);
	}
}