
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


## Drift
### Environment variables
Video api'et afvikles i docker. Følgende environment variable kan sættes op:

| Environment variable                                 | Beskrivelse                                                                                                                                                                 | Krævet / Default                                                              |
|------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| sessiondata_headername                               | Hvis denne er sat, vil video api'et lede efter sessiondata i HTTP request header af dette navn                                                                              | Ikke krævet / Ingen default                                                   |
| organisation.service.enabled                         | Hvis denne er sat til true kaldes Organisations Servicen for at hente organisations information.                                                                            | Ikke krævet. Default false.                                                   |
| organisation.service.endpoint                        | Endpoint URL på organisations servicen. F.eks. http://organisationfrontend:80/services                                                                                      | Ikke krævet. Skal være sat hvis organisation.service.enabled er sat til true. |
| short.link.base.url                                  | Base url to prefix short id with. F.eks. https://landing.video.dk/                                                                                                          | Krævet                                                                        |
| overflow.pool.organisation.id                        | Organisation id to use for pool overflow.                                                                                                                                   | Krævet                                                                        |                                                                      
| organisationtree.service.endpoint                    | Endpoint URL på organisation tree servicen. F.eks. http://organisationfrontend:80/services                                                                                  | Krævet                                                                        |                                                                     
| audit.nats.disabled                                  | Disable audit logging to nats.                                                                                                                                              | No                                                                            |                                                                    
| audit.nats.url                                       | Nats url to connect to                                                                                                                                                      | Yes                                                                           |
| audit.nats.subject                                   | Nats subject to publish to                                                                                                                                                  | Yes                                                                           |
| LOG_LEVEL_PERFORMANCE                                | Do performance logging. Set to INFO to do logging.                                                                                                                          | No  Defaults to WARNING                                                       |
| ALLOWED_ORIGINS                                      | List of allowed origins for CORS requests.                                                                                                                                  | Yes                                                                           |
| userservice.token.attribute.auto.create.organisation | Attribut der angiver hvilken parent organisation der automatisk skal oprettes organisation i.                                                                               | Yes                                                                           |
| events.nats.subject.scheduling-info                  | Nats subject for scheduling info events                                                                                                                                     | Yes                                                                           |
| event.organisation.filter                            | List of organisations to send events for                                                                                                                                    | Yes                                                                           |
| pool.fill.organisation.user                          | User to set as created by on pool scheduling info.                                                                                                                          | Yes                                                                           |
| pool.fill.organisation                               | Organisation to set as created by on pool scheduling info.                                                                                                                  | Yes                                                                           |
| pool.fill.interval                                   | Fixed delay in java.time.Duration format.                                                                                                                                   | Yes                                                                           |
| pool.fill.disabled                                   | Set to true if pool filling should be disabled                                                                                                                              | No                                                                            |
| baseline.flyway                                      | Set to true once when migrating Flyway from 6 to 10 to create new flyway baseline starting from version 86. Table flyway_schema_history should be renamed before migrating. | No                                                                            |