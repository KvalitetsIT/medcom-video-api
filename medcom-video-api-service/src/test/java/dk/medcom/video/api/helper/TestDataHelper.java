package dk.medcom.video.api.helper;

import dk.medcom.video.api.dao.entity.DirectMedia;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.dao.entity.ProvisionStatus;

import java.util.Date;
import java.util.UUID;

public class TestDataHelper {
    public static Organisation createOrganisation(boolean poolEnabled, String orgId, long id)  {
        Organisation organisation = new Organisation();
        organisation.setId(id);
        organisation.setName("this is a name");
        organisation.setOrganisationId(orgId);
        organisation.setPoolSize(poolEnabled ? 10 : null);

        return organisation;
    }

    public static SchedulingInfo createSchedulingInfo(Organisation organization) {
        SchedulingInfo schedulingInfo = new SchedulingInfo();
        schedulingInfo.setProvisionStatusDescription("provisions status description");
        schedulingInfo.setProvisionStatus(ProvisionStatus.PROVISIONED_OK);
        schedulingInfo.setProvisionVMRId("vmr id");
        schedulingInfo.setOrganisation(organization);
        schedulingInfo.setvMRStartTime(new Date());
        schedulingInfo.setUuid(UUID.randomUUID().toString());
        schedulingInfo.setVMRAvailableBefore(10);
        schedulingInfo.setEndMeetingOnEndTime(true);
        schedulingInfo.setMaxParticipants(20);
        schedulingInfo.setMeetingUser(createMeetingUser(organization));
        schedulingInfo.setPortalLink("portal link");
        schedulingInfo.setUpdatedByUser(createMeetingUser(organization));
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

    public static MeetingUser createMeetingUser(Organisation org) {
        MeetingUser meetingUser = new MeetingUser();
        meetingUser.setOrganisation(org);
        meetingUser.setEmail("jpe@kvalitetsit.dk");
        meetingUser.setId(2L);

        return meetingUser;
    }

    private static SchedulingTemplate createSchedulingTemplate() {
        return new SchedulingTemplate();
    }
}
