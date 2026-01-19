package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.service.model.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HelperMethods {
    private static long count = 0;

    public static PoolInfoDto randomPoolInfo() throws PermissionDeniedException {
        var poolInfo = new PoolInfoDto();
        poolInfo.setAvailablePoolSize((int) count++);
        poolInfo.setDesiredPoolSize((int) count++);
        poolInfo.setOrganizationId(randomString());
        poolInfo.setSchedulingInfoList(List.of(randomSchedulingInfo(), randomSchedulingInfo()));
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto(randomSchedulingTemplate()));

        return poolInfo;
    }

    public static Meeting randomMeeting() {
        var meeting = new Meeting();
        meeting.setId(count++);
        meeting.setUuid(randomString());
        meeting.setSubject(randomString());
        meeting.setOrganisation(randomOrganisation());
        meeting.setMeetingUser(randomMeetingUser());
        meeting.setCreatedTime(Date.from(Instant.now().minusSeconds(count++)));
        meeting.setUpdatedByUser(randomMeetingUser());
        meeting.setUpdatedTime(Date.from(Instant.now().minusSeconds(count++)));
        meeting.setOrganizedByUser(randomMeetingUser());
        meeting.setStartTime(Date.from(Instant.now().minusSeconds(count++)));
        meeting.setEndTime(Date.from(Instant.now().minusSeconds(count++)));
        meeting.setDescription(randomString());
        meeting.setProjectCode(randomString());
        meeting.setShortId(randomString());
        meeting.setExternalId(randomString());
        meeting.setMeetingLabels(Set.of(randomMeetingLabel(), randomMeetingLabel()));
        meeting.setGuestMicrophone(GuestMicrophone.muted);
        meeting.setGuestPinRequired(randomBoolean());
        meeting.setMeetingAdditionalInfo(Set.of(randomAdditionalInfo(), randomAdditionalInfo()));

        return meeting;
    }

    public static SchedulingInfo randomSchedulingInfo() {
        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setId(count++);
        schedulingInfo.setUuid(randomString());
        schedulingInfo.setHostPin(count++);
        schedulingInfo.setGuestPin(count++);
        schedulingInfo.setVMRAvailableBefore((int) count++);
        schedulingInfo.setvMRStartTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingInfo.setMaxParticipants((int) count++);
        schedulingInfo.setEndMeetingOnEndTime(randomBoolean());
        schedulingInfo.setUriWithDomain(randomString());
        schedulingInfo.setUriWithoutDomain(randomString());
        schedulingInfo.setPoolOverflow(randomBoolean());
        schedulingInfo.setPool(randomBoolean());
        schedulingInfo.setVmrType(VmrType.lecture);
        schedulingInfo.setHostView(ViewType.one_main_twentyone_pips);
        schedulingInfo.setGuestView(ViewType.five_mains_seven_pips);
        schedulingInfo.setVmrQuality(VmrQuality.hd);
        schedulingInfo.setEnableOverlayText(randomBoolean());
        schedulingInfo.setGuestsCanPresent(randomBoolean());
        schedulingInfo.setForcePresenterIntoMain(randomBoolean());
        schedulingInfo.setForceEncryption(randomBoolean());
        schedulingInfo.setMuteAllGuests(randomBoolean());
        schedulingInfo.setSchedulingTemplate(randomSchedulingTemplate());
        schedulingInfo.setProvisionStatus(ProvisionStatus.PROVISIONED_OK);
        schedulingInfo.setProvisionStatusDescription(randomString());
        schedulingInfo.setProvisionTimestamp(Date.from(Instant.now().minusSeconds(count++)));
        schedulingInfo.setOrganisation(randomOrganisation());
        schedulingInfo.setMeeting(randomMeeting());
        schedulingInfo.setPortalLink(randomString());
        schedulingInfo.setIvrTheme(randomString());
        schedulingInfo.setMeetingUser(randomMeetingUser());
        schedulingInfo.setCreatedTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingInfo.setUpdatedByUser(randomMeetingUser());
        schedulingInfo.setUpdatedTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingInfo.setReservationId(randomString());
        schedulingInfo.setUriDomain(randomString());
        schedulingInfo.setCustomPortalGuest(randomString());
        schedulingInfo.setCustomPortalHost(randomString());
        schedulingInfo.setReturnUrl(randomString());
        schedulingInfo.setDirectMedia(DirectMedia.best_effort);
        schedulingInfo.setNewProvisioner(randomBoolean());

        return schedulingInfo;
    }

    public static SchedulingTemplate randomSchedulingTemplate() {
        var schedulingTemplate = new SchedulingTemplate();
        schedulingTemplate.setId(count++);
        schedulingTemplate.setOrganisation(randomOrganisation());
        schedulingTemplate.setConferencingSysId(count++);
        schedulingTemplate.setUriPrefix(randomString());
        schedulingTemplate.setUriDomain(randomString());
        schedulingTemplate.setHostPinRequired(randomBoolean());
        schedulingTemplate.setHostPinRangeLow(count++);
        schedulingTemplate.setHostPinRangeHigh(count++);
        schedulingTemplate.setGuestPinRequired(randomBoolean());
        schedulingTemplate.setGuestPinRangeLow(count++);
        schedulingTemplate.setGuestPinRangeHigh(count++);
        schedulingTemplate.setVMRAvailableBefore((int) count++);
        schedulingTemplate.setMaxParticipants((int) count++);
        schedulingTemplate.setEndMeetingOnEndTime(randomBoolean());
        schedulingTemplate.setUriNumberRangeLow(count++);
        schedulingTemplate.setUriNumberRangeHigh(count++);
        schedulingTemplate.setIvrTheme(randomString());
        schedulingTemplate.setIsDefaultTemplate(randomBoolean());
        schedulingTemplate.setIsPoolTemplate(randomBoolean());
        schedulingTemplate.setVmrType(VmrType.lecture);
        schedulingTemplate.setHostView(ViewType.one_main_twentyone_pips);
        schedulingTemplate.setGuestView(ViewType.five_mains_seven_pips);
        schedulingTemplate.setVmrQuality(VmrQuality.hd);
        schedulingTemplate.setEnableOverlayText(randomBoolean());
        schedulingTemplate.setGuestsCanPresent(randomBoolean());
        schedulingTemplate.setForcePresenterIntoMain(randomBoolean());
        schedulingTemplate.setForceEncryption(randomBoolean());
        schedulingTemplate.setMuteAllGuests(randomBoolean());
        schedulingTemplate.setDirectMedia(DirectMedia.best_effort);
        schedulingTemplate.setCreatedBy(randomMeetingUser());
        schedulingTemplate.setCreatedTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingTemplate.setUpdatedBy(randomMeetingUser());
        schedulingTemplate.setUpdatedTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingTemplate.setDeletedBy(randomMeetingUser());
        schedulingTemplate.setDeletedTime(Date.from(Instant.now().minusSeconds(count++)));
        schedulingTemplate.setCustomPortalGuest(randomString());
        schedulingTemplate.setCustomPortalHost(randomString());
        schedulingTemplate.setReturnUrl(randomString());

        return schedulingTemplate;
    }

    public static SchedulingTemplateRequestModel randomSchedulingTemplateRequestModel() {
        return new SchedulingTemplateRequestModel(count++,
                randomString(),
                randomString(),
                randomBoolean(),
                count++,
                count++,
                randomBoolean(),
                count++,
                count++,
                (int) count++,
                (int) count++,
                randomBoolean(),
                count++,
                count++,
                randomString(),
                randomBoolean(),
                randomBoolean(),
                randomString(),
                randomString(),
                randomString(),
                VmrTypeModel.conference,
                ViewTypeModel.nine_mains_zero_pips,
                ViewTypeModel.sixteen_mains_zero_pips,
                VmrQualityModel.sd,
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                DirectMediaModel.never);
    }

    public static CreateMeetingModel randomCreateMeetingModel() {
        return new CreateMeetingModel(randomString(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                randomString(),
                randomString(),
                UUID.randomUUID(),
                randomString(),
                (int) count++,
                randomBoolean(),
                count++,
                MeetingTypeModel.POOL,
                UUID.randomUUID(),
                List.of(randomString(), randomString()),
                randomString(),
                GuestMicrophoneModel.muted,
                randomBoolean(),
                VmrTypeModel.lecture,
                ViewTypeModel.five_mains_seven_pips,
                ViewTypeModel.one_main_seven_pips,
                VmrQualityModel.sd,
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomString(),
                (int) count++,
                (int) count++,
                List.of(randomAdditionalInfoModel(), randomAdditionalInfoModel()));
    }

    public static UpdateMeetingModel randomUpdateMeetingModel() {
        return new UpdateMeetingModel(randomString(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                randomString(),
                randomString(),
                randomString(),
                List.of(randomString(), randomString()),
                List.of(randomAdditionalInfoModel(), randomAdditionalInfoModel()));
    }

    public static PatchMeetingModel randomPatchMeetingModel() {
        return new PatchMeetingModel(randomString(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                randomString(),
                randomString(),
                randomString(),
                List.of(randomString(), randomString()),
                GuestMicrophoneModel.off,
                randomBoolean(),
                (int) count++,
                (int) count++,
                List.of(randomAdditionalInfoModel(), randomAdditionalInfoModel()));
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    private static Organisation randomOrganisation() {
        var organisation = new Organisation();
        organisation.setId(count++);
        organisation.setOrganisationId(randomString());
        organisation.setName(randomString());
        organisation.setPoolSize((int) count++);
        organisation.setGroupId(count++);
        organisation.setSmsSenderName(randomString());
        organisation.setAllowCustomUriWithoutDomain(randomBoolean());
        organisation.setSmsCallbackUrl(randomString());

        return organisation;
    }

    private static MeetingUser randomMeetingUser() {
        var meetingUser = new MeetingUser();
        meetingUser.setId(count++);
        meetingUser.setOrganisation(randomOrganisation());
        meetingUser.setEmail(randomString());

        return meetingUser;
    }

    private static MeetingLabel randomMeetingLabel() {
        var meetingLabel = new MeetingLabel();
        meetingLabel.setId(count++);
        meetingLabel.setLabel(randomString());
        //Creates circular dependency
        //meetingLabel.setMeeting(randomMeetingModel());

        return meetingLabel;
    }

    private static MeetingAdditionalInfo randomAdditionalInfo() {
        var additionalInfo = new MeetingAdditionalInfo();
        additionalInfo.setId(count++);
        //Creates circular dependency
        //additionalInfo.setMeeting(randomMeetingModel());
        additionalInfo.setInfoKey(randomString());
        additionalInfo.setInfoValue(randomString());
        additionalInfo.setCreatedTime(Instant.now());

        return additionalInfo;
    }

    private static AdditionalInformationModel randomAdditionalInfoModel() {
        return new AdditionalInformationModel(randomString(), randomString());
    }

    private static boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }


    // ---------- assertions --------------

    public static void assertPoolInfo(PoolInfoDto expected, String shortLinkBaseUrl, PoolInfoModel actual) {
        assertEquals(expected.getOrganizationId(), actual.organisationId());
        assertEquals(expected.getAvailablePoolSize(), actual.availablePoolSize());
        assertEquals(expected.getDesiredPoolSize(), actual.desiredPoolSize());
        assertEquals(expected.getSchedulingInfoList().size(), actual.schedulingInfoList().size());

        for (int i = 0; i < expected.getSchedulingInfoList().size(); i++) {
            assertSchedulingInfo(expected.getSchedulingInfoList().get(i), shortLinkBaseUrl, actual.schedulingInfoList().get(i));
        }

        assertSchedulingTemplateDto(expected.getSchedulingTemplate(), actual.schedulingTemplate());
    }

    public static void assertMeeting(Meeting expected, String shortLinkBaseUrl, MeetingModel actual) {
        assertEquals(expected.getSubject(), actual.subject());
        assertEquals(expected.getUuid(), actual.uuid().toString());
        assertMeetingUser(expected.getMeetingUser(), actual.createdBy());
        assertMeetingUser(expected.getUpdatedByUser(), actual.updatedBy());
        assertMeetingUser(expected.getOrganizedByUser(), actual.organizedBy());
        assertEquals(expected.getStartTime().toInstant(), actual.startTime().toInstant());
        assertEquals(expected.getEndTime().toInstant(), actual.endTime().toInstant());
        assertEquals(expected.getDescription(), actual.description());
        assertEquals(expected.getProjectCode(), actual.projectCode());
        assertEquals(expected.getCreatedTime().toInstant(), actual.createdTime().toInstant());
        assertEquals(expected.getUpdatedTime().toInstant(), actual.updatedTime().toInstant());
        assertEquals(expected.getShortId(), actual.shortId());
        assertEquals(shortLinkBaseUrl + expected.getShortId(), actual.shortLink());
        assertEquals(actual.shortLink(), actual.shortlink());
        assertEquals(expected.getExternalId(), actual.externalId());
        assertEquals(expected.getGuestMicrophone().toString(), actual.guestMicrophone().toString());
        assertEquals(expected.getGuestPinRequired(), actual.guestPinRequired());
        assertLabels(expected.getMeetingLabels(), actual.labels());
        assertAdditionalInformation(expected.getMeetingAdditionalInfo(), actual.additionalInformation());
    }

    public static boolean assertCreateMeeting(CreateMeetingModel expected, CreateMeetingDto actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertTime(expected.startTime(), actual.getStartTime());
        assertTime(expected.endTime(), actual.getEndTime());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.schedulingInfoReservationId(), actual.getSchedulingInfoReservationId());
        assertEquals(expected.organizedByEmail(), actual.getOrganizedByEmail());
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.isEndMeetingOnEndTime());
        assertEquals(expected.schedulingTemplateId(), actual.getSchedulingTemplateId(), 0);
        assertEquals(expected.meetingType().toString(), actual.getMeetingType().toString());
        assertEquals(expected.uuid(), actual.getUuid());
        assertEquals(expected.labels(), actual.getLabels());
        assertEquals(expected.externalId(), actual.getExternalId());
        if (expected.guestMicrophone() != null) {
            assertEquals(expected.guestMicrophone().toString(), actual.getGuestMicrophone().toString());
        } else {
            assertNull(actual.getGuestMicrophone());
        }
        assertEquals(expected.guestPinRequired(), actual.getGuestPinRequired());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertEquals(expected.vmrQuality().toString(), actual.getVmrQuality().toString());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertEquals(expected.uriWithoutDomain(), actual.getUriWithoutDomain());
        assertEquals(expected.hostPin(), actual.getHostPin());
        assertEquals(expected.guestPin(), actual.getGuestPin());
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());

        return true;
    }

    public static boolean assertUpdateMeeting(UpdateMeetingModel expected, UpdateMeetingDto actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertTime(expected.startTime(), actual.getStartTime());
        assertTime(expected.endTime(), actual.getEndTime());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.organizedByEmail(), actual.getOrganizedByEmail());
        assertEquals(expected.labels(), actual.getLabels());
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());

        return true;
    }

    public static boolean assertPatchMeeting(PatchMeetingModel expected, PatchMeetingDto actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertEquals(expected.subject() != null, actual.isSubjectIsSet());
        assertTime(expected.startTime(), actual.getStartTime());
        assertEquals(expected.startTime() != null, actual.isStartTimeIsSet());
        assertTime(expected.endTime(), actual.getEndTime());
        assertEquals(expected.endTime() != null, actual.isEndTimeIsSet());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.description() != null, actual.getDescriptionIsSet());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.projectCode() != null, actual.isProjectIsSet());
        assertEquals(expected.organizedByEmail(), actual.getOrganizedByEmail());
        assertEquals(expected.organizedByEmail() != null, actual.isOrganizedByEmailIsSet());
        assertEquals(expected.labels(), actual.getLabels());
        assertEquals(expected.labels() != null, actual.isLabelsIsSet());

        if (expected.guestMicrophone() == null) {
            assertNull(actual.getGuestMicrophone());
            assertFalse(actual.isGuestMicrophoneSet());
        } else {
            assertEquals(expected.guestMicrophone().toString(), actual.getGuestMicrophone().toString());
            assertTrue(actual.isGuestMicrophoneSet());
        }

        assertEquals(expected.guestPinRequired(), actual.isGuestPinRequired());
        assertEquals(expected.guestPinRequired() != null, actual.isGuestPinRequiredSet());
        assertEquals(expected.guestPin(), actual.getGuestPin());
        assertEquals(expected.guestPin() != null, actual.isGuestPinSet());
        assertEquals(expected.hostPin(), actual.getHostPin());
        assertEquals(expected.hostPin() != null, actual.isHostPinSet());
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());
        assertEquals(expected.additionalInformation() != null, actual.isAdditionalInfoSet());

        return true;
    }

    public static void assertSchedulingInfo(SchedulingInfo expected, String shortLinkBaseUrl, SchedulingInfoModel actual) {
        assertEquals(expected.getUuid(), actual.uuid().toString());
        assertEquals(expected.getHostPin(), actual.hostPin(), 0);
        assertEquals(expected.getGuestPin(), actual.guestPin(), 0);
        assertEquals(expected.getVMRAvailableBefore(), actual.vmrAvailableBefore(), 0);
        assertEquals(expected.getMaxParticipants(), actual.maxParticipants(), 0);
        assertEquals(expected.getEndMeetingOnEndTime(), actual.endMeetingOnEndTime());
        assertEquals(expected.getUriWithDomain(), actual.uriWithDomain());
        assertEquals(expected.getUriWithoutDomain(), actual.uriWithoutDomain());
        assertEquals(expected.getProvisionStatus().toString(), actual.provisionStatus().toString());
        assertEquals(expected.getProvisionStatusDescription(), actual.provisionStatusDescription());
        assertEquals(expected.getPortalLink(), actual.portalLink());
        assertEquals(expected.getIvrTheme(), actual.ivrTheme());
        assertTime(actual.provisionTimestamp(), expected.getProvisionTimestamp());
        assertEquals(expected.getProvisionVMRId(), actual.provisionVmrId());
        assertEquals(expected.getVmrType().toString(), actual.vmrType().toString());
        assertEquals(expected.getHostView().toString(), actual.hostView().toString());
        assertEquals(expected.getGuestView().toString(), actual.guestView().toString());
        assertEquals(expected.getVmrQuality().toString(), actual.vmrQuality().toString());
        assertEquals(expected.getEnableOverlayText(), actual.enableOverlayText());
        assertEquals(expected.getGuestsCanPresent(), actual.guestsCanPresent());
        assertEquals(expected.getForcePresenterIntoMain(), actual.forcePresenterIntoMain());
        assertEquals(expected.getForceEncryption(), actual.forceEncryption());
        assertEquals(expected.getMuteAllGuests(), actual.muteAllGuests());
        assertMeetingUser(expected.getMeetingUser(), actual.createdBy());
        assertMeetingUser(expected.getUpdatedByUser(), actual.updatedBy());
        assertTime(actual.createdTime(), expected.getCreatedTime());
        assertTime(actual.updatedTime(), expected.getUpdatedTime());
        assertEquals(expected.getReservationId(), actual.reservationId().toString());
        assertEquals(expected.getCustomPortalGuest(), actual.customPortalGuest());
        assertEquals(expected.getCustomPortalHost(), actual.customPortalHost());
        assertEquals(expected.getReturnUrl(), actual.returnUrl());
        assertMeeting(expected.getMeeting(), shortLinkBaseUrl, actual.meetingDetails());
        assertEquals(expected.getDirectMedia().toString(), actual.directMedia().toString());
        assertEquals(shortLinkBaseUrl + expected.getMeeting().getShortId(), actual.shortLink());
        assertEquals(shortLinkBaseUrl + expected.getMeeting().getShortId(), actual.shortlink());
    }


    public static void assertSchedulingTemplate(SchedulingTemplate expected, SchedulingTemplateModel actual) {
        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getOrganisation().getOrganisationId(), actual.organisationId());
        assertEquals(expected.getConferencingSysId(), actual.conferencingSysId());
        assertEquals(expected.getUriPrefix(), actual.uriPrefix());
        assertEquals(expected.getUriDomain(), actual.uriDomain());
        assertEquals(expected.getHostPinRequired(), actual.hostPinRequired());
        assertEquals(expected.getHostPinRangeLow(), actual.hostPinRangeLow());
        assertEquals(expected.getHostPinRangeHigh(), actual.hostPinRangeHigh());
        assertEquals(expected.getGuestPinRequired(), actual.guestPinRequired());
        assertEquals(expected.getGuestPinRangeLow(), actual.guestPinRangeLow());
        assertEquals(expected.getGuestPinRangeHigh(), actual.guestPinRangeHigh());
        assertEquals(expected.getVMRAvailableBefore(), actual.vMRAvailableBefore(), 0);
        assertEquals(expected.getMaxParticipants(), actual.maxParticipants(), 0);
        assertEquals(expected.getEndMeetingOnEndTime(), actual.endMeetingOnEndTime());
        assertEquals(expected.getUriNumberRangeLow(), actual.uriNumberRangeLow());
        assertEquals(expected.getUriNumberRangeHigh(), actual.uriNumberRangeHigh());
        assertEquals(expected.getIvrTheme(), actual.ivrTheme());
        assertEquals(expected.getIsDefaultTemplate(), actual.isDefaultTemplate());
        assertEquals(expected.getIsPoolTemplate(), actual.isPoolTemplate());
        assertEquals(expected.getCustomPortalGuest(), actual.customPortalGuest());
        assertEquals(expected.getCustomPortalHost(), actual.customPortalHost());
        assertEquals(expected.getReturnUrl(), actual.returnUrl());
        assertEquals(expected.getVmrType().toString(), actual.vmrType().toString());
        assertEquals(expected.getHostView().toString(), actual.hostView().toString());
        assertEquals(expected.getGuestView().toString(), actual.guestView().toString());
        assertEquals(expected.getVmrQuality().toString(), actual.vmrQuality().toString());
        assertEquals(expected.getEnableOverlayText(), actual.enableOverlayText());
        assertEquals(expected.getGuestsCanPresent(), actual.guestsCanPresent());
        assertEquals(expected.getForcePresenterIntoMain(), actual.forcePresenterIntoMain());
        assertEquals(expected.getForceEncryption(), actual.forceEncryption());
        assertEquals(expected.getMuteAllGuests(), actual.muteAllGuests());
        assertEquals(expected.getDirectMedia().toString(), actual.directMedia().toString());
        assertMeetingUser(expected.getCreatedBy(), actual.createdBy());
        assertMeetingUser(expected.getUpdatedBy(), actual.updatedBy());
        assertTime(actual.createdTime(), expected.getCreatedTime());
        assertTime(actual.updatedTime(), expected.getUpdatedTime());
    }

    public static void assertSchedulingTemplateDto(SchedulingTemplateDto expected, SchedulingTemplateModel actual) {
        assertEquals(expected.getTemplateId(), actual.id());
        assertEquals(expected.getOrganisationId(), actual.organisationId());
        assertEquals(expected.getConferencingSysId(), actual.conferencingSysId());
        assertEquals(expected.getUriPrefix(), actual.uriPrefix());
        assertEquals(expected.getUriDomain(), actual.uriDomain());
        assertEquals(expected.isHostPinRequired(), actual.hostPinRequired());
        assertEquals(expected.getHostPinRangeLow(), actual.hostPinRangeLow());
        assertEquals(expected.getHostPinRangeHigh(), actual.hostPinRangeHigh());
        assertEquals(expected.isGuestPinRequired(), actual.guestPinRequired());
        assertEquals(expected.getGuestPinRangeLow(), actual.guestPinRangeLow());
        assertEquals(expected.getGuestPinRangeHigh(), actual.guestPinRangeHigh());
        assertEquals(expected.getvMRAvailableBefore(), actual.vMRAvailableBefore(), 0);
        assertEquals(expected.getMaxParticipants(), actual.maxParticipants(), 0);
        assertEquals(expected.isEndMeetingOnEndTime(), actual.endMeetingOnEndTime());
        assertEquals(expected.getUriNumberRangeLow(), actual.uriNumberRangeLow());
        assertEquals(expected.getUriNumberRangeHigh(), actual.uriNumberRangeHigh());
        assertEquals(expected.getIvrTheme(), actual.ivrTheme());
        assertEquals(expected.getIsDefaultTemplate(), actual.isDefaultTemplate());
        assertEquals(expected.getIsPoolTemplate(), actual.isPoolTemplate());
        assertEquals(expected.getVmrType().toString(), actual.vmrType().toString());
        assertEquals(expected.getHostView().toString(), actual.hostView().toString());
        assertEquals(expected.getGuestView().toString(), actual.guestView().toString());
        assertEquals(expected.getVmrQuality().toString(), actual.vmrQuality().toString());
        assertEquals(expected.isEnableOverlayText(), actual.enableOverlayText());
        assertEquals(expected.isGuestsCanPresent(), actual.guestsCanPresent());
        assertEquals(expected.isForcePresenterIntoMain(), actual.forcePresenterIntoMain());
        assertEquals(expected.isForceEncryption(), actual.forceEncryption());
        assertEquals(expected.isMuteAllGuests(), actual.muteAllGuests());
        assertEquals(expected.getCustomPortalGuest(), actual.customPortalGuest());
        assertEquals(expected.getCustomPortalHost(), actual.customPortalHost());
        assertEquals(expected.getReturnUrl(), actual.returnUrl());
        assertEquals(expected.getDirectMedia().toString(), actual.directMedia().toString());
        assertMeetingUserDto(expected.getCreatedBy(), actual.createdBy());
        assertMeetingUserDto(expected.getUpdatedBy(), actual.updatedBy());
        assertTime(actual.createdTime(), expected.getCreatedTime());
        assertTime(actual.updatedTime(), expected.getUpdatedTime());
    }

    public static boolean assertSchedulingTemplateRequest(SchedulingTemplateRequestModel expected, UpdateSchedulingTemplateDto actual) {
        assertEquals(expected.conferencingSysId(), actual.getConferencingSysId());
        assertEquals(expected.uriPrefix(), actual.getUriPrefix());
        assertEquals(expected.uriDomain(), actual.getUriDomain());
        assertEquals(expected.hostPinRequired(), actual.isHostPinRequired());
        assertEquals(expected.hostPinRangeLow(), actual.getHostPinRangeLow());
        assertEquals(expected.hostPinRangeHigh(), actual.getHostPinRangeHigh());
        assertEquals(expected.guestPinRequired(), actual.isGuestPinRequired());
        assertEquals(expected.guestPinRangeLow(), actual.getGuestPinRangeLow());
        assertEquals(expected.guestPinRangeHigh(), actual.getGuestPinRangeHigh());
        assertEquals(expected.vMRAvailableBefore(), actual.getvMRAvailableBefore(), 0);
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.isEndMeetingOnEndTime());
        assertEquals(expected.uriNumberRangeLow(), actual.getUriNumberRangeLow());
        assertEquals(expected.uriNumberRangeHigh(), actual.getUriNumberRangeHigh());
        assertEquals(expected.ivrTheme(), actual.getIvrTheme());
        assertEquals(expected.isDefaultTemplate(), actual.getIsDefaultTemplate());
        assertEquals(expected.isPoolTemplate(), actual.getIsPoolTemplate());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertEquals(expected.vmrQuality().toString(), actual.getVmrQuality().toString());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertEquals(expected.customPortalGuest(), actual.getCustomPortalGuest());
        assertEquals(expected.customPortalHost(), actual.getCustomPortalHost());
        assertEquals(expected.returnUrl(), actual.getReturnUrl());
        assertEquals(expected.directMedia().toString(), actual.getDirectMedia().toString());

        return true;
    }

    public static boolean assertSchedulingTemplateRequest(SchedulingTemplateRequestModel expected, CreateSchedulingTemplateDto actual) {
        assertEquals(expected.conferencingSysId(), actual.getConferencingSysId());
        assertEquals(expected.uriPrefix(), actual.getUriPrefix());
        assertEquals(expected.uriDomain(), actual.getUriDomain());
        assertEquals(expected.hostPinRequired(), actual.isHostPinRequired());
        assertEquals(expected.hostPinRangeLow(), actual.getHostPinRangeLow());
        assertEquals(expected.hostPinRangeHigh(), actual.getHostPinRangeHigh());
        assertEquals(expected.guestPinRequired(), actual.isGuestPinRequired());
        assertEquals(expected.guestPinRangeLow(), actual.getGuestPinRangeLow());
        assertEquals(expected.guestPinRangeHigh(), actual.getGuestPinRangeHigh());
        assertEquals(expected.vMRAvailableBefore(), actual.getvMRAvailableBefore(), 0);
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.isEndMeetingOnEndTime());
        assertEquals(expected.uriNumberRangeLow(), actual.getUriNumberRangeLow());
        assertEquals(expected.uriNumberRangeHigh(), actual.getUriNumberRangeHigh());
        assertEquals(expected.ivrTheme(), actual.getIvrTheme());
        assertEquals(expected.isDefaultTemplate(), actual.getIsDefaultTemplate());
        assertEquals(expected.isPoolTemplate(), actual.getIsPoolTemplate());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertEquals(expected.vmrQuality().toString(), actual.getVmrQuality().toString());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertEquals(expected.customPortalGuest(), actual.getCustomPortalGuest());
        assertEquals(expected.customPortalHost(), actual.getCustomPortalHost());
        assertEquals(expected.returnUrl(), actual.getReturnUrl());
        assertEquals(expected.directMedia().toString(), actual.getDirectMedia().toString());

        return true;
    }

    private static void assertMeetingUser(MeetingUser expected, MeetingUserModel actual) {
        assertEquals(expected.getOrganisation().getOrganisationId(), actual.organisationId());
        assertEquals(expected.getEmail(), actual.email());
    }

    private static void assertMeetingUserDto(MeetingUserDto expected, MeetingUserModel actual) {
        assertEquals(expected.organisationId, actual.organisationId());
        assertEquals(expected.email, actual.email());
    }

    private static void assertLabels(Set<MeetingLabel> expected, List<String> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i ++) {
            var actualLabel = actual.get(i);
            assertTrue(expected.stream().anyMatch(x -> x.getLabel().equals(actualLabel)));
        }
    }

    private static void assertAdditionalInformation(Set<MeetingAdditionalInfo> expected, List<AdditionalInformationModel> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i ++) {
            var actualAddInfo = actual.get(i);
            assertTrue(expected.stream().anyMatch(x -> x.getInfoKey().equals(actualAddInfo.key()) && x.getInfoValue().equals(actualAddInfo.value())));
        }
    }

    private static void assertAdditionalInformation(List<AdditionalInformationModel> expected, List<AdditionalInformationType> actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i ++) {
            assertEquals(expected.get(i).key(), actual.get(i).key());
            assertEquals(expected.get(i).value(), actual.get(i).value());
        }
    }

    private static void assertTime(OffsetDateTime expected, Date actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertEquals(expected.toInstant().truncatedTo(ChronoUnit.SECONDS), actual.toInstant().truncatedTo(ChronoUnit.SECONDS));
    }
}
