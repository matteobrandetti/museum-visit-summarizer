package it.uniroma3.atcs.acmemuseum.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.cliftonlabs.json_simple.JsonException;

import it.uniroma3.atcs.acmemuseum.driver.JsonDriver;
import it.uniroma3.atcs.acmemuseum.model.DayHour;
import it.uniroma3.atcs.acmemuseum.model.MuseumStatistic;
import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.service.POIService;
import it.uniroma3.atcs.acmemuseum.service.StatisticsService;

@Controller
public class StatisticsController {
	
	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private POIService poiService;
	
	@Autowired
	private JsonDriver jd; 
	

	@RequestMapping(value = "/statisticsPage", method = RequestMethod.GET)
	public String getStatisticsPage(Model model) {
		String nextPage = "statistics.html";
		model.addAttribute("pois", this.poiService.getAllPOIs()); 
		model.addAttribute("poi", new POI()); 
		return nextPage;
	}
	
	
	@RequestMapping(value = "/numberOfVisitorsStatisticsPage", method = RequestMethod.GET)
	public String getNumberOfVisitorsPerRoomPerHourPage(Model model) {
		String nextPage = "showMuseumStatistics.html";
		
		MuseumStatistic ms = this.statisticsService.getMuseumStatistic(); 
		List<DayHour> hours = ms.getHours().stream().filter(h -> h.getNumberOfVisitors() != 0).collect(Collectors.toList()); 
		model.addAttribute("hours", hours); 
		
		long avgVisitTime = ms.getAverageVisitTimeInMin(); 
		model.addAttribute("avgVisitTime", avgVisitTime); 
		
		long avgGroupVisitTime = ms.getAverageVisitGroupTimeInMin(); 
		model.addAttribute("avgGroupVisitTime", avgGroupVisitTime); 

		
		return nextPage;
	}
	
	
	
	@RequestMapping(value = "/getPOIStatistics", method = RequestMethod.POST)
	public String getPOIStatistics(Model model, @ModelAttribute("poi") POI poi) {
		String nextPage = "poiStatistics.html";
		
		if(!this.poiService.containsPOIByName(poi.getName())){
			nextPage = "statistics.html";
		}
		else {
			Integer avgTimeOnPOI = this.statisticsService.getAverageTimeOnASinglePOI(poi.getName()); 
			Integer percentOfVisitorsOnAPOI = this.statisticsService.getPercentofTotalVisitorOnASinglePOI(poi.getName());
			model.addAttribute("poi", poi); 
			model.addAttribute("avgTimeOnPOI", avgTimeOnPOI); 
			model.addAttribute("percentOfVisitorsOnAPOI", percentOfVisitorsOnAPOI); 
		}
		return nextPage; 
	}
	
	@RequestMapping(value = "/getUploadPage", method = RequestMethod.GET)
	public String getUploadPage(Model model) {
		String nextPage = "jsonUpload.html";
		return nextPage;
	}
	
	@RequestMapping(value = "/uploadJsonLog", method = RequestMethod.POST)
	public String handleLogUpload(Model model, @RequestParam("file") MultipartFile file) {
		String nextPage = "statistics.html";
		try {
			this.statisticsService.resetMuseumStatistic();
			this.jd.saveVisitorsFromStreamToPersistence(file.getBytes());
			this.statisticsService.computeAndSaveMuseumStatistics();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonException e) {
			System.out.println("Json Format Error");
			e.printStackTrace();
		}
		model.addAttribute("pois", this.poiService.getAllPOIs()); 
		model.addAttribute("poi", new POI()); 
		
		return nextPage; 
	}
	



}
