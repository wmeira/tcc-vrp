 package utfpr.tcc.vrp.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import utfpr.tcc.vrp.prop.Path;
import utfpr.tcc.vrp.service.BingMapsRestServices;

public class Scenario {
	
	private final static Logger logger = Logger.getLogger(Scenario.class.getName());	
	
	public static final boolean SAVE_ERROR = false;
	public static final boolean SAVE_SUCCESS = true;
	
	private final double MAX_DISTANCE = 350; //in kms. +8hours to go and back (travelTime)
	
	private String name;
	private String file = null;
	private Calendar date;
	private Deposit deposit;
	private List<Delivery> deliveries;
	private List<Pickup> pickups;
	private List<Vehicle> vehicles;	
	private List<List<Itinerary>> itineraryMatrix;
	
	public Scenario() {
		vehicles = new ArrayList<Vehicle>();
		deliveries = new ArrayList<Delivery>();
		pickups = new ArrayList<Pickup>();

		itineraryMatrix = new ArrayList<List<Itinerary>>();
	}
	
	public Scenario(String name, String dateS, Deposit deposit, List<Vehicle> vehicles, List<Delivery> deliveries, List<Pickup> pickups) {
		this.name = name;
		setDate(dateS);
		this.deposit = deposit;
		this.vehicles = vehicles;
		this.deliveries = deliveries;
		this.pickups = pickups; 		
		itineraryMatrix = new ArrayList<List<Itinerary>>();
 	}
	
	public Scenario(String name, Calendar date, Deposit deposit, List<Vehicle> vehicles, List<Delivery> deliveries, List<Pickup> pickups) {
		this.name = name;
		this.date = date;
		this.deposit = deposit;
		this.vehicles = vehicles;
		this.deliveries = deliveries;
		this.pickups = pickups; 
		itineraryMatrix = new ArrayList<List<Itinerary>>();
	}
	
	public Scenario(Element eScenario, String file) {
		
		this.file = file;
		
		this.name = eScenario.getAttributeValue("name");
		setDate(eScenario.getAttributeValue("date"));
		
		Element eDeposit = eScenario.getChild("deposit");
		this.deposit = new Deposit(eDeposit);
		
		Element eVehicles = eScenario.getChild("vehicles");
		this.vehicles = new ArrayList<Vehicle>();
		for(Element eVehicle : eVehicles.getChildren()) {
			this.vehicles.add(new Vehicle(eVehicle));
		}
		
		Element eDeliveries = eScenario.getChild("deliveries");
		this.deliveries = new ArrayList<Delivery>();
		for(Element eDelivery : eDeliveries.getChildren()) {
			this.deliveries.add(new Delivery(eDelivery));
		}
		
		Element ePickups = eScenario.getChild("pickups");
		this.pickups = new ArrayList<Pickup>();
		for(Element ePickup : ePickups.getChildren()) {
			this.pickups.add(new Pickup(ePickup));
		}
		
		itineraryMatrix = new ArrayList<List<Itinerary>>();
		
		Element eItineraryMatrix = eScenario.getChild("itineraryMatrix");
		List<Element> eNodes = eItineraryMatrix.getChildren("node");
		int fromNode = 0;
		int toNode;
		List<Itinerary> itineraries;
		for(Element eNode : eNodes) {		
			itineraries = new ArrayList<Itinerary>();
			List<Element> eItineraries = eNode.getChildren("itinerary");
			toNode = 0;
			for(Element eItinerary : eItineraries) {
				double distance = Double.parseDouble(eItinerary.getAttributeValue("distance"));
				double travelTime = Double.parseDouble(eItinerary.getAttributeValue("travelTime"));
				List<Element> eItineraryItems = eItinerary.getChildren("itineraryItem");
				List<String> types = new ArrayList<String>();
				List<String> instructions = new ArrayList<String>();
				
				for(Element eItineraryItem : eItineraryItems) {
					types.add(eItineraryItem.getAttributeValue("type"));
					instructions.add(eItineraryItem.getAttributeValue("instruction"));
				}
				
				Itinerary itinerary = new Itinerary(distance, travelTime, getNode(fromNode), getNode(toNode), types, instructions);		
				itineraries.add(itinerary);
				toNode++;
			}
			itineraryMatrix.add(itineraries);
			fromNode++;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDateFormated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormated = dateFormat.format(date.getTime());
        return dateFormated;
	}
	
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	/**
	 * Insert the scenario's date using the dd/MM/yyyy format.
	 * 
	 * @param date Date formated: dd/MM/yyyy
	 */
	public void setDate(String dateS) {
		
		String[] splitedDate = dateS.split("\\/| |\\.");
		int year = Integer.parseInt(splitedDate[2].trim());
		int month = Integer.parseInt(splitedDate[1].trim());
		int day = Integer.parseInt(splitedDate[0].trim());		
		this.date = new GregorianCalendar(year, month-1, day);
	}

	public Deposit getDeposit() {
		return deposit;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
	}

	public List<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public List<Pickup> getPickups() {
		return pickups;
	}

	public void setPickups(List<Pickup> pickups) {
		this.pickups = pickups;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}	
	
	public String getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file.toString();
	}
	
	public void setFile(String sfile) {		
		this.file = sfile;
	}
	
	public int getNumberOfNodes() {
		int nodesNumber = 1 + pickups.size() + deliveries.size(); // 1 -> central deposit.
		return nodesNumber;
	}

	public Element getJDomElement() {
		
		Element eScenario = new Element("scenario");
		eScenario.setAttribute("name", name);
		eScenario.setAttribute("date", getDateFormated());
		
		eScenario.addContent(deposit.getJDomElement());
		
		Element eVehicles = new Element("vehicles");
		for(Vehicle vehicle : vehicles) {
			eVehicles.addContent(vehicle.getJDomElement());
		}
		eScenario.addContent(eVehicles);
		
		Element eDeliveries = new Element("deliveries");
		for(Delivery delivery : deliveries) {
			eDeliveries.addContent(delivery.getJDomElement());
		}		
		eScenario.addContent(eDeliveries);
		
		Element ePickups = new Element("pickups");
		for(Pickup pickup : pickups) {
			ePickups.addContent(pickup.getJDomElement());
		}
		eScenario.addContent(ePickups);
		
		Element eItineraryMatrix = new Element("itineraryMatrix");
		
		for(int i = 0; i < getNumberOfNodes(); i++) {
			Element eNode = new Element("node");
			List<Itinerary> itineraries = itineraryMatrix.get(i);
			
			for(int j = 0; j < getNumberOfNodes(); j++) {
				Element eItinerary = new Element("itinerary");
				Itinerary itinerary = itineraries.get(j);
				eItinerary.setAttribute("distance", Double.toString(itinerary.getDistance()));
				eItinerary.setAttribute("travelTime", Double.toString(itinerary.getTravelTime()));
				
				List<String> instructions = itinerary.getInstructions();
				List<String> types = itinerary.getTypes();
				for(int k = 0; k < instructions.size(); k++) {
					Element eItineraryItem = new Element("itineraryItem");
					eItineraryItem.setAttribute("type", types.get(k));
					eItineraryItem.setAttribute("instruction", instructions.get(k));
					eItinerary.addContent(eItineraryItem);
				}
				eNode.addContent(eItinerary);
				
			}
			eItineraryMatrix.addContent(eNode);			
		}
		eScenario.addContent(eItineraryMatrix);
		
		return eScenario;
	}	
	
	public boolean save(boolean isNew) {		
		
		try {
			
			Document doc = new Document();
			Element eScenario = getJDomElement();
			doc.setRootElement(eScenario);
		
			XMLOutputter xmlOutput = new XMLOutputter();			
			
			xmlOutput.setFormat(Format.getPrettyFormat());	
			Path path = Path.getInstance();
			
			String pathNewScenario;
			if(isNew) {
				pathNewScenario = path.getScenarioPath() + name;
				if(checkFileExists(pathNewScenario + ".xml")) {
					pathNewScenario += "(";
					for(int i = 1; ; i++) {
						if(!checkFileExists(pathNewScenario + i + ").xml")) {
							pathNewScenario = pathNewScenario + i + ").xml";
							break;
						}
					}
				} else {
					pathNewScenario += ".xml";
				}
				
			} else {
				pathNewScenario = file;
			}
			FileOutputStream fw = new FileOutputStream(pathNewScenario);
			
			//FileWriter fw = new FileWriter(pathNewScenario);
			xmlOutput.output(doc, fw);	
			File file = new File(pathNewScenario);
			setFile(file.toString());

			fw.close();
			
			return SAVE_SUCCESS;	
			
		} catch (IOException e) {
			logger.severe("Problema de E/S ao salvar cenário.");
			e.printStackTrace();
			return SAVE_ERROR;
		} 
	}

	
	public Object[][] getTableDetails() {
		Object[][] details = new Object[][] { 
				{"Nome", name},
				{"Data", getDateFormated()},
				{"Número de Veículos", vehicles.size()},
				{"Número de Coletas", pickups.size()},
				{"Número de Entregas", deliveries.size()},
				{"Máximo de Janelas de Tempo" , getMaxTimeWindows()}
		};		
		
		return details;
	}
	
	public void createCompleteItineraryMatrix() throws IOException, JDOMException {
		
		if(itineraryMatrix == null) {
			itineraryMatrix = new ArrayList<List<Itinerary>>();
		} else {
			itineraryMatrix.clear();
		}		
		
		List<Itinerary> nodeLine; 		
		int listPosition;
		Location location;
		List<Object> itineraryResult; 				
		double distance;
		double travelTime;
		List<String> instructions;
		List<String> types;	
		//Deposit	
		
		int nodes = getNumberOfNodes();
		for(int n = 0; n < nodes; n++) {
			
			nodeLine = new ArrayList<Itinerary>();
			
			if(n == 0) {
				//Deposit
				location = deposit.getLocation();				
				nodeLine.add(new Itinerary(0, 0, deposit, deposit, new ArrayList<String>(), new ArrayList<String>()));				
				
				for(int i = 0; i < deliveries.size(); i++) {
					itineraryResult = BingMapsRestServices.getItinerary(location, deliveries.get(i).getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1 + i), types, instructions));	
				}				
				for(int i = 0; i < pickups.size(); i++) {
					itineraryResult = BingMapsRestServices.getItinerary(location, pickups.get(i).getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1 + i), types, instructions));	
				}	
				
			} else if(n >= 1 && n <= deliveries.size()) {
				//Delivery
				listPosition = n - 1;
				location = deliveries.get(listPosition).getLocation();
				
				itineraryResult = BingMapsRestServices.getItinerary(location, deposit.getLocation());
				distance = (Double) itineraryResult.get(0);
				travelTime = (Double) itineraryResult.get(1);
				instructions = (List<String>) itineraryResult.get(2);
				types = (List<String>) itineraryResult.get(3);		
				nodeLine.add(new Itinerary(distance, travelTime, getNode(n), deposit, types, instructions));	
				
				for(int i = 0; i < deliveries.size(); i++) {
					if(i ==  listPosition) {
						nodeLine.add(new Itinerary(0, 0, getNode(n), getNode(n), new ArrayList<String>(), new ArrayList<String>()));	
						continue;
					}
					
					itineraryResult = BingMapsRestServices.getItinerary(location, deposit.getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1+i), types, instructions));			
				}
				
				for(int i = 0; i < pickups.size(); i++) {						
					itineraryResult = BingMapsRestServices.getItinerary(location, pickups.get(i).getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1+deliveries.size() + i), types, instructions));	
				}				
				
			} else {
				//Pickup
				listPosition = n - 1 - deliveries.size();
				location = pickups.get(listPosition).getLocation();
				
				itineraryResult = BingMapsRestServices.getItinerary(location, deposit.getLocation());
				distance = (Double) itineraryResult.get(0);
				travelTime = (Double) itineraryResult.get(1);
				instructions = (List<String>) itineraryResult.get(2);
				types = (List<String>) itineraryResult.get(3);		
				nodeLine.add(new Itinerary(distance, travelTime, getNode(n), deposit, types, instructions));
				
				for(int i = 0; i < deliveries.size(); i++) {
					
					itineraryResult = BingMapsRestServices.getItinerary(location, deliveries.get(i).getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1+i), types, instructions));				
				}
				
				for(int i = 0; i < pickups.size(); i++) {	
					if(i ==  listPosition) {
						nodeLine.add(new Itinerary(0, 0, getNode(n), getNode(n), new ArrayList<String>(), new ArrayList<String>()));
						continue;
					}
					itineraryResult = BingMapsRestServices.getItinerary(location, pickups.get(i).getLocation());
					distance = (Double) itineraryResult.get(0);
					travelTime = (Double) itineraryResult.get(1);
					instructions = (List<String>) itineraryResult.get(2);
					types = (List<String>) itineraryResult.get(3);		
					nodeLine.add(new Itinerary(distance, travelTime, getNode(n), getNode(1+deliveries.size()+i), types, instructions));				
				}
			}					
			itineraryMatrix.add(nodeLine);
		}
	}
		
	public void addNodeDistanceTravelTimeMatrices(int node) throws IOException, JDOMException {
		
		int listPosition;
		Location location;
		int numberOfNodes = getNumberOfNodes();
		
		//LINE		
		if(node >= 1 && node <= deliveries.size()) {
			//new delivery
			listPosition = node - 1;
			location = deliveries.get(listPosition).getLocation();
		} else {
			//new pickup                                                                                          
			listPosition = node - 1 - deliveries.size();
			location = pickups.get(listPosition).getLocation();
		}

		List<Itinerary> nodeLine = new ArrayList<Itinerary>(); 
		List<Object> itineraryResult; 
		double distance;
		double travelTime;
		List<String> instructions;
		List<String> types ;
		
		if(node == 0) {
			nodeLine.add(new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
		} else {
			itineraryResult = BingMapsRestServices.getItinerary(location, deposit.getLocation()); 		
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);		
			nodeLine.add(new Itinerary(distance, travelTime, getNode(node), deposit, types, instructions));
		}

		for(int i = 0; i < deliveries.size(); i++) {		
			if(node == i+1) {
				nodeLine.add(new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
				continue;
			}
			itineraryResult = BingMapsRestServices.getItinerary(location, deliveries.get(i).getLocation());
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);		
			nodeLine.add(new Itinerary(distance, travelTime, getNode(node), getNode(1 + i), types, instructions));				
		}
			
		for(int i = 0; i < pickups.size(); i++) {	
			if(node == i+1+deliveries.size()) {
				nodeLine.add(new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
				continue;				
			}
			itineraryResult = BingMapsRestServices.getItinerary(location, pickups.get(i).getLocation());
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);		
			nodeLine.add(new Itinerary(distance, travelTime, getNode(node), getNode(1 + deliveries.size() + i), types, instructions));				
		}
		
		itineraryMatrix.add(node, nodeLine);
		
		//COLUMN
		
		for(int n = 0; n < numberOfNodes; n++) {
			
			if(n == node) {
				continue;
			}
			
			nodeLine = itineraryMatrix.get(n);
			
			if(n == 0) {
				itineraryResult = BingMapsRestServices.getItinerary(deposit.getLocation(), location);				
			} else if(n >= 1 && n <= deliveries.size()) {
				//new delivery
				listPosition = n - 1;
				itineraryResult = BingMapsRestServices.getItinerary(deliveries.get(listPosition).getLocation(), location);				
			} else {
				//new pickup
				listPosition = n - 1 - deliveries.size();
				itineraryResult = BingMapsRestServices.getItinerary(pickups.get(listPosition).getLocation(), location);			
			}
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);					
			nodeLine.add(node, new Itinerary(distance, travelTime, getNode(n), getNode(node), types, instructions));
		}		
	}
	
	public void editNodeDistanceTravelTimeMatrices(int node) throws IOException, JDOMException {
		
		List<Itinerary> nodeLine = itineraryMatrix.get(node); 
		int listPosition = -1;
		Location location;
		int numberOfNodes = getNumberOfNodes();
		
		//LINE 		
		if(node == 0) {
			listPosition = 0;
			location = deposit.getLocation();
		} else if(node >= 1 && node <= deliveries.size()) {
			//new delivery
			listPosition = node - 1;
			location = deliveries.get(listPosition).getLocation();
		} else {
			//new pickup
			listPosition = node - 1 - deliveries.size();
			location = pickups.get(listPosition).getLocation();
		}
		
		List<Object> itineraryResult;
		double distance;
		double travelTime;
		List<String> instructions;
		List<String> types;
		
		if(node == 0) {
			nodeLine.set(0, new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
		} else {
			itineraryResult = BingMapsRestServices.getItinerary(location, deposit.getLocation());
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);					
			nodeLine.set(0, new Itinerary(distance, travelTime, getNode(node), deposit, types, instructions));			
		}

		
		for(int i = 0; i < deliveries.size(); i++) {
			if(node == (i+1)) {
				nodeLine.set(1+i, new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
				continue;
			}
			itineraryResult = BingMapsRestServices.getItinerary(location, deliveries.get(i).getLocation());
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);					
			nodeLine.add(1+i, new Itinerary(distance, travelTime, getNode(node), getNode(i+1), types, instructions));			
		}
			
		for(int i = 0; i < pickups.size(); i++) {
			if(node == (i+ deliveries.size() + 1)) {
				nodeLine.set(i+deliveries.size()+1, new Itinerary(0, 0, getNode(node), getNode(node), new ArrayList<String>(), new ArrayList<String>()));
				continue;
			}
			
			itineraryResult = BingMapsRestServices.getItinerary(location, pickups.get(i).getLocation());
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);					
			nodeLine.add(1+i+deliveries.size(), new Itinerary(distance, travelTime, getNode(node), 
					getNode(i+ deliveries.size() + 1), types, instructions));						
		}
		
		//COLUMN
		
		for(int n = 0; n < numberOfNodes; n++) {
			
			if(n==node) {
				continue;
			}
			
			nodeLine = itineraryMatrix.get(n);
			
			if(n == 0) {
				itineraryResult = BingMapsRestServices.getItinerary(deposit.getLocation(), location);													
			} else if(n >= 1 && n <= deliveries.size()) {
				//new delivery
				listPosition = n - 1;
				itineraryResult = BingMapsRestServices.getItinerary(deliveries.get(listPosition).getLocation(), location);
				
			} else {
				//new pickup
				listPosition = n - 1 - deliveries.size();
				itineraryResult = BingMapsRestServices.getItinerary(pickups.get(listPosition).getLocation(), location);
			}
			
			distance = (Double) itineraryResult.get(0);
			travelTime = (Double) itineraryResult.get(1);
			instructions = (List<String>) itineraryResult.get(2);
			types = (List<String>) itineraryResult.get(3);	
			nodeLine.set(node, new Itinerary(distance, travelTime, getNode(n), getNode(node),types, instructions));
		}		
	}
	
	public void deleteNodeDistanceTravelTimeMatrices(int node) {
		if(node >= getNumberOfNodes()) {
			return;
		}
		
		itineraryMatrix.remove(node);
		List<Itinerary> line;
		for(int i = 0; i < itineraryMatrix.size(); i++) {
			line = itineraryMatrix.get(i);
			line.remove(node);
		}
	}

	public int getNodePosition(Node node) {
		int index;
		try{
			if(node instanceof Deposit) {
				index = 0;
			}
			else if(node instanceof Delivery) {
				index = 1 + deliveries.indexOf(node);				
			} else {
				index = 1 + deliveries.size() + pickups.indexOf(node);
			}
		} catch(IndexOutOfBoundsException e) {
			index = -1;
		}		
		
		return index;
	}
	
	public Node getNode(int index) {
		
		Node node = null;
		try {
			if(index == 0) {
				node = deposit;
			} else if(index >= 1 && index <= deliveries.size()) {
				node = deliveries.get(index-1);
			} else {
				node = pickups.get(index - 1 - deliveries.size());
			}			
		} 
		catch(IndexOutOfBoundsException e) {
			node = null;
		}		
		
		return node;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public List<List<Itinerary>> getItineraryMatrix() {
		return itineraryMatrix;
	}

	public void setItineraryMatrix(List<List<Itinerary>> itineraryMatrix) {
		this.itineraryMatrix = itineraryMatrix;
	}

	private boolean checkFileExists(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	public int getMaxTimeWindows() {
		
		int maxTW = 1;
		for(Delivery delivery : getDeliveries()) {
			if(maxTW < delivery.getAvailabilities().size()) {
				maxTW = delivery.getAvailabilities().size();
			}
		}
		for(Pickup pickup : getPickups()) {
			if(maxTW < pickup.getAvailabilities().size()) {
				maxTW = pickup.getAvailabilities().size();
			}
		}	
		
		return maxTW;
	}
	
	public Itinerary getItinerary(Node fromNode, Node toNode) {
		
		return itineraryMatrix.get(getNodePosition(fromNode)).get(getNodePosition(toNode));
	}

	public List<String> getScenarioWarnings() {
		List<String> warnings = new ArrayList<String>();
		
		double maxWeightVehicle = 0;
		double maxVolumeVehicle = 0;
		double totalWeightVehicles = 0;
		double totalVolumeVehicles = 0;
		double totalDeliveryWeight = 0;
		double totalDeliveryVolume = 0;
		double totalPickupWeight = 0;
		double totalPickupVolume = 0;
		
		boolean twValid;
		
		for(Vehicle v : vehicles) {
			if(maxWeightVehicle < v.getWeightCapacity()) {
				maxWeightVehicle = v.getWeightCapacity();
			}
			
			if(maxVolumeVehicle < v.getVolumeCapacity()) {
				maxVolumeVehicle = v.getVolumeCapacity();
			}
			totalWeightVehicles += v.getWeightCapacity();
			totalVolumeVehicles += v.getVolumeCapacity();
		}
		
		for(Delivery d : deliveries) {
			if(d.getProduct().getWeight() > maxWeightVehicle) {
				warnings.add("ENTREGA do cliente '" + d.getClient() + "' está acima do PESO máximo de todos os veículos do cenário.");
			}
			
			if(d.getProduct().getVolume() > maxVolumeVehicle) {
				warnings.add("ENTREGA do cliente '" + d.getClient() + "\' está acima do VOLUME máximo de todos os veículos do cenário.");
			}
			
			twValid = false;
			for(TimeWindow tw : d.getAvailabilities()) {
				if((tw.getFinalTimeMinutes() - tw.getInitialTimeMinutes()) > 0) {
					twValid = true;
					break;
				}
			}
			if(!twValid) {
				warnings.add("ENTREGA do cliente '" + d.getClient() + "' possui janelas de disponibilidade mal " +
						"formuladas ou fora do horário de trabalho do depósito." );
			}
			
			totalDeliveryWeight += d.getProduct().getWeight();
			totalDeliveryVolume += d.getProduct().getVolume();
		}
		
		for(Pickup p : pickups) {
			if(p.getProduct().getWeight() > maxWeightVehicle) {
				warnings.add("COLETA do cliente '" + p.getClient() + "' está acima do PESO máximo de todos os veículos do cenário.");
			}
			
			if(p.getProduct().getVolume() > maxVolumeVehicle) {
				warnings.add("COLETA do cliente '" + p.getClient() + "' está acima do VOLUME máximo de todos os veículos do cenário.");
			}
			
			totalPickupWeight += p.getProduct().getWeight();
			totalPickupVolume += p.getProduct().getVolume();
		}
		
		if(totalWeightVehicles < totalDeliveryWeight) {
			warnings.add("A quantidade em PESO das ENTREGAS supera o máximo possível para os veículos disponíveis.");
		}
		
		if(totalVolumeVehicles < totalDeliveryVolume) {
			warnings.add("A quantidade em VOLUME das ENTREGAS supera o máximo possível para os veículos disponíveis.");
		}
		
		if(totalWeightVehicles < totalPickupWeight) {
			warnings.add("A quantidade em PESO das COLETAS supera o máximo possível para os veículos disponíveis.");
		}
		
		if(totalVolumeVehicles < totalPickupVolume) {
			warnings.add("A quantidade em VOLUME das COLETAS supera o máximo possível para os veículos disponíveis.");
		}
		
		for(int i = 0; i < getNumberOfNodes(); i++) {
			if(itineraryMatrix.get(0).get(i).getDistance() > MAX_DISTANCE) {
				warnings.add("O cliente '" + getNode(i).toString() + "' está muito longe do depósito (" + 
						itineraryMatrix.get(0).get(i).getDistance()  + " km). O endereço pode estar incorreto. ");
			}
		}
		return warnings;
	}
	
}
