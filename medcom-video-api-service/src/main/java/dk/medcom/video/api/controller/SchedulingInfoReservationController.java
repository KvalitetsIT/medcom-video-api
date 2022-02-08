package dk.medcom.video.api.controller;

import dk.medcom.video.api.api.SchedulingInfoDto;
import dk.medcom.video.api.api.ViewType;
import dk.medcom.video.api.api.VmrQuality;
import dk.medcom.video.api.api.VmrType;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.SchedulingInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SchedulingInfoReservationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoReservationController.class);

    private final SchedulingInfoService schedulingInfoService;
    private final String shortLinkBaseUrl;

    SchedulingInfoReservationController(SchedulingInfoService schedulingInfoService, @Value("${short.link.base.url}") String shortLinkBaseUrl) {
        this.schedulingInfoService = schedulingInfoService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
    }

    @APISecurityAnnotation({UserRole.ADMIN})
    @GetMapping(value = "/scheduling-info-reserve")
    public EntityModel<SchedulingInfoDto> reserveSchedulingInfo(
            @RequestParam(name = "vmrType", defaultValue = "conference") VmrType vmrType,
            @RequestParam(name = "hostView", defaultValue = "one_main_seven_pips") ViewType hostView,
            @RequestParam(name = "guestView", defaultValue = "one_main_seven_pips") ViewType guestView,
            @RequestParam(name = "vmrQuality", defaultValue = "hd") VmrQuality vmrQuality,
            @RequestParam(name = "enableOverlayText", defaultValue = "true") Boolean enableOverlayText,
            @RequestParam(name = "guestsCanPresent", defaultValue = "true") Boolean guestsCanPresent,
            @RequestParam(name = "forcePresenterIntoMain", defaultValue = "true") Boolean forcePresenterIntoMain,
            @RequestParam(name = "forceEncryption", defaultValue = "false") Boolean forceEncryption,
            @RequestParam(name = "muteAllGuests", defaultValue = "false") Boolean muteAllGuests) throws RessourceNotFoundException {
        LOGGER.debug("Entry of /scheduling-info-reserve.");

        SchedulingInfo schedulingInfo = schedulingInfoService.reserveSchedulingInfo(vmrType, hostView, guestView, vmrQuality, enableOverlayText, guestsCanPresent, forcePresenterIntoMain, forceEncryption, muteAllGuests);
        SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
        EntityModel<SchedulingInfoDto> resource = new EntityModel<>(schedulingInfoDto);

        LOGGER.debug("Exit of /scheduling-info-reserve");

        return resource;
    }

    @APISecurityAnnotation({UserRole.ADMIN})
    @GetMapping(value = "/scheduling-info-reserve/{reservationId}")
    public EntityModel<SchedulingInfoDto> getByReservationId(@PathVariable("reservationId") UUID reservationId) throws RessourceNotFoundException {
        LOGGER.debug("Entry of /scheduling-info-reserve/{reservationId}. Id: {}", reservationId);

        SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByReservation(reservationId);
        SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
        EntityModel<SchedulingInfoDto> resource = new EntityModel<>(schedulingInfoDto);

        LOGGER.debug("Exit of /scheduling-info-reserve/{reservationId}");

        return resource;
    }
}
