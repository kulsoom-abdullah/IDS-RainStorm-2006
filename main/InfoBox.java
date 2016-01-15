
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.text.DateFormat;

import net.java.games.jogl.GL;
import net.java.games.jogl.util.GLUT;

public class InfoBox {
	Rectangle2D.Float size;
//	String detail;
//	String type;
//	String occured;
//	String source;
//	String destination;
	
	Alarm alarm;
	
	public InfoBox() {
		alarm = new Alarm();
		size = new Rectangle2D.Float();
//		detail = new String();
//		type = new String();
//		occured = new String();
//		source = new String();
//		destination = new String();
	}
	
	public void drawInfo(GL gl, GLUT glut, float x, float y, int number) {
		float xx = 0f;
		float yy = 0f;
		float in = 16f;
		
		// make sure the info box is large enough for info
//		float width_determinate = 0f;
		
		// keep up with all lengths 
		float width_temp = 0f;
		
		// the font we need metrics on
		int gl_font = GLUT.BITMAP_HELVETICA_12;
		
		// This list will hold all listings in order
		LinkedList listings = new LinkedList();
		
		// This string is used over and over again
		String temp = new String();
		int badip=0;
		// Same but int
		int tmp = 0;
		
		for(int a = 0; a < 9; a++) {
			switch(a){
			case 0:
				temp = alarm.getAlarmTitle();
				if(!temp.equals("0") && temp != null&&temp.length()>4) {
					temp = "Alarm: " + temp;
					width_temp = glut.glutBitmapLength(gl_font, temp);
					listings.add(temp);
					yy += in;
				}
				break;
			case 1:
				temp = alarm.getType();
				temp = "Type: " + temp;
				width_temp = glut.glutBitmapLength(gl_font, temp);
				listings.add(temp);
				yy += in;
				break;
			case 2:
				temp = alarm.getTimestamp();
				long occ = Long.parseLong(temp)*1000;
				temp = DateFormat.getTimeInstance().format(new Date(occ));
				temp = "Time: " + temp;
				width_temp = glut.glutBitmapLength(gl_font, temp);
				listings.add(temp);
				yy += in;
				break;
			case 3:
				temp = alarm.getZone();
				temp = "Zone: " + temp;
				width_temp = glut.glutBitmapLength(gl_font, temp);
				listings.add(temp);
				yy += in;
				break;
			case 4: 
				temp = alarm.getVictimIP();
				if(!temp.equals("0") &&  temp != null) {
					temp = "Attacker IP: " + temp;//temp = "Victim IP: " + temp;
					width_temp = glut.glutBitmapLength(gl_font, temp);
					listings.add(temp);
					yy += in;
					badip=1;
				}
				else badip=0;
				break;
			case 5: 
				temp = alarm.getBadIP();
				if(badip==1){
				temp = "Victim IP: " + temp;//temp = "Attacker IP: " + temp;
				}//if no 2nd ip
				else temp = "Attacker IP: " + temp;
				width_temp = glut.glutBitmapLength(gl_font, temp);
				listings.add(temp);
				yy += in;
				
				break;
			case 6:
				temp = alarm.getPort();
				if(!temp.equals("0") &&  temp != null) {
					temp = "Port: " + temp;
					width_temp = glut.glutBitmapLength(gl_font, temp);
					listings.add(temp);
					yy += in;
				}
				break;
			case 7:
				temp = alarm.getCI();
				if(!temp.equals("0") && temp != null) {
					temp = "CI: " + temp;
					width_temp = glut.glutBitmapLength(gl_font, temp);
					listings.add(temp);
					yy += in;
				}
				break;
			case 8:
				temp = alarm.getDetail();
				if(!temp.equals("0") && temp != null) {
					String dS = "Details: ";
					width_temp = glut.glutBitmapLength(gl_font, dS);
					listings.add(dS);
					yy += in;
					temp = " " + temp;
					width_temp = glut.glutBitmapLength(gl_font, temp);
					listings.add(" " + temp);
					yy += in;
				}
				break;
			default:
				break;
			}
			
			if(width_temp > xx) {
				xx = width_temp;
				width_temp = 0f;
			}
		}
		
		xx += 10;
		
		float colx = 0f;
		float coly = 0f;
		
		if(x < xx + 15) {
			size.x = x;
			colx = x - 10f;
		} else {
			size.x = x - xx; //240f;
			colx = x + 15f;
		}
		if(y < yy + 25f) {
			size.y = y;
			size.x += 12f;
			coly = y;// - 10f;
		} else {
			size.y = y - yy;
			coly = y + 10f;
		}
		
		String numS = String.valueOf(number + 1);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glRasterPos2f(colx,coly);
        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, numS);
		
		size.width = xx; //240f;
		size.height = yy;// + 10;
		
		gl.glColor3f(0.2f, 0.4f, 0.2f);
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2f(size.x, size.y);
			gl.glVertex2f(size.x, size.y + size.height);
			gl.glVertex2f(size.x + size.width, size.y + size.height);
			gl.glVertex2f(size.x + size.width, size.y);
		gl.glEnd();
		
		gl.glColor3f(0.2f, 0.8f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2f(size.x, size.y);
			gl.glVertex2f(size.x, size.y + size.height);
			gl.glVertex2f(size.x + size.width, size.y + size.height);
			gl.glVertex2f(size.x + size.width, size.y);
		gl.glEnd();
		
		xx = size.x + 4f;
		yy = size.y - 4f;

		Iterator it = listings.iterator();
		
		gl.glColor3f(1.0f, 0.6f, 0.2f);
		
		while(it.hasNext()) {
			temp = (String)it.next();
			gl.glRasterPos2f(xx, yy+=in);
	        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, temp);
		}
		
		//System.out.println(yy);
	}
	
//	public void draw(GL gl, GLUT glut, float x, float y, int number) {
//		float colx = 0f;
//		float coly = 0f;
//		
//		if(x < 270) {
//			size.x = x;
//			colx = x - 10f;
//		} else {
//			size.x = x - 240f;
//			colx = x + 15f;
//		}
//		if(y < 130f) {
//			size.y = y;
//			coly = y;// - 10f;
//		} else {
//			size.y = y - 100f;
//			coly = y + 10f;
//		}
//		
//		String numS = String.valueOf(number + 1);
//		gl.glColor3f(1.0f, 1.0f, 1.0f);
//		gl.glRasterPos2f(colx,coly);
//        glut.glutBitmapString(gl, GLUT.BITMAP_TIMES_ROMAN_10, numS);
//		
//		size.width = 240f;
//		size.height = 100f;
//		
//		gl.glColor3f(0.2f, 0.4f, 0.2f);
//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex2f(size.x, size.y);
//			gl.glVertex2f(size.x, size.y + size.height);
//			gl.glVertex2f(size.x + size.width, size.y + size.height);
//			gl.glVertex2f(size.x + size.width, size.y);
//		gl.glEnd();
//		
//		gl.glColor3f(0.2f, 0.8f, 0.2f);
//		gl.glBegin(GL.GL_LINE_LOOP);
//			gl.glVertex2f(size.x, size.y);
//			gl.glVertex2f(size.x, size.y + size.height);
//			gl.glVertex2f(size.x + size.width, size.y + size.height);
//			gl.glVertex2f(size.x + size.width, size.y);
//		gl.glEnd();
//		
//		float xx = size.x + 4f;
//		float yy = size.y - 2f;
//		float in = 16f;
//		
//		gl.glColor3f(1.0f, 0.6f, 0.2f);
//		
//		gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, "Alarm Type: " + type);
//        
//        long occ = Long.parseLong(occured)*1000;
//
//        gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, "Occured: " + DateFormat.getTimeInstance().format(new Date(occ)));
//        gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, "Source: " + source);
//        
//        gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, "Destination: " + destination);
//        
//        gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, "Alarm Details: ");
//        
//        gl.glRasterPos2f(xx, yy+=in);
//        glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_12, detail);
//	}
}
