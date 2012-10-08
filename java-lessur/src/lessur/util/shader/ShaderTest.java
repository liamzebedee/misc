//package org.newdawn.slick.tests;

package lessur.util.shader;

import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * A test for running shaders
 *
 * @author liamzebedee
 */
public class ShaderTest extends BasicGame {
	/** A sample shader, that changes the render position of the pixels, and makes the color +1 */
	Shader sampleShader;
		
	/**
	 * Create a new image rendering test
	 */
	public ShaderTest() {
		super("Slick Shader Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		try {
			sampleShader = Shader.loadShader(new File("res/testVertexShader.vrt"), new File("res/testFragmentShader.frg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sampleShader.setUniformFloatArray("testSecondVar", new float[]{1,2});
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		sampleShader.startUse();
		g.drawString("Test String", 0, 0);
		sampleShader.endUse();
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new ShaderTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
