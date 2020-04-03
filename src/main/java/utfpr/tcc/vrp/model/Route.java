package utfpr.tcc.vrp.model;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import utfpr.tcc.vrp.view.RoutePanel;

public class Route {

	private Vehicle vehicle;	
	private Deposit deposit;
	
	private double depositWeightDeparture;
	private double depositWeightReturn;
	
	private double depositVolumeDeparture;
	private double depositVolumeReturn;	
	
	private List<Integer> arrivalTimes;	
	private List<Integer> departureTimes;
	private List<Service> services;
	
	private List<Double> weightRoute;
	private List<Double> volumeRoute;
	private List<Double> traveledDistance;
	
	private double totalTimeTraveled;
	private double totalDistanceTraveled;
	
	public Route() {
		
	}
		
	public Route(Vehicle vehicle, 
					Deposit deposit, 
					List<Service> services, 
					List<Integer> arrivalTimes,
					List<Integer> departureTimes,
					List<Double> traveledDistance,
					List<Double> weightRoute,
					List<Double> volumeRoute) {
		
		this.vehicle = vehicle;
		this.deposit = deposit;		
		this.services = services;
		
		this.depositWeightDeparture = weightRoute.get(0);
		this.depositWeightReturn =  weightRoute.get(weightRoute.size()-1);
		
		weightRoute.remove(0);
		
		this.depositVolumeDeparture = volumeRoute.get(0);
		this.depositVolumeReturn = volumeRoute.get(volumeRoute.size()-1);
		
		volumeRoute.remove(0);
		
		this.weightRoute = weightRoute;
		this.volumeRoute = volumeRoute;
		
		//Ordered service list.  
		this.arrivalTimes = arrivalTimes;	
		this.departureTimes = departureTimes;
		
		this.traveledDistance = traveledDistance;
		
		//calculateWeightDuringRoute();
		//calculateVolumeDuringRoute();	
		
		this.totalTimeTraveled = arrivalTimes.get(arrivalTimes.size() - 1);
		this.totalDistanceTraveled = traveledDistance.get(traveledDistance.size() - 1);
	}
	
	private void calculateWeightDuringRoute() {
		weightRoute = new ArrayList<Double>();
		
		double weight = depositWeightDeparture;
		
		for(int i = 0; i < services.size(); i++) {
			Service s = services.get(i);
			if(s instanceof Delivery) {
				weight = weight - services.get(i).getProduct().getWeight();
			} else {
				weight = weight + services.get(i).getProduct().getWeight();
			}
			weightRoute.add(weight);			
		}		
	}
	
	private void calculateVolumeDuringRoute() {
		volumeRoute = new ArrayList<Double>();
		
		double volume = depositVolumeDeparture;
		
		for(int i = 0; i < services.size(); i++) {
			Service s = services.get(i);
			if(s instanceof Delivery) {
				volume = volume - services.get(i).getProduct().getVolume();
			} else {
				volume = volume + services.get(i).getProduct().getVolume();
			}
			volumeRoute.add(volume);			
		}	
	}
	
	public List<Point2D.Double> getWayPointList() {
		List<Point2D.Double> wayPoints = new ArrayList<Point2D.Double>();
		
		wayPoints.add(new Point2D.Double(deposit.getLocation().getLatitude(), deposit.getLocation().getLongitude()));
		
		for(Service service : services) {
			wayPoints.add(new Point2D.Double(service.getLocation().getLatitude(), service.getLocation().getLongitude()));
		}
		
		wayPoints.add(new Point2D.Double(deposit.getLocation().getLatitude(), deposit.getLocation().getLongitude()));

		return wayPoints;
	}
	
	public Object[][] getTableRoute() {
		
		int numberOfRows = 2 + services.size(); //2 times deposit. Start - end.
		int numberOfColumns = RoutePanel.COLUMN_IDENTIFIERS_ROUTE.length;
		char id = 'A';		
		Object[][] obj = new Object[numberOfRows][numberOfColumns];
		Object[] line;
		/*TABLE_ID_COLUMN, 
		TABLE_CLIENTE_COLUMN,
		TABLE_TIPO_COLUMN,
		TABLE_ARRIVAL_TIME_COLUMN,
		TABLE_DEPARTURE_TIME_COLUMN,
		TABLE_WEIGHT_COLUMN,
		TABLE_VOLUME_COLUMN*/
		
		line = new Object[] { id, "-", "Depósito", 
				"-", 
				minutesToTimeFormatted(departureTimes.get(0)), 
				doubleToBrazilianString(depositWeightDeparture), 
				doubleToBrazilianString(depositVolumeDeparture),
				"<html><body>" +
				"<b>Depósito Central</b>" +
				"<br>Endereço: " + deposit.getLocation().getAddress() + 
				"<br>Horário de Abertura: " + deposit.getOperationTime().getInitialTimeFormated() +
				"<br>Horário de Fechamento: " + deposit.getOperationTime().getFinalTimeFormated() + 
				"</body></html>"
		};		
		obj[0] = line;
		id++;
		
		for(int i = 0; i < services.size(); i++) {
			Service service = services.get(i);
			line = new Object[numberOfColumns];
			line[0] = id;
			line[1] = service.getClient();
			if(service instanceof Delivery) line[2] = "Entrega";
			else line[2] = "Coleta";
			line[3] = minutesToTimeFormatted(arrivalTimes.get(i));
			line[4] = minutesToTimeFormatted(departureTimes.get(i+1));
			line[5] = doubleToBrazilianString(weightRoute.get(i));
			line[6] = doubleToBrazilianString(volumeRoute.get(i));
			String tooltip = "<html><body>" +
					  "<b>" + service.getClient() + "</b><i> (" + line[2] + ")</i>" +
					  "<br>Endereço: " + service.getLocation().getAddress() + 
					  "<br>Mercadoria: " + service.getProduct().getWeightString() + "kg" + 
					  " | " + service.getProduct().getVolumeString() + "m³" +
					  "<br>Disponibilidade: " + service.getAvailabilityText() + 
					  "</body></html>";
			line[7] = tooltip;
			obj[i+1] = line;
			id++;
		}
		
		line = new Object[] { id, "-", "Depósito", 
				minutesToTimeFormatted(arrivalTimes.get(arrivalTimes.size()-1)), 
				"-", 
				doubleToBrazilianString(depositWeightReturn), 
				doubleToBrazilianString(depositVolumeReturn),
				"<html><body>" +
				"<b>Depósito Central</b>" +
				"<br>Endereço: " + deposit.getLocation().getAddress() + 
				"<br>Horário de Abertura: " + deposit.getOperationTime().getInitialTimeFormated() +
				"<br>Horário de Fechamento: " + deposit.getOperationTime().getFinalTimeFormated() + 
				"</body></html>"
		};		
		obj[numberOfRows-1] = line;
		
		return obj;
	}
	
	private String minutesToTimeFormatted(int minutes) {
		String timeFormatted = "";
		NumberFormat nf = NumberFormat.getInstance();	
		nf.setMinimumIntegerDigits(2);
		
		int minuteOpening = deposit.getOperationTime().getInitialMinute();
		int hourOpening = deposit.getOperationTime().getInitialHour();
		
		int hour = minutes/60 + hourOpening;
		int minute = minutes%60 + minuteOpening;
		
		if(minute > 60) {
			hour++;
			minute = minute - 60;
		}
		
		if(hour > 24) {
			hour = hour - 24;
		}
		timeFormatted = nf.format(hour) + ":" + nf.format(minute);
		
		return timeFormatted;
		
	}
	
	public String doubleToBrazilianString(double number) {
		number = Math.round(number*10000)/10000;
		String sNumber = Double.toString(number).replace(".", ",");
		return sNumber;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Deposit getDeposit() {
		return deposit;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
	}

	public double getDepositWeightDeparture() {
		return depositWeightDeparture;
	}

	public void setDepositWeightDeparture(double depositWeightDeparture) {
		this.depositWeightDeparture = depositWeightDeparture;
	}

	public double getDepositWeightReturn() {
		return depositWeightReturn;
	}

	public void setDepositWeightReturn(double depositWeightReturn) {
		this.depositWeightReturn = depositWeightReturn;
	}

	public List<Integer> getArrivalTimes() {
		return arrivalTimes;
	}

	public void setArrivalTimes(List<Integer> arrivalTimes) {
		this.arrivalTimes = arrivalTimes;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public List<Double> getWeightRoute() {
		return weightRoute;
	}

	public void setWeightRoute(List<Double> weightRoute) {
		this.weightRoute = weightRoute;
	}

	public double getTotalTimeTraveled() {
		return totalTimeTraveled;
	}

	public void setTotalTimeTraveled(double totalTimeTraveled) {
		this.totalTimeTraveled = totalTimeTraveled;
	}

	public double getTotalDistanceTraveled() {
		return totalDistanceTraveled;
	}

	public void setTotalDistanceTraveled(double totalDistanceTraveled) {
		this.totalDistanceTraveled = totalDistanceTraveled;
	}

	public double getDepositVolumeDeparture() {
		return depositVolumeDeparture;
	}

	public void setDepositVolumeDeparture(double depositVolumeDeparture) {
		this.depositVolumeDeparture = depositVolumeDeparture;
	}

	public double getDepositVolumeReturn() {
		return depositVolumeReturn;
	}

	public void setDepositVolumeReturn(double depositVolumeReturn) {
		this.depositVolumeReturn = depositVolumeReturn;
	}

	public List<Double> getVolumeRoute() {
		return volumeRoute;
	}

	public void setVolumeRoute(List<Double> volumeRoute) {
		this.volumeRoute = volumeRoute;
	}

	public List<Integer> getDepartureTimes() {
		return departureTimes;
	}

	public void setDepartureTimes(List<Integer> departureTimes) {
		this.departureTimes = departureTimes;
	}

	public List<Double> getTraveledDistance() {
		return traveledDistance;
	}

	public void setTraveledDistance(List<Double> traveledDistance) {
		this.traveledDistance = traveledDistance;
	}
}
