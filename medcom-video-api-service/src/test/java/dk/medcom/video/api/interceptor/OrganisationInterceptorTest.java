package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class OrganisationInterceptorTest {
    private OrganisationStrategy organisationStrategy;

    @Mock
    private UserContextService userContextService;

    @InjectMocks
    private OrganisationInterceptor organisationInterceptor;

    private static final String ORG = "ORG";
    private OrganisationServiceClient organisationServiceClient;

    @BeforeEach
    public void setup() {
       organisationStrategy = Mockito.mock(OrganisationStrategy.class);
       organisationServiceClient = Mockito.mock(OrganisationServiceClient.class);

       organisationInterceptor = new OrganisationInterceptor(organisationStrategy, organisationServiceClient);

       MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExistingOrganisationNotCreated()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Organisation serviceOrganisation = new Organisation();
        serviceOrganisation.setCode(ORG);
        serviceOrganisation.setPoolSize(10);
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(serviceOrganisation);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verifyNoInteractions(organisationServiceClient);
    }

    @Test
    public void testOrganisationCreatedFromTemplate()  {
        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, "auto");
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(null);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verify(organisationServiceClient).createOrganisation(Mockito.eq(userContext.getAutoCreateOrganisation().get()), Mockito.argThat(x -> x.getCode().equals(ORG)));
    }

    @Test
    public void testNotFoundOrganisationNotCreatedWithoutAutoCreateTemplate()  {
        Mockito.when(organisationStrategy.findOrganisationByCode(ORG)).thenReturn(null);

        UserContext userContext = new UserContextImpl(ORG, "EMAIL", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        organisationInterceptor.preHandle(null, null, null);

        Mockito.verifyNoInteractions(organisationServiceClient);
    }
}
