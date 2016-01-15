import net.java.games.jogl.GL;
/*
 * CameraControl.java
 *
 * Created on January 18, 2006, 7:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Greg Tedder
 */
public class CameraControl {
   
   public static final int ZOOM_ONE = 0;
   public static final int ZOOM_TWO = 1;
   public static final int ZOOM_THREE = 2;
   public static final int ZOOM_FOUR = 4;
   
   /** The current zoom */
   public int zoom;
   
   /** The current scale */
   public float scale_x;
   public float scale_y;
   
   /** The velocity of scaling */
   public float scale_x_velocity;
   public float scale_y_velocity;
   
   /** Keep up with the actual size of the panel */
   public float panel_width;
   public float panel_height;
   
   /** The current translations */
   public float translate_x;
   public float translate_y;
   
   /** The constants for the KeyPressed */
   public int keyPressedX = 0; // -1, 0 or 1
   public int keyPressedY = 0; // -1, 0 or 1
   
   /** Creates a new instance of CameraControl */
   public CameraControl() {
       panel_width = 0;
       panel_height = 0;
      zoom = 0;
      scale_x = 1.0f;
      scale_y = 1.0f;
      scale_x_velocity = 1.0f;
       scale_y_velocity = 1.0f;
      translate_x = 0.0f;
      translate_y = 0.0f;
   }
   
   /** Use this zoom */
   public void zoom(int x) {
	   scale_y += (float)x * -1 * 0.01 * scale_y;
	   if(scale_y < 1.0) {
		   scale_y = 1.0f;
	   }
	   scale_x = scale_y;
	   update();
   }
   
   /** Update the panning */
   public void update() {
       if(keyPressedY != 0) {
           // TODO, do not pass the top or bottom
           translate_y += (float)keyPressedY * 4f * scale_x;
       }
       if(keyPressedX != 0) {
    	   // TODO do not pass the sides
    	   translate_x += (float)keyPressedX * 5f * scale_x;
       }
       
       // Stop from zooming out of the box x-wise
       if(translate_x > 0) {
		   translate_x = 0f;
	   }
	   float width_stop = (translate_x * -1f) + (panel_width / scale_x);
	   if(width_stop > (panel_width * scale_x)) {
		   translate_x += (width_stop - (panel_width * scale_x));
	   }
	   //System.out.println("if(ws:" + width_stop + " > " + (panel_width * scale_x));
   }
   

   
}
