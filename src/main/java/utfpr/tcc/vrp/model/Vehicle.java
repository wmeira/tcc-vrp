package utfpr.tcc.vrp.model;

import org.jdom2.Element;

public class Vehicle {

	private String name;
	private double volumeCapacity;
	private double weightCapacity;
	
		
	public Vehicle(String name, double volumeCapacity, double weightCapacity) {
		this.name = name;
		this.volumeCapacity = volumeCapacity;
		this.weightCapacity = weightCapacity;
	}
	
	public Vehicle(Element eVehicle) {
		this.name = eVehicle.getAttributeValue("name");
		this.volumeCapacity = Double.parseDouble(eVehicle.getAttributeValue("volume_capacity"));
		this.weightCapacity = Double.parseDouble(eVehicle.getAttributeValue("weight_capacity"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public double getVolumeCapacity() {
		return volumeCapacity;
	}


	public void setVolumeCapacity(double volumeCapacity) {
		this.volumeCapacity = volumeCapacity;
	}
	
	public String getVolumeCapacityString() {
		String volume = Double.toString(volumeCapacity).replace('.', ',');
		return volume;
	}

	public double getWeightCapacity() {
		return weightCapacity;
	}
	
	public String getWeightCapacityString() {
		String weight = Double.toString(weightCapacity).replace('.', ',');
		return weight;
	}

	public void setWeightCapacity(double weightCapacity) {
		this.weightCapacity = weightCapacity;
	}
	
	public Element getJDomElement() {
		Element eVehicle = new Element("vehicle");
		
		eVehicle.setAttribute("name", name);
		eVehicle.setAttribute("weight_capacity", Double.toString(weightCapacity));
		eVehicle.setAttribute("volume_capacity", Double.toString(volumeCapacity));
		
		return eVehicle;
	}
	
	public Object[] getRowVehicle() {
		Object[] obj = new Object[] {name, getWeightCapacityString(), getVolumeCapacityString()};
  		return obj;		
	}
	
	
}
