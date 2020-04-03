package utfpr.tcc.vrp.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jdom2.JDOMException;

import utfpr.tcc.vrp.exception.AddressMatchException;
import utfpr.tcc.vrp.model.Location;
import utfpr.tcc.vrp.service.BingMapsRestServices;

public class Controller {
	
	protected final Color errorColor = new Color(250, 244, 183);
	
	public Controller() {
		
	}
	
	protected boolean isValidPositiveNumber(String number) {
		String pattern = "^[+]?\\d+([,.]\\d+)?$";
	    Pattern r = Pattern.compile(pattern);	    
	    Matcher m = r.matcher(number);
		
		return m.find();
	}
	
	protected boolean isValidHour(String hour) {
		String[] hourMinute = hour.split(":");
		if(Integer.parseInt(hourMinute[0]) >= 24 || Integer.parseInt(hourMinute[1]) >= 60) {
			return false;
		}
		
		return true;
	}
	
	protected boolean isAfterHour(String first, String second) {
		String[] firstHourMinute = first.split(":");
		String[] secondHourMinute = second.split(":");
		
		if(Integer.parseInt(firstHourMinute[0]) > Integer.parseInt(secondHourMinute[0])) {
			return false;
		}
		
		if(Integer.parseInt(firstHourMinute[0]) == Integer.parseInt(secondHourMinute[0])) {
			if(Integer.parseInt(firstHourMinute[1]) >= Integer.parseInt(secondHourMinute[1])) {
				return false;
			}		
		}
		
		return true;	
	}
	
	protected boolean isDateValid(String date) {
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    sdf.setLenient(false);
	    if(!(sdf.parse(date, new ParsePosition(0)) != null)) {
	    	return false;
	    }
	    
	    return true;
 	}
	
	protected Location getLocation(String address, Logger logger) {
		Location location = null;
		
		if(address.isEmpty()) {
			JOptionPane.showMessageDialog(null,  "Campo endereço está vazio." , "Erro", 
					JOptionPane.ERROR_MESSAGE);		
			return null;
		}
		
		try {
			location = BingMapsRestServices.geocodeAddress(address);
			if(!location.getAddress().contains("Brasil")) {
				JOptionPane.showMessageDialog(null,  "Endereço incorreto ou incompleto.\n" +
						"O endereço deve estar localizado dentro do território brasileiro." , "Erro", 
						JOptionPane.ERROR_MESSAGE);				
				location = null;
			}
		} catch (UnsupportedEncodingException e) {
			throwUnsupportedEncodingException(e, logger);
		} catch (IOException e) {
			throwIOException(e, logger);
		} catch (JDOMException e) {
			throwJDOMException(e, logger);
		} catch (AddressMatchException e) {
			throwAddressMatchException(e, logger);
		}
		
		return location;
	}
	
	protected void throwUnsupportedEncodingException(Exception e, Logger logger) {
		logger.severe("Problema de codificação do endereço.");
		JOptionPane.showMessageDialog(null, "Problema na comunicação com o servidor Bing.\nTente novamente.", "Erro", 
				JOptionPane.ERROR_MESSAGE);			
	}
	
	protected void throwIOException(Exception e, Logger logger) {
		logger.severe("Servidor de serviços REST do Bing não responde.");
		JOptionPane.showMessageDialog(null, "Não foi possível conectar ao serviço Bing para mapear o endereço " +
				"fornecido.\nVerifique a conexão com a internet.\n", "Erro", 
					JOptionPane.ERROR_MESSAGE);		
	}
	
	protected void throwJDOMException(Exception e, Logger logger) {
		logger.severe("Não foi possível converter a string resultado do serviço em XML (JDOM builder).");
		JOptionPane.showMessageDialog(null, "Problema na resposta do servidor Bing.\nTente novamente.", "Erro", 
				JOptionPane.ERROR_MESSAGE);	
	}
	
	protected void throwAddressMatchException(Exception e, Logger logger) {
		logger.warning("Não foi possível localizar o endereço. Insira mais detalhes.");
		JOptionPane.showMessageDialog(null, "Endereço incompleto.\nInsira mais detalhes.", "Erro", 
				JOptionPane.ERROR_MESSAGE);
	}
	
}
