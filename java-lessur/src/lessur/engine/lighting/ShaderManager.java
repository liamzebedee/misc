/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessur.engine.lighting;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Ciano
 */
public class ShaderManager {
    private static ShaderManager shaderManager;
    public static Boolean hasShaders=null;

    private boolean has_opengl2, has_arb;
    
    public static ShaderManager getShaderManager() {
        if(shaderManager==null) shaderManager = new ShaderManager();
        return shaderManager;
    }
    public static boolean does_support_shaders() {
        if (hasShaders == null) {
            try {
                getShaderManager();
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

    public int getGLVersion() {
        //"""Returns a list with the GL version numbers. I.e. [2, 1, 0]."""
        return Integer.parseInt(Character.toString(GL11.glGetString(GL11.GL_VERSION).charAt(0)));
    }

    private boolean findShaderType() {
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

    public Integer createProgram(String vSource, String fSource) {
        if(has_opengl2) return createShaderOpenGL_L2(vSource, fSource);
        else if(has_arb) return createShaderARB(vSource, fSource);
        else return null;
    }

    public int createShaderOpenGL_L2(String vertex_source, String fragment_source) {
        //# Create shaders
        Integer v_shader = null;
        if (vertex_source!=null) {
            v_shader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(v_shader, vertex_source);
            GL20.glCompileShader(v_shader);
        }

        Integer f_shader = null;
        if (fragment_source!=null) {
            f_shader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(f_shader, fragment_source);
            GL20.glCompileShader(f_shader);
        }

        return createProgramOpenGL2(v_shader, f_shader);
    }

    public int createShaderARB(String vertex_source, String fragment_source) {

        //# Create shaders
        Integer v_shader = null;
        if (vertex_source!=null) {
            v_shader = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
            ARBShaderObjects.glShaderSourceARB(v_shader, vertex_source);
            ARBShaderObjects.glCompileShaderARB(v_shader);
        }

        Integer f_shader = null;
        if (fragment_source!=null) {
            f_shader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
            ARBShaderObjects.glShaderSourceARB(f_shader, fragment_source);
            ARBShaderObjects.glCompileShaderARB(f_shader);
        }

        return createProgramARB(v_shader, f_shader);
    }

    public int createProgramOpenGL2(Integer vShader, Integer fShader) {
        int program = GL20.glCreateProgram();

        //# Attach the shaders
        if (vShader!=null) GL20.glAttachShader(program, vShader);
        if (fShader!=null) GL20.glAttachShader(program, fShader);

        //# Link the program
        GL20.glLinkProgram(program);
        return program;
    }

    public int createProgramARB(Integer vShader, Integer fShader) {
        int program = ARBShaderObjects.glCreateProgramObjectARB();

        //# Attach the shaders
        if (vShader!=null) ARBShaderObjects.glAttachObjectARB(program, vShader);
        if (fShader!=null) ARBShaderObjects.glAttachObjectARB(program, fShader);

        //# Link the program
        ARBShaderObjects.glLinkProgramARB(program);
        return program;
    }

    public void enableProgram(int program) {
        if(has_opengl2) enableProgramOpenGL2(program);
        else if(has_arb) enableProgramARB(program);
        else System.out.println("No shader support for enable");
    }

    public void enableProgramOpenGL2(int program) {
        GL20.glUseProgram(program);
    }

    public void enableProgramARB(int program) {
        ARBShaderObjects.glUseProgramObjectARB(program);
    }

    public void setUniform1i(int program, String name, int value) {
        if(has_opengl2) {
            int loc = GL20.glGetUniformLocation(program, name);
            GL20.glUniform1i(loc, value);
        }
        else if(has_arb) {
            int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
            ARBShaderObjects.glUniform1iARB(loc, value);
        }
        else System.out.println("Cannot call this without shader support!");
    }

    public void setUniform1f(int program, String name, float value) {
        if(has_opengl2) {
            int loc = GL20.glGetUniformLocation(program, name);
            GL20.glUniform1f(loc, value);
        }
        else if(has_arb) {
            int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
            ARBShaderObjects.glUniform1fARB(loc, value);
        }
        else System.out.println("Cannot call this without shader support!");
    }

    public void setUniformf(int program, String name, float values[]) {
        if(has_opengl2) {
            int loc = GL20.glGetUniformLocation(program, name);
            if(values.length == 2) GL20.glUniform2f(loc, values[0], values[1]);
            else if(values.length == 3) GL20.glUniform3f(loc, values[0], values[1], values[2]);
            else if(values.length == 4) GL20.glUniform4f(loc, values[0], values[1], values[2], values[3]);
        }
        else if(has_arb) {
            int loc = ARBShaderObjects.glGetUniformLocationARB(program, name);
            if(values.length == 2) ARBShaderObjects.glUniform2fARB(loc, values[0], values[1]);
            else if(values.length == 3) ARBShaderObjects.glUniform3fARB(loc, values[0], values[1], values[2]);
            else if(values.length == 4) ARBShaderObjects.glUniform4fARB(loc, values[0], values[1], values[2], values[3]);
        }
        else System.out.println("Cannot call this without shader support!");
    }

}
