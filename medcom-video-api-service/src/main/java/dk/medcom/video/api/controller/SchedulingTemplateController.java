package dk.medcom.video.api.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.CreateSchedulingTemplateDto;
import dk.medcom.video.api.dto.SchedulingTemplateDto;
import dk.medcom.video.api.dto.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.service.SchedulingTemplateService;

@RestController
public class SchedulingTemplateController {

	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingTemplateController.class);

	@Autowired
	SchedulingTemplateService schedulingTemplateService;
	
	@RequestMapping(value = "/scheduling-templates", method = RequestMethod.GET)
	public Resources <SchedulingTemplateDto> getSchedulingTemplates() throws PermissionDeniedException  {
		LOGGER.debug("Entry of /scheduling-templates.get");
		
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateService.getSchedulingTemplates();
		List<SchedulingTemplateDto> schedulingTemplateDtos = new LinkedList<SchedulingTemplateDto>();
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			SchedulingTemplateDto schedulingTemplateDto = new SchedulingTemplateDto(schedulingTemplate);
			schedulingTemplateDtos.add(schedulingTemplateDto);
		}
		Resources<SchedulingTemplateDto> resources = new Resources<>(schedulingTemplateDtos);
		
		Link selfRelLink = linkTo(methodOn(SchedulingTemplateController.class).getSchedulingTemplates()).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /scheduling-templates.get resources: " + resources.toString());
		return resources;

	}
	@RequestMapping(value = "/scheduling-templates/{id}", method = RequestMethod.GET)
	public Resource <SchedulingTemplateDto> getSchedulingTemplateById(@PathVariable("id") Long id) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Entry of /scheduling-templates.get id: " + id);
		
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(id); 
		SchedulingTemplateDto schedulingTemplateDto = new SchedulingTemplateDto(schedulingTemplate);
		Resource <SchedulingTemplateDto> resource = new Resource <SchedulingTemplateDto>(schedulingTemplateDto);
		
		LOGGER.debug("Exit of /scheduling-template.get resource: " + resource);
		return resource;

	}
	
	@APISecurityAnnotation({UserRole.ADMIN})
	@RequestMapping(value = "/scheduling-templates", method = RequestMethod.POST)
	public Resource <SchedulingTemplateDto> createSchedulingTemplate(@Valid @RequestBody CreateSchedulingTemplateDto createSchedulingTemplateDto) throws PermissionDeniedException {
		LOGGER.debug("Entry of /scheduling-template.post");
		
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.createSchedulingTemplate(createSchedulingTemplateDto);
		SchedulingTemplateDto schedulingTemplateDto = new SchedulingTemplateDto(schedulingTemplate);
		Resource <SchedulingTemplateDto> resource = new Resource <SchedulingTemplateDto>(schedulingTemplateDto);
		
		LOGGER.debug("Exit of /scheduling-template.post resource: " + resource);
		return resource;

	}	
	@APISecurityAnnotation({UserRole.ADMIN})
	@RequestMapping(value = "/scheduling-templates/{id}", method = RequestMethod.PUT)
	public Resource <SchedulingTemplateDto> updateSchedulingTemplate(@PathVariable("id") Long id, @Valid @RequestBody UpdateSchedulingTemplateDto updateSchedulingTemplateDto ) throws PermissionDeniedException, RessourceNotFoundException  {
	
		LOGGER.debug("Entry of /scheduling-template.put id: " + id);
		
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.updateSchedulingTemplate(id, updateSchedulingTemplateDto);
		SchedulingTemplateDto schedulingTemplateDto = new SchedulingTemplateDto(schedulingTemplate);
		Resource <SchedulingTemplateDto> resource = new Resource <SchedulingTemplateDto>(schedulingTemplateDto);
		
		LOGGER.debug("Exit of /scheduling-template.put resource: " + resource);
		return resource;

	}
	
//	@APISecurityAnnotation({UserRole.ADMIN}) //TODO: skal der laves slet?
//	@RequestMapping(value = "/scheduling-templates/{id}", method = RequestMethod.DELETE)
//	public Resource <SchedulingTemplateDto> deleteSchedulingTemplate(@PathVariable("id") String id)  {
//		LOGGER.debug("Entry of /schedulingTemplate.delete id: " + id);
//		
////		meetingService.deleteMeeting(uuid);
//
//		LOGGER.debug("Exit of /scheduling-template.delete");
//		return null;
//	}

	
}
