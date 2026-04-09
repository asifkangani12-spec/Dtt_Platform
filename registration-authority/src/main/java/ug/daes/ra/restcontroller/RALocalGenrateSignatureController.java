package ug.daes.ra.restcontroller;

import com.dtt.common.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ug.daes.ra.asserts.RAServiceAsserts;
import ug.daes.ra.dto.LogModelDTO;
import ug.daes.ra.exception.ErrorCodes;
import ug.daes.ra.exception.RAServiceException;

import ug.daes.ra.service.iface.implementation.RALocalGenrateSignatureIfaceImpl;

import java.util.Locale;

@RestController
@RequestMapping(value = "/api/")
public class RALocalGenrateSignatureController {

	/** The logger. */
	static Logger logger = LoggerFactory.getLogger(RALocalGenrateSignatureController.class);

	/** The class. */
	private static final String CLASS = "RALocalGenrateSignatureController";
	private final PKIRestController controller;
	private final RALocalGenrateSignatureIfaceImpl raLocalGenrateSignatureIfaceImpl;
	private final MessageSource messageSource;

	public RALocalGenrateSignatureController(
			PKIRestController controller,
			RALocalGenrateSignatureIfaceImpl raLocalGenrateSignatureIfaceImpl,
			MessageSource messageSource) {

		this.controller = controller;
		this.raLocalGenrateSignatureIfaceImpl = raLocalGenrateSignatureIfaceImpl;
		this.messageSource = messageSource;
	}


@PostMapping(value = "post/service/certificate/generate-signatur/local-agent/subscriber", consumes = "application/json")
public String generateSignatureForLocalAgentSub(@RequestBody LogModelDTO generateSignatureRequest) {
	try {
		logger.info("{} :: generateSignatureForLocalAgentSub :: request :: {}", CLASS, generateSignatureRequest);

		ApiResponse ApiResponse = controller.getServiceStatus();

		if (!ApiResponse.isSuccess()) {
			throw new RAServiceException(ApiResponse.getMessage());
		}

		RAServiceAsserts.notNullorEmpty(generateSignatureRequest, ErrorCodes.E_INVALID_REQUEST);

		String response = raLocalGenrateSignatureIfaceImpl.generateSignatureForAgentSubscriber(generateSignatureRequest);

		logger.info("{} :: generateSignatureForLocalAgentSub() :: response :: {}", CLASS, response);

		return new ApiResponse(true,
				messageSource.getMessage("api.response.generate.signature.response", null, Locale.ENGLISH),
				"\"" + response + "\"").toString();

	} catch (RAServiceException e) {

		logger.error("{} :: generateSignatureForLocalAgentSub() :: business error :: {}", CLASS, e.getMessage());


		String errorCode = ErrorCodes.getErrorCode(e.getMessage());
		String errorMessage = ErrorCodes.getErrorMessage(errorCode);

		return new ApiResponse(false, errorMessage, null).toString();

	} catch (Exception e) {

		logger.error("{} :: generateSignatureForLocalAgentSub() :: system error", CLASS, e);


		return new ApiResponse(false, ErrorCodes.getErrorMessage(ErrorCodes.E_RA_501), null)
				.toString();
	}
}

	@PostMapping(value = "post/service/certificate/generate-signatur/local-agent/org", consumes = "application/json")
	public String generateSignatureForLocalAgentOrg(@RequestBody LogModelDTO generateSignatureRequest) {
		try {
			logger.info("{} :: generateSignatureForLocalAgentOrg :: request :: {}",
					CLASS,
					generateSignatureRequest);

			ApiResponse ApiResponse = controller.getServiceStatus();

			if (!ApiResponse.isSuccess()) {
				throw new RAServiceException(ApiResponse.getMessage());
			}

			RAServiceAsserts.notNullorEmpty(generateSignatureRequest, ErrorCodes.E_INVALID_REQUEST);

			String response = raLocalGenrateSignatureIfaceImpl.generateSignatureForAgentOrganization(generateSignatureRequest);

			logger.info("{} :: generateSignatureForLocalAgentOrg() :: response :: {}", CLASS, response);

			return new ApiResponse(true,
					messageSource.getMessage("api.response.generate.signature.response", null, Locale.ENGLISH),
					"\"" + response + "\"").toString();

		} catch (RAServiceException e) {
			logger.error("{} :: generateSignatureForLocalAgentOrg() :: business error :: {}", CLASS, e.getMessage());

			return new ApiResponse(false,
					ErrorCodes.getErrorMessage(ErrorCodes.getErrorCode(e.getMessage())),
					null).toString();

		} catch (Exception e) {

			logger.error("{} :: generateSignatureForLocalAgentOrg() :: unexpected system error", CLASS, e);

			return new ApiResponse(false,
					ErrorCodes.getErrorMessage(ErrorCodes.E_RA_501),
					null).toString();
		}
	}

}
