import net.java.games.jogl.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.awt.Font;


public class RainMain extends JFrame implements MouseWheelListener {
	
    public static Dimension PREFERRED_FRAME_SIZE =
        new Dimension (616,708);//838
    
    GLCanvas canvas;
    
    public static ParseAlarmLog alarmList;  //global
    public static ArrayList alarmTypes;
    //public static alarmTypesList;
    public static JCheckBox pr1Box;
    public static JCheckBox pr2Box;
    public static JCheckBox pr3Box;
    public static JScrollPane alarmScroll = new JScrollPane();
    Insets spacing = new Insets(1,0,0,1);
    JButton resetButton;
    JButton filterButton;
    
    public static JList alarmTypeList;
    public static Set alarmTypesSet;//create unique list of alarm types
    
    Alarm temp;
    Font font = new Font("TimesRoman", Font.PLAIN, 12);
    Color gold = new Color(255,215,0);
    
    public RainMain(String logfile){
    	
        // init Frame
        super ("IDS Rainstorm");
//        System.out.println ("constructor");
        //this.setSize(616,808);
        this.setResizable(true);
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        int sz = (int)tk.getScreenSize().getHeight();
	this.PREFERRED_FRAME_SIZE.height = sz - 32;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // get a GLCanvas
        GLCapabilities capabilities = new GLCapabilities();
        canvas =
            GLDrawableFactory.getFactory().createGLCanvas(capabilities);
        
        // add a GLEventListener, which will get called when the
        // canvas is resized or needs a repaint
        alarmList=new ParseAlarmLog(logfile);
        Iterator list = alarmList.getAlarmLog().iterator();
      
        alarmTypesSet = new TreeSet();
        
        while(list.hasNext()){
        		temp=(Alarm)list.next();
        		
        		alarmTypesSet.add(temp.getAlarmTitle());
        }
        
        Renderer r = new Renderer(logfile);
        canvas.addGLEventListener(r);
        canvas.addMouseMotionListener(r);
        canvas.addMouseListener(r);
        canvas.addMouseWheelListener(r);
        canvas.addComponentListener(r);
        canvas.addKeyListener(r);
        
        //<html>Candidate 1: 
       // <font color=red>Sparky the Dog</font></html>
        
        canvas.addMouseWheelListener(this);
        
        //checkbox for user to filter on alarm color
        
        pr1Box = new JCheckBox("<html><font color=#ff0000 face=Times New Roman>High Priority Alarms</font></html>");
        pr1Box.setSelected(true);
        pr2Box = new JCheckBox("<html><font color=#ffff00 face=Times New Roman>Medium Priority Alarms</font></html>");
        pr2Box.setSelected(true);
        pr3Box = new JCheckBox("<html><font color=#00ff00 face=Times New Roman>Low Priority Alarms</font></html>");
        pr3Box.setSelected(true);
        pr1Box.setMargin(spacing);
        pr2Box.setMargin(spacing);
        pr3Box.setMargin(spacing);
        
        pr1Box.addItemListener(r);
        pr2Box.addItemListener(r);
        pr3Box.addItemListener(r);
        //pr1Box.setFont();
        pr1Box.setBackground(Color.BLACK);
        pr2Box.setBackground(Color.BLACK);
        pr3Box.setBackground(Color.BLACK);
        //Put the check boxes in a column in a panel
        JPanel checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.add(pr1Box);
        checkPanel.add(pr2Box);
        checkPanel.add(pr3Box);
        checkPanel.setToolTipText("Filters on priorities chosen regardless\nof alarm titles selected in list");
        //checkPanel.setOpaque(true);
        checkPanel.setBackground(Color.BLACK);
        //checkPanel.setForeground(Color.BLACK);
        
        alarmTypeList = new JList(alarmTypesSet.toArray() );
        alarmTypeList.setLayoutOrientation(JList.VERTICAL_WRAP);
        alarmTypeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        alarmTypeList.setVisibleRowCount(3);
        alarmTypeList.setFont(font);
        alarmTypeList.setForeground(gold);
        alarmTypeList.setBackground(Color.black);
        alarmTypeList.setFixedCellWidth(150);
       // alarmTypeList.addListSelectionListener(r);
        //alarmTypeList.setToolTipText();
        alarmScroll.getViewport().add(alarmTypeList);
        alarmScroll.setBackground(Color.black);
        alarmScroll.setForeground(gold);
        
        resetButton = new JButton("Reset");//reset every filter
        filterButton=new JButton("Filter");//filter on alarms selected
        resetButton.setBackground(Color.black);
        resetButton.setToolTipText("Plots all alarms");
        //resetButton.setForeground(gold);
        resetButton.setFont(font);
        resetButton.addActionListener(r);
        filterButton.setBackground(Color.black);
        filterButton.setFont(font);
        filterButton.addActionListener(r);
        filterButton.setToolTipText("Plots alarm titles and priorities selected");
        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
       buttonPanel.add(filterButton);
       buttonPanel.add(resetButton);
       buttonPanel.setForeground(Color.black);
       
       
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(Color.black);
      // JPanel menuPanel = new JPanel(new GridLayout(0,3)); 
       //JPanel menuPanel = new JPanel(new FlowLayout());
       GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.FIRST_LINE_START;
        //c.gridwidth=3;
        c.weighty=0;
        c.weightx = 0.5;
        
        menuPanel.add(checkPanel,c);
        
        //menuPanel.add(checkPanel);
        c.gridx=2;
        c.gridy=0;
        c.gridwidth=1;
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.PAGE_START;
        c.weightx=0;
        c.weighty=1;
        
        menuPanel.add(buttonPanel,c);
        c.gridy=0;
        //menuPanel.add(buttonPanel);
        c.gridx=3;
        c.gridwidth=3;
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.FIRST_LINE_END;
        c.weightx=1;
        c.weighty=1;
        c.ipadx=30;
        c.ipady=15;
        menuPanel.add(alarmScroll,c);
        //menuPanel.add(alarmScroll);
        //another panel to hold both checkboxpanel and jlist.
        getContentPane().add(menuPanel, BorderLayout.PAGE_START);
        // now add the canvas to the Frame.  Note we use BorderLayout.CENTER
        // to make the canvas stretch to fill the container (ie, the frame)
        getContentPane().add(canvas, BorderLayout.CENTER);
        //System.out.println(this.getHeight());
        
//        // try this
//        SecondView second = new SecondView();
//        second.pack();
//        second.setVisible(true);
    }
    
    public Dimension getPreferredSize () {
        return PREFERRED_FRAME_SIZE;
    }

    
    /** main just creates and shows a RainMain Frame
     */
    public static void main (String[] args) {
    	
   // 	System.setProperty("java.librarypath","/Users/kulsoom/Documents/workspace/Rain.jar");
    //	System.loadLibrary("jogl");
    	
    	// I work in an IDE, so I cheat on command line arguments
//    	args = new String[1];
//    	//args[0] = "samplealarmlog.txt";
//        //args[0] = "xaa.txt";
//        args[0] = "extract2.txt";
//        //args[0] = "iss.txt";
    	//arg filename in line, then pass filename to rainmain   
    	if(args.length < 1) {      
    		System.out.println("Usage:java RainMain <logfile>");
    		System.exit(1);   
    	}    
    	JFrame f = new RainMain(args[0]);
    	
    	//JFrame f = new RainMain("samplealarmlog.txt");
//        JFrame f = new RainMain("testInput24HR.txt");
    	f.pack();
        //f.addMouseMotionListener(); 
    	f.setVisible(true);
    }

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
//		camera.Zoom(e.getUnitsToScroll());
//		System.out.println("WHEEL");
		int direction = arg0.getUnitsToScroll();
		
		PREFERRED_FRAME_SIZE.height += direction;
		
		canvas.reshape(0,
				0,
				616,
				PREFERRED_FRAME_SIZE.height - 32);
	}
	         
}

