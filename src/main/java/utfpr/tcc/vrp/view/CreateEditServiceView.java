package utfpr.tcc.vrp.view;

import java.awt.Font;
import java.awt.Toolkit;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import utfpr.tcc.vrp.prop.Path;

public class CreateEditServiceView extends DialogView {

	private final static Logger logger = Logger.getLogger(CreateEditServiceView.class.getName());
	
	public static final boolean PICKUP = false;
	public static final boolean DELIVERY = true;
	public static final int SCREEN_WIDTH = 430;
	public static final int SCREEN_HEIGHT = 300;
	
	private boolean typeService;	
	private MaskFormatter hourMask;
	
	private JLabel lblClient;
	private JLabel lblAddress;
	private JLabel lblWeight;
	private JLabel lblVolume;
	private JLabel lblAvailability;
	
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnPlus;
	private JButton btnMinus;
	
	private JTextField txtClient;
	private JTextField txtAddress;
	private JTextField txtWeight;
	private JTextField txtVolume;
	
	private final int numberOfIntervals = 4;
	private JLabel[] lblIntervals;
 	private JFormattedTextField[] txtInitialIntervals;
	private JFormattedTextField[] txtFinalIntervals;
		
	private JPanel pnlContent;
	
	private String iconsPath;
	
	public CreateEditServiceView(JDialog owner, boolean service) {
		
		super(owner);
		this.typeService = service;
		
		this.iconsPath = Path.getInstance().getIconsPath();
		
		initComponents();
	}
	
	private void initComponents() {
		initPanelContent();
		initLabels();
		initTextFields();
		initButtons();
		
		initIntervals();
		
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
	
	private void initIntervals() {
		
		btnPlus = new JButton();
		btnPlus.setIcon(new ImageIcon(iconsPath + "create2.png"));
		btnPlus.setBounds(205, 115, 20, 20);
		pnlContent.add(btnPlus);
		
		btnMinus = new JButton();
		btnMinus.setIcon(new ImageIcon(iconsPath + "delete2.png"));
		btnMinus.setBounds(230, 115, 20, 20);
		pnlContent.add(btnMinus);
		
		try {
	        hourMask = new MaskFormatter("##:##");
	        hourMask.setPlaceholderCharacter('0');
	    } catch (ParseException ex) {
	    	logger.severe("Erro ao colocar máscara no campo intervalo de tempo.");
	    }	
		
		lblIntervals = new JLabel[numberOfIntervals];
		txtInitialIntervals = new JFormattedTextField[numberOfIntervals];
		txtFinalIntervals = new JFormattedTextField[numberOfIntervals];
		
		int y = 115;
		
		for(int i = 0; i < numberOfIntervals; i++) {
			lblIntervals[i] = new JLabel();
			lblIntervals[i].setText("Intervalo " + (i+1) + ": ");
			lblIntervals[i].setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblIntervals[i].setBounds(35, y + 25*i, 79, 20);
			pnlContent.add(lblIntervals[i]);
			
			txtInitialIntervals[i] = new JFormattedTextField(hourMask);
			txtInitialIntervals[i].setFont(new Font("Tahoma", Font.PLAIN, 13));
			txtInitialIntervals[i].setBounds(108, y + 25*i, 40, 20);
			txtInitialIntervals[i].setEnabled(false);
			pnlContent.add(txtInitialIntervals[i]);
			
			txtFinalIntervals[i] = new JFormattedTextField(hourMask);
			txtFinalIntervals[i].setFont(new Font("Tahoma", Font.PLAIN, 13));
			txtFinalIntervals[i].setBounds(158, y + 25*i, 40, 20);
			txtFinalIntervals[i].setEnabled(false);
			pnlContent.add(txtFinalIntervals[i]);
		}		
	}
	
	private void initPanelContent() {
		pnlContent = new JPanel();
		pnlContent.setLayout(null);
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));	
	}
	
	private void initLabels() {
		lblClient = new JLabel("Cliente: ");
		lblClient.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblClient.setBounds(10, 10, 64, 20);		
		pnlContent.add(lblClient);

		lblAddress = new JLabel("Endereço: ");
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblAddress.setBounds(10, 35, 64, 20);		
		pnlContent.add(lblAddress);
		
		lblWeight = new JLabel("Peso(kg): ");
		lblWeight.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWeight.setBounds(10, 60, 64, 20);		
		pnlContent.add(lblWeight);
		
		lblVolume = new JLabel("Volume(m³): ");
		lblVolume.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblVolume.setBounds(225, 60, 79, 20);		
		pnlContent.add(lblVolume);
		
		lblAvailability = new JLabel("Intervalos de Tempo Disponíveis para Atendimento");
		lblAvailability.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblAvailability.setBounds(10, 85, 311, 20);		
		pnlContent.add(lblAvailability);
	}
	
	private void initTextFields() {
		txtClient = new JTextField();
		txtClient.setColumns(100);
		txtClient.setBounds(84, 11, 330, 20);
		txtClient.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnlContent.add(txtClient);
		
		txtAddress = new JTextField();
		txtAddress.setColumns(255);
		txtAddress.setBounds(84, 35, 330, 20);
		txtAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnlContent.add(txtAddress);
		
		txtWeight = new JTextField();
		txtWeight.setHorizontalAlignment(SwingConstants.RIGHT);
		txtWeight.setColumns(20);
		txtWeight.setBounds(84, 60, 100, 20);
		txtWeight.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnlContent.add(txtWeight);
		
		txtVolume = new JTextField();
		txtVolume.setHorizontalAlignment(SwingConstants.RIGHT);
		txtVolume.setColumns(20);
		txtVolume.setBounds(314, 60, 100, 20);
		txtVolume.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnlContent.add(txtVolume);
		
	}
	
	private void initButtons() {
		btnSave = new JButton("Salvar");
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSave.setBounds(225, 237, 90, 24);
		pnlContent.add(btnSave);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnCancel.setBounds(324, 237, 90, 24);
		pnlContent.add(btnCancel);
	}
	
	public boolean getTypeService() {
		return typeService;
	}

	public JButton getBtnSave() {
		return btnSave;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JTextField getTxtClient() {
		return txtClient;
	}

	public JTextField getTxtAddress() {
		return txtAddress;
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

	public JButton getBtnPlus() {
		return btnPlus;
	}

	public JButton getBtnMinus() {
		return btnMinus;
	}

	public int getNumberOfIntervals() {
		return numberOfIntervals;
	}

	public JFormattedTextField[] getTxtInitialIntervals() {
		return txtInitialIntervals;
	}

	public JFormattedTextField[] getTxtFinalIntervals() {
		return txtFinalIntervals;
	}	
}
