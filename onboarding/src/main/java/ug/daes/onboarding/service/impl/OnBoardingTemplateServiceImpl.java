package ug.daes.onboarding.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import ug.daes.onboarding.constant.Constant;
import ug.daes.onboarding.dto.EditTemplateDTO;
import ug.daes.onboarding.dto.MobileTemplateDTO;
import ug.daes.onboarding.dto.SubscriberDTO;
import ug.daes.onboarding.dto.TemplateApproveDTO;
import ug.daes.onboarding.dto.TemplateDTO;
import ug.daes.onboarding.exceptions.ApplicationException;
import ug.daes.onboarding.model.OnbMapMethodOnboardingStep;

import ug.daes.onboarding.model.OnbSubscriberOnboardingTemplate;
import ug.daes.onboarding.model.OnboardingMethod;
import ug.daes.onboarding.model.OnboardingSteps;
import ug.daes.onboarding.repository.MapMethodObStepRepoIface;
import ug.daes.onboarding.repository.OnBoardingMethodRepoIface;
import ug.daes.onboarding.repository.OnBoardingStepRepoIface;
import ug.daes.onboarding.repository.OnBoardingTemplateRepoIface;
import ug.daes.onboarding.service.iface.TemplateServiceIface;

@Service
public class OnBoardingTemplateServiceImpl implements TemplateServiceIface {

	private static final Logger logger = LoggerFactory.getLogger(OnBoardingTemplateServiceImpl.class);


	private static final String CLASS = "OnBoardingTemplateServiceImpl";
	private static final String EXCEPTION = "Unexpected exception";
	private static final String TEMPLATE_NOT_FOUND = "api.error.template.not.found";
	private static final String PUBLISHED = "PUBLISHED";
	private static final String UNPUBLISHED = "UNPUBLISHED";
	private static final String RESPONSE_TEMPLATE = "api.response.template";
	private final OnBoardingMethodRepoIface methodRepoIface;
	private final OnBoardingTemplateRepoIface templateRepoIface;
	private final OnBoardingStepRepoIface stepRepoIface;
	private final MapMethodObStepRepoIface mapStepRepoIface;

	private final ExceptionHandlerUtil exceptionHandlerUtil;
	private final OnBoardingTemplateRepoIface templateRepo;

	public OnBoardingTemplateServiceImpl(
			OnBoardingMethodRepoIface methodRepoIface,
			OnBoardingTemplateRepoIface templateRepoIface,
			OnBoardingStepRepoIface stepRepoIface,
			MapMethodObStepRepoIface mapStepRepoIface,

			ExceptionHandlerUtil exceptionHandlerUtil,
			OnBoardingTemplateRepoIface templateRepo) {

		this.methodRepoIface = methodRepoIface;
		this.templateRepoIface = templateRepoIface;
		this.stepRepoIface = stepRepoIface;
		this.mapStepRepoIface = mapStepRepoIface;

		this.exceptionHandlerUtil = exceptionHandlerUtil;
		this.templateRepo = templateRepo;
	}

	@Override
	public ApiResponse getTemplates() {
		List<OnbSubscriberOnboardingTemplate> templates = new ArrayList<>();

		try {
			templates = templateRepoIface.getAllTemplate();

			if (templates != null) {
				logger.info(CLASS + " getTemplates res {}", templates);
				return exceptionHandlerUtil.createSuccessResponse("api.response.template.list", templates);
			} else {
				return exceptionHandlerUtil.successResponse("api.response.template.list.is.empty");
			}
		} catch (Exception e) {
			logger.error(CLASS + " getTemplates Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public ApiResponse getActiveTemplate(SubscriberDTO subscriberDTO) {
		logger.info(CLASS + " SubscriberDTO received: {}", subscriberDTO);
		logger.info(CLASS + " getActiveTemplate req {}", subscriberDTO.getMethodName());
		OnbSubscriberOnboardingTemplate template = new OnbSubscriberOnboardingTemplate();
		EditTemplateDTO templateDTO = new EditTemplateDTO();
		try {
			if (subscriberDTO.getMethodName() != null) {
				template = templateRepoIface.getPublishTemplate(subscriberDTO.getMethodName(), PUBLISHED);

				List<OnbMapMethodOnboardingStep> stepList = mapStepRepoIface.findBytemplateId(template.getTemplateId());

				HashMap<String, OnbMapMethodOnboardingStep> hm = new HashMap<>();
                stepList.forEach(mapMethodOnboardingStep ->
                        hm.put(mapMethodOnboardingStep.getOnboardingStep(), mapMethodOnboardingStep)
                );

				templateDTO.setSteps(hm);
				templateDTO.setTemplateName(template.getTemplateName());
				templateDTO.setTemplateMethod(template.getTemplateMethod());
				templateDTO.setPublishedStatus(template.getPublishedStatus());
				templateDTO.setState(template.getState());
				templateDTO.setTemplateId(template.getTemplateId());

                logger.info(CLASS + " getActviteTemplate res Template {}", templateDTO);
                return exceptionHandlerUtil.createSuccessResponse(RESPONSE_TEMPLATE, templateDTO);
            } else {
				return exceptionHandlerUtil.createErrorResponse("api.error.method.name.is.empty");
			}
		} catch (Exception e) {
			logger.error(CLASS + " getActviteTemplate Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}
	@Override
	public ApiResponse saveTemplates(TemplateDTO templateDTO) {
		logger.info(CLASS + " saveTemplates req {}", templateDTO.getTemplateName());

		try {

			if (isTemplateExists(templateDTO)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.template.methodname.alreday.exist");
			}

            OnbSubscriberOnboardingTemplate template = prepareTemplate(templateDTO);

			template.setPublishedStatus(UNPUBLISHED);
			template = templateRepoIface.save(template);



			logger.info(CLASS + " saveTemplates  res  Template Saved  {}", template);

			return exceptionHandlerUtil.createSuccessResponse("api.response.template.saved", template);

		} catch (Exception e) {
			logger.error(CLASS + " saveTemplates Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}
	private boolean isTemplateExists(TemplateDTO templateDTO) {
		int count = templateRepo.isTemplateExistWithMethod(
				templateDTO.getTemplateName(),
				templateDTO.getTemplateMethod()
		);
		return count > 0;
	}
	private OnbSubscriberOnboardingTemplate prepareTemplate(TemplateDTO templateDTO) {

		OnbSubscriberOnboardingTemplate template = new OnbSubscriberOnboardingTemplate();

		template.setTemplateName(templateDTO.getTemplateName());
		template.setTemplateMethod(templateDTO.getTemplateMethod());
		template.setCreatedDate(AppUtil.getDate());
		template.setUpatedDate(AppUtil.getDate());
		template.setApprovedBy(templateDTO.getApprovedBy());

		if (templateDTO.getTemplateId() == 0) {
			template.setCreatedBy(templateDTO.getCreatedBy());
		} else {
			handleTemplateUpdate(templateDTO, template);
		}

		return template;
	}
	private void handleTemplateUpdate(TemplateDTO templateDTO, OnbSubscriberOnboardingTemplate template) {

		OnbSubscriberOnboardingTemplate templateStatus =
				templateRepoIface.findBytemplateId(templateDTO.getTemplateId());

		template.setTemplateId(templateDTO.getTemplateId());

		if (templateStatus != null) {

            if (PUBLISHED.equals(templateStatus.getPublishedStatus())) {
                throw new ApplicationException(
                        "api.response.your.template.status.is.published.please.unpublished.it.before.making.any.modifications"
                );
            } else {
				template.setUpdatedBy(templateDTO.getUpdatedBy());

				for (@SuppressWarnings("unused") OnboardingSteps steps : templateDTO.getSteps()) {
					mapStepRepoIface.deleteBytemplateId(templateDTO.getTemplateId());
				}
			}

		} else {

			template.setUpdatedBy(templateDTO.getUpdatedBy());

			for (@SuppressWarnings("unused") OnboardingSteps steps : templateDTO.getSteps()) {
				mapStepRepoIface.deleteBytemplateId(templateDTO.getTemplateId());
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	public ApiResponse getTemplateById(int id) {
		logger.info(CLASS + " getTemplateById req  id {}", id);
		OnbSubscriberOnboardingTemplate template = new OnbSubscriberOnboardingTemplate();
		MobileTemplateDTO templateDTO = new MobileTemplateDTO();
		try {
			template = templateRepoIface.findBytemplateId(id);

			List<OnbMapMethodOnboardingStep> stepList = mapStepRepoIface.findBytemplateId(template.getTemplateId());

			for (OnbMapMethodOnboardingStep mapMethodOnboardingStep : stepList) {

				if (mapMethodOnboardingStep.getOnboardingStep().equals("SELFIE_CAPTURING")) {
					mapMethodOnboardingStep.setOnboardingStepId(1);
				} else if (mapMethodOnboardingStep.getOnboardingStep().equals("MRZ_SCANNING")) {
					mapMethodOnboardingStep.setOnboardingStepId(2);
				} else if (mapMethodOnboardingStep.getOnboardingStep().equals("PDF417_READING")) {
					mapMethodOnboardingStep.setOnboardingStepId(3);
				} else if (mapMethodOnboardingStep.getOnboardingStep().equals("NFC")) {
					mapMethodOnboardingStep.setOnboardingStepId(4);
				} else if (mapMethodOnboardingStep.getOnboardingStep().equals("UNID")) {
					mapMethodOnboardingStep.setOnboardingStepId(5);
				}
			}
			logger.info(CLASS + " getTemplateById req stepList {}", stepList);
			templateDTO.setSteps(stepList);
			templateDTO.setTemplateName(template.getTemplateName());
			templateDTO.setTemplateMethod(template.getTemplateMethod());
			templateDTO.setPublishedStatus(template.getPublishedStatus());
			templateDTO.setState(template.getState());
			templateDTO.setTemplateId(template.getTemplateId());

            logger.info(CLASS + " getTemplateById res  Template by Id {}", templateDTO);
            return exceptionHandlerUtil.createSuccessResponse("api.response.template.by.id", templateDTO);
        } catch (Exception e) {
			logger.error(CLASS + "getTemplateById  Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getMethods() {

		List<OnboardingMethod> methods = new ArrayList<>();

		try {
			methods = methodRepoIface.findAll();
            logger.info(CLASS + " getMethod res Method List {}", methods);
            return exceptionHandlerUtil.createSuccessResponse("api.response.method.list", methods);
        } catch (Exception e) {
			logger.error(CLASS + " getMethod Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse getOnBoardingSteps() {
		List<OnboardingSteps> steps = new ArrayList<>();

		try {
			steps = stepRepoIface.findAll();

            logger.info(CLASS + " getOnBoardingStep res List of Steps {}", steps);
            return exceptionHandlerUtil.createSuccessResponse("api.response.list.of.steps", steps);
        } catch (Exception e) {
			logger.error(CLASS + " getOnBoardingSteps Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

	@Override
	public ApiResponse updateTemplateStatus(int id, String status) {
		logger.info(CLASS + " updateTemplateStatus req id  {} and  status {} ", id, status);
		OnbSubscriberOnboardingTemplate template = templateRepoIface.findBytemplateId(id);
		try {

			if (status.equals(template.getPublishedStatus())) {
				return exceptionHandlerUtil.createErrorResponse("api.response.template.is.already" + " " + status);
			}
			template.setState(Constant.ACTIVE);
			template.setPublishedStatus(status);
			template = templateRepoIface.save(template);
            logger.info(CLASS + " updateTemplateStatus res Template has been {},  {} ", status, template);
            return exceptionHandlerUtil.createSuccessResponse("api.response.template.has.been" + " " + status,
                    template);
        } catch (Exception e) {
			logger.error(CLASS + " updateTemplateStatus Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

	@Override
	public ApiResponse testTemplate(SubscriberDTO subscriberDTO) {
		logger.info(CLASS + " testActviteTemplate req  {}", subscriberDTO);
		OnbSubscriberOnboardingTemplate template = new OnbSubscriberOnboardingTemplate();
		EditTemplateDTO templateDTO = new EditTemplateDTO();
		try {
			if (subscriberDTO.getMethodName() != null) {
				template = templateRepoIface.getPublishTemplate(subscriberDTO.getMethodName(), PUBLISHED);

				List<OnbMapMethodOnboardingStep> stepList = mapStepRepoIface.findBytemplateId(template.getTemplateId());

				HashMap<String, OnbMapMethodOnboardingStep> hm = new HashMap<>();
				stepList.forEach(mapMethodOnboardingStep -> hm.put(mapMethodOnboardingStep.getOnboardingStep(), mapMethodOnboardingStep));
				templateDTO.setSteps(hm);

				templateDTO.setTemplateName(template.getTemplateName());
				templateDTO.setTemplateMethod(template.getTemplateMethod());
				templateDTO.setPublishedStatus(template.getPublishedStatus());
				templateDTO.setState(template.getState());
				templateDTO.setTemplateId(template.getTemplateId());

                logger.info(CLASS + " testActviteTemplate res Template {}", templateDTO);
                return exceptionHandlerUtil.createSuccessResponse(RESPONSE_TEMPLATE, templateDTO);

            } else {
				return exceptionHandlerUtil.successResponse("api.response.method.name.is.empty");

			}
		} catch (Exception e) {
			logger.error(CLASS + " testActviteTemplate Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}
	}

	@Override
	public ApiResponse templateApprove(TemplateApproveDTO templateApproveDTO) {
		logger.info(CLASS + " approveTemplate  req  {}", templateApproveDTO);
		try {
			OnbSubscriberOnboardingTemplate template = templateRepoIface
					.findBytemplateId(templateApproveDTO.getTemplateId());

			if (PUBLISHED.equals(template.getPublishedStatus())) {
				return exceptionHandlerUtil.successResponse(
						"api.response.your.template.status.is.published.please.unpublished.it.before.making.any.modifications");

			} else {

				template.setRemarks(templateApproveDTO.getRemarks());
			}

			template = templateRepoIface.save(template);
            logger.info(CLASS + " approveTemplate res Template State Updated {}", template);
            return exceptionHandlerUtil.createSuccessResponse("api.response.template.state.updated", template);

        } catch (Exception e) {
			logger.error(CLASS + " approveTemplate Exception {}", e.getMessage());
				logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);

		}

	}

	enum templateApproveEnum {
		NEW, ACTIVE, MODIFIED, DECLINED, DELETE, DELETED
	}

	@Override
	public ApiResponse deleteTemplateById(int id) {
		logger.info(CLASS + " deleteTemplateById req id {}", id);
		try {
			OnbSubscriberOnboardingTemplate template = templateRepoIface.findBytemplateId(id);
			if (template == null) {
				return exceptionHandlerUtil.createErrorResponse(TEMPLATE_NOT_FOUND);

			}
			if (template.getPublishedStatus().equals(PUBLISHED)) {
				return exceptionHandlerUtil.createErrorResponse("api.error.template.is.in.use.cannot.deleted");

			} else if (UNPUBLISHED.equals(template.getPublishedStatus())) {
				template.setPublishedStatus(templateApproveEnum.DELETED.toString());
				template.setState(templateApproveEnum.MODIFIED.toString());
				template.setUpatedDate(AppUtil.getDate());
				templateRepoIface.save(template);
				logger.info(CLASS + " deleteTemplateById  res  Template Status to DELETED ");
				return exceptionHandlerUtil.successResponse("api.response.template.status.to.deleted");

			} else if ("DELETED".equals(template.getPublishedStatus())) {
				return exceptionHandlerUtil.createErrorResponse("api.error.template.already.deleted");

			} else {
				return exceptionHandlerUtil.createErrorResponse("api.error.unpublished.the.template.first");

			}
		} catch (Exception e) {
			logger.error(CLASS + " deleteTemplateById  Exception  {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);

		}
	}

	@Override
	public ApiResponse isTemplateAlreadyExixts(String templateName, String methodId) {
		logger.info(CLASS + " isTemplateExist req   templateName {}  and MethodName {} ", templateName, methodId);
		try {
			int a = templateRepoIface.isTemplateExist(templateName);
			if (a == 0) {
				return exceptionHandlerUtil.createErrorResponse("api.error.not.exist");

			} else {
				return exceptionHandlerUtil.successResponse("api.response.exist");

			}
		} catch (Exception e) {
			logger.error(CLASS + " isTemplateAlreadyExixts Exception {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);

		}
	}

	@Override
	public ApiResponse getTemplateLatestById(int id) {
		logger.info(CLASS + " getTemplateLatestById req id  {}", id);
		OnbSubscriberOnboardingTemplate template = new OnbSubscriberOnboardingTemplate();
		EditTemplateDTO templateDTO = new EditTemplateDTO();
		template = templateRepoIface.findBytemplateId(id);

		try {
			if (template != null) {
				List<OnbMapMethodOnboardingStep> stepList = mapStepRepoIface.findBytemplateId(template.getTemplateId());

				HashMap<String, OnbMapMethodOnboardingStep> hm = new HashMap<>();
                stepList.forEach(mapMethodOnboardingStep ->
                        hm.put(mapMethodOnboardingStep.getOnboardingStep(), mapMethodOnboardingStep)
                );
				templateDTO.setTemplateId(id);
				templateDTO.setSteps(hm);
				templateDTO.setTemplateName(template.getTemplateName());
				templateDTO.setTemplateMethod(template.getTemplateMethod());
				templateDTO.setPublishedStatus(template.getPublishedStatus());
				templateDTO.setState(template.getState());
				templateDTO.setTemplateId(template.getTemplateId());

                logger.info(CLASS + " getTemplateLatestById  res  Template  {}", templateDTO);
                return exceptionHandlerUtil.createSuccessResponse(RESPONSE_TEMPLATE, templateDTO);

            } else {
				return exceptionHandlerUtil.createErrorResponse("api.error.template.is.empty");
			}
		} catch (Exception e) {
			logger.error(CLASS + " getTemplateLatestById  Exception  {}", e.getMessage());
			logger.error(EXCEPTION, e);
			return exceptionHandlerUtil.handleException(e);
		}

	}

}
