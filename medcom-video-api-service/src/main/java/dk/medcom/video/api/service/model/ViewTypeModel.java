package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.ViewType;

public enum ViewTypeModel {
    one_main_zero_pips,
    one_main_seven_pips,
    one_main_twentyone_pips,
    two_mains_twentyone_pips,
    four_mains_zero_pips,
    five_mains_seven_pips,
    one_main_thirtythree_pips,
    nine_mains_zero_pips,
    sixteen_mains_zero_pips,
    twentyfive_mains_zero_pips;

    public static ViewTypeModel from(ViewType viewType) {
        if (viewType == null) {
            return null;
        }
        return ViewTypeModel.valueOf(viewType.toString());
    }
}
