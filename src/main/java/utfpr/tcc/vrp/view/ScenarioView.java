package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import utfpr.tcc.vrp.model.ScenarioModel;
import utfpr.tcc.vrp.prop.Path;
import utfpr.tcc.vrp.solver.model.MathematicalModel;

public class ScenarioView extends javax.swing.JFrame {
	
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 300;	
	public static final String DEFAULT_TITLE = 	"TRABALHO DE CONCLUSÃO DE CURSO - William Hitoshi Tsunoda Meira";  
	public static final String SOLVING_TITLE = "Solucionando cenário... (o processo poderá levar alguns minutos!)";
	public static final String MAPPING_TITLE = "Solução Completa. Gerando mapas de rota...";
	
	public static final String TABLE_DETAIL_NAME_COLUMN = "details";
	public static final String TABLE_DETAIL_VALUE_COLUMN = "values";	
	public static final Object[] COLUMN_IDENTIFIERS_SCENARIO_DETAILS = new Object[] {TABLE_DETAIL_NAME_COLUMN, TABLE_DETAIL_VALUE_COLUMN};
	
	private JPanel pnlContent;
	
	private JLabel lblScenarios;
	private JLabel lblDetails;
	private JLabel lblModel;
	
	private JList listScenarios;
	private JScrollPane scrollScenarios;
	
	private JTable tblDetails;
	
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnCreate;
	private JButton btnSolve;	
	
	private JComboBox cbxModel;
	
	private JProgressBar progressBar;
	
	private ImagePanel pnlBingMaps;
	

	private ScenarioModel scenarioModel;
	
	public ScenarioView(ScenarioModel scenarioModel) {
		
		this.scenarioModel = scenarioModel;		
		initComponents();		
	}
	
	private void initComponents() {
		
		Path path = Path.getInstance();
		
		cbxModel = new JComboBox(MathematicalModel.values());
		cbxModel.setBounds(371, 219, 174, 26);
		
		BufferedImage bingMapsLogo = null;
		String pathIcons = Path.getInstance().getIconsPath();
		try {
			bingMapsLogo = ImageIO.read(new File(pathIcons + "bingMaps.png"));
		} catch (IOException e) {
			System.out.println("ERRO");
		}
		pnlBingMaps = new ImagePanel(bingMapsLogo);
		pnlBingMaps.setBounds(654, 20, 101, 28);
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setIndeterminate(false);
		progressBar.setBounds(20, 11, 735, 7);
	
		initLabels();		
		initListScenarios();
		initTableDetails();
		initButtons(path.getIconsPath());
		initPanel();
		
		pnlContent.add(pnlBingMaps);
		
		this.setContentPane(pnlContent);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(path.getIconsPath() + "truck.png"));
		this.setTitle(DEFAULT_TITLE);  
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);              
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);  
        this.setResizable(false);
        centerFrame();
        
	}		
	
	private void centerFrame() {
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - this.getWidth()) / 2;
        final int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y); 
	}
	
	private void initLabels() {
		
		lblScenarios = new JLabel("Lista de Cenários");		
		lblScenarios.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblScenarios.setBounds(20, 11, 269, 40);		
		
		lblDetails = new JLabel("Detalhes");
		lblDetails.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDetails.setBounds(371, 11, 269, 40);
		
		lblModel = new JLabel("Modelo");
		lblModel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblModel.setBounds(371, 195, 174, 23);
		
		
	}
	
	private void initListScenarios() {
		listScenarios = new JList(scenarioModel.getScenarios().keySet().toArray());
		listScenarios.setFont(new Font("Tahoma", Font.PLAIN, 13));			
		listScenarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		scrollScenarios = new JScrollPane(listScenarios, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollScenarios.setBounds(20, 51, 269, 210);
	}
	
	private void initTableDetails() {

		DefaultTableModel modelTableDetails = new DefaultTableModel() {		    
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			boolean[] columnEditables = new boolean[] {
					false, false
				};
		}; 		
		
		modelTableDetails.setColumnIdentifiers(COLUMN_IDENTIFIERS_SCENARIO_DETAILS);
		/*modelTableDetails.addColumn(TABLE_DETAIL_NAME_COLUMN);
		modelTableDetails.addColumn(TABLE_DETAIL_VALUE_COLUMN);		*/
		
		tblDetails = new JTable() {
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		        Component returnComp = super.prepareRenderer(renderer, row, column);
		        Color alternateColor = DialogView.ALTERNATE_COLOR;
		        Color whiteColor = Color.WHITE;
		        if (!returnComp.getBackground().equals(getSelectionBackground())){
		            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
		            returnComp .setBackground(bg);
		            bg = null;
		        }		        	            
		        return returnComp;
		    }
		};
		
		tblDetails.setShowGrid(false);			
		
		tblDetails.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tblDetails.setModel(modelTableDetails);
		tblDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblDetails.getColumnModel().getColumn(0).setPreferredWidth(150);
		tblDetails.getColumnModel().getColumn(1).setPreferredWidth(200);
		tblDetails.setRowHeight(21);
		tblDetails.setBounds(371, 51, 384, 123);
		tblDetails.setEnabled(false);
	}
	
	private void initButtons(String iconsPath) {
		btnEdit = new JButton();
		btnEdit.setToolTipText("Editar Cenário Selecionado");
		btnEdit.setIcon(new ImageIcon(iconsPath + "edit.png"));
		btnEdit.setBounds(299, 102, 48, 40);
		
		
		btnDelete = new JButton();	
		btnDelete.setToolTipText("Excluir Cenário Selecionado");
		btnDelete.setIcon(new ImageIcon(iconsPath + "delete.png"));
		btnDelete.setBounds(299, 153, 48, 40);
		
		
		btnCreate = new JButton();
		btnCreate.setToolTipText("Criar Novo Cenário");
		btnCreate.setIcon(new ImageIcon(iconsPath + "create.png"));
		btnCreate.setBounds(299, 51, 48, 40);
		
		
		btnSolve = new JButton("Solucionar");
		btnSolve.setToolTipText("Solucionar Cenário Selecionado");
		btnSolve.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSolve.setIcon(new ImageIcon(iconsPath + "solve.png"));
		btnSolve.setBounds(555, 202, 200, 59);
		
	}
	
	private void initPanel() {
		pnlContent = new JPanel();		
		pnlContent.setLayout(null);	
		
		pnlContent.add(lblScenarios);
		pnlContent.add(lblDetails);
		pnlContent.add(lblModel);
		
		pnlContent.add(scrollScenarios);
		pnlContent.add(tblDetails);
		
		pnlContent.add(btnEdit);
		pnlContent.add(btnDelete);
		pnlContent.add(btnCreate);
		pnlContent.add(btnSolve);		
		
		pnlContent.add(cbxModel);
		pnlContent.add(progressBar);
	}
	
	public JList getListScenarios() {
		return listScenarios;
	}

	public JTable getTblDetails() {
		return tblDetails;
	}

	public JButton getBtnEdit() {
		return btnEdit;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}

	public JButton getBtnCreate() {
		return btnCreate;
	}

	public JButton getBtnSolve() {
		return btnSolve;
	}
	
	public JPanel getPnlContent() {
		return pnlContent;
	}

	public JComboBox getCbxModel() {
		return cbxModel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}
}
