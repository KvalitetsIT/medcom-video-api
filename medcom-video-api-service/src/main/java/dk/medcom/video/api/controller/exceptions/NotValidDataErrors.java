package dk.medcom.video.api.controller.exceptions;

public enum NotValidDataErrors {

    NON_AD_HOC_ORGANIZATION(10, "Can not create ad hoc meeting on non ad hoc organization: %s"),
    SCHEDULING_INFO_NOT_FOUND_ORGANISATION(11, "Unused scheduling information not found for organisation tree for %s"),
    EXTERNAL_ID_NOT_UNIQUE(12, "ExternalId not unique within organisation."),
    ORGANISATION_ID_NOT_FOUND(13, "OrganisationId %s in request not found."),
    SCHEDULING_INFO_CAN_NOT_BE_CREATED(14, "Scheduling information can not be created on organisation %s that is not pool enabled."),
    UNABLE_TO_GENERATE_SHORT_ID(20, "Unable to generate unique shortId. Try again."),
    DATA_FORMAT_WRONG(30, "Date format is wrong, year must only have 4 digits"),
    SCHEDULING_TEMPLATE_NOT_FOUND(40, "Scheduling template %s not found."),
    SCHEDULING_TEMPLATE_NOT_IN_ORGANISATION(41, "Scheduling template %s does not belong to organisation %s."),
    BOTH_FROM_AND_TO_START_TIME_MUST_BE_PROVIDED_OR_NONE(50, "Either both from-start-time and to-start-time must be provided or none of them must be provided."),
    INVALID_RESERVATION_ID(60, "ReservationId not owned by organisation or not found."),
    NULL_VALUE(70, "Field can not be set to null")
    ;

    private final int errorCode;
    private final String errorText;

    NotValidDataErrors(int code, String text) {
        this.errorCode = code;
        this.errorText = text;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorText(String[] values) {
        return String.format(this.errorText, values);
    }
}
