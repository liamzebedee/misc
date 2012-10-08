package lessur.engine.lighting;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import lessur.engine.LessurMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.SlickCallable;

public class Lighting {
	public ArrayList<ConvexHull> convexHullList = new ArrayList<ConvexHull>();
	public ArrayList<Light> lightList = new ArrayList<Light>();
	private float intensity = 1.0f;
	// private FrameBuffer fbo;
	private Image buffer;
	private Graphics bufferGraphics;
	private static Lighting instance;

	private Lighting(int width, int height) {
		try {
			buffer = new Image(width, height);
			bufferGraphics = buffer.getGraphics();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		// fbo = new FrameBuffer(new Point(width, height));
	}

	public static Lighting getInstance(GameContainer gc) {
		if(instance != null) return instance;
		else{
			instance = new Lighting(gc.getWidth(), gc.getHeight());
			return instance;
		}
	}

	private void clearFbo() {
		GL11.glClearDepth(1.1);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	private void clearFramebufferAlpha() {
		GL11.glColorMask(false, false, false, true);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	/*
	 * 	The basic rendering process for a single frame looks like:
			Clear screen, initialise camera matrix 
			Fill z buffer with all visible objects. 
			For every light: 
				Clear alpha buffer 
				Load alpha buffer with light intensity 
				Mask away shadow regions with shadow geometry 
				Render geometry with full detail (colours, textures etc.) modulated by the light intensity.
	 */
	public void render(final Graphics g, final LessurMap map) {
		SlickCallable slickCall = new SlickCallable() {
			protected void performGLOperations() throws SlickException {
				// Clear the color buffer
				GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

				// Use less-than or equal depth testing
				GL11.glDepthFunc(GL11.GL_LEQUAL);

				// Switch's to frame buffer
				//fbo.enable();
				Graphics.setCurrent(bufferGraphics);
				
				// Clear the fbo, and z-buffer
				clearFbo();
				bufferGraphics.clear();

				// fill z-buffer
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
				GL11.glColorMask(false, false, false, false);
				// TODO Render casters here later...
				for (ConvexHull hull: convexHullList) {
					hull.render();
				}
				GL11.glDepthMask(false);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				for (Light light : lightList) {
					// Clear alpha buffer
					clearFramebufferAlpha();

					// Load alpha buffer with light intensity 
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glColorMask(false, false, false, true);
					light.render(intensity);

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);

					// Mask away shadow regions with shadow geometry
					for (ConvexHull hull: convexHullList) {
						hull.drawShadowGeometry(light);
					}
					GL11.glDisable(GL11.GL_DEPTH_TEST);

					// Draw geometry pass
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
					GL11.glColorMask(true, true, true, false);
					
					map.render(0, 0);
					for (Light light1 : lightList) {
						light1.render(intensity);
					}
					for (ConvexHull hull: convexHullList) {
						//hull.render();
					}

				}
				/*fbo.disable();*/
				bufferGraphics.flush();
				
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				// Render the fbo on top of the color buffer
				//fbo.render(1.0f);
				
			}
		};
		try {
			slickCall.call();
		} catch (SlickException e) {
			e.printStackTrace();
		}

		Graphics.setCurrent(g);
		g.drawImage(buffer, 0, 0);
	}
}

