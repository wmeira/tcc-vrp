package utfpr.tcc.vrp.service;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import utfpr.tcc.vrp.exception.AddressMatchException;
import utfpr.tcc.vrp.model.Location;
import utfpr.tcc.vrp.prop.Path;


public class BingMapsRestServices {
	
	private final static Logger logger = Logger.getLogger(BingMapsRestServices.class.getName());	

	/**
	 * Bing Maps Key - Basic. Non-comercial. 
	 */
	private static final String parameterBingKey = "key=" + Path.getInstance().getBingKey();
	
	/**
	 * The culture to use for the request.
	 */
	private static final String parameterCulture = "culture=pt-Br";
	
	/**
	 * The output format of the request.
	 */
	private static final String parameterOutputFormat = "o=xml";
	
	/**
	 * The maximum number of results.
	 */
	private static final String parameterMaxResults = "maxResults=1";
	
	/**
	 * URL to access the "Find a Location by Address" from Bing Maps REST Service. 
	 * 	 */
	private static final String geocoderBingURL = "http://dev.virtualearth.net/REST/v1/Locations?";
	
	/**
	 * URL to access the "Calculate a Route" from Bing Maps REST Service. 
	 * 	 */
	private static final String calculateRouteURL = "http://dev.virtualearth.net/REST/V1/Routes?";
	
	/**
	 * URL to access the "Get a Static Map" from Bing Maps REST Service. 
	 * 	 */
	private static final String staticMapRouteURL = "http://dev.virtualearth.net/REST/v1/Imagery/Map/Road/Routes?";
		
	/**
	 * Earth's radius
	 */
	private static int EARTH_RADIUS = 6371; 
	
	
	/**
	 * Get the complete address and the latitude and longitude (geocoding) from an incomplete address 
	 * information using the Locations Bing Maps REST service.
	 * 
	 * @param address Address to convert to 
	 * @return the result from the Bing Maps Location REST service request.  
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static Location geocodeAddress(String address) throws IOException, JDOMException, UnsupportedEncodingException, AddressMatchException {
				
		String parameterQuery;
		parameterQuery = "query=" + URLEncoder.encode(address, "UTF-8");
		String parametersURL = parameterCulture + 
				"&" + parameterBingKey + 
				"&" + parameterOutputFormat + 
				"&" + parameterMaxResults + 
				"&" + parameterQuery;			
		
		String url = geocoderBingURL + parametersURL;			
		String xml = httpGetREST(url);
		
		/* Regular Expression that strips off any non-word characters in the prolog. 
		Byte order mark - BOM - from UTF-8 are 0xEF,0xBB,0xBF. */			 
		xml = xml.trim().replaceFirst("^([\\W]+)<","<"); 
		
        SAXBuilder builder = new SAXBuilder();	        
		Document document = builder.build(new StringReader(xml));
		
		Element rootNode = document.getRootElement();			
		Element eResourceSets = rootNode.getChild("ResourceSets", rootNode.getNamespace());;
		Element eResourceSet = eResourceSets.getChild("ResourceSet", eResourceSets.getNamespace());
		Element eEstimatedTotal = eResourceSet.getChild("EstimatedTotal", eResourceSet.getNamespace());
		
		if(Integer.parseInt(eEstimatedTotal.getText()) <= 0) {
			throw new AddressMatchException();
		}
		Element eResource = eResourceSet.getChild("Resources", eResourceSet.getNamespace());
		Element eLocation = eResource.getChild("Location", eResource.getNamespace());
		String completeAddress = eLocation.getChildText("Name", eLocation.getNamespace());
		Element ePoint = eLocation.getChild("Point", eLocation.getNamespace());
		double latitude = Double.parseDouble(ePoint.getChildText("Latitude", ePoint.getNamespace()));
		double longitude = Double.parseDouble(ePoint.getChildText("Longitude", ePoint.getNamespace()));
		
		Location location = new Location(completeAddress, latitude, longitude);	
		
		System.out.println(completeAddress);
		System.out.println("Latitude: " + latitude + ". Longitude: " + longitude);
		
		return location;
	}
	
	/**
	 * Get a BufferedImage that represents the Bing map and the vehicle route traced over it.
	 * using the Get Static Map Bing Maps REST service.
	 * 
	 * @param vehicle Vehicle with route.
	 * @return the generated buffered image from Bing Static Map REST service request.  
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws HttpException 
	 */		
	public static BufferedImage getStaticMap(List<Point2D.Double> points, int width, int height) throws URISyntaxException, HttpException, IOException {
		
		BufferedImage imBuff = null;
		
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			for(int i = 0; i < points.size(); i++) {
				Point2D.Double point = points.get(i);
				qparams.add(new BasicNameValuePair("wp." + i, point.getX() + "," + point.getY()));
			}
			qparams.add(new BasicNameValuePair("key", "AsL0kSymn5kuJ92vVKGguuvTY-THO-NqTzH88PES54V3H1Zs132JXL91wg17vj2E"));
			qparams.add(new BasicNameValuePair("mapSize", width + "," + height));
			URI uri = URIUtils.createURI("http", "dev.virtualearth.net", -1, "/REST/v1/Imagery/Map/Road/Routes", 
			    URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
			
			System.out.println(httpget.getURI());
			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
			    InputStream instream = entity.getContent();
			    try {
			    	imBuff = ImageIO.read(instream);
			    	//File outputfile = new File(Path.getInstance().getScenarioPath() + "image.jpg");
			    	//ImageIO.write(imBuff, "jpg", outputfile);
			    } finally {
			        instream.close();
			    }
			}
		}  catch (IOException e) {
			logger.severe("Problema de encode na URL.");
			e.printStackTrace();
			throw e;
		} catch (URISyntaxException e) {
			logger.severe("Sintaxe incorreta da URL do serviço REST Bing.");
			throw e;
		}
	
		//String url = "http://dev.virtualearth.net/REST/v1/Imagery/Map/Road/Routes?wp.0=-25.4310207,-49.2496262&
		//wp.1=-25.4296455,-49.2503738&mapSize=800,600&key=AsL0kSymn5kuJ92vVKGguuvTY-THO-NqTzH88PES54V3H1Zs132JXL91wg17vj2E";
		return imBuff;
	}

	/**
	 * Get the travel distance and duration between two locations 
	 * using the Routes Bing Maps REST service.
	 * 
	 * @param a Location from
	 * @param b Location to
	 * @return the travel distance and duration from the Bing Maps Routes REST service request. [0] travel distance; [1] travel duration  
	 * @throws IOException 
	 * @throws JDOMException 
	 */	
	public static List<Object> getItinerary(Location a, Location b) throws IOException, JDOMException {		
		
		String parameterWayPoint1 = "wp.0=" + a.getLatitude() + "," + a.getLongitude();
		String parameterWayPoint2 = "wp.1=" + b.getLatitude() + "," + b.getLongitude();
		String parametersURL = parameterBingKey + 
				"&" + parameterOutputFormat + 
				"&" + parameterCulture +
				"&" + parameterWayPoint1 + 
				"&" + parameterWayPoint2;		
		
		String url = calculateRouteURL + parametersURL;				
		String xml = httpGetREST(url);
		
		System.out.println(url);
		/* Regular Expression that strips off any non-word characters in the prolog. 
		Byte order mark - BOM - from UTF-8 are 0xEF,0xBB,0xBF. */			 
		xml = xml.trim().replaceFirst("^([\\W]+)<","<");
		
		
		SAXBuilder builder = new SAXBuilder();	        
		Document document = builder.build(new StringReader(xml));
		
		Element rootNode = document.getRootElement();			
		Element eResourceSets = rootNode.getChild("ResourceSets", rootNode.getNamespace());;
		Element eResourceSet = eResourceSets.getChild("ResourceSet", eResourceSets.getNamespace());
		Element eEstimatedTotal = eResourceSet.getChild("EstimatedTotal", eResourceSet.getNamespace());
		Element eResource = eResourceSet.getChild("Resources", eResourceSet.getNamespace());
		Element eRoute = eResource.getChild("Route", eResource.getNamespace());
		Element eRouteLeg = eRoute.getChild("RouteLeg", eRoute.getNamespace());
		
		double travelDistance = Double.parseDouble(eRoute.getChildText("TravelDistance", eRoute.getNamespace()));
		double travelDuration = Double.parseDouble(eRoute.getChildText("TravelDuration", eRoute.getNamespace()));	
		travelDuration = Math.floor(travelDuration/60 * 100)/100; //sec to min with 3 decimal places.
		
		List<Element> eItineraryItem = eRouteLeg.getChildren("ItineraryItem", eRouteLeg.getNamespace());			
		List<String> instructions = new ArrayList<String>();
		List<String> type = new ArrayList<String>();

		Element eInstruction; 
		
		for(Element eItinerary : eItineraryItem) {
			eInstruction = eItinerary.getChild("Instruction", eItinerary.getNamespace());			
			byte ptext[] = eInstruction.getText().getBytes();
			String s = new String(ptext, "UTF-8");
			instructions.add(s);
			type.add(eInstruction.getAttributeValue("maneuverType"));
		}		

		System.out.println("Travel distance: " + travelDistance);
		System.out.println("Travel duration: " + travelDuration + "\n");
		
		List<Object> result = new ArrayList<Object>();
		result.add(travelDistance);
		result.add(travelDuration);
		result.add(instructions);
		result.add(type);
		
		return result;
	}
	
	/**
	 * Get the distance in km between two locations.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceLocations(Location a, Location b) {
		double dLatitude = Math.toRadians(b.getLatitude() - a.getLatitude());
		double dLongitude = Math.toRadians(b.getLongitude() - a.getLongitude());
		double radianLatitudeA = Math.toRadians(a.getLatitude());		
		double radianLatitudeB = Math.toRadians(b.getLatitude());
		
		double param = Math.sin(dLatitude/2) * Math.sin(dLatitude/2) + 
				Math.sin(dLongitude/2) * Math.sin(dLongitude/2) * 
				Math.cos(radianLatitudeA) * Math.cos(radianLatitudeB); 
		
		double distance = 2 * Math.atan2(Math.sqrt(param), Math.sqrt(1-param)) * EARTH_RADIUS;
		
		//In km.
		return distance;
	}	
	
	/**
	 * Call a URL Rest Service.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	private static String httpGetREST(String urlString) throws IOException {
	  	  
    	URL url = new URL(urlString);
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	
    	// If the response takes more than 200 ms return the IOException.
    	if (connection.getResponseCode() != 200) {
    		throw new IOException(connection.getResponseMessage());
    	}

    	// Buffer the result into a string
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    	StringBuilder stringBuilder = new StringBuilder();
    	
    	String line;
    	while ((line = bufferedReader.readLine()) != null) {
    		stringBuilder.append(line);
    	}
    	bufferedReader.close();

    	connection.disconnect();
    	return stringBuilder.toString();
	}
	
	
}
