package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.atcs.acmemuseum.model.MuseumStatistic;

public interface StatisticRepository extends CrudRepository<MuseumStatistic, Long> {
	
	public Collection<MuseumStatistic> findAll();
	
}

