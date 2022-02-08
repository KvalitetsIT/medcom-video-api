package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MeetingService {
    List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) throws PermissionDeniedException;

    Meeting getMeetingByShortId(String shortId) throws RessourceNotFoundException, PermissionDeniedException;

    List<Meeting> getMeetingsBySubject(String subject) throws PermissionDeniedException;

    List<Meeting> getMeetingsByOrganizedBy(String organizedBy) throws PermissionDeniedException;

    List<Meeting> getMeetingsByUriWithDomain(String uriWithDomain) throws PermissionDeniedException;

    List<Meeting> searchMeetings(String search, Date fromStartTime, Date toStartTime) throws PermissionDeniedException;

    Meeting getMeetingsByUriWithDomainSingle(String uriWithDomain) throws PermissionDeniedException, RessourceNotFoundException;

    Meeting getMeetingsByUriWithoutDomain(String uriWithoutDomain) throws PermissionDeniedException, RessourceNotFoundException;

    List<Meeting> getMeetingsByLabel(String label) throws PermissionDeniedException;

    Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException;

    Meeting createMeeting(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException;

    @Transactional(rollbackFor = Throwable.class)
    Meeting updateMeeting(String uuid, UpdateMeetingDto updateMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException;

    void deleteMeeting(String uuid) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException;

    @Transactional
    Meeting patchMeeting(UUID uuid, PatchMeetingDto patchMeetingDto) throws PermissionDeniedException, NotValidDataException, RessourceNotFoundException, NotAcceptableException;

    Meeting convert(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotValidDataException;
}
