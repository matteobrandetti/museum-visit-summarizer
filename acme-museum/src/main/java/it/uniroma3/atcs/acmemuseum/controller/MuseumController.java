package it.uniroma3.atcs.acmemuseum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MuseumController {
	
	
	@RequestMapping(value = "/",method=RequestMethod.GET)
	public String getIndex() {
		return "index.html"; 
	}
	

}
