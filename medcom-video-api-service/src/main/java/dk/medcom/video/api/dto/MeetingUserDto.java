package dk.medcom.video.api.dto;

import dk.medcom.video.api.dao.MeetingUser;

public class MeetingUserDto {

	public String organisationId;
	public String email;

	public MeetingUserDto() {
		// Empty constructor
	}

	public MeetingUserDto(MeetingUser meetingUser) {
		if (meetingUser == null) {
			return;
		}
		organisationId = meetingUser.getOrganisation().getOrganisationId();
		email = meetingUser.getEmail();
	}
		

	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
