package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.atcs.acmemuseum.model.Visitor;


@Repository
public interface VisitorRepository extends CrudRepository<Visitor, Long> {

	public Collection<Visitor> findAll();
	
 	public Visitor findByNumber(Integer number); 
 	
 	
 	
}
