package utfpr.tcc.vrp.model;

import java.util.List;

import org.jdom2.Element;



public class Delivery extends Service {
	
	public Delivery(String client, Location location, List<TimeWindow> availability, Product product) {
		super(client, location, availability, product);
	}	
	
	public Delivery(Element eDelivery) {
		super(eDelivery);
	}
	
	public Element getJDomElement() {
		Element eDelivery = getJDomElement("delivery");
		return eDelivery;
	}
	
}
