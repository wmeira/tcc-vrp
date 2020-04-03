package utfpr.tcc.vrp.model;

import org.jdom2.Element;

public class Deposit implements Node {

	/**
	 * Name of the deposit. 
	 */
	private String name;
	
	/**
	 * Location of the deposit. 
	 */
	private Location location; //Address, Latitude, Longitude.
	
	/**
	 * Operation time of the deposit. When it opens and closes.
	 */
	private TimeWindow operationTime;
	
	public Deposit(String name, Location location, TimeWindow operationTime) {
		this.name = name;
		this.location = location;
		this.operationTime = operationTime;
	}
	
	public Deposit(Element eDeposit) {
		this.name = eDeposit.getAttributeValue("name");
		Element eLocation = eDeposit.getChild("location");
		this.location = new Location(eLocation);
		Element eAvailability = eDeposit.getChild("availability");
		this.operationTime = new TimeWindow(eAvailability);		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public TimeWindow getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(TimeWindow operationTime) {
		this.operationTime = operationTime;
	}
	
	public Element getJDomElement() {
		Element eDeposit = new Element("deposit");
		
		eDeposit.setAttribute("name", name);		
		eDeposit.addContent(location.getJDomElement());
		eDeposit.addContent(operationTime.getJDomElement());
		
		return eDeposit;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean isInsideTimeWindows(String textHoutTime) {
		String time[] = textHoutTime.split(":");
		int minutes = Integer.parseInt(time[0])*60 + Integer.parseInt(time[1]);
		int initialMinutes = operationTime.getInitialTimeMinutes();
		int finalMinutes = operationTime.getFinalTimeMinutes();
		
		if(minutes >= initialMinutes && minutes <= finalMinutes) {
			return true;
		}
		
		return false;
	}
}
