package com.dtt.service.impl;

import java.util.Date;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.dtt.enums.ServiceNames;
import com.dtt.model.GenericSubscriber;
import com.dtt.repo.GenericSubscriberRepo;
import com.dtt.requestdto.WalletTransactionDto;
import com.dtt.requestdto.WalletTransactionListDto;
import com.dtt.service.iface.WalletTransactionIface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;

@Service
public class WalletTransactionImpl implements WalletTransactionIface {


	private final Logger logger = LoggerFactory.getLogger(WalletTransactionImpl.class);
	private static final String ERROR_SOMETHING_WENT_WRONG =
			"api.error.something.went.wrong";

	/** The Constant CLASS. */
	 static final String CLASS = "VarifyCredentialsImpl";


	private final ExceptionHandlerUtil exceptionHandlerUtil;
	private final GenericSubscriberRepo subscriberRepoIface;
	private final LogModelServiceImpl logModelService;

    public WalletTransactionImpl(ExceptionHandlerUtil exceptionHandlerUtil, GenericSubscriberRepo subscriberRepoIface, LogModelServiceImpl logModelService) {
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.subscriberRepoIface = subscriberRepoIface;
        this.logModelService = logModelService;
    }




	@Override
	public ApiResponse addWalletTransaction(
			WalletTransactionDto walletTransactionDto) {
		try {
			Date startTime = AppUtil.getCurrentDate();
			logger.info(CLASS + "request body{} " , walletTransactionDto);
			GenericSubscriber subscriber = subscriberRepoIface
					.findbyDocumentNumber(walletTransactionDto.getPassportNumber());
			String typeMessage = walletTransactionDto.getType();

			if(subscriber != null) {
				Date endTime = AppUtil.getCurrentDate();
				logModelService.setLogModelDTO(walletTransactionDto.isSuccess(), subscriber.getSubscriberUid(), null, ServiceNames.WALLET.toString(),
						AppUtil.getUUId(), typeMessage, startTime, endTime, "false");
				
				return exceptionHandlerUtil.createSuccessResponse( "api.response.log.successfully", null);
			}else {

				return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
			}
			

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {
			logger.error("Exception occurred:", e);
			logger.error(CLASS + "addWalletTransaction Exception Something went Wrong, Onboarding Failed::{}"
					,e.getMessage());
			return exceptionHandlerUtil.createErrorResponse(ERROR_SOMETHING_WENT_WRONG);
		} catch (Exception e) {
			logger.error("Exception occurred:", e);
			return exceptionHandlerUtil.createErrorResponse(ERROR_SOMETHING_WENT_WRONG);
		}
	}

	@Override
	public ApiResponse addListWalletTransaction(WalletTransactionListDto walletTransactionDto) {
		try {

			logger.info(CLASS + "request body of addListWalletTransaction:{}" , walletTransactionDto);
			Date startTime = AppUtil.getCurrentDate();
			// Convert list to JSON
	        ObjectMapper objectMapper = new ObjectMapper();
	        
	        String s = objectMapper.writeValueAsString(walletTransactionDto);
	       
	        JsonNode json = objectMapper.readTree(s);
	        
	     // Access the "walletTransactionDtos" array
            JsonNode jsonList = json.get("walletTransactionDtos");
	        
	        if(jsonList.isArray()) {
	        	for (JsonNode jsonNode : jsonList) {
	        		
	        		GenericSubscriber subscriber = subscriberRepoIface
	    					.findbyDocumentNumber(jsonNode.get("passportNumber").asText());
	        		if(subscriber != null) {
	        			String typeMessage = jsonNode.get("type").asText();
						boolean success = jsonNode.get("success").asBoolean();
		        		Date endTime = AppUtil.getCurrentDate();
		    			logModelService.setLogModelDTO(success, subscriber.getSubscriberUid(), null, ServiceNames.OTHER.toString(),
		    					AppUtil.getUUId(), typeMessage, startTime, endTime, "false");
	        		}
	        	}
	        }
			return exceptionHandlerUtil.createSuccessResponse( "api.response.log.successfully", null);
			
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {
			logger.error(CLASS + "addWalletTransaction Exception Something went Wrong, Onboarding Failed :{}"
					, e.getMessage());
			return exceptionHandlerUtil.createErrorResponse(ERROR_SOMETHING_WENT_WRONG);
		} catch (Exception e) {
			return exceptionHandlerUtil.createErrorResponse(ERROR_SOMETHING_WENT_WRONG);
		}
	}

}

