package it.uniroma3.atcs.acmemuseum.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.atcs.acmemuseum.graph.VisitGraphFactory;
import it.uniroma3.atcs.acmemuseum.model.MuseumStatistic;
import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.model.Presentation;
import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.service.StatisticsService;
import it.uniroma3.atcs.acmemuseum.service.VisitorService;

@Controller
public class VisitController {
	
	
	@Autowired
	private VisitorService visitorService;
	
	
	@Autowired
	private StatisticsService statisticsService;
	
	
	@RequestMapping(value = "/visitPage", method=RequestMethod.GET)
	public String getVisitPage(Model model) {
		model.addAttribute("visitors", this.visitorService.getAllVisitorsAsList()); 
		model.addAttribute("visitor", new Visitor()); 
		return "searchVisitor.html"; 
	}
	
	
	@RequestMapping(value = "/getVisitorPage", method = RequestMethod.POST)
	public String getVisitorStatistics(Model model, @ModelAttribute("visitor") Visitor vis, HttpServletResponse response) {
		String nextPage = "visitorPage.html";
		Visitor visitor = this.visitorService.getVisitorByNumberWithCompleteInfo(vis.getNumber());
		model.addAttribute("visitor", visitor); 
		
		if(visitor == null) {
			model.addAttribute("visitor", new Visitor()); 
			return "searchVisitor.html";  
		}
		Visit visit = visitor.getVisit(); 
		
		model.addAttribute("number", visitor.getNumber()); 
		
		Long time = visit.getTotalTimeInMin(); 
		model.addAttribute("totalTime", time); 
		
		MuseumStatistic ms = this.statisticsService.getMuseumStatistic(); 
		
		Long avgTime = ms.getAverageVisitTimeInMin(); 
		model.addAttribute("avgTime", avgTime); 
		
		if(avgTime > time) {
			model.addAttribute("message", "The visitor stayed less than average!");
		}
		else {
			model.addAttribute("message", "It seemed the visitor enjoyed the museum, he/she stayed more than average!");
		}

		List<Presentation> presentations = new ArrayList<>(); 
		if(!visit.getPresentations().isEmpty()) {
			 presentations = visit.getPresentationsOrderedByStartTime();
			 float visAvgPresRate = visit.getAverageRateOfPresentation();
			 float avgPresRate = ms.getAveragePresentationRate();
			 model.addAttribute("vAveragePresRate", visAvgPresRate); 
			 model.addAttribute("averagePresRate", avgPresRate); 
			 
			 if(visAvgPresRate > avgPresRate) {
				 model.addAttribute("message1", "The visitor enjoyed presentations more than average!");
			 }
			 else {
				 model.addAttribute("message1", "The visitor rated presentations less than average!");
			 }
			 
			 
			 model.addAttribute("vAveragePresTime", visit.getAverageTimeOfPresentation());
			 model.addAttribute("averagePresTime", ms.getAveragePresentationTime());
			 
			 model.addAttribute("stoppedPresentations", visit.getNumberOfStoppedPresentations()); 
			 model.addAttribute("numberOfPresentations", visit.getPresentations().size()); 
			 
		}
		model.addAttribute("presentations",  presentations); 
		
		return nextPage; 
	}
	
	@RequestMapping(value = "/getPOIGraph/{id}", method=RequestMethod.GET)
	public String getPOIGraph(@PathVariable("id") Long id, Model model) throws IOException {
		Visitor visitor = this.visitorService.getVisitorById(id);
		Visit visit = visitor.getVisit();
		List<POI> pois = visit.getElementsPOIOrderedByStartTime().stream().map(ep -> ep.getPosition())
				.collect(Collectors.toList());
		model.addAttribute("pois", pois); 
		String json = VisitGraphFactory.getInstance().getPositionGraphJson(visit);
		model.addAttribute("json", json);
		return "positionGraph.html";
	}
	
	@RequestMapping(value = "/getRoomGraph/{id}", method=RequestMethod.GET)
	public String getRoomGraph(@PathVariable ("id") Long id, Model model) throws IOException {
		Visitor visitor = this.visitorService.getVisitorById(id); 
		Visit visit = visitor.getVisit(); 
		model.addAttribute("rooms", visit.getVisitedRoomSortedByVisitTime());
		String json =  VisitGraphFactory.getInstance().getRoomGraphJson(visit);
		model.addAttribute("json", json); 
		return "roomGraph.html"; 
	}
	
	
	
	@RequestMapping(value="/getPositionGraphImage/{id}", method=RequestMethod.GET)
	public void getPositionGraphImage(@PathVariable ("id") Long id, Model model, HttpServletResponse response) throws IOException {
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		Visitor visitor = this.visitorService.getVisitorById(id); 
		Visit visit = visitor.getVisit(); 
		byte[] positionGraphImage = VisitGraphFactory.getInstance().getPositionGraphImage(visit);
		response.getOutputStream().write(positionGraphImage);
	    response.getOutputStream().close();
	}
	
	@RequestMapping(value="/getRoomGraphImage/{id}", method=RequestMethod.GET)
	public void getRoomGraphImage(@PathVariable ("id") Long id, Model model, HttpServletResponse response) throws IOException {
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		Visitor visitor = this.visitorService.getVisitorById(id); 
		Visit visit = visitor.getVisit(); 
		byte[] roomGraphImage = VisitGraphFactory.getInstance().getRoomGraphImage(visit);
		response.getOutputStream().write(roomGraphImage);
	    response.getOutputStream().close();
	}
	
	
	
}
