package dk.medcom.video.api.controller.v2.mapper;

import org.openapitools.model.Info;
import org.openapitools.model.InfoType;

public class InfoMapper {

    public static Info internalToExternal(org.springframework.boot.actuate.info.Info input) {
        var info = new Info();

        var details = input.getDetails();

        for (var key : details.keySet()) {
            var infoItem = new InfoType(key, details.get(key));
            info.addInfoItem(infoItem);
        }

        return info;
    }
}
