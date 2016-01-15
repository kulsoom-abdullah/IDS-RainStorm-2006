import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader; 
 
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Calendar;

/*
 * The file is transformed into a hashmap of key/value pairs
 * Items are stored in the format of R:x-C:x
 * All rules are processed after being read from the file
 * 
 */
public class ParseAlarmLog {
	private Vector alarmVector = null;
	//searching for subset (in form of list) needed for zoom view?
	public ParseAlarmLog(String log){
		alarmVector = new Vector();
		String line,entry;
		String timestamp = null,zone = null,badIP = null,type = null,detail = null,thresholdValue = null,victimIP = null,port = null,alarmTitle = null,CI = null;
		boolean on=true;//default/initial is all lines are true (not filtered)
		StringTokenizer st = null;
		//Vector alarmVec = new Vector();	
		int currRow = 1,currCol=1;
		
		// filterAlarms will determine the days contained in the log
		//   and will prompt user to select one day
		long beginTimestamp, currentTimestamp, endTimestamp;    // mark the beginning and ending timestamps for the range
		
//		System.out.println(log+" parseAlarm constr");//HERE
		//log = "samplealarmlog.txt";

		beginTimestamp = filterAlarms (log);  // initial pass through the logfile to analyze timestamps and get user input
		//beginTimestamp = filterAlarms ("samplealarmlog.txt");
		endTimestamp = beginTimestamp + 86400;
		
System.out.println ("begin at " + beginTimestamp + ", end at " + endTimestamp);
		
		try{	
			System.out.println("here");
			
			FileReader fr = new FileReader(log);
			BufferedReader br = new BufferedReader(fr,256);
			
		
			//this is the 2nd pass through the logfile (after filterAlarms)		
			line=br.readLine();
			//each row
			while (line != null){
				st = new StringTokenizer(line,"\t");
				//each column
				currCol=1;
				while(st.hasMoreElements()){
					switch(currCol){
					case 2: timestamp = (String) st.nextElement();
							break;
					case 3: zone = (String)st.nextElement();
							break;
					case 4: badIP = (String)st.nextElement();
							break;
					case 5: type = (String)st.nextElement();
							break;
					case 6: detail = (String)st.nextElement();
							break;
					case 7: thresholdValue = (String)st.nextElement();
							break;
					case 8: victimIP = (String)st.nextElement();
							break;
					case 9: port = (String)st.nextElement();
							break;
					case 10: alarmTitle = (String)st.nextElement();//changed case 11
							if(alarmTitle.length()<3)
								alarmTitle=(String)st.nextElement();//then title in 11
							break;
					case 13: CI = (String)st.nextElement();
							break;
					default: entry = (String)st.nextElement();
					}
					currCol++;
				}
				currentTimestamp = Long.parseLong (timestamp);
				if ( (currentTimestamp >= beginTimestamp) && (currentTimestamp < endTimestamp) ) {
    				// only add an alarm that falls within the range of 1 day
    				// only add an alarm that falls within the range of 1 day
					//#TODO Change here to take out some alarms from view.	
						if(!(type.matches("28")||type.matches("32")))
							alarmVector.add(new Alarm(timestamp,zone,badIP,type,detail,thresholdValue,victimIP,port,alarmTitle,CI,on));								 
			    }
				line=br.readLine();		
				currRow++;
			}			
			br.close();	
			fr.close();
//System.out.println ( "number of elements in alarmVector: " + alarmVector.size() );		
		}      
		catch (IOException e) {  
			System.err.println(e);
		}

//System.out.println ( "First element: " + alarmVector.firstElement().toString() );
//System.out.println ( "Last element:  " + alarmVector.lastElement() );

	}
	public Vector getAlarmLog(){
		return alarmVector;
	}
	
	// initial pass through the logfile, looking at timestamps
	public long filterAlarms (String log){  

		String line, timestamp ;
		StringTokenizer st = null;
		long earliestTs = Long.MAX_VALUE;  //starting value for range of timestamps
		long latestTs = Long.MIN_VALUE;    //ending value for range of timestamps
		long currentTs;		
		//System.out.println(log+"filterAlarms");
		try{	
			FileReader fr = new FileReader(log);
			//FileReader fr = new FileReader("samplealarmlog.txt");
			BufferedReader br = new BufferedReader(fr,256);	

			line=br.readLine();
			while (line != null) {
    		    st = new StringTokenizer (line,"\t");
    		    
    		    if (st.hasMoreElements())
        		    st.nextElement();       //discard the first token
        		else continue;
        		
        		if (st.hasMoreElements())   //get the timstamp
        		    timestamp = (String) st.nextElement();
        		else continue; 
        		  
    		    currentTs = Long.parseLong (timestamp);
    		    if (currentTs < earliestTs) {
    		        earliestTs = currentTs;
		        }
    		    if (currentTs > latestTs) {
    		        latestTs = currentTs;
		        }
				line=br.readLine();		
			}
			
			Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(earliestTs*1000);  //convert seconds to millis
            int year1 = cal.get(Calendar.YEAR);
            int month1 = cal.get(Calendar.MONTH) + 1;    //MONTH goes from 0-11
            int day1 = cal.get(Calendar.DAY_OF_MONTH);
            int hour1 = cal.get(Calendar.HOUR_OF_DAY);
            System.out.print("\n"+"earliest date = " + month1 + "-" + day1 + "-" + year1 + ", hour " + hour1);
            System.out.println(", timestamp "+earliestTs);
            
            cal.setTimeInMillis(latestTs*1000);  //convert seconds to millis
            int year2 = cal.get(Calendar.YEAR);
            int month2 = cal.get(Calendar.MONTH) + 1;      //MONTH goes from 0-11
            int day2 = cal.get(Calendar.DAY_OF_MONTH);
            int hour2 = cal.get(Calendar.HOUR_OF_DAY);
            System.out.print("  latest date = " + month2 + "-" + day2 + "-" + year2 + ", hour " + hour2);
            System.out.println(", timestamp "+latestTs + "\n");
            
            br.close();
            fr.close();
		}
		catch (IOException e) {  
			System.err.println(e);
		}
		
		// get the user to choose the day
		Calendar cal = Calendar.getInstance();  //use this Calendar for user input
		float daysInLog = (latestTs - earliestTs) / 86400.0f;
		int inputStyle = 0;
		if ( daysInLog > 10.0 ) {
		    System.out.println ("There are more than 10 days in the log.");
            System.out.println ("Please type the desired day in the format MM-DD-YYYY");
            System.out.print (">");  //user prompt
            inputStyle = 1;
        }
        else {            
            cal.setTimeInMillis(earliestTs*1000);  //convert seconds to millis     
            System.out.println ("Please select one of the following options:"); 
            System.out.println ("(type the number and press Enter)");                  
            for (int i=1; i<=(daysInLog + 1); i++) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;    //MONTH goes from 0-11
                int day = cal.get(Calendar.DAY_OF_MONTH);
                System.out.println ( i + ". " + month + "-" + day + "-" + year);
                cal.add (Calendar.DATE,1);  // add 1 day
            }
            System.out.print (">");  //user prompt
            inputStyle = 2;   
        }
       
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 

       
        String desiredDay = null; 
        int minC, maxC;     //min & max characters allowable for user input
        try {             
            if (inputStyle == 1) {
                minC = 8;
                maxC = 10;   
            }
            else  {  //inputStyle == 2
                minC = 1;
                maxC = 2;   
            }
            desiredDay = br.readLine();
            while ( (desiredDay.length() < minC) || (desiredDay.length() > maxC) ) {
                System.out.print (">");  //user prompt   
                desiredDay = br.readLine(); 
            }
        } catch (IOException ioe) { 
           System.out.println("error reading user input"); 
           System.exit(1); 
        } 
                  
        cal.clear();    // clear all the fields in cal
        
        if ( inputStyle == 1 ) {
            String year_s=null, month_s=null, date_s=null;
            int year, month, date;
            st = new StringTokenizer ( desiredDay, "-");  //read the MM-DD-YYYY format
    		if (st.hasMoreElements())
                month_s = (String) st.nextElement();  
     		if (st.hasMoreElements())
                date_s = (String) st.nextElement();
    		if (st.hasMoreElements())
                year_s = (String) st.nextElement();
            year = Integer.parseInt ( year_s );
            month = Integer.parseInt ( month_s ) - 1;  //MONTH goes from 0-11
            date = Integer.parseInt ( date_s );
            cal.set (year, month, date);           
        }
        else if ( inputStyle == 2 ) {
            int dayOffset = Integer.parseInt (desiredDay) - 1;
            cal.setTimeInMillis(earliestTs*1000);  //convert seconds to millis     
            cal.add (Calendar.DATE, dayOffset);
            //cal.clear ( Calendar.HOUR_OF_DAY ); // this didn't seem to work (?) so I used cal.set() below
            cal.clear ( Calendar.MINUTE );  // keep the date, but clear the time.
            cal.clear ( Calendar.SECOND );
            cal.set ( Calendar.HOUR_OF_DAY, 0 );
        }
        long dayTs = cal.getTimeInMillis() / 1000;  //beginning of selected day in timestamp format
        if ( (dayTs + 86400) > earliestTs && dayTs <= latestTs )  //check for valid user selection
                // add 86400 (1 day) on the first check, otherwise it will be outside the range.
                return ( dayTs );  //used to filter 1 day of alarms, beginning with this timestamp
        else {
            System.out.println ("selected day is not found in the logfile.  exiting...");
            System.exit(1);
        }
        return ( -1 );  //should not get here
	}
	
}
