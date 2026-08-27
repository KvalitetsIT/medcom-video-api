package dk.medcom.video.api.serializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public OffsetDateTimeSerializer() { super(OffsetDateTime.class); }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator jsonGenerator, SerializationContext serializationContext) throws JacksonException {

        if (value == null) {
            throw new NullPointerException("OffsetDateTime argument is null.");
        }

        jsonGenerator.writeString(DATE_TIME_FORMATTER.format(value));
    }
}
