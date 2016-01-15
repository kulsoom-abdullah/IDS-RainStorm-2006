
import java.awt.geom.Line2D;

import net.java.games.jogl.GL;

public class Arrow {
	
	// the center
	float x = 0;
	float y = 0;
	
	// a side
	float x1 = 0;
	float y1 = 0;
	
	// a side
	float x2 = 0;
	float y2 = 0;
	
	// padding
	float padx;
	float pady;
	
	public void drawArrow(GL gl, float x1f, float y1f, float x2f, float y2f, float spacing, boolean highlight) {
		
		this.x = x2f - x1f;
		this.y = y2f - y1f;
		
		this.normalize();
		
		double theta = Math.atan2((double)x, (double)y);
		double degrees = Math.toDegrees(theta);
		
		this.x1 = (float)Math.sin(Math.toRadians((degrees + (double)25)));
		this.y1 = (float)Math.cos(Math.toRadians((degrees + (double)25)));
		
		this.x2 = (float)Math.sin(Math.toRadians((degrees - (double)25)));
		this.y2 = (float)Math.cos(Math.toRadians((degrees - (double)25)));
		
		this.pad(spacing);
		
		
		// draw the line
		
		short sh = 11111;
		gl.glEnable(GL.GL_LINE_STIPPLE);
        gl.glLineStipple(3, sh);
        
        if(highlight == true ) {
        	gl.glColor3f(1.0f, 0.8f, 0.0f);
        } else {
        	gl.glColor3f(0.5f,0.5f,0f);
        }
        gl.glBegin(GL.GL_LINES);
			gl.glVertex2f(x1f + padx, y1f + pady);
			gl.glVertex2f(x2f, y2f);
		gl.glEnd();
        
		
		gl.glDisable(GL.GL_LINE_STIPPLE);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		
		// draw the arrow
		
//		gl.glColor3f(1.0f, 0.6f, 0.2f);
		if(highlight == false) {
//			gl.glColor3f(0.8f,0.8f,0.8f);
			gl.glColor3f(1.0f, 0.6f, 0.2f);
		}
		gl.glBegin(GL.GL_LINES);
		gl.glLineWidth(0.1f);
			gl.glVertex2f(x1f + padx, y1f + pady);
			gl.glVertex2f((x1f + x1 * 12) + padx, (y1f + y1 * 12) + pady);
			gl.glVertex2f(x1f + padx, y1f + pady);
			gl.glVertex2f((x1f + x2 * 12) + padx, (y1f + y2 * 12) + pady);
		gl.glEnd();
		
		gl.glDisable(GL.GL_LINE_SMOOTH);
	}
	
public void drawArrowReverse(GL gl, float x1f, float y1f, float x2f, float y2f, float spacing, boolean highlight) {
		
		this.x = x1f - x2f;
		this.y = y1f - y2f;
		
		this.normalize();
		
		double theta = Math.atan2((double)x, (double)y);
		double degrees = Math.toDegrees(theta);
		
		this.x2 = (float)Math.sin(Math.toRadians((degrees + (double)25)));
		this.y2 = (float)Math.cos(Math.toRadians((degrees + (double)25)));
		
		this.x1 = (float)Math.sin(Math.toRadians((degrees - (double)25)));
		this.y1 = (float)Math.cos(Math.toRadians((degrees - (double)25)));
		
		this.pad(spacing);
		
		
		// draw the line
		
		short sh = 11111;
		gl.glEnable(GL.GL_LINE_STIPPLE);
        gl.glLineStipple(3, sh);
        
        if(highlight == true ) {
        	gl.glColor3f(1.0f, 0.8f, 0.0f);
        } else {
        	gl.glColor3f(0.5f,0.5f,0f);
        }
        gl.glBegin(GL.GL_LINES);
			gl.glVertex2f(x2f + padx, y2f + pady);
			gl.glVertex2f(x1f, y1f);
		gl.glEnd();
        
		
		gl.glDisable(GL.GL_LINE_STIPPLE);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		
		// draw the arrow
		
//		gl.glColor3f(1.0f, 0.6f, 0.2f);
		if(highlight == false) {
//			gl.glColor3f(0.8f,0.8f,0.8f);
			gl.glColor3f(1.0f, 0.6f, 0.2f);
		}
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2f(x2f + padx, y2f + pady);
			gl.glVertex2f((x2f + x2 * 12) + padx, (y2f + y2 * 12) + pady);
			gl.glVertex2f(x2f + padx, y2f + pady);
			gl.glVertex2f((x2f + x1 * 12) + padx, (y2f + y1 * 12) + pady);
		gl.glEnd();
		
		gl.glDisable(GL.GL_LINE_SMOOTH);
	}
	
	public void pad(float spacing) {
		padx = (x * spacing);
		pady = (y * spacing);
	}
	
	public void divide(float f) {
		this.x /= f;
		this.y /= f;
	}
	
	public float length(float x1,float y1,float x2,float y2) {
		//System.out.println("Length: " + (float)Math.sqrt(x*x + y*y));
		return (float)Math.sqrt(x1*x2 + y1*y2);
	}
	
	public float length() {
		//System.out.println("Length: " + (float)Math.sqrt(x*x + y*y));
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public void normalize() {
		divide(length());
	}
	
//	public void normal(Line2D.Float line) {
//		System.out.println("Cross Product");
//		this.x = (v1.y * v2.z) - (v1.z * v2.y);
//		this.y = (v1.z * v2.x) - (v1.x * v2.z);
//		crossY = y;
//		this.z = (v1.x * v2.y) - (v1.y * v2.x);
//		System.out.println("X: " + x + "\nY: " + y + "\nZ: " + z);
//	}
}
