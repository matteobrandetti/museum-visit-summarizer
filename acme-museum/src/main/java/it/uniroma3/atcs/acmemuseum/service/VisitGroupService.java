package it.uniroma3.atcs.acmemuseum.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.atcs.acmemuseum.model.VisitGroup;
import it.uniroma3.atcs.acmemuseum.model.Visitor;
import it.uniroma3.atcs.acmemuseum.repository.VisitGroupRepository;

@Service
@Transactional
public class VisitGroupService {
	
	@Autowired
	private VisitGroupRepository repository;
	
	@Autowired
	private VisitorService visitorService; 
	
	private List<VisitGroup> visitGroups; 
	
	private List<VisitGroup> visitGroupsWithCompleteInfo; 
	
	
	public List<VisitGroup> getAllVisitGroupsAsList(){
		if(this.visitGroups == null) {
			this.visitGroups = this.repository.findAll().stream().collect(Collectors.toList()); 
		}
		return this.visitGroups;   
	}
	
	public List<VisitGroup> getAllVisitGroupsWithCompleteInfo() {
		if (this.visitGroupsWithCompleteInfo == null) {
			List<Visitor> visitors = this.visitorService.getVisitorsWithCompleteInfo(); 
			Map<Long, Visitor> map = new HashMap<>(); 
			for(Visitor v: visitors) {
				map.put(v.getId(), v); 
			}
			List<VisitGroup> groups = this.getAllVisitGroupsAsList(); 
			for(VisitGroup vp: groups) {
				List<Visitor> actualVisitors = new ArrayList<>(); 
				for(Visitor v: vp.getVisitors()) {
					Visitor actualVisitor = map.get(v.getId()); 
					actualVisitors.add(actualVisitor); 
				}
				vp.setVisitors(actualVisitors);
			}
			this.visitGroupsWithCompleteInfo = groups; 
			
		}
		return this.visitGroupsWithCompleteInfo;  
	}
	
	public VisitGroup getVisitGroupByNumberWithCompleteInfos(Integer number) {
		VisitGroup vg = this.getVisitGroupByNumber(number);
		List<Visitor> visitors = new ArrayList<>(); 
		for(Visitor v: vg.getVisitors()) {
			visitors.add(this.visitorService.getVisitorByNumberWithCompleteInfo(v.getNumber())); 
		}
		vg.setVisitors(visitors);
		return vg; 
	}
	
	public void add(VisitGroup visitGroup) {
		this.repository.save(visitGroup);
	}
	
	public VisitGroup getVisitGroupByNumber(Integer number) {
		return this.repository.findByNumber(number);
	}
	
	public VisitGroup getVisitGroupById(Long id) {
		return this.repository.findById(id).orElse(null);
	}
	
	public Collection<VisitGroup> getAllVisitGroups(){
		return this.repository.findAll(); 
	}
	
	public void addAll(Collection<VisitGroup> values) {
		values.stream().forEach(vg -> this.repository.save(vg));	
	}
	
	
	

}
