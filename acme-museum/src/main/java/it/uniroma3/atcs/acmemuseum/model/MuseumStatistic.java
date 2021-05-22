package it.uniroma3.atcs.acmemuseum.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class MuseumStatistic {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 
	
	@Column
	private long averageVisitTimeInMin; 
	
	@Column
	private long averagePresentationTime; 
	
	@Column
	private float averagePresentationRate; 
	
	@Column
	private int averageVisitGroupTimeInMin; 
	
	@Column
	private long averageGroupPresentationTime; 
	
	@Column
	private float averageGroupPresentationRate; 
	
	
	
	@Column
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<DayHour> hours; 
    
	
	public void addHour(Integer hour) {
		if (hour <= 24 && hour >= 1) {
			DayHour dh = new DayHour();
			dh.setHour(hour);
			this.hours.add(dh);
		} 
	}
	
	/**
	 * For a given hour it sets the number of visitors
	 * @param hour: a given hour in the day 
	 * @param num: the number of visitors
	 */
	public void setHourNumberOfVisitors(Integer hour, Integer num) {
		this.hours.stream().filter(h-> h.getHour().equals(hour)).forEach(h -> h.setNumberOfVisitors(num));
	}
	
	
}
