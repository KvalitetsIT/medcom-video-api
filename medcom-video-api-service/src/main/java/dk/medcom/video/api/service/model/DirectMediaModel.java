package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.DirectMedia;

public enum DirectMediaModel {
    never,
    best_effort;

    public static DirectMediaModel from(DirectMedia directMedia) {
        return DirectMediaModel.valueOf(directMedia.toString());
    }
}
