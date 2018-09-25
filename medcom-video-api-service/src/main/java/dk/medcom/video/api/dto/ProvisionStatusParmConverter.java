package dk.medcom.video.api.dto;

import java.beans.PropertyEditorSupport;

public class ProvisionStatusParmConverter extends PropertyEditorSupport{

	 public void setAsText(final String stringStatus) throws IllegalArgumentException {
		 int status = Integer.parseInt(stringStatus);
	     setValue(ProvisionStatus.getProvisionStatus(status) );
	    }

}