package dk.medcom.video.api.service.exception;

import dk.medcom.video.api.controller.exceptions.NotAcceptableErrors;
import dk.medcom.video.api.controller.exceptions.NotValidDataErrors;
import org.openapitools.model.DetailedError;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMapper {
    private static final Map<Integer, DetailedError.DetailedErrorCodeEnum> notAcceptableErrorsToDetailedError = new HashMap<>();
    private static final Map<Integer, DetailedError.DetailedErrorCodeEnum> notValidDataErrorsToDetailedError = new HashMap<>();

    static {
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION_OR_PROVISIONED_OK.getErrorCode(), DetailedError.DetailedErrorCodeEnum._12);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION.getErrorCode(), DetailedError.DetailedErrorCodeEnum._13);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_INVALID_TEMPLATE_USED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._14);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE.getErrorCode(), DetailedError.DetailedErrorCodeEnum._15);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.GUEST_PINCODE_ASSIGNMENT_FAILED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._16);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.HOST_PINCODE_ASSIGNMENT_FAILED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._17);
        notAcceptableErrorsToDetailedError.put(NotAcceptableErrors.CREATE_OR_UPDATE_POOL_TEMPLATE_FAILED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._18);

        notValidDataErrorsToDetailedError.put(NotValidDataErrors.NON_AD_HOC_ORGANIZATION.getErrorCode(), DetailedError.DetailedErrorCodeEnum._19);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.SCHEDULING_INFO_NOT_FOUND_ORGANISATION.getErrorCode(), DetailedError.DetailedErrorCodeEnum._20);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.EXTERNAL_ID_NOT_UNIQUE.getErrorCode(), DetailedError.DetailedErrorCodeEnum._21);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.ORGANISATION_ID_NOT_FOUND.getErrorCode(), DetailedError.DetailedErrorCodeEnum._22);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.SCHEDULING_INFO_CAN_NOT_BE_CREATED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._23);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.UNABLE_TO_GENERATE_SHORT_ID.getErrorCode(), DetailedError.DetailedErrorCodeEnum._24);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.DATA_FORMAT_WRONG.getErrorCode(), DetailedError.DetailedErrorCodeEnum._25);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_FOUND.getErrorCode(), DetailedError.DetailedErrorCodeEnum._26);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_IN_ORGANISATION.getErrorCode(), DetailedError.DetailedErrorCodeEnum._27);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.BOTH_FROM_AND_TO_START_TIME_MUST_BE_PROVIDED_OR_NONE.getErrorCode(), DetailedError.DetailedErrorCodeEnum._28);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.INVALID_RESERVATION_ID.getErrorCode(), DetailedError.DetailedErrorCodeEnum._29);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.NULL_VALUE.getErrorCode(), DetailedError.DetailedErrorCodeEnum._30);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.CUSTOM_MEETING_ADDRESS_NOT_ALLOWED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._31);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.URI_ALREADY_USED.getErrorCode(), DetailedError.DetailedErrorCodeEnum._32);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.URI_IS_INVALID.getErrorCode(), DetailedError.DetailedErrorCodeEnum._33);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.SCHEDULING_INFO_RESERVATION_PIN_COMBINATION.getErrorCode(), DetailedError.DetailedErrorCodeEnum._34);
        notValidDataErrorsToDetailedError.put(NotValidDataErrors.ADDITIONAL_INFO_KEYS_NOT_UNIQUE_FOR_MEETING.getErrorCode(), DetailedError.DetailedErrorCodeEnum._35);
    }

    public static DetailedError.DetailedErrorCodeEnum fromNotAcceptable(int notAcceptableErrorCode) {
        return notAcceptableErrorsToDetailedError.get(notAcceptableErrorCode);
    }

    public static DetailedError.DetailedErrorCodeEnum fromNotValidData(int notValidDataErrorCode) {
        return notValidDataErrorsToDetailedError.get(notValidDataErrorCode);
    }
}
