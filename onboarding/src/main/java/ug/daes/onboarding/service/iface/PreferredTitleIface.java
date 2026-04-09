package ug.daes.onboarding.service.iface;

import com.dtt.common.util.ApiResponse;
import ug.daes.onboarding.dto.TitleDto;

public interface PreferredTitleIface {

    ApiResponse getPreferredTitles();

    ApiResponse addUpdateTitle(TitleDto titleDto);
}
