package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import ug.daes.onboarding.dto.TitleDto;
import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.repository.PreferedTitlesRepo;
import ug.daes.onboarding.repository.SubscriberRepoIface;
import ug.daes.onboarding.service.iface.PreferredTitleIface;


import java.util.List;


@Service
public class PreferredTitleImpl implements PreferredTitleIface {
    private static final Logger logger = LoggerFactory.getLogger(PreferredTitleImpl.class);

    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private final SubscriberRepoIface subscriberRepoIface;
    private final PreferedTitlesRepo preferedTitlesRepol;


    public PreferredTitleImpl(ExceptionHandlerUtil exceptionHandlerUtil,
                              SubscriberRepoIface subscriberRepoIface,
                              PreferedTitlesRepo preferedTitlesRepol) {
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.subscriberRepoIface = subscriberRepoIface;
        this.preferedTitlesRepol = preferedTitlesRepol;
    }
    @Override
    public ApiResponse getPreferredTitles() {
        try{
            List<String> preferedTitlesList = preferedTitlesRepol.getPreferedTitles();
            logger.info("preferedTitlesList ::{}",preferedTitlesList);
            return exceptionHandlerUtil.createSuccessResponse("api.response.title.fetched",preferedTitlesList);

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
        return exceptionHandlerUtil.handleException(e);
    }

    }

    @Override
    public ApiResponse addUpdateTitle(TitleDto titleDto) {
        try {
            if (titleDto.getSuid() == null || titleDto.getSuid().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.suid.cantbe.null.or.empty");
            }

            OnbSubscriber subscriber = subscriberRepoIface.findBysubscriberUid(titleDto.getSuid());
            if (subscriber == null) {
                return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
            }

            if(titleDto.getTitle().equals("None")){
                subscriber.setTitle("");
                subscriberRepoIface.save(subscriber);
            }else{
                subscriber.setTitle(titleDto.getTitle());
                subscriberRepoIface.save(subscriber);
            }
            return exceptionHandlerUtil.successResponse("api.response.title.updated");
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            return exceptionHandlerUtil.handleException(e);
        }
    }
}
