package dk.medcom.video.api.controller.exceptions;

public enum NotAcceptableErrors {

    MUST_HAVE_STATUS_AWAITS_PROVISION_OR_PROVISIONED_OK(10, "Meeting must have status AWAITS_PROVISION (0)  or PROVISIONED_OK (3) in order to be updated"),
    MUST_HAVE_STATUS_AWAITS_PROVISION(11, "Meeting must have status AWAITS_PROVISION (0) in order to be deleted"),
    URI_ASSIGNMENT_FAILED_INVALID_TEMPLATE_USED(20, "The Uri assignment failed due to invalid setup on the template used"),
    URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE(21, "The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template"),
    GUEST_PINCODE_ASSIGNMENT_FAILED(30, "The guest pincode assignment failed due to invalid setup on the template used"),
    HOST_PINCODE_ASSIGNMENT_FAILED(31, "The host pincode assignment failed due to invalid setup on the template used");

    private final int errorCode;
    private final String errorText;

    NotAcceptableErrors(int code, String text) {
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
