package it.uniroma3.atcs.acmemuseum.model;

import java.time.LocalTime;
import java.util.Comparator;

public class POIElementSorter implements Comparator<POIElement>{

	@Override
	public int compare(POIElement arg0, POIElement arg1) {
		LocalTime startTime0 = arg0.getStartTime(); 
		LocalTime startTime1 = arg1.getStartTime();
		return startTime0.compareTo(startTime1); 
	}

}
