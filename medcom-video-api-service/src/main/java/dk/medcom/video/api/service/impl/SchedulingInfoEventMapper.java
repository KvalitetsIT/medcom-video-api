package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.domain.MessageType;
import dk.medcom.video.api.service.domain.SchedulingInfoEvent;

public class SchedulingInfoEventMapper {
    public static SchedulingInfoEvent map(SchedulingInfo schedulingInfo, MessageType messageType) {
        var schedulingInfoEvent = new SchedulingInfoEvent();

        schedulingInfoEvent.setMessageType(messageType);

        schedulingInfoEvent.setSchedulingInfoIdentifier(schedulingInfo.getId());

        schedulingInfoEvent.setUuid(schedulingInfo.getUuid());
        schedulingInfoEvent.setHostPin(schedulingInfo.getHostPin());
        schedulingInfoEvent.setGuestPin(schedulingInfo.getGuestPin());
        schedulingInfoEvent.setVMRAvailableBefore(schedulingInfo.getVMRAvailableBefore());

        schedulingInfoEvent.setIvrTheme(schedulingInfo.getIvrTheme());
        schedulingInfoEvent.setUriWithoutDomain(schedulingInfo.getUriWithoutDomain());
        schedulingInfoEvent.setUriDomain(schedulingInfo.getUriDomain());
        schedulingInfoEvent.setUriWithDomain(schedulingInfo.getUriWithDomain());
        schedulingInfoEvent.setOrganisationCode(schedulingInfo.getOrganisation().getOrganisationId());
        schedulingInfoEvent.setPortalLink(schedulingInfo.getPortalLink());
        schedulingInfoEvent.setMaxParticipants(schedulingInfo.getMaxParticipants());
        schedulingInfoEvent.setEndMeetingOnEndTime(schedulingInfo.getEndMeetingOnEndTime());
        if(schedulingInfo.getVmrType() != null) {
            schedulingInfoEvent.setVmrType(schedulingInfo.getVmrType().toString());
        }
        if(schedulingInfo.getHostView() != null) {
            schedulingInfoEvent.setHostView(schedulingInfo.getHostView().toString());
        }
        if(schedulingInfo.getGuestView() != null) {
            schedulingInfoEvent.setGuestView(schedulingInfo.getGuestView().toString());
        }
        if(schedulingInfo.getVmrQuality() != null) {
            schedulingInfoEvent.setVmrQuality(schedulingInfo.getVmrQuality().toString());
        }
        schedulingInfoEvent.setEnableOverlayText(schedulingInfo.getEnableOverlayText());
        schedulingInfoEvent.setGuestsCanPresent(schedulingInfo.getGuestsCanPresent());
        schedulingInfoEvent.setForcePresenterIntoMain(schedulingInfo.getForcePresenterIntoMain());
        schedulingInfoEvent.setForceEncryption(schedulingInfo.getForceEncryption());
        schedulingInfoEvent.setMuteAllGuests(schedulingInfo.getMuteAllGuests());
        schedulingInfoEvent.setMeetingUser(schedulingInfo.getMeetingUser().getEmail());
        if(schedulingInfo.getCreatedTime() != null) {
            schedulingInfoEvent.setCreatedTime(schedulingInfo.getCreatedTime().toInstant());
        }
        schedulingInfoEvent.setCustomPortalGuest(schedulingInfo.getCustomPortalGuest());
        schedulingInfoEvent.setCustomPortalHost(schedulingInfo.getCustomPortalHost());
        schedulingInfoEvent.setReturnUrl(schedulingInfo.getReturnUrl());
        if(schedulingInfo.getMeeting() != null) {
            schedulingInfoEvent.setMeetingEndTime(schedulingInfo.getMeeting().getEndTime().toInstant());
        }
        if(schedulingInfo.getvMRStartTime() != null) {
            schedulingInfoEvent.setvMRStartTime(schedulingInfo.getvMRStartTime().toInstant());
        }
        schedulingInfoEvent.setDirectMedia(schedulingInfo.getDirectMedia().toString());

        return schedulingInfoEvent;
    }
}
