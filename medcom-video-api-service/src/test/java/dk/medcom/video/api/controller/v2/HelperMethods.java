package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.service.model.*;
import org.openapitools.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class HelperMethods {
    private static long count = 0;

    public static SchedulingInfoModel randomSchedulingInfo() {
        return new SchedulingInfoModel(UUID.randomUUID(),
                count++,
                count++,
                (int) count++,
                (int) count++,
                randomBoolean(),
                randomString(),
                randomString(),
                ProvisionStatusModel.AWAITS_PROVISION,
                randomString(),
                randomString(),
                randomString(),
                OffsetDateTime.now(),
                randomString(),
                VmrTypeModel.lecture,
                ViewTypeModel.one_main_zero_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomMeetingUser(),
                randomMeetingUser(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                UUID.randomUUID(),
                randomString(),
                randomString(),
                randomString(),
                randomMeeting(),
                DirectMediaModel.best_effort,
                randomString(),
                randomString());
    }

    public static MeetingModel randomMeeting() {
        return new MeetingModel(randomString(),
                UUID.randomUUID(),
                randomMeetingUser(),
                randomMeetingUser(),
                randomMeetingUser(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                randomString(),
                randomString(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                GuestMicrophoneModel.muted,
                randomBoolean(),
                List.of(randomString(),
                randomString()),
                randomAdditionalInformationModel());
    }

    public static SchedulingTemplateModel randomSchedulingTemplate() {
        return new SchedulingTemplateModel(count++,
                randomString(),
                count++,
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
                DirectMediaModel.never,
                randomMeetingUser(),
                randomMeetingUser(),
                OffsetDateTime.now(),
                OffsetDateTime.now());
    }

    public static PoolInfoModel randomPoolInfoInOrganisation(String organisation) {
        return new PoolInfoModel(organisation,
                (int) count++,
                (int) count++,
                randomSchedulingTemplate(),
                List.of(randomSchedulingInfo(),
                randomSchedulingInfo()));
    }

    private static MeetingUserModel randomMeetingUser() {
        return new MeetingUserModel(randomString(), randomString());
    }

    private static List<AdditionalInformationModel> randomAdditionalInformationModel() {
        return List.of(
                new AdditionalInformationModel(randomString(), randomString()),
                new AdditionalInformationModel(randomString(), randomString())
        );
    }

    public static CreateMeeting randomCreateMeetingInput() {
        return new CreateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now())
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .maxParticipants((int) count++)
                .endMeetingOnEndTime(randomBoolean())
                .schedulingTemplateId(count++)
                .meetingType(MeetingType.POOL)
                .uuid(UUID.randomUUID())
                .externalId(randomString())
                .guestMicrophone(GuestMicrophone.MUTED)
                .guestPinRequired(randomBoolean())
                .schedulingInfoReservationId(UUID.randomUUID())
                .labels(List.of(randomString()))
                .vmrType(VmrType.CONFERENCE)
                .hostView(ViewType.ONE_MAIN_THIRTYTHREE_PIPS)
                .guestView(ViewType.ONE_MAIN_ZERO_PIPS)
                .vmrQuality(VmrQuality.SD)
                .enableOverlayText(randomBoolean())
                .guestsCanPresent(randomBoolean())
                .forcePresenterIntoMain(randomBoolean())
                .forceEncryption(randomBoolean())
                .muteAllGuests(randomBoolean())
                .uriWithoutDomain(randomString())
                .guestPin((int) count++)
                .hostPin((int) count++)
                .additionalInformation(List.of(randomAdditionalInformationInput()));
    }

    public static PatchMeeting randomPatchMeetingInput() {
        return new PatchMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now())
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .labels(List.of(randomString()))
                .guestMicrophone(GuestMicrophone.MUTED)
                .guestPinRequired(randomBoolean())
                .guestPin((int) count++)
                .hostPin((int) count++)
                .additionalInformation(List.of(randomAdditionalInformationInput()));
    }

    public static UpdateMeeting randomUpdateMeetingInput() {
        return new UpdateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now())
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .labels(List.of(randomString()))
                .additionalInformation(List.of(randomAdditionalInformationInput()));
    }

    public static SchedulingTemplateRequest randomSchedulingTemplateRequestInput() {
        return new SchedulingTemplateRequest()
                .conferencingSysId(count++)
                .uriPrefix(randomString())
                .uriDomain(randomString())
                .hostPinRequired(randomBoolean())
                .hostPinRangeLow(count++)
                .hostPinRangeHigh(count++)
                .guestPinRequired(randomBoolean())
                .guestPinRangeLow(count++)
                .guestPinRangeHigh(count++)
                .vmrAvailableBefore((int) count++)
                .maxParticipants((int) count++)
                .endMeetingOnEndTime(randomBoolean())
                .uriNumberRangeLow(count++)
                .uriNumberRangeHigh(count++)
                .ivrTheme(randomString())
                .isDefaultTemplate(randomBoolean())
                .isPoolTemplate(randomBoolean())
                .customPortalGuest(randomString())
                .customPortalHost(randomString())
                .returnUrl(randomString())
                .vmrType(VmrType.LECTURE)
                .hostView(ViewType.ONE_MAIN_SEVEN_PIPS)
                .guestView(ViewType.TWENTYFIVE_MAINS_ZERO_PIPS)
                .vmrQuality(VmrQuality.FULLHD)
                .enableOverlayText(randomBoolean())
                .guestsCanPresent(randomBoolean())
                .forcePresenterIntoMain(randomBoolean())
                .forceEncryption(randomBoolean())
                .muteAllGuests(randomBoolean())
                .directMedia(DirectMedia.NEVER);
    }

    public static CreateSchedulingInfo randomCreateSchedulingInfoInput() {
        return new CreateSchedulingInfo()
                .organizationId(randomString())
                .schedulingTemplateId(count++);
    }

    public static UpdateSchedulingInfo randomUpdateSchedulingInfoInput() {
        return new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.PROVISIONED_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
    }

    private static AdditionalInformationType randomAdditionalInformationInput() {
        return new AdditionalInformationType().key(randomString()).value(randomString());
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    private static boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }


    // ---------- assertions --------------

    public static void assertSchedulingInfo(SchedulingInfoModel expected, SchedulingInfo actual) {
        assertEquals(expected.uuid(), actual.getUuid());
        assertEquals(expected.hostPin(), actual.getHostPin(), 0);
        assertEquals(expected.guestPin(), actual.getGuestPin(), 0);
        assertEquals(expected.vmrAvailableBefore(), actual.getVmrAvailableBefore(), 0);
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.getEndMeetingOnEndTime());
        assertEquals(expected.uriWithDomain(), actual.getUriWithDomain());
        if (expected.uriWithoutDomain() == null) {
            assertFalse(actual.getUriWithoutDomain().isPresent());
        } else {
            assertEquals(expected.uriWithoutDomain(), actual.getUriWithoutDomain().get());
        }
        assertEquals(expected.provisionStatus().toString(), actual.getProvisionStatus().toString());
        assertEquals(expected.provisionStatusDescription(), actual.getProvisionStatusDescription());
        assertEquals(expected.portalLink(), actual.getPortalLink());
        assertEquals(expected.ivrTheme(), actual.getIvrTheme());
        assertEquals(expected.provisionTimestamp(), actual.getProvisionTimestamp());
        assertEquals(expected.provisionVmrId(), actual.getProvisionVmrId());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertEquals(expected.vmrQuality().toString(), actual.getVmrQuality().toString());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertMeetingUser(expected.createdBy(), actual.getCreatedBy());
        assertMeetingUser(expected.updatedBy(), actual.getUpdatedBy());
        assertEquals(expected.createdTime(), actual.getCreatedTime());
        assertEquals(expected.updatedTime(), actual.getUpdatedTime());
        assertEquals(expected.reservationId(), actual.getReservationId());
        assertEquals(expected.customPortalGuest(), actual.getCustomPortalGuest());
        assertEquals(expected.customPortalHost(), actual.getCustomPortalHost());
        assertEquals(expected.returnUrl(), actual.getReturnUrl());
        assertMeeting(expected.meetingDetails(), actual.getMeetingDetails());
        assertNotNull(actual.getLinks());
        assertTrue(actual.getLinks().getSelf().getHref().toString().contains(actual.getUuid().toString()));
        assertEquals(expected.directMedia().toString(), actual.getDirectMedia().toString());
        assertEquals(expected.shortLink(), actual.getShortLink());
        assertEquals(expected.shortlink(), actual.getShortlink());
    }

    public static void assertMeeting(MeetingModel expected, Meeting actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertEquals(expected.uuid(), actual.getUuid());
        assertMeetingUser(expected.createdBy(), actual.getCreatedBy());
        assertMeetingUser(expected.updatedBy(), actual.getUpdatedBy());
        assertMeetingUser(expected.organizedBy(), actual.getOrganizedBy());
        assertEquals(expected.startTime(), actual.getStartTime());
        assertEquals(expected.endTime(), actual.getEndTime());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.createdTime(), actual.getCreatedTime());
        assertEquals(expected.updatedTime(), actual.getUpdatedTime());
        assertEquals(expected.shortId(), actual.getShortId());
        assertEquals(expected.shortLink(), actual.getShortLink());
        assertEquals(expected.shortlink(), actual.getShortlink());
        assertEquals(expected.externalId(), actual.getExternalId());
        assertGuestMicrophone(expected.guestMicrophone(), actual.getGuestMicrophone());
        assertEquals(expected.guestPinRequired(), actual.getGuestPinRequired());
        assertLabels(expected.labels(), actual.getLabels());
        assertNotNull(actual.getLinks());
        assertTrue(actual.getLinks().getSelf().getHref().toString().contains(actual.getUuid().toString()));
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());
    }

    public static void assertSchedulingTemplate(SchedulingTemplateModel expected, SchedulingTemplate actual) {
        assertEquals(expected.id(), actual.getId());
        assertEquals(expected.organisationId(), actual.getOrganisationId());
        assertEquals(expected.conferencingSysId(), actual.getConferencingSysId());
        assertEquals(expected.uriPrefix(), actual.getUriPrefix());
        assertEquals(expected.uriDomain(), actual.getUriDomain());
        assertEquals(expected.hostPinRequired(), actual.getHostPinRequired());
        assertEquals(expected.hostPinRangeLow(), actual.getHostPinRangeLow());
        assertEquals(expected.hostPinRangeHigh(), actual.getHostPinRangeHigh());
        assertEquals(expected.guestPinRequired(), actual.getGuestPinRequired());
        assertEquals(expected.guestPinRangeLow(), actual.getGuestPinRangeLow());
        assertEquals(expected.guestPinRangeHigh(), actual.getGuestPinRangeHigh());
        assertEquals(expected.vMRAvailableBefore(), actual.getvMRAvailableBefore(), 0);
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.getEndMeetingOnEndTime());
        assertEquals(expected.uriNumberRangeLow(), actual.getUriNumberRangeLow());
        assertEquals(expected.uriNumberRangeHigh(), actual.getUriNumberRangeHigh());
        assertEquals(expected.ivrTheme(), actual.getIvrTheme());
        assertEquals(expected.isDefaultTemplate(), actual.getIsDefaultTemplate());
        assertEquals(expected.isPoolTemplate(), actual.getIsPoolTemplate());
        assertEquals(expected.customPortalGuest(), actual.getCustomPortalGuest());
        assertEquals(expected.customPortalHost(), actual.getCustomPortalHost());
        assertEquals(expected.returnUrl(), actual.getReturnUrl());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertEquals(expected.vmrQuality().toString(), actual.getVmrQuality().toString());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertEquals(expected.directMedia().toString(), actual.getDirectMedia().toString());
        assertMeetingUser(expected.createdBy(), actual.getCreatedBy());
        assertMeetingUser(expected.updatedBy(), actual.getUpdatedBy());
        assertEquals(expected.createdTime(), actual.getCreatedTime());
        assertEquals(expected.updatedTime(), actual.getUpdatedTime());
        assertNotNull(actual.getLinks());
        assertTrue(actual.getLinks().getSelf().getHref().toString().contains(actual.getId().toString()));
    }

    public static void assertPoolInfo(PoolInfoModel expected, PoolInfo actual) {
        assertEquals(expected.organisationId(), actual.getOrganisationId());
        assertEquals(expected.desiredPoolSize(), actual.getDesiredPoolSize(), 0);
        assertEquals(expected.availablePoolSize(), actual.getAvailablePoolSize(), 0);
        assertSchedulingTemplate(expected.schedulingTemplate(), actual.getSchedulingTemplate());
        assertEquals(expected.schedulingInfoList().size(), actual.getSchedulingInfoList().size());

        for (int i=0; i < expected.schedulingInfoList().size(); i++) {
            assertSchedulingInfo(expected.schedulingInfoList().get(i), actual.getSchedulingInfoList().get(i));
        }
    }

    public static boolean assertSchedulingTemplateRequest(SchedulingTemplateRequestModel expected, SchedulingTemplateRequest actual) {
        assertEquals(expected.conferencingSysId(), actual.getConferencingSysId());
        assertEquals(expected.uriPrefix(), actual.getUriPrefix());
        assertEquals(expected.uriDomain(), actual.getUriDomain());
        assertEquals(expected.hostPinRequired(), actual.getHostPinRequired());
        assertEquals(expected.hostPinRangeLow(), actual.getHostPinRangeLow());
        assertEquals(expected.hostPinRangeHigh(), actual.getHostPinRangeHigh());
        assertEquals(expected.guestPinRequired(), actual.getGuestPinRequired());
        assertEquals(expected.guestPinRangeLow(), actual.getGuestPinRangeLow());
        assertEquals(expected.guestPinRangeHigh(), actual.getGuestPinRangeHigh());
        assertEquals(expected.vMRAvailableBefore(), actual.getVmrAvailableBefore(), 0);
        assertEquals(expected.maxParticipants(), actual.getMaxParticipants(), 0);
        assertEquals(expected.endMeetingOnEndTime(), actual.getEndMeetingOnEndTime());
        assertEquals(expected.uriNumberRangeLow(), actual.getUriNumberRangeLow());
        assertEquals(expected.uriNumberRangeHigh(), actual.getUriNumberRangeHigh());
        assertEquals(expected.ivrTheme(), actual.getIvrTheme());
        assertEquals(expected.isDefaultTemplate(), actual.getIsDefaultTemplate());
        assertEquals(expected.isPoolTemplate(), actual.getIsPoolTemplate());
        assertEquals(expected.customPortalGuest(), actual.getCustomPortalGuest());
        assertEquals(expected.customPortalHost(), actual.getCustomPortalHost());
        assertEquals(expected.returnUrl(), actual.getReturnUrl());
        assertEquals(expected.vmrType().toString(), actual.getVmrType().toString());
        assertEquals(expected.hostView().toString(), actual.getHostView().toString());
        assertEquals(expected.guestView().toString(), actual.getGuestView().toString());
        assertVmrQuality(expected.vmrQuality(), actual.getVmrQuality());
        assertEquals(expected.enableOverlayText(), actual.getEnableOverlayText());
        assertEquals(expected.guestsCanPresent(), actual.getGuestsCanPresent());
        assertEquals(expected.forcePresenterIntoMain(), actual.getForcePresenterIntoMain());
        assertEquals(expected.forceEncryption(), actual.getForceEncryption());
        assertEquals(expected.muteAllGuests(), actual.getMuteAllGuests());
        assertEquals(expected.directMedia().toString(), actual.getDirectMedia().toString());

        return true;
    }

    public static boolean assertCreateMeeting(CreateMeetingModel actual, CreateMeeting expected) {
        assertEquals(actual.subject(), expected.getSubject());
        assertEquals(actual.startTime(), expected.getStartTime());
        assertEquals(actual.endTime(), expected.getEndTime());
        assertEquals(actual.description(), expected.getDescription());
        assertEquals(actual.projectCode(), expected.getProjectCode());
        assertEquals(actual.organizedByEmail(), expected.getOrganizedByEmail());
        assertEquals(actual.maxParticipants(), expected.getMaxParticipants());
        assertEquals(actual.endMeetingOnEndTime(), expected.getEndMeetingOnEndTime());
        assertEquals(actual.schedulingTemplateId(), expected.getSchedulingTemplateId());
        if (expected.getMeetingType() == null) {
            assertNull(actual.meetingType());
        } else {
            assertEquals(actual.meetingType().toString(), expected.getMeetingType().toString());
        }
        assertEquals(actual.uuid(), expected.getUuid());
        assertEquals(actual.externalId(), expected.getExternalId());
        assertGuestMicrophone(actual.guestMicrophone(), expected.getGuestMicrophone());
        assertEquals(actual.guestPinRequired(), expected.getGuestPinRequired());
        assertLabels(actual.labels(), expected.getLabels());
        assertEquals(actual.vmrType().toString(), expected.getVmrType().toString());
        assertEquals(actual.hostView().toString(), expected.getHostView().toString());
        assertEquals(actual.guestView().toString(), expected.getGuestView().toString());
        assertVmrQuality(actual.vmrQuality(), expected.getVmrQuality());
        assertEquals(actual.enableOverlayText(), expected.getEnableOverlayText());
        assertEquals(actual.guestsCanPresent(), expected.getGuestsCanPresent());
        assertEquals(actual.forcePresenterIntoMain(), expected.getForcePresenterIntoMain());
        assertEquals(actual.forceEncryption(), expected.getForceEncryption());
        assertEquals(actual.muteAllGuests(), expected.getMuteAllGuests());
        assertEquals(actual.uriWithoutDomain(), expected.getUriWithoutDomain());
        assertEquals(actual.guestPin(), expected.getGuestPin());
        assertEquals(actual.hostPin(), expected.getHostPin());
        assertAdditionalInformation(actual.additionalInformation(), expected.getAdditionalInformation());

        return true;
    }

    public static boolean assertPatchMeeting(PatchMeetingModel expected, PatchMeeting actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertEquals(expected.startTime(), actual.getStartTime());
        assertEquals(expected.endTime(), actual.getEndTime());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.organizedByEmail(), actual.getOrganizedByEmail());
        assertLabels(expected.labels(), actual.getLabels());
        assertGuestMicrophone(expected.guestMicrophone(), actual.getGuestMicrophone());
        assertEquals(expected.guestPinRequired(), actual.getGuestPinRequired());
        assertEquals(expected.guestPin(), actual.getGuestPin());
        assertEquals(expected.hostPin(), actual.getHostPin());
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());

        return true;
    }

    public static boolean assertUpdateMeeting(UpdateMeetingModel expected, UpdateMeeting actual) {
        assertEquals(expected.subject(), actual.getSubject());
        assertEquals(expected.startTime(), actual.getStartTime());
        assertEquals(expected.endTime(), actual.getEndTime());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.projectCode(), actual.getProjectCode());
        assertEquals(expected.organizedByEmail(), actual.getOrganizedByEmail());
        assertLabels(expected.labels(), actual.getLabels());
        assertAdditionalInformation(expected.additionalInformation(), actual.getAdditionalInformation());

        return true;
    }

    private static void assertMeetingUser(MeetingUserModel expected, MeetingUser actual) {
        assertEquals(expected.organisationId(), actual.getOrganisationId());
        assertEquals(expected.email(), actual.getEmail());
    }

    private static void assertGuestMicrophone(GuestMicrophoneModel expected, GuestMicrophone actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(expected.toString(), actual.toString());
        }
    }

    private static void assertVmrQuality(VmrQualityModel expected, VmrQuality actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(expected.toString(), actual.toString());
        }
    }

    private static void assertLabels(List<String> expected, List<String> actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i ++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private static void assertAdditionalInformation(List<AdditionalInformationModel> expected, List<AdditionalInformationType> actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i ++) {
            assertEquals(expected.get(i).key(), actual.get(i).getKey());
            assertEquals(expected.get(i).value(), actual.get(i).getValue());
        }
    }
}
