package utfpr.tcc.vrp.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import utfpr.tcc.vrp.model.Delivery;
import utfpr.tcc.vrp.model.Location;
import utfpr.tcc.vrp.model.Pickup;
import utfpr.tcc.vrp.model.Product;
import utfpr.tcc.vrp.model.Service;
import utfpr.tcc.vrp.model.TimeWindow;
import utfpr.tcc.vrp.view.CreateEditServiceView;

public class CreateEditServiceController extends Controller {

	private final static Logger logger = Logger.getLogger(CreateEditServiceController.class.getName());	
	
	private CreateEditServiceView view;
	private Service model;
	private boolean isSaved = false;
	private boolean addressChanged = false;
	private int numberEnabledIntervals = 1;
	
	public CreateEditServiceController(CreateEditServiceView view, Service model) {
		this.view = view;
		this.model = model;
		
		if(view.getTypeService() == CreateEditServiceView.DELIVERY) {
			if(this.model == null) {
				view.setTitle("Adicionar Entrega");
			} else {
				view.setTitle("Editar Entrega");
			}
		} else {
			if(this.model == null) {
				view.setTitle("Adicionar Coleta");
			} else {
				view.setTitle("Editar Coleta");
			}
		}
		
		//Add listeners
		view.getBtnSave().addActionListener(new BtnSaveListener());
		view.getBtnCancel().addActionListener(new BtnCancelListener());
		view.getBtnPlus().addActionListener(new BtnPlusListener());
		view.getBtnMinus().addActionListener(new BtnMinusListener());
		
		loadServico();
		
	}
	
	private void loadServico() {
		if(model == null) {
			updateEnabledIntervals();
			view.getTxtInitialIntervals()[0].setValue("08:00");
			view.getTxtFinalIntervals()[0].setValue("17:00");
			return;
		}
		
		view.getTxtClient().setText(model.getClient());
		view.getTxtAddress().setText(model.getLocation().getAddress());
		view.getTxtVolume().setText(model.getProduct().getVolumeString());
		view.getTxtWeight().setText(model.getProduct().getWeightString());
		numberEnabledIntervals = model.getAvailabilities().size();		
		updateEnabledIntervals();
		
		List<TimeWindow> tws = model.getAvailabilities();
		for(int i = 0; i < numberEnabledIntervals; i++) {
			view.getTxtInitialIntervals()[i].setValue(tws.get(i).getInitialTimeFormated());
			view.getTxtFinalIntervals()[i].setValue(tws.get(i).getFinalTimeFormated());
		}
		
	}
	
	private void updateEnabledIntervals() {
		for(int i = 0; i < view.getNumberOfIntervals(); i++) {
			if(i < numberEnabledIntervals) {
				view.getTxtInitialIntervals()[i].setEnabled(true);
				view.getTxtFinalIntervals()[i].setEnabled(true);				
			} else {
				view.getTxtInitialIntervals()[i].setEnabled(false);
				view.getTxtFinalIntervals()[i].setEnabled(false);	
			}
		}
	}
	
	private void closeWindow() {
		view.setVisible(false);
        view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
	}
	
	private void resetBackground() {
		view.getTxtClient().setBackground(Color.white);
		view.getTxtAddress().setBackground(Color.white);
		view.getTxtVolume().setBackground(Color.white);
		view.getTxtWeight().setBackground(Color.white);
		
		for(int i = 0; i < numberEnabledIntervals; i++) {
			view.getTxtInitialIntervals()[i].setBackground(Color.white);
			view.getTxtFinalIntervals()[i].setBackground(Color.white);
		}
	}
	
	private String validateTextFields() {
		StringBuilder errors = new StringBuilder();
		
		String client = view.getTxtClient().getText().trim();
		if(client == null || client.isEmpty()) {
			errors.append("Campo nome do cliente é obrigatório.\n");
			view.getTxtClient().setBackground(errorColor);
		}
		
		String address = view.getTxtAddress().getText().trim();
		if(address == null || address.isEmpty()) {
			errors.append("Campo endereço é obrigatório.\n");
			view.getTxtAddress().setBackground(errorColor);
		}	
		
		String weight = view.getTxtWeight().getText().trim();
		if(weight == null || weight.isEmpty()) {
			errors.append("Campo peso é obrigatório.\n");
			view.getTxtWeight().setBackground(errorColor);
		} else {
			if(!isValidPositiveNumber(weight)) {
				errors.append("Campo peso deve ser um número positivo.\n");
				view.getTxtWeight().setBackground(errorColor);
			}
		}
		
		String volume = view.getTxtVolume().getText().trim();
		if(volume == null || volume.isEmpty()) {
			errors.append("Campo volume é obrigatório.\n");
			view.getTxtVolume().setBackground(errorColor);			
		} else {
			if(!isValidPositiveNumber(volume)) {
				errors.append("Campo volume deve ser um número positivo.\n");
				view.getTxtVolume().setBackground(errorColor);
			}
		}
		
		boolean hourTest = true; 
		for(int i = 0; i < numberEnabledIntervals; i++) {
			
			String first = view.getTxtInitialIntervals()[i].getText();
			String second = view.getTxtFinalIntervals()[i].getText();
			if(!isValidHour(first)) {
				errors.append("Horário inválido no campo intervalo " + (i+1) + ".\n");
				view.getTxtInitialIntervals()[i].setBackground(errorColor);
				hourTest = false;
			} 
			
			if(!isValidHour(second)) {
				errors.append("Horário inválido no campo intervalo " + (i+1) + ".\n");
				view.getTxtFinalIntervals()[i].setBackground(errorColor);
				hourTest = false;
			}
			
			if(hourTest && !isAfterHour(first, second)) {
				errors.append("Horário inicial do intervalo " + (i+1) + " é posterior ou igual ao horário final.\n");
				view.getTxtInitialIntervals()[i].setBackground(errorColor);
				view.getTxtFinalIntervals()[i].setBackground(errorColor);
			}
			
			hourTest = true;
		}
		return errors.toString();
	}
	
	public Service getService() {
		return model;
	}
	
	public boolean isSaved() {
		return isSaved;
	}
	
	public boolean isAddressChanged() {
		return addressChanged;
	}
	
	class BtnSaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {		
			resetBackground();
			
			String errors = validateTextFields();	
			if(!errors.isEmpty()) {
				JOptionPane.showMessageDialog(view, 
						"Ocorreram os seguintes erros ao salvar o serviço: \n\n" + errors + "\n", "Erro ao Salvar Serviço", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String address = view.getTxtAddress().getText();
			Location location = null;
			if(model != null) {				
				if(!address.equals(model.getLocation().getAddress())) {
					location = getLocation(address, logger);
					if(location == null) {
						view.getTxtAddress().setBackground(errorColor);
						return;
					}
					
					//Address changed and is valid.
					addressChanged = true;
				} else {
					location = model.getLocation();
				}					
			} else {
				location = getLocation(address, logger);
				if(location == null) {
					view.getTxtAddress().setBackground(errorColor);
					return;
				}			
			}
				
			String client = view.getTxtClient().getText().trim();
			String sWeight = view.getTxtWeight().getText().trim().replace(',', '.');
			double weight = Double.parseDouble(sWeight);
			String sVolume = view.getTxtVolume().getText().trim().replace(',', '.');
			double volume = Double.parseDouble(sVolume);
			Product product = new Product(weight, volume);
			
			List<TimeWindow> tw = new ArrayList<TimeWindow>();
			for(int i = 0; i < numberEnabledIntervals; i++) {
				tw.add(new TimeWindow(view.getTxtInitialIntervals()[i].getText(), 
										view.getTxtFinalIntervals()[i].getText()));
			}
			 
			Service service;
			if(view.getTypeService() == CreateEditServiceView.DELIVERY) {
				service = new Delivery(client, location, tw, product);
			} else {
				service = new Pickup(client, location, tw, product);
			}
			
			model = service;
			
			isSaved = true;
			closeWindow();
		}
	}
	
	class BtnCancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			closeWindow();
		}
	}
	
	class BtnPlusListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			if(numberEnabledIntervals < view.getNumberOfIntervals() ) {
				numberEnabledIntervals++;
				updateEnabledIntervals();
			} else {
				//JOptionPane.showConfirmDialog(view, "Máximo 4 intervalos." ,"Erro ao Adicionar Novo Intervalo",JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class BtnMinusListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			if(numberEnabledIntervals > 1) {
				numberEnabledIntervals--;
				updateEnabledIntervals();
			} else {
				//JOptionPane.showConfirmDialog(view, "Mínimo de 1 intervalo." ,"Erro ao Remover Intervalo",JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
