package com.orbitz.oltp.util;

import java.util.Comparator;

import com.orbitz.oltp.app.view.model.ProductItinerary;

/*
 * abhaila
 * 
 * Adaptee: ItinerarySorter
 * 
 * This class sorts different product itineraries based on their dates
 */
@SuppressWarnings("rawtypes")
public class ProductItinerarySorter<T extends ProductItinerary> implements Comparator<T>{

	public int compare(T o1, T o2) {
		return o1.getStartDate().compareTo(o2.getStartDate());
	}


	
}

