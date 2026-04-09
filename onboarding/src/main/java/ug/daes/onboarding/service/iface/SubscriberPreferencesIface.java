package ug.daes.onboarding.service.iface;

import com.dtt.common.util.ApiResponse;
import ug.daes.onboarding.dto.SubscriberPreferenceRequestDTO;

public interface SubscriberPreferencesIface {

    ApiResponse saveLanguagePreference(SubscriberPreferenceRequestDTO subscriberPreferenceRequestDTO);
    ApiResponse getPreferenceBySuid(String suid);

    ApiResponse updateLanguagePreference(SubscriberPreferenceRequestDTO subscriberPreferenceRequestDTO);
}
