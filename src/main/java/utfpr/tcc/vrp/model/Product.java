package utfpr.tcc.vrp.model;

import org.jdom2.Element;

public class Product {

	/**
	 * Weight of the Product.
	 */
	private double weight;
	
	/**
	 * Volume of the Product.
	 */
	private double volume;
	
	public Product(double weight, double volume) {
		this.weight = weight;
		this.volume = volume;
	}
	
	public Product(Element eProduct) {
		this.weight = Double.parseDouble(eProduct.getAttributeValue("weight"));
		this.volume = Double.parseDouble(eProduct.getAttributeValue("volume"));
	}

	public double getWeight() {
		return weight;
	}
	
	public String getWeightString() {
		String w = Double.toString(weight).replace('.', ',');
		return w;		
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getVolume() {
		return volume;
	}
	
	public String getVolumeString() {
		String v = Double.toString(volume).replace('.', ',');
		return v;		
	}

	public void setVolume(double volume) {
		this.volume = volume;
	} 
	
	public Element getJDomElement() {
		Element eProduct = new Element("product");
		eProduct.setAttribute("weight", Double.toString(weight));
		eProduct.setAttribute("volume", Double.toString(volume));
		
		return eProduct;
	}
	
	
}
