package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;

import java.util.Optional;

public interface PoolFinderService {
     Optional<SchedulingInfo> findPoolSubject(Organisation organisation, CreateMeetingDto createMeetingDto);
}
