package utfpr.tcc.vrp.solver.data;

import ilog.concert.IloException;
import ilog.concert.IloIntMap;
import ilog.concert.IloNumMap;
import ilog.opl.IloOplModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import utfpr.tcc.vrp.model.Deposit;
import utfpr.tcc.vrp.model.Itinerary;
import utfpr.tcc.vrp.model.Route;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.model.Service;
import utfpr.tcc.vrp.model.Solution;
import utfpr.tcc.vrp.model.Vehicle;
import utfpr.tcc.vrp.solver.model.MathematicalModel;

public class VRPMPDTWDataOutput {
	private IloOplModel oplModel;
	private Scenario scenario;
	private List<Object> results;
	
	private final static Logger logger = Logger.getLogger(VRPMPDTWDataOutput.class.getName());
	
	public VRPMPDTWDataOutput(IloOplModel oplModel, Scenario scenario, List<Object> results) {

		super();
		this.oplModel = oplModel;
		this.results = results;
		this.scenario = scenario;
	}
	
	public void FulfillResults() throws IloException {
		
		int numberOfNodes = scenario.getNumberOfNodes();
		int numberOfVehicles = scenario.getVehicles().size();
		
		//int objetiveValue =  (int) Math.round(oplModel.getSolutionGetter().getObjValue());
		double executionTime = oplModel.getElement("TempoExecucao").asNum();
		double objectiveValue = oplModel.getElement("objetivo").asNum();
		
		//String status = oplModel.getCplex().getStatus().toString();
		int resultStatus = oplModel.getElement("status").asInt();
		String status = "" + resultStatus; 
		if(resultStatus == 1) {
			status = "Ótima";			
			
		} else if(resultStatus == 11) {
			status = "Não ótima (limite de tempo excedido)";
		} else if(resultStatus == 102) {
			status = "Relaxada";
		}
						
		IloNumMap quantPeso = oplModel.getElement("quantPeso").asNumMap();	
		IloNumMap quantVolume = oplModel.getElement("quantVolume").asNumMap();	
		IloIntMap rota = oplModel.getElement("rota").asIntMap();			
		IloIntMap tempoChegada = oplModel.getElement("tempoChegada").asIntMap();		
				
		List<String> problems = verifyProblems();
		
		if(problems.size() > 0) {
			status = "Relaxada";
		}
		
		List<Route> routes = new ArrayList<Route>();
		List<Vehicle> vehicles = scenario.getVehicles();
		Deposit deposit = scenario.getDeposit();
		
		List<Service> services;
		List<Integer> arrivalTimes;
		List<Integer> departureTimes;
		List<Double> traveledDistance;
		
		List<Double> weightRoute;
		List<Double> volumeRoute;
		
		double departureWeight; //Load when the vehicle goes out of the deposit. Before services.
		double returnWeight; //Load when the vehicle return to the deposit. After services.
		double departureVolume;
		double returnVolume;
		
		try {
			
			for(int v = 0; v < numberOfVehicles; v++) {
				
				weightRoute = new ArrayList<Double>();
				volumeRoute = new ArrayList<Double>();
				
				Vehicle vehicle = vehicles.get(v);						
				services = new ArrayList<Service>();
				
				//Arrival time is when the vehicle got to the client j. 
				arrivalTimes = new ArrayList<Integer>();		
				//Departure time is when the vehicle started to move towards the client j
				departureTimes = new ArrayList<Integer>();				
				
				traveledDistance = new ArrayList<Double>();
				
				int nextNode = 0;		
				int lastNode = 0;
				int time;
				double distance = 0;
				do {
					for(int n = 0; n < numberOfNodes; n++) {					
						if(rota.getSub(nextNode+1).getSub(n+1).get(v+1) == 1) { 
							
							weightRoute.add(quantPeso.getSub(nextNode+1).getSub(n+1).get(v+1));
							volumeRoute.add(quantVolume.getSub(nextNode+1).getSub(n+1).get(v+1));
							
							lastNode = nextNode;
							nextNode = n;							
							if(n == 0); //Fim da rota.
							else if(n >= 1 && n <= scenario.getDeliveries().size()) services.add(scenario.getDeliveries().get(n - 1));
							else services.add(scenario.getPickups().get(n - 1 - scenario.getDeliveries().size()));			
							

							
							break;
						}
					}					
					time = tempoChegada.getSub(nextNode+1).get(v+1);
					arrivalTimes.add(time);
					Itinerary itinerary = scenario.getItineraryMatrix().get(lastNode).get(nextNode);
					time = time - (int) Math.round(itinerary.getTravelTime());
					departureTimes.add(time);
					distance += itinerary.getDistance();
					traveledDistance.add(distance);					
				} while(nextNode != 0);
				
				
				Route route = new Route(vehicle, 
										deposit, 
										services, 
										arrivalTimes, 
										departureTimes, 
										traveledDistance, 
										weightRoute,
										volumeRoute
										);
				routes.add(route);
			}
			
		} catch (IloException e) {
			logger.severe("Não foi possível ler as variáveis de saída.");			
			e.printStackTrace();
			throw e;
		}
		
		
		Solution solution = new Solution(scenario, MathematicalModel.VRPMPDTW, routes, executionTime, status, problems);		
		
		results.add(solution);
	}
	
	
	private List<String> verifyProblems() throws IloException {
		List<String> problems = new ArrayList<String>();
		int maxTimeWindows = scenario.getMaxTimeWindows();
		int numberOfNodes = scenario.getNumberOfNodes();
		int numberOfVehicles = scenario.getVehicles().size();
		
		IloNumMap relaxQuant = oplModel.getElement("relaxPeso").asNumMap();	
		IloNumMap relaxVolume = oplModel.getElement("relaxVolume").asNumMap();	
		IloNumMap relaxJanela = oplModel.getElement("relaxJanela").asNumMap();		
		double value;
		for(int v = 0; v < numberOfVehicles; v++) {
			for(int i = 0; i < numberOfNodes; i++) {
				value = Math.round(relaxJanela.getSub(i+1).get(v+1)*1000)/1000;
				if(value > 0) {
					long relaxJ = Math.round(relaxJanela.getSub(i+1).get(v+1));
					problems.add("Houve antecipação/atraso de " + relaxJ + " minutos " +
							"na janela de disponibilidade no " + scenario.getNode(i).toString() + ".");
				}
				
				for(int j = 0; j < numberOfNodes; j++) {		
					value = Math.round(relaxQuant.getSub(i+1).getSub(j+1).get(v+1)*1000)/1000;
					if(value > 0) {
						problems.add("Veículo \"" + scenario.getVehicles().get(v).getName() + "\" " +
								"ultrapassou " + value + " kg  do peso máximo " +
								"durante o percurso " + scenario.getNode(i).toString() + " -> " + 	
								scenario.getNode(j).toString() + ".");
					}
					
					value = Math.round(relaxVolume.getSub(i+1).getSub(j+1).get(v+1)*1000)/1000;
					if(value > 0) {
						problems.add("Veículo \"" + scenario.getVehicles().get(v).getName() + "\" " +
								"ultrapassou " + value + " m³  do volume máximo " +
								"durante o percurso " + scenario.getNode(i).toString() + " -> " + 	
								scenario.getNode(j).toString() + ".");
					}
				}
			}
		}
		
		return problems;	
	}
}
