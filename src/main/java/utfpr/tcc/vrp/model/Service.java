package utfpr.tcc.vrp.model;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;



public abstract class Service implements Node {	
	/**
	 * Client name.
	 */
	private String client;
	
	/**
	 * Location of the delivery (Address, Longitude and Latitude).
	 */
	private Location location;
	
	/**
	 * List of periods of the day that the client is available to receive/send the product.
	 */
	private List<TimeWindow> availabilities; 
	
	/**
	 * Product characteristics: weight and volume.
	 */
	private Product product;
	
	public Service(String client, Location location, List<TimeWindow> availability, Product product) {
		this.client = client;
		this.location = location;
		this.availabilities = availability;
		this.product = product;		
	}
	
	public Service(Element eService) {
		this.client = eService.getAttributeValue("name");
		Element eLocation = eService.getChild("location");
		this.location = new Location(eLocation);
		
		Element eProduct = eService.getChild("product");
		this.product = new Product(eProduct);
		
		Element eAvailabilities = eService.getChild("availabilities");
		this.availabilities = new ArrayList<TimeWindow>();
		for(Element eAvailability : eAvailabilities.getChildren()) {
			this.availabilities.add(new TimeWindow(eAvailability));			
		}
	}
	
	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<TimeWindow> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<TimeWindow> availability) {
		this.availabilities = availability;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	public Element getJDomElement(String serviceType) {
		Element eService = new Element(serviceType);
		eService.setAttribute("name", client);
		eService.addContent(location.getJDomElement());
		eService.addContent(product.getJDomElement());
		Element eAvailabilities = new Element("availabilities");
		for(TimeWindow timeWindow : availabilities) {
			eAvailabilities.addContent(timeWindow.getJDomElement());			
		}
		eService.addContent(eAvailabilities);
		
		return eService;
	}
	
	public String getAvailabilityText() {
		String availability = "";
		
		for(TimeWindow a: availabilities) {
			availability += a.getInitialTimeFormated() + "-" + a.getFinalTimeFormated() + " ";
		}
		
		return availability.trim();
	}
	 
	public Object[] getRowService() {
		Object[] obj = new Object[] {client, location.getAddress(), product.getWeightString(), product.getVolumeString(), getAvailabilityText()};
  		return obj;		
	}
	
	@Override
	public String toString() {
		return client;
	}
	
	public boolean isInsideTimeWindows(String textHourTime) {
		String time[] = textHourTime.split(":");
		int minutes = Integer.parseInt(time[0])*60 + Integer.parseInt(time[1]);
		
		
		for(TimeWindow tw : availabilities) {
			int initialMinutes = tw.getInitialTimeMinutes();
			int finalMinutes = tw.getFinalTimeMinutes();
			
			if(minutes >= initialMinutes && minutes <= finalMinutes) {
				return true;
			}			
		}
		
		return false;
	}
	
	public void organizeAvailabilities(TimeWindow depositTW) {
		int depositOpen = depositTW.getInitialTimeMinutes();
		int depositClose = depositTW.getFinalTimeMinutes();
		bubbleSortTimeWindows();
		
		TimeWindow tw = null; 
		for(int i = 0; i < availabilities.size(); i++) {
			tw = availabilities.get(i);
			if(tw.getInitialTimeMinutes() >= depositOpen && tw.getInitialTimeMinutes() < depositClose) {
				if(tw.getFinalTimeMinutes() > depositClose) {
					tw.setFinalTimeMinutes(depositClose);
				}
				break;
			} else if(tw.getFinalTimeMinutes() > depositOpen && tw.getFinalTimeMinutes() < depositClose) {
				tw.setInitialTimeMinutes(depositOpen);
				break;
			} else {
				tw = null;
				availabilities.remove(i);
				i--;
			}			
		}
		
		if(tw == null) {
			availabilities.add(new TimeWindow(depositTW.getInitialTimeFormated(), depositTW.getInitialTimeFormated())); //Insert one tw with 0 minutes tw.  
			return; //All time windows were invalid and removed.
		}

		for(int i = 1; i < availabilities.size(); i++) {
			if(availabilities.get(i).getInitialTimeMinutes() > tw.getFinalTimeMinutes()) {
				if(availabilities.get(i).getInitialTimeMinutes() > depositClose) {
					availabilities.remove(i);
					i--;
				} else {
					tw = availabilities.get(i);
					if(availabilities.get(i).getFinalTimeMinutes() > depositClose) {
						tw.setFinalTimeMinutes(depositClose);
					}					
				}				
			} else {
				if(availabilities.get(i).getFinalTimeMinutes() > tw.getFinalTimeMinutes()) {
					if(availabilities.get(i).getFinalTimeMinutes() > depositClose) {
						tw.setFinalTimeMinutes(depositClose);
					} else {
						tw.setFinalTimeMinutes(availabilities.get(i).getFinalTimeMinutes());
					}					
				}
				availabilities.remove(i);
				i--;
			}
 		}
	}
	
	private void bubbleSortTimeWindows() {
		
		if(availabilities.size() < 2) {
			return;
		}
		
		boolean swapped = true;
		int lenght = availabilities.size();
		TimeWindow timeWindow; 
		
		while(swapped) {
			swapped = false;
			for(int i = 1; i < lenght; i++) {
				if(availabilities.get(i-1).getInitialTimeMinutes() > availabilities.get(i).getInitialTimeMinutes()) {
					timeWindow = availabilities.get(i-1);
					availabilities.set(i-1, availabilities.get(i));
					availabilities.set(i, timeWindow);
					swapped = true;
				}
			}	
			lenght--;
		}		
	}
}
	
