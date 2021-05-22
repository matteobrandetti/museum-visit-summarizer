package it.uniroma3.atcs.acmemuseum.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.atcs.acmemuseum.model.Room;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
	
	public Collection<Room> findAll();
	
	public Room findByCode(String code); 


}