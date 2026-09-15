package dk.medcom.video.api.helper;

import dk.medcom.video.api.dao.entity.DirectMedia;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.dao.entity.ProvisionStatus;

import java.util.Date;
import java.util.UUID;

public class TestDataHelper {
    public static SchedulingInfo createSchedulingInfo(String organisationCode) {
        SchedulingInfo schedulingInfo = new SchedulingInfo();
        schedulingInfo.setProvisionStatusDescription("provisions status description");
        schedulingInfo.setProvisionStatus(ProvisionStatus.PROVISIONED_OK);
        schedulingInfo.setProvisionVMRId("vmr id");
        schedulingInfo.setOrganisationCode(organisationCode);
        schedulingInfo.setvMRStartTime(new Date());
        schedulingInfo.setUuid(UUID.randomUUID().toString());
        schedulingInfo.setVMRAvailableBefore(10);
        schedulingInfo.setEndMeetingOnEndTime(true);
        schedulingInfo.setMaxParticipants(20);
        schedulingInfo.setMeetingUser(createMeetingUser(organisationCode));
        schedulingInfo.setPortalLink("portal link");
        schedulingInfo.setUpdatedByUser(createMeetingUser(organisationCode));
        schedulingInfo.setCreatedTime(new Date());
        schedulingInfo.setGuestPin(4324L);
        schedulingInfo.setHostPin(473892L);
        schedulingInfo.setIvrTheme("ivr theme");
        schedulingInfo.setProvisionTimestamp(new Date());
        schedulingInfo.setUpdatedTime(new Date());
        schedulingInfo.setSchedulingTemplate(createSchedulingTemplate());
        schedulingInfo.setUriWithDomain("uri with domain");
        schedulingInfo.setUriWithoutDomain("uri without domain");
        schedulingInfo.setDirectMedia(DirectMedia.never);

        return schedulingInfo;
    }

    public static MeetingUser createMeetingUser(String organisationCode) {
        MeetingUser meetingUser = new MeetingUser();
        meetingUser.setOrganisationCode(organisationCode);
        meetingUser.setEmail("jpe@kvalitetsit.dk");
        meetingUser.setId(2L);

        return meetingUser;
    }

    private static SchedulingTemplate createSchedulingTemplate() {
        return new SchedulingTemplate();
    }
}
