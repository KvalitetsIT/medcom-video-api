package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.organisation.Organisation;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.assertEquals;

public class OrganisationInterceptorTest {
    private OrganisationStrategy organisationStrategy;

    private OrganisationRepository organisationRepository;

    @Mock
    private UserContextService userContextService;

    @InjectMocks
    private OrganisationInterceptor organisationInterceptor;

    private static final String ORG = "ORG";

    @Before
    public void setup() {
       organisationRepository = Mockito.mock(OrganisationRepository.class);
       organisationStrategy = Mockito.mock(OrganisationStrategy.class);

       organisationInterceptor = new OrganisationInterceptor(organisationStrategy, organisationRepository);

       MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateOrganisationInDatabase()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(serviceOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        ArgumentCaptor<dk.medcom.video.api.dao.Organisation> dbOrganisationCaptor = ArgumentCaptor.forClass(dk.medcom.video.api.dao.Organisation.class);
        Mockito.verify(organisationRepository).save(dbOrganisationCaptor.capture());

        dk.medcom.video.api.dao.Organisation dbOrganisation = dbOrganisationCaptor.getValue();
        assertEquals(ORG, dbOrganisation.getOrganisationId());
    }

    @Test
    public void testExistingOrganisationNotCreatedInDatabase()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(serviceOrganisation);

        dk.medcom.video.api.dao.Organisation dbOrganisation = new dk.medcom.video.api.dao.Organisation();
        dbOrganisation.setOrganisationId(ORG);
        Mockito.when(organisationRepository.findByOrganisationId(ORG)).thenReturn(dbOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verify(organisationRepository, Mockito.never()).save(Mockito.any(dk.medcom.video.api.dao.Organisation.class));
    }

    @Test
    public void testNotFoundOrganisationNotCreated()  {
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(null);

        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verifyNoMoreInteractions(organisationRepository);
    }
}
