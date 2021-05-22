package it.uniroma3.atcs.acmemuseum.graph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import com.google.gson.Gson;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import it.uniroma3.atcs.acmemuseum.model.POI;
import it.uniroma3.atcs.acmemuseum.model.Room;
import it.uniroma3.atcs.acmemuseum.model.Visit;

public class VisitGraphDecorator {

	private DefaultDirectedGraph <String, Integer> roomGraph;

	private DefaultDirectedGraph <String, Integer> positionGraph;
	
	private Visit visit; 

	public VisitGraphDecorator(Visit v) {
		this.roomGraph = new DefaultDirectedGraph<>(Integer.class);
		this.positionGraph = new DefaultDirectedGraph<>(Integer.class);
        this.visit = v; 
		this.initGraphs(v);
	}

	/* It creates a graph of the visit */
	/* The nodes are POIs and there is an edge from a to b*/
	/* if the visitor went from poi a to b */
	private void initGraphs(Visit visit) {
		List<POI> positionsVisited = visit.getElementsPOIOrderedByStartTime()
				.stream()
				.map(pe -> pe.getPosition())
				.collect(Collectors.toList());
		POI lastPOI = null;
		Room lastRoom = null;
		Integer positionCounter = 0; 
		Integer roomCounter = 0; 
		for (POI poi : positionsVisited) {
			if (!positionGraph.containsVertex(poi.getName())) {
				positionGraph.addVertex(poi.getName());
			}
			if (lastPOI != null) {
				positionGraph.addEdge(lastPOI.getName(), poi.getName(), positionCounter);
				if (!poi.equals(lastPOI)) {
					lastPOI = poi;
					positionCounter++;
				} 
			}
			else {
				lastPOI = poi; 
			}

			Room room = poi.getRoom();
			if (!roomGraph.containsVertex(room.getCode())) {
				roomGraph.addVertex(room.getCode());
			}

			if (lastRoom != null && !lastRoom.equals(room)) {
				roomGraph.addEdge(lastRoom.getCode(), room.getCode(), roomCounter);
				if (!room.equals(lastRoom)) {
					roomCounter++;
					lastRoom = room;
				}
			}
			else {
				lastRoom = room; 
			}

		}
	}
	
	
	public DirectedGraph<String, Integer> getRoomGraph() {
		return roomGraph;
	}

	public DirectedGraph<String, Integer> getPositionGraph() {
		return positionGraph;
	}
	
	public Visit getVisit() {
		return this.visit; 
	}
	
	public byte[] createImageForPositionGraph() throws IOException {
		JGraphXAdapter<String, Integer> graphAdapter = new JGraphXAdapter<String, Integer>(this.positionGraph);
		mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
		layout.execute(graphAdapter.getDefaultParent());
		BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", baos);
		byte[] bytes = baos.toByteArray();
		System.out.println("\n Creating position graph \n"); 
		return bytes; 
	}
	
	public byte[] createImageForRoomGraph() throws IOException {
		JGraphXAdapter<String, Integer> graphAdapter = new JGraphXAdapter<String, Integer>(this.roomGraph);
		mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
		layout.execute(graphAdapter.getDefaultParent());
		BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", baos);
		byte[] bytes = baos.toByteArray();
		System.out.println("\n Creating room graph \n"); 
		System.out.println(this.roomGraph.toString()); 
		return bytes; 
	}
	
	public class Node{
		private String id; 
		public Node(String name) {
			this.id = name; 
		}
		public String getId() {
			return this.id; 
		}
	}
	
	public class Edge{
		private String source; 
		private String target; 
		private Integer weight; 
		public Edge(String from, String to, Integer weight) {
			this.source = from; 
			this.target = to; 
			this.weight = weight; 
		}
		public String getSource() {
			return source;
		}
		public String getTarget() {
			return target;
		}
		public Integer getWeight() {
			return weight;
		}
	}
	
	public class Graph{
		private Set<Node> nodes; 
		private Set<Edge> links; 
		public Graph() {}
		
		public Set<Node> getNodes() {
			return nodes;
		}
		public void setNodes(Set<Node> nodes) {
			this.nodes = nodes;
		}
		public Set<Edge> getLinks() {
			return links;
		}
		public void setEdges(Set<Edge> links) {
			this.links = links;
		}
		
		public void addNode(String name) {
			this.nodes.add(new Node(name)); 
		}
		
		public void addEdge(String source, String target, Integer weight) {
			this.links.add(new Edge(source, target, weight)); 
		}
	}
	
	public String exportPositionGraphToJsonFormat() {
        Gson gson = new Gson();

		Set<String> vertexes = this.positionGraph.vertexSet();
		Set<Node> nodes = new HashSet<>(); 
		for(String v: vertexes) {
			nodes.add(new Node(v));
		}
		
		Set<Edge> edges = new HashSet<>(); 
		for(String v1: vertexes) {
			for(String v2: vertexes) {
				if(this.positionGraph.containsEdge(v1, v2)) {
					Integer weight = this.positionGraph.getEdge(v1, v2); 
					edges.add(new Edge(v1,v2,weight)); 
					
				}
			}
		}
		
		Graph g = new Graph();
		g.setNodes(nodes);
		g.setEdges(edges);
        
		String jsonString = gson.toJson(g); 
		return jsonString;
	}
	
	
	public String exportRoomGraphToJsonFormat() {
        Gson gson = new Gson();

		Set<String> vertexes = this.roomGraph.vertexSet();
		Set<Node> nodes = new HashSet<>(); 
		for(String v: vertexes) {
			nodes.add(new Node(v));
		}
		
		Set<Edge> edges = new HashSet<>(); 
		for(String v1: vertexes) {
			for(String v2: vertexes) {
				if(this.roomGraph.containsEdge(v1, v2)) {
					Integer weight = this.roomGraph.getEdge(v1, v2); 
					edges.add(new Edge(v1,v2,weight)); 
					
				}
			}
		}
		
		Graph g = new Graph();
		g.setNodes(nodes);
		g.setEdges(edges);
        
		String jsonString = gson.toJson(g); 
		return jsonString;
	}
	
	

}
