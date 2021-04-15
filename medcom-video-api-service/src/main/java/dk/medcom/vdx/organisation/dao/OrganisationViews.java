package dk.medcom.vdx.organisation.dao;

import java.util.Optional;

public interface OrganisationViews {
    Optional<Long> getGroupIdFromLongLivedMeetingRooms(String uri);

    Optional<String> getOrganisationName(Long groupId);

    Optional<Long> getGroupIdFromRegisteredClients(String uri);

    Optional<Long> getGroupIdFromDomain(String domain);

    Optional<Long> getGroupIdFromDomainLike(String domain);

    Optional<String> getGroupName(Long groupId);
}
