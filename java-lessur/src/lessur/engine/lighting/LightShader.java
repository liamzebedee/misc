/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessur.engine.lighting;

import lessur.engine.lighting.Program.ShaderType;

import org.newdawn.slick.Color;

/**
 *
 * @author Ciano
 */
public class LightShader {
    private static LightShader lightShader;
    public static LightShader getLightShader() {
        if(lightShader==null) lightShader = new LightShader();
        return lightShader;
    }

    private String vertexShader=
                "varying vec2 pos; \n" +
                "void main() \n" +
                "{ \n" +
                "pos = gl_Vertex.xy; \n" +
                "gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.xy, 0.0, 1.0); \n" +
                "}";
    private String fragmentShader =
                "varying vec2 pos; \n" +
                "uniform vec4 color; \n" +
                "void main() { \n" +
                "float t = 1.0 - sqrt(pos.x*pos.x + pos.y*pos.y); \n" +
                "// Enable this line if you want sigmoid function on the light interpolation \n" +
                "//t = 1.0 / (1.0 + exp(-(t*12.0 - 6.0))); \n" +
                "gl_FragColor = vec4(color.r, color.g, color.b, color.a) * t; \n" +
                "}";

    private Program program;
    
    public LightShader() {
    }

    public void createProgram() {
    	program = Program.create();
        program.attachShader(ShaderType.FRAGMENT, fragmentShader);
        program.attachShader(ShaderType.VERTEX, vertexShader);
        program.finaliseProgram();
    }

    public void enable() {
        if(program==null) createProgram();
        program.enable();
    }

    public void disable() {
        program.disable();
    }

    public void setState(Color c) {
        program.setUniformf("color", new float[] {c.r, c.g, c.b, c.a});
    }

    
}
