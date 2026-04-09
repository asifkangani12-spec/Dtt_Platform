package com.dtt.common.util;

import com.dtt.common.constants.ErrorCode;
import com.dtt.common.constants.ErrorCodeException;
import com.dtt.common.exception.OrgnizationServiceException;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import java.util.Locale;

@Component
public class ExceptionHandlerUtil {

    private static final String CLASS = ExceptionHandlerUtil.class.getSimpleName();
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerUtil.class);

    private final MessageSource messageSource;


    public ExceptionHandlerUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }




    /**
     * Handle generic exceptions and return formatted ApiResponse
     */
    public ApiResponse handleException(Exception e) {

        Locale locale = LocaleContextHolder.getLocale();
        String errorCode = ErrorCodeException.GENERIC_ERROR.getCode();
        String messageKey = "api.error.something.went.wrong.please.try.after.sometime";

        if (e instanceof JDBCConnectionException) {

            errorCode = ErrorCodeException.CONNECTION_ERROR.getCode();
            messageKey = "api.error.database.connection";

            logger.error("{} - {} : Database connection error occurred: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());

        } else if (e instanceof ConstraintViolationException
                || e instanceof DataException
                || e instanceof LockAcquisitionException
                || e instanceof PessimisticLockException
                || e instanceof QueryTimeoutException
                || e instanceof SQLGrammarException
                || e instanceof GenericJDBCException) {

            errorCode = ErrorCodeException.DATABASE_ERROR.getCode();
            messageKey = "api.error.database";

            logger.error("{} - {} : Database-related error occurred: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());

        } else {

            logger.error("{} - {} : Unexpected error occurred: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
        }

        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        String formattedMessage =
                String.format("%s [ErrorCode: %s]", errorMessage, errorCode);

        logger.info("{} - {} : Returning error response: errorCode={}, message={}",
                CLASS, Utility.getMethodName(), errorCode, formattedMessage);

        return createErrorResponse(errorCode, formattedMessage);
    }

    /**
     * RestTemplate error handler
     */
    public ApiResponse handleErrorRestTemplateResponse(int statusCode) {

        Locale locale = LocaleContextHolder.getLocale();

        return switch (statusCode) {

            case 500 -> AppUtil.createApiResponse(
                    false,
                    messageSource.getMessage("api.error.internal.server.error", null, locale),
                    statusCode
            );

            case 400 -> AppUtil.createApiResponse(
                    false,
                    messageSource.getMessage("api.error.bad.request", null, locale),
                    null
            );

            case 401, 403 -> AppUtil.createApiResponse(
                    false,
                    messageSource.getMessage("api.error.forbidden", null, locale),
                    null
            );

            case 408 -> AppUtil.createApiResponse(
                    false,
                    messageSource.getMessage("api.error.request.timeout", null, locale),
                    null
            );

            default -> AppUtil.createApiResponse(
                    false,
                    messageSource.getMessage(
                            "api.error.something.went.wrong.please.try.after.sometime",
                            null,
                            locale
                    ),
                    null
            );
        };
    }

    /**
     * Success response with localized message key
     */
    public ApiResponse createSuccessResponse(String successMessageKey, Object result) {

        Locale locale = LocaleContextHolder.getLocale();
        String successMsg = messageSource.getMessage(successMessageKey, null, locale);

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage(successMsg);
        response.setResult(result);

        return response;
    }

    /**
     * Success response with custom message (no localization)
     */
    public ApiResponse createSuccessResponseWithCustomMessage(String message, Object result) {

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setResult(result);

        return response;
    }

    public ApiResponse createFailedResponseWithCustomMessage(String message, Object result) {

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setResult(result);

        return response;
    }

    public ApiResponse successResponse(String successMessageKey) {

        Locale locale = LocaleContextHolder.getLocale();
        String successMsg = messageSource.getMessage(successMessageKey, null, locale);

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage(successMsg);
        response.setResult(null);

        return response;
    }

    /**
     * Error response with localized message key
     */
    public ApiResponse createErrorResponse(String messageKey) {

        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        logger.error("Error response created with message: {}", errorMessage);

        return AppUtil.createApiResponse(false, errorMessage, null);
    }


    public ApiResponse createErrorResponse(String errorCode, String message) {

        return AppUtil.createApiResponse(false, message, errorCode);
    }

    public ApiResponse createErrorResponseWithResult(String messageKey, Object result) {

        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(messageKey, null, locale);

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        response.setResult(result);

        return response;
    }

    public ApiResponse handleResponse(ResponseEntity<ApiResponse> res) {

        if (res == null) {
            return buildGenericError();
        }

        int statusCode = res.getStatusCodeValue();

        if (statusCode == HttpStatus.OK.value()
                || statusCode == HttpStatus.CREATED.value()) {

            return res.getBody() != null
                    ? res.getBody()
                    : buildGenericError();
        }

        String errCode = ErrorCode.getOrDefault(
                String.valueOf(res.getStatusCode().value()),
                "UNKNOWN_ERROR"
        );

        return AppUtil.createApiResponse(
                false,
                MessageSourceHolder.getMessage(
                        "api.error.with.code",
                        errCode
                ),
                null
        );
    }

    public ApiResponse handleHttpException(HttpStatusCodeException e) {

        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());

        String errCode = ErrorCode.getOrDefault(
                String.valueOf(status.value()),
                "UNKNOWN_ERROR"
        );
        return AppUtil.createApiResponse(
                false,
                MessageSourceHolder.getMessage(
                        "api.error.http",
                        status,
                        errCode
                ),
                null
        );
    }

    public ApiResponse handleResourceAccessException(ResourceAccessException e) {

        return AppUtil.createApiResponse(
                false,
                MessageSourceHolder.getMessage("api.error.network"),
                null
        );
    }

    public static ApiResponse handleGenericException(Exception e) {

        return AppUtil.createApiResponse(
                false,
                MessageSourceHolder.getMessage("api.error.generic"),
                null
        );
    }

    private ApiResponse buildGenericError() {
        return AppUtil.createApiResponse(
                false,
                MessageSourceHolder.getMessage("api.error.generic"),
                null
        );
    }

    public OrgnizationServiceException orgnizationServiceException(String messageKey) {
        String message = messageSource.getMessage(messageKey, null, Locale.ENGLISH);
        return new OrgnizationServiceException(message);
    }
}
