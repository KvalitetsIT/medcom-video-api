package dk.medcom.video.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public OffsetDateTimeSerializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (value == null) {
            throw new IOException("OffsetDateTime argument is null.");
        }
        jsonGenerator.writeString(DATE_TIME_FORMATTER.format(value));
    }
}

