package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateSchedulingInfoDto;
import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class PoolServiceImpl implements PoolService {
    private final Logger logger = LoggerFactory.getLogger(PoolServiceImpl.class);
    private final SchedulingInfoService schedulingInfoService;
    private final MeetingUserRepository meetingUserRepository;
    private final OrganisationRepository organisationRepository;
    private final NewProvisionerOrganisationFilter newProvisionerOrganisationFilter;
    private final String poolOrganisation;
    private final String poolOrganisationUser;

    public PoolServiceImpl(SchedulingInfoService schedulingInfoService,
                           MeetingUserRepository meetingUserRepository,
                           OrganisationRepository organisationRepository,
                           NewProvisionerOrganisationFilter newProvisionerOrganisationFilter,
                           String poolOrganisation,
                           String poolOrganisationUser) {
        this.schedulingInfoService = schedulingInfoService;
        this.meetingUserRepository = meetingUserRepository;
        this.organisationRepository = organisationRepository;
        this.newProvisionerOrganisationFilter = newProvisionerOrganisationFilter;
        this.poolOrganisation = poolOrganisation;
        this.poolOrganisationUser = poolOrganisationUser;
    }

    @Override
    @Transactional
    public void fillOrDeletePool(PoolInfoDto poolInfo) {
        logger.info("Handling pool for organisation {}. Ignoring? {}.", poolInfo.getOrganizationId(), !newProvisionerOrganisationFilter.newProvisioner(poolInfo.getOrganizationId()));
        if (newProvisionerOrganisationFilter.newProvisioner(poolInfo.getOrganizationId())) {
            logger.info("Organisation wants {} pool rooms and {} is available.", poolInfo.getDesiredPoolSize(), poolInfo.getAvailablePoolSize());
            if (poolInfo.getDesiredPoolSize() > poolInfo.getAvailablePoolSize()) {
                logger.info("Filling pool for {}.", poolInfo.getOrganizationId());
                fillPool(poolInfo);
            } else if (poolInfo.getDesiredPoolSize() < poolInfo.getAvailablePoolSize()) {
                logger.info("Too many pool rooms for {}.", poolInfo.getOrganizationId());
                deleteInPool(poolInfo);
            } else {
                logger.info("No need for additional or fewer pool rooms for {}.", poolInfo.getOrganizationId());
            }
        } else {
            logger.debug("Not filling pool for as organisation is filtered out.");
        }
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

    private void deleteInPool(PoolInfoDto x) {

        for (int i = x.getDesiredPoolSize(); i < x.getAvailablePoolSize(); i++) {
            try {
                schedulingInfoService.deleteSchedulingInfoPool(x.getSchedulingInfoList().get(i).getUuid());
            } catch (RessourceNotFoundException e) {
                logger.error("Error deleting scheduling info with uuid {}", x.getSchedulingInfoList().get(i).getUuid(), e);
            }
        }
    }
}
