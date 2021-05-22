package it.uniroma3.atcs.acmemuseum.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.repository.VisitRepository;

@Service
@Transactional
public class VisitService {
	
	@Autowired
	private VisitRepository repository;
	
	
	public Visit getVisitById(Long id) {
		return this.repository.findById(id).orElse(null); 
	}
	
	public Collection<Visit> getAllVisits(){
		return this.repository.findAll(); 
	}
	
	public List<Visit> getAllVisitsAsList(){
		return this.repository.findAll().stream().collect(Collectors.toList()); 
	}
	
	public Visit getVisitWithPresentationsByVisitId(long id){
		return this.repository.findVisitWithCompleteInfoById(id); 
	}
	

}
