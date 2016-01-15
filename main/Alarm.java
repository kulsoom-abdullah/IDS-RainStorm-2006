
public class Alarm {
	private String timestamp;
	private String zone;
	private String badIP;
	private String type;
	private String detail;
	private String thresholdValue;
	private String victimIP;
	private String port;
	private String alarmTitle;
	private String CI;
	private boolean on;//for flagging alarms to be filtered from drawing
	
	/** Change */
	float x = 0f;
	float y = 0f;
	
	/** Change, if both of these wind up true, then these
	 * are the IP's we are having trouble with. I check these
	 * in calculateAlarms method in Renderer */
	boolean ipoff1 = false;
	boolean ipoff2 = false;
	
	public Alarm(){
		timestamp=null;
		zone=null;
		badIP=null;
		type=null;
		detail=null;
		thresholdValue=null;
		victimIP=null;
		port=null;
		alarmTitle=null;
		CI=null;
	}
	
	public Alarm(String _timestamp, String _zone, String _badIP, String _type, String _detail, String _thresholdValue, String _victimIP, String _port, String _alarmTitle, String _ci, boolean _on) {
		this.timestamp = _timestamp;
		this.zone = _zone;
		this.badIP = _badIP;
		this.type = _type;
		this.detail = _detail;
		this.thresholdValue = _thresholdValue;
		this.victimIP = _victimIP;
		this.port = _port;
		this.alarmTitle = _alarmTitle;
		this.CI = _ci;
		on=true;
		processAlarm();
	}
	public void processAlarm() {
		String[] tmpDetail;
		String _port;
		if(detail.indexOf("from")!= -1){//if "from" there,that ip is bad, bad becomes victim
			this.victimIP = this.badIP; //bad becomes victim
			tmpDetail = filterIP(detail);
			this.badIP = tmpDetail[0];
			if(tmpDetail[1]!=null)
				this.alarmTitle = tmpDetail[1].trim();
		}
		
		//Check for Port in 6th Column
		//If port, move to 9th column, otherwise make 9th col "0"
		if(detail.indexOf("tcp")!=-1){
			_port = filterPort(detail,"tcp");
			this.port = _port;
		}
		else if(detail.indexOf("udp")!=-1){
			_port = filterPort(detail,"udp");
			this.port = _port;
		}
		else{
			this.port = "0";
		}
	}

	private String[] filterIP(String entry){
		String ip = entry.substring(entry.indexOf("from")+5);
		String text = null;
		if(ip.indexOf(" ")!=-1){
			ip = ip.substring(0,ip.indexOf(" "));
			text=ip.substring(ip.indexOf(" ")+1,ip.length());
		}
		//text after IP
		else{
		try{
			text = entry.substring(entry.indexOf(ip)+ip.length());
			text=text.substring(0,text.length());
		}catch(Exception e){
			//means that there is no text after the IP
			text = null;
		}
		}//end else
		String[] both = {ip,text};
		return both;
		
	}
	private String filterPort(String detail, String type) {
		String port = detail.substring(detail.indexOf(type)+type.length()+1);
		if(port.indexOf(" ")!=-1){
			port=port.substring(0,port.indexOf(" "));
		}
		return port;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getAlarmTitle() {
		return alarmTitle;
	}
	public void setAlarmTitle(String alarmTitle) {
		this.alarmTitle = alarmTitle;
	}
	public String getBadIP() {
		return badIP;
	}
	public void setBadIP(String badIP) {
		this.badIP = badIP;
	}
	public String getCI() {
		return CI;
	}
	public void setCI(String ci) {
		CI = ci;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVictimIP() {
		return victimIP;
	}
	public void setVictimIP(String victimIP) {
		this.victimIP = victimIP;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public boolean getOnBoolean() {
		return on;
	}
	
	public void setOnBoolean(boolean on) {
		this.on=on;
	}
	
	public String toString(){
		String ret = "";
		ret+="Timestamp:\t\t"+timestamp+"\n";
		ret+="Zone:\t\t\t"+zone+"\n";
		ret+="BadIP:\t\t\t"+badIP+"\n";
		ret+="Type:\t\t\t"+type+"\n";
		ret+="Detail:\t\t\t"+detail+"\n";
		ret+="Threshold Value:\t"+thresholdValue+"\n";
		ret+="VictimIP:\t\t"+victimIP+"\n";
		ret+="Port:\t\t\t"+port+"\n";
		ret+="Alarm Title:\t\t"+alarmTitle+"\n";
		ret+="CI:\t\t\t"+CI+"\n";
		ret+="on:\t\t\t"+on+"\n";
		return ret;		
	}
}

