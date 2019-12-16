package dk.medcom.video.api.dto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
    private List<String> validDateFormats = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss Z",
                                                          "yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();

        ParseException parseException = null;
        for (String format : validDateFormats) {
            try {
                return dateFromString(value, format);
            }
            catch(ParseException p) {
                parseException = p;
            }
        }

        throw new JsonParseException(jsonParser, parseException.getMessage(), parseException);
    }

    private Date dateFromString(String dateTime, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.parse(dateTime);
    }
}
