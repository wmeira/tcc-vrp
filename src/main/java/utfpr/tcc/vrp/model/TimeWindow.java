package utfpr.tcc.vrp.model;

import java.text.NumberFormat;

import org.jdom2.Element;

public class TimeWindow {
	
	/**
	 * Initial Hour of the Time Window.
	 */
	private int initialHour;
	
	/**
	 * Initial Minute of the Time Window.
	 */
	private int initialMinute;
	
	/**
	 * Final Hour of the Time Window.
	 */
	private int finalHour;
	
	/**
	 * Final Minute of the Time Window.
	 */
	private int finalMinute;
	
	private NumberFormat nf;
	
	public TimeWindow(String initialTime, String finalTime) {
		String[] time = initialTime.split(":");
		this.initialHour = Integer.parseInt(time[0]);
		this.initialMinute = Integer.parseInt(time[1]);
		time = finalTime.split(":");
		this.finalHour = Integer.parseInt(time[0]);
		this.finalMinute = Integer.parseInt(time[1]);

		this.nf = NumberFormat.getInstance();	
		nf.setMinimumIntegerDigits(2);
	}
	public TimeWindow(int initialHour, int initialMinute, int finalHour, int finalMinute) {
		this.initialHour = initialHour;
		this.initialMinute = initialMinute;
		this.finalHour = finalHour;
		this.finalMinute = finalMinute;
		
		this.nf = NumberFormat.getInstance();	
		nf.setMinimumIntegerDigits(2);
	}	
	
	public TimeWindow(Element eTimeWindow) {
		String from = eTimeWindow.getAttributeValue("from");
		String to = eTimeWindow.getAttributeValue("to");
		
		setInitialTime(from);
		setFinalTime(to);		
		
		this.nf = NumberFormat.getInstance();	
		nf.setMinimumIntegerDigits(2);
	}
	
	/**
	 * Set initialHour and initialMinute passing a string using the format: hh:mm
	 * @param initialTime String time in the format hh:mm
	 */
	public void setInitialTime(String initialTime) {
		String[] time = initialTime.split(":");
		String hour = time[0];
		String minute = time[1];
		
		this.initialHour = Integer.parseInt(hour);
		this.initialMinute = Integer.parseInt(minute);
	}
	
	/**
	 * Set finalHour and finalMinute passing a string using the format: hh:mm
	 * @param finalTime String time in the format hh:mm
	 */
	public void setFinalTime(String finalTime) {
		String[] time = finalTime.split(":");
		String hour = time[0];
		String minute = time[1];
		
		this.finalHour = Integer.parseInt(hour);
		this.finalMinute = Integer.parseInt(minute);
	}
	
	public int getInitialHour() {
		return initialHour;
	}

	public void setInitialHour(int initialHour) {
		this.initialHour = initialHour;
	}

	public int getInitialMinute() {
		return initialMinute;
	}

	public void setInitialMinute(int initialMinute) {
		this.initialMinute = initialMinute;
	}

	public int getFinalHour() {
		return finalHour;
	}

	public void setFinalHour(int finalHour) {
		this.finalHour = finalHour;
	}

	public int getFinalMinute() {
		return finalMinute;
	}

	public void setFinalMinute(int finalMinute) {
		this.finalMinute = finalMinute;
	}
	
	public String getInitialTimeFormated() {		
		return nf.format(initialHour) + ":" + nf.format(initialMinute);
	}
	
	public String getFinalTimeFormated() {
		return nf.format(finalHour) + ":" + nf.format(finalMinute);
	}
	
	public Element getJDomElement() {
		Element eTimeWindow = new Element("availability");
		eTimeWindow.setAttribute("from", getInitialTimeFormated());
		eTimeWindow.setAttribute("to", getFinalTimeFormated());
		
		return eTimeWindow;
	}
	
	public int getInitialTimeMinutes() {
		return initialHour*60 + initialMinute;
	}
	
	public int getFinalTimeMinutes() {
		return finalHour*60 + finalMinute;
	}
	
	public void setInitialTimeMinutes(int minutes) {
		initialHour = minutes/60;
		initialMinute = minutes%60;
	}
	
	public void setFinalTimeMinutes(int minutes) {
		finalHour = minutes/60;
		finalMinute = minutes%60;
	}
	
	

}
