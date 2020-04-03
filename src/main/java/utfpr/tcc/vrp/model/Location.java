package utfpr.tcc.vrp.model;

import org.jdom2.Element;

public class Location {
	
	private String address;
	private double latitude;
	private double longitude;
	
	public Location(String address, double latitude, double longitude) {
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Location(Element eLocation) {
		this.address = eLocation.getAttributeValue("address");
		this.latitude = Double.parseDouble(eLocation.getAttributeValue("latitude"));
		this.longitude = Double.parseDouble(eLocation.getAttributeValue("longitude"));		
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public Element getJDomElement() {
		Element eLocation = new Element("location");
		
		eLocation.setAttribute("address", this.address);
		eLocation.setAttribute("latitude", Double.toString(this.latitude));
		eLocation.setAttribute("longitude", Double.toString(this.longitude));
		
		return eLocation;
	}
	
	
	
}
