package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import utfpr.tcc.vrp.prop.Path;

public class SolutionView extends DialogView {
	
	public static final int SCREEN_WIDTH = 1024;
	public static final int SCREEN_HEIGHT = 768;
	
	private JPanel pnlContent;
	private JPanel pnlInfo;
	private JPanel pnlRoutes;
	
	private JLabel lblTotalTimeTraveled;
	private JLabel lblTotalDistanceTraveled;
	private JLabel lblExecutionTime;
	private JLabel lblStatus;
	private JLabel lblProblemas;
	private JLabel lblSolutionInfo;
	private JLabel lblRoutes;
	
	private JTextArea txtProblems;
	
	private JTabbedPane tabRoutes;
	
	private ImagePanel imagePanel;
	private JTable tblVehicle;	
	
	private String iconsPath;

	public SolutionView(JFrame owner) {
		super(owner);
		this.iconsPath = Path.getInstance().getIconsPath();
		
		initComponents();
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public SolutionView(JDialog owner) {
		super(owner);
		setModalityType(ModalityType.APPLICATION_MODAL);
		this.iconsPath = Path.getInstance().getIconsPath();
		
		initComponents();
	}
	
	private void initComponents() {
		
		pnlContent = new JPanel();
		pnlContent.setLayout(null);
		pnlContent.setVisible(true);
		
		lblSolutionInfo = new JLabel("Informações Gerais da Solução");
		lblSolutionInfo.setBounds(10, 7, 220, 15);
		lblSolutionInfo.setFont(new Font("Tahoma", Font.BOLD, 13));
		pnlContent.add(lblSolutionInfo);
		
		initPanelInfo();
		
		lblRoutes = new JLabel("Rotas");
		lblRoutes.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblRoutes.setBounds(10, 123, 220, 15);
		pnlContent.add(lblRoutes);
		
		initPanelRoutes();

		this.setContentPane(pnlContent);			
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);	
		centerDialog();			
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(iconsPath + "truck.png"));				
		this.setModal(true);
		this.setResizable(false);
	}
	
	private void initPanelInfo() {
		pnlInfo = new JPanel();
		pnlInfo.setLayout(null);
		pnlInfo.setForeground(Color.BLACK);
		pnlInfo.setBorder(new LineBorder(new Color(192, 192, 192)));
		pnlInfo.setBounds(10, 25, 998, 93);
		//pnlInfo.setVisible(true);
				
		lblTotalTimeTraveled = new JLabel("Tempo Total de Viagem: ");
		lblTotalTimeTraveled.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTotalTimeTraveled.setBounds(10, 5, 375, 15);
		pnlInfo.add(lblTotalTimeTraveled);
		
		lblTotalDistanceTraveled = new JLabel("Distância Total de Viagem: ");
		lblTotalDistanceTraveled.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTotalDistanceTraveled.setBounds(10, 25, 375, 15);
		pnlInfo.add(lblTotalDistanceTraveled);
		
		lblExecutionTime = new JLabel("Tempo de Execução: ");
		lblExecutionTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblExecutionTime.setBounds(10, 65, 375, 15);
		pnlInfo.add(lblExecutionTime);
		
		lblStatus = new JLabel("Estado da Solução: ");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblStatus.setBounds(10, 45, 375, 15);
		pnlInfo.add(lblStatus);
		
		lblProblemas = new JLabel("Problemas: ");
		lblProblemas.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblProblemas.setBounds(395, 5, 76, 15);
		pnlInfo.add(lblProblemas);

		txtProblems = new JTextArea();
		txtProblems.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtProblems.setLineWrap(true);
		txtProblems.setEditable(false);
		txtProblems.setBounds(437, 18, 327, 48);
		txtProblems.setMargin(new Insets(0,2,0,2));
		pnlInfo.add(new JScrollPane(txtProblems));
		
		JScrollPane scrollPane = new JScrollPane(txtProblems);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(395, 21, 593, 61);
		pnlInfo.add(scrollPane);
		
		pnlContent.add(pnlInfo);
	}
	
	private void initPanelRoutes() {
		
		pnlRoutes = new JPanel();
		pnlRoutes.setLayout(null);
		pnlRoutes.setForeground(Color.BLACK);
		pnlRoutes.setBounds(10, 145, 1000, 587);
		
		tabRoutes = new JTabbedPane(JTabbedPane.TOP);
		tabRoutes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		tabRoutes.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabRoutes.setBounds(0, 0, 1000, 587);
		pnlRoutes.add(tabRoutes);
		
		pnlContent.add(pnlRoutes);
	}

	public void setTextTotalTimeTraveled(double totalTimeTraveled) {
		
		String totalTimeTraveledS = Double.toString(totalTimeTraveled).replace(".", ",");
		String text = "Tempo Total de Viagem: " + totalTimeTraveledS + " minuto(s)";
//		if(totalTimeTraveled >= 2) text += " minutos";
//		else text += " minuto";
		
		lblTotalTimeTraveled.setText(text);	
	}	

	public void setTextTotalDistanceTraveled(double totalDistanceTraveled) {
		totalDistanceTraveled = Math.floor(totalDistanceTraveled * 1000)/1000;		
		String totalDistanceTraveledS = Double.toString(totalDistanceTraveled).replace(".", ",");
		lblTotalDistanceTraveled.setText("Distância Total de Viagem: " + totalDistanceTraveledS + " km");
	}
	
	public void setTextExecutionTime(double executionTime) {
		String executionTimeS = Double.toString(executionTime).replace(".", ",");
		String text = "Tempo de Execução: " + executionTimeS + " segundo(s)";
		
//		if(executionTime >= 2) text += " segundos";
//		else text += " segundo";
		
		lblExecutionTime.setText(text);
	}

	public void setTextStatus(String status) {
		lblStatus.setText("Estado da Solução: " + status);
	}

	public JTextArea getTxtProblems() {
		return txtProblems;
	}

	public JTabbedPane getTabRoutes() {
		return tabRoutes;
	}

	public void setTxtProblems(JTextArea txtProblems) {
		this.txtProblems = txtProblems;
	}
}
