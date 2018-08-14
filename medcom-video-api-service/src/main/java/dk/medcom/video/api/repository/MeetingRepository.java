package dk.medcom.video.api.repository;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Meeting;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

}
