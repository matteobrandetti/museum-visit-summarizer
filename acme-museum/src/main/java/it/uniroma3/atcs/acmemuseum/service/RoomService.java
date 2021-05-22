package it.uniroma3.atcs.acmemuseum.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.Room;
import it.uniroma3.atcs.acmemuseum.repository.RoomRepository;

@Service
@Transactional
public class RoomService {

	@Autowired
	private RoomRepository repository;

	public void add(Room room) {
		this.repository.save(room);
	}

	public void addAll(Collection<Room> rooms) {
		for (Room room : rooms) {
			this.repository.save(room);
		}
	}

	public Room getRoomById(Long id) {
		return this.repository.findById(id).orElse(null);
	}

	public Room getRoomByCode(String code) {
		return this.repository.findByCode(code);
	}

	public List<Room> getAllRooms() {
		return this.repository.findAll().stream().collect(Collectors.toList());
	}

}
