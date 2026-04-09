package com.dtt.service.impl;

import java.text.ParseException;
import java.util.Date;

import com.dtt.common.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dtt.enums.LogMessageType;
import com.dtt.enums.TransactionType;
import com.dtt.requestdto.LogModelDTO;
import com.dtt.service.iface.LogModelServiceIface;
import com.dtt.utils.KafkaSender;
import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.DAESService;
import ug.daes.Result;

@Service("genericLogModelService")
public class LogModelServiceImpl implements LogModelServiceIface {

	private static Logger logger = LoggerFactory.getLogger(LogModelServiceImpl.class);

	/** The Constant CLASS. */
	 static final String CLASS = "LogModelServiceImpl";

	private final KafkaSender mqSender;

    public LogModelServiceImpl(KafkaSender mqSender) {
        this.mqSender = mqSender;
    }


    public void setLogModel(Boolean response, String identifier, String geoLocation, String serviceName,
			String correlationID, String totalTime, Date startTime, Date endTime, String otpStatus,String message){
		LogModelDTO logModel = new LogModelDTO();
		logModel.setIdentifier(identifier);
		logModel.setCorrelationID(correlationID);
		logModel.setTransactionID(correlationID);
		logModel.setTimestamp(null);
		logModel.setStartTime(AppUtil.getTimeStampString(startTime));
		logModel.setEndTime(AppUtil.getTimeStampString(endTime));
		logModel.setServiceName(serviceName);
		logModel.setLogMessage(message);
		logModel.setTransactionType(TransactionType.OTHER.toString());
		logModel.setGeoLocation(geoLocation);
		logModel.seteSealUsed(false);
		logModel.setSignatureType(null);
		logModel.setCallStack(otpStatus);
		if (Boolean.TRUE.equals(response)) {
			logModel.setLogMessageType(LogMessageType.SUCCESS.toString());
		} else {
			logModel.setLogMessageType(LogMessageType.FAILURE.toString());
		}
		logModel.setChecksum(null);

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(logModel);
			Result checksumResult = DAESService.addChecksumToTransaction(json);
			String push = new String(checksumResult.getResponse());
			LogModelDTO log = objectMapper.readValue(push, LogModelDTO.class);
			mqSender.send(log);
			logger.info(CLASS + " setLogModel log {}",logModel );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(CLASS + " setLogModel Exception  {]", e.getMessage() );
		}
	}
	
	@Override
	public void setLogModelDTO(Boolean response, String identifier, String geoLocation, String serviceName,
			String correlationID, String message, Date startTime, Date endTime, String otpStatus)
			throws ParseException {
		LogModelDTO logModel = new LogModelDTO();
		logModel.setIdentifier(identifier);
		logModel.setCorrelationID("id");
		logModel.setTransactionID("id");
		logModel.setTimestamp(null);
		logModel.setStartTime(AppUtil.getTimeStampString(startTime));
		logModel.setEndTime(AppUtil.getTimeStampString(endTime));
		logModel.setServiceName(serviceName);
		logModel.setLogMessage(message);
		logModel.setTransactionType(TransactionType.BUSINESS.toString());
		logModel.setGeoLocation(geoLocation);
		logModel.seteSealUsed(false);
		logModel.setSignatureType(null);
		logModel.setCallStack(otpStatus);
		logModel.setUserActivityType("WALLET_AUTHENTICATION");

		if (Boolean.TRUE.equals(response)) {
			logModel.setLogMessageType(LogMessageType.SUCCESS.toString());
		} else {
			logModel.setLogMessageType(LogMessageType.FAILURE.toString());
		}
		logModel.setChecksum(null);

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(logModel);
			Result checksumResult = DAESService.addChecksumToTransaction(json);
			String push = new String(checksumResult.getResponse());
			LogModelDTO log = objectMapper.readValue(push, LogModelDTO.class);
			mqSender.send(log);
			logger.info(CLASS + " setLogModel log {}",logModel );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(CLASS + " setLogModel Exception  {]", e.getMessage() );
		}
	}


	public void setLogModelFCMToken(Boolean response, String identifier, String geoLocation, String serviceName,
			String correlationID, String message, Date startTime, Date endTime, String otpStatus)
			{
		LogModelDTO logModel = new LogModelDTO();
		logModel.setIdentifier(identifier);
		logModel.setCorrelationID(correlationID);
		logModel.setTransactionID(correlationID);
		logModel.setTimestamp(null);
		logModel.setStartTime(AppUtil.getTimeStampString(startTime));
		logModel.setEndTime(AppUtil.getTimeStampString(endTime));
		logModel.setServiceName(serviceName);
		logModel.setLogMessage(message);
		logModel.setTransactionType(TransactionType.BUSINESS.toString());
		logModel.setGeoLocation(geoLocation);
		logModel.seteSealUsed(false);
		logModel.setSignatureType(null);
		logModel.setCallStack(otpStatus);
		if (Boolean.TRUE.equals(response)){
			logModel.setLogMessageType(LogMessageType.SUCCESS.toString());
		} else {
			logModel.setLogMessageType(LogMessageType.FAILURE.toString());
		}
		logModel.setChecksum(null);

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(logModel);
			Result checksumResult = DAESService.addChecksumToTransaction(json);
			String push = new String(checksumResult.getResponse());
			LogModelDTO log = objectMapper.readValue(push, LogModelDTO.class);
			mqSender.send(log);
		} catch (Exception e) {
			// catch
		}
	}

}
