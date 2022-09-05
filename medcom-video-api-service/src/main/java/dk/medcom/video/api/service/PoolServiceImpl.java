package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateSchedulingInfoDto;
import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

public class PoolServiceImpl implements PoolService {
    private Logger logger = LoggerFactory.getLogger(PoolServiceImpl.class);
    private final PoolInfoService poolInfoService;
    private final SchedulingInfoService schedulingInfoService;
    private final MeetingUserRepository meetingUserRepository;
    private final OrganisationRepository organisationRepository;
    private final Function<String, Boolean> organisationFilter;

    public PoolServiceImpl(PoolInfoService poolInfoService,
                           SchedulingInfoService schedulingInfoService,
                           MeetingUserRepository meetingUserRepository,
                           OrganisationRepository organisationRepository,
                           Function<String, Boolean> organisationFilter) {
        this.poolInfoService = poolInfoService;
        this.schedulingInfoService = schedulingInfoService;
        this.meetingUserRepository = meetingUserRepository;
        this.organisationRepository = organisationRepository;
        this.organisationFilter = organisationFilter;
    }

    @Override
    @Transactional
    public void fillPools() {
        logger.info("Checking if pools rooms should be created!");
        var poolInfo = poolInfoService.getPoolInfo();

        poolInfo.forEach( x -> {
            logger.info("Handling pool for organisation {}. Ignoring? {}.", x.getOrganizationId(), organisationFilter.apply(x.getOrganizationId()));
            if(organisationFilter.apply(x.getOrganizationId())) {
                logger.info("Organisation wants {} pool rooms and {} is available.", x.getDesiredPoolSize(), x.getAvailablePoolSize());
                if(x.getDesiredPoolSize() > x.getAvailablePoolSize()) {
                    logger.info("Filling pool for {}.", x.getOrganizationId());
                    fillPool(x);
                }
                else {
                    logger.info("No need for additional pool rooms for {}.", x.getOrganizationId());
                }
            }
            else {
                logger.debug("Not filling pool for as organisation is filtered out.");
            }
        });
    }

    private void fillPool(PoolInfoDto x) {
        var createSchedulingInfoDto = new CreateSchedulingInfoDto();
        createSchedulingInfoDto.setOrganizationId(x.getOrganizationId());
        createSchedulingInfoDto.setSchedulingTemplateId(x.getSchedulingTemplate().getTemplateId());

        try {
            MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationAndEmail(organisationRepository.findByOrganisationId("medcom"), "medcom@medcom.dk"); // TODO Flyt til parameter.
            if (meetingUser == null) {
                meetingUser = new MeetingUser();
                meetingUser.setEmail("medcom@medcom.dk");
                meetingUser.setOrganisation(organisationRepository.findByOrganisationId("medcom"));
                meetingUser = meetingUserRepository.save(meetingUser);
            }
            schedulingInfoService.createSchedulingInfoWithCustomCreatedBy(createSchedulingInfoDto, meetingUser);
        } catch (NotValidDataException | NotAcceptableException e) {
            logger.error("Error creating scheduling info.", e);
        }
    }
}
