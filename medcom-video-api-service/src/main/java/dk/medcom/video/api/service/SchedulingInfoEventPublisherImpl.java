package dk.medcom.video.api.service;

import dk.kvalitetsit.audit.client.messaging.MessagePublisher;
import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import dk.medcom.video.api.service.domain.SchedulingInfoEvent;
import io.nats.client.JetStreamApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SchedulingInfoEventPublisherImpl implements SchedulingInfoEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingInfoEventPublisherImpl.class);
    private final MessagePublisher natsPublisher;
    private final EntitiesIvrThemeDao entitiesIvrThemeDao;

    public SchedulingInfoEventPublisherImpl(MessagePublisher natsPublisher, EntitiesIvrThemeDao entitiesIvrThemeDao) {
        this.natsPublisher = natsPublisher;
        this.entitiesIvrThemeDao = entitiesIvrThemeDao;
    }

    @Override
    public void publishEvent(SchedulingInfoEvent schedulingInfoEvent, boolean newProvisioner) {
        if(!newProvisioner) {
            logger.info("Not publishing event due to new_provisioner is false. Organisation is: {}.", schedulingInfoEvent.getOrganisationCode());
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
        catch(IOException | JetStreamApiException e) {
            logger.warn("Error publishing nats message", e);

            throw new MessagingException("Error during publish of event.", e);
        }
    }
}
