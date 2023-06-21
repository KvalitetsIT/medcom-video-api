package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.dao.OrganisationRepository;
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
    private OrganisationServiceClient organisationServiceClient;

    @Before
    public void setup() {
       organisationRepository = Mockito.mock(OrganisationRepository.class);
       organisationStrategy = Mockito.mock(OrganisationStrategy.class);
       organisationServiceClient = Mockito.mock(OrganisationServiceClient.class);

       organisationInterceptor = new OrganisationInterceptor(organisationStrategy, organisationRepository, organisationServiceClient);

       MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateOrganisationInDatabase()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(serviceOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        ArgumentCaptor<dk.medcom.video.api.dao.entity.Organisation> dbOrganisationCaptor = ArgumentCaptor.forClass(dk.medcom.video.api.dao.entity.Organisation.class);
        Mockito.verify(organisationRepository).save(dbOrganisationCaptor.capture());

        dk.medcom.video.api.dao.entity.Organisation dbOrganisation = dbOrganisationCaptor.getValue();
        assertEquals(ORG, dbOrganisation.getOrganisationId());
    }

    @Test
    public void testExistingOrganisationNotCreatedInDatabase()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(serviceOrganisation);

        dk.medcom.video.api.dao.entity.Organisation dbOrganisation = new dk.medcom.video.api.dao.entity.Organisation();
        dbOrganisation.setOrganisationId(ORG);
        Mockito.when(organisationRepository.findByOrganisationId(ORG)).thenReturn(dbOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verify(organisationRepository, Mockito.never()).save(Mockito.any(dk.medcom.video.api.dao.entity.Organisation.class));
    }

    @Test
    public void testOrganisationCreatedFromTempalte()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, "auto");
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(null);
        Mockito.when(organisationServiceClient.createOrganisation(Mockito.eq(userContext.getAutoCreateOrganisation().get()), Mockito.argThat(x -> x.getCode().equals(userContext.getUserOrganisation())))).thenReturn(serviceOrganisation);

        dk.medcom.video.api.dao.entity.Organisation dbOrganisation = new dk.medcom.video.api.dao.entity.Organisation();
        dbOrganisation.setOrganisationId(ORG);
        Mockito.when(organisationRepository.findByOrganisationId(ORG)).thenReturn(dbOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verify(organisationRepository, Mockito.never()).save(Mockito.any(dk.medcom.video.api.dao.entity.Organisation.class));
    }

    @Test
    public void testNotFoundOrganisationNotCreated()  {
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(null);

        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verifyNoMoreInteractions(organisationRepository);
    }
}
