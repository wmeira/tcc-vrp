package utfpr.tcc.vrp.model;

import java.util.List;

import org.jdom2.Element;



public class Pickup extends Service {
	
	public Pickup(String client, Location location, List<TimeWindow> availability, Product product) {
		super(client, location, availability, product);
	}
	
	public Pickup(Element ePickup) {
		super(ePickup);
	}

	public Element getJDomElement() {
		Element ePickup = getJDomElement("pickup");
		return ePickup;
	}
}
