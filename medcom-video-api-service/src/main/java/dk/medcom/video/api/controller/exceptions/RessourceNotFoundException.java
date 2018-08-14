package dk.medcom.video.api.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RessourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private String ressource;
	
	private String field;
	
	public RessourceNotFoundException(String ressource, String field) {
		this.ressource = ressource;
		this.field = field;
	}

	public String getRessource() {
		return ressource;
	}

	public void setRessource(String ressource) {
		this.ressource = ressource;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}
