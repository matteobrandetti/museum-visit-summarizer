package it.uniroma3.atcs.acmemuseum.graph;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;


import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.model.POIElement;
import it.uniroma3.atcs.acmemuseum.model.Room;
import it.uniroma3.atcs.acmemuseum.model.Visit;

class TestVisitGraph {
	
	public void createGraph() throws IOException {

	    File imgFile = new File("src/main/resources/static/graph/graph.png");
	    imgFile.createNewFile();
	    	    
	    Room room1 = new Room("Room1"); 
	    room1.setId(new Long(1));
	    POI p1 = new POI("p1");
		room1.addPOI(p1);
	    POI p2 = new POI("p2");
		room1.addPOI(p2);
		POI p3 = new POI("p3"); 
	    room1.addPOI(p3);

	    Room room2 = new Room("Room2"); 
	    room2.setId(new Long(2));
	    POI p4 = new POI("p4");
		room2.addPOI(p4);
	    POI p5 = new POI("p5");
		room2.addPOI(p5);
		POI p6 = new POI("p6"); 
	    room2.addPOI(p6);
	    
	    List<POIElement> pes = new LinkedList<>(); 
	    
	    POIElement pe1 = new POIElement();
	    pe1.setPosition(p1);
	    pe1.setStartTime(LocalTime.now());
	    pes.add(pe1); 
	    
	    POIElement pe2 = new POIElement();
	    pe2.setPosition(p2);
	    pe2.setStartTime(LocalTime.now());
	    pes.add(pe2); 

	    POIElement pe3 = new POIElement();
	    pe3.setPosition(p3);
	    pe3.setStartTime(LocalTime.now());
	    pes.add(pe3); 
    
	    POIElement pe4 = new POIElement();
	    pe4.setPosition(p1);
	    pe4.setStartTime(LocalTime.now());
	    pes.add(pe4); 

	    POIElement pe5 = new POIElement();
	    pe5.setPosition(p4);
	    pe5.setStartTime(LocalTime.now());
	    pes.add(pe5); 

	    POIElement pe6 = new POIElement();
	    pe6.setPosition(p5);
	    pe6.setStartTime(LocalTime.now());
	    pes.add(pe6); 
	    
	    POIElement pe7 = new POIElement();
	    pe7.setPosition(p4);
	    pe7.setStartTime(LocalTime.now());
	    pes.add(pe7); 
	    
	    POIElement pe8 = new POIElement();
	    pe8.setPosition(p6);
	    pe8.setStartTime(LocalTime.now());
	    pes.add(pe8); 
	    
	    POIElement pe9 = new POIElement();
	    pe9.setPosition(p2);
	    pe9.setStartTime(LocalTime.now());
	    pes.add(pe9); 

	    Visit v = new Visit(); 
	    v.setElementsPOI(pes);

        new VisitGraphDecorator(v); 
	   
	}
	

	

}
