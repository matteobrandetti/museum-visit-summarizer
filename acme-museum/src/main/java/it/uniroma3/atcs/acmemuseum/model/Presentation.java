package it.uniroma3.atcs.acmemuseum.model;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import static java.time.temporal.ChronoUnit.SECONDS;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity @NoArgsConstructor @Getter @Setter
public class Presentation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 
	
	@Column
	private LocalTime startTime; 
	
	@Column
	private LocalTime endTime; 
	
	@Column
	private Integer rate; 
	
	@Column
	private boolean isInterrupted; 
	
	@OneToOne(fetch = FetchType.EAGER)
	private POI position;

	public Presentation(LocalTime startTime, LocalTime endTime, Integer rate, POI position, boolean isInterrupted) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.rate = rate;
		this.position = position;
		this.isInterrupted = isInterrupted; 
	}

	public Long getTotalTime() {
		return startTime.until(endTime, SECONDS); 
	} 
	
	

}
