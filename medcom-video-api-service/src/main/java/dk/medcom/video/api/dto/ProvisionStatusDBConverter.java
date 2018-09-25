package dk.medcom.video.api.dto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProvisionStatusDBConverter implements AttributeConverter<ProvisionStatus, Integer> {
	
    @Override
    public ProvisionStatus convertToEntityAttribute(Integer databaseValue) {
        return ProvisionStatus.getProvisionStatus(databaseValue);
    }
    
	@Override
	public Integer convertToDatabaseColumn(ProvisionStatus provisionStatus) {
        return provisionStatus.getValue();
	}

}