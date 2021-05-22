package it.uniroma3.atcs.acmemuseum.model;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity @NoArgsConstructor @Getter @Setter
public class Visit {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JoinColumn(name = "visit")
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private List<POIElement> elementsPOI;
	
	@JoinColumn(name = "visit")
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Presentation> presentations; 
	
	
	public void addElementPOI(POIElement pe) {
		this.elementsPOI.add(pe); 
	}
	
	public void addPresentation(Presentation p) {
		this.presentations.add(p); 
	}
	
	/**
	 * It returns the poi elements sorted by time
	 * @return the poi visited sorted by time.
	 */
	public LinkedList<POIElement> getElementsPOIOrderedByStartTime(){
		LinkedList<POIElement> positions = new LinkedList<>(this.elementsPOI); 
		Collections.sort(positions, new POIElementSorter());
		return positions; 
	}
	
	
	/**
	 * It returns the presentations sorted by start time
	 * @return a list of presentations sorted 
	 */
	public LinkedList<Presentation> getPresentationsOrderedByStartTime(){
		LinkedList<Presentation> pres = new LinkedList<>(this.presentations); 
		Collections.sort(pres, new PresentationSorter());
		return pres; 
	}
	
	
	/**
	* It returns the entrance time (h:min:sec) of the visit, i.e.
	 * the time where he was first detected on a poi
	 * @return the entrance time in the museum
	 */
	public LocalTime getEntranceTime() {
		return this.getElementsPOIOrderedByStartTime().getFirst().getStartTime(); 
	}
	
	/**
	 * It returns the list of room visited by the visitor
	 * sorted by visit time
	 * @return a list of room visited
	 */
	public List<Room> getVisitedRoomSortedByVisitTime(){
		LinkedList<Room> rooms = new LinkedList<>(); 
		for(POIElement pe: this.elementsPOI) {
			if (!rooms.isEmpty()) {
				if (!rooms.getLast().equals(pe.getPosition().getRoom())) {
					rooms.add(pe.getPosition().getRoom());
				} 
			}
			else {
				rooms.add(pe.getPosition().getRoom());
			}
		}
		return rooms; 
	}
	
	
	/**
	 * It returns the entrance hour of the visit, i.e.
	 * the hour where he was first detected on a poi
	 * @return the entrance hour in the museum
	 */
	
	public Integer getEntranceHour() {
		return this.getEntranceTime().getHour(); 
	}
	
	
	/**
	* It returns the exit time (h:min:sec) of the visit, i.e.
	 * the time where he was last detected on a poi
	 * @return the exit time in the museum
	 */
	public LocalTime getExitTime() {
		return this.getElementsPOIOrderedByStartTime().getLast().getEndTime(); 
	}
	
	
	/**
	 * It computes the total time of the visit
	 * @return the total time of the visit in SECONDS
	 */
	public Long getTotalTime() {
		LocalTime entranceTime = this.getEntranceTime(); 
		LocalTime exitTime = this.getExitTime();
		return entranceTime.until(exitTime, SECONDS); 
	}
	
	
	/**
	 * It computes the total time of the visit
	 * @return the total time of the visit in MINS
	 */
	public Long getTotalTimeInMin() {
		LocalTime entranceTime = this.getEntranceTime(); 
		LocalTime exitTime = this.getExitTime();
		return entranceTime.until(exitTime, MINUTES); 
	}
	
	
	
	/**
	 * It checks whether the visitor has been on 
	 * a specific POI
	 * @param poi, the poi to look for
	 * @return whether the visitor has been on a poi
	 */
	
	public boolean hasBeenOnPOI(POI poi) {
		return this.elementsPOI.stream().anyMatch(ep -> ep.getPosition().equals(poi));
	}
	
	/**
	 * It computes the total seconds a visitor
	 * has been on a specific POI. If the visitor has not 
	 * been on the specific POI it returns 0 sec.
	 * @param the poi to compute the seconds
	 * @return the total seconds a visitor has been on a specific POI
	 */
	public float getSecondsOnPOI(POI poi) {
		float totalTime = 0;
		for (POIElement pe : this.elementsPOI) {
			if (pe.getPosition().getName().equals(poi.getName())) {
				LocalTime endTime = pe.getEndTime();
				LocalTime startTime = pe.getStartTime();
				long time = startTime.until(endTime, SECONDS);
				totalTime += time;
			}
		}
		return totalTime;
	}
	
	
	public Map<String, Long> getTotalTimeOfPresentationsByName(){
		Map<String, Long> presToTime = new HashMap<>(); 
		for(Presentation p: this.presentations) {
			presToTime.put(p.getPosition().getName(), p.getTotalTime()); 
		}
		return presToTime;  
	}
	
	
	public long getAverageTimeOfPresentation() {
		long sumTime = 0; 
		long total = this.presentations.size(); 
		for(Presentation p: this.presentations) {
			sumTime += p.getTotalTime(); 
		}
	    return (long) (sumTime / total); 
	}
	
	/**
	 * It computes the average rate given in this visit
	 * by the visitor
	 * @return the average rate of the presentations
	 */
	public float getAverageRateOfPresentation() {
		float sumRates = 0; 
		float total = this.presentations.size(); 
		for(Presentation p: this.presentations) {
			sumRates += p.getRate(); 
		}
	    return sumRates / total; 
	}
	
	
	public int getNumberOfPresentations() {
		return this.presentations.size(); 
	}
	
	
	/**
	 * It counts the stopped presentations
	 * @return the number of presentation stopped
	 */
	public int getNumberOfStoppedPresentations() {
		return this.presentations.stream()
				.filter(p -> p.isInterrupted())
				.collect(Collectors.toList())
				.size();
	}
	
	

}
