package utfpr.tcc.vrp.view;

import java.awt.Font;
import java.awt.Toolkit;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import utfpr.tcc.vrp.prop.Path;

public class CreateEditVehicleView extends DialogView {

	private final static Logger logger = Logger.getLogger(CreateEditVehicleView.class.getName());
	
	public static final int SCREEN_WIDTH = 280;
	public static final int SCREEN_HEIGHT = 170;
	
	private JLabel lblName;
	private JLabel lblWeight;
	private JLabel lblVolume;
	
	private JButton btnCancel;
	private JButton btnSave;
	
	private JTextField txtName;
	private JTextField txtWeight;
	private JTextField txtVolume;	
	
	private JPanel pnlContent;

	public CreateEditVehicleView(JDialog owner) {
		super(owner);		
		this.iconsPath = Path.getInstance().getIconsPath();
		
		initComponents();	
	}
	
	private void initComponents() {
			
		initPanelContent();
		initLabels();
		initTextFields();
		initButtons();
		
		/*
		 * JDialog
		 */		
		this.setContentPane(pnlContent);				
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);	
		centerDialog();			
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	
		this.setResizable(false);		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(iconsPath + "truck.png"));				
		this.setModal(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);	
	}
	
	private void initPanelContent() {
		pnlContent = new JPanel();
		pnlContent.setLayout(null);
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));	
	}
	
	private void initLabels() {
		lblName = new JLabel("Nome:");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblName.setBounds(10, 3, 46, 35);		
		pnlContent.add(lblName);
		
		lblWeight = new JLabel("Capacidade de Peso (kg):");
		lblWeight.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWeight.setBounds(10, 41, 167, 24);
		pnlContent.add(lblWeight);
		
		lblVolume = new JLabel("Capacidade de Volume (m³):");
		lblVolume.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblVolume.setBounds(10, 72, 178, 24);
		pnlContent.add(lblVolume);
		
	}

	private void initTextFields() {
		txtName = new JTextField();
		txtName.setColumns(100);
		txtName.setBounds(64, 10, 200, 20);
		txtName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnlContent.add(txtName);
		
		txtWeight = new JTextField();
		txtWeight.setColumns(20);
		txtWeight.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtWeight.setBounds(187, 43, 77, 20);
		pnlContent.add(txtWeight);
		
		txtVolume = new JTextField();
		txtVolume.setColumns(20);
		txtVolume.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtVolume.setBounds(187, 74, 77, 20);
		pnlContent.add(txtVolume);
		
	}
	
	private void initButtons() {
		btnSave = new JButton("Salvar");
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSave.setBounds(72, 107, 90, 24);
		pnlContent.add(btnSave);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnCancel.setBounds(174, 107, 90, 24);
		pnlContent.add(btnCancel);
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public JTextField getTxtName() {
		return txtName;
	}

	public JTextField getTxtWeight() {
		return txtWeight;
	}

	public JTextField getTxtVolume() {
		return txtVolume;
	}

	public JPanel getPnlContent() {
		return pnlContent;
	}
	
	
}
