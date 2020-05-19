# medcom-video-api
## Databasemodel 
Video api'ets database er struktureret som følger
![databasemodel](/medcom-video-api-qa/docs/database.png)

## API dokumentation
API'ets snitflade dokumentation kan findes på swaggerhub:
https://app.swaggerhub.com/apis/Kvalitetsit/VDX-Booking-Module-API/0.4.0

## Test
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

| Environment variable       | Beskrivelse                                                                                    |           Tvunget / Default  |
| -------------------------- |------------------------------------------------------------------------------------------------| -----------------------------|
| sessiondata_headername     | Hvis denne er sat, vil video api'et lede efter sessiondata i HTTP request header af dette navn | Ikke tvunget/ Ingen default  |
|organisation.service.enabled| Hvis denne er sat til true kaldes Organisations Servicen for at hente organisations information.| Ikke tvungen. Default false. |
|organisation.service.endpoint| Endpoint URL på organisations servicen. F.eks. http://organisationfrontend:80/services         | Ikke tvungen. Skal være sat hvis organisation.service.enabled er sat til true. |
 |short.link.base.url        | Base url to prefix short id with. F.eks. https://landing.video.dk/ | Tvunget
 |baseline.flyway           | Set to true to create new new flyway baseline starting from version 40. Needed to Flyway 3 to 6 migration. | Ikke tvunget. 