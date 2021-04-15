package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrganisationByUriService {
    private final SchedulingInfoRepository schedulingInfoRepository;
    private final OrganisationViews organisationViews;

    public OrganisationByUriService(SchedulingInfoRepository schedulingInfoRepository,
                                   OrganisationViews organisationViews) {
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.organisationViews = organisationViews;
    }

    public Map<String, Organisation> getOrganisationByUriWithDomain(List<String> uri) {
        // Schedulerede møderum
        Map<String, Organisation> result = getOrganisationFromSchedulingInfo(uri);
        List<String> uriWithNoMatch = uri.stream().filter(x -> !result.containsKey(x)).collect(Collectors.toList());

        if (!uriWithNoMatch.isEmpty()){
            // Faste møderum
            result.putAll(getOrganisationFromLongLivedMeetingRooms(uriWithNoMatch));
            uriWithNoMatch = uri.stream().filter(x -> !result.containsKey(x)).collect(Collectors.toList());

            if (!uriWithNoMatch.isEmpty()){
                // Registrerede klienter
                result.putAll(getOrganisationFromRegisteredClients(uriWithNoMatch));
                uriWithNoMatch = uri.stream().filter(x -> !result.containsKey(x)).collect(Collectors.toList());

                if (!uriWithNoMatch.isEmpty()){
                    // Domæner
                    result.putAll(getOrganisationFromDomain(uriWithNoMatch));
                }
            }
        }

        addGroupName(result);
        return result;
    }

    private void addGroupName(Map<String, Organisation> result) {
        for (Map.Entry<String, Organisation> org : result.entrySet()) {
            Optional<String> groupName = organisationViews.getGroupName(org.getValue().getGroupId());
            groupName.ifPresent(name -> org.getValue().setGroupName(name));
        }
    }

    private Map<String, Organisation> getOrganisationFromSchedulingInfo(List<String> uri){
        List<SchedulingInfo> dbResult = schedulingInfoRepository.findAllByUriWithDomainAndProvisionStatusOk(uri, ProvisionStatus.PROVISIONED_OK);

        Map<String, Organisation> result = new HashMap<>();
        for (SchedulingInfo schedulingInfo : dbResult) {
            var org = new Organisation();
            org.setGroupId(schedulingInfo.getOrganisation().getGroupId());
            org.setOrganisationId(schedulingInfo.getOrganisation().getOrganisationId());
            org.setOrganisationName(schedulingInfo.getOrganisation().getName());

            result.put(schedulingInfo.getUriWithDomain(), org);
        }
        return result;
    }

    private Map<String, Organisation> getOrganisationFromLongLivedMeetingRooms(List<String> uris){
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            Optional<Long> groupId = organisationViews.getGroupIdFromLongLivedMeetingRooms(uri);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                Optional<String> organisationName = organisationViews.getOrganisationName(groupId.get());
                organisationName.ifPresent(organisation::setOrganisationName);

                result.put(uri, organisation);
            }
        }
        return result;
    }

    private Map<String,Organisation> getOrganisationFromRegisteredClients(List<String> uris) {
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            Optional<Long> groupId = organisationViews.getGroupIdFromRegisteredClients(uri);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                Optional<String> organisationName = organisationViews.getOrganisationName(groupId.get());
                organisationName.ifPresent(organisation::setOrganisationName);

                result.put(uri, organisation);
            }
        }
        return result;
    }

    private Map<String, Organisation> getOrganisationFromDomain(List<String> uris) {
        Map<String, Organisation> result = new HashMap<>();
        for (String uri : uris) {
            String domain = uri.substring(uri.indexOf('@') + 1).toLowerCase();

            Optional<Long> groupId = organisationViews.getGroupIdFromDomain(domain);

            if (groupId.isPresent()){
                Organisation organisation = new Organisation();
                organisation.setGroupId(groupId.get());

                Optional<String> organisationName = organisationViews.getOrganisationName(groupId.get());
                organisationName.ifPresent(organisation::setOrganisationName);

                result.put(uri, organisation);
            }else {
                groupId = organisationViews.getGroupIdFromDomainLike(domain);

                if (groupId.isPresent()){
                    Organisation organisation = new Organisation();
                    organisation.setGroupId(groupId.get());

                    Optional<String> organisationName = organisationViews.getOrganisationName(groupId.get());
                    organisationName.ifPresent(organisation::setOrganisationName);

                    result.put(uri, organisation);
                }
            }
        }
        return result;
    }
}
