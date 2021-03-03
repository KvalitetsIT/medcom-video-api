package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.controller.exceptions.ResourceNotFoundException;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class OrganisationTreeControllerTest {

    private OrganisationTreeController organisationTreeController;
    private OrganisationTreeService organisationTreeService;

    @Before
    public void setup() {
        organisationTreeService = Mockito.mock(OrganisationTreeService.class);
        organisationTreeController = new OrganisationTreeController(organisationTreeService, new OrganisationTreeBuilderImpl());
    }

    @Test
    public void testGetOrganisationTree() {
        var input = "child";

        var child = createOrganisation("child", 13, 12, null);
        var childOne = createOrganisation("childOne", 12, 11, null);
        var parent = createOrganisation("parent", 11, 10, 20);
        var superParent = createOrganisation("superParent", 10, null, null);

        Mockito.when(organisationTreeService.findOrganisations(input)).thenReturn(Optional.of(Arrays.asList(child, childOne, parent, superParent)));

        var result = organisationTreeController.getOrganisationTree(input);
        assertNotNull(result);

        assertEquals(0, result.getPoolSize());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getOrganisationId(), result.getCode());
        assertEquals(0, result.getPoolSize());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().get(0);
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize());
        assertEquals(1, treeChild.getChildren().size());

        treeChild = treeChild.getChildren().get(0);
        assertEquals(childOne.getOrganisationName(), treeChild.getName());
        assertEquals(childOne.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize());
        assertEquals(1, treeChild.getChildren().size());

        treeChild = treeChild.getChildren().get(0);
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize());
        assertTrue(treeChild.getChildren().isEmpty());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testOrganisationNotFound() {
        var input = "not_found";
        Mockito.when(organisationTreeService.findOrganisations(input)).thenReturn(Optional.empty());
        organisationTreeController.getOrganisationTree(input);
    }

    private Organisation createOrganisation(String name, int groupId, Integer parentId, Integer poolSize) {
        var organisation = new Organisation();
        organisation.setGroupId((long) groupId);
        if(parentId != null) {
            organisation.setParentId(Long.valueOf(parentId));
        }
        if(poolSize != null) {
            organisation.setPoolSize(poolSize);
        }
        organisation.setOrganisationName(name);

        return organisation;
    }
}
