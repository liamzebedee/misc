package lessur.engine.lighting;

import java.awt.Point;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBTextureRectangle;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.EXTTextureRectangle;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVTextureRectangle;

public class FrameBuffer {

    private int tex_target;
    private boolean supportNonP2;
    private Point size;
    private int a_width, a_height;
    private Integer fbo, tex, depthbuffer;
    private boolean enabled;
    private int old_x, old_y, old_w, old_h;

    public FrameBuffer(Point size) {
        //# See if we support non-p2 textures
        try {
            tex_target = EXTTextureRectangle.GL_TEXTURE_RECTANGLE_EXT;
            supportNonP2 = true;
        } catch(Exception e) {
            try {
                tex_target = ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;
                supportNonP2 = true;
            } catch(Exception e2) {
                try {
                    tex_target = NVTextureRectangle.GL_TEXTURE_RECTANGLE_NV;
                    supportNonP2 = true;
                } catch(Exception e3) {
                    supportNonP2 = false;
                    tex_target = GL11.GL_TEXTURE_2D;
                }
            }
        }
        //# FIXME: This should be handled properly
        tex_target = GL11.GL_TEXTURE_2D;
        if (size!=null) {
            //# Actual size to draw
            this.size = size;
            //# Internal size
            if(!supportNonP2) {
                a_width = next_poweroftwo(size.x);
                a_height = next_poweroftwo(size.y);
            }
            else {
                a_width = size.x;
                a_height = size.y;
            }
        } else {
            IntBuffer viewport = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
            //# Actual size to draw
            this.size = new Point(viewport.get(2), viewport.get(3));
            //# Internal size
            if(!supportNonP2) {
                a_width = next_poweroftwo(this.size.x);
                a_height = next_poweroftwo(this.size.y);
            }
            else {
                a_width = this.size.x;
                a_height = this.size.y;
            }
        }
        fbo = null;
        depthbuffer = null;
        tex = null;
        enabled = false;

        //# Create the FBO right away
        create_fbo();
    }

    private void del() {
        if (fbo != null && depthbuffer != null) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(fbo);
            EXTFramebufferObject.glDeleteRenderbuffersEXT(depthbuffer);
        }
        GL11.glDeleteTextures(tex);
    }

    private int next_poweroftwo(int n) {
        return (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2)));
    }

    private void create_fbo() {
        //# Generate the framebuffer
        boolean FBOEnabled = GLContext.getCapabilities().GL_EXT_framebuffer_object;
        if (!FBOEnabled) System.out.println("Framebuffer objects not supported");
        
        fbo = EXTFramebufferObject.glGenFramebuffersEXT();
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
        //# Create a depthbuffer
        depthbuffer = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthbuffer);

        //# Make depth buffer width, height in size
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                GL11.GL_DEPTH_COMPONENT, a_width, a_height);
        //# Attach depthbuffer to framebuffer
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthbuffer);

        //# Create a texture to render to in size width, height
        tex = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glTexImage2D(tex_target, 0, GL11.GL_RGBA8, a_width, a_height,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (IntBuffer)null);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);


        EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, tex, 0);

        //# Check that all went well
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
        System.out.println("FBO status: "+status);
        if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
            if(status == 36061) {
                //# FIXME: We need to handle this somehow
                System.out.println("Framebuffer objects not supported!");
            }
            else System.out.println("Could not create framebuffer!");
        }
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        //#glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0)
    }

    public void enable() {
        if (enabled) return;
        if (fbo==null) create_fbo();
        enabled = true;
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        old_x = viewport.get(0); old_y=viewport.get(1); old_w=viewport.get(2); old_h=viewport.get(3);
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
        //#glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, self.depthbuffer);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glViewport(0, 0, a_width, a_height);
    }

    public void disable() {
        if(!enabled) return;
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        //#glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0)
        GL11.glViewport(old_x, old_y, old_w, old_h);
        enabled = false;
    }

    public void render(float alpha) {
        if(!enabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex2f(0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex2f(size.x, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex2f(size.x, size.y);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex2f(0.0f, size.y);
            GL11.glEnd();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }
    
}