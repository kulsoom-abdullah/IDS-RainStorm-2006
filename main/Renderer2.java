
import java.awt.Color;
//import java.awt.Component;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

//import javax.swing.JFrame;
//import java.
import net.java.games.jogl.GLEventListener;
import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLU;
import net.java.games.jogl.util.GLUT;
import javax.media.opengl.GLAutoDrawable;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Renderer2 implements GLEventListener,MouseMotionListener,
        MouseListener,MouseWheelListener, ComponentListener, KeyListener {
    
	/** This hold the main renderer for callback */
	Renderer renderer;
	/** This holds a clone fo the Main renderer's RedBox 
	 * at the time of alarm transfer 
	 */
	
	Rectangle2D.Float RedBox;
	
	// These have to do with the 4 views in this renderer
	Rectangle2D.Float CenterView;
	Rectangle2D.Float IPView;
	Rectangle2D.Float SubnetView;
	Rectangle2D.Float ChronoView;
	
	// This is the padding on top of the subnet view
	float sub_pad_top = 10f;
	
	// this is the offset of one IP
	float ip_off_normal = 0f;
	
	// This is the IP at the top of the RedBox
	String StartIP = "0.0.0.0";
	
	/** The list of alarms sent from renderer */
	LinkedList ZoomList = new LinkedList();
	/** A boolean to help with rendering and alarm reset */
    boolean rendered = true;
    
    /** A list of alarms that have destinations */
    LinkedList DestList = new LinkedList();
    
    /** An entire list of converted coordinate ZoomedAlarms */
    LinkedList ConvertList = new LinkedList();
    
    /** The ZoomAlarm that is currently colided with */
    ZoomedAlarm alarm_colision;
    
    /** Replacing the one above, a list of all colisions */
    LinkedList alarm_colision_list = new LinkedList();
    
    /** The current number of colisions */
    int colision_number = 0;
    
    /** The currently viewed colision */
    int current_colision = 0;
    
    /** The x increment of the center view */
    float x_center_increment = 0;
    
    /** The info box to display alarm info */
    InfoBox infoBox = new InfoBox();
    
    /** The dotted Line Drawing object */
    Arrow dLine = new Arrow();
	
    private BufferedImage image;

    File f = new File("screen");
//     The label that contains the screen capture image
    //private JLabel imageLabel;

//     A flag that indicates a screen shot should be taken
    private boolean capture;
    //put ip array strings here
    
    //int width = 616;//(72*8)+(2*10);
    //int height = 808;//(256*3)+(2*10);
//    int margin_top    = 10;
//    int margin_bottom = 10;
//    int margin_left   = 10;
//    int margin_right  = 10;
    int timespan   = ( 24*60*60 );
    int colwidth   = 72;
    int num_cols   = 8;
    int winwidth   = 72;
    int colheight  = 788;
    long totalips   = 164096;//have this hard coded, could have a function to calculate totalips
    int ips_row    = 0;
    int ips_cols   = 0;
    int winheight  =  9;//9.58502340093604;
    //int current    = 'all',
    int timewin    = 24;
    int timestart  = 0;
    int ipwin      = 200;//256;
    int ipstart    = 0;
    int zoom       = 1;
    float offset     = 0;
    
    /*** ADDED ****/
//    float untranslatey = 0.066666667f; // 6 / 90
    
    /** ADDED */
    
//    int jwinwidth = 0;
//    int jwinheight = 0;
    
    //String[] subnets={ "128.61.0.0/16", "130.207.0.0/16", "199.77.128.0/17", "143.215.15.0/24" };
    //String[] subnets={ "128.61.0.0", "130.207.0.0", "199.77.128.0", "143.215.15.0" };
    
    long [] yo={1,2,3};
    String[] subnets={ "115.196.0.0/16", "33.50.0.0/16","112.176.0.0/17", };
    
    //long[] subnetint={2151481344l, 2194604032l, 3343745024l, 2413235968l};
    long[] subnetint={1942224896l, 556924928l, 1890582528l, 951240960l}; //updated
    
    long[] subnetmask= { 4294901760l, 4294901760l, 4294934528l, 4294967040l};//hard coded
    
    //long[] subnetint={2151481344l, 2194604032l,3343745024l,2413235968l};
    
    //subnetint[0]= 2151481344;
    
//	subnetint[1]=2194604032;
//	subnetint[2]=3343745024;
//	subnetint[3]=2413235968;//hard coded
    
    //long[] subnetmask= { 4294901760l, 4294901760l, 4294934528l, 4294967040l};//hard coded
    
    //protected static final double TWO_PI = 2 * Math.PI;
    //protected static final double ARC_SEGMENT = TWO_PI / 5; // how many circle outline points
    
    int startDay=0; //midnight
    int endDay=0; //11:59
    int mouse_x,mouse_y;//keeps track of mouse position
    float mouse_xf, mouse_yf; // keeps track of floating precision mouse possition.
    private GL gl;
    private GLU glu;
    private GLDrawable gldrawable;
    GLUT glut=new GLUT();
    String logfile;
    //ParseAlarmLog alarmList;
    //String testinput[][];
    //JFrame _frame;
    
    private float 	_screen_TopRightX,
//            _screen_TopRightY,
            _screen_TopLeftX;
//            _screen_TopLeftY,
//            _screen_BottomRightX,
//            _screen_BottomRightY,
//            _screen_BottomLeftX,
//            _screen_BottomLeftY,
//            _screen_Width,
//            _screen_Height,
//            _frame_Width,
//            _frame_Height,
//            margin;
    
    /** The CameraControl class for zoom */
    public CameraControl camera;
    
    public Renderer2(String filename){
        camera = new CameraControl();
        mouse_xf = 0;
        mouse_yf = 0;
        
        this.logfile=filename;
System.out.println(logfile+" in renderer2");
        
    }
    
    /** Called after OpenGL is init'ed
     */
    public void init(GLDrawable drawable) {
        //System.out.println ("init()");
    	
        gl = drawable.getGL();
        gldrawable=drawable;
        // set erase color
        gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f ); //black
        // set drawing color and point size
        gl.glColor3f( 1.0f, 1.0f, 1.0f ); //white
        gl.glPointSize(3.0f); //a 'dot' is 4 by 4 pixels
        //alarmList=new ParseAlarmLog(logfile);
        
        short sh = 11111;
        
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_LINE_STIPPLE);
        gl.glLineStipple(2, sh);
        gl.glDisable(GL.GL_LINE_STIPPLE);
        gl.glDisable(GL.GL_LINE_SMOOTH);
        
        
    }
    
    /** Called by drawable to initiate drawing
     */
    public void display(GLDrawable drawable) {
        //System.out.println ("display()");
        
        gl = drawable.getGL();
        glu = drawable.getGLU();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //drawADot(gl);
        //drawSomeLines(gl);
        //drawFilledRect (gl, Color.cyan.darker());
        
        
//        gl.glViewport( 0, 0, (int)camera.panel_width, (int)camera.panel_height );
//        gl.glMatrixMode( GL.GL_PROJECTION );
//        gl.glLoadIdentity();
//        glu.gluOrtho2D(0.0, (double)camera.panel_width, (double)camera.panel_height, 0.0);
        gl.glViewport( (int)CenterView.x, (int)CenterView.y, (int)CenterView.width, (int)CenterView.height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double)CenterView.width, (double)CenterView.height, 0.0);
        
        
        // set the proper mouse possition
        // ***CODE ADDED BY GREG TEDDER***
        int x = 0;
        int y = 0;
        int viewport[] = new int[4];
        double modelview[] = new double[16];
        double projection[] = new double[16];
        float winX = 0;
        float winY = 0;
        float winZ[] = new float[1];
        double posX[] = new double[1];
        double posY[] = new double[1];
        double posZ[] = new double[1];
        
        
        // push a matrix onto the stack
        gl.glPushMatrix();
        
        /** NO MORE TRANSALATION OR SCALING */
        // scale the matrix
        gl.glScalef(camera.scale_x, camera.scale_y, 1.0f);
        // now render everything on top
        gl.glTranslatef(camera.translate_x, camera.translate_y, 0.0f);
        
        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview);
        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection);
        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
        
//      Get the x_center_increment for later calculations
        winX = 1.0f;
        winY = (float)viewport[3] - 1.0f;
        gl.glReadPixels(1, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
        glu.gluProject((double)winX, (double)winY, (double)winZ[0], modelview, projection, viewport, posX, posY, posZ);
        
        x_center_increment = (float)posX[0];
        
        winX = 0.0f;
        winY = (float)viewport[3] - 1.0f;
        gl.glReadPixels(1, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
        glu.gluProject((double)winX, (double)winY, (double)winZ[0], modelview, projection, viewport, posX, posY, posZ);
        
        x_center_increment -= (float)posX[0];
        
//        System.out.println("ScreenPosX: " + posX[0]);
//        System.out.println("Width: " + IPView.width);
//        System.out.println("ScreenIncrement: " + x_center_increment);
        
        
        // get the mouse possition
        winX = (float)mouse_x;
        winY = (float)viewport[3] - (float)mouse_y;
        gl.glReadPixels(mouse_x, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
        
        glu.gluUnProject((double)winX, (double)winY, (double)winZ[0], modelview, projection, viewport, posX, posY, posZ);
        
        mouse_xf = (float)posX[0];
        mouse_yf = (float)posY[0];
//        mouse_y = (int)posY[0];
        
//        mouse_x = (int)posX[0];
//        mouse_y = (int)posY[0];
        
//        float yy = (float)this.mouse_y / (float)camera.panel_y * (float)this.height;
//        mouse_y = (int)yy;
//        System.out.println(yy);
        
        
        // ***END OF CODE ADDITION***
        
        setDrawableSize();
        //drawBorderBox(gl);
        //drawIPcolumns(gl);
        //drawIPlines(gl);
        if(camera.zoom > 0) {
            // don't draw the red box
            camera.update();
        } else {
//            drawRedBox(gl);
        }
        
        
        //drawAlarms(gl);
        
        drawIPPartitions(gl);
        drawClockPartitions(gl);
        
        PreDrawColision();
        
        drawAlarms2(gl);
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        
        gl.glColor3f(0.3f,0.3f,0.3f);
        gl.glBegin(GL.GL_LINE_LOOP);
        	gl.glVertex2f(0f,1f);
        	gl.glVertex2f(0f, CenterView.height);
        	gl.glVertex2f(CenterView.width, CenterView.height);
        	gl.glVertex2f(CenterView.width, 1f);
        gl.glEnd();
        	
        
        // draw the controll pad
        //camera.drawControlPad(gl,(float)mouse_x,(float)mouse_y);
        
        gl.glPopMatrix();
        
        // TODO Now draw the measurements
        gl.glViewport( (int)IPView.x, (int)IPView.y, (int)IPView.width, (int)IPView.height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double)IPView.width, (double)IPView.height, 0.0);
        
        gl.glPushMatrix();
        gl.glColor3f(0.1f,0.1f,0.1f);
        gl.glBegin(GL.GL_QUADS);
        	gl.glVertex3f(0,0,1.0f);
        	gl.glVertex3f(0,IPView.height,1.0f);
        	gl.glVertex3f(IPView.width,IPView.height,1.0f);
        	gl.glVertex3f(IPView.width,0,1.0f);
        gl.glEnd();
        gl.glScalef(1.0f, camera.scale_y, 1.0f);
        drawIPPartitionMarkers(gl);
        gl.glPopMatrix();
        
        /**********************SUBNET VIEW**********************/
        gl.glViewport( (int)SubnetView.x, (int)SubnetView.y, (int)SubnetView.width, (int)SubnetView.height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double)SubnetView.width, (double)SubnetView.height, 0.0);
        
        gl.glPushMatrix();
        gl.glColor3f(0.1f,0.1f,0.1f);
        gl.glBegin(GL.GL_QUADS);
        	gl.glVertex3f(0,0,1.0f);
        	gl.glVertex3f(0,SubnetView.height,1.0f);
        	gl.glVertex3f(SubnetView.width,SubnetView.height,1.0f);
        	gl.glVertex3f(SubnetView.width,0,1.0f);
        gl.glEnd();
        // no scaling for this one
        drawSubnets(gl);
        gl.glPopMatrix();
        
        /**********************CHRONO VIEW**********************/
        gl.glViewport( (int)ChronoView.x, (int)ChronoView.y, (int)ChronoView.width, (int)ChronoView.height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double)ChronoView.width, (double)ChronoView.height, 0.0);
        
        gl.glPushMatrix();
        gl.glColor3f(0.1f,0.1f,0.1f);
        gl.glBegin(GL.GL_QUADS);
        	gl.glVertex3f(0,0,1.0f);
        	gl.glVertex3f(0,ChronoView.height,1.0f);
        	gl.glVertex3f(ChronoView.width,ChronoView.height,1.0f);
        	gl.glVertex3f(ChronoView.width,0,1.0f);
        gl.glEnd();
        drawClock(gl);
        gl.glPopMatrix();
        
        /**********************OVER VIEW**********************/
        gl.glViewport( 0, 0, (int)camera.panel_width, (int)camera.panel_height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double)camera.panel_width, (double)camera.panel_height, 0.0);
        
        drawSubnetDirection(gl);
        
        drawColision(gl);
        //for screenshots
        if( capture ) {
        	TakeScreenshotJOGL1.writeBufferToFile(drawable,f);
        	//captureImage(drawable);
        	capture = false;
        	}
    }
    
    
    
    /** Called to indicate the drawing surface has been moved and/or resized
     * In our case, draw the same main screen, no animation etc.
     */
    public void reshape(GLDrawable drawable,
            int x,
            int y,
            int width,
            int height) {
        // System.out.println ("reshape()");
        GL gl = drawable.getGL();
        GLU glu = drawable.getGLU();
        gl.glViewport( 0, 0, width, height/2 );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        
        camera.panel_width = width;
        camera.panel_height = height;
        
        // for rectangle conversion
        float w = (float)width;
        float h = (float)height;
        
        float w1 = 0.14f;
        float w2 = 0.72f;
        
        float h1 = 0.04f;
        float h2 = 0.96f;
        
        // set up the views here
        /** x moves 7%, y moves 4%, width = 86%, height = 96% */
        CenterView = new Rectangle2D.Float((w * w1),
        		//(h * h1),
        		0,
        		(w * w2),
        		(h * h2));
        /** x moves 0, y moves 4%, width = 7%, height = 96% */
        IPView = new Rectangle2D.Float(0f,
        		//(h * h1),
        		0,
        		(w * w1),
        		(h * h2));
        /** x moves 93%, y moves 4%, width = 7%, height = 96%*/
        SubnetView = new Rectangle2D.Float((w * (w1 + w2)),
        		//(h * h1),
        		0,
        		(w * w1),
        		(h * h2));
        /** x moves 7%, y moves 0%, width = 86%, height = 4%*/
        ChronoView = new Rectangle2D.Float((w * w1),
        		//0f,
        		(h * h2),
        		(w * w2),
        		(h * h1));
        
        /* if there is any variance in window size, 616 can quickly become 608.563 etc, 
         * throwing all your calculations off */
//        glu.gluOrtho2D( 0.0, 616.0, 808.0, 0.0);
        
        // by changing this, many of the calculations you have already done will now work
        glu.gluOrtho2D(0.0, (double)width, (double)height/2, 0.0);
        
        initValues();
    }
    
    
    /** Called by drawable to indicate mode or device has changed
     */
    public void displayChanged(GLDrawable drawable,
            boolean modeChanged,
            boolean deviceChanged) {
        //System.out.println ("displayChanged()");
    }
    
   /*
    * OUR HELPER METHODS
    */
    
    
    /** This is really drawing one dot.. would need to call this for every alarm
     */
    
    protected void drawADot(GL gl, float x, float y, Color color) {
        gl.glColor3f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
          /*TODO need to implement the alarm type color scheme,
          in a way for user to filter on specific alarm color */
//        gl.glBegin(GL.GL_LINES);
//        
//        gl.glVertex2f(x, y);
//        gl.glVertex2f(x+1, y);//dot is 1 pixel on main screen, do highest alarm in case of overlap
//        gl.glEnd();
//        drawCircle(gl, x, y, 8f);
        
        gl.glBegin(GL.GL_QUADS);
        
        gl.glVertex2f((x-3), (y-3));
        gl.glVertex2f((x - 3), (y+3));
        gl.glVertex2f((x+3), (y+3));
        gl.glVertex2f((x+3), (y-3));
        
        gl.glEnd();
    }
    
    
    public void setDrawableSize(){
//        _frame_Height 			= 808f;
//        _frame_Width 			= 616f - 70f;
//        _screen_TopRightX 		= _frame_Width  	- 20;
//        _screen_TopRightY 		= margin_top;
//        _screen_TopLeftX 		= margin_left;
//        _screen_TopLeftY 		= margin_bottom;
//        _screen_BottomRightX 	= _frame_Width  	- 20;
//        _screen_BottomRightY 	= _frame_Height 	- 10;
//        _screen_BottomLeftX 		= 10;
//        _screen_BottomLeftY 		= _frame_Height 	- 10;
//        _screen_Width 			= _screen_TopRightX - _screen_TopLeftX;
//        _screen_Height 			= _screen_TopRightY - _screen_BottomRightY;
    }
    
    /** Draws border in the frame
     */
    protected void drawBorderBox(GL gl){
        
//        gl.glColor3f(0.5f, 0.5f, 0.5f ); //gray
//        gl.glBegin(GL.GL_LINES);
//        gl.glVertex2i(margin_left, margin_bottom);
//        gl.glVertex2i(margin_top, height-margin_top);
//        gl.glVertex2i(margin_top, height-margin_top);
//        gl.glVertex2i(colwidth*num_cols+margin_right, height-margin_right);
//        gl.glVertex2i(colwidth*num_cols+margin_right,margin_right);
//        gl.glVertex2i(margin_left, margin_bottom);
//        gl.glVertex2i(colwidth*num_cols+margin_right,margin_bottom);
//        gl.glEnd();
        
        //2 lines below are for testing x.y cordinate discrepancy
//        gl.glBegin(GL.GL_LINES);
//        gl.glVertex2i(0,674);
//        gl.glVertex2i(colwidth,674);
//        gl.glEnd();
//        
//        gl.glBegin(GL.GL_LINES);
//        gl.glVertex2i(margin_right,805);
//        gl.glVertex2i(colwidth+margin_right,805);
//        gl.glEnd();
    }
    
    /** This should become our new drawAlarms function */
    protected void drawAlarms2(GL gl) {
    	// Iterate over the alarms sent to us from Renderer
    	Iterator it = ZoomList.iterator();
    	
    	// Only the alarms that have VictimIP's Listed
    	DestList = new LinkedList(); 
    	
    	// All the drawn alarams contained in ZoomedAlarm classes
    	ConvertList = new LinkedList();
    	
    	while(it.hasNext()) {
    		ZoomedAlarm temp = (ZoomedAlarm)it.next();
    		// System.out.println(temp.x);
    		drawADot(gl,
    				temp.x, 
    				temp.y, 
    				temp.color);
    		
    		
    		
//          gl.glColor3f(0.5f, 0.5f, 0.5f);
//          gl.glRasterPos2f((float)temp.x,(float)temp.y);
//          // TODO FONT
//          glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, temp.alarm.getVictimIP());
          
    		/* This isn't used until further down, it
    		 * is what makes sure that only alarms with
    		 * victimIP's are put into DestList
    		 */
      		String subS = temp.alarm.getVictimIP();
      		
      		/** BEGIN, Getting coordinates from jogl */
      		int x = 0;
  	        int y = 0;
  	        int viewport[] = new int[4];
  	        double modelview[] = new double[16];
  	        double projection[] = new double[16];
  	        float winX = 0;
  	        float winY = 0;
  	        float winZ[] = new float[1];
  	        double posX[] = new double[1];
  	        double posY[] = new double[1];
  	        double posZ[] = new double[1];
  			
  			gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview);
  	        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection);
  	        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
  	        
  	        winX = temp.x;
  	        winY = temp.y;//(float)viewport[2] - (float)temp.y;
  	        //gl.glReadPixels((int)temp.x, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
  	        
  	        glu.gluProject((double)winX, (double)winY, 0.0/*(double)winZ[0]*/, modelview, projection, viewport, posX, posY, posZ);
  	        
  	        float xf = (float)posX[0];
  	        float yf = camera.panel_height - (float)posY[0]; // comes back upside down, so reverse
  	        
  	      /** END, Getting coordinates from jogl */
  	        
  	        ZoomedAlarm al = new ZoomedAlarm();
  	        
  	        al.x = xf;
	        al.y = temp.y;
	        al.alarm = temp.alarm;
	        al.screen_line.x1 = xf;
	        al.screen_line.y1 = yf;
	        al.color = temp.color;
	        
	        al.screen_projection.x = xf - camera.scale_x * 3;
	        al.screen_projection.y = yf - camera.scale_x * 3;
	        al.screen_projection.width = camera.scale_x * 6f;
	        al.screen_projection.height = camera.scale_x * 6f;
  	        
  	        // everything gets added here
  	        ConvertList.add(al);
  	        
      		// Add all alarms with VictimIP's to DestList
      		if(subS.equals("0")) {
      			// do nothing
      		} else {
      			// Set the draw a line boolean to true
      			al.line = true;
      			// add the alarm to the list
      	        DestList.add(al);
      		}
    	}
    	
//    	if(alarm_colision_list.size() > 0) {
//    		ZoomedAlarm al = (ZoomedAlarm)alarm_colision_list.get(current_colision);
//    		drawADot(gl,
//    				al.x, 
//    				al.y, 
//    				new Color(255,255,0));    		
//    	}
    	
    	rendered = true;
    }
    
    protected void drawIPPartitions(GL gl) {
    	float partition2 = ip_off_normal * ((float)CenterView.height / 9.1f) * 2;
    	float partition3 = ip_off_normal * ((float)CenterView.height / 9.1f) * 3f;
    	float partition5 = ip_off_normal * ((float)CenterView.height / 9.1f) * 5f;
    	float partition10 = ip_off_normal * ((float)CenterView.height / 9.1f) * 10f;
    	float partition15 = ip_off_normal * ((float)CenterView.height / 9.1f) * 15f;
    	float partition20 = ip_off_normal * ((float)CenterView.height / 9.1f) * 20f;
    	
    	float partition = 0f;
    	
    	if(camera.scale_y < 1.3f) {
    		partition = partition20;
    	} else if(camera.scale_y >= 1.3f && camera.scale_y < 1.8f) {
    		partition = partition15;
    	} else if(camera.scale_y >= 1.8f && camera.scale_y < 2.6f) {
    		partition = partition10;
    	} else if(camera.scale_y >= 2.6f && camera.scale_y < 5.12f) {
    		partition = partition5;
    	} else if(camera.scale_y >= 5.12f && camera.scale_y < 9.4f) {
    		partition = partition3;
    	} else {
    		partition = partition2;
    		if(camera.scale_y > 10f) {
    			camera.scale_y = 10f;
    			camera.scale_x = 10f;
    		}
    	}
    	
    	for(int x = 0; x < 20; x++) {
    		gl.glColor3f(0.5f,0.5f,0.5f);
    		gl.glBegin(GL.GL_LINES);
    		if(x == 0) {
//    			gl.glVertex3f(0, 1f, 1.0f);
//    			gl.glVertex3f(((CenterView.width * 2) * camera.scale_x), 1f, 1.0f);
    		} else {
    			gl.glVertex3f(0, (partition * x), 1.0f);
    			gl.glVertex3f(((CenterView.width * 2) * camera.scale_x), (partition * x), 1.0f);
    		}
    		gl.glEnd();
    	}
    	
//    	gl.glBegin(GL.GL_LINES);
//			gl.glVertex3f(0, CenterView.height, 1.0f);
//			gl.glVertex3f(((CenterView.width * 2) * camera.scale_x), CenterView.height, 1.0f);
//		gl.glEnd();
    }
    
    protected void drawIPPartitionMarkers(GL gl) {
    	float partition2 = ip_off_normal * ((float)CenterView.height / 9.1f) * 2;
    	float partition3 = ip_off_normal * ((float)CenterView.height / 9.1f) * 3f;
    	float partition5 = ip_off_normal * ((float)CenterView.height / 9.1f) * 5f;
    	float partition10 = ip_off_normal * ((float)CenterView.height / 9.1f) * 10f;
    	float partition15 = ip_off_normal * ((float)CenterView.height / 9.1f) * 15f;
    	float partition20 = ip_off_normal * ((float)CenterView.height / 9.1f) * 20f;
    	
    	float partition = 0f;
    	int increment = 0;
    	
    	if(camera.scale_y < 1.3f) {
    		partition = partition20;
    		increment = 20;
    	} else if(camera.scale_y >= 1.3f && camera.scale_y < 1.8f) {
    		partition = partition15;
    		increment = 15;
    	} else if(camera.scale_y >= 1.8f && camera.scale_y < 2.6f) {
    		partition = partition10;
    		increment = 10;
    	} else if(camera.scale_y >= 2.6f && camera.scale_y < 5.12f) {
    		partition = partition5;
    		increment = 5;
    	} else if(camera.scale_y >= 5.12f && camera.scale_y < 9.4f) {
    		partition = partition3;
    		increment = 3;
    	} else {
    		partition = partition2;
    		increment = 2;
    		if(camera.scale_y > 10f) {
    			camera.scale_y = 10f;
    			camera.scale_x = 10f;
    		}
    	}
    	
    	long ip = renderer.ip2int(StartIP);
    	for(int x = 0; x < 20; x++) {
    		//ip += increment;//x * (increment + 1);
    		String ips = renderer.int2ip(ip);
    		gl.glColor3f(0.5f,0.5f,0.5f);
    		gl.glRasterPos2f((float)20f,(float)partition * (float)x);
            // TODO FONT
            glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, ips);
            ip += increment;
    	}
    }
    
    protected void drawSubnets(GL gl) {
        /** GREG, I have subtracted 12, this is added back in to the top */
    	float sub_off = ((SubnetView.height - 12f) * 0.6f - sub_pad_top) / (float)4228250625l;
    	//System.out.println((int)sub_off);
    	
    	// reset the destinaltion list to hold the ips with destination
//    	DestList = new LinkedList();
    	LinkedList tempList = new LinkedList();
    	tempList.addAll(DestList);
    	DestList = new LinkedList();
    	
        /** Added by Greg; This will organize things for collision detection */
        TreeSet tree = new TreeSet();
        /** Added by Greg; This will get me a count of the diffent IP's */
        TreeSet counter = new TreeSet();
        
    	Iterator it = tempList.iterator();
    	while(it.hasNext()) {
    		ZoomedAlarm alarm = (ZoomedAlarm)it.next();
    		String subS = alarm.alarm.getVictimIP();
    		//CHANGED by Kulsoom added if statement
    		if(alarm.alarm.ipoff2){  //if(renderer.ipOffset(renderer.ip2int(subS))>0.0){//is this local ip?
    			//then we need to print the other IP on the right
    			subS=alarm.alarm.getBadIP();
    		}
    		// check whether this is necessary
    		if(subS.equals("0")) {
    			// do nothing
    		} else {
    			// make sure the alarm is contained within the view
    			float colision_x = alarm.x; // * camera.scale_x + camera.translate_x;
//    			float colision_x = (alarm.x * camera.scale_x) + 
//						CenterView.x + 
//							(camera.translate_x * ((1.007f)
//										// (camera.translate_x * ((camera.panel_width / camera.panel_height)
//										//(camera.translate_x * ((CenterView.width / CenterView.height)
//									* camera.scale_x));
    			float colision_y = alarm.y * camera.scale_y + camera.translate_y;
    			
    			/** CHANGE (IPView.width - 8) */
    			if(colision_x >= (IPView.width - 8) && colision_x <= (CenterView.width + IPView.width)) {
    				if(colision_y >= 0 && colision_y <= CenterView.height) {	
    					long ip = renderer.ip2int(subS);
                        /** I have added 12 to the final value. GREG */
    	    			float ypos = ip * sub_off + sub_pad_top + 12f;
                                // No longer necessary, Greg
//    	    			gl.glColor3f(0.5f, 0.5f, 0.5f);
    	    			
                                /* Added by Greg to hold y possition */
                                alarm.y_adjusted = ypos;
                                
                                /* Added by Greg to hold subs */
                                alarm.subs = subS;
                                
                                /* Added by Greg for comparison */
                                alarm.ip = this.ip2int(subS);
                                
                                /* This holds all alarms for drawing, Greg */
                                tree.add(alarm);
                                
                                /* THis holds one copy of each victim, Greg */
                                counter.add(alarm.subs);
                                
//                                System.out.println(" " + subS + " : " + alarm.y_adjusted + " ");
                                
                                // no longer necessary, Greg
//    	    	        gl.glRasterPos2f(5f, ypos);
//    	    	        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, subS);
    	    	        //System.out.println("X: " + colision_x + " Y: " + colision_y);
                                
 
    	    	        //System.out.println("X: " + colision_x + " Y: " + colision_y);
    	    	        DestList.add(alarm);
        			}
    			}
    		}
    	}
        /** Added by Greg to process colision and separation.
         */
       // Space out evenly if there are too many
       if(counter.size() < 1) { /* If there are zero entries */
           // do nothing
       } else if((counter.size() * 15) > (int)SubnetView.height) { /* If there are too many to successfully separate */
           float offset = (SubnetView.height - 12f) / (float)counter.size();
           sub_off = offset;
           
           Iterator i = tree.iterator();
           ZoomedAlarm al1 = (ZoomedAlarm)i.next();
           int cc = 0;
           al1.y_adjusted = cc * sub_off + 12f;
           this.drawSubnetIndividuals(gl, al1);
           ZoomedAlarm al2;
           while(i.hasNext()) {
               al2 = (ZoomedAlarm)i.next();
               if(al1.subs.equals(al2.subs)) {
                   al2.y_adjusted = al1.y_adjusted;
               } else {
                   cc++;
                   al2.y_adjusted = cc * sub_off + 12f;
               }
               this.drawSubnetIndividuals(gl, al2);
               al1 = al2;
           }
       } else if(tree.size() == 1) { /* If the tree only has one entry */
           Iterator i = tree.iterator();
           this.drawSubnetIndividuals(gl, (ZoomedAlarm)i.next());
       } else if(tree.size() == 2) { /* If the tree only has two entries */
//           System.out.println("SIZE2");
           Iterator i = tree.iterator();
           ZoomedAlarm al1 = (ZoomedAlarm)i.next();
           ZoomedAlarm al2 = (ZoomedAlarm)i.next();
           if(al1.subs.equals(al2.subs)) {
               
           } else if(al1.y_adjusted + 10f > al2.y_adjusted) {
               al1.y_adjusted = al2.y_adjusted - 10f;
           }
           this.drawSubnetIndividuals(gl, al1);
           this.drawSubnetIndividuals(gl, al2);
       } else { /* For regular colision detction and drawing */
           Iterator i = tree.iterator();
           ZoomedAlarm al1 = (ZoomedAlarm)i.next();
           ZoomedAlarm al2 = (ZoomedAlarm)i.next();
           this.drawSubnetIndividuals(gl, al1);
           while(i.hasNext()) {
               ZoomedAlarm al3 = (ZoomedAlarm)i.next();
               
               // if it is the same IP
               if(al1.subs.equals(al2.subs)) {
//                   System.out.println("SAME");
                   al2.y_adjusted = al1.y_adjusted;
               } else {
                   if(al2.y_adjusted + 10 > al3.y_adjusted) { // move a2 above a3
                       al2.y_adjusted = al3.y_adjusted - 10f;
                   }
                   if(al1.y_adjusted + 10f > al2.y_adjusted) { // move a2 below a1
                       al2.y_adjusted = al1.y_adjusted + 10f;
                   } else {
                       // leave it be
                   }   
               }
               
               this.drawSubnetIndividuals(gl, al2);
               
               al1 = al2;
               al2 = al3;
           }
           if(al1.subs.equals(al2.subs)) {
//                   System.out.println("SAME");
                   al2.y_adjusted = al1.y_adjusted;
           } else {
               if(al1.y_adjusted + 10f > al2.y_adjusted) { // move a2 below a1
                   al2.y_adjusted = al1.y_adjusted + 10f;
               }
           }
           this.drawSubnetIndividuals(gl, al2); // this is actually al3
       }
       /** End of Major Addition */
       
    	if(alarm_colision_list.size() > 0) {
    		ZoomedAlarm al = (ZoomedAlarm)alarm_colision_list.get(current_colision);
    		if(al.line == true) {
    			if(al.screen_line.x2 > 1.0f) {
		    		String subS = al.alarm.getVictimIP();
		    		//CHANGED by Kulsoom added if statement
		    		if(al.alarm.ipoff2){ //if(renderer.ipOffset(renderer.ip2int(subS))>0){//is this local ip?
		    			//then we need to print the other IP
		    			subS=al.alarm.getBadIP();
		    		}
		    		
		    		long ip = renderer.ip2int(subS);
					//float ypos = ip * sub_off + sub_pad_top;
                    float ypos = al.y_adjusted; // Change by Greg
                    
            // added by Greg to hold y possition 
            //al.y_adjusted = ypos;
            
            // draw a background
            gl.glColor3f(0f,0f,0f);
            gl.glBegin(GL.GL_QUADS);
                gl.glVertex2f(0, SubnetView.y + al.y_adjusted + 2f);
                gl.glVertex2f(0, SubnetView.y + al.y_adjusted - 10f);
                gl.glVertex2f(SubnetView.width, al.y_adjusted - 10f);
                gl.glVertex2f(SubnetView.width, al.y_adjusted + 2f);
            gl.glEnd();
                    
gl.glColor3f(0.8f, 0.8f, 0.0f);
gl.glRasterPos2f(5f, ypos);
glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, subS);
}
}
}
}
    
    /** Added by Greg to draw individual Victim IP's */
    protected void drawSubnetIndividuals(GL gl, ZoomedAlarm al) {
        gl.glColor3f(0.5f, 0.5f, 0.5f);
        gl.glRasterPos2f(5f, al.y_adjusted);
        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, al.subs);
    }
    
    protected void drawSubnetDirection(GL gl) {
    	float sub_off = (SubnetView.height - sub_pad_top) / (float)4228250625l;
    	Iterator it = DestList.iterator();
    	
    	// use this to check highliteing
    	ZoomedAlarm temp = new ZoomedAlarm();
    	if(alarm_colision_list.size() > 0) {
    		temp = (ZoomedAlarm)alarm_colision_list.get(current_colision);
    	}
        
    	
    	while(it.hasNext()) {
    		ZoomedAlarm alarm = (ZoomedAlarm)it.next();
//    		float center_x = (alarm.x * camera.scale_x) + 
//    				CenterView.x + 
//    				(camera.translate_x * ((1.007f)
//    				// (camera.translate_x * ((camera.panel_width / camera.panel_height)
//    				//(camera.translate_x * ((CenterView.width / CenterView.height)
//    						* camera.scale_x));
    		float center_y = alarm.y * camera.scale_y + ChronoView.height;// + CenterView.y;
    		//System.out.println(camera.scale_x);
    		float center_x = alarm.x;// + CenterView.x;
    		// float center_y = alarm.y;// + ChronoView.height;
    		long ip = renderer.ip2int(alarm.alarm.getVictimIP());
    		//CHANGE by Kulsoom, added if statement
    		if(alarm.alarm.ipoff2){ //if(renderer.ipOffset(ip)>0.0){//is this local ip?
    			//then we need to join line to correct spot on right
    			//System.out.println("in 1st if");
    			ip = renderer.ip2int(alarm.alarm.getBadIP());
    		}
    		
    		//	float ypos = ip * sub_off + sub_pad_top;
            float ypos = alarm.y_adjusted; // Change by Greg
    		float sub_x = IPView.width + CenterView.width + 5;
    		float sub_y = ChronoView.height + ypos;
    		
    		//add to line
    		alarm.screen_line.x2 = sub_x;
    		alarm.screen_line.y2 = sub_y;
    		
    		// TODO // Stronger Comparisons
    	//if(alarm.isSame(temp)) {
    			//System.out.println("Colision");
//    			gl.glColor3f(1.0f, 1.0f, 1.0f);
//    			gl.glBegin(GL.GL_LINES);
//    				gl.glVertex3f(center_x, center_y, 1.0f);
//    				gl.glVertex3f(sub_x, sub_y, 1.0f);
//    			gl.glEnd();
    	//	} else {
//    			gl.glColor3f(0.5f,0.5f,0f);
//	    		gl.glBegin(GL.GL_LINES);
//	    			gl.glVertex3f(center_x, center_y, 1.0f);
//	    			gl.glVertex3f(sub_x, sub_y, 1.0f);
//	    		gl.glEnd();
    			
    			// if both booleans in alarm are true, draw reverse
    			//if victim aka attacker is local, then reverse arrow--kulsoom
    			if(alarm.alarm.ipoff2) {//if(alarm.alarm.ipoff1 == true && alarm.alarm.ipoff2 == true) {
    				dLine.drawArrowReverse(gl, center_x, center_y, sub_x, sub_y, alarm.screen_projection.width, false);
    			} else {
    				dLine.drawArrow(gl, center_x, center_y, sub_x, sub_y, alarm.screen_projection.width, false);
    			}
    				
    		//}
    	}
    	
    }
    
    protected void drawClock(GL gl) {
    	float translation = x_center_increment * camera.translate_x;
    	int num_listed = 0;
    	
    	if(camera.scale_y < 1.3f) {
    		num_listed = 8;
    	} else if(camera.scale_x >= 1.3f && camera.scale_x < 1.8f) {
    		num_listed = 12;
    	} else {
    		num_listed = 24;
    	} 
    	
//    	System.out.println("ScreenIncrement: " + x_center_increment);
    	
    	float partition = ChronoView.width / num_listed;
    	float hour_increment = x_center_increment * partition;
    	
    	int time_increment = 24 / num_listed;
    	int time = 0;
    	//hour_increment *= time_increment;
    	
    	for(int x = 0; x < num_listed + 1; x++) {
    		time = x * time_increment;
    		String tm = String.valueOf(time) + ":00";
    		
	    	gl.glColor3f(0.5f, 0.5f, 0.5f);
	        gl.glRasterPos2f((float)hour_increment * x + translation,(float)ChronoView.height);
	        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, tm);
    	}
    }
    
    public void drawClockPartitions(GL gl) {
    	float translation = x_center_increment * camera.translate_x;
		int num_listed = 0;
		    	
		if(camera.scale_y < 1.3f) {
			num_listed = 8;
		} else if(camera.scale_x >= 1.3f && camera.scale_x < 1.8f) {
			num_listed = 12;
		} else {
			num_listed = 24;
		} 
    	
//    	System.out.println("ScreenIncrement: " + x_center_increment);
    	
    	//float partition = ChronoView.width / num_listed;
		float partition = ChronoView.width / 24;
    	float hour_increment = /*x_center_increment */ partition;
    	
    	int time_increment = 24 / num_listed;
    	int time = 0;
    	//hour_increment *= time_increment;
    	
//    	for(int x = 0; x < num_listed + 1; x++) {
//    		time = x * time_increment;
//    		float x_x = (float)hour_increment * (float)x;
//	    	gl.glColor3f(0.5f, 0.5f, 0.5f);
//	    	gl.glBegin(GL.GL_LINES);
//	    		gl.glVertex2f(x_x, 0f);
//	    		gl.glVertex2f(x_x, CenterView.height);
//	    	gl.glEnd();
//    	}
    	
    	int every_ = 24 / num_listed;
    	
    	// lets make every x line bright, and draw all 24
    	for(int x = 0; x < 24 + 1; x++) {
    		
    		float x_x = (float)hour_increment * (float)x;
    		gl.glColor3f(0.2f, 0.2f, 0.2f);
    		if(x != 0) {
    			//System.out.println(every_ + " " + (x % every_));
	    		if((x % every_) == 0) {
	    			gl.glColor3f(0.5f, 0.5f, 0.5f);
	    		} 
    		}
	    	gl.glBegin(GL.GL_LINES);
	    		gl.glVertex2f(x_x, 0f);
	    		gl.glVertex2f(x_x, CenterView.height);
	    	gl.glEnd();
    	}
    }
    
    /** Subnet is drawn in it's own function */
    protected void drawColision(GL gl) {
    	//draw alarm
    	if(alarm_colision_list.size() > 0) {
    		ZoomedAlarm al = (ZoomedAlarm)alarm_colision_list.get(current_colision);
//			drawADot(gl,
//				al.screen_line.x1, 
//				al.screen_line.y1, 
//				new Color(255,255,0));
    		float x = al.screen_projection.x;
    		float y = al.screen_projection.y;
    		float w = al.screen_projection.width;
    		float h = al.screen_projection.height;
    		gl.glColor3f((float)al.color.getRed(), 
    				(float)al.color.getGreen(),
    				(float)al.color.getBlue());
    		gl.glBegin(GL.GL_QUADS);
				gl.glVertex2f(x,y);
				gl.glVertex2f(x, y+h);
				gl.glVertex2f(x+w, y+h);
				gl.glVertex2f(x+w, y);
			gl.glEnd();
    		gl.glColor3f(1.0f, 1.0f, 1.0f);
    		gl.glBegin(GL.GL_LINE_LOOP);
    			gl.glVertex2f(x,y);
    			gl.glVertex2f(x, y+h);
    			gl.glVertex2f(x+w, y+h);
    			gl.glVertex2f(x+w, y);
    		gl.glEnd();
			
			// draw line
			if(al.line == true) {
				if(al.screen_line.x2 > 1.0f) {
//					gl.glColor3f(1.0f, 0.8f, 0.0f);
//					gl.glBegin(GL.GL_LINES);
//						gl.glVertex2f(al.screen_line.x1, al.screen_line.y1);
//						gl.glVertex2f(al.screen_line.x2, al.screen_line.y2);
//					gl.glEnd();
					//dLine.drawArrow(gl, al.screen_line.x1, al.screen_line.y1, al.screen_line.x2, al.screen_line.y2, al.screen_projection.width, true);
					
//					// if both booleans in alarm are true, draw reverse
					if(al.alarm.ipoff2) {//if(al.alarm.ipoff1 == true && al.alarm.ipoff2 == true) {
	    				dLine.drawArrowReverse(gl, al.screen_line.x1, al.screen_line.y1, al.screen_line.x2, al.screen_line.y2, al.screen_projection.width, true);
	    			} else {
	    				dLine.drawArrow(gl, al.screen_line.x1, al.screen_line.y1, al.screen_line.x2, al.screen_line.y2, al.screen_projection.width, true);
	    			}
				}
			}
			
			// draw how many are in colision
			//String numS = String.valueOf(alarm_colision_list.size());
//			String numS = String.valueOf(current_colision + 1);
//			gl.glColor3f(1.0f, 1.0f, 1.0f);
//			gl.glRasterPos2f((float)mouse_x + 15,(float)mouse_y + 15);
//	        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, numS);
	        
//	        infoBox.detail = al.alarm.getDetail();
//	        infoBox.type = al.alarm.getType();
//	        infoBox.occured = al.alarm.getTimestamp();
//	        infoBox.source = al.alarm.getBadIP();
//	        infoBox.destination = al.alarm.getVictimIP();
	        
	        // this will replace the above
	        infoBox.alarm = al.alarm;
	        
	        // TODO // Draw Info Box
	        if(al.line == true) {
				if(al.screen_line.x2 > 1.0f) {
					//infoBox.draw(gl, glut, mouse_x, mouse_y, current_colision);
					infoBox.drawInfo(gl, glut, mouse_x, mouse_y, current_colision);
				}
	        } else {
	        	//5infoBox.draw(gl, glut, mouse_x, mouse_y, current_colision);
	        	infoBox.drawInfo(gl, glut, mouse_x, mouse_y, current_colision);
	        }
			
    	}
    }
    
    /** Setup the proper variables for highlighting colision, 
     * this helps keep track of the mouse parsing through
     * colisions */
    protected void PreDrawColision() {
    	int number = alarm_colision_list.size();
    	if(colision_number != number) {
    		colision_number = number;
    		current_colision = number - 1;
    	}
    	if(number > 0) {
    		
    	} else {
    		colision_number = 0;
    	}
    }

    
    
    /** Converts ip (as a dotted string),returns it as a 32bit integer
     */
    
    public static long ip2int(String ipAddress){
        long ipAddressLong = 0;
        StringTokenizer stringTokenizer = new StringTokenizer(ipAddress,".");
        while(stringTokenizer.hasMoreTokens()){
            long tempLong = 0;
            try{
                tempLong = Long.parseLong(stringTokenizer.nextToken());
                if (tempLong < 0 || tempLong > 255) return 0;
            }catch(Exception e){					   return 0;  }
            ipAddressLong *= 256;
            ipAddressLong += tempLong;
        }
        
        return ipAddressLong;
    }
    
    
    /** Converts 32bit integer and return ip address as string
     */
    
    public static String int2ip(long ipValue){//correct
        StringBuffer stringBuffer = new StringBuffer(Long.toBinaryString(ipValue));
        StringBuffer stringBufferReturn = new StringBuffer();
        
        if (stringBuffer.length() > 32) return "";
        while (stringBuffer.length() < 32)
            stringBuffer.insert(0,"0");
        
        stringBufferReturn.append(Integer.parseInt(stringBuffer.substring(0,8), 2));
        stringBufferReturn.append(".");
        stringBufferReturn.append(Integer.parseInt(stringBuffer.substring(8,16), 2));
        stringBufferReturn.append(".");
        stringBufferReturn.append(Integer.parseInt(stringBuffer.substring(16,24), 2));
        stringBufferReturn.append(".");
        stringBufferReturn.append(Integer.parseInt(stringBuffer.substring(24,32), 2));
        
        return stringBufferReturn.toString();
    }
    
/*
 * This calculates an offset of a given ip,0-1 of all ips
 * normalized acrossed all columns
 */
    public float ipOffset(long ip){
        
        float off=0;
        int total=0;
        long sub=0;
        long mask=0;
        
        for (int i=0; i<subnetint.length;i++){
            sub=subnetint[i];
            mask=subnetmask[i];
            if((ip&mask)==sub){
                off=total+(ip&((int)~mask));
                //System.out.println("if"+off+"off="+off/totalips);
                
                return(off/totalips);
            }
            total+=(~mask)+1;
            
        }
        return 0;
    }
    
    public long offset2ip(float offSet){//make this diff from global offset int var
        
        //long offset=0;
        int total=0;
        long sub=0;
        long mask=0;
        long pretotal=0;
        int span=0;
        float suboff=0;
        
        if(offSet>1 || offSet<0)
            return 0;
        for(int i=0; i<subnetint.length;i++){
            sub=subnetint[i];
            mask=subnetmask[i];
            //Long.toBinaryString(mask);
            //System.out.println("mask invert:"+(~mask));
            pretotal=total;
            span= (int)(~ mask) + 1; //span=(~mask)+1;
            //System.out.println("span:"+(span));
            //span=Integer.parseInt(Integer.toBinaryString((int)span), 2);
            total=span+total;
            //System.out.println("total:"+(total));
            if( (float)total / totalips /*.3994*/ > offSet ) {
                float temp=(float)totalips/span;
                float temp1=(float)pretotal/totalips;
                
                suboff = (float)((float)( offSet - temp1 ) * temp);
                //System.out.println("pretpta;"+pretotal+"span"+span+" suboff"+suboff+"sub"+sub+"return "+(long)(sub + (int)( suboff  * span ))  );
                return((long)(sub + (int)( suboff  * span )));
            }
        }
        
        return( 0 );
    }
    
/*
 *this calculates the percent of the day the time is.
 *0 = midnight, 0.5 = noon, 0.999 = 11:59
 */
    private float percentOfDay(String timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timestamp)*1000); //convert secs to millis
        int year = cal.get(Calendar.YEAR);//2005
        int month = cal.get(Calendar.MONTH);//9         MONTH goes from 0-11
        int day = cal.get(Calendar.DAY_OF_MONTH);//12
        int hour = cal.get(Calendar.HOUR_OF_DAY);
//System.out.print(day+" day"+"month"+month+" year"+year);
//System.out.print(" "+timestamp);
        if(month==(9-1) && day==12) {
            this.startDay=1;
            this.endDay=0;
//System.out.println(hour+"hour");
        }
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int offset=sec + ((hour*60)+min)*60;
        if(month==(9-1) && day==13){
            this.endDay=1;
            this.startDay=0;
//System.out.println(hour+","+min+","+sec+","+((float)offset)/(24*60*60));
        }
//System.out.println(hour+","+min+","+sec+","+((float)offset)/(24*60*60));
        float percent = (float)offset/86400;//div by #sec in 1 day
return percent;
        //if(this.startDay==1 & this.endDay==0) return percent;
        //else return -1;
    }
    
    public static Color alarmColor(String colorIndex) throws Exception{
        Color color = Color.PINK;
        int integer = Integer.parseInt(colorIndex);
        if (integer == 35 || integer == 31|| integer ==  18||integer ==16||integer ==9||integer ==7)
            color = Color.GREEN;
        else if (integer == 28|| integer ==24|| integer ==19|| integer ==13|| integer ==6|| integer ==5)
            color = Color.YELLOW;
        else if (integer == 32|| integer ==20|| integer ==17|| integer ==12|| integer ==11|| integer ==4|| integer ==2)
            color = Color.RED;
        else
            color = Color.MAGENTA;
        return color;
    }
    
//    public void zoomIn(float x, float y, float width, float height) {
//        float pos[] = this.colAlignXY();
//        
//        //camera.zoomIn(x, y, width, height);
//       
//        gldrawable.display();//redraws canvas
//    }
    
    public void mouseDragged(MouseEvent e) {
        
    }//2b used for future panning
    
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        
        // reset the colided alarm
//        alarm_colision = null;
        
        // replacing the above
        alarm_colision_list = new LinkedList();
        
        
        
        Rectangle2D.Float rect = new Rectangle2D.Float((float) mouse_x - 1,
        		(float) mouse_y - 1,// - ChronoView.height,
    			3f,
    			3f);
        // If intersecting with Alarms, dont check lines
        boolean alarms = false;
        // check collision
        Iterator it = ConvertList.iterator();
        //System.out.println(ZoomList.size());
        while(it.hasNext()) {
        	ZoomedAlarm alarm = (ZoomedAlarm)it.next();
        	if(alarm.screen_line.x1 == 0) {
        		
        	} else {
        		//System.out.println(mouse_y);
        		//System.out.println(alarm.screen_line.y1);
        		if(alarm.screen_projection.contains(rect) 
        				|| alarm.screen_projection.intersects(rect)) {
        			if(alarms = false) {
        				alarms = true;
        				alarm_colision_list = new LinkedList();
        			}
        			// System.out.println("Colision");
        			alarm_colision_list.add(alarm);
        		} else if (alarm.line == true) {
        			if(alarm.screen_line.intersects(rect) && alarms == false) {
        		
		        		// System.out.println(alarm.alarm.getVictimIP());
		        		// alarm_colision = alarm;
		        		alarm_colision_list.add(alarm);
        			}
	        	}
        	}
        }
        
        gldrawable.display();//redraws canvas
/*
 
 */
        
    }
    
    public void mouseClicked(MouseEvent e) {
//        iterate the current colisions
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(current_colision != 0) {
            	current_colision--;
            } else {
            	current_colision = colision_number -1;
            }
        } else {
        	if(current_colision >= (colision_number - 1)) {
        		current_colision = 0;
        	} else {
        		current_colision++;
        	}
        }
        
        gldrawable.display();//redraws canvas
    }
    
    public void mousePressed(MouseEvent e) {
        
    }
    
    public void mouseReleased(MouseEvent e) {
        
    }
    
    public void mouseEntered(MouseEvent e) {
        
    }
    
    public void mouseExited(MouseEvent e) {
        
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
//        if(camera.zoom > 0) {
//            camera.Zoom(e.getUnitsToScroll());
        	camera.zoom(e.getUnitsToScroll());
        	
            gldrawable.display();//redraws canvas
//        }
//        System.out.println(camera.scale_y_velocity);
        
    }
    
    public void componentHidden(ComponentEvent e) {
        
    }
    
    public void componentMoved(ComponentEvent e) {
        camera.panel_width = e.getComponent().getWidth();
        camera.panel_height = e.getComponent().getHeight();
    }
    
    public void componentResized(ComponentEvent e) {
        camera.panel_width = e.getComponent().getWidth();
        camera.panel_height = e.getComponent().getHeight();
    }
    
    public void componentShown(ComponentEvent e) {
        camera.panel_width = e.getComponent().getWidth();
        camera.panel_height = e.getComponent().getHeight();
    }
    
    public void keyTyped(KeyEvent e) {
        
    }
    
    public void keyPressed(KeyEvent e) {
//        if(e.getKeyCode() == KeyEvent.VK_UP) {
//            camera.keyPressedY = 1;
//        } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
//            camera.keyPressedY = -1;
//        }
    	//for screenshots
    	if(e.getKeyCode() == KeyEvent.VK_6){
    		capture = true;
         gldrawable.display();//redraws canvas
         System.out.println("hit s");
    	}
    	if(e.getKeyCode() == KeyEvent.VK_UP) {
    		RedBox.y-=1f;
    		renderer.MoveRedBox(RedBox.x, RedBox.y);
    	} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
    		RedBox.y+=2f;
    		renderer.MoveRedBox(RedBox.x, RedBox.y);
    	}
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            camera.keyPressedX = 1;
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            camera.keyPressedX = -1;
        }
        camera.update();
        gldrawable.display();//redraws canvas
    }
    
    public void keyReleased(KeyEvent e) {
//        if(e.getKeyCode() == KeyEvent.VK_UP) {
//            if(camera.keyPressedY > 0) {
//                camera.keyPressedY = 0;
//            }
//        } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
//            if(camera.keyPressedY < 0) {
//                camera.keyPressedY = 0;
//            }
//        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
        	if(camera.keyPressedX > 0) {
        		camera.keyPressedX = 0;
        	}
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
        	if(camera.keyPressedX < 0) {
        		camera.keyPressedX = 0;
        	}
        }
        camera.update();
        gldrawable.display();//redraws canvas
    }
    
//    public void drawCircle(GL gl, float x, float y, float radius) {
//    	int smooth = 6;
//    	
//    	float pi2 = (float)Math.PI * 2f;
//    	float angle = pi2 / (float)smooth;
//    	
//    	gl.glBegin(GL.GL_LINE_LOOP);
//    	
//    	for (float pheta = 1.1f; pheta < pi2; pheta += angle) {
//    		float xx = radius / 2 * (float)Math.cos((double)pheta) + x;
//    		float yy = radius / 2 * (float)Math.sin((double)pheta) + y;
//    		gl.glVertex3f(xx,yy, 1.0f);
//    	}
//    	
//    	gl.glEnd();
//    }
    
    public float[] Project(float a, float b) {
    	int x = 0;
	        int y = 0;
	        int viewport[] = new int[4];
	        double modelview[] = new double[16];
	        double projection[] = new double[16];
	        float winX = 0;
	        float winY = 0;
	        float winZ[] = new float[1];
	        double posX[] = new double[1];
	        double posY[] = new double[1];
	        double posZ[] = new double[1];
			
			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
	        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);
	        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
	        
	        winX = a;
	        winY = b; //(float)viewport[3] - (float)b;
	        // gl.glReadPixels((int)a, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
	        
	        glu.gluProject((double)winX, (double)winY, 1.0/*(double)winZ[0]*/, modelview, projection, viewport, posX, posY, posZ);
	        
	        float xf[] = {(float)posX[0], (float)posY[0]};
	        return xf;
    }
    
    public float[] UnProject(float a, float b) {
    	int x = 0;
	        int y = 0;
	        int viewport[] = new int[4];
	        double modelview[] = new double[16];
	        double projection[] = new double[16];
	        float winX = 0;
	        float winY = 0;
	        float winZ[] = new float[1];
	        double posX[] = new double[1];
	        double posY[] = new double[1];
	        double posZ[] = new double[1];
			
			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
	        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);
	        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
	        
	        winX = a;
	        winY = b; //(float)viewport[3] - (float)b;
	        gl.glReadPixels((int)a, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
	        
	        glu.gluUnProject((double)winX, (double)winY, 1.0/*(double)winZ[0]*/, modelview, projection, viewport, posX, posY, posZ);
	        
	        float xf[] = {(float)posX[0], (float)posY[0]};
	        return xf;
    }
    
    public synchronized void ResetZoomedAlarm(Renderer r) {
    	this.renderer = r;
    	
    	if(rendered == true) {
    		ZoomList = new LinkedList();
    	}
    	rendered = false;
    }
    
    public synchronized void AddZoomedAlarm(Alarm a, 
    		Rectangle2D.Float rect,
    		Color c,
    		float x, 
    		float y,
    		String StartIP) {
    	ZoomedAlarm alarm = new ZoomedAlarm();
    	//float ax = (x - rect.x) * ((float)camera.panel_width / 72);
    	//float ay = (y - rect.y) * ((float)camera.panel_height / 9);
    	float ax = (x - rect.x) * ((float)CenterView.width / 72f);
    	float ay = (y - rect.y) * ((float)CenterView.height / 9.1f);
    	//    	System.out.println(ax);
    	alarm.x = ax;
    	alarm.y = ay;
    	alarm.alarm = a;
    	alarm.color = c;
    	ZoomList.add(alarm);
    	this.RedBox = rect;
    	
//    	System.out.println(StartIP);
    	this.StartIP = StartIP;
    	
    	ip_off_normal = renderer.ipOffset();
    	//.out.println(renderer.ipOffset());
    }
    
    public void initValues() {
    	try {
    		ip_off_normal = renderer.ipOffset();
    	} catch (NullPointerException e) {
    		System.out.println(e);
    	}
    }
    
    public void update() {
//    	try {
//    		ip_off_normal = renderer.ipOffset();
//    	} catch (NullPointerException e) {
//    		System.out.println(e);
//    	}
    	
    	gldrawable.display();
    }
    
    /*
     * Copy the frame buffer into the BufferedImage.  The data needs to
     * be flipped top to bottom because the origin is the lower left in
     * OpenGL, but is the upper right in Java's BufferedImage format.
     */
    private void captureImage( GLDrawable d )
    {
    GL gl = d.getGL();
    int width = d.getSize().width;
    int height = d.getSize().height;

//     Allocate a buffer for the pixels
    ByteBuffer rgbData = ByteBuffer.allocate(width * height * 3);

//     Set up the OpenGL state.
    gl.glReadBuffer(GL.GL_FRONT);
    gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);

//     Read the pixels into the ByteBuffer
    gl.glReadPixels(0,
    0,
    width,
    height,
    GL.GL_RGB, 
    GL.GL_UNSIGNED_BYTE,  
    rgbData);              

//     Allocate space for the converted pixels
    int[] pixelInts = new int[width * height];

//     Convert RGB bytes to ARGB ints with no transparency. Flip 
//     image vertically by reading the rows of pixels in the byte 
//     buffer in reverse - (0,0) is at bottom left in OpenGL.

    int p = width * height * 3; // Points to first byte (red) in each row.
    int q;                  // Index into ByteBuffer
    int i = 0;                 // Index into target int[]
    int bytesPerRow = width*3; // Number of bytes in each row

    for (int row = height - 1; row >= 0; row--) {
    p = row * bytesPerRow;
    q = p;
    for (int col = 0; col < width; col++) {
    int iR = rgbData.get(q++);
    int iG = rgbData.get(q++);
    int iB = rgbData.get(q++);

    pixelInts[i++] = ( (0xFF000000) 
    | ((iR & 0xFF) << 16) 
    | ((iG & 0xFF) << 8)
    | (iB & 0xFF) );
    }
    }

//     Set the data for the BufferedImage
    image.setRGB(0, 0, width, height, pixelInts, 0, width);
    }

    
}

