package it.uniroma3.atcs.acmemuseum.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class DayHour {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 
	
	@Column
	private Integer hour; 
	
	@Column
	private Integer numberOfVisitors; 
	
	@ElementCollection
    @MapKeyColumn(name = "room_name")
    @Column(name = "number_of_visitors")
    private Map<String, Integer> roomToNumberOfVisitors;
	

}
