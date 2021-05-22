package it.uniroma3.atcs.acmemuseum.model;

import java.util.List;

import javax.persistence.Column;
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
public class VisitGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 
	
	@Column
	private Integer number; 
	
//	@OneToOne(fetch = FetchType.EAGER)
//	private Visit visit; 
	
	@JoinColumn(name = "visit_group")
	@OneToMany(fetch = FetchType.EAGER)
	private List<Visitor> visitors; 

	
	public VisitGroup(Integer number) {
		this.number = number; 
	}

	public void addVisitor(Visitor visitor) {
		this.visitors.add(visitor); 		
	}
	
	public long getAveragePresentationTime() {
		int size = 0; 
		int sum = 0; 
		for(Visitor v: this.visitors) {
			if (!v.getVisit().getPresentations().isEmpty()) {
				sum += v.getVisit().getAverageTimeOfPresentation();
				size++;
			} 
		}
		if(size == 0) {
			return 0; 
		}
		return (long) (sum /size); 
	}
	
	public float getAveragePresentationRate() {
		int size = 0; 
		float sum = 0; 
		for(Visitor v: this.visitors) {
			if (!v.getVisit().getPresentations().isEmpty()) {
				sum += v.getVisit().getAverageRateOfPresentation(); 
				size++; 
			}
			
		}
		if(size == 0) {
			return 0; 
		}
		return (float) (sum /size); 
	}
	
	public Integer getAverageTimeMin() {
		int size = 0;
		int sumMin = 0;
		for (Visitor v : this.visitors) {
			sumMin += v.getVisit().getTotalTimeInMin();
			size++;
		}
		if(this.visitors.isEmpty()) {
			return 0; 
		}
		return (Integer) (sumMin / size);

	}

}
