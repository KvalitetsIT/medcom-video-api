package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.organisation.model.Organisation;

import java.util.Optional;

public interface PoolFinderService {
     Optional<SchedulingInfo> findPoolSubject(Organisation organisation, CreateMeetingDto createMeetingDto);
}
