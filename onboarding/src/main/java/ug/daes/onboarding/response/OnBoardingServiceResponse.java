package ug.daes.onboarding.response;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class OnBoardingServiceResponse {

    private final MessageSource messageSource;

    public OnBoardingServiceResponse(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ApiResponse createApiResponse(boolean success, String message, Object result) {
        return AppUtil.createApiResponse(success, messageSource.getMessage(message, null, Locale.ENGLISH), result);
    }
}
