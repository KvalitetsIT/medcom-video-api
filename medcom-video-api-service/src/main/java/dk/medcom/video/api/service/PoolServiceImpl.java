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

public class PoolServiceImpl implements PoolService {
    private final Logger logger = LoggerFactory.getLogger(PoolServiceImpl.class);
    private final PoolInfoService poolInfoService;
    private final SchedulingInfoService schedulingInfoService;
    private final MeetingUserRepository meetingUserRepository;
    private final OrganisationRepository organisationRepository;
    private final NewProvisionerOrganisationFilter newProvisionerOrganisationFilter;
    private final String poolOrganisation;
    private final String poolOrganisationUser;

    public PoolServiceImpl(PoolInfoService poolInfoService,
                           SchedulingInfoService schedulingInfoService,
                           MeetingUserRepository meetingUserRepository,
                           OrganisationRepository organisationRepository,
                           NewProvisionerOrganisationFilter newProvisionerOrganisationFilter,
                           String poolOrganisation,
                           String poolOrganisationUser) {
        this.poolInfoService = poolInfoService;
        this.schedulingInfoService = schedulingInfoService;
        this.meetingUserRepository = meetingUserRepository;
        this.organisationRepository = organisationRepository;
        this.newProvisionerOrganisationFilter = newProvisionerOrganisationFilter;
        this.poolOrganisation = poolOrganisation;
        this.poolOrganisationUser = poolOrganisationUser;
    }

    @Override
    @Transactional
    public void fillPools() {
        logger.info("Checking if pools rooms should be created!");
        var poolInfo = poolInfoService.getPoolInfo();

        poolInfo.forEach( x -> {
            logger.info("Handling pool for organisation {}. Ignoring? {}.", x.getOrganizationId(), !newProvisionerOrganisationFilter.newProvisioner(x.getOrganizationId()));
            if(newProvisionerOrganisationFilter.newProvisioner(x.getOrganizationId())) {
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

    private void fillPool(PoolInfoDto poolInfoDto) {
        var createSchedulingInfoDto = new CreateSchedulingInfoDto();
        createSchedulingInfoDto.setOrganizationId(poolInfoDto.getOrganizationId());
        createSchedulingInfoDto.setSchedulingTemplateId(poolInfoDto.getSchedulingTemplate().getTemplateId());

        try {
            MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationAndEmail(organisationRepository.findByOrganisationId(poolOrganisation), poolOrganisationUser);
            if (meetingUser == null) {
                meetingUser = new MeetingUser();
                meetingUser.setEmail(poolOrganisationUser);
                meetingUser.setOrganisation(organisationRepository.findByOrganisationId(poolOrganisation));
                meetingUser = meetingUserRepository.save(meetingUser);
            }
            schedulingInfoService.createSchedulingInfoWithCustomCreatedBy(createSchedulingInfoDto, meetingUser);
        } catch (NotValidDataException | NotAcceptableException e) {
            logger.error("Error creating scheduling info.", e);
        }
    }
}
