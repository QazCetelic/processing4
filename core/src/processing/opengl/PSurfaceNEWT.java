package processing.opengl;

import java.awt.Color;
import java.awt.Frame;
import java.net.URL;
import java.util.ArrayList;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.newt.Display;
import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PSurface;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class PSurfaceNEWT implements PSurface {
  /** Selected GL profile */
  public static GLProfile profile;

  PJOGL pgl;

  GLWindow window;
  Frame frame;
  FPSAnimator animator;

  PApplet sketch;
  PGraphics graphics;

  int sketchWidth;
  int sketchHeight;

  public PSurfaceNEWT(PGraphics graphics) {
    this.graphics = graphics;
    this.pgl = (PJOGL) ((PGraphicsOpenGL)graphics).pgl;
  }

  public void initOffscreen() {
    // TODO Auto-generated method stub

  }

  public Frame initFrame(PApplet sketch, Color backgroundColor,
                         int deviceIndex, boolean fullScreen,
                         boolean spanDisplays) {
    this.sketch = sketch;

    Display display = NewtFactory.createDisplay(null);
    display.addReference(); // trigger creation
    Screen screen = NewtFactory.createScreen(display, 0);
    screen.addReference();
    int screenWidth = screen.getWidth();
    int screenHeight = screen.getHeight();
    System.out.println("Screen res " + screenWidth + "x" + screenHeight);

    ArrayList<MonitorDevice> monitors = new ArrayList<MonitorDevice>();
    for (int i = 0; i < screen.getMonitorDevices().size(); i++) {
      MonitorDevice monitor = screen.getMonitorDevices().get(i);
      System.out.println("Monitor " + monitor.getId() + " ************");
      System.out.println(monitor.toString());
      System.out.println(monitor.getViewportInWindowUnits());
      System.out.println(monitor.getViewport());

      monitors.add(monitor);
    }
    System.out.println("*******************************");

    if (profile == null) {
      if (PJOGL.PROFILE == 2) {
        try {
          profile = GLProfile.getGL2ES1();
        } catch (GLException ex) {
          profile = GLProfile.getMaxFixedFunc(true);
        }
      } else if (PJOGL.PROFILE == 3) {
        try {
          profile = GLProfile.getGL2GL3();
        } catch (GLException ex) {
          profile = GLProfile.getMaxProgrammable(true);
        }
        if (!profile.isGL3()) {
          PGraphics.showWarning("Requested profile GL3 but is not available, got: " + profile);
        }
      } else if (PJOGL.PROFILE == 4) {
        try {
          profile = GLProfile.getGL4ES3();
        } catch (GLException ex) {
          profile = GLProfile.getMaxProgrammable(true);
        }
        if (!profile.isGL4()) {
          PGraphics.showWarning("Requested profile GL4 but is not available, got: " + profile);
        }
      } else throw new RuntimeException(PGL.UNSUPPORTED_GLPROF_ERROR);
    }

    // Setting up the desired capabilities;
    GLCapabilities caps = new GLCapabilities(profile);
    caps.setAlphaBits(PGL.REQUESTED_ALPHA_BITS);
    caps.setDepthBits(PGL.REQUESTED_DEPTH_BITS);
    caps.setStencilBits(PGL.REQUESTED_STENCIL_BITS);
    caps.setBackgroundOpaque(true);
    caps.setOnscreen(true);
    pgl.capabilities = caps;

    sketchWidth = sketch.sketchWidth();
    sketchHeight = sketch.sketchHeight();

    window = GLWindow.create(screen, caps);
    window.setPosition(0, 0);
    window.setSize(sketchWidth, sketchHeight);
    window.setVisible(true);


    NEWTMouseListener mouseListener = new NEWTMouseListener();
    window.addMouseListener(mouseListener);
    NEWTKeyListener keyListener = new NEWTKeyListener();
    window.addKeyListener(keyListener);
//    NEWTWindowListener winListener = new NEWTWindowListener();
//    window.addWindowListener(winListener);

    DrawListener drawlistener = new DrawListener();
    window.addGLEventListener(drawlistener);

    animator = new FPSAnimator(window, 60);


    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyNotify(final WindowEvent e) {
        animator.stop();
      }
    });

    frame = new DummyFrame();
    return frame;
  }

  class DummyFrame extends Frame {

    public DummyFrame() {
      super();
//      setVisible(false);
    }

    @Override
    public void setResizable(boolean resizable) {
      super.setResizable(resizable);

      // call NEWT function to make the window resizable
    }

    @Override
    public void setVisible(boolean visible) {
      // don't call super.setVisible()
      // make the NEWT window visible/invisible
      window.setVisible(visible);
    }

    @Override
    public void setTitle(String title) {
      window.setTitle(title);
    }
  }


  public void setTitle(String title) {
    window.setTitle(title);
  }

  public void setVisible(boolean visible) {
    window.setVisible(visible);
  }

  public void setResizable(boolean resizable) {
    // TODO Auto-generated method stub

  }

  public void placeWindow(int[] location) {
    // TODO Auto-generated method stub

  }

  public void placeWindow(int[] location, int[] editorLocation) {
    // TODO Auto-generated method stub

  }

  public void placePresent(Color stopColor) {
    // TODO Auto-generated method stub

  }

  public void setupExternalMessages() {
    // TODO Auto-generated method stub

  }

  public void startThread() {
    animator.start();
  }

  public void pauseThread() {
    animator.pause();
  }

  public void resumeThread() {
    animator.resume();
  }

  public boolean stopThread() {
    return animator.stop();
  }

  public boolean isStopped() {
    return !animator.isAnimating();
  }

  public void setSize(int width, int height) {
    // TODO Auto-generated method stub

  }

  public void setFrameRate(float fps) {
    // TODO Auto-generated method stub

  }

  public void requestFocus() {
    // TODO Auto-generated method stub

  }

  public void blit() {
    // TODO Auto-generated method stub

  }





  class DrawListener implements GLEventListener {
    public void display(GLAutoDrawable drawable) {
      System.out.println("yea");
      pgl.getGL(drawable);
      pgl.getBuffers(window);
      sketch.handleDraw();
      if (sketch.frameCount == 1) {
        requestFocus();
      }
    }
    public void dispose(GLAutoDrawable drawable) {
      pgl.getGL(drawable);
      sketch.dispose();
//      if (sketch.exitCalled) {
//        sketch.exitActual();
//      }
    }
    public void init(GLAutoDrawable drawable) {
      pgl.getGL(drawable);
      sketch.start();
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
      pgl.getGL(drawable);

    }
  }

  protected class NEWTWindowListener implements com.jogamp.newt.event.WindowListener {
    public NEWTWindowListener() {
      super();
    }
    @Override
    public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {
//      pg.parent.focusGained(null);
    }

    @Override
    public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {
//      pg.parent.focusLost(null);
    }

    @Override
    public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {
    }

    @Override
    public void windowDestroyed(com.jogamp.newt.event.WindowEvent arg0) {
    }

    @Override
    public void windowMoved(com.jogamp.newt.event.WindowEvent arg0) {
    }

    @Override
    public void windowRepaint(com.jogamp.newt.event.WindowUpdateEvent arg0) {
    }

    @Override
    public void windowResized(com.jogamp.newt.event.WindowEvent arg0) { }
  }

  // NEWT mouse listener
  protected class NEWTMouseListener extends com.jogamp.newt.event.MouseAdapter {
    public NEWTMouseListener() {
      super();
    }
    @Override
    public void mousePressed(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.PRESS);
    }
    @Override
    public void mouseReleased(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.RELEASE);
    }
    @Override
    public void mouseClicked(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.CLICK);
    }
    @Override
    public void mouseDragged(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.DRAG);
    }
    @Override
    public void mouseMoved(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.MOVE);
    }
    @Override
    public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.WHEEL);
    }
    @Override
    public void mouseEntered(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.ENTER);
    }
    @Override
    public void mouseExited(com.jogamp.newt.event.MouseEvent e) {
      nativeMouseEvent(e, MouseEvent.EXIT);
    }
  }

  // NEWT key listener
  protected class NEWTKeyListener extends com.jogamp.newt.event.KeyAdapter {
    public NEWTKeyListener() {
      super();
    }
    @Override
    public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
      nativeKeyEvent(e, KeyEvent.PRESS);
    }
    @Override
    public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
      nativeKeyEvent(e, KeyEvent.RELEASE);
    }
    public void keyTyped(com.jogamp.newt.event.KeyEvent e)  {
      nativeKeyEvent(e, KeyEvent.TYPE);
    }
  }

  protected void nativeMouseEvent(com.jogamp.newt.event.MouseEvent nativeEvent,
                                  int peAction) {
    int modifiers = nativeEvent.getModifiers();
    int peModifiers = modifiers &
                      (InputEvent.SHIFT_MASK |
                       InputEvent.CTRL_MASK |
                       InputEvent.META_MASK |
                       InputEvent.ALT_MASK);

    int peButton = 0;
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
      peButton = PConstants.LEFT;
    } else if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
      peButton = PConstants.CENTER;
    } else if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
      peButton = PConstants.RIGHT;
    }

    if (PApplet.platform == PConstants.MACOSX) {
      //if (nativeEvent.isPopupTrigger()) {
      if ((modifiers & InputEvent.CTRL_MASK) != 0) {
        peButton = PConstants.RIGHT;
      }
    }

    int peCount = 0;
    if (peAction == MouseEvent.WHEEL) {
      peCount = nativeEvent.isShiftDown() ? (int)nativeEvent.getRotation()[0] :
                                            (int)nativeEvent.getRotation()[1];
    } else {
      peCount = nativeEvent.getClickCount();
    }

    MouseEvent me = new MouseEvent(nativeEvent, nativeEvent.getWhen(),
                                   peAction, peModifiers,
                                   nativeEvent.getX(), nativeEvent.getY(),
                                   peButton,
                                   peCount);

    sketch.postEvent(me);
  }

  protected void nativeKeyEvent(com.jogamp.newt.event.KeyEvent nativeEvent,
                                int peAction) {
    int peModifiers = nativeEvent.getModifiers() &
                      (InputEvent.SHIFT_MASK |
                       InputEvent.CTRL_MASK |
                       InputEvent.META_MASK |
                       InputEvent.ALT_MASK);

    char keyChar;
    if (nativeEvent.getKeyChar() == 0) {
      keyChar = PConstants.CODED;
    } else {
      keyChar = nativeEvent.getKeyChar();
    }

    KeyEvent ke = new KeyEvent(nativeEvent, nativeEvent.getWhen(),
                               peAction, peModifiers,
                               keyChar,
                               nativeEvent.getKeyCode());

    sketch.postEvent(ke);
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public void setCursor(int kind) {
    // TODO Auto-generated method stub

  }

  public void setCursor(PImage image, int hotspotX, int hotspotY) {
    // TODO Auto-generated method stub

  }

  public void showCursor() {
    window.setPointerVisible(true);
  }

  public void hideCursor() {
    window.setPointerVisible(false);
  }
}
