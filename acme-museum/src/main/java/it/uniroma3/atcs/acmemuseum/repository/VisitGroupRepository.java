package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.atcs.acmemuseum.model.VisitGroup;

@Repository
public interface VisitGroupRepository extends CrudRepository<VisitGroup, Long> {

	public Collection<VisitGroup> findAll();
	
 	public VisitGroup findByNumber(Integer number); 
 	
 	
}
