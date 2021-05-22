package it.uniroma3.atcs.acmemuseum.graph;

import java.io.IOException;

import it.uniroma3.atcs.acmemuseum.model.Visit;

public class VisitGraphFactory {
	
	private static VisitGraphFactory instance; 
	
	private VisitGraphFactory() {
	} 
	
	public static VisitGraphFactory getInstance() {
		if(instance == null) {
			instance = new VisitGraphFactory(); 
		}
		return instance; 
	}
	
	public byte[] getPositionGraphImage(Visit visit) throws IOException {
		return new VisitGraphDecorator(visit).createImageForPositionGraph(); 
	}
	
	public byte[] getRoomGraphImage(Visit visit) throws IOException {
		return new VisitGraphDecorator(visit).createImageForRoomGraph(); 
	}
	
	public String getPositionGraphJson(Visit visit) throws IOException {
		return new VisitGraphDecorator(visit).exportPositionGraphToJsonFormat(); 
	}
	
	public String getRoomGraphJson(Visit visit) throws IOException {
		return new VisitGraphDecorator(visit).exportRoomGraphToJsonFormat(); 
	}
}
