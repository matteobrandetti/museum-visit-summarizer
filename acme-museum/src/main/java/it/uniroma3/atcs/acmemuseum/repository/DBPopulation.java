package it.uniroma3.atcs.acmemuseum.repository;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.github.cliftonlabs.json_simple.JsonException;

import it.uniroma3.atcs.acmemuseum.driver.JsonDriver;
import it.uniroma3.atcs.acmemuseum.service.StatisticsService;

@Component
public class DBPopulation implements ApplicationRunner{
	
	@Autowired
	private StatisticsService ss; 
	
	@Autowired
	private JsonDriver jd; 

	@Override
	public void run(ApplicationArguments args) throws Exception {
		readAndSavePositions(); 
		readAndSaveVisitorsFromLog(); 
		this.ss.computeAndSaveMuseumStatistics();
	}
	
	private void readAndSaveVisitorsFromLog() throws IOException, JsonException {
		String workDir = System.getProperty("user.dir");
		String path = "/src/main/resources/static/data/";
		this.jd.saveVisitorsFromFileToPersistence(workDir + path, "data.json");
	}
	
	private void readAndSavePositions() throws IOException, JsonException {	
		String workDir = System.getProperty("user.dir");
		String path = "/src/main/resources/static/data/";
		this.jd.saveMuseumFromFileToPersistence(workDir + path, "museum.json");
	}

}
