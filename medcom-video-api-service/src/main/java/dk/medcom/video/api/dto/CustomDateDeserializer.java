package dk.medcom.video.api.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
    private List<String> validDateFormats = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss Z",
                                                          "yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();

        DateTimeParseException parseException = null;
        for (String format : validDateFormats) {
            try {
                return dateFromString(value, format);
            }
            catch(DateTimeParseException p) {
                parseException = p;
            }

        }
        throw parseException;
    }

    private Date dateFromString(String startTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(startTime, formatter.withZone(ZoneId.of("Europe/Copenhagen")));
        Instant instant = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toInstant();

        return new Date(instant.toEpochMilli());
    }
}
