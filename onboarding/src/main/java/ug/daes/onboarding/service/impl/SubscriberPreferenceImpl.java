package ug.daes.onboarding.service.impl;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.springframework.stereotype.Service;
import ug.daes.onboarding.dto.SubscriberPreferenceRequestDTO;
import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.model.OnbSubscriberPreferences;
import ug.daes.onboarding.repository.SubscriberPreferencesRepo;
import ug.daes.onboarding.repository.SubscriberRepoIface;
import ug.daes.onboarding.service.iface.SubscriberPreferencesIface;

@Service
public class SubscriberPreferenceImpl implements SubscriberPreferencesIface {
    private static final String API_ERROR_GENERIC =  "api.error.generic";
    private static final String API_ERROR_SUBSCRIBER_SUID_CANNOT_BE_NULL_OR_EMPTY =  "api.error.subscriber.suid.cant.be.null.or.empty";

    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private final SubscriberPreferencesRepo subscriberPreferencesRepo;
    private final SubscriberRepoIface subscriberRepoIface;

    public SubscriberPreferenceImpl(ExceptionHandlerUtil exceptionHandlerUtil,
                                    SubscriberPreferencesRepo subscriberPreferencesRepo,
                                    SubscriberRepoIface subscriberRepoIface) {
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.subscriberPreferencesRepo = subscriberPreferencesRepo;
        this.subscriberRepoIface = subscriberRepoIface;
    }

    @Override
    public ApiResponse saveLanguagePreference(SubscriberPreferenceRequestDTO subscriberPreferenceRequestDTO) {
        try {
            if (subscriberPreferenceRequestDTO.getSuid() == null || subscriberPreferenceRequestDTO.getSuid().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_SUID_CANNOT_BE_NULL_OR_EMPTY);
            }
            if (subscriberPreferenceRequestDTO.getLanguage() == null || subscriberPreferenceRequestDTO.getLanguage().isEmpty() ||
                    (!subscriberPreferenceRequestDTO.getLanguage().equalsIgnoreCase("en") && !subscriberPreferenceRequestDTO.getLanguage().equalsIgnoreCase("ar"))) {
                return exceptionHandlerUtil.createErrorResponse("api.error.language");

            }

            OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(subscriberPreferenceRequestDTO.getSuid());
            if (subscriber == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }

            OnbSubscriberPreferences subscriberPreferencesPresent = subscriberPreferencesRepo.getBySubUid(subscriberPreferenceRequestDTO.getSuid());

            if (subscriberPreferencesPresent != null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.preference.already.found");
            }


            OnbSubscriberPreferences subscriberPreferences = new OnbSubscriberPreferences();
            subscriberPreferences.setSuid(subscriberPreferenceRequestDTO.getSuid());
            subscriberPreferences.setLanguagePreferred(subscriberPreferenceRequestDTO.getLanguage());
            subscriberPreferences.setCreatedOn(AppUtil.getDate());
            subscriberPreferences.setUpdatedOn(AppUtil.getDate());
            subscriberPreferencesRepo.save(subscriberPreferences);
            return exceptionHandlerUtil.createSuccessResponse("api.response.subscriber.preferences.saved", null);

        } catch (Exception e) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_GENERIC);
        }
    }

    @Override
    public ApiResponse getPreferenceBySuid(String suid) {
        try {
            if (suid == null || suid.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_SUID_CANNOT_BE_NULL_OR_EMPTY);
            }

            OnbSubscriberPreferences subscriberPreferences = subscriberPreferencesRepo.getBySubUid(suid);
            if (subscriberPreferences == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.preference.empty");

            }
            return exceptionHandlerUtil.createSuccessResponse("api.response.subscriber.preference.found", subscriberPreferences);
        } catch (Exception e) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_GENERIC);
        }
    }

    @Override
    public ApiResponse updateLanguagePreference(SubscriberPreferenceRequestDTO subscriberPreferenceRequestDTO) {
        try {
            if (subscriberPreferenceRequestDTO.getSuid() == null || subscriberPreferenceRequestDTO.getSuid().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(API_ERROR_SUBSCRIBER_SUID_CANNOT_BE_NULL_OR_EMPTY);
            }
            if (subscriberPreferenceRequestDTO.getLanguage() == null || subscriberPreferenceRequestDTO.getLanguage().isEmpty() ||
                    (!subscriberPreferenceRequestDTO.getLanguage().equalsIgnoreCase("en") && !subscriberPreferenceRequestDTO.getLanguage().equalsIgnoreCase("ar"))) {
                return exceptionHandlerUtil.createErrorResponse("api.error.language");

            }

            OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(subscriberPreferenceRequestDTO.getSuid());
            if (subscriber == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }

            OnbSubscriberPreferences subscriberPreferencesPresent = subscriberPreferencesRepo.getBySubUid(subscriberPreferenceRequestDTO.getSuid());

            if (subscriberPreferencesPresent == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.preference.not.found");
            }

            subscriberPreferencesPresent.setLanguagePreferred(subscriberPreferenceRequestDTO.getLanguage());
            subscriberPreferencesRepo.save(subscriberPreferencesPresent);
            return exceptionHandlerUtil.createSuccessResponse("api.response.subscriber.preferences.updated", subscriberPreferencesPresent);

        } catch (Exception e) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_GENERIC);
        }
    }
}