package utfpr.tcc.vrp.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

public class ItineraryPanel extends JPanel {

	private List<String> types; 
	private List<String> instructions;
	
	private JList lstInstructions;
	
	public ItineraryPanel(List<String> types, List<String> instructions) {
		this.types = types;
		this.instructions = instructions;
		
		initList();
		
		this.setLayout(null);
		this.setBounds(0, 0,  435, 200);	
		this.setVisible(true);
	}
	
	
	public void initList() {
		
		ListRenderer cellRenderer = new ListRenderer();
		
		lstInstructions = new JList();
		lstInstructions.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lstInstructions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//lstInstructions.setBounds(0,0,435,200);
		lstInstructions.setCellRenderer(cellRenderer);
		
		lstInstructions.setListData(instructions.toArray());
		
		JScrollPane scrollPane = new JScrollPane(lstInstructions);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(2,2,430, 195);
		this.add(scrollPane);
	}
	
	class ListRenderer extends JTextArea implements ListCellRenderer  {
		
		   private int lineHeight = 18;
		   private int charsPerLine = 70;
		   
		   
		   public ListRenderer() {

		   }

		   public Component getListCellRendererComponent(JList list, Object value,
		         int index, boolean isSelected, boolean cellHasFocus) {

		        //Color alternateColor = new Color(252,242,206);
			   	Color alternateColor = new Color(240,240,240);
		        Color whiteColor = Color.WHITE;		 
		        
		        if (index % 2 == 0) setBackground(alternateColor);
		        else setBackground(whiteColor);
		        
		        String text = (index+1) + ". " + value.toString();
		        setMargin(new Insets(2,2,2,2));
		        setPreferredSize(new Dimension (400, lineHeight * (text.length() / charsPerLine) + lineHeight));
		        setLineWrap(true);         
		        setWrapStyleWord(true);
		        setText(" " + text);
		        setFont(list.getFont());      
		        setOpaque(true); 		        
		        return this;
		   }

		}	
}
