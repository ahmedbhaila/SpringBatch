package com.orbitz.oltp.processor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.orbitz.oltp.app.config.OLTPBatchJobConfig;
import com.orbitz.oltp.app.view.model.AirFare;
import com.orbitz.oltp.app.view.model.AirProductView;
import com.orbitz.oltp.app.view.model.BillingInfoView;
import com.orbitz.oltp.app.view.model.CarProductView;
import com.orbitz.oltp.app.view.model.ClassicTripSummaryView;
import com.orbitz.oltp.app.view.model.HotelProductView;
import com.orbitz.oltp.app.view.model.ProductItinerary;
import com.orbitz.oltp.app.view.model.ProductItinerarySortable;
import com.orbitz.oltp.db.model.MemberTrip;
import com.orbitz.oltp.util.HtmlToJsonUtil;
import com.orbitz.oltp.util.NovoHTMLGrabber;
import com.orbitz.oltp.util.ProductItinerarySorter;
import com.orbitz.oltp.view.model.ASTripEventView;
import com.orbitz.oltp.view.model.CarCostSummary;
import com.orbitz.oltp.view.model.HotelCostSummary;

@Configuration
public class MemberTripProcessor implements
        ItemProcessor<MemberTrip, MemberTrip> {

    @Autowired
    NovoHTMLGrabber htmlGrabber;

    @Autowired
    HtmlToJsonUtil htmlToJSON;
    
    protected int segmentCounter = 0;
    
    protected List<ProductItinerary<ProductItinerarySortable>> products 
                        = new LinkedList<ProductItinerary<ProductItinerarySortable>>();
    
    protected Logger log = Logger.getLogger(MemberTripProcessor.class);
    
    

    public MemberTrip process(MemberTrip item) throws Exception {
         String html = htmlGrabber.getHTML(item.getMemberId(),
         item.getTripLocator());
        // String html = htmlGrabber.getHTML("ddtestpambu@yahoo.com",
        // "FEBFFUHINJR");
//        String html = htmlGrabber.getHTML("ddtestpambu@yahoo.com",
//                "JANEBZUAOBQ");
        // NOVE5QWINJT
        // JANEBZUAOBQ -> Air Three travelers
        // MAYONXWCMBQ -> Hotel booking, fails!
        // AUGW5AHGMRZ -> Air with no tickets
        // DEC6VWWCNJR
        // MAY6B2G2OBY -> multi leg
        // MARJBUGIOBZ -> one way air
        // MAY652W6OJY -> return trip
        // MAR25RG6MJQ -> return trip with one stopver
        // APRYNRWIMRT -> Air + Car
        // AUGSBTWGNRX -> OAS + Car for dhskfhkas@yahoo.com 
        // JAN12399684 -> PKG for apurohit@orbitz.com
        // AUGWFQW6MRS -> Air + Car + OAS for addsafd@adfads.com
        // AUGYFSGCOBZ -> Air + Car + OAS for addsafd@adfads.com
        // NOVE5QWINJT -> OAS(Multiple) for assaf.adato@orbitz.com
        // FEBO5SXSNZU -> OAS(Single) for benhuynh1@sbcglobal.net
        // AUGSBTWGNRX -> Car + OAS dhskfhkas@yahoo.com
        // MAR13135802 -> Air booking for 17martest@orbitz.com
        
        
        // Trip Locators from PROD - for hotel testing
        // JANTF4WEMRT for ddtestpambu@yahoo.com
        // AUG2VSHUMRW for ddtestpambu@yahoo.com
        // OCTV5SEAMZV for ddtestpambu@yahoo.com
        // PBORB1315386504 
        // SEP3ZWWCMJQ
        // AUGHV2W6MJV
        // AUGCF4W2MJT
        // JUNGBRHKNZU car + hotel\
        // JUNW5RXIMRV
       
//        String html = htmlGrabber.getHTML("cognizant@cognizant.com",
//                "MAY16202816");
//        String html = htmlGrabber.getHTML("dharani.orbitztest@orbitz.com",
//                "AUG2VSHUMRW");
        
//        String html = htmlGrabber.getHTML("addsafd@adfads.com",
//                "AUGWFQW6MRS");


        
//        // String jsonString = htmlToJSON.convert(html);
        if(html != null){
            String jsonString = htmlToJSON.convertToJSON(html);
            log.info(jsonString);
            //processJSON(jsonString);
            //processCarJSON(jsonString);
            item.setJsonString(jsonString);
            //item.setProcessed(1);
        }
        return item;
    }

    private List<AirProductView> processAirDeparture(String json, String jsonPath, ClassicTripSummaryView cstv){
        //JSONArray slices = JsonPath.read(json, "$..div[?(@.class == 'sliceContent')].table");
        JSONArray slices = JsonPath.read(json, "$..div[?(@.class == 'sliceContent')]");
        List<AirProductView> airProducts = new ArrayList<AirProductView>();
        for(int i = 0; i < slices.size(); i ++){
            if(slices.get(i) instanceof JSONArray){
                JSONArray multiFlights = (JSONArray)slices.get(i);
                for (int j = 0; j < multiFlights.size(); j++) {
                    //segmentCounter = j;
                    segmentCounter = j;
                    airProducts.addAll(processFlight((JSONObject)multiFlights.get(j), json, i, cstv)); 
                    
                }
            }
            else{
                segmentCounter += i;
                airProducts.addAll(processFlight((JSONObject)slices.get(i), json, i, cstv));
            }
            
        }
        return airProducts;
    }
    private List<AirProductView> processFlight(JSONObject flightObject, String json, int legIndex, ClassicTripSummaryView cstv){
//        JSONArray overnightFlightInfo  = ((JSONArray)flightObject.get("p"));
//        String overnightMessage = null;
//        String duration = null;
//        try{
//            overnightMessage = ((JSONObject)((JSONObject)overnightFlightInfo.get(0)).get("span")).get("strong").toString();
//            duration = overnightFlightInfo.get(1).toString();
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
        
        List<AirProductView> airProducts = new ArrayList<AirProductView>();
        JSONArray flightInfo = null;
        if(!(flightObject.get("table") instanceof JSONArray)){
            flightInfo = new JSONArray();
            flightInfo.add(flightObject.get("table"));
        }
        else{
            flightInfo  = ((JSONArray)flightObject.get("table"));
        }
        for (int k = 0; k < flightInfo.size(); k++) {
            segmentCounter = k;
            JSONArray multiFlights = (JSONArray)((JSONObject)flightInfo.get(k)).get("tr");
            for (int i = 0; i < multiFlights.size(); i += 2) {

                String departEvent = ((JSONArray) ((JSONObject) multiFlights
                        .get(i)).get("td")).get(0).toString();
                String departEventTime = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i)).get("td")).get(1)).get("strong").toString();
                String departAirportInfo = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i)).get("td")).get(2)).get("content").toString();
                String departCity = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i)).get("td")).get(2)).get("strong").toString();

                String arriveEvent = ((JSONArray) ((JSONObject) multiFlights
                        .get(i + 1)).get("td")).get(0).toString();
                String arriveEventTime = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i + 1)).get("td")).get(1)).get("strong")
                        .toString();
                String arriveAirportInfo = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i + 1)).get("td")).get(2)).get("content")
                        .toString();
                String arriveCity = ((JSONObject) ((JSONArray) ((JSONObject) multiFlights
                        .get(i + 1)).get("td")).get(2)).get("strong")
                        .toString();

                // start of trip slice
                String tripStart = JsonPath.read(json,
                        "$..[?(@.class == 'slice')].h3[" + legIndex + "]");
                if (tripStart.startsWith("Leave")) {
                    tripStart = tripStart.substring(tripStart.indexOf(" ") + 1,
                            tripStart.length());
                } else if (tripStart.startsWith("Return")) {
                    tripStart = tripStart.substring(tripStart.indexOf(" ") + 1,
                            tripStart.length());
                }

                int extractLocation = departAirportInfo.indexOf("(");
                String departAirport = departAirportInfo.substring(0,
                        extractLocation - 1);
                String departAirportCode = departAirportInfo.substring(
                        extractLocation + 1, departAirportInfo.indexOf(")"));

                extractLocation = arriveAirportInfo.indexOf("(");
                String arriveAirport = arriveAirportInfo.substring(0,
                        extractLocation - 1);
                String arriveAirportCode = arriveAirportInfo.substring(
                        extractLocation + 1, arriveAirportInfo.indexOf(")"));

                // Air Segment information
                String airLine = JsonPath.read(json,
                        "$..[?(@.class == 'segmentSection')][" + segmentCounter
                                + "].span[0].content");
                String airCraftInfo = JsonPath.read(json,
                        "$..[?(@.class == 'segmentSection')][" + segmentCounter
                                + "].span[1]");

                JsonPath.read(json, "$..[?(@.class == 'segmentSection')].div");

                // TODO : to handle single tickets
                List<String> ticketNumbers = new ArrayList<String>();
                try {
                    ticketNumbers = JsonPath
                            .read(json,
                                    "$..[?(@.class == 'reservationInformation')].table.tr.td[5].content");
                    if(ticketNumbers.get(0).startsWith("$")){
                        // this is a hack, sometimes the ticketing cost appears instead of ticket numbers
                        ticketNumbers = JsonPath
                                .read(json,
                                        "$..[?(@.class == 'reservationInformation')].table.tr.td[3].content");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                if (ticketNumbers.size() == 0) {
                    // this is a single ticket
                    ticketNumbers
                            .add(JsonPath
                                    .read(json,
                                            "$..[?(@.class == 'reservationInformation')].table.tr.td[5].content")
                                    .toString());
                    if(ticketNumbers.get(0).startsWith("$")){
                        // this is a hack, sometimes the ticketing cost appears instead of ticket numbers
                        ticketNumbers.clear();
                        ticketNumbers.add(JsonPath
                                .read(json,
                                        "$..[?(@.class == 'reservationInformation')].table.tr.td[3].content").toString());
                    }
                }
                
                

                List<String> travelers = JsonPath
                        .read(json,
                                "$..[?(@.class == 'productContent airProduct')].table.tr[*].td[*]");
                travelers.removeAll(Collections.singleton(""));

                if (travelers.size() == 0) {
                    // didnt find any travelers with the expression above
                    travelers = JsonPath
                            .read(json,
                                    "$..[?(@.class == 'travelerInformation')].tr[*].td[*]");
                    travelers.removeAll(Collections.singleton(""));
                }

                AirProductView apv = new AirProductView();
                apv.setArrivalCity(arriveCity);
                apv.setDepartureCity(departCity);
                apv.setTravelers(travelers);
                apv.setTickets(ticketNumbers);
                
                DateTimeFormatter formatter = DateTimeFormat
                        .forPattern("EEEE, MMMM d, Y hh:mma");
                DateTime departDateTime = formatter.parseDateTime(tripStart
                        + " " + departEventTime);
                DateTime arriveDateTime = formatter.parseDateTime(tripStart
                        + " " + arriveEventTime);
                apv.setEventEpoch(departDateTime.getMillis());
                apv.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
                        .print(departDateTime));
                
                if( (departEventTime.endsWith("am") && arriveEventTime.endsWith("pm")) || 
                        arriveEventTime.endsWith("am") && departEventTime.endsWith("pm")){
                    apv.setOvernightFlight(true);
                }
                
                
                
                String[] flightInfoVal = airCraftInfo.split("\\|");
                apv.setCabinType(flightInfoVal[0].replaceAll("\u00A0", "")
                        .trim());
                apv.setAircraftType(flightInfoVal[1].replaceAll("\u00A0", "")
                        .trim());
                if (flightInfoVal.length > 4) {
                    apv.setMealPrefs(flightInfoVal[2].replaceAll("\u00A0", "")
                            .trim());
                    apv.setDuration(flightInfoVal[3].replaceAll("\u00A0", "")
                            .trim());
                    apv.setDistance(flightInfoVal[4].replaceAll("\u00A0", "")
                            .trim());
                } else {
                    apv.setDuration(flightInfoVal[2].replaceAll("\u00A0", "")
                            .trim());
                    apv.setDistance(flightInfoVal[3].replaceAll("\u00A0", "")
                            .trim());
                }

                apv.setEventType("Depart");
                apv.setDepartureAirport(departAirport);
                apv.setDepartureCityCode(departAirportCode);
                apv.setDepartureTime(departEventTime);
                apv.setAirLine(airLine);

                apv.setEventType("Arrive");
                apv.setEventType("Depart");
                apv.setArrivalAirport(arriveAirport);
                apv.setArrivalCityCode(arriveAirportCode);
                apv.setArrivalTime(arriveEventTime);

                airProducts.add(apv);
            }
        }
        
        return airProducts;
        
        
    }
    private void processJSON(String json) {
        
        // Reservation related information
        String orbitzRecordLocator = "";
        String tripStatus = "";
        String tripTitle = "";
        String airLineRecordLocator = "";
        boolean tripCancelled = false;
        try{
            
            // trip title
            tripTitle = ((JSONArray)JsonPath.read(json, "$..[?(@.id == 'otpDetailsSummary')].h2")).get(0).toString();
        }
        catch(Exception e){
            // couldnt find Trip title, try another expression
            tripTitle = JsonPath.read(json, "$..[?(@.id == 'otpDetailsSummary')].form.div[0].h2").toString();
        }
        
        try{
            
            orbitzRecordLocator = JsonPath
                .read(json,
                        "$..[?(@.class == 'reservationInformation')].table.tr.td[1].content");
            
            // Not needed for now, since view zero doesnt seem to be displaying it
//            airLineRecordLocator = JsonPath
//                    .read(json,
//                            "$..[?(@.class == 'reservationInformation')].table.tr.td[2].content");
        }
        catch(Exception e){
            log.error(e.getMessage());
        }
        try{
            // status of trip
            tripStatus = JsonPath.read(json, "$..[?(@.id == 'otpDetailsSummary')].p[1].span.content");
            
            if(tripStatus.contains("canceled")){
                // trip was cancelled 
                tripCancelled = true;
            }
            
            
           
        }
        catch(Exception e){
            // orbitzRecord locator not found
            e.getMessage();
        }
        
        
        // package related information
        String orbitzPackageRecordLocator = null;
        try{
            orbitzPackageRecordLocator = JsonPath.read(json, "$..[?(@.class == 'packageDetailsSummary')].p[0]");
            orbitzPackageRecordLocator = orbitzPackageRecordLocator.substring(orbitzPackageRecordLocator.indexOf(":") + 1, orbitzPackageRecordLocator.length());
        }
        catch(Exception e){
            log.error(e.getMessage());
        }
        
        
        ClassicTripSummaryView ctsv = new ClassicTripSummaryView();
        ctsv.setOrbitzRecordLocator(orbitzRecordLocator);
        ctsv.setOrbitzPackageRecordLocator(orbitzPackageRecordLocator);
        ctsv.setTripMessage(tripStatus);
        ctsv.setTripCancelled(tripCancelled);
        ctsv.setTripTitle(tripTitle);
        
        
        

        String jsonString = "";
        // general info here
        
        
        
        
        // start of trip
        //String tripStart = ((JSONArray)JsonPath.read(json, "$..[?(@.class == 'slice')].h3")).get(0).toString();
        
        
        
        
        // is there a return trip involved
        
        List<AirProductView> airProducts = processAirDeparture(json,
                "$..[?(@.class == 'slice')][0]", ctsv);
        for (AirProductView apv : airProducts) {
            ProductItinerary<ProductItinerarySortable> productIt = new ProductItinerary<ProductItinerarySortable>(
                    apv.getEventEpoch(), apv.getEventDate(), apv);
            productIt.setAir(true);
            products.add(productIt);
        }

        // Air Cost Summary
        String totalTripCost = "";
        Map<String, String> travelersAirFare = new HashMap<String, String>();
        String baseAirCostSummaryPath = "$..[?(@.class == 'costSummary')].table.tr[";
        
        if(airProducts.size() != 0){
            for (int i = 0; i < airProducts.get(0).getTravelers().size(); i++) {

                String airFarePath = baseAirCostSummaryPath + i
                        + "].td[0].content";
                String airFarePassenger = baseAirCostSummaryPath + i + "].th";
                try {
                    travelersAirFare.put(JsonPath.read(json, airFarePassenger)
                            .toString(), JsonPath.read(json, airFarePath)
                            .toString());
                    totalTripCost = JsonPath.read(json, airFarePath)
                            .toString();
                    
                    String serviceFee = JsonPath.read(json, baseAirCostSummaryPath
                            + airProducts.get(0).getTravelers().size()
                            + "].td[0].content");
                    
                    AirFare airFare = new AirFare();
                    airFare.setCost(totalTripCost);
                    airFare.setTravelerName(airProducts.get(0).getTravelers()
                            .get(i));
                    airFare.setServiceFee(serviceFee);
                    ctsv.getAirFares().add(airFare);
                } catch (Exception e) {
                    log.error(e.getMessage());

                    // look for another node for this info
                    baseAirCostSummaryPath = "$..[?(@.class == 'panelContent costSummary')].table.tr[0].td.content";
                    
                    try{
                        totalTripCost = JsonPath.read(json, baseAirCostSummaryPath);
                        AirFare airFare = new AirFare();
                        airFare.setCost(totalTripCost);
                        airFare.setTravelerName(airProducts.get(0).getTravelers()
                                .get(i));
                        //  TODO: some weird issue w
                        //  airFare.setTicketNumber(airProducts.get(0).getTickets()
                        // .get(i));
                        ctsv.getAirFares().add(airFare);
                    }
                    catch(Exception ex){
                        totalTripCost = JsonPath.read(json, "$..[?(@.class == 'panelContent costSummary')].table.tr[0].td");
                        AirFare airFare = new AirFare();
                        airFare.setCost(totalTripCost);
                        airFare.setTravelerName(airProducts.get(0).getTravelers()
                                .get(i));
                        ctsv.getAirFares().add(airFare);
                        log.error(e.getMessage());
                    }
                    

                }
            }
            // total trip cost
            if (totalTripCost.equals("")) {
                totalTripCost = JsonPath.read(json, baseAirCostSummaryPath
                        + airProducts.get(0).getTravelers().size()
                        + "].td[0].content");
                AirFare airFare = new AirFare();
                airFare.setCost(totalTripCost);
                airFare.setTravelerName(airProducts.get(0).getTravelers()
                        .get(0));
                airFare.setTicketNumber(airProducts.get(0).getTickets()
                        .get(0));
                ctsv.getAirFares().add(airFare);
            }
        }

        BillingInfoView billingInfo = null;
        try {
            // Billing Information here
            String baseBillingInfoPath = "$..[?(@.class == 'myStuffPanel billingInfoPanel')].div.dl.dd[";
            String cardHolderName = JsonPath.read(json, baseBillingInfoPath
                    + "0]");
            String cardType = JsonPath.read(json, baseBillingInfoPath + "1]");
            String cardNumber = JsonPath.read(json, baseBillingInfoPath + "2]");

            billingInfo = new BillingInfoView();
            billingInfo.setCardHoldersName(cardHolderName);
            billingInfo.setCardNumber(cardNumber);
            billingInfo.setCardType(cardType);
            billingInfo.setSubTotal(totalTripCost);
        } catch (Exception e) {
            e.getMessage();
        }

        ctsv.setBillingInfoView(billingInfo);
        
        
        processCarJSON(json, ctsv);
        processHotelJSON(json, ctsv);
        ctsv.setServices(processAttractionJSON(json));
        


        Map<String, List<ProductItinerary<?>>> productMap = new LinkedHashMap<String, List<ProductItinerary<?>>>();

        // sort the products
        Collections.sort(products,
                new ProductItinerarySorter<ProductItinerary<?>>());

        // create a map with key = "Event Date"
        for (ProductItinerary<ProductItinerarySortable> productItinerary : products) {
            String eventDateKey = productItinerary.getProduct().getEventDate();
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
            //System.out.println("JSON String is " + jsonString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        
//        // is there a change of planes
//        String changePlanesText = JsonPath.read(json, "$..[?(@.class == 'padAlert')].strong[0]");
//        if(changePlanesText != null){
//            changeOfPlanes = true;
//            String timeBetweenFlights = JsonPath.read(json, "$..[?(@.class == 'padAlert')].strong[1]");
//        }
//        
//       
//        
//        
//        
//        String airLineRecordLocator = JsonPath
//                .read(json,
//                        "$..[?(@.class == 'reservationInformation')].table.tr.td[3].content");
//        
//        // TODO : to handle single tickets
//        List<String> ticketNumbers = new ArrayList<String>();
//        try{
//            ticketNumbers = JsonPath
//                .read(json,
//                        "$..[?(@.class == 'reservationInformation')].table.tr.td[5].content");
//        }
//        catch(Exception e){
//            System.err.println(e.getMessage());
//        }
//        if(ticketNumbers.size() == 0){
//            // this is a single ticket
//            ticketNumbers.add(JsonPath
//                .read(json,
//                        "$..[?(@.class == 'reservationInformation')].table.tr.td[5].content").toString());
//        }
//        
//        
//        // TODO: flight cost when its a package
////        String totalAirFareCost = JsonPath.read(json,
////                "$..[?(@.class == 'flightResInfoCost')].td[1].content");
//        
//        // TODO: Fix for single traveler
//        List<String> travelers = JsonPath
//                .read(json,
//                        "$..[?(@.class == 'productContent airProduct')].table.tr[*].td[*]");
//        travelers.removeAll(Collections.singleton(""));
//        
//        if(travelers.size() == 0){
//            // didnt find any travelers with the expression above
//            travelers = JsonPath
//            .read(json,
//                    "$..[?(@.class == 'travelerInformation')].tr[*].td[*]");
//            travelers.removeAll(Collections.singleton(""));
//        }
//
//        // Air Segment information
//        String airLine = JsonPath.read(json,
//                "$..[?(@.class == 'segmentSection')].span[0].content");
//        String airCraftInfo = JsonPath.read(json,
//                "$..[?(@.class == 'segmentSection')].span[1]");
//
//        // Air Slice Content
//
//        // Depart
//        String departEvent = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[0].td[0]");
//        String departEventTime = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[0].td[1].strong");
//        String departAirport = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[0].td[2].content");
//        String departCity = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[0].td[2].strong");
//
//        // Arrival
//        String event = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[1].td[0]");
//        String eventTime = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[1].td[1].strong");
//        String airport = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[1].td[2].content");
//        String city = JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[1].td[2].strong");
//        
//        JsonPath.read(json,
//                "$..[?(@.class == 'sliceContent')].table.tr[5].td[2].strong");
//        
//        
//        if(changeOfPlanes){
//            String returnTripStart = ((JSONArray)JsonPath.read(json, "$..[?(@.class == 'slice')].h3")).get(1).toString();
//            
//            // Air Segment information
//            String returnAirLine = JsonPath.read(json,
//                    "$..[?(@.class == 'segmentSection')][1].span[0].content");
//            String returnAirCraftInfo = JsonPath.read(json,
//                    "$..[?(@.class == 'segmentSection')][1].span[1]");
//
//            // pick up return trip information
//            // Depart
//            String returnDepartEvent = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[0]");
//            String returnDepartEventTime = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[1].strong");
//            String returnDepartAirport = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[2].content");
//            String returnDepartCity = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[2].strong");
//
//            // Arrival
//            String returnEvent = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[0]");
//            String returnEventTime = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[1].strong");
//            String returnAirport = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[2].content");
//            String returnCity = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[2].strong");
//            
//            if(hasReturnTrip){
//                indexForSlice += 2;
//            }
//        }
//        
//        if(hasReturnTrip){
//            String returnTripStart = ((JSONArray)JsonPath.read(json, "$..[?(@.class == 'slice')].h3")).get(1).toString();
//            
//            // Air Segment information
//            String returnAirLine = JsonPath.read(json,
//                    "$..[?(@.class == 'segmentSection')][1].span[0].content");
//            String returnAirCraftInfo = JsonPath.read(json,
//                    "$..[?(@.class == 'segmentSection')][1].span[1]");
//
//            // pick up return trip information
//            // Depart
//            String returnDepartEvent = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[0]");
//            String returnDepartEventTime = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[1].strong");
//            String returnDepartAirport = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[2].content");
//            String returnDepartCity = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice + "].td[2].strong");
//
//            // Arrival
//            String returnEvent = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[0]");
//            String returnEventTime = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[1].strong");
//            String returnAirport = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[2].content");
//            String returnCity = JsonPath.read(json,
//                    "$..[?(@.class == 'sliceContent')].table.tr[" + indexForSlice  + 1 + "].td[2].strong");
//        }

        

    }
    
    private void processCarJSON(String json, ClassicTripSummaryView cstv){
        try {
            String carConfirmationNumber = JsonPath.read(json,
                    "$..[?(@.class == 'reservationInformation')].dl.dd[0]");
            String carPrimaryDriver = JsonPath.read(json,
                    "$..[?(@.class == 'reservationInformation')].dl.dd[1]");
            
            // TODO: CAR rate fails for air + car 
//            String carRate = JsonPath.read(json,
//                    "$..[?(@.class == 'reservationInformation')].dl.dd[2]");

            String carVendor = JsonPath.read(json,
                    "$..[?(@.class == 'vendorName')].h3[0].content");
            String carClass = JsonPath.read(json,
                    "$..[?(@.class == 'vendorName')].h3[0].span.content");

            
            // Since car and hotel use the same CSS class we need to 
            // check if this is not a hotel itineary
            String pickup = null;
            String pickupLocation = null;
            List<String> pickupContacts = null;
            String dropOff = null;
            String dropOffLocation = null;
            
            String isCarItin = JsonPath.read(json,
                    "$..[?(@.class == 'itinDetails')].tr[0].th[0]");
            if (isCarItin.equals("Check-in:")) {
                // this is a hotel itin, look for second instance of itinDetails
                pickup = JsonPath.read(json,
                       "$..[?(@.class == 'itinDetails')][1].tr[0].th[1]");
                pickupLocation = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')][1].tr[0].td.content");
                // String pickupContact = JsonPath.read(json,
                // "$..[?(@.class == 'itinDetails')].tr[0].td.p[0].span");

                pickupContacts = JsonPath
                        .read(json,
                                "$..[?(@.class == 'itinDetails')][1].tr[0].td.p[*].span[*]");

                dropOff = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')][1].tr[1].th[1]");
                dropOffLocation = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')][1].tr[1].td.content");
            }
            else {
                pickup = JsonPath.read(json,
                      "$..[?(@.class == 'itinDetails')].tr[0].th[1]");
                pickupLocation = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[0].td.content");
                // String pickupContact = JsonPath.read(json,
                // "$..[?(@.class == 'itinDetails')].tr[0].td.p[0].span");

                pickupContacts = JsonPath
                        .read(json,
                                "$..[?(@.class == 'itinDetails')].tr[0].td.p[*].span[*]");

                dropOff = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[1].th[1]");
                dropOffLocation = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[1].td.content");
            }

            // Car Cost Summary
            // TODO: AIR + CAR doesnt need this
            try {
                String carRatePath = "$..[?(@.id == 'carCostSummary')].tr[";
                String baseCarRate = JsonPath.read(json, carRatePath
                        + "0].td[1].content");
                String carTaxes = JsonPath.read(json, carRatePath
                        + "1].td[1].content");

                // list of car taxes and surcharges
                List<String> taxesAndSurcharges = JsonPath.read(json,
                        "$..[?(@.id == 'carCostSummary')].tr[*].td[*].content");
                List<String> taxes = new ArrayList<String>();
                for (int i = 2; i < taxesAndSurcharges.size() - 1; i++) {
                    taxes.add(taxesAndSurcharges.get(i));
                }
                // String carTaxRate = JsonPath.read(json, carRatePath
                // + "2].td.content");
                String carTotalEstimate = JsonPath.read(json, carRatePath
                        + (taxesAndSurcharges.size() - 1)
                        + "].th[1].div.content");
                String carAmountPaid = JsonPath.read(json, carRatePath
                        + (taxesAndSurcharges.size()) + "].td[1].strong");
                String carAmountDue = JsonPath.read(json, carRatePath
                        + (taxesAndSurcharges.size() + 1) + "].td[1].strong");

                CarCostSummary ccs = new CarCostSummary();
                ccs.setAmountDueAtRental(carAmountDue);
                ccs.setAmountPaidAtReservation(carAmountPaid);
                ccs.setBaseRate(baseCarRate);
                ccs.setTaxesAndFees(carTaxes);
                ccs.setTotalCarRentalEstimate(carTotalEstimate);
                ccs.getCarTaxRates().addAll(taxes);

                cstv.setCarCostSummary(ccs);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            
            // More car related information
            //String baseAdditonalCarInfo = "$..[?(@.content == 'Additional car rental information')].dl[1].dd[";
            // fix careNote, details and shuttle and arrival info they are out of sequence
            try{
                String baseAdditonalCarInfo  = "$..[?(@.class == 'toggleArea additionalInfo')].dl.dd[";
                String carNote = JsonPath.read(json, baseAdditonalCarInfo + "0]");
                String carDetails = JsonPath
                        .read(json, baseAdditonalCarInfo + "1]");
            
                // TODO: arrival info is not always present
                String arrivalInformation = JsonPath.read(json,
                        baseAdditonalCarInfo + "2]");
            
//            String shuttleInformation = JsonPath.read(json,
//                    baseAdditonalCarInfo + "3]");
            }
            catch(Exception e){
                log.error(e.getMessage());
            }

            pickup = pickup.replaceAll("Return ", "");
            pickup = pickup.replaceAll("Leave ", "");
            pickup = pickup.replaceAll(" : ", "");

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("E, MMMM d, Y hh:mma");
            DateTime dateTime = formatter.parseDateTime(pickup);

            CarProductView carPickup = new CarProductView();
            carPickup.getDrivers().add(carPrimaryDriver);
            carPickup.setEventType("Pickup");
            carPickup.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
                    .print(dateTime));
            carPickup.setRentalCompany(carVendor);
            carPickup.setRentalLocation(pickupLocation);
            carPickup.setEventEpoch(dateTime.getMillis());
            carPickup.setConfirmationNumber(carConfirmationNumber);
            carPickup.setCarClass(carClass);
            //carPickup.setCarNote(carNote);
            //carPickup.setCarDetails(carDetails);
            //carPickup.setArrivalInformation(arrivalInformation);
            //carPickup.setShuttleInformation(shuttleInformation);
            carPickup.getContacts().addAll(pickupContacts);
            
            //carPickup.setCarRate(baseCarRate);

            carPickup.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
                    .print(dateTime));
            carPickup.setTime(DateTimeFormat.forPattern("hh:mma")
                    .print(dateTime));
            carPickup.setEventType(pickup);

            dropOff = dropOff.replaceAll("Return ", "");
            dropOff = dropOff.replaceAll("Leave ", "");
            dropOff = dropOff.replaceAll(" : ", "");

            dateTime = formatter.parseDateTime(dropOff);

            CarProductView carDropOff = new CarProductView();
            carDropOff.getDrivers().add(carPrimaryDriver);
            carDropOff.setEventType("DropOff");
            carDropOff.setEventDate(dropOff);
            carDropOff.setRentalCompany(carVendor);
            carDropOff.setRentalLocation(pickupLocation);
            carDropOff.setEventEpoch(dateTime.getMillis());

            carDropOff.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
                    .print(dateTime));
            carDropOff.setEventType(dropOff);
            carDropOff.setTime(DateTimeFormat.forPattern("hh:mma")
                    .print(dateTime));
            carDropOff.setRentalLocation(dropOffLocation);

            ProductItinerary<ProductItinerarySortable> productIt = new ProductItinerary<ProductItinerarySortable>(
                    carPickup.getEventEpoch(), carPickup.getEventDate(),
                    carPickup);
            productIt.setCar(true);
            products.add(productIt);

            ProductItinerary<ProductItinerarySortable> productCarDropOff = new ProductItinerary<ProductItinerarySortable>(
                    carDropOff.getEventEpoch(), carDropOff.getEventDate(),
                    carDropOff);
            productIt.setCar(true);

            products.add(productCarDropOff);
        } catch (Exception e) {
            e.getMessage();
        }
    
        
//        Map<String, List<ProductItinerary<?>>> productMap = new LinkedHashMap<String, List<ProductItinerary<?>>>();
//     // sort the products
//        Collections.sort(products,
//                new ProductItinerarySorter<ProductItinerary<?>>());
//
//        // create a map with key = "Event Date"
//        for (ProductItinerary<ProductItinerarySortable> productItinerary : products) {
//            String eventDateKey = productItinerary.getProduct().getEventDate();
//            if (productMap.get(eventDateKey) != null) {
//                productMap.get(eventDateKey).add(productItinerary);
//            } else {
//                List<ProductItinerary<?>> productItins = new LinkedList<ProductItinerary<?>>();
//                productItins.add(productItinerary);
//                productMap.put(eventDateKey, productItins);
//            }
//        }
//        
//        ClassicTripSummaryView ctsv = new ClassicTripSummaryView();
//        ctsv.setProductMap(productMap);            
//            
//
//        // convert into JSON string
//        String jsonString = "";
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            jsonString = mapper.writeValueAsString(ctsv);
//            System.out.println("JSON String is " + jsonString);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
        
        
    }
    
    private String getFormattedTime(String json, String jsonPath){
        String timeString = "";
        // this is military time, convert to a string
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("HHmm")
                .parse( String.valueOf(JsonPath.read(json, jsonPath))));
            timeString = String.valueOf(calendar.get(Calendar.HOUR)) + ":" 
                + String.valueOf(calendar.get(Calendar.MINUTE));
            int ampm = calendar.get(Calendar.AM_PM);
            timeString = timeString + " " + (ampm == 1 ? "pm" : "am");

        }
        catch(Exception e){
            log.error(e.getMessage());
        }
        return timeString;
    }

    private void processHotelJSON(String json, ClassicTripSummaryView ctsv){
        log.info(json);
        List<String> hotelProperties = JsonPath.read(json, "$..[?(@.class =='reservationInformation')].dl.dt[*]");
        Map<String, String> hotelPropMap = new HashMap<String, String>();
        for (int i = 0; i < hotelProperties.size(); i ++) {
            hotelPropMap.put(hotelProperties.get(i),
                    JsonPath.read(json, "$..[?(@.class =='reservationInformation')].dl.dd[" + i + "]").toString());
        }
        
        
        
        
        String hotelName = null;
        // get vendor name
        try{
            hotelName = JsonPath.read(json, "$..[?(@.class == 'vendorName')].a.content[0]");
        }
        catch(Exception e){
            try{
                hotelName = JsonPath.read(json, "$..[?(@.class == 'vendorName')].content[0]");
            }
            catch(Exception ex){
                log.error(e.getMessage());
            }
        }
        

        try {
            // vendor address
            String hotelAddress1 = JsonPath.read(json,
                    "$..[?(@.class == 'vendorAddress')].content[0]");
            String hotelAddress2 = JsonPath.read(json,
                    "$..[?(@.class == 'vendorAddress')].content[1]");

            // vendor contact
            List<String> contactKeys = JsonPath.read(json,
                    "$..[?(@.class == 'vendorPhoneNumbers')].strong[*]");
            Map<String, String> hotelContactProps = new HashMap<String, String>();
            for (int i = 0; i < contactKeys.size(); i++) {
                hotelContactProps.put(
                        contactKeys.get(i),
                        JsonPath.read(
                                json,
                                "$..[?(@.class == 'vendorPhoneNumbers')].content["
                                        + i + "]").toString());
            }

            // hotel room description
            String roomDescription = JsonPath.read(json,
                    "$..[?(@.class == 'panelContent')].p.content[0]");

            // hotel itin details
            String checkinDate = JsonPath.read(json,
                    "$..[?(@.class == 'itinDetails')].tr[0].th[1]");
            String checkinTime = null;
            boolean militaryCheckInTime = false;
            if (JsonPath
                    .read(json, "$..[?(@.class == 'itinDetails')].tr[0].td") instanceof Integer) {
                // checkinTime = getFormattedTime(json,
                // "$..[?(@.class == 'itinDetails')].tr[0].td");
                checkinTime = String.valueOf(JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[0].td"));
                militaryCheckInTime = true;
            } else {
                checkinTime = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[0].td");
            }
            if (checkinTime.equals("")) {
                checkinTime = "10:00 am";
            }
            String checkOutDate = JsonPath.read(json,
                    "$..[?(@.class == 'itinDetails')].tr[1].th[1]");
            String checkOutTime = null;

            if (JsonPath
                    .read(json, "$..[?(@.class == 'itinDetails')].tr[1].td") instanceof Integer) {
                // this is military time, convert to a string
                // checkOutTime = getFormattedTime(json,
                // "$..[?(@.class == 'itinDetails')].tr[1].td");
                checkOutTime = String.valueOf(JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[1].td"));
            } else {
                checkOutTime = JsonPath.read(json,
                        "$..[?(@.class == 'itinDetails')].tr[1].td");
            }
            if (checkOutTime.equals("")) {
                checkOutTime = "10:00 am";
            }

            // additonal hotel information
            String additonalHotelInfo = JsonPath
                    .read(json,
                            "$..[?(@.class == 'toggleArea additionalInfo')].p.content[0]");

            // special requests
            List<String> specialRequestsKey = JsonPath.read(json,
                    "$..[?(@.h4 == 'Special requests:')].content[*]");
            Map<String, String> specialRequests = new HashMap<String, String>();
            for (int i = 0; i < specialRequestsKey.size(); i++) {
                specialRequests.put(
                        specialRequestsKey.get(i),
                        JsonPath.read(
                                json,
                                "$..[?(@.h4 == 'Special requests:')].p[" + i
                                        + "].content").toString());
            }

            // hotel room and guest info
            List<String> roomGuestKeys = JsonPath
                    .read(json,
                            "$..[?(@.class == 'toggleArea additionalInfo')].dl[0].dt[*]");
            Map<String, String> roomGuestMap = new HashMap<String, String>();
            for (int i = 0; i < roomGuestKeys.size(); i++) {
                roomGuestMap.put(
                        roomGuestKeys.get(i),
                        JsonPath.read(
                                json,
                                "$..[?(@.class == 'toggleArea additionalInfo')].dl[0].dd["
                                        + i + "]").toString());
            }

            Map<String, String> cancellationInfo = null;
            // cancellation and other information
            try {
                List<String> cancellationKeys = JsonPath
                        .read(json,
                                "$..[?(@.class == 'toggleArea additionalInfo')].dl[1].dt[*]");
                cancellationInfo = new HashMap<String, String>();
                for (int i = 0; i < cancellationKeys.size(); i++) {
                    if (cancellationKeys.get(i).equals("Cancellation:")) {
                        String content = "";
                        String cancelMessage1 = JsonPath
                                .read(json,
                                        "$..[?(@.class == 'toggleArea additionalInfo')].dl[1].dd[0].div.p.content");
                        String cancelMessage2 = JsonPath
                                .read(json,
                                        "$..[?(@.class == 'toggleArea additionalInfo')].dl[1].dd[0].div.div.p.content");
                        content = cancelMessage1 + "|" + cancelMessage2;
                        cancellationInfo.put(cancellationKeys.get(i), content);

                    } else {
                        cancellationInfo.put(
                                cancellationKeys.get(i),
                                JsonPath.read(
                                        json,
                                        "$..[?(@.class == 'toggleArea additionalInfo')].dl[1].dd["
                                                + i + "]").toString());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            HotelCostSummary hcs = null;
            try {
                // cost and billing
                String hotelCostSummary = JsonPath
                        .read(json,
                                "$..[?(@.class == 'myStuffPanel costSummaryPanel')].h3.content[0]");

                // cost breakdown
                String subTotal = JsonPath
                        .read(json,
                                "$..[?(@.class == 'myStuffPanel costSummaryPanel')].div.table.tr[0].td[0].content");

                String taxes = JsonPath
                        .read(json,
                                "$..[?(@.class == 'myStuffPanel costSummaryPanel')].div.table.tr[1].td[0].content");

                String total = JsonPath
                        .read(json,
                                "$..[?(@.class == 'myStuffPanel costSummaryPanel')].div.table.tr[2].td[0].content");

                hcs = new HotelCostSummary();
                hcs.setCost(subTotal);
                hcs.setTaxes(taxes);
                hcs.setTotal(total);
                hcs.setHotelCostSummary(hotelCostSummary);

            } catch (Exception e) {
                log.error(e.getMessage());
            }

            List<String> travelers = null;
            try {
                // TODO: Fix reservation made for
                travelers = JsonPath.read(json,
                        "$..[?(@.class == 'travelerName')].content");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("E, MMMM d, Y hh:mm a");

            if (militaryCheckInTime) {
                formatter = DateTimeFormat.forPattern("E, MMMM d, Y HHmm");
            }
            // Sun, Apr 17, 2011 02:00 PM
            DateTime dateTime = formatter.parseDateTime(checkinDate + " "
                    + checkinTime);

            HotelProductView hpv = new HotelProductView();
            hpv.setHotelAddress(hotelAddress1);
            hpv.setHotelAddress2(hotelAddress2);
            hpv.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x").print(
                    dateTime));
            hpv.setEventEpoch(dateTime.getMillis());
            hpv.setEventType("Check-In");
            hpv.setHotelName(hotelName);
            hpv.setTravelers(travelers);
            hpv.setRoomDescription(roomDescription);
            hpv.setHotelContacts(hotelContactProps);
            hpv.setAdditonalHotelInfo(additonalHotelInfo);
            hpv.setSpecialRequests(specialRequests);
            hpv.setHotelGuestInfo(roomGuestMap);
            hpv.setCancellationInfo(cancellationInfo);
            hpv.setTime(DateTimeFormat.forPattern("hh:mma").print(dateTime));

            dateTime = formatter.parseDateTime(checkOutDate + " "
                    + checkOutTime);

            HotelProductView hpvCheckout = new HotelProductView();
            hpvCheckout.setHotelAddress(hotelAddress1);
            hpvCheckout.setHotelAddress2(hotelAddress2);
            hpvCheckout.setEventDate(DateTimeFormat.forPattern("E, MMMM d, x")
                    .print(dateTime));
            hpvCheckout.setEventEpoch(dateTime.getMillis());
            hpvCheckout.setEventType("Check-Out");
            hpvCheckout.setHotelName(hotelName);
            hpvCheckout.setTravelers(travelers);
            hpvCheckout.setRoomDescription(roomDescription);
            hpvCheckout.setHotelContacts(hotelContactProps);
            hpvCheckout.setAdditonalHotelInfo(additonalHotelInfo);
            hpvCheckout.setSpecialRequests(specialRequests);
            hpvCheckout.setHotelGuestInfo(roomGuestMap);
            hpvCheckout.setCancellationInfo(cancellationInfo);
            hpvCheckout.setTime(DateTimeFormat.forPattern("hh:mma").print(
                    dateTime));

            ctsv.setHostCostSummary(hcs);

            ProductItinerary<ProductItinerarySortable> productIt = new ProductItinerary<ProductItinerarySortable>(
                    hpv.getEventEpoch(), hpv.getEventDate(), hpv);
            productIt.setCar(true);
            products.add(productIt);

            ProductItinerary<ProductItinerarySortable> productCarDropOff = new ProductItinerary<ProductItinerarySortable>(
                    hpvCheckout.getEventEpoch(), hpvCheckout.getEventDate(),
                    hpvCheckout);
            productIt.setHotel(true);

            products.add(productCarDropOff);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
        
    }
    
    private List<ASTripEventView> processAttractionJSON(String json){
        List<ASTripEventView> services = new ArrayList<ASTripEventView>();
        try {
            
            
            List<JSONObject> asList = JsonPath.read(json,
                    "$..[?(@.id == 'oasDetails')].div[*]");
            
            List<String> attractionServiceNames = JsonPath.read(json,
                    "$..[?(@.id == 'oasDetails')].div[*].div[*].strong");
            List<String> attractionServiceLocators = JsonPath.read(json,
                    "$..[?(@.id == 'oasDetails')].div[*].div[*].content");
            int i = 0, j = 0;
            for (String locator : attractionServiceLocators) {
                ASTripEventView atv = new ASTripEventView();
                atv.setAttractionServiceName(attractionServiceNames.get(j));
                atv.setOrbitzRecordLocator(locator);
                if(attractionServiceLocators.size() > 1){
                    atv.setDate(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table[" + i + "].tbody.tr[0].td[1].content").toString());
                    atv.setQuantity(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table[" + i + "].tbody.tr[1].td[1].content").toString());
                    atv.setCost(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table[" + i + "].tbody.tr[2].td[1].content").toString());
                }
                else{
                    atv.setDate(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table.tbody.tr[0].td[1].content").toString());
                    atv.setQuantity(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table.tbody.tr[1].td[1].content").toString());
                    atv.setCost(JsonPath.read(json, "$..[?(@.id == 'oasDetails')].div[0].table.tbody.tr[2].td[1].content").toString());
                }
                
                services.add(atv);
                j+=2;
                i ++;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return services;
        
        
    }
    

}
