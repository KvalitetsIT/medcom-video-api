package dk.medcom.video.api.service.impl;

import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import dk.medcom.video.api.service.SchedulingInfoEventPublisher;
import dk.medcom.video.api.service.domain.SchedulingInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class SchedulingInfoEventPublisherImpl implements SchedulingInfoEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingInfoEventPublisherImpl.class);
    private NatsPublisher natsPublisher;
    private final EntitiesIvrThemeDao entitiesIvrThemeDao;
    private final Function<String, Boolean> organisationFilter;

    public SchedulingInfoEventPublisherImpl(NatsPublisher natsPublisher, EntitiesIvrThemeDao entitiesIvrThemeDao, Function<String, Boolean> organisationFilter) {
        this.natsPublisher = natsPublisher;
        this.entitiesIvrThemeDao = entitiesIvrThemeDao;
        this.organisationFilter = organisationFilter;
    }

    @Override
    public void publishCreate(SchedulingInfoEvent schedulingInfoEvent) {
        if(!organisationFilter.apply(schedulingInfoEvent.getOrganisationCode())) {
            logger.info("Not publishing event due to organisation {} not configured for events.", schedulingInfoEvent.getOrganisationCode());
            return;
        }

        logger.info("Publishing scheduling info event to nats for uri with domain {}.", schedulingInfoEvent.getUriWithDomain());

        if(schedulingInfoEvent.getIvrTheme() != null && !schedulingInfoEvent.getIvrTheme().equals("0")) {
            var theme = entitiesIvrThemeDao.getTheme(schedulingInfoEvent.getIvrTheme());

            theme.ifPresent(x -> schedulingInfoEvent.setIvrThemeProvisionId(x.getProvisionId()));
        }

        try {
            natsPublisher.publishMessage(schedulingInfoEvent);
        }
        catch(IOException | InterruptedException | TimeoutException e) {
            logger.warn("Error publishing nats message", e);

            throw new MessagingException("Error during publish of event.", e);
        }
    }
}
