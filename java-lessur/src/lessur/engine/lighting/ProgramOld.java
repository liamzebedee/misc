/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessur.engine.lighting;

/**
 *
 * @author Ciano
 */
public class ProgramOld {

    private int program;
    private ShaderManager sm;

    public ProgramOld(String vShader, String fShader) {
        sm = ShaderManager.getShaderManager();
        program = sm.createProgram(vShader, fShader);
    }

    public void setUniformf(String name, float values[]) {
        sm.setUniformf(program, name, values);
    }
    public void setUniform1f(String name, float value) {
        sm.setUniform1f(program, name, value);
    }
    public void enable() {
        sm.enableProgram(program);
    }
    public void disable() {
        sm.enableProgram(0);
    }
}
