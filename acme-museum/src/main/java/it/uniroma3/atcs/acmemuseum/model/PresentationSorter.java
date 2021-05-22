package it.uniroma3.atcs.acmemuseum.model;

import java.time.LocalTime;
import java.util.Comparator;

public class PresentationSorter implements Comparator<Presentation>{

	@Override
	public int compare(Presentation arg0, Presentation arg1) {
		LocalTime startTime0 = arg0.getStartTime(); 
		LocalTime startTime1 = arg1.getStartTime();
		return startTime0.compareTo(startTime1); 
	}

}
