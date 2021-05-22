package it.uniroma3.atcs.acmemuseum.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.DayHour;
import it.uniroma3.atcs.acmemuseum.model.MuseumStatistic;
import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.model.POIElement;
import it.uniroma3.atcs.acmemuseum.model.Room;
import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.model.VisitGroup;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.repository.StatisticRepository;


@Service
@Transactional
public class StatisticsService {
	
	@Autowired
	private VisitorService visitorService; 
	
	@Autowired
	private POIService positionService; 
	
	@Autowired
	private VisitGroupService visitGroupService; 
	
	@Autowired
	private StatisticRepository statisticRepository; 
	
	
	public MuseumStatistic getMuseumStatistic() {
		return this.statisticRepository.findAll().stream().collect(Collectors.toList()).get(0);
	}
	
	/**
	 * It restores the statistics
	 */
	public void resetMuseumStatistic() {
		this.statisticRepository.deleteAll();
	}
	
	/**
	 * It computes the statistic of the museum and
	 * saves them in the database
	 */
	public void computeAndSaveMuseumStatistics() {
		Map<Integer, Integer> visitorsByHour = this.getNumberOfVisitorsByHour(); 
		MuseumStatistic ms = new MuseumStatistic(); 
		ms.setHours(new LinkedList<>());
		for(int i = 1; i<=24; i++) {
			ms.addHour(i);
			if(visitorsByHour.containsKey(i)) {
				int nVisitors = visitorsByHour.get(i); 
				ms.setHourNumberOfVisitors(i, nVisitors);
			}
			else {
				ms.setHourNumberOfVisitors(i, new Integer(0));
			}
		}
		Map<Integer, Map<Room, Integer>> hourToRoomsToVisitors = this.getNumberOfVisitorsPerHourPerRoom(); 
		for(DayHour th: ms.getHours()) {
			if(hourToRoomsToVisitors.containsKey(th.getHour())) {
				Map<String, Integer> roomToVisitors = new HashMap<>(); 
				for(Entry<Room, Integer> pair: hourToRoomsToVisitors.get(th.getHour()).entrySet()) {
					roomToVisitors.put(pair.getKey().getCode(), pair.getValue()); 
				}
				th.setRoomToNumberOfVisitors(roomToVisitors);
			}
		}
		
		/*Compute museum statistics*/
		ms.setAverageVisitTimeInMin(this.getAverageVisitTimeInMin());
		ms.setAveragePresentationTime(this.getAveragePresentationTime());
		ms.setAveragePresentationRate(this.getAveragePresentationRate());
		ms.setAverageGroupPresentationRate(this.getAverageGroupPresentationRate());
		ms.setAverageGroupPresentationTime(this.getAverageGroupPresentationTime());
		ms.setAverageVisitGroupTimeInMin(this.getAverageGroupTime());
		
		this.statisticRepository.save(ms); 
	}
	
	/**
	 * It computes the average visit time of the visitors in seconds
	 * @return the average visit time of the visitors of the museum
	 */
	public long getAverageVisitTimeInSec() {
		List<Visitor> visitors = new ArrayList<>(this.visitorService.getVisitorsWithCompleteInfo()); 
		float totalVisitors = visitors.size(); 
        float totAvgTime = 0; 
		for(Visitor v: visitors) {
			totAvgTime += v.getVisit().getTotalTime();
		}
		
		return (long) (totAvgTime / totalVisitors); 
	}
	
	
	/**
	 * It computes the average visit time of the visitors in mins
	 * @return the average visit time of the visitors of the museum
	 */
	public long getAverageVisitTimeInMin() {
		return (long) (this.getAverageVisitTimeInSec() / 60); 
	}

	
	public long getAveragePresentationTime() {
		List<Visitor> visitors = new ArrayList<>(this.visitorService.getVisitorsWithCompleteInfo()); 
		float totalVisitors = 0; 
        float totAvgTime = 0; 
        for(Visitor v: visitors) {
        	Visit visit = v.getVisit();
        	if (!visit.getPresentations().isEmpty()) {
				totAvgTime += visit.getAverageTimeOfPresentation();
				totalVisitors += 1; 
			} 
		}
		return (long) (totAvgTime / totalVisitors); 
	}
	
	
	public long getAverageGroupPresentationTime() {
		List<VisitGroup> groups = this.visitGroupService.getAllVisitGroupsWithCompleteInfo();
		int size = 0;  
		long sum = 0; 
		for(VisitGroup vg: groups) {
			if (vg.getAveragePresentationTime() != 0) {
				sum += vg.getAveragePresentationTime();
				size++;
			} 
		}
		return sum / size; 
	}
	
	
	public Float getAveragePresentationRate() {
		List<Visitor> visitors = new ArrayList<>(this.visitorService.getVisitorsWithCompleteInfo()); 
		float totalVisitors = 0; 
        float totAvgRate = 0; 
        for(Visitor v: visitors) {
        	Visit visit = v.getVisit();
        	if (!visit.getPresentations().isEmpty()) {
				totAvgRate += visit.getAverageRateOfPresentation();
				totalVisitors += 1; 
			} 
		}
		return totAvgRate / totalVisitors; 
	}
	
	
	public Float getAverageGroupPresentationRate() {
		List<VisitGroup> groups = this.visitGroupService.getAllVisitGroupsWithCompleteInfo(); 
		int size = 0; 
		float sum = 0; 
		for(VisitGroup vg: groups) {
			if (vg.getAveragePresentationRate() != 0) {
				sum += vg.getAveragePresentationRate();
				size++;
			} 
		}
		return sum / size; 
	}
	
	public Integer getAverageGroupTime() {
		List<VisitGroup> groups = this.visitGroupService.getAllVisitGroupsWithCompleteInfo(); 
		int size = 0; 
		int sum = 0; 
		for(VisitGroup vg: groups) {
			if (vg.getAverageTimeMin() != 0) {
				sum += vg.getAverageTimeMin();
				size++;
			} 
		}
		return (Integer) (sum / size); 
	}
	

	/***
	 * It computes the percent of visitor on a single POI
	 * during a day, i.e. attraction power
	 * @param the name of the POI to compute the attraction power
	 * @return the percent of total visitor on the POI on a 100 scale
	 */
	public Integer getPercentofTotalVisitorOnASinglePOI(String name) {
		List<Visitor> visitors = new ArrayList<>(this.visitorService.getVisitorsWithCompleteInfo()); 
		float totalVisitors = visitors.size(); 
		POI poi = this.positionService.getPOIByName(name); 
		int countVisitorsForPOI = 0; 
		for(Visitor v: visitors) {
			Visit visit = v.getVisit(); 
			if(visit.hasBeenOnPOI(poi)) {
				countVisitorsForPOI += 1; 
			}
		}
		Float percent = (countVisitorsForPOI * 100) / totalVisitors;
		return Math.round(percent); 
	}
	
	
	/**
	 * It computes the average time of the visitors
	 * on a specific POI.
	 * @param name, the name of the POI to compute the average time
	 * @return the average time on the POI in seconds.
	 */
	public Integer getAverageTimeOnASinglePOI(String name) {
		List<Visitor> visitors = this.visitorService.getVisitorsWithCompleteInfo();
		int totalTime = 0;
		int countVisitors = 0;
		POI poi = this.positionService.getPOIByName(name);
		for (Visitor v : visitors) {
			Visit visit = v.getVisit();
			if(visit.hasBeenOnPOI(poi)) {
				totalTime += visit.getSecondsOnPOI(poi); 
				countVisitors += 1; 
			}
		}
		Integer avgTime = 0; 
		if(countVisitors != 0) {
			avgTime = totalTime / countVisitors;
		}		
		return avgTime; 
	}

	/***
	 * It creates a map of the form <hour, <room, numberOfVisitors>> 
	 * where for every hour are computed the number of visitors for every room
	 * @return a map representing the number of visitor on every room per hour
	 */
	public Map<Integer, Map<Room, Integer>> getNumberOfVisitorsPerHourPerRoom() {
		Map<Integer, Map<Room, Integer>> visitorsPerRoomPerHour = new HashMap<>();
		List<Visitor> visitors = this.visitorService.getVisitorsWithCompleteInfo();
		for (Visitor v : visitors) {
			Set<Room> visitedRooms = new HashSet<>();
			Visit visit = v.getVisit();
			List<POIElement> positions = visit.getElementsPOIOrderedByStartTime();
			for (POIElement p : positions) {
				LocalTime startTime = p.getStartTime();
				Room room = p.getPosition().getRoom();
				if (!visitedRooms.contains(room)) {
					if (!visitorsPerRoomPerHour.containsKey(startTime.getHour())) {
						visitorsPerRoomPerHour.put(startTime.getHour(), new HashMap<>());
					}
					Map<Room, Integer> roomToVisitors = visitorsPerRoomPerHour.get(startTime.getHour());
					if (!roomToVisitors.containsKey(room)) {
						roomToVisitors.put(room, new Integer(0));
					}
					Integer value = roomToVisitors.get(room)+1;
					roomToVisitors.put(room, value);
					
					visitedRooms.add(room);
				} 
			}

		}

		return visitorsPerRoomPerHour;
	}

	/**
	 * It computes the number of visitor per hour.
	 * Values are stored in a map of the type <hour, numberOfVisitor>
	 * @return a map representing the number of visitors per hour in the museum
	 */
	public Map<Integer, Integer> getNumberOfVisitorsByHour(){
		Map<Integer, Integer> visitorPerHour = new HashMap<>(); 
		List<Visitor> visitors = this.visitorService.getVisitorsWithCompleteInfo();
		for(Visitor v: visitors) {
			Visit visit = v.getVisit();
			if (!visit.getElementsPOI().isEmpty()) {
				LocalTime startTime = visit.getEntranceTime();
				if (!visitorPerHour.containsKey(visit.getEntranceHour())) {
					visitorPerHour.put(startTime.getHour(), new Integer(0));
				}
				Integer number = visitorPerHour.get(visit.getEntranceHour());
				number += 1;
				visitorPerHour.put(visit.getEntranceHour(), number);
			}
		}
		return visitorPerHour; 
	}
	

}
