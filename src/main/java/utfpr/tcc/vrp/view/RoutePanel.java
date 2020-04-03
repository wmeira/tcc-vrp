package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.http.HttpException;

import utfpr.tcc.vrp.model.Itinerary;
import utfpr.tcc.vrp.model.Node;
import utfpr.tcc.vrp.model.Route;
import utfpr.tcc.vrp.model.Scenario;
import utfpr.tcc.vrp.model.Vehicle;
import utfpr.tcc.vrp.service.BingMapsRestServices;

public class RoutePanel extends JPanel {

	public static final int IMAGE_WIDTH = 500;
	public static final int IMAGE_HEIGHT = 400;
	
	
	public static final String TABLE_ID_COLUMN = "ID";	
	public static final String TABLE_CLIENTE_COLUMN = "Cliente"; 
	public static final String TABLE_TIPO_COLUMN = "Tipo"; 
	public static final String TABLE_ARRIVAL_TIME_COLUMN = "T. Chegada"; 
	public static final String TABLE_DEPARTURE_TIME_COLUMN = "T. Saída"; 
	public static final String TABLE_WEIGHT_COLUMN = "Peso(kg)"; 
	public static final String TABLE_VOLUME_COLUMN = "Volume(m³)"; 
	public static final String TABLE_TOOLTIP_COLUMN = ""; //invisible
	
	public static final Object[] COLUMN_IDENTIFIERS_ROUTE = new Object[] {	TABLE_ID_COLUMN, 
																				TABLE_CLIENTE_COLUMN,
																				TABLE_TIPO_COLUMN,
																				TABLE_ARRIVAL_TIME_COLUMN,
																				TABLE_DEPARTURE_TIME_COLUMN,
																				TABLE_WEIGHT_COLUMN,
																				TABLE_VOLUME_COLUMN,
																				TABLE_TOOLTIP_COLUMN};
	
	private ImagePanel mapPanel;	
	private JTable tblRoute;
	private JTabbedPane tabPaths;
	private JPanel pnlVehicle;
	private JLabel lblWeightCapacity;
	private JLabel lblVolumeCapacity;
	private JLabel lblTotalTimeTraveled;
	private JLabel lblTotalTravelDistance;
	private JLabel lblHowToGet;
	private JLabel lblVehicleInformations;
	
	private BufferedImage bufferedImage;
	private Route route;
	private Scenario scenario;
	
	private DefaultTableModel modelTableRoute;
	
	public RoutePanel(Scenario scenario, Route route) throws URISyntaxException, HttpException, IOException {
		
		this.scenario = scenario;
		this.route = route;		
		
		bufferedImage = BingMapsRestServices.getStaticMap(route.getWayPointList(), IMAGE_WIDTH, IMAGE_HEIGHT);
		mapPanel = new ImagePanel(bufferedImage);
		mapPanel.setBounds(10,10, IMAGE_WIDTH, IMAGE_HEIGHT);
		mapPanel.setLayout(null);
		mapPanel.setVisible(true);
		this.add(mapPanel);
		
		lblVehicleInformations = new JLabel("Informações do Veículo:");
		lblVehicleInformations.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblVehicleInformations.setBounds(533, 11, 170, 20);
		add(lblVehicleInformations);
		
		initPanelVehicle();		
		
		lblHowToGet = new JLabel("Como chegar:");
		lblHowToGet.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblHowToGet.setBounds(533, 153, 104, 20);
		add(lblHowToGet);		
		
		initTabbedPaneHowToGet();
		
		initTable();		
		
		this.setLayout(null);
		this.setForeground(Color.BLACK);
		this.setBounds(0, 0,  1000, 587);		
	}
	
	private void initPanelVehicle() {
		
		pnlVehicle = new JPanel();
		pnlVehicle.setBounds(533, 36, 452, 106);		
		pnlVehicle.setLayout(null);		
		
		lblWeightCapacity = new JLabel();
		lblWeightCapacity.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWeightCapacity.setBounds(10, 5, 424, 20);
		pnlVehicle.add(lblWeightCapacity);
		
		lblVolumeCapacity = new JLabel();
		lblVolumeCapacity.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblVolumeCapacity.setBounds(10, 30, 424, 20);
		pnlVehicle.add(lblVolumeCapacity);
				
		lblTotalTimeTraveled = new JLabel();
		lblTotalTimeTraveled.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTotalTimeTraveled.setBounds(10, 55, 424, 20);
		pnlVehicle.add(lblTotalTimeTraveled);
		
		lblTotalTravelDistance = new JLabel();
		lblTotalTravelDistance.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTotalTravelDistance.setBounds(10, 80, 424, 20);
		pnlVehicle.add(lblTotalTravelDistance);
		
		setVehicleInformations();
		this.add(pnlVehicle);
	}
	
	private void setVehicleInformations() {
		Vehicle vehicle = route.getVehicle();
		lblWeightCapacity.setText("Capacidade de Peso: " + vehicle.getWeightCapacityString() + " kg");	
		lblVolumeCapacity.setText("Capacidade de Volume: " + vehicle.getVolumeCapacityString() + " m³");
		double number = Math.floor(route.getTotalDistanceTraveled()*1000)/1000;
		String numberFormatted = Double.toString(number).replace(".",",");
		lblTotalTimeTraveled.setText("Distância Total de Viagem: " + numberFormatted + " km");
		number = Math.floor(route.getTotalTimeTraveled()*1000)/1000;
		numberFormatted = Double.toString(number).replace(".", ",");
		lblTotalTravelDistance.setText("Tempo Total de Viagem: " + numberFormatted + " minuto(s)");
	}
	
	private void initTable() {
		
		modelTableRoute = new DefaultTableModel() {		    
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			boolean[] columnEditables = new boolean[] {
					false, false
				};
		}; 		
		modelTableRoute.setColumnIdentifiers(COLUMN_IDENTIFIERS_ROUTE);
		Object[][] obj = route.getTableRoute();
		for(int i = 0; i < obj.length; i++) {
			modelTableRoute.addRow(obj[i]);			
		}		
		
		
		tblRoute = new JTable()  {
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
	            jc.setToolTipText(modelTableRoute.getValueAt(row, 7).toString());
	            Vehicle vehicle = route.getVehicle();
	            
            	jc.setFont(new Font("Tahoma", Font.PLAIN, 13));
            	jc.setForeground(Color.BLACK);
            	
            	if(column==3 && row > 0) { //It just matter if you arrive out of time, it has no problem with departure out of the time window.
            		String value = modelTableRoute.getValueAt(row, column).toString();
            		boolean inside;
            		if(row == route.getServices().size() + 1) {
            			inside = route.getDeposit().isInsideTimeWindows(value);
            		} else {
            			inside = route.getServices().get(row-1).isInsideTimeWindows(value);
            		}
            		
            		if(!inside) {
		            	jc.setFont(new Font("Tahoma", Font.BOLD, 13));
		            	jc.setForeground(Color.RED);
            		}
            	}
            	else if(column==5) {
	            	Double value = Double.parseDouble(modelTableRoute.getValueAt(row, column).toString().replace(",","."));
	            	if(value > vehicle.getWeightCapacity()) {
		            	jc.setFont(new Font("Tahoma", Font.BOLD, 13));
		            	jc.setForeground(Color.RED);
	            	}
	            } else if(column==6) {
	            	Double value = Double.parseDouble(modelTableRoute.getValueAt(row, column).toString().replace(",","."));
	            	if(value > vehicle.getVolumeCapacity()) {
		            	jc.setFont(new Font("Tahoma", Font.BOLD, 13));
		            	jc.setForeground(Color.RED);	            		
	            	}
	            }
	            
		        return returnComp;
		    }
		};
		tblRoute.setShowGrid(false);
		tblRoute.setAlignmentY(CENTER_ALIGNMENT);
		tblRoute.setAlignmentY(CENTER_ALIGNMENT);
		tblRoute.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		tblRoute.setModel(modelTableRoute);
		tblRoute.getColumnModel().getColumn(0).setPreferredWidth(20);
		tblRoute.getColumnModel().getColumn(1).setPreferredWidth(100);
		tblRoute.getColumnModel().getColumn(2).setPreferredWidth(40);
		tblRoute.getColumnModel().getColumn(3).setPreferredWidth(50);
		tblRoute.getColumnModel().getColumn(4).setPreferredWidth(50);
		tblRoute.getColumnModel().getColumn(5).setPreferredWidth(70);
		tblRoute.getColumnModel().getColumn(6).setPreferredWidth(70);
		
		DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();
	    {
	    	renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
	    }
	    
	    tblRoute.getColumnModel().getColumn(0).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(1).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(2).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(3).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(4).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(5).setCellRenderer(renderCenter);
	    tblRoute.getColumnModel().getColumn(6).setCellRenderer(renderCenter);	
	    
	    tblRoute.removeColumn(tblRoute.getColumnModel().getColumn(7));
	    
	    tblRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
	    
		JScrollPane scrollPane = new JScrollPane(tblRoute);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setLocation(10, 421);		
		scrollPane.setSize(975, 133);
		add(scrollPane);
	}

	private void initTabbedPaneHowToGet() {
		tabPaths = new JTabbedPane(JTabbedPane.TOP);
		tabPaths.setBounds(543, 184, 440, 226);
		ItineraryPanel itineraryPanel;
		Itinerary itinerary;
		String title = ""; 
		
		char id = 'A';
		char idTo;
		Node fromNode = route.getDeposit();
		
		Node toNode;
		for(int i = 0; i < route.getServices().size(); i++) {
			idTo = (char) (id+1);
			toNode = route.getServices().get(i);
			itinerary = scenario.getItinerary(fromNode, toNode);
			itineraryPanel = new ItineraryPanel(itinerary.getTypes(), itinerary.getInstructions());
			title = id + "->" + idTo;
			tabPaths.addTab(title, itineraryPanel);
			
			id++;
			fromNode = toNode;
		}
		
		toNode = route.getDeposit();		
		itinerary = scenario.getItinerary(fromNode, toNode);
		itineraryPanel = new ItineraryPanel(itinerary.getTypes(), itinerary.getInstructions());
		idTo = (char) (id+1);
		title = id + "->" + idTo;
		tabPaths.addTab(title, itineraryPanel);
		
		this.add(tabPaths);	
	}
}
