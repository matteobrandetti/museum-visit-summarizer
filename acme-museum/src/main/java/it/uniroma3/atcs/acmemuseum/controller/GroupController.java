package it.uniroma3.atcs.acmemuseum.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.atcs.acmemuseum.model.MuseumStatistic;
import it.uniroma3.atcs.acmemuseum.model.Presentation;
import it.uniroma3.atcs.acmemuseum.model.Visit;
import it.uniroma3.atcs.acmemuseum.model.VisitGroup;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.service.StatisticsService;
import it.uniroma3.atcs.acmemuseum.service.VisitGroupService;
import it.uniroma3.atcs.acmemuseum.service.VisitorService;

@Controller
public class GroupController {

	@Autowired
	private VisitGroupService groupService;
	
	@Autowired
	private VisitorService visitorService;
	
	@Autowired
	private StatisticsService statisticsService;
	
	
	@RequestMapping(value = "/groupsPage", method=RequestMethod.GET)
	public String getGroupPage(Model model) {
		model.addAttribute("groups", this.groupService.getAllVisitGroupsAsList()); 
		model.addAttribute("group", new VisitGroup()); 
		return "searchGroup.html"; 
	}
	
	
	@RequestMapping(value = "/getGroupPage", method = RequestMethod.POST)
	public String getGroupStatistics(Model model, @ModelAttribute("group") VisitGroup group, HttpServletResponse response) {
		String nextPage = "groupPage.html";
		VisitGroup g = this.groupService.getVisitGroupByNumberWithCompleteInfos(group.getNumber()); 
		model.addAttribute("group", g); 
		
		if(g == null) {
			model.addAttribute("group", new VisitGroup()); 
			return "searchGroup.html"; 
		}
		
		model.addAttribute("number", group.getNumber()); 

				
		Integer gAvgTime = g.getAverageTimeMin(); 
		model.addAttribute("gAvgTime", gAvgTime); 
		
		MuseumStatistic ms = this.statisticsService.getMuseumStatistic(); 
		
	    Integer avgTime = ms.getAverageVisitGroupTimeInMin(); 
		model.addAttribute("avgTime", avgTime); 
		
		if(avgTime > gAvgTime) {
			model.addAttribute("message", "The group stayed less than average!");
		}
		else {
			model.addAttribute("message", "It seems that the group enjoyed the museum, they stayed more than average!");
		}

		float gAvgPresRate = g.getAveragePresentationRate(); 
		float avgPresRate = ms.getAverageGroupPresentationRate(); 
		model.addAttribute("gAveragePresRate", gAvgPresRate);
		model.addAttribute("averagePresRate", avgPresRate);

		if (gAvgPresRate > avgPresRate) {
			model.addAttribute("message1", "The group enjoyed presentations more than average!");
		} else {
			model.addAttribute("message1", "The grop rated presentations less than average!");
		}

		model.addAttribute("gAveragePresTime", g.getAveragePresentationTime());
		model.addAttribute("averagePresTime", ms.getAverageGroupPresentationRate()); 

		return nextPage;
	}
	
	
	
	@RequestMapping(value="/getVisitor/{number}", method=RequestMethod.GET)
	public String getVisitor(@PathVariable ("number") Integer number, Model model) {
		String nextPage = "visitorPage.html";
		Visitor visitor = this.visitorService.getVisitorByNumberWithCompleteInfo(number);
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
}
