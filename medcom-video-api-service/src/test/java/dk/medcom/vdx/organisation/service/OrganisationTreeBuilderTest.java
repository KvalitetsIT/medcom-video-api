package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class OrganisationTreeBuilderTest {
    private OrganisationTreeBuilder organisationTreeBuilder;

    @Before
    public void setup() {
        organisationTreeBuilder = new OrganisationTreeBuilderImpl();
    }

    @Test
    public void testBuildTree() {
        var child = createOrganisation("child", 13, 12, null, "child_code");
        var childOne = createOrganisation("childOne", 12, 11, null, null);
        var childTwo = createOrganisation("childTwo", 14, 11, null, null);
        var parent = createOrganisation("parent", 11, 10, 20, "parent_code");
        var superParent = createOrganisation("superParent", 10, null, null, null);

        var result = organisationTreeBuilder.buildOrganisationTree(Arrays.asList(childOne, child, superParent, parent, childTwo));
        assertNotNull(result);

        assertEquals(0, result.getPoolSize());
        assertEquals(superParent.getOrganisationName(), result.getName());
        assertEquals(superParent.getGroupId().toString(), result.getCode());
        assertEquals(0, result.getPoolSize());
        assertEquals(1, result.getChildren().size());

        var treeChild = result.getChildren().get(0);
        assertEquals(parent.getOrganisationName(), treeChild.getName());
        assertEquals(parent.getOrganisationId(), treeChild.getCode());
        assertEquals(20, treeChild.getPoolSize());
        assertEquals(2, treeChild.getChildren().size());

        var childOneTree = treeChild.getChildren().get(0);
        assertEquals(childOne.getOrganisationName(), childOneTree.getName());
        assertEquals(childOne.getGroupId().toString(), childOneTree.getCode());
        assertEquals(0, childOneTree.getPoolSize());
        assertEquals(1, childOneTree.getChildren().size());

        var childTwoTree = treeChild.getChildren().get(1);
        assertEquals(childTwo.getOrganisationName(), childTwoTree.getName());
        assertEquals(childTwo.getGroupId().toString(), childTwoTree.getCode());
        assertEquals(0, childTwoTree.getPoolSize());
        assertEquals(0, childTwoTree.getChildren().size());

        treeChild = childOneTree.getChildren().get(0);
        assertEquals(child.getOrganisationName(), treeChild.getName());
        assertEquals(child.getOrganisationId(), treeChild.getCode());
        assertEquals(0, treeChild.getPoolSize());
        assertTrue(treeChild.getChildren().isEmpty());
    }

    @Test
    public void testBuildEmptyTree() {
        var result = organisationTreeBuilder.buildOrganisationTree(Collections.emptyList());
        assertNull(result);
    }

    @Test
    public void testBuildNullTree() {
        var result = organisationTreeBuilder.buildOrganisationTree(null);
        assertNull(result);
    }


    private Organisation createOrganisation(String name, int groupId, Integer parentId, Integer poolSize, String organisationId) {
        var organisation = new Organisation();
        organisation.setGroupId((long) groupId);
        if(parentId != null) {
            organisation.setParentId(Long.valueOf(parentId));
        }
        if(poolSize != null) {
            organisation.setPoolSize(poolSize);
        }
        organisation.setOrganisationName(name);
        organisation.setOrganisationId(organisationId);

        return organisation;
    }

}
