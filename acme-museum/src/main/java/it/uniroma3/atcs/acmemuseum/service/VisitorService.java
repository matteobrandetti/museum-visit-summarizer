package it.uniroma3.atcs.acmemuseum.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.repository.VisitorRepository;

@Service
@Transactional
public class VisitorService {
	
	@Autowired
	private VisitorRepository visitorRepository;
	
	@Autowired
	private VisitService visitService; 
	
	private List<Visitor> visitors; 
	
	private List<Visitor> visitorsWithCompleteInfos; 
	
	
	public void add(Visitor visitor) {
		this.visitorRepository.save(visitor);
	}
	
	public Visitor getVisitorByNumber(Integer number) {
		return this.visitorRepository.findByNumber(number);
	}
	
	public Visitor getVisitorByNumberWithCompleteInfo(Integer number) {
		Visitor v = this.visitorRepository.findByNumber(number);
		if (v != null) {
			Visit visit = v.getVisit();
			v.setVisit(this.visitService.getVisitWithPresentationsByVisitId(visit.getId()));
		}
		return v; 
	}
	
	public Visitor getVisitorById(Long id) {
		return this.visitorRepository.findById(id).orElse(null);
	}
	
	public Collection<Visitor> getAllVisitors(){
		return this.visitorRepository.findAll(); 
	}
	
	public List<Visitor> getAllVisitorsAsList(){
		if(this.visitors == null) {
			this.visitors = this.visitorRepository.findAll().stream().collect(Collectors.toList()); 
		}
		return this.visitors;  
	}
	
	public List<Visitor> getVisitorsWithCompleteInfo() {
		if (this.visitorsWithCompleteInfos == null) {
			List<Visitor> visitors = this.getAllVisitorsAsList();
			for (Visitor v : visitors) {
				Visit visit = v.getVisit();
				visit.setPresentations(
						this.visitService.getVisitWithPresentationsByVisitId(visit.getId()).getPresentations());
			}
			this.visitorsWithCompleteInfos = visitors; 
		}
		return this.visitorsWithCompleteInfos; 
	}

	public void addAllVisitors(List<Visitor> visitors) {
		visitors.stream().forEach(v -> visitorRepository.save(v));
	}

}
