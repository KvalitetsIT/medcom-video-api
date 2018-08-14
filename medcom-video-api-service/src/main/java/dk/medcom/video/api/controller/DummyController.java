package dk.medcom.video.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.dto.DummyDto;

@RestController
public class DummyController {

	@RequestMapping(value = "/dummies")
	public DummyDto[] getDummies() {
		
		DummyDto dummy1 = new DummyDto("Hej");
		DummyDto dummy2 = new DummyDto("Farvel");
		return new DummyDto[] { dummy1, dummy2 };
		
	}
}
