package dk.medcom.video.api.converter;

import org.openapitools.model.VmrQuality;
import org.springframework.core.convert.converter.Converter;

public class StringToVmrQualityConverter implements Converter<String, VmrQuality> {
    @Override
    public VmrQuality convert(String source) {
        try {
            return VmrQuality.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
