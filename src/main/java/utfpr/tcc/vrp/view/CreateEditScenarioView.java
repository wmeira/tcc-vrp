package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;

import utfpr.tcc.vrp.prop.Path;

public class CreateEditScenarioView extends DialogView {
	
	private final static Logger logger = Logger.getLogger(CreateEditScenarioView.class.getName());

	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	
	public static final String CREATE_TITLE = "Criar Cenário";
	public static final String EDIT_TITLE = "Editar Cenário";
	public static final String CALCULATING_ROUTE_TITLE = "Calculando rotas...";
	
	public static final String TABLE_NAME_COLUMN = "Nome";
	public static final String TABLE_CLIENT_COLUMN = "Cliente";
	public static final String TABLE_WEIGHT_COLUMN = "Peso (kg)"; 
	public static final String TABLE_VOLUME_COLUMN = "Volume (m³)"; 
	public static final String TABLE_ADDRESS_COLUMN = "Endereço"; 
	public static final String TABLE_AVAILABILITY_COLUMN = "Disponibilidade"; 
	
	public static final Object[] COLUMN_IDENTIFIERS_VEHICLE = new Object[] {	TABLE_NAME_COLUMN, 
			  															TABLE_WEIGHT_COLUMN,
			  															TABLE_VOLUME_COLUMN};
	
	private static final Object[] COLUMN_IDENTIFIERS_SERVICES = new Object[] {	TABLE_CLIENT_COLUMN, 
					 														TABLE_ADDRESS_COLUMN,
					 														TABLE_WEIGHT_COLUMN,
					 														TABLE_VOLUME_COLUMN,
					 														TABLE_AVAILABILITY_COLUMN};
	
	private MaskFormatter dateMask;
	private MaskFormatter hourMask;
	
	private JPanel pnlContent;
	private JPanel pnlDeposit;
	private JPanel pnlDelivery;
	private JPanel pnlPickup;
	private JPanel pnlVehicle;
	
	private JLabel lblNameScenario;
	private JLabel lblDeposit;
	private JLabel lblPickups;
	private JLabel lblDeliveries;
	private JLabel lblVehicles;
	private JLabel lblAddressDeposit;
	private JLabel lblLatitudeDeposit;
	private JLabel lblLongitudeDeposit;
	private JLabel lblOpeningTimeDeposit;
	private JLabel lblClosingTimeDeposit;
	private JLabel lblDateScenario;
	
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnAddVehicle;
	private JButton btnAddPickup;
	private JButton btnAddDelivery;
	private JButton btnDeleteVehicle;
	private JButton btnDeletePickup;
	private JButton btnDeleteDelivery;
	private JButton btnEditVehicle;
	private JButton btnEditDelivery;
	private JButton btnEditPickup;
	private JButton btnGetCoordinatesAddressDeposit;
	
	private JTable tblVehicles;
	private JTable tblDeliveries;
	private JTable tblPickups;
	
	private JTextField txtNameScenario;
	private JFormattedTextField txtDateScenario;
	private JTextField txtAddressDeposit;
	private JTextField txtLatitudeDeposit;
	private JTextField txtLongitudeDeposit;
	private JFormattedTextField txtOpeningTimeDeposit;
	private JFormattedTextField txtClosingTimeDeposit;	
	
	private JProgressBar progressBar;
	
	public CreateEditScenarioView(JFrame owner) {
		super(owner);		
		this.iconsPath = Path.getInstance().getIconsPath();
		
		initComponents();		
	}

	private void initComponents() {
				
		initPanelContent();				
		
		initPanelDeposit();
		initPanelDeposit();
		initPanelVehicle();
		initPanelPickup();
		initPanelDelivery();

		/*
		 * JDialog
		 */		
		this.setContentPane(pnlContent);	
		
		progressBar = new JProgressBar();
		progressBar.setToolTipText("");
		progressBar.setBounds(28, 536, 363, 25);
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		pnlContent.add(progressBar);
		
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);	
		centerDialog();			
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	
		this.setResizable(false);		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(iconsPath + "truck.png"));				
		this.setModal(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);		
	}
	
	private void initPanelVehicle() {
		pnlVehicle = new JPanel();
		pnlVehicle.setLayout(null);
		pnlVehicle.setForeground(Color.BLACK);
		pnlVehicle.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pnlVehicle.setBounds(421, 89, 350, 147);
		pnlVehicle.setVisible(false);
		
		DefaultTableModel modelTableVehicles = new DefaultTableModel() {		    
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			boolean[] columnEditables = new boolean[] {
					false, false
				};
		}; 	
		modelTableVehicles.setColumnIdentifiers(COLUMN_IDENTIFIERS_VEHICLE);
		
		tblVehicles = new JTable()  {		    
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		        Component returnComp = super.prepareRenderer(renderer, row, column);
		        Color alternateColor = DialogView.ALTERNATE_COLOR;
		        Color whiteColor = Color.WHITE;
		        if (!returnComp.getBackground().equals(getSelectionBackground())){
		            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
		            returnComp .setBackground(bg);
		            bg = null;
		        }
		        
	            JComponent jc = (JComponent) returnComp;
	            jc.setToolTipText(getValueAt(row, column).toString());
	            
		        return returnComp;
		    }
		};	
		
		tblVehicles.setShowGrid(false);				
		tblVehicles.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tblVehicles.setModel(modelTableVehicles);
		tblVehicles.getColumnModel().getColumn(0).setPreferredWidth(130);
		tblVehicles.getColumnModel().getColumn(1).setPreferredWidth(85);
		tblVehicles.getColumnModel().getColumn(2).setPreferredWidth(85);
		tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
	    {
	    	renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
	    }
	    
		tblVehicles.getColumnModel().getColumn(1).setCellRenderer(renderCenter);
		tblVehicles.getColumnModel().getColumn(2).setCellRenderer(renderCenter);
		
		JScrollPane scrollPane = new JScrollPane(tblVehicles);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setLocation(10, 11);		
		scrollPane.setSize(300, 126);
		pnlVehicle.add(scrollPane);

		btnAddVehicle = new JButton();
		btnAddVehicle.setIcon(new ImageIcon(iconsPath + "create2.png"));
		btnAddVehicle.setBounds(315, 39, 25, 25);
		pnlVehicle.add(btnAddVehicle);
		
		btnEditVehicle = new JButton();
		btnEditVehicle.setIcon(new ImageIcon(iconsPath + "edit2.png"));
		btnEditVehicle.setBounds(315, 11, 25, 25);
		pnlVehicle.add(btnEditVehicle);
		
		btnDeleteVehicle = new JButton();
		btnDeleteVehicle.setIcon(new ImageIcon(iconsPath + "delete2.png"));
		btnDeleteVehicle.setBounds(315, 67, 25, 25);
		pnlVehicle.add(btnDeleteVehicle);		
		
		pnlContent.add(pnlVehicle);
		
	}
	
	private void initPanelDelivery() {
		pnlDelivery = new JPanel();
		pnlDelivery.setLayout(null);
		pnlDelivery.setForeground(Color.BLACK);
		pnlDelivery.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pnlDelivery.setBounds(28, 265, 743, 115);
		pnlDelivery.setVisible(false);
				
		DefaultTableModel modelTableDeliveries = new DefaultTableModel() {		    
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			boolean[] columnEditables = new boolean[] {
					false, false
				};
		}; 		
		modelTableDeliveries.setColumnIdentifiers(COLUMN_IDENTIFIERS_SERVICES);
		
		tblDeliveries = new JTable()  {
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		        Component returnComp = super.prepareRenderer(renderer, row, column);
		        Color alternateColor = DialogView.ALTERNATE_COLOR;
		        Color whiteColor = Color.WHITE;
		        if (!returnComp.getBackground().equals(getSelectionBackground())){
		            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
		            returnComp .setBackground(bg);
		            bg = null;
		        }
	            JComponent jc = (JComponent) returnComp;
	            jc.setToolTipText(getValueAt(row, column).toString());
	            
		        return returnComp;
		    }
		};
		
		tblDeliveries.setShowGrid(false);				
		tblDeliveries.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tblDeliveries.setModel(modelTableDeliveries);
		tblDeliveries.getColumnModel().getColumn(0).setPreferredWidth(80);
		tblDeliveries.getColumnModel().getColumn(1).setPreferredWidth(240);
		tblDeliveries.getColumnModel().getColumn(2).setPreferredWidth(40);
		tblDeliveries.getColumnModel().getColumn(3).setPreferredWidth(60);
		tblDeliveries.getColumnModel().getColumn(4).setPreferredWidth(140);
		tblDeliveries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	

		DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
	    {
	    	renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
	    }
	    
	    tblDeliveries.getColumnModel().getColumn(2).setCellRenderer(renderCenter);
	    tblDeliveries.getColumnModel().getColumn(3).setCellRenderer(renderCenter);
		
		JScrollPane scrollPane = new JScrollPane(tblDeliveries);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setLocation(10, 11);		
		scrollPane.setSize(693, 93);
		pnlDelivery.add(scrollPane);

		btnAddDelivery = new JButton();
		btnAddDelivery.setIcon(new ImageIcon(iconsPath + "create2.png"));
		btnAddDelivery.setBounds(708, 39, 25, 25);
		pnlDelivery.add(btnAddDelivery);
		
		btnEditDelivery = new JButton();
		btnEditDelivery.setIcon(new ImageIcon(iconsPath + "edit2.png"));
		btnEditDelivery.setBounds(708, 11, 25, 25);
		pnlDelivery.add(btnEditDelivery);
		
		btnDeleteDelivery = new JButton();
		btnDeleteDelivery.setIcon(new ImageIcon(iconsPath + "delete2.png"));
		btnDeleteDelivery.setBounds(708, 67, 25, 25);
		pnlDelivery.add(btnDeleteDelivery);		
		
		pnlContent.add(pnlDelivery);
		
	}

	private void initPanelPickup() {
		pnlPickup = new JPanel();
		pnlPickup.setLayout(null);
		pnlPickup.setForeground(Color.BLACK);
		pnlPickup.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pnlPickup.setBounds(28, 409, 743, 115);
		pnlPickup.setVisible(false);
		
		DefaultTableModel modelTablePickups = new DefaultTableModel() {		    
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			boolean[] columnEditables = new boolean[] {
					false, false
				};
		}; 		
		
		modelTablePickups.setColumnIdentifiers(COLUMN_IDENTIFIERS_SERVICES);
		
		tblPickups = new JTable()  {
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		        Component returnComp = super.prepareRenderer(renderer, row, column);
		        Color alternateColor = DialogView.ALTERNATE_COLOR;
		        Color whiteColor = Color.WHITE;
		        if (!returnComp.getBackground().equals(getSelectionBackground())){
		            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
		            returnComp .setBackground(bg);
		            bg = null;
		        }
	            JComponent jc = (JComponent) returnComp;
	            jc.setToolTipText(getValueAt(row, column).toString());
	            
		        return returnComp;
		    }
		};
		
		tblPickups.setShowGrid(false);				
		tblPickups.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tblPickups.setModel(modelTablePickups);
		tblPickups.getColumnModel().getColumn(0).setPreferredWidth(80);
		tblPickups.getColumnModel().getColumn(1).setPreferredWidth(240);
		tblPickups.getColumnModel().getColumn(2).setPreferredWidth(40);
		tblPickups.getColumnModel().getColumn(3).setPreferredWidth(60);
		tblPickups.getColumnModel().getColumn(4).setPreferredWidth(140);
		tblPickups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	

		DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
	    {
	    	renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
	    }
	    
	    tblPickups.getColumnModel().getColumn(2).setCellRenderer(renderCenter);
	    tblPickups.getColumnModel().getColumn(3).setCellRenderer(renderCenter);
	    
		JScrollPane scrollPane = new JScrollPane(tblPickups);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setLocation(10, 11);		
		scrollPane.setSize(693, 93);
		pnlPickup.add(scrollPane);

		btnAddPickup = new JButton();
		btnAddPickup.setIcon(new ImageIcon(iconsPath + "create2.png"));
		btnAddPickup.setBounds(708, 39, 25, 25);
		pnlPickup.add(btnAddPickup);
		
		btnEditPickup = new JButton();
		btnEditPickup.setIcon(new ImageIcon(iconsPath + "edit2.png"));
		btnEditPickup.setBounds(708, 11, 25, 25);
		pnlPickup.add(btnEditPickup);
		
		btnDeletePickup = new JButton();
		btnDeletePickup.setIcon(new ImageIcon(iconsPath + "delete2.png"));
		btnDeletePickup.setBounds(708, 67, 25, 25);
		pnlPickup.add(btnDeletePickup);		
		
		pnlContent.add(pnlPickup);
	}

	private void initPanelDeposit() {
		pnlDeposit = new JPanel();
		pnlDeposit.setForeground(Color.BLACK);
		pnlDeposit.setBorder(new LineBorder(Color.LIGHT_GRAY));
		pnlDeposit.setBounds(28, 89, 350, 147);		
		pnlDeposit.setLayout(null);
		pnlDeposit.setVisible(false);
		
		txtAddressDeposit = new JTextField();
		txtAddressDeposit.setColumns(255);
		txtAddressDeposit.setBounds(10, 27, 330, 20);
		pnlDeposit.add(txtAddressDeposit);
		txtAddressDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		txtLatitudeDeposit = new JTextField();
		txtLatitudeDeposit.setColumns(20);
		txtLatitudeDeposit.setEditable(false);
		txtLatitudeDeposit.setBounds(69, 52, 76, 20);
		pnlDeposit.add(txtLatitudeDeposit);
		txtLatitudeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		txtLongitudeDeposit = new JTextField();
		txtLongitudeDeposit.setColumns(20);
		txtLongitudeDeposit.setEditable(false);
		txtLongitudeDeposit.setBounds(228, 52, 76, 20);	
		pnlDeposit.add(txtLongitudeDeposit);
		txtLongitudeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		try {
            hourMask = new MaskFormatter("##:##");
            hourMask.setPlaceholderCharacter('0');
        } catch (ParseException ex) {
        	logger.severe("Erro ao colocar máscara no campo horário de " +
            		"aberura/fechamento do depósito central.");
        }		
		
		txtOpeningTimeDeposit = new JFormattedTextField(hourMask);
		txtOpeningTimeDeposit.setToolTipText("Formato 24 horas");
		txtOpeningTimeDeposit.setHorizontalAlignment(SwingConstants.CENTER);
		txtOpeningTimeDeposit.setColumns(5);
		txtOpeningTimeDeposit.setBounds(159, 83, 42, 20);
		pnlDeposit.add(txtOpeningTimeDeposit);
		txtOpeningTimeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		txtClosingTimeDeposit = new JFormattedTextField(hourMask);
		txtClosingTimeDeposit.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		txtClosingTimeDeposit.setToolTipText("Formato 24 horas");
		txtClosingTimeDeposit.setHorizontalAlignment(SwingConstants.CENTER);
		txtClosingTimeDeposit.setColumns(5);
		txtClosingTimeDeposit.setBounds(159, 118, 42, 20);
		pnlDeposit.add(txtClosingTimeDeposit);
		txtClosingTimeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		lblAddressDeposit = new JLabel("Endereço:");
		lblAddressDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblAddressDeposit.setBounds(10, 7, 76, 20);
		pnlDeposit.add(lblAddressDeposit);
		
		lblLatitudeDeposit = new JLabel("Latitude:");
		lblLatitudeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblLatitudeDeposit.setBounds(10, 52, 60, 20);
		pnlDeposit.add(lblLatitudeDeposit);
		
		lblLongitudeDeposit = new JLabel("Longitude:");
		lblLongitudeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblLongitudeDeposit.setBounds(159, 52, 60, 20);
		pnlDeposit.add(lblLongitudeDeposit);
		
		lblOpeningTimeDeposit = new JLabel("Horário de Abertura:");
		lblOpeningTimeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOpeningTimeDeposit.setBounds(10, 83, 131, 20);
		pnlDeposit.add(lblOpeningTimeDeposit);
		
		lblClosingTimeDeposit = new JLabel("Horário de Fechamento:");
		lblClosingTimeDeposit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblClosingTimeDeposit.setBounds(10, 118, 139, 20);
		pnlDeposit.add(lblClosingTimeDeposit);
		
		btnGetCoordinatesAddressDeposit = new JButton();
		btnGetCoordinatesAddressDeposit.setToolTipText("Procurar Coordenadas do Endereço");
		btnGetCoordinatesAddressDeposit.setBounds(314, 52, 20, 20);
		btnGetCoordinatesAddressDeposit.setIcon(new ImageIcon(iconsPath + "search.png"));
		pnlDeposit.add(btnGetCoordinatesAddressDeposit);
				
		pnlContent.add(pnlDeposit);
	}

	private void initPanelContent() {
		pnlContent = new JPanel();
		pnlContent.setLayout(null);
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));	
		pnlContent.setVisible(false);
		
		lblNameScenario = new JLabel("Nome do Cenário");
		lblNameScenario.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNameScenario.setBounds(10, 11, 117, 20);
		pnlContent.add(lblNameScenario);
		
		txtNameScenario = new JTextField();
		txtNameScenario.setColumns(100);
		txtNameScenario.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtNameScenario.setBounds(28, 31, 254, 20);	
		pnlContent.add(txtNameScenario);
		
		lblDateScenario = new JLabel("Data do Cenário");
		lblDateScenario.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDateScenario.setBounds(400, 11, 117, 20);
		pnlContent.add(lblDateScenario);
		
		try {
            dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('-');
        } catch (ParseException ex) {
        	logger.severe("Erro ao colocar máscara no campo data do cenário.");
        }		
		
		txtDateScenario = new JFormattedTextField(dateMask);
		txtDateScenario.setHorizontalAlignment(SwingConstants.CENTER);
		txtDateScenario.setColumns(15);
		txtDateScenario.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtDateScenario.setBounds(421, 31, 76, 20);			
		pnlContent.add(txtDateScenario);
		
		lblDeposit = new JLabel("Depósito Central");
		lblDeposit.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDeposit.setBounds(10, 62, 108, 16);			
		pnlContent.add(lblDeposit);

		lblPickups = new JLabel("Coletas");
		lblPickups.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblPickups.setBounds(10, 383, 76, 20);
		pnlContent.add(lblPickups);
		
		lblDeliveries = new JLabel("Entregas");
		lblDeliveries.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDeliveries.setBounds(10, 239, 76, 20);
		pnlContent.add(lblDeliveries);
		
		lblVehicles = new JLabel("Veículos");
		lblVehicles.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblVehicles.setBounds(400, 60, 95, 20);
		pnlContent.add(lblVehicles);
		
		btnSave = new JButton("Salvar");
		btnSave.setBounds(401, 536, 180, 25);
		pnlContent.add(btnSave);
		btnSave.setToolTipText("Salvar Cenário");
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		btnCancel = new JButton("Cancelar");
		btnCancel.setBounds(591, 536, 180, 25);
		pnlContent.add(btnCancel);
		btnCancel.setToolTipText("Cancelar Alterações");
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 13));
		
	}

	public void setAllVisible() {		
		pnlDeposit.setVisible(true);	
		pnlDelivery.setVisible(true);
		pnlPickup.setVisible(true);
		pnlVehicle.setVisible(true);		
		pnlContent.setVisible(true);
		this.setVisible(true);
	}


	public JButton getBtnSave() {
		return btnSave;
	}
	
	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JButton getBtnAddVehicle() {
		return btnAddVehicle;
	}

	public JButton getBtnAddPickup() {
		return btnAddPickup;
	}

	public JButton getBtnAddDelivery() {
		return btnAddDelivery;
	}

	public JButton getBtnDeleteVehicle() {
		return btnDeleteVehicle;
	}

	public JButton getBtnDeletePickup() {
		return btnDeletePickup;
	}

	public JButton getBtnDeleteDelivery() {
		return btnDeleteDelivery;
	}

	public JButton getBtnEditVehicle() {
		return btnEditVehicle;
	}

	public JButton getBtnEditDelivery() {
		return btnEditDelivery;
	}

	public JButton getBtnEditPickup() {
		return btnEditPickup;
	}

	public JButton getBtnGetCoordinatesAddressDeposit() {
		return btnGetCoordinatesAddressDeposit;
	}

	public JTable getTblVehicles() {
		return tblVehicles;
	}

	public JTable getTblDeliveries() {
		return tblDeliveries;
	}

	public JTable getTblPickups() {
		return tblPickups;
	}

	public JTextField getTxtNameScenario() {
		return txtNameScenario;
	}

	public JFormattedTextField getTxtDateScenario() {
		return txtDateScenario;
	}

	public JTextField getTxtAddressDeposit() {
		return txtAddressDeposit;
	}

	public JTextField getTxtLatitudeDeposit() {
		return txtLatitudeDeposit;
	}

	public JTextField getTxtLongitudeDeposit() {
		return txtLongitudeDeposit;
	}

	public JFormattedTextField getTxtOpeningTimeDeposit() {
		return txtOpeningTimeDeposit;
	}

	public JFormattedTextField getTxtClosingTimeDeposit() {
		return txtClosingTimeDeposit;
	}

	public JPanel getPnlContent() {
		return pnlContent;
	}

	public JPanel getPnlDeposit() {
		return pnlDeposit;
	}

	public JPanel getPnlDelivery() {
		return pnlDelivery;
	}

	public JPanel getPnlPickup() {
		return pnlPickup;
	}

	public JPanel getPnlVehicle() {
		return pnlVehicle;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
}
