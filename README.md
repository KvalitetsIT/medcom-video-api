# medcom-video-api
## Databasemodel 
Video api'ets database er struktureret som følger
![databasemodel](/medcom-video-api-qa/docs/database.png)

## API dokumenatation
API's dokumentation kan findes på swaggerhub:
https://app.swaggerhub.com/apis/Kvalitetsit/VDX-Booking-Module-API/0.4.0

## Test
### Unit test
Der vil løbende blive udviklet unit test til koden på formen: præcondition (Given), udførsel (When), tjek (Then)

Repository testene der kører mod en database gør brug af af docker containere til at starte en database op inden testene udføres.

### Code coverage
Til udregning af testcoverage anvendes Jacoco Maven Plugin. Testcoverage udregnes i de enkelte Maven moduler og aggregeres til en samlet rapport i modulet medcom-video-api-qa

Således er en samlet rapport over testcoverage tilgængelig efter kørsel af build og test i
./medcom-video-api-qa/target/site/jacoco-aggregate/index.html

### Integrations test
Integrations testen udføres fra Postman scripts. Se video infrastuktur projektet for dette:
https://github.com/KvalitetsIT/medcom-video-infrastructure/tree/master/postman
