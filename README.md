
![Build Status](https://github.com/KvalitetsIT/medcom-video-api/workflows/Java%20CI%20with%20Maven/badge.svg) 


# medcom-video-api
## Databasemodel 
Video api'ets database er struktureret som følger
![databasemodel](/medcom-video-api-qa/docs/database.png)

## API dokumentation
API'ets snitflade dokumentation kan findes på swaggerhub:
https://api-docs.vconf.dk/videoapi/v1/videoapi/

## Test7
### Unit test
Der udvikles løbende unit test til koden på formen: præcondition (Given), udførsel (When), tjek (Then)

Repository testene, der kører mod en database, gør brug af af docker containere til at starte en database op inden testene udføres. I medcom-video-api-service modules findes et sql script til indlæsning af unit test data (V901__insert _test_data.sql)

### Code coverage
Til udregning af testcoverage anvendes Jacoco Maven Plugin. Testcoverage udregnes i de enkelte Maven moduler og aggregeres til en samlet rapport i modulet medcom-video-api-qa

Således er en samlet rapport over testcoverage tilgængelig efter kørsel af build og test i
./medcom-video-api-qa/target/site/jacoco-aggregate/index.html

### Integrations test
Integrations testen udføres fra Postman scripts. Se video infrastuktur projektet for dette:
https://github.com/KvalitetsIT/medcom-video-infrastructure/tree/master/postman

For at køre disse scripts mod udviklings versionen i f.eks. eclipse, køres klassen TestApplication.java i MVN modulet medcom-video-api-test. User context sættes inden i klassen TestUserContext i samme modul. User Context har indvirkning på, hvilke data der kan oprettes, hentes og rettes. I samme modul findes også script til test data V901__insert _test_data.sql, som indlæses i en tom database når TestApplication.java køres.

## Udfasning af v1

### Ændringer i v2
I v2 bliver timestamps håndteret anderledes. Ved query requests går man således fra `2019-10-02T14:01:00+%2B0000` til `2019-10-02T14:01:00Z`.

Det er i output ændret fra `2019-10-02T13:00:00 +0000` til `2019-10-02T15:00:00+02:00`.


#### Adgang
V2 anvender Oauth2, og i stedet for SAML attributes skal der derfor være claims i medsendte JWT token. 
Navnene er genanvendt, således at følgende claims børe være til stede i et token:
* userservice.token.attribute.organisation
* userservice.token.attribute.email
* userservice.token.attribute.userrole

Ift. userservice.token.attribute.userrole, så skelnes der nu mellem om man er meeting-provisioner eller 
meeting-provisioner-user, dvs. hvis et endpoint kræver, at man er provision-user, så er det ikke længere nok at være 
provisioner og sende mail + organisation med, man skal eksplicit have attributten/claimet meeting-provisioner-user.

Værdierne af userservice.token.attribute.userrole er ikke konfigurable. Der er følgende mulige:
* meeting-admin: Admin scope
* meeting-user: User scope
* meeting-planner: Meeting Planner scope
* meeting-provisioner: Provisioner scope
* meeting-provisioner-user: Provisioner User scope

## Drift
### Environment variables
Video api'et afvikles i docker. Følgende environment variable kan sættes op:

| Environment variable                                  | Beskrivelse                                                                                                                                                                 | Krævet / Default             |
|-------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| LOG_LEVEL                                             | Log Level for applikation log.                                                                                                                                              | No / Default: "INFO"         |
| LOG_LEVEL_FRAMEWORK                                   | Log level for framework.                                                                                                                                                    | No / Default: "INFO"         |
| LOG_LEVEL_PERFORMANCE                                 | Do performance logging. Set to INFO to do logging.                                                                                                                          | No / Default: "WARNING"      |
| CORRELATION_ID                                        | HTTP header to take correlation id from. Used to correlate log messages.                                                                                                    | No / Default: "x-request-id" | 
| SESSION.ID                                            | Session id.                                                                                                                                                                 | No / Default: "SESSION"      |
| sessiondata.headername                                | Hvis denne er sat, vil video api'et lede efter sessiondata i HTTP request header af dette navn.                                                                             | No / Default : ""            |
| CONTEXT                                               | Angiver SERVER_SERVLET_CONTEXT_PATH.                                                                                                                                        | No / Default: "/"            |
| ALLOWED_ORIGINS                                       | List of allowed origins for CORS requests.                                                                                                                                  | Yes                          |
| jdbc.url                                              | JDBC connection URL.                                                                                                                                                        | Yes                          |
| jdbc.user                                             | JDBC user.                                                                                                                                                                  | Yes                          |
| jdbc.pass                                             | JDBC password.                                                                                                                                                              | Yes                          |
| jdbc.driverClassName                                  | JDBC driver class name.                                                                                                                                                     | No                           |
| audit.nats.disabled                                   | Disable audit logging to nats.                                                                                                                                              | No                           |
| audit.nats.url                                        | Nats URL to connect to. Related to audit service.                                                                                                                           | Yes                          |
| audit.nats.subject                                    | Nats subject to publish to. Related to audit service.                                                                                                                       | Yes                          |
| events.nats.subject.scheduling-info                   | Nats subject to publish scheduling-info events to.                                                                                                                          | Yes                          |
| userservice.url                                       | URL til userservice, kaldes hvis ikke sessiondata er med i header.                                                                                                          | Yes                          |
| userservice.token.attribute.organisation              | Navn på attribut, der angiver, hvilken organisation brugeren tilhører.                                                                                                      | Yes                          |
| userservice.token.attribute.username                  | Navn på attribut, der angiver brugernavn på brugeren.                                                                                                                       | Yes                          |
| userservice.token.attribute.email                     | Navn på attribut, der angiver email på brugeren.                                                                                                                            | Yes                          |
| userservice.token.attribute.userrole                  | Navn på attribut, der angiver userroles en bruger har.                                                                                                                      | Yes                          |
| userservice.token.attribute.auto.create.organisation  | Navn på attribut, der angiver hvilken parent organisation der automatisk skal oprettes organisation i.                                                                      | Yes                          |
| scheduling.template.default.conferencing.sys.id       | Default værdi for scheduling template conferencing_sys_id, long.                                                                                                            | Yes                          |
| scheduling.template.default.uri.prefix                | Default værdi for scheduling template uri_prefix, string.                                                                                                                   | Yes                          |
| scheduling.template.default.uri.domain                | Default værdi for scheduling template uri_domain, string.                                                                                                                   | Yes                          |
| scheduling.template.default.host.pin.required         | Default værdi for scheduling template host_pin_required, boolean.                                                                                                           | Yes                          |
| scheduling.template.default.host.pin.range.low        | Default værdi for scheduling template host_pin_range_low, long.                                                                                                             | Yes                          |
| scheduling.template.default.host.pin.range.high       | Default værdi for scheduling template host_pin_range_high, long.                                                                                                            | Yes                          |
| scheduling.template.default.guest.pin.required        | Default værdi for scheduling template guest_pin_required, boolean.                                                                                                          | Yes                          |
| scheduling.template.default.guest.pin.range.low       | Default værdi for scheduling template guest_pin_range_low, long.                                                                                                            | Yes                          |
| scheduling.template.default.guest.pin.range.high      | Default værdi for scheduling template guest_pin_range_high, long.                                                                                                           | Yes                          |
| scheduling.template.default.vmravailable.before       | Default værdi for scheduling template vmr_available_before, int.                                                                                                            | Yes                          |
| scheduling.template.default.max.participants          | Default værdi for scheduling template max_participants, int.                                                                                                                | Yes                          |
| scheduling.template.default.end.meeting.on.end.time   | Default værdi for scheduling template end_meeting_on_end_time, boolean.                                                                                                     | Yes                          |
| scheduling.template.default.uri.number.range.low      | Default værdi for scheduling template uri_number_range_low, long.                                                                                                           | Yes                          |
| scheduling.template.default.uri.number.range.high     | Default værdi for scheduling template uri_number_range_high, long.                                                                                                          | Yes                          |
| scheduling.template.default.ivr.theme                 | Default værdi for scheduling template ivr_theme, string.                                                                                                                    | Yes                          |
| scheduling.info.citizen.portal                        | URL til citizen portal link.                                                                                                                                                | Yes                          |
| mapping.role.provisioner                              | Navn på provisioner role.                                                                                                                                                   | Yes                          |
| mapping.role.admin                                    | Navn på admin role                                                                                                                                                          | Yes                          |
| mapping.role.user                                     | Navn på user role                                                                                                                                                           | Yes                          |
| mapping.role.meeting_planner                          | Navn på meeting-planner role.                                                                                                                                               | Yes                          |
| organisation.service.enabled                          | Hvis denne er sat til true kaldes Organisationsservicen for at hente organisationsinformation.                                                                              | No / Default: "false"        |
| organisation.service.endpoint                         | Endpoint URL på organisations servicen. Påkrævet, hvis organisation.service.enabled er sat til 'true'.                                                                      | Depends                      |
| organisationtree.service.endpoint                     | Endpoint URL på organisation tree servicen.                                                                                                                                 | Yes                          |
| short.link.base.url                                   | Base URL to prefix short id with.                                                                                                                                           | Yes                          |
| overflow.pool.organisation.id                         | Organisation id to use for pool overflow.                                                                                                                                   | Yes                          |
| event.organisation.filter                             | Liste af organisationer, der anvender ny provisioner.                                                                                                                       | No / Default: tom liste      |
| pool.fill.disabled                                    | Set to true if pool filling should be disabled.                                                                                                                             | No / Default: "false"        |
| pool.fill.organisation.user                           | User to set as created by on pool scheduling info.                                                                                                                          | Yes                          |
| pool.fill.organisation                                | Organisation to set as created by on pool scheduling info.                                                                                                                  | Yes                          |
| pool.fill.interval                                    | Fixed delay in java.time.Duration format.                                                                                                                                   | Yes                          |
| pool.meeting.minimumAgeSec                            | Minimum age of possible pool meetings.                                                                                                                                      | No / 60                      |
| spring.security.oauth2.resourceserver.jwt.issuer-uri  | Keycloak path + /realms/<KEYCLOAK_REALM>                                                                                                                                    | Yes                          |
| spring.security.oauth2.resourceserver.jwt.jwk-set-uri | Keycloak path + /realms/<KEYCLOAK_REALM>/protocol/openid-connect/certs                                                                                                      | No                           |
| baseline.flyway                                       | Set to true once when migrating Flyway from 6 to 10 to create new flyway baseline starting from version 86. Table flyway_schema_history should be renamed before migrating. | No                           |