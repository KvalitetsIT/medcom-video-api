package dk.medcom.video.api.converter;

import org.openapitools.model.ViewType;
import org.springframework.core.convert.converter.Converter;

public class StringToViewTypeConverter implements Converter<String, ViewType> {
    @Override
    public ViewType convert(String source) {
        try {
            return ViewType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
