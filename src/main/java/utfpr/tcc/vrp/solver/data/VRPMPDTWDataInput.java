package utfpr.tcc.vrp.solver.data;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplFactory;
import utfpr.tcc.vrp.model.Delivery;
import utfpr.tcc.vrp.model.Itinerary;
import utfpr.tcc.vrp.model.Pickup;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.model.Vehicle;

public class VRPMPDTWDataInput extends IloCustomOplDataSource {
	private Scenario scenario;
	
	public VRPMPDTWDataInput(IloOplFactory oplFactory, Scenario scenario) {

        super(oplFactory);
        this.scenario = scenario;
   }

	@Override
	public void customRead() {
		
		IloOplDataHandler handler = this.getDataHandler();
		
		int nodes = scenario.getNumberOfNodes();
		
		handler.startElement("LimiteTempo");
			handler.addIntItem(1200); //It can be variable sent by the scenario controller.
		handler.endElement();
		
		handler.startElement("NumeroNos");
			handler.addIntItem(nodes);
		handler.endElement();
		
		handler.startElement("NumeroVeiculos");
			handler.addIntItem(scenario.getVehicles().size());
		handler.endElement();
		
		handler.startElement("CapPesoMax");
			handler.startArray();
				for(Vehicle vehicle : scenario.getVehicles()) {
					handler.addNumItem(vehicle.getWeightCapacity());
				}
				handler.endArray();
		handler.endElement();
		
		handler.startElement("CapVolMax");
		handler.startArray();
		for(Vehicle vehicle : scenario.getVehicles()) {
			handler.addNumItem(vehicle.getVolumeCapacity());
		}
		handler.endArray();
		handler.endElement();
		
		handler.startElement("QPesoEntrega");
			handler.startArray();
				handler.addNumItem(0.0);
				for(Delivery delivery : scenario.getDeliveries()) {
					handler.addNumItem(delivery.getProduct().getWeight());
				}
				for(Pickup pickup : scenario.getPickups()) {
					handler.addNumItem(0.0);
				}
			handler.endArray();
		handler.endElement();
		
		handler.startElement("QPesoColeta");
			handler.startArray();
				handler.addNumItem(0.0);
				for(Delivery delivery : scenario.getDeliveries()) {
					handler.addNumItem(0.0);
				}
				for(Pickup pickup : scenario.getPickups()) {
					handler.addNumItem(pickup.getProduct().getWeight());
				}
			handler.endArray();
		handler.endElement();
		
		handler.startElement("QVolEntrega");
			handler.startArray();
				handler.addNumItem(0.0);
				for(Delivery delivery : scenario.getDeliveries()) {
					handler.addNumItem(delivery.getProduct().getVolume());
				}
				for(Pickup pickup : scenario.getPickups()) {
					handler.addNumItem(0.0);
				}
			handler.endArray();
		handler.endElement();
		
		handler.startElement("QVolColeta");
			handler.startArray();
				handler.addNumItem(0.0);
				for(Delivery delivery : scenario.getDeliveries()) {
					handler.addNumItem(0.0);
				}
				for(Pickup pickup : scenario.getPickups()) {
					handler.addNumItem(pickup.getProduct().getVolume());
				}
			handler.endArray();
		handler.endElement();
		
		handler.startElement("TempoServico");
			handler.startArray();
				handler.addIntItem(0);
				for(int i = 1; i < nodes; i++) {
					handler.addIntItem(30);
				}
			handler.endArray();
		handler.endElement();
		
				
		int maxJT = scenario.getMaxTimeWindows();		
		
		handler.startElement("MaximoJanelasTempo");
			handler.addIntItem(maxJT);
		handler.endElement();
		
		int initialTW[][] = new int[nodes][maxJT];
		int finalTW[][] = new int[nodes][maxJT];
		
		int initialMinute = scenario.getDeposit().getOperationTime().getInitialTimeMinutes();
		int finalMinute = scenario.getDeposit().getOperationTime().getFinalTimeMinutes();
		int windowMinutes = finalMinute - initialMinute; //lenght of the time window in minutes.
 		int completeWith; //How many tw must be completed with the last valid tw.
 		int lastITW = 0; 
 		int lastFTW = 0;
		int nodePosition = 0;		
 		for(int tw = 0; tw < maxJT; tw++) {
 			initialTW[nodePosition][tw] = 0;
 			finalTW[nodePosition][tw] = windowMinutes;
		}		
 		nodePosition++;		
		for(Delivery delivery : scenario.getDeliveries()) {
			lastITW = 0;
			lastFTW = 0;
			for(int tw = 0; tw < delivery.getAvailabilities().size(); tw++) {
				int iTW = delivery.getAvailabilities().get(tw).getInitialTimeMinutes() - initialMinute;
				int fTW = delivery.getAvailabilities().get(tw).getFinalTimeMinutes() - initialMinute;
								
				if(iTW < 0) initialTW[nodePosition][tw] = 0;
				else if(iTW > windowMinutes) initialTW[nodePosition][tw] = windowMinutes;				
				else initialTW[nodePosition][tw] = iTW;
				
				if(fTW < 0) finalTW[nodePosition][tw] = 0;
				else if(fTW > windowMinutes) finalTW[nodePosition][tw] = windowMinutes;
				else finalTW[nodePosition][tw] = fTW;
				
				lastITW = initialTW[nodePosition][tw];
				lastFTW = finalTW[nodePosition][tw];
			}
			for(int tw = delivery.getAvailabilities().size(); tw < maxJT; tw++) {
				initialTW[nodePosition][tw] = lastITW;
				finalTW[nodePosition][tw] = lastFTW;
			}
			nodePosition++;
		}
		
		for(Pickup pickup : scenario.getPickups()) {
			completeWith = maxJT - pickup.getAvailabilities().size();
			lastITW = 0;
			lastFTW = 0;
			for(int tw = 0; tw < pickup.getAvailabilities().size(); tw++) {
				int iTW = pickup.getAvailabilities().get(tw).getInitialTimeMinutes() - initialMinute;
				int fTW = pickup.getAvailabilities().get(tw).getFinalTimeMinutes() - initialMinute;
								
				if(iTW < 0) initialTW[nodePosition][tw] = 0;
				else if(iTW > windowMinutes) initialTW[nodePosition][tw] = windowMinutes;				
				else initialTW[nodePosition][tw] = iTW;
				
				if(fTW < 0) finalTW[nodePosition][tw] = 0;
				else if(fTW > windowMinutes) finalTW[nodePosition][tw] = windowMinutes;
				else finalTW[nodePosition][tw] = fTW;
				
				lastITW = initialTW[nodePosition][tw];
				lastFTW = finalTW[nodePosition][tw];
			}
			for(int tw = pickup.getAvailabilities().size(); tw < maxJT; tw++) {
				initialTW[nodePosition][tw] = lastITW;
				finalTW[nodePosition][tw] = lastFTW;
			}
			nodePosition++;
		}
		

		handler.startElement("InicioJT");
		handler.startArray();	
			for(int i = 0; i < nodes; i++) {
				handler.startArray();
				for(int j = 0; j < maxJT; j++) {					
					handler.addIntItem(initialTW[i][j]);					
				}
				handler.endArray();
			}
		handler.endArray();
		handler.endElement();
		
		handler.startElement("FimJT");
		handler.startArray();
			for(int i = 0; i < nodes; i++) {
				handler.startArray();
				for(int j = 0; j < maxJT; j++) {
					handler.addIntItem(finalTW[i][j]);
				}
				handler.endArray();
			}
		handler.endArray();
		handler.endElement();
		
		handler.startElement("TempoViagem");
		handler.startArray();
			for(int i = 0; i < nodes; i++) {
				handler.startArray();
				for(int j = 0; j < nodes; j++) {
					Itinerary itinerary = scenario.getItineraryMatrix().get(i).get(j);
					int minutes = (int) Math.round(itinerary.getTravelTime());
					handler.addIntItem(minutes);				
					
				}
				handler.endArray();					
			}
		handler.endArray();
		handler.endElement();
	}
}
