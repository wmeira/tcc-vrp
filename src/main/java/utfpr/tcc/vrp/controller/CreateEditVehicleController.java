package utfpr.tcc.vrp.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import utfpr.tcc.vrp.model.Vehicle;
import utfpr.tcc.vrp.view.CreateEditVehicleView;

public class CreateEditVehicleController extends Controller {
	
	private CreateEditVehicleView view;
	private Vehicle model = null;
	private boolean isSaved = false;
	
	public CreateEditVehicleController(CreateEditVehicleView view, Vehicle model) {
		this.view = view;
		this.model = model;
		
		if(this.model == null) {
			view.setTitle("Adicionar Veículo");
		} else {
			view.setTitle("Editar Veículo");
		}
		
		//Add listeners
		view.getBtnSave().addActionListener(new BtnSaveListener());
		view.getBtnCancel().addActionListener(new BtnCancelListener());
		
		loadVehicle();
	}
	
	private void loadVehicle() {
		if(model == null) {
			return;
		}
		
		view.getTxtName().setText(model.getName());
		view.getTxtWeight().setText(model.getWeightCapacityString());
		view.getTxtVolume().setText(model.getVolumeCapacityString());
	}
	
	private void closeWindow() {
		view.setVisible(false);
        view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
	}

	private String validateTextFields() {
		StringBuilder errors = new StringBuilder();
		
		String name = view.getTxtName().getText().trim();
		if(name == null || name.isEmpty()) {
			errors.append("Campo nome é obrigatório.\n");
			view.getTxtName().setBackground(errorColor);
		}		
		
		String weight = view.getTxtWeight().getText().trim();
		if(weight == null || weight.isEmpty()) {
			errors.append("Campo capacidade de peso é obrigatório.\n");
			view.getTxtWeight().setBackground(errorColor);
		} else {
			if(!isValidPositiveNumber(weight)) {
				errors.append("Campo capacidade de peso deve ser um número positivo.\n");
				view.getTxtWeight().setBackground(errorColor);
			} 			
		}
				
		String volume = view.getTxtVolume().getText().trim();
		if(volume == null || volume.isEmpty()) {
			errors.append("Campo capacidade volumétrica é obrigatório.\n");
			view.getTxtVolume().setBackground(errorColor);
		} else {
		 	if(!isValidPositiveNumber(volume)) {
				errors.append("Campo capacidade volumétrica deve ser um número positivo.\n");
				view.getTxtVolume().setBackground(errorColor);
		 	}
		}

		return errors.toString();
	}
	
	private void resetBackground() {
		view.getTxtName().setBackground(Color.white);
		view.getTxtVolume().setBackground(Color.white);
		view.getTxtWeight().setBackground(Color.white);
	}	
	
	public Vehicle getVehicle() {
		return model;
	}
	
	public boolean isSaved() {
		return isSaved;
	}
	
	class BtnSaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {		
			resetBackground();
			
			String errors = validateTextFields();	
			if(!errors.isEmpty()) {
				JOptionPane.showConfirmDialog(view, 
						"Ocorreram os seguintes erros ao salvar o veículo: \n\n" + errors + "\n", "Erro ao Salvar Veículo", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			String name = view.getTxtName().getText().trim();
			String sWeight = view.getTxtWeight().getText().trim().replace(',', '.');
			double weight = Double.parseDouble(sWeight);
			String sVolume = view.getTxtVolume().getText().trim().replace(',', '.');
			double volume = Double.parseDouble(sVolume);
			
			Vehicle v = new Vehicle(name, volume, weight);
			model = v;
			
			isSaved = true;
			closeWindow();
		}
	}
	
	class BtnCancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			closeWindow();
		}
	}
	
}
