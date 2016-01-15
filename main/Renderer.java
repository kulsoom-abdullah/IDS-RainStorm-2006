
import java.awt.Color;
//import java.awt.Component;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

//import javax.swing.JFrame;
//import java.
import net.java.games.jogl.GLEventListener;
import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLU;
import net.java.games.jogl.util.GLUT;

public class Renderer implements GLEventListener,MouseMotionListener,
        MouseListener,MouseWheelListener, ComponentListener, KeyListener, ItemListener, ActionListener  {
    
    /** The other Window */
    public SecondView second;
    
    /** The red box, we will use this due to its colision detection */
    Rectangle2D.Float RedBox = new Rectangle2D.Float(0,0,72,9);

    
//    /** The normalized offset of 1f,1f */
//    float normal_offset[] = new float[2];
//    
//    /** The normalized mouse */
//    float mouse_normal[] = new float[2];
    
    /** For drawing and resetting a list */
    boolean dragged = false;
    
    /** Boolean added to catch filter changes */
    boolean changed = false;
    
    public static String[] selectedAlarms;
    
    /** For redrawing alarms */
    boolean reshape = false;
    
    /**For filtering alarms**/
   // FilterAlarms filter;
    
    /** For glGenLists */
    int gen_list = 0;
    
    //put ip array strings here
    
    int width = 616;//(72*8)+(2*10);
    int height = 808;//808;//(256*3)+(2*10);
    int margin_top    = 10;
    int margin_bottom = 10;
    int margin_left   = 10;
    int margin_right  = 10;
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
            _screen_TopRightY,
            _screen_TopLeftX,
            _screen_TopLeftY,
            _screen_BottomRightX,
            _screen_BottomRightY,
            _screen_BottomLeftX,
            _screen_BottomLeftY,
            _screen_Width,
            _screen_Height,
            _frame_Width,
            _frame_Height,
            margin;
    
    /** The CameraControl class for zoom */
    public CameraControl camera;
    
    public Renderer(String filename){
        camera = new CameraControl();
        mouse_xf = 0;
        mouse_yf = 0;
        
        this.logfile=filename;
        //System.out.println(logfile+" in renderer");//ok
        second = SecondView.CreateSecondView();
        
        second.r.renderer = this;
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

        //RainMain.alarmList=new ParseAlarmLog(logfile);
        
        /** CHANGE **/
        second.setVisible(false);
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
        
//        // set the normalized offsets
//        normal_offset = Project(1f,1f);
//        
//        mouse_normal = UnProject(1f,1f);
       
        
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
        // scale the matrix
        gl.glScalef(camera.scale_x, camera.scale_y, 1.0f);
        // now render everything on top
        gl.glTranslatef(camera.translate_x, camera.translate_y, 0.0f);
        
        
        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview);
        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection);
        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
        
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
        drawBorderBox(gl);
        drawIPcolumns(gl);
        drawIPlines(gl);
//        if(camera.zoom > 0) {
            // don't draw the red box
            camera.update();
//        } else {
            drawRedBox(gl);
//        }
        
        drawAlarms(gl);
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        
        // draw the controll pad
        //camera.drawControlPad(gl,(float)mouse_x,(float)mouse_y);
        
        gl.glPopMatrix();
        
        // TODO Now draw the measurements
        if(second.isVisible()) {
        	second.r.update();
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
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        
        //this.width = width;
        //this.colwidth = (this.width - this.margin_left - this.margin_right) / this.num_cols;
        
//        if(height < 800) {
//        	this.height = 708;
//        	System.out.println("Height");
//        }
        
        this.height = height;
        this.colheight = this.height - 20;

        /** Fixing Size Problems:  */
//        this.width = width;
//        this.height = height;
//        this.margin_top = (int)(height * 0.01f);
//        this.margin_bottom = (int)(height * 0.01f);
//        this.margin_left = (int)(width * 0.01f);
//        this.margin_right = (int)(width * 0.01f);
////        System.out.println(this.margin_bottom);
//        this.colwidth = (width - (margin_left * 2)) / num_cols;
//        this.winwidth = this.colwidth;
//        
//        this.RedBox.width = colwidth;
        
        /** OPTIMIZATION */
        this.calculateAlarms(gl);
        reshape = true;
        
        /* if there is any variance in window size, 616 can quickly become 608.563 etc, 
         * throwing all your calculations off */
//        glu.gluOrtho2D( 0.0, 616.0, 808.0, 0.0);
        
        // by changing this, many of the calculations you have already done will now work
        //                    was width     was height
        //glu.gluOrtho2D(0.0, (double)616, (double)808, 0.0);
        glu.gluOrtho2D(0.0, width, height, 0.0);
    }
    
    public void calculateAlarms(GL gl) {
        //	Enumeration list = RainMain.alarmList.getAlarmLog().elements();
    	Iterator list = RainMain.alarmList.getAlarmLog().iterator();
        Color color;
        Alarm temp;
        float percent, ipoff1, ipoff2,x,y;
        int count=0;
        
        /** ADDED reset the zoomed list */
        if(dragged == true) {
        	SecondView.second.r.ResetZoomedAlarm(this);
        	//System.out.println(RedBox.x);
        	second.r.RedBox = (Rectangle2D.Float)RedBox.clone();
        	second.r.StartIP = int2ip( offset2ip( offset ));
        	second.r.update();
        }
        
        gen_list = gl.glGenLists(2);        
        gl.glNewList(gen_list,GL.GL_COMPILE);
        
        while(list.hasNext()){
            try{
                count++;
                temp = (Alarm)list.next();
                color = alarmColor(temp.getType());
                if(temp.getPort().matches("22"))
                	color=Color.RED;
                percent = percentOfDay(temp.getTimestamp());
                ipoff1 = ipOffset(ip2int(temp.getBadIP()));
                ipoff2=ipOffset(ip2int(temp.getVictimIP()));
                //System.out.println(ipoff1 + " and " + ipoff2);
               /*if(count>250 && count<350)
                
                   System.out.println(count+" "+temp.getTimestamp()+" "+temp.getBadIP());
                */
                x = 0;
                y = 0;
                
                if(percent==-1){
                    ipoff1=-1;//skip this alarm
                    ipoff2=-1;
                }
                if(ipoff1>0.00){
                    
                    x=margin_left +
                            ( colwidth * ( (int)( num_cols * ipoff1 ) + percent ) );//_screen_TopLeftX + (colwidth * ((int) (8*ipoff)+xx));
                    y=margin_top + ( colheight * ( num_cols * ipoff1 - (int)( num_cols * ipoff1 ) ) );//_screen_TopLeftY + (colheight * (8*ipoff - (int)(8*ipoff)));
                    //drawADot(gl, x, y, color);
                    temp.x = x;
                    temp.y = y;
                    temp.ipoff1 = true;
                }
                
                //ipoff = ipOffset(ip2int(temp.getVictimIP()));
                if(ipoff2>0.00){
                    
                    x=margin_left +
                            ( colwidth * ( (int)( num_cols * ipoff2 ) + percent ) );//_screen_TopLeftX + (colwidth * ((int) (8*ipoff)+xx));
                    y=margin_top + ( colheight * ( num_cols * ipoff2 - (int)( num_cols * ipoff2 ) ) );//_screen_TopLeftY + (colheight * (8*ipoff - (int)(8*ipoff)));
                    //drawADot(gl, x, y, color);
                    temp.x = x;
                    temp.y = y;
                    temp.ipoff2 = true;
                }
            } catch(Exception e) {}
        }
        
        gl.glEndList();
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
        gl.glBegin(GL.GL_LINES);
        
        gl.glVertex2f(x, y);
        gl.glVertex2f(x+1, y);//dot is 1 pixel on main screen, do highest alarm in case of overlap
        gl.glEnd();
    }
    
    /**
     * draws the moving red selector box
     * @param gl
     * @param c
     */
    protected void drawRedBox(GL gl) {
    	
        //TODO might changewinwidth to fit 256 ip,
        
        float[] colXY = colAlignXY();
        
//      lets update the RedBox
    	RedBox.x = colXY[0];
    	RedBox.y = colXY[1];
        
        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex2f(colXY[0], colXY[1]);
        gl.glVertex2f(colXY[0] + colwidth, colXY[1] );
        gl.glVertex2f(colXY[0], colXY[1] + winheight);
        gl.glVertex2f(colXY[0] + colwidth, colXY[1] + winheight);
        gl.glEnd();
        // textlabel at top of box
        gl.glColor3f(1.0f, 1.0f, 0f);
        //gl.glRasterPos2i(mouse_x+margin_left+3, mouse_y-margin_top-2); // <-- position of text
        String ip = int2ip( offset2ip( offset ) ) ;
        //gl.glRasterPos2i(mouse_x+margin_left+3, mouse_y-margin_top-2); // <-- position of text
        gl.glRasterPos2f(colXY[0] + 1, colXY[1] - 2);
        //System.out.println(ip+"y"+colXY[1]);
        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, ip);
        
        //System.out.println("RedBox " + RedBox.y);
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
    	
    	// FIXING SCREEN SIZE PROBLEMS
    	_frame_Height 			= this.height;
        _frame_Width 			= this.width; //616f - 70f;
        _screen_TopRightX 		= _frame_Width  	- (margin_left * 2);
        _screen_TopRightY 		= margin_top;
        _screen_TopLeftX 		= margin_left;
        _screen_TopLeftY 		= margin_bottom;
        _screen_BottomRightX 	= _frame_Width  	- (margin_left * 2);
        _screen_BottomRightY 	= _frame_Height 	- (margin_top * 2);
        _screen_BottomLeftX 		= margin_left;
        _screen_BottomLeftY 		= _frame_Height 	- margin_bottom;
        _screen_Width 			= _screen_TopRightX - _screen_TopLeftX;
        _screen_Height 			= _screen_TopRightY - _screen_BottomRightY;
    }
    
    /** Draws border in the frame
     */
    protected void drawBorderBox(GL gl){
        
        gl.glColor3f(0.5f, 0.5f, 0.5f ); //gray
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2i(margin_left, margin_bottom);
        gl.glVertex2i(margin_top, height-margin_top);
        gl.glVertex2i(margin_top, height-margin_top);
        gl.glVertex2i(colwidth*num_cols+margin_right, height-margin_right);
        gl.glVertex2i(colwidth*num_cols+margin_right,margin_right);
        gl.glVertex2i(margin_left, margin_bottom);
        gl.glVertex2i(colwidth*num_cols+margin_right,margin_bottom);
        gl.glEnd();
        
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
    
    
    /** Draws vertical axes to create columns
     */
    protected void drawIPcolumns(GL gl){
        
        gl.glColor3f(0.5f, 0.5f, 0.5f ); //gray
        gl.glBegin(GL.GL_LINES);
        for (int i = 0; i <= num_cols; i++){
            gl.glVertex2i(margin_left+( i * colwidth ), margin_top +1);
            gl.glVertex2i(margin_left +( i * colwidth ),height - margin_bottom);
        }
        gl.glEnd();
    }
    
   /*
    * Draw alarms in main by passing file name to ParseAlarmLog
    * will access the arraylist through this object.
    */
    protected void drawAlarms(GL gl){
//right now I am just using hard coded ip's and timestamps to see if they draw in the right spot
        //redbox shows up too high, mouse pointer too low.
        
        //Enumeration list = RainMain.alarmList.getAlarmLog().elements();
     	Iterator list = RainMain.alarmList.getAlarmLog().iterator();
        Color color;
        Alarm temp;
        float percent, ipoff1, ipoff2,x,y;
        int count=0;
        
        /** ADDED reset the zoomed list */
        if(dragged == true) {
        	SecondView.second.r.ResetZoomedAlarm(this);
        	//System.out.println(RedBox.x);
         	if(!changed) {
        	second.r.RedBox = (Rectangle2D.Float)RedBox.clone();
        	second.r.StartIP = int2ip( offset2ip( offset ));
        	second.r.update();
         	}
        }
        
//        gl.glCallList(gen_list);
        
        while(list.hasNext()){
            try{
            	/** ADDED OPTIMIZATION */
            	Rectangle2D draw = new Rectangle2D.Float(
            			RedBox.x - 20f,
            			RedBox.y - 20f,
            			RedBox.width + 40f,
            			RedBox.height + 40f);
            	
                count++;
                temp = (Alarm)list.next();
                color = alarmColor(temp.getType());
                
                
                
                
                
//                percent = percentOfDay(temp.getTimestamp());
//                ipoff1 = ipOffset(ip2int(temp.getBadIP()));
//                ipoff2=ipOffset(ip2int(temp.getVictimIP()));
//                //System.out.println(ipoff1 + " and " + ipoff2);
//               /*if(count>250 && count<350)
//                
//                   System.out.println(count+" "+temp.getTimestamp()+" "+temp.getBadIP());
//                */
//                x = 0;
//                y = 0;
//                
//                if(percent==-1){
//                    ipoff1=-1;//skip this alarm
//                    ipoff2=-1;
//                }
//                if(ipoff1>0.00){
//                    
//                    x=margin_left +
//                            ( colwidth * ( (int)( num_cols * ipoff1 ) + percent ) );//_screen_TopLeftX + (colwidth * ((int) (8*ipoff)+xx));
//                    y=margin_top + ( colheight * ( num_cols * ipoff1 - (int)( num_cols * ipoff1 ) ) );//_screen_TopLeftY + (colheight * (8*ipoff - (int)(8*ipoff)));
//                    drawADot(gl, x, y, color);
//                }
//                
//                //ipoff = ipOffset(ip2int(temp.getVictimIP()));
//                if(ipoff2>0.00){
//                    
//                    x=margin_left +
//                            ( colwidth * ( (int)( num_cols * ipoff2 ) + percent ) );//_screen_TopLeftX + (colwidth * ((int) (8*ipoff)+xx));
//                    y=margin_top + ( colheight * ( num_cols * ipoff2 - (int)( num_cols * ipoff2 ) ) );//_screen_TopLeftY + (colheight * (8*ipoff - (int)(8*ipoff)));
//                    drawADot(gl, x, y, color);
//                }
                
        
        
        
        
        
                /** ADDED OPTIMIZATION */
                x = temp.x;
                y = temp.y;
                
//                /** ADDED CONDITION OPTIMIZATION */
//            	if(RedBox.contains((double)x, (double)y) || reshape == true) {
                if(temp.getOnBoolean()==false)//if "on" then draw
            		drawADot(gl,x,y,color);
            		
//            	}
                
                
                /** ADDED New way to send IP's */
                if(dragged == true && temp.getOnBoolean()==false) {
                	
                	
                	
                    if(changed == true && second.r.RedBox.contains((double)x, (double)y) == true) {
                        // System.out.println("LLLLL");    
                         SecondView.second.r.AddZoomedAlarm(temp,
             			(Rectangle2D.Float)second.r.RedBox,
             			color,
             			(float)x,
             			(float)y,
             			second.r.StartIP);
                     } else if(changed == false && RedBox.contains((double)x, (double)y) == true) {
             	//System.out.println("X: " + x + " Y: " + y);
             	SecondView.second.r.AddZoomedAlarm(temp,
             			(Rectangle2D.Float)RedBox.clone(),
             			color,
             			(float)x,
             			(float)y,
             			int2ip( offset2ip( offset ) ));
                     
             }
 	               
                 }
                
//			   System.out.println("x = " + x + " y = " + y + " col = " + col);
                
            }catch(Exception e){
//			   System.out.println(e.toString());
            }
            
            
        }
        
        /** ADDED Reset the dragged */
        dragged = false;
        
        changed=false;
//        /** ADDED Reset reshape */
//        reshape = false;
        
        
    }
    
   /*
    * Draw ip lines in main, horizontal & shows divisions btn subnets.
    */
    protected void drawIPlines(GL gl){
        
        float ipoff=0;
        int x[]=new int[4];
        int y[]=new int[4];
        float result=0;
        int i=0;
        
        for (i=0;i<subnets.length;i++ ) {
            
            ipoff=ipOffset(subnetint[i]);//(ip2int(subnets[i]));-im hard coding
            //ipoff=ipOffset(ip2int(subnets[i]));
            result=num_cols*ipoff;
            x[i]=margin_left+(colwidth*(int)result)+1;
            
            
            for(int n=num_cols-1; n<0;n--){
                if((result)>n-1)
                    result=(n-1)-result;
                //	   System.out.println("resultif"+result);
                //return;
                //   }
            }
            //System.out.println("res*ipoff"+result);
            result=(float)((Math.floor(result)+1)-result);
            // System.out.println("flor"+result);
            
            // y[i]=(int)(result*colheight)+margin_bottom;//ORIGINAL
            
            y[i] = margin_top +  (int)( colheight * ( num_cols * ipoff - (int)( num_cols * ipoff ) ) );
            
            //System.out.println("y"+y[i]);
        }
        
        //y[i]= margin_bottom +
        //(int) (colheight * (( num_cols * ipoff ) ));
        
        /*my $y = $o{maincanvas}->{margin_top} +
        ( $o{colheight} * ( $o{num_cols} * $ipoff - int( $o{num_cols} * $ipoff ) ) );*/
        
        // System.out.println("x"+x[i]+"y"+y[i]);
        
        gl.glColor3f(0.5f, 0.5f, 0.5f ); //gray
        gl.glBegin(GL.GL_LINES);
        for(int z=0;z<4;z++){
            //System.out.println("x"+x[z]+"y"+y[z]);
            gl.glVertex2i(x[z],y[z]);
            gl.glVertex2i(x[z]+colwidth-1,y[z]);
        }
        //gl.glVertex2i (296,633);//hard coding again, a line that doesnt show
        //gl.glVertex2i (390,633);
        gl.glEnd();
        
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
    
    /** This gets a single offset */
    public float ipOffset() {
    	float space = colheight * 8;
    	return (float)space / totalips;
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
cal.setTimeInMillis(Long.parseLong(timestamp)*1000);  //have to convert from seconds to millis
int year = cal.get(Calendar.YEAR);//2005
int month = cal.get(Calendar.MONTH);//9         MONTH goes from 0-11
int day = cal.get(Calendar.DAY_OF_MONTH);//12
int hour = cal.get(Calendar.HOUR_OF_DAY);
//System.out.print(" day="+day+" month="+month+" year="+year);
//System.out.println(" "+timestamp);
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
    /**
     *
     * @return float[0] = xC, float[1] = yC
     */
    public float[] colAlignXY(){
        
        int num_cols = 8;//getNumColumns();
        float[] point = new float[2];
        point[0] = -1f;
        point[1] = -1f;
        int[] options = new int[num_cols];
        for (int i = num_cols - 1; i >= 0; i--){
            options[i] = (int)(_screen_TopLeftX + (colwidth * i));
            
            // *** CHANGE *** Lets use the floating point mouse possitions
//            if (options[i] <= mouse_x){
            if (options[i] <= (int)mouse_xf){
                point[0] = options[i];
                break;
//            } else if (mouse_x <= _screen_TopLeftX){
            } else if ((int)mouse_xf <= _screen_TopLeftX){
                point[0] = _screen_TopLeftX;
                break;
            }
        }
        if (options.length >= 0){
            if (point[0] >= options[options.length - 1]){
                point[0] = options[options.length - 1];
            }
        }
        
        /* CHANGE, We need floating point precision on the y axis. 
         * I changed a few mouse_y variable to mouse_yf in the calculations */
        if ( mouse_y < margin_top )
            point[1] = margin_top; //mouse above columns
        else if (mouse_y > colheight + margin_top){ //mouse below columns
//		System.out.println("tester");
            point[1] = colheight + margin_top;
        } else {
            //check extra line vector
            point[1] = mouse_yf;  // *** CHANGED ***
            //System.out.println("else mousey"+point[1]);
        }
        return point;
    }
    
    public static Color alarmColor(String colorIndex) throws Exception{
        Color color = Color.PINK;
        int integer = Integer.parseInt(colorIndex);
        if ( integer == 31|| integer ==  18||integer ==16||integer ==7)
            color = Color.GREEN;
        else if ( integer ==24|| integer ==19|| integer ==13|| integer ==6|| integer ==5)
            color = Color.YELLOW;
        else if (integer ==9|| integer == 35 || integer ==20|| integer ==17|| integer ==12|| integer ==11|| integer ==4|| integer ==2)
            color = Color.RED;
        else if(integer == 28 || integer == 32)
        		color = Color.BLACK;
        else
            color = Color.MAGENTA;
        return color;
    }
    //worm activity-35, high email-9, mail rejects -12(Red),touched-28 high con index,32(black)
    
    public void mouseDragged(MouseEvent e) {
    	
    	
        mouse_x = e.getX();
        mouse_y = e.getY();

//        mouse_x *= mouse_normal[0];
//        mouse_y *= mouse_normal[1];
        
        //System.out.println("mousemove"+mouse_x+" "+mouse_y);
        
        //int col = (int)( ( mouse_x - margin_left ) / colwidth );//which col mouse in
        int col = (int)( ( mouse_x - _screen_TopLeftX ) / colwidth );
        if(col<0) col=0;
        if(col>(num_cols-1)) col=num_cols-1;
        
        //int dh=height - margin_bottom - margin_top;
        //float coloff=(float)mouse_y/dh;  //Float.
        float coloff = ( float ) ((mouse_y-10f) / colheight);
        //if(coloff<0) coloff=0;
        //if(coloff > 1) coloff=1;
        
        if(mouse_y<margin_top)
            coloff=0;
        if(mouse_y>margin_top+colheight)
            coloff=1;
        
        offset=(col+coloff)/num_cols;//(col/num_cols)*coloff;//.00475382
        
        float pos[] = this.colAlignXY();
//        if(e.getButton() == MouseEvent.BUTTON1) {
            //second.r.zoomIn(pos[0], pos[1], (float)colwidth, (float)winheight);
//        } 
            
            /***********************************/
            if(!SecondView.second.isVisible()) {
        		SecondView.second.setVisible(true);
        	}
        
            /** ADDED */
        	dragged = true;    
            
        gldrawable.display();
    }//2b used for future panning
    
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        
//        mouse_x *= mouse_normal[0];
//        mouse_y *= mouse_normal[1];
        
        //System.out.println("mousemove"+mouse_x+" "+mouse_y);
        
        //int col = (int)( ( mouse_x - margin_left ) / colwidth );//which col mouse in
        int col = (int)( ( mouse_x - _screen_TopLeftX ) / colwidth );
        if(col<0) col=0;
        if(col>(num_cols-1)) col=num_cols-1;
        
        //int dh=height - margin_bottom - margin_top;
        //float coloff=(float)mouse_y/dh;  //Float.
        float coloff = ( float ) ((mouse_y-10f) / colheight);
        //if(coloff<0) coloff=0;
        //if(coloff > 1) coloff=1;
        
        if(mouse_y<margin_top)
            coloff=0;
        if(mouse_y>margin_top+colheight)
            coloff=1;
        
        offset=(col+coloff)/num_cols;//(col/num_cols)*coloff;//.00475382
       
        	
        float[] colXY = colAlignXY();
        
//      lets update the RedBox
    	RedBox.x = colXY[0];
    	RedBox.y = colXY[1];
        
        //mouse_x = (int)(mouse_x / colwidth)*colwidth;//want grid coordinates, not real
        //mouse_y = (int)(mouse_y /winheight)*winheight;
        
        //System.out.println("red box"+mouse_x+" "+mouse_y+"offset"+offset+" col:"+col+" coloff:"+coloff);
        
        gldrawable.display();//redraws canvas
/*
 
 */
        
    }
    
    public void mouseClicked(MouseEvent e) {
    	
    	
        float pos[] = this.colAlignXY();
//        if(camera.zoom == 0) {
//            if(e.getButton() == MouseEvent.BUTTON1) {
//                second.r.zoomIn(pos[0], pos[1], (float)colwidth, (float)winheight);
//            } 
//        } else {
//            if(e.getButton() != MouseEvent.BUTTON1) {
//                camera.zoomOut();
//            }
//        }
        
        float[] colXY = colAlignXY();
        
//      lets update the RedBox
    	RedBox.x = colXY[0];
    	RedBox.y = colXY[1];
    	
    	//System.out.println(colXY[1]);
        
        /***********************************/
    	if(!SecondView.second.isVisible()) {
    		SecondView.second.setVisible(true);
    	}
        
        /** ADDED */
    	dragged = true;
        
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
//            gldrawable.display();//redraws canvas
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
        gldrawable.display();//redraws canvas
    }
    
    /** Listens to the check boxes. */
    public void itemStateChanged(ItemEvent e) {//screen redraws only when it moves
        Object checked = e.getItemSelectable();
       // System.out.println(e.getStateChange());
        //System.out.println(checked.toString());

        if(checked.toString().indexOf("High")>0){//if red-check or unchecked?
        		System.out.println("clicked red");
        
        		if(RainMain.pr1Box.isSelected()){
        			System.out.println("red is selected");
        			selectPriority("1");
        			//this.displayChanged(gldrawable,true,true);
        			drawAlarms(gl);
        			second.r.drawAlarms2(gl);//does not work in zoom if opened already
        		}
        		else{
        			unselectPriority("1");
        			drawAlarms(gl);
        			second.r.drawAlarms2(gl);
        		}
        			//it was unchecked
        }//end if high
        
        if(checked.toString().indexOf("Medium")>0){//if yellow-check or unchecked?
	        	System.out.println("clicked yellow");
	        	
	      		if(RainMain.pr2Box.isSelected()){
        			System.out.println("yellow is selected");
        			selectPriority("2");
    				drawAlarms(gl);
    				second.r.drawAlarms2(gl);
	      		}
        		else{
        			unselectPriority("2");
        			drawAlarms(gl);
        			second.r.drawAlarms2(gl);
        		}
        }//end if medium
        
        if(checked.toString().indexOf("Low")>0){//if green-check or unchecked?
        		System.out.println("clicked green");
        		
        		if(RainMain.pr3Box.isSelected()){
        			System.out.println("green is selected");
    				selectPriority("3");
    			//this.displayChanged(gldrawable,true,true);
    				drawAlarms(gl);
    				second.r.drawAlarms2(gl);
        }
		else{
			unselectPriority("3");
			drawAlarms(gl);
			second.r.drawAlarms2(gl);
		}
        
    }//end if green.
        dragged = true;
        changed = true;
        gldrawable.display();
        }
    

    public void actionPerformed(ActionEvent e) {//buttons
    	
   System.out.println(e.getActionCommand());
   if(e.getActionCommand().compareTo("Reset")==0){
	   resetAlarms();
   }
   if(e.getActionCommand().compareTo("Filter")==0){
	   getAlarmList();
	   filterAlarmTypes();
   }
    	
    }
    
    public void getAlarmList() {//get alarms selected
    	
   // 	e.toString();

    	Object ob[] = RainMain.alarmTypeList.getSelectedValues();
 //   	RainMain.alarmTypeList.get
    	selectedAlarms=new String[ob.length];
    
    	System.out.println(ob.length);
     	//selectedAlarms[ob.length-1] = ob[ob.length-1].toString();
    	// 	RainMain.alarmTypeList.
    	for (int i=0; i < ob.length; i++){
    	selectedAlarms[i] = ob[i].toString();
    	
    	
    	System.out.println(ob[i].toString());
    	}//end if	
    	
    	}
    	
    
    
    
    
	public void selectPriority(String priority){//filters out, or turns "on" alarms
		Iterator list = RainMain.alarmList.getAlarmLog().iterator();
		Alarm temp;
		Color color=null;
		String alarmP=null;
		int rgb; //green=-16711936
		//YELlow   = -256 Red=-65536
		
		while(list.hasNext()){
			temp=(Alarm)list.next();
			
			try{
            color = alarmColor(temp.getType());
			}catch(Exception e){}
			
            rgb = color.getRGB();
            if(rgb==-16711936)
            	alarmP = "3";
            else if(rgb==-256)
            	alarmP = "2";
            else if(rgb==-65536)
            	alarmP = "1";
            
			if (priority.compareTo(alarmP)==0){
				temp.setOnBoolean(false);
			}	
		}
	}
	
	public void unselectPriority(String priority){//filters out, or turns "on" alarms
		Iterator list = RainMain.alarmList.getAlarmLog().iterator();
		Alarm temp;
		Color color=null;
		int rgb;
		String alarmP=null;
		
		while(list.hasNext()){
			temp=(Alarm)list.next();
			
			try{
	            color = alarmColor(temp.getType());
				}catch(Exception e){}
				
            rgb = color.getRGB();
            if(rgb==-16711936)
            	alarmP = "3";
            else if(rgb==-256)
            	alarmP = "2";
            else if(rgb==-65536)
            	alarmP = "1";
            
			if (alarmP.compareTo(priority)==0){
				temp.setOnBoolean(true);
			}	
		}
	}
	
	public void resetAlarms(){//if reset button is clicked
		
		Iterator list = RainMain.alarmList.getAlarmLog().iterator();
		Alarm temp;
		RainMain.alarmTypeList.clearSelection();//reset all 
		RainMain.pr1Box.setSelected(true);
		RainMain.pr2Box.setSelected(true);
		RainMain.pr3Box.setSelected(true);
		while(list.hasNext()){
			temp=(Alarm)list.next();
			temp.setOnBoolean(false);//odd? wont work if set to true.
		}
		
	}
	
	public void filterAlarmTypes(){ //when filter button pressed
		
		Iterator list_all = RainMain.alarmList.getAlarmLog().iterator();
		Alarm temp;
		boolean match=false;
		Color color=null;
		int rgb;
		String alarmP=null;
		while(list_all.hasNext()){
			temp=(Alarm)list_all.next();

			for(int i=0;i<selectedAlarms.length;i++){
				
				if(temp.getAlarmTitle().compareTo(selectedAlarms[i].toString())==0 ){
				
					match=true;//this alarm matches a type
				}
			}//end for
			
			try{
	            color = alarmColor(temp.getType());
				}catch(Exception e){}
            rgb = color.getRGB();
            if(rgb==-16711936)
            	alarmP = "3";
            else if(rgb==-256)
            	alarmP = "2";
            else if(rgb==-65536)
            	alarmP = "1";
            
				if(RainMain.pr1Box.isSelected()&(alarmP.compareTo("1")==0)){	
					match=true;
				}
				if(RainMain.pr2Box.isSelected()&(alarmP.compareTo("2")==0)){
					match=true;
				}
				if(RainMain.pr3Box.isSelected()&(alarmP.compareTo("3")==0)){					
					match=true;
				}
				if(match){	
				temp.setOnBoolean(false);//we have a match
				//	match =false;
				}
				//}//end if
			
				//}//loop through alarms selected end for
				//else if(temp.getAlertName().compareTo(selectedAlarms[i].toString())!=0){//alarmname not on list filtered
				
				if(!match){
					temp.setOnBoolean(true);//none was a match
					}
					match=false;
				//}//end else if
			//}//end for
		}//end while
	//	drawAlarms(gl);//redraw
	//	second.r.drawAlarms2(gl);
        dragged = true;
        changed = true;
        gldrawable.display();
		//reset zoom view
	}
    /** This is called from Renderer2 */
    public void MoveRedBox(float x, float y) {
    	mouse_x = (int)x + 4;
        mouse_y = (int)y;
        
//        mouse_x *= mouse_normal[0];
//        mouse_y *= mouse_normal[1];
        
        //System.out.println("mousemove"+mouse_x+" "+mouse_y);
        
        //int col = (int)( ( mouse_x - margin_left ) / colwidth );//which col mouse in
        int col = (int)( ( mouse_x - _screen_TopLeftX ) / colwidth );
        if(col<0) col=0;
        if(col>(num_cols-1)) col=num_cols-1;
        
        //int dh=height - margin_bottom - margin_top;
        //float coloff=(float)mouse_y/dh;  //Float.
        float coloff = ( float ) ((mouse_y-10f) / colheight);
        //if(coloff<0) coloff=0;
        //if(coloff > 1) coloff=1;
        
        if(mouse_y<margin_top)
            coloff=0;
        if(mouse_y>margin_top+colheight)
            coloff=1;
        
        
        offset=(col+coloff)/num_cols;//(col/num_cols)*coloff;//.00475382
        
        
        float[] colXY = colAlignXY();
        
//      lets update the RedBox
    	RedBox.x = colXY[0];
    	RedBox.y = colXY[1];
//        RedBox.x = x;
//        RedBox.y = y;
        
        //mouse_x = (int)(mouse_x / colwidth)*colwidth;//want grid coordinates, not real
        //mouse_y = (int)(mouse_y /winheight)*winheight;
        
        //System.out.println("red box"+mouse_x+" "+mouse_y+"offset"+offset+" col:"+col+" coloff:"+coloff);
        
    	/** ADDED */
    	dragged = true;
    	
    	//System.out.println("Dragged" + RedBox.y);
    	
        gldrawable.display();//redraws canvas
    }
    
//    public float[] Project(float a, float b) {
//    	int x = 0;
//	        int y = 0;
//	        int viewport[] = new int[4];
//	        double modelview[] = new double[16];
//	        double projection[] = new double[16];
//	        float winX = 0;
//	        float winY = 0;
//	        float winZ[] = new float[1];
//	        double posX[] = new double[1];
//	        double posY[] = new double[1];
//	        double posZ[] = new double[1];
//			
//			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
//	        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);
//	        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
//	        
//	        winX = a;
//	        //winY = b; //(float)viewport[3] - (float)b;
//	        winY = (float)viewport[3] - (float)b;
//	        gl.glReadPixels((int)a, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
//	        
//	        float xf[] = new float[2];
//	        
//	        glu.gluProject((double)winX, (double)winY, (double)winZ[0], modelview, projection, viewport, posX, posY, posZ);
//	        
////	        System.out.println(posY[0]);
//        	xf[0] = (float)posX[0];
//        	xf[1] = (float)posY[0];
//	        
//	        return xf;
//    }
//    
//    public float[] UnProject(float a, float b) {
//    	int x = 0;
//	        int y = 0;
//	        int viewport[] = new int[4];
//	        double modelview[] = new double[16];
//	        double projection[] = new double[16];
//	        float winX = 0;
//	        float winY = 0;
//	        float winZ[] = new float[1];
//	        double posX[] = new double[1];
//	        double posY[] = new double[1];
//	        double posZ[] = new double[1];
//			
//			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
//	        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);
//	        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
//	        
//	        winX = a;
//	        winY = b; //(float)viewport[3] - (float)b;
//	        winY = viewport[3] - (float)b;
//	        gl.glReadPixels((int)a, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ);
//	        
//	        glu.gluUnProject((double)winX, (double)winY, (double)winZ[0], modelview, projection, viewport, posX, posY, posZ);
//	        
////	        System.out.println(posY[0]);
//	        
//	        float xf[] = {(float)posX[0], (float)posY[0]};
//	        return xf;
//    }
    
/*	snapTo(float x, int scale)
{
 int temp = Math.round(x / scale);
 return temp * scale;
}*/
    
    
    
    // glc.repaint();
    
 /*   # move the red window square and update the text over it with the current IP selected
    # update the zoomed window if the mouse button is pressed
    sub motion {
        my ( $c, $mx, $my ) = @_;
        # if the mouse Y is too high, clips, too low, clips, otherwise returns mouse Y.
        my $col = int( ( $mx - $o{maincanvas}->{margin_left} ) / $o{colwidth} );
        $col = 0 if $col < 0;
        $col = $o{num_cols}-1 if $col > ($o{num_cols}-1);
        my $dh = $o{maincanvas}->{height} - $o{maincanvas}->{margin_bottom} - $o{maincanvas}->{margin_top};
        my $coloff = ( $my - $o{maincanvas}->{margin_top} - ($o{winheight}/2) ) / $dh;
        $coloff = 0 if $coloff < 0;
        $coloff = 1 - ($o{winheight}/$dh) if $coloff > 1 - ($o{winheight}/$dh);
        $o{offset} = ($col+$coloff)/$o{num_cols};
        my $y = $o{maincanvas}->{margin_top} + ($dh*$coloff);
        my $x = $o{maincanvas}->{margin_left} + ( $o{colwidth} * $col );
        $c->coords( 'window', $x, $y, $x + $o{winwidth}, $y + $o{winheight} );
        $c->coords( 'wintitle', $x + ($o{winwidth}/2), $y );
        $c->itemconfigure( 'wintitle', -text => int2ip( off2ip( $o{offset} ) ) );
        # draw the alarms if the mouse button is pressed and there is a zoom window
        $o{startoff} = $o{offset} if ( $w{mb} && defined( $w{tl} ) );
        if ( $w{mb} && defined( $w{tl} ) ){
            $o{ipstart} = off2ip($o{offset});
            drawAlarms() ;
        }
    }
  
  }*/
    
    
}

