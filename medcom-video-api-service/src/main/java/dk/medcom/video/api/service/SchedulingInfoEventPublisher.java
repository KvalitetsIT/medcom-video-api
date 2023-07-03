package dk.medcom.video.api.service;

import dk.medcom.video.api.service.domain.SchedulingInfoEvent;

public interface SchedulingInfoEventPublisher {
    void publishEvent(SchedulingInfoEvent schedulingInfoEvent);
}
