package it.uniroma3.atcs.acmemuseum.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.repository.POIRepository;

@Service
@Transactional
public class POIService {
	
	@Autowired
	private POIRepository repository;
	
	
	public void add(POI poi) {
		this.repository.save(poi);
	}
	
	public void addAll(Collection<POI> positions) {
		for(POI poi: positions) {
			this.repository.save(poi); 
		}
	}
	
	public POI getPOIById(Long id) {
		return this.repository.findById(id).orElse(null);
	}
	
	public boolean containsPOIByName(String name) {
		return this.repository.findByName(name) != null; 
	}
	
	public POI getPOIByName(String name) {
		return this.repository.findByName(name);
	}
	
	public List<POI> getAllPOIs() {
		return this.repository.findAll().stream().collect(Collectors.toList()); 
	}
	
	public Map<String, POI> getAllPOIsAsMap() {
		 Map<String, POI> nameToPOIs = new HashMap<>(); 
		 Collection<POI> positions = this.repository.findAll();
		 positions.stream().forEach(p -> nameToPOIs.put(p.getName(), p));
		 return nameToPOIs; 
	}

}
