package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.organisation.model.OrganisationTree;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrganisationFinderTest {
    private OrganisationFinder organisationFinder;

    @Before
    public void setup() {
        organisationFinder = new OrganisationFinder();
    }

    @Test
    public void testCurrentOrganisationIsParent() {
        var child = createOrganisation("child name", "child", null, 0);
        var childOne = createOrganisation("childOne name", "childOne", Collections.singletonList(child), 0);
        var parent = createOrganisation("parent name", "parent", Collections.singletonList(childOne), 10);
        var superParent = createOrganisation("superParent name", "superParent", Collections.singletonList(parent), 0);

        var result = organisationFinder.findPoolOrganisation("parent", superParent);
        assertNotNull(result);

        assertEquals(10, result.getPoolSize());
        assertEquals(parent.getName(), result.getName());
        assertEquals(parent.getCode(), result.getCode());
        assertEquals(1, result.getChildren().size());

    }

    @Test
    public void testFindParentWithPool() {
        var child = createOrganisation("child name", "child", null, 0);
        var childOne = createOrganisation("childOne name", "childOne", Collections.singletonList(child), 0);
        var parent = createOrganisation("parent name", "parent", Collections.singletonList(childOne), 10);
        var superParent = createOrganisation("superParent name", "superParent", Collections.singletonList(parent), 0);

        var result = organisationFinder.findPoolOrganisation("child", superParent);
        assertNotNull(result);

        assertEquals(10, result.getPoolSize());
        assertEquals(parent.getName(), result.getName());
        assertEquals(parent.getCode(), result.getCode());
        assertEquals(1, result.getChildren().size());
    }

    @Test
    public void testFindParentWithPoolMultipleChilds() {
        var child = createOrganisation("child name", "child", null, 0);
        var childOne = createOrganisation("childOne name", "childOne", Collections.singletonList(child), 0);
        var childTwo = createOrganisation("childTwo name", "childTwo", null, 20);
        var parent = createOrganisation("parent name", "parent", Arrays.asList(childTwo, childOne), 10);
        var superParent = createOrganisation("superParent name", "superParent", Collections.singletonList(parent), 0);

        var result = organisationFinder.findPoolOrganisation("child", superParent);
        assertNotNull(result);

        assertEquals(10, result.getPoolSize());
        assertEquals(parent.getName(), result.getName());
        assertEquals(parent.getCode(), result.getCode());
        assertEquals(2, result.getChildren().size());
    }

    private OrganisationTree createOrganisation(String name, String code, List<OrganisationTree> children, int poolSize) {
        var organisation = new OrganisationTree();
        organisation.setPoolSize(poolSize);
        organisation.setCode(code);
        organisation.setName(name);
        if(children != null) {
            organisation.getChildren().addAll(children);
        }

        return organisation;
    }
}
