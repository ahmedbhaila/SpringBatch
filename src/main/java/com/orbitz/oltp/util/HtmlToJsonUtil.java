package com.orbitz.oltp.util;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbitz.oltp.app.view.model.AirFare;
import com.orbitz.oltp.app.view.model.AirProductView;
import com.orbitz.oltp.app.view.model.BillingInfoView;
import com.orbitz.oltp.app.view.model.CarProductView;
import com.orbitz.oltp.app.view.model.ClassicTripSummaryView;
import com.orbitz.oltp.app.view.model.ProductItinerary;
import com.orbitz.oltp.app.view.model.ProductItinerarySortable;

@Component
public class HtmlToJsonUtil {
	
	protected Tidy tidy = new Tidy();
	
	@PostConstruct 
	protected void setupTidy(){
		tidy.setShowWarnings(false);
		tidy.setShowErrors(0);
		tidy.setQuiet(true);
		//tidy.setErrout(null);
		tidy.setXmlTags(false);
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.setXHTML(true);
		tidy.setMakeClean(true);
	}
	
	// XPath magic here
	// Trip Detail Title: div[@id="bodyContent"]/div[1]/h2
	// Trip Detail Info Text: div[@id="bodyContent"]/div[1]/p[1]
	// Trip Detail Cancel Text: div[@id="bodyContent"]/div[1]/p[2]/span
	
	// Package details summary 
		// title: div[@class='packageDetailsSummary']/h2
		// Record Locator: div[@class='packageDetailsSummary']/p[1]
		// Total Trip Cost: div[@class='packageDetailsSummary']/p[2]
	
	// Flight Reservation details
		// Orbitz Record Locator: div[@class='reservationInformation']/table[1]/tr[1]/td[2]
		// Airline Record Lcoator: div[@class='reservationInformation]/table[1]/tr[2]/td[2]
		// Airline Ticket Numbers: div[@class='reservationInformation]/table[1]/tr[3]/td[2]
		// Traveler Info: div[@class='reservationInformation]/table[2]/tr[1]/td[1]

	
	public String convertToJSON(String html){
	    //   HTML -> XHTML
        Document document = tidy.parseDOM(new ByteArrayInputStream(html.getBytes()), null);
        String xhtmlContent = getStringXHTML(document);
        JSONObject xmlJSONObj = XML.toJSONObject(xhtmlContent);
        return xmlJSONObj.toString();
        
	}
	public String convert(String html){
		String jsonString = "";
		ClassicTripSummaryView ctsv = new ClassicTripSummaryView();
		List<ProductItinerary<ProductItinerarySortable>> products = new LinkedList<ProductItinerary<ProductItinerarySortable>>();
		//System.out.println(html);
		//setupTidy();
		
		// HTML -> XHTML
		Document document = tidy.parseDOM(new ByteArrayInputStream(html.getBytes()), null);
		String xhtmlContent = getStringXHTML(document);
		
		String content = "";
		content += getXPathValue(xhtmlContent, "//div[@id='bodyContent']/div[1]/h2") + ", ";
		content += getXPathValue(xhtmlContent, "//div[@id='bodyContent']/div[1]/p[1]") + ", ";
		content += getXPathValue(xhtmlContent, "//div[@id='bodyContent']/div[1]/p[2]/span") + ", ";
		//content += getXPathValue(xhtmlContent, ")
		
		if(!(xhtmlContent.contains("MasterServlet Uncaught Exception"))){
			// generate trip info
			NodeList nodes = evaluateXPath(xhtmlContent,
					"//div[@id='bodyContent']/div[1]");
			String tripTitle = nodes.item(0).getChildNodes().item(3)
					.getTextContent();
			String tripInfo = nodes.item(0).getChildNodes().item(5)
					.getTextContent();
			if(nodes.item(0).getChildNodes().item(9) != null){
				String tripMessage = nodes.item(0).getChildNodes().item(9)
						.getTextContent();
				if(tripMessage.contains("canceled")){
				    ctsv.setTripCancelled(true);
				}
				ctsv.setTripMessage(tripMessage);
			}

			ctsv.setTripTitle(tripTitle);
			ctsv.setTripInfo(tripInfo);
			

			// generate package details summary if available
			nodes = evaluateXPath(xhtmlContent,
					"//div[@class='packageDetailsSummary']");
			if (nodes.item(0) != null) {
				String packageInfo = nodes.item(0).getChildNodes().item(1)
						.getTextContent();
				String orbitzPackageRecordLocator = nodes.item(0)
						.getChildNodes().item(3).getTextContent();
				String totalTripCost = nodes.item(0).getChildNodes().item(5)
						.getTextContent();

				ctsv.setPackageInfo(packageInfo);
				ctsv.setOrbitzPackageRecordLocator(orbitzPackageRecordLocator);
				ctsv.setTotalTripCost(totalTripCost);
			}
			//System.out.println(xhtmlContent);

			// air details

			String airDetailsXPath = "//div[@id='airDetails']/div[@class='reservationInformation']/table[@class='flightResInfo']";
			nodes = evaluateXPath(xhtmlContent, airDetailsXPath);
			String ticketNumbers = "";  
			if (nodes.item(0) != null) {
				String orbitzRecordLocator = nodes.item(0).getChildNodes()
						.item(1).getChildNodes().item(3).getTextContent();
				String airLineRecordLocator = nodes.item(0).getChildNodes()
						.item(3).getChildNodes().item(3).getTextContent();
				ticketNumbers = nodes.item(0).getChildNodes().item(5)
						.getChildNodes().item(3).getTextContent();
				
				ctsv.setOrbitzRecordLocator(orbitzRecordLocator);

				// traveler information (fix this)
				//String travelerInfoXPath = "//table[@class='travelerInformation']/tr[1]/td[1]";
				String travelerInfoXPath = "//table[@class='travelerInformation']/tr";
				NodeList travelerNodeList = evaluateXPath(xhtmlContent, travelerInfoXPath);
				List<String> travelers = new ArrayList<String>();
				for(int i = 0; i < travelerNodeList.getLength(); i ++){
				    travelers.add(travelerNodeList.item(i).getChildNodes().item(1).getTextContent());
				}
				String travelerName = getXPathValue(xhtmlContent,
						travelerInfoXPath);
				
				// get air slice and legs for each slice
				//String airSliceXPath = "//div[@id='airDetails']/div[@class='slice']";
				String airSliceXPath = "//div[@class='slice']";
				nodes = evaluateXPath(xhtmlContent, airSliceXPath);

				// iterate thru all slices
				for (int i = 0; i < nodes.getLength(); i++) {
					String departureDate = nodes.item(i).getChildNodes()
							.item(1).getTextContent();
					System.out.println("date is " + departureDate);

					// sliceContent -> segmentSection
					NodeList sliceContentList = nodes.item(i).getChildNodes()
							.item(3).getChildNodes();

					String carrier = sliceContentList.item(5).getChildNodes()
							.item(1).getTextContent();
					System.out.println("Carrier is " + carrier);
					String flightInfo = sliceContentList.item(5)
							.getChildNodes().item(3).getTextContent();
					System.out.println("Flight Info is " + flightInfo);

					// look for any notes attached to this slice
					int jIndexStart = 7;
					int jIndexInc = 6;
					int jIndex = 7;
					if (sliceContentList.item(jIndexStart).getChildNodes()
							.item(1).getNodeName().equals("p")) {
						String sliceNote = sliceContentList.item(jIndexStart)
								.getChildNodes().item(1).getTextContent();
						jIndexStart = 9;
						jIndexInc = 9;
						jIndex = 9;
					}

					for (int j = jIndexStart; j < sliceContentList.getLength(); j += jIndexInc) {
						// table
						NodeList flightTableList = sliceContentList
								.item(jIndex).getChildNodes();
						String eventName = flightTableList.item(1)
								.getChildNodes().item(1).getTextContent();
						String eventTime = departureDate
								+ " "
								+ flightTableList.item(1).getChildNodes()
										.item(3).getTextContent();
						
						String eventLocation = flightTableList.item(1)
								.getChildNodes().item(5).getTextContent();
						
						
						AirProductView apv = new AirProductView();
						apv.setAirLine(carrier);
						apv.setDepartureAirport(eventLocation);
						apv.setDepartureTime(eventTime);
						apv.setEventDate(eventTime);
						apv.setDepartTimeExtract(eventTime.split("\n")[1]);
						apv.getTravelers().addAll(travelers);
						
						Pattern pattern = Pattern.compile("\\((.*)\\)");
                        Matcher matcher = pattern.matcher(eventLocation);
                        if(matcher.find()){
                            apv.setDepartureCityCode(matcher.group(1));
                        }
						
						String[] flightInfoVal = flightInfo.split("\\|");
						apv.setCabinType(flightInfoVal[0].replaceAll("\u00A0", "").trim());
						apv.setAircraftType(flightInfoVal[1].replaceAll("\u00A0", "").trim());
						if(flightInfo.length() > 5){
						    apv.setMealPrefs(flightInfoVal[2].replaceAll("\u00A0", "").trim());
						    apv.setDuration(flightInfoVal[3].replaceAll("\u00A0", "").trim());
						    apv.setDistance(flightInfoVal[4].replaceAll("\u00A0", "").trim());
						}
						else{
						    apv.setDuration(flightInfoVal[2].replaceAll("\u00A0", "").trim());
                            apv.setDistance(flightInfoVal[3].replaceAll("\u00A0", "").trim());
						}
						    
						    
						

						// some cleanup
						eventTime = eventTime.replaceAll("Return ", "");
						eventTime = eventTime.replaceAll("Leave ", "");
						eventTime = eventTime.replaceAll(" : ", "");
						eventTime = eventTime.replaceAll("\n", "");
						
						if(eventTime.startsWith("Flight")){
							eventTime = eventTime.substring(eventTime.indexOf(":") + 2, eventTime.length());
						}
						
						"1234 ahm".replaceAll("\\d+", "");
						DateTimeFormatter formatter = DateTimeFormat
								.forPattern("EEEE, MMMM d, Y hh:mma");
						DateTime dateTime = formatter.parseDateTime(eventTime);
						apv.setEventEpoch(dateTime.getMillis());
						apv.setEventDate(DateTimeFormat.forPattern(
								"E, MMMM d, x").print(dateTime));

						eventName = flightTableList.item(3).getChildNodes()
								.item(1).getTextContent();
						eventTime = departureDate
								+ " "
								+ flightTableList.item(3).getChildNodes()
										.item(3).getTextContent();
						apv.setArrivalTimeExtract(eventTime.split("\n")[1]);
						eventLocation = flightTableList.item(3).getChildNodes()
								.item(5).getTextContent();
						
						matcher = pattern.matcher(eventLocation);
                        if(matcher.find()){
                            apv.setArrivalCityCode(matcher.group(1));
                        }

						apv.setArrivalAirport(eventLocation);
						apv.setArrivalTime(eventTime);
						
						
						
						

						ProductItinerary<ProductItinerarySortable> productIt = new ProductItinerary<ProductItinerarySortable>(
								apv.getEventEpoch(), apv.getEventDate(), apv);
						productIt.setAir(true);
						products.add(productIt);

						// for(int k = 1; k < flightTableList.getLength(); k +=2
						// ){
						//
						//
						// String eventName =
						// flightTableList.item(k).getChildNodes().item(1).getTextContent();
						//
						// System.out.println("Event name is " + eventName);
						// String eventTime = departureDate + " " +
						// flightTableList.item(k).getChildNodes().item(3).getTextContent();
						//
						// System.out.println("Event Time is " + eventTime);
						// String eventLocation =
						// flightTableList.item(k).getChildNodes().item(5).getTextContent();
						// System.out.println("Event Location is " +
						// eventLocation);
						//
						//
						//
						//
						//
						// AirProductSlice aps = new AirProductSlice();
						// aps.setCarrier(carrier);
						// aps.setFlightInfo(flightInfo);
						// aps.setLocation(eventLocation);
						// aps.setLocationName(eventName);
						// aps.setLocationTime(eventTime);
						//
						// // some cleanup
						// eventTime = eventTime.replaceAll("Return ", "");
						// eventTime = eventTime.replaceAll("Leave ", "");
						// eventTime = eventTime.replaceAll(" : ", "");
						// eventTime = eventTime.replaceAll("\n", "");
						//
						//
						//
						// DateTimeFormatter formatter =
						// DateTimeFormat.forPattern("EEEE, MMMM d, Y hh:mma");
						// DateTime dateTime =
						// formatter.parseDateTime(eventTime);
						// aps.setEventEpoch(dateTime.getMillis());
						// aps.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x").print(dateTime));
						//
						//
						// ProductItinerary<ProductItinerarySortable> productIt
						// =
						// new ProductItinerary<ProductItinerarySortable>(
						// aps.getEventEpoch(),
						// aps.getLocationTime(), aps);
						// productIt.setAir(true);
						// products.add(productIt);
						//
						//
						// }
					}

				}
			}

			// Car Rental Reservation
			String carDetailsXPath = "//div[@id='carDetails']";
			NodeList carReservationNodes = evaluateXPath(xhtmlContent,
					carDetailsXPath);

			// reservation info
			if (carReservationNodes.item(0) != null) {
				NodeList carReservationInfoNodes = carReservationNodes.item(0)
						.getChildNodes().item(1).getChildNodes().item(1)
						.getChildNodes();
				String confirmationNumber = carReservationInfoNodes.item(3)
						.getTextContent();
				String primaryDriver = carReservationInfoNodes.item(7)
						.getTextContent();

				// vendor info
				String vendorInfo = carReservationNodes.item(0).getChildNodes()
						.item(3).getChildNodes().item(3).getTextContent();

				List<CarProductView> carsProducts = new ArrayList<CarProductView>();

				// car itinerary details - table
				NodeList itinDetails = (NodeList) carReservationNodes.item(0)
						.getChildNodes().item(3).getChildNodes().item(5)
						.getChildNodes().item(1).getChildNodes();
				for (int i = 1; i < itinDetails.getLength(); i += 2) {

					CarProductView cpv = new CarProductView();
					cpv.setDrivers(Arrays
							.asList(new String[] { primaryDriver }));
					cpv.setRentalCompany(vendorInfo);
					cpv.setRentalLocation(vendorInfo);

					String eventName = itinDetails.item(i).getChildNodes()
							.item(1).getTextContent();
					String eventTime = itinDetails.item(i).getChildNodes()
							.item(3).getTextContent();
					String eventLocation = itinDetails.item(i).getChildNodes()
							.item(5).getTextContent();

					// convert event time

					// some cleanup
					// Tue, Nov 18, 2008 9:00am
					eventTime = eventTime.replaceAll("Return ", "");
					eventTime = eventTime.replaceAll("Leave ", "");
					eventTime = eventTime.replaceAll(" : ", "");

					DateTimeFormatter formatter = DateTimeFormat
							.forPattern("E, MMMM d, Y hh:mma");
					DateTime dateTime = formatter.parseDateTime(eventTime);
					cpv.setEventEpoch(dateTime.getMillis());

					cpv.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
							.print(dateTime));
					cpv.setEventType(eventName);

					ProductItinerary<ProductItinerarySortable> productIt = new ProductItinerary<ProductItinerarySortable>(
							cpv.getEventEpoch(), cpv.getEventDate(), cpv);
					productIt.setCar(true);

					products.add(productIt);
				}

				// additional car details
//				String carDetails = carReservationNodes.item(0).getChildNodes()
//						.item(5).getChildNodes().item(3).getChildNodes()
//						.item(1).getChildNodes().item(3).getTextContent();
				
				String carDetails = carReservationNodes.item(0).getChildNodes().item(5).getTextContent();
			}
			
			// air fare details
			String airFareXPath = "//div[@id='airCostSummary']/table[@class='cost']";
			nodes = evaluateXPath(xhtmlContent, airFareXPath);
			for(int i = 3; i < nodes.item(0).getChildNodes().getLength(); i += 4){
			    AirFare airFare = new AirFare();
	            airFare.setCost(nodes.item(0).getChildNodes().item(i).getChildNodes().item(1).getTextContent());
	            airFare.setTravelerName(nodes.item(0).getChildNodes().item(i).getChildNodes().item(3).getTextContent());
	            airFare.setTicketNumber(ticketNumbers);
	            ctsv.getAirFares().add(airFare);
			}
			
			// total air fare cost
			String totalAirCost = nodes.item(0).getChildNodes().item(nodes.item(0).getChildNodes().getLength() - 2).getChildNodes().item(3).getTextContent();
			ctsv.setTotalAirFareCost(totalAirCost);
			
			
			
			// cost summary
			String subTotal = getXPathValue(xhtmlContent,
					"//td[@class='subtotal'][1]");
			
			// billing info
			String billingInfoXPath = "//div[@class='myStuffPanel billingInfoPanel']";
			String[] billingInfo = getXPathValue(xhtmlContent, billingInfoXPath)
					.split("\n");
			if(billingInfo.length > 9){
				String cardHoldersName = billingInfo[5];
				String cardType = billingInfo[7];
				String cardNumber = billingInfo[9];

				BillingInfoView billingInfoView = new BillingInfoView();
				billingInfoView.setCardHoldersName(cardHoldersName);
				billingInfoView.setCardNumber(cardNumber);
				billingInfoView.setCardType(cardType);

				ctsv.setBillingInfoView(billingInfoView);
			}

			Map<String, List<ProductItinerary<?>>> productMap = new LinkedHashMap<String, List<ProductItinerary<?>>>();

			// sort the products
			Collections.sort(products,
					new ProductItinerarySorter<ProductItinerary<?>>());

			// create a map with key = "Event Date"
			for (ProductItinerary<ProductItinerarySortable> productItinerary : products) {
				String eventDateKey = productItinerary.getProduct()
						.getEventDate();
				if (productMap.get(eventDateKey) != null) {
					productMap.get(eventDateKey).add(productItinerary);
				} else {
					List<ProductItinerary<?>> productItins = new LinkedList<ProductItinerary<?>>();
					productItins.add(productItinerary);
					productMap.put(eventDateKey, productItins);
				}
			}
			ctsv.setProductMap(productMap);

			// convert into JSON string
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonString = mapper.writeValueAsString(ctsv);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
		
		return jsonString;
		
		
		
		
		
		
	}
	protected String getStringXHTML(Document document){
		StringWriter sw = new StringWriter();
		try {
	        
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(document), new StreamResult(sw));
	        
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
		return sw.toString();
	}
	
	
	
	
	
	protected String getXPathValue(String xml, String xPathExpression){
		String value = "";
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile(xPathExpression);
			
			value = expr.evaluate(document);
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return value;
	}
	protected NodeList evaluateXPath(String xml, String xPathExpression){
		NodeList nodeList = null;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			//XPathExpression expr = xpath.compile(xPathExpression);
			
			nodeList = (NodeList)xpath.evaluate(xPathExpression, document, XPathConstants.NODESET);
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return nodeList;
	}
	
}
