package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.atcs.acmemuseum.model.POI;

@Repository
public interface POIRepository extends CrudRepository<POI, Long> {
	
	public Collection<POI> findAll();
	
	public POI findByName(String name); 


}
