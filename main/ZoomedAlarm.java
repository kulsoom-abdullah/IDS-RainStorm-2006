import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

public class ZoomedAlarm implements Comparable {
	float x;
	float y;
	float radius;
	Color color;
	Alarm alarm;
	boolean line;
	/** This is the bounds of this rect, including
	 * the scale and projection.
	 */
	Rectangle2D.Float screen_projection;
	Line2D.Float screen_line;
        /** Added by Greg, to hold colision adjusted y possition */
        float y_adjusted;
        /** Added by Greg to hold the drawn sub string */
        String subs;
        /** Added for comparason */
        long ip;
	
    	public ZoomedAlarm() {
    		x = 0f;
    		y = 0f;
    		radius = 0f;
    		screen_projection = new Rectangle2D.Float();
    		screen_line = new Line2D.Float();
    		line = false;
                    y_adjusted = 0f;
                    subs = new String();
    	}
	
	public boolean isSame(ZoomedAlarm za) {
		if(za.alarm == null) {
			return false;
		} else {
			if(za.alarm.getBadIP().equals(alarm.getBadIP())) {
				if(za.alarm.getVictimIP().equals(alarm.getVictimIP())){
					return true;
				}
			} 
		}
		return false;
	}
	
    
    /** Added by Greg, do not return 0, or you will loose alarms */
    public int compareTo(Object o) {
//        ZoomedAlarm a = (ZoomedAlarm)o;
//        if(a.y_adjusted < y_adjusted) {
//            return 1;
//        } else if(a.y_adjusted > y_adjusted) {
//            return -1;
//        }
//        return 1;
        
        ZoomedAlarm a = (ZoomedAlarm)o;
        if(a.ip < this.ip) {
            return 1;
        } else if(a.ip > this.ip) {
            return -1;
        }
        return 1;
    }
}
