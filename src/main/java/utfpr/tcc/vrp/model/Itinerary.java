package utfpr.tcc.vrp.model;

import java.util.List;

public class Itinerary {

	private double distance;
	private double travelTime;	
	private Node fromNode;
	private Node toNode;	
	private List<String> instructions;
	private List<String> types;
	
	public Itinerary() {
		
	}
	
	public Itinerary(double distance, double travelTime, Node fromNode, Node toNode, List<String> types, List<String> instructions) {
		this.distance = distance;
		this.travelTime = travelTime;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.types = types;
		this.instructions = instructions;
	}	

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<String> instructions) {
		this.instructions = instructions;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> type) {
		this.types = type;
	}
	
	
}
