package dk.medcom.video.api.api;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.core.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CustomDateDeserializer extends StdDeserializer<Date> {
    private final List<String> validDateFormats = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss Z",
                                                          "yyyy-MM-dd'T'HH:mm:ssZ",
                                                          "yyyy-MM-dd'T'HH:mm:ss X",
                                                          "yyyy-MM-dd'T'HH:mm:ssXXX");
    protected CustomDateDeserializer() { super(Date.class); }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException {
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
        throw deserializationContext.weirdStringException(value, Date.class, parseException != null  ? parseException.getMessage() : "Unable to parse date");
    }

    private Date dateFromString(String dateTime, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.parse(dateTime);
    }
}
