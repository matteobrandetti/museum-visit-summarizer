package it.uniroma3.atcs.acmemuseum.model;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @NoArgsConstructor @Getter @Setter
public class POIElement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 
	
	@Column
	private LocalTime startTime; 
	
	@Column
	private LocalTime endTime; 
	
	@OneToOne(fetch = FetchType.EAGER)
	private POI position; 
	
	
	public POIElement(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime; 
		this.endTime = endTime; 
	}
	
	public POIElement(LocalTime startTime, LocalTime endTime, POI position) {
		this(startTime, endTime); 
		this.position = position; 
	}

	public Long getTime() {
		return startTime.until(endTime, SECONDS); 
	}

	@Override
	public String toString() {
		return "POIElement [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", position=" + position
				+ "]";
	} 
	
	
}
