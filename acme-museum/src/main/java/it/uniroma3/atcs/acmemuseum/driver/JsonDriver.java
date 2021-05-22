package it.uniroma3.atcs.acmemuseum.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.model.POIElement;
import it.uniroma3.atcs.acmemuseum.model.Presentation;
import it.uniroma3.atcs.acmemuseum.model.Room;
import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.model.VisitGroup;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.service.POIService;
import it.uniroma3.atcs.acmemuseum.service.RoomService;
import it.uniroma3.atcs.acmemuseum.service.VisitGroupService;
import it.uniroma3.atcs.acmemuseum.service.VisitorService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@Getter
@Setter
public class JsonDriver {

	@Autowired
	private POIService positionService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private VisitGroupService visitGroupService;
	
	@Autowired
	private RoomService roomService;
	
	/**
	 * It extracts information from the museum file, transformed in json format,
	 * it reconstructs the objects and save them in the database
	 * @param path, the local path in the file system
	 * @param filename, the name of the json file to read from
	 * @throws IOException
	 * @throws JsonException
	 */
	public void saveMuseumFromFileToPersistence(String path, String filename) throws IOException, JsonException {
		Reader reader = Files.newBufferedReader(Paths.get(path, filename));
		persistMuseum(reader); 
	}

	/**
	 * It extracts information from the log file, transformed in json format,
	 * it reconstructs the objects and save them in the database
	 * @param path, the local path in the file system
	 * @param filename, the name of the json file to read from
	 * @throws IOException
	 * @throws JsonException
	 */
	public void saveVisitorsFromFileToPersistence(String path, String filename) throws IOException, JsonException {
		Reader reader = Files.newBufferedReader(Paths.get(path, filename));
		persistVisitorsAndGroups(reader); 
	}
	
	/**
	 * It extracts information from the json file given as input, it reconstructs the objects
	 * and save them in the database.
	 * @param stream, the stream file of the visitors log
	 * @throws IOException
	 * @throws JsonException
	 */
	public void saveVisitorsFromStreamToPersistence(byte[] stream) throws IOException, JsonException {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(stream));
		persistVisitorsAndGroups(reader); 
	}
	
	
	
	private void persistMuseum(Reader reader) throws JsonException {
		List<Room> rooms = new ArrayList<>();
		JsonArray array = (JsonArray) Jsoner.deserialize(reader);
		array.forEach(entry -> {
			JsonObject jo = (JsonObject) entry;
			String code = (String) jo.get("room_code");
			Room room = new Room(code);
			room.setPositions(new LinkedList<>());
			
			JsonArray positionsArray = (JsonArray) jo.get("positions");
			positionsArray.forEach(posEntry -> {
				JsonObject posElem = (JsonObject) posEntry;

				String posName = (String) posElem.get("name");
				POI poi = new POI(posName); 
				room.addPOI(poi); 
			});
			
			rooms.add(room); 

		});
		this.roomService.addAll(rooms);
	}
	

	private void persistVisitorsAndGroups(Reader reader) throws JsonException {
		List<Visitor> visitors = new ArrayList<>();
		Map<String, POI> namesToPOIs = this.positionService.getAllPOIsAsMap();
		Map<Integer, VisitGroup> visitGroups = new HashMap<>();
		JsonArray array = (JsonArray) Jsoner.deserialize(reader);

		array.forEach(entry -> {
			JsonObject jo = (JsonObject) entry;
			Integer number = Integer.parseInt((String) jo.get("group_number"));
			VisitGroup vg = new VisitGroup(number); 
			vg.setVisitors(new LinkedList<>());
			visitGroups.put(number,vg);

		});

		array.forEach(entry -> {
			JsonObject jo = (JsonObject) entry;
			Integer number = Integer.parseInt((String) jo.get("number"));
			Visitor visitor = new Visitor(number);
			Visit visit = new Visit();

			visitor.setVisit(visit);
			visit.setElementsPOI(new LinkedList<>());
			visit.setPresentations(new LinkedList<>());

			JsonArray positionsArray = (JsonArray) jo.get("positions");

			positionsArray.forEach(posEntry -> {
				JsonObject posElem = (JsonObject) posEntry;

				String posName = (String) posElem.get("name");
				POI poi = namesToPOIs.get(posName);

				String startTime = (String) posElem.get("start_time");
				String endTime = (String) posElem.get("end_time");

				POIElement poiElem = new POIElement(LocalTime.parse(startTime), LocalTime.parse(endTime), poi);
				visit.addElementPOI(poiElem);

			});

			JsonArray presentationsArray = (JsonArray) jo.get("presentations");

			presentationsArray.forEach(presEntry -> {
				JsonObject pres = (JsonObject) presEntry;

				String posName = (String) pres.get("name");
				POI poi = namesToPOIs.get(posName);

				String startTime = (String) pres.get("start_time");
				String endTime = (String) pres.get("end_time");
				Boolean isInterrupted = (Boolean) pres.get("has_stopped");
				Integer rate = Integer.parseInt((String) pres.get("rate"));
				Presentation p = new Presentation(LocalTime.parse(startTime), LocalTime.parse(endTime), rate, poi, isInterrupted);
				visit.addPresentation(p);

			});
			
			if(!visitor.getVisit().getElementsPOI().isEmpty()) {
				visitors.add(visitor);
				Integer groupNumber = Integer.parseInt((String) jo.get("group_number"));
				VisitGroup group = visitGroups.get(groupNumber);
				group.addVisitor(visitor);	
			}
		});

		this.visitorService.addAllVisitors(visitors);
		this.visitGroupService.addAll(visitGroups.values());

		
	}

}
