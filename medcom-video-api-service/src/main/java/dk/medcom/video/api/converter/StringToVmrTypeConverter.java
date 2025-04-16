package dk.medcom.video.api.converter;

import org.openapitools.model.VmrType;
import org.springframework.core.convert.converter.Converter;

public class StringToVmrTypeConverter implements Converter<String, VmrType> {
    @Override
    public VmrType convert(String source) {
        try {
            return VmrType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
