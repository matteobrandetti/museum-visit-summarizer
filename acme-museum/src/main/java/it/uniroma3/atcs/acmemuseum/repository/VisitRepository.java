package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.atcs.acmemuseum.model.Visit;

@Repository
public interface VisitRepository extends CrudRepository<Visit, Long> {

	public Collection<Visit> findAll();
	
	@Query("SELECT v" +
            "FROM Visit v "
            + "JOIN FETCH v.presentations p "
            + "WHERE v.id = :id ")
	public Visit findVisitWithCompleteInfoById(@Param("id") Long id);
	
}
