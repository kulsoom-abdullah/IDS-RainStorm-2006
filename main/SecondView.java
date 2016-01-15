import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import net.java.games.jogl.GLCanvas;
import net.java.games.jogl.GLCapabilities;
import net.java.games.jogl.GLDrawableFactory;
/*
 * SecondView.java
 *
 * Created on January 22, 2006, 1:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Greg Tedder
 */
public class SecondView extends JFrame {
    
    public static SecondView second;
    
    public Renderer2 r;
    
    public static final Dimension PREFERRED_FRAME_SIZE =
        new Dimension (808,616);
    
    public static SecondView CreateSecondView() {
        if(second == null) {
            second = new SecondView();
        }
        return second;
    }
    
    public Dimension getPreferredSize () {
        return PREFERRED_FRAME_SIZE;
    }
    
    /** Creates a new instance of SecondView */
    public SecondView() {
        // init Frame
        super ("Zoom View");
        System.out.println ("constructor");
//        this.setSize(616,808);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // get a GLCanvas
        GLCapabilities capabilities = new GLCapabilities();
        GLCanvas canvas =
            GLDrawableFactory.getFactory().createGLCanvas(capabilities);
        
        // add a GLEventListener, which will get called when the
        // canvas is resized or needs a repaint
        r = new Renderer2("logfile");
        canvas.addGLEventListener(r);
        canvas.addMouseMotionListener(r);
        canvas.addMouseListener(r);
        canvas.addMouseWheelListener(r);
        canvas.addComponentListener(r);
        canvas.addKeyListener(r);
        
        // now add the canvas to the Frame.  Note we use BorderLayout.CENTER
        // to make the canvas stretch to fill the container (ie, the frame)
        getContentPane().add(canvas, BorderLayout.CENTER);
        //System.out.println(this.getHeight());
        
        /**************************************************/
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        this.pack();
        this.setLocation(new Point(617, 0));
        this.setVisible(true);
    }
    
}
