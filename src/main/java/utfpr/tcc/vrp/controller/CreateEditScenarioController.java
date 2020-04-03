package utfpr.tcc.vrp.controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jdom2.JDOMException;

import utfpr.tcc.vrp.model.Delivery;
import utfpr.tcc.vrp.model.Deposit;
import utfpr.tcc.vrp.model.Location;
import utfpr.tcc.vrp.model.Pickup;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.model.TimeWindow;
import utfpr.tcc.vrp.model.Vehicle;
import utfpr.tcc.vrp.view.CreateEditScenarioView;
import utfpr.tcc.vrp.view.CreateEditServiceView;
import utfpr.tcc.vrp.view.CreateEditVehicleView;

public class CreateEditScenarioController extends Controller {
	
	private final static Logger logger = Logger.getLogger(CreateEditScenarioController.class.getName());	
	
	private CreateEditScenarioView view; //view
	private Scenario model = null; //model
	private boolean isNew;
	private String defaultTitle;
	private String fileNewScenario = null;

	public CreateEditScenarioController(CreateEditScenarioView view, Scenario model) {
		this.view = view;
		this.model = model;

		if(this.model == null) {
			isNew = true;
			view.setTitle(view.CREATE_TITLE);
		} else {
			isNew = false;
			view.setTitle(view.EDIT_TITLE);
			fileNewScenario = model.getFile();
		}
		defaultTitle = view.getTitle();
		
		//Add Listeners to the view.
		view.getBtnSave().addActionListener(new BtnSaveListener());
		view.getBtnCancel().addActionListener(new BtnCancelListener());
		
		view.getBtnAddVehicle().addActionListener(new BtnAddVehicleListener());
		view.getBtnEditVehicle().addActionListener(new BtnEditVehicleListener());
		view.getBtnDeleteVehicle().addActionListener(new BtnDeleteVehicleListener());
		
		view.getBtnAddDelivery().addActionListener(new BtnAddDeliveryListener());
		view.getBtnEditDelivery().addActionListener(new BtnEditDeliveryListener());
		view.getBtnDeleteDelivery().addActionListener(new BtnDeleteDeliveryListener());
		
		view.getBtnAddPickup().addActionListener(new BtnAddPickupListener());
		view.getBtnEditPickup().addActionListener(new BtnEditPickupListener());
		view.getBtnDeletePickup().addActionListener(new BtnDeletePickupListener());
		
		view.getBtnGetCoordinatesAddressDeposit().addActionListener(new BtnGetCoordinatesAddressDeposit());
		
		loadScenario();
		
	}
	
	private void loadScenario() {
		if(model == null) { //Create Scenario
			model = new Scenario();		
			view.getTxtOpeningTimeDeposit().setValue("08:00");			
			view.getTxtClosingTimeDeposit().setValue("17:00");			
			return;
		}		
		
		view.getTxtNameScenario().setText(model.getName());
		view.getTxtDateScenario().setValue(model.getDateFormated());
		
		loadDeposit();
		loadVehicles(); 
		loadPickups();
		loadDeliveries();				
	}
	
	private void loadDeposit() {
		Deposit deposit = model.getDeposit();
		view.getTxtAddressDeposit().setText(deposit.getLocation().getAddress());
		view.getTxtAddressDeposit().setCaretPosition(0);
		view.getTxtLatitudeDeposit().setText(Double.toString(deposit.getLocation().getLatitude()));
		view.getTxtLatitudeDeposit().setCaretPosition(0);
		view.getTxtLongitudeDeposit().setText(Double.toString(deposit.getLocation().getLongitude()));
		view.getTxtLongitudeDeposit().setCaretPosition(0);
		view.getTxtOpeningTimeDeposit().setValue(deposit.getOperationTime().getInitialTimeFormated());
		view.getTxtClosingTimeDeposit().setValue(deposit.getOperationTime().getFinalTimeFormated());
	}
	
	private void loadVehicles() {
		List<Vehicle> vehicles = model.getVehicles();
		DefaultTableModel dm = (DefaultTableModel) view.getTblVehicles().getModel();
		for(Vehicle v : vehicles) {			
			dm.addRow(v.getRowVehicle());			
		}
		if(vehicles.size() > 0) {
			view.getTblVehicles().setRowSelectionInterval(0, 0);
		}		
	}
	
	private void loadPickups() {
		List<Pickup> pickups = model.getPickups();
		DefaultTableModel dm = (DefaultTableModel) view.getTblPickups().getModel();
		for(Pickup p : pickups) {			
			dm.addRow(p.getRowService());			
		}
		if(pickups.size() > 0) {
			view.getTblPickups().setRowSelectionInterval(0, 0);
		}
	}
	
	private void loadDeliveries() {
		List<Delivery> delivery = model.getDeliveries();
		DefaultTableModel dm = (DefaultTableModel) view.getTblDeliveries().getModel();
		for(Delivery d : delivery) {			
			dm.addRow(d.getRowService());			
		}
		if(delivery.size() > 0) {
			view.getTblDeliveries().setRowSelectionInterval(0, 0);
		}
	}
	
	
	private void cleanTable(DefaultTableModel dm) {
		for(int row = dm.getRowCount() - 1; row > -1 ; row--) {
			dm.removeRow(row);
		}
	}
	
	private void resetBackground() {
		view.getTxtNameScenario().setBackground(Color.white);
		view.getTxtNameScenario().setBackground(Color.white);
		view.getTxtDateScenario().setBackground(Color.white);
		
		view.getTxtAddressDeposit().setBackground(Color.white);
		view.getTxtOpeningTimeDeposit().setBackground(Color.white);
		view.getTxtClosingTimeDeposit().setBackground(Color.white);
		
	}
	
	private void setEnableView(boolean enabled) {
		view.getBtnAddDelivery().setEnabled(enabled);
		view.getBtnAddPickup().setEnabled(enabled);
		view.getBtnAddVehicle().setEnabled(enabled);
		view.getBtnCancel().setEnabled(enabled);
		view.getBtnDeleteDelivery().setEnabled(enabled);
		view.getBtnDeletePickup().setEnabled(enabled);
		view.getBtnDeleteVehicle().setEnabled(enabled);
		view.getBtnEditDelivery().setEnabled(enabled);
		view.getBtnEditPickup().setEnabled(enabled);
		view.getBtnEditVehicle().setEnabled(enabled);
		view.getBtnGetCoordinatesAddressDeposit().setEnabled(enabled);
		view.getBtnSave().setEnabled(enabled);
		
		view.getTxtAddressDeposit().setEnabled(enabled);
		view.getTxtClosingTimeDeposit().setEnabled(enabled);
		view.getTxtDateScenario().setEnabled(enabled);
		view.getTxtLatitudeDeposit().setEnabled(enabled);
		view.getTxtLongitudeDeposit().setEnabled(enabled);
		view.getTxtNameScenario().setEnabled(enabled);
		view.getTxtOpeningTimeDeposit().setEnabled(enabled);
		
		view.getTblDeliveries().setEnabled(enabled);
		view.getTblPickups().setEnabled(enabled);
		view.getTblVehicles().setEnabled(enabled);
		
	}
	
	private String validateTextFields() {
		StringBuilder errors = new StringBuilder();
		
		String nameScenario = view.getTxtNameScenario().getText().trim();
		if(nameScenario == null || nameScenario.isEmpty()) {		
			errors.append("Campo nome do cenário vazio.\n"); 
			view.getTxtNameScenario().setBackground(errorColor);
		} 
		
		String date = view.getTxtDateScenario().getText().trim();
		if(!isDateValid(date)) {
			errors.append("Campo data do cenário inválido/incompleto.\n"); 
			view.getTxtDateScenario().setBackground(errorColor);
		}
		
		String addressDeposit = view.getTxtAddressDeposit().getText().trim();
		if(addressDeposit.isEmpty()) {
			errors.append("Campo endereço do depósito vazio.\n"); 
			view.getTxtAddressDeposit().setBackground(errorColor);
		}
		
		boolean hourTest = true;
		String openingTimeDeposit = view.getTxtOpeningTimeDeposit().getText().trim();
		if(!isValidHour(openingTimeDeposit)) {
			errors.append("Campo horário de abertura inválido.\n"); 
			view.getTxtOpeningTimeDeposit().setBackground(errorColor);
			hourTest = false;
		}
		String closingTimeDeposit = view.getTxtClosingTimeDeposit().getText().trim();
		if(!isValidHour(openingTimeDeposit)) {
			errors.append("Campo horário de fechamento inválido.\n"); 
			view.getTxtClosingTimeDeposit().setBackground(errorColor);
			hourTest = false;
		}
		
		if(hourTest && !isAfterHour(openingTimeDeposit, closingTimeDeposit)) {
			errors.append("Horário de abertura é posterior ou igual ao horário de fechamento.\n"); 
			view.getTxtClosingTimeDeposit().setBackground(errorColor);
			view.getTxtOpeningTimeDeposit().setBackground(errorColor);
		}
		
		
		return errors.toString();
	}
	
	private Deposit getDepositFromView(Location location) {
		
		String[] firstHourMinute = view.getTxtOpeningTimeDeposit().getText().split(":");
		String[] secondHourMinute = view.getTxtClosingTimeDeposit().getText().split(":");
		TimeWindow tw = new TimeWindow(Integer.parseInt(firstHourMinute[0]), Integer.parseInt(firstHourMinute[1]), 
				Integer.parseInt(secondHourMinute[0]), Integer.parseInt(secondHourMinute[1]));
		
		Deposit deposit = new Deposit("Depósito Central", location, tw);
		
		return deposit;
	}	
	
	private void closeWindow() {
		view.setVisible(false);
        view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
	}
	
	private void setProgressBarStatus(boolean status) {
		setEnableView(!status);		
		view.getProgressBar().setIndeterminate(status);
		view.getProgressBar().setVisible(status);
		
		if(status) {
			view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			view.setTitle(view.CALCULATING_ROUTE_TITLE);
		} else {
			view.setCursor(null);
			view.setTitle(defaultTitle);
		}

		
	}
	
	public String getFileNewScenario() {
		return fileNewScenario;
	}
	
	class BtnSaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			
			resetBackground();
			String errors = validateTextFields();	
			if(!errors.isEmpty()) {
				JOptionPane.showMessageDialog(view, 
						"Ocorreram os seguintes erros ao salvar cenário: \n\n" + errors + "\n", "Erro", 
						 JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Thread t = new Thread()
	        {						
	            public void run() {
	            	
	            	boolean saveNew = isNew;
	            	String name = view.getTxtNameScenario().getText().trim();
	    			String date = view.getTxtDateScenario().getText().trim();
	    			model.setName(name);
	    			model.setDate(date);
	    			
	    			//Deposit, Distance and Travel Time
	    			String address = view.getTxtAddressDeposit().getText();
	    			Location location;	
	    			Deposit deposit;
	    			
	    			if(isNew) {
	    				
		    			setProgressBarStatus(true);
		    			
	    				location = getLocation(address, logger);					
	    				if(location == null) {
	    					view.getTxtAddressDeposit().setBackground(errorColor);
	    					return;			
	    				} 				
	    				deposit = getDepositFromView(location);	
	    				model.setDeposit(deposit);	
	    				
	    				try {
	    					model.createCompleteItineraryMatrix(); //deposit's node is the first one => 0.
	    				} catch (IOException e1) {
	    					throwIOException(e1, logger);					
	    				} catch (JDOMException e1) {
	    					throwJDOMException(e1, logger);					
	    				}		
	    				
	    			} else {	
	    				int answer = JOptionPane.showConfirmDialog(view,"Salvar como novo cenário?" , "Salvar Cenário", JOptionPane.YES_NO_CANCEL_OPTION);
	    				if(answer == JOptionPane.YES_OPTION) {
	    					saveNew = true;
	    					String newName = JOptionPane.showInputDialog("Novo nome para cenário?", view.getTxtNameScenario().getText());
	    					if(newName != null) {
	    						model.setName(newName);
	    					}	    					
	    				} else if(answer == JOptionPane.CANCEL_OPTION) {
	    	            	setProgressBarStatus(false);
	    					return;
	    				}		
	    				
		    			setProgressBarStatus(true);
		    			
	    				if(!address.equals(model.getDeposit().getLocation().getAddress())) {
	    					location = getLocation(address, logger);						
	    					if(location == null) {
	    						view.getTxtAddressDeposit().setBackground(errorColor);
	    		            	setProgressBarStatus(false);
	    						return;			
	    					} 
	    					
	    					deposit = getDepositFromView(location);	
	    					model.setDeposit(deposit);	
	    					
	    					try {
	    						model.editNodeDistanceTravelTimeMatrices(0); //deposit's node is the first one => 0.
	    					} catch (IOException e1) {
	    						throwIOException(e1, logger);					
	    					} catch (JDOMException e1) {
	    						throwJDOMException(e1, logger);					
	    					}	
	    					
	    				} else {
	    					//same address, don't need to change the matrices.
	    					location = model.getDeposit().getLocation();
	    					deposit = getDepositFromView(location);	
	    					model.setDeposit(deposit);	
	    				}					
	    			}
	    				    			
	    			for(Delivery delivery : model.getDeliveries()) {
	    				delivery.organizeAvailabilities(deposit.getOperationTime());
	    			}
	    			
	    			for(Pickup pickup : model.getPickups()) {
	    				pickup.organizeAvailabilities(deposit.getOperationTime());
	    			}	    			
	    			
	    			boolean save = model.save(saveNew);

	    			if(save == Scenario.SAVE_ERROR) {	
	    				JOptionPane.showConfirmDialog(view, 
	    						"Ocorreu um erro ao salvar. Tente novamente.", "Erro", 
	    						JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
	    			} else {
	    				int answer = JOptionPane.showConfirmDialog(view, 
	    						"Cenário Salvo com Sucesso! Deseja voltar a tela principal?", "Sucesso ao Salvar Cenário", 
	    						JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
	    						fileNewScenario = model.getFile();
	    				if(answer == JOptionPane.YES_OPTION) {	    					
	    					closeWindow();
	    				} else {
	    					//just if the you want to stay on the window, adjust latitude and longitude text fields.
	    					view.getTxtLatitudeDeposit().setText(Double.toString((location.getLatitude())));
	    					view.getTxtLatitudeDeposit().setCaretPosition(0);
	    					view.getTxtLongitudeDeposit().setText(Double.toString((location.getLongitude())));
	    					view.getTxtLongitudeDeposit().setCaretPosition(0);
	    				}
	    			}
	    			
	            	setProgressBarStatus(false);
	            }
	            
	        };
	        
	        t.start();		
			
			
		}
	}
	
	class BtnCancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			closeWindow();
		}
	}
	
	class BtnAddVehicleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CreateEditVehicleView createVehicleView = new CreateEditVehicleView(view);
			CreateEditVehicleController createVehicleController = new CreateEditVehicleController(createVehicleView, null);

			createVehicleView.setVisible(true);							
			Vehicle v = createVehicleController.getVehicle();
			
			if(createVehicleController.isSaved()) {
				model.getVehicles().add(v);
				cleanTable((DefaultTableModel) view.getTblVehicles().getModel());
				loadVehicles(); 
			}
		}
	}
	class BtnEditVehicleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = view.getTblVehicles().getSelectedRow();
			
			if(index < 0) {
				return;
			}
			
			Vehicle vehicle = model.getVehicles().get(view.getTblVehicles().getSelectedRow());
			CreateEditVehicleView editVehicleView = new CreateEditVehicleView(view);
			CreateEditVehicleController editVehicleController = new CreateEditVehicleController(editVehicleView, vehicle);
			
			editVehicleView.setVisible(true);	
			Vehicle v = editVehicleController.getVehicle();
			
			if(editVehicleController.isSaved()) {
				model.getVehicles().set(index, v);
				cleanTable((DefaultTableModel) view.getTblVehicles().getModel());
				loadVehicles(); 	
			}		
		}
	}
	class BtnDeleteVehicleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			int index = view.getTblVehicles().getSelectedRow();
			
			if(index < 0) {
				return;
			}
			
			model.getVehicles().remove(index);
			
			DefaultTableModel dm = (DefaultTableModel) view.getTblVehicles().getModel();
			dm.removeRow(index);			
			
			if(model.getVehicles().size() > 0) {
				view.getTblVehicles().setRowSelectionInterval(0, 0);
			}	
		}
	}
	
	class BtnAddDeliveryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Thread t = new Thread()
	        {
	            public void run() {
	            	CreateEditServiceView createDeliveryView = new CreateEditServiceView(view, CreateEditServiceView.DELIVERY);
	    			CreateEditServiceController createDeliveryController = new CreateEditServiceController(createDeliveryView, null);

	    			createDeliveryView.setVisible(true);
	    			
	    			setProgressBarStatus(true);
	    			Delivery d = (Delivery) createDeliveryController.getService();
	    			
	    			if(d != null) {
	    				try {
	    					model.getDeliveries().add(d);	    					
	    					if(!isNew) {
	    						model.addNodeDistanceTravelTimeMatrices(model.getNodePosition(d));
	    					}	    					
	    					cleanTable((DefaultTableModel) view.getTblDeliveries().getModel());
	    					loadDeliveries(); 
	    				}  catch (IOException ex) {
	    					throwIOException(ex, logger);					
	    				} catch (JDOMException ex) {
	    					throwJDOMException(ex, logger);	
	    				}						
	    			}

	            	setProgressBarStatus(false);
	            }
	        };
	        
	        t.start();
	        
			
		}
	}
	class BtnEditDeliveryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Thread t = new Thread()
	        {
	            public void run() {
	            	int index = view.getTblDeliveries().getSelectedRow();
	    			
	    			if(index < 0) {
	    				return;
	    			}
	    			
	    			Delivery delivery = model.getDeliveries().get(view.getTblDeliveries().getSelectedRow());
	    			CreateEditServiceView editDeliveryView = new CreateEditServiceView(view, CreateEditServiceView.DELIVERY);
	    			CreateEditServiceController editDeliveryController = new CreateEditServiceController(editDeliveryView, delivery);
	    			
	    			editDeliveryView.setVisible(true);	
	    			
	    			setProgressBarStatus(true);
	    			
	    			Delivery d = (Delivery) editDeliveryController.getService();	
	    			if(editDeliveryController.isSaved()) {
	    				try {	    
	    	    			model.getDeliveries().set(index, d);
	    					if(editDeliveryController.isAddressChanged() && !isNew) {
	    						int node = model.getNodePosition(model.getDeliveries().get(index));
	    						model.editNodeDistanceTravelTimeMatrices(node);
	    					}	
	    	    			cleanTable((DefaultTableModel) view.getTblDeliveries().getModel());
	    					loadDeliveries(); 	    					
	    				} catch (IOException ex) {
	    					throwIOException(ex, logger);					
	    				} catch (JDOMException ex) {
	    					throwJDOMException(ex, logger);	
	    				}		
	    			}
	    			
	            	setProgressBarStatus(false);
	            }
	        };
	        
	        t.start();
		}
	}
	class BtnDeleteDeliveryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = view.getTblDeliveries().getSelectedRow();
			
			if(index < 0) {
				return;
			}
			
			if(!isNew) {
				int node = model.getNodePosition(model.getDeliveries().get(index));
				model.deleteNodeDistanceTravelTimeMatrices(node);				
			}
			
			model.getDeliveries().remove(index);
			
			DefaultTableModel dm = (DefaultTableModel) view.getTblDeliveries().getModel();
			dm.removeRow(index);	
			
			if(model.getDeliveries().size() > 0) {
				view.getTblDeliveries().setRowSelectionInterval(0, 0);
			}
		}
	}
	
	class BtnAddPickupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Thread t = new Thread()
	        {
	            public void run() {
	            	CreateEditServiceView createPickupView = new CreateEditServiceView(view, CreateEditServiceView.PICKUP);
	    			CreateEditServiceController createPickupController = new CreateEditServiceController(createPickupView, null);

	    			createPickupView.setVisible(true);	
	    			
	    			setProgressBarStatus(true);
	    			
	    			Pickup p = (Pickup) createPickupController.getService();
	    			
	    			if(p != null) {
	    				
	    				try {
	    					model.getPickups().add(p);
	    					if(!isNew) {
	    						model.addNodeDistanceTravelTimeMatrices(model.getNodePosition(p));
	    					}	    					
	    					cleanTable((DefaultTableModel) view.getTblPickups().getModel());
	    					loadPickups(); 					
	    				} catch (IOException ex) {
	    					throwIOException(ex, logger);					
	    				} catch (JDOMException ex) {
	    					throwJDOMException(ex, logger);	
	    				}		
	    			}
	    			
	            	setProgressBarStatus(false);
	            }
	        };
	        
	        t.start();
		}
		
	}
	class BtnEditPickupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			        	
			Thread t = new Thread()
	        {
	            public void run() {
	            	
	            	int index = view.getTblPickups().getSelectedRow();
	    				    			
	    			if(index < 0) {
	    				return;
	    			}
	    				    			
	            	Pickup pickup = model.getPickups().get(view.getTblPickups().getSelectedRow());
	    			CreateEditServiceView editPickupView = new CreateEditServiceView(view, CreateEditServiceView.PICKUP);
	    			CreateEditServiceController editPickupController = new CreateEditServiceController(editPickupView, pickup);
	    				   
	    			editPickupView.setVisible(true);	
	    			
	    			setProgressBarStatus(true);
	    			Pickup p = (Pickup) editPickupController.getService();
	    			
	    			if(editPickupController.isSaved()) {
	    				
	    				try {
	    					model.getPickups().set(index, p);
	    					if(editPickupController.isAddressChanged() && !isNew) {
	    						int node = model.getNodePosition(model.getPickups().get(index));
	    						model.editNodeDistanceTravelTimeMatrices(node);
	    					}
	    					cleanTable((DefaultTableModel) view.getTblPickups().getModel());
	    					loadPickups(); 		
	    				} catch (IOException ex) {
	    					throwIOException(ex, logger);					
	    				} catch (JDOMException ex) {
	    					throwJDOMException(ex, logger);	
	    				}	
	    			}
	    			
	            	setProgressBarStatus(false);
	            }
	        };
	        
	        t.start();
		}
	}
	class BtnDeletePickupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = view.getTblPickups().getSelectedRow();
			
			if(index < 0) {
				return;
			}
			
			if(!isNew) {
				int node = model.getNodePosition(model.getPickups().get(index));
				model.deleteNodeDistanceTravelTimeMatrices(node);				
			}
			
			model.getPickups().remove(index); 
			
			DefaultTableModel dm = (DefaultTableModel) view.getTblPickups().getModel();
			dm.removeRow(index);	
			
			if(model.getPickups().size() > 0) {
				view.getTblPickups().setRowSelectionInterval(0, 0);
			}
		}
	}
	
	class BtnGetCoordinatesAddressDeposit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			Thread t = new Thread()
	        {
	            public void run() {
	            	view.getTxtAddressDeposit().setBackground(Color.white);
	            	setProgressBarStatus(true);
	    			String address = view.getTxtAddressDeposit().getText();
	                Location location = getLocation(address, logger);			
	    			
	    			if(location == null) {
	    				view.getTxtAddressDeposit().setBackground(errorColor);
	    				setProgressBarStatus(false);
	    				view.setCursor(null);
	    				return;
	    			}			
	    			
	    			view.getTxtAddressDeposit().setText(location.getAddress());
	    			view.getTxtAddressDeposit().setCaretPosition(0);
	    			view.getTxtLatitudeDeposit().setText(Double.toString(location.getLatitude()));
	    			view.getTxtLatitudeDeposit().setCaretPosition(0);
	    			view.getTxtLongitudeDeposit().setText(Double.toString(location.getLongitude()));
	    			view.getTxtLongitudeDeposit().setCaretPosition(0);
	    			
	    			setProgressBarStatus(false);
	            }
	        };
	        
	        t.start();	
		}
	}
}
