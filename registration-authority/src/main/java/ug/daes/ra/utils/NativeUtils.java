/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package ug.daes.ra.utils;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import ug.daes.DAESService;
import ug.daes.Result;
import ug.daes.ra.dto.LogModelDTO;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.request.entity.LogModel;

/**
 * The Class NativeUtils.
 */
/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
 * All rights reserved.
 */


import java.io.IOException;

/**
 * The Class NativeUtils.
 */
public class NativeUtils {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(NativeUtils.class);

	/** Constants for formatting and generation */
	private static final String CLASS_NAME = "NativeUtils";
	private static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd HH:mm:ss";
	private static final String UPPER_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final Random RANDOM = new Random();


	private NativeUtils() {
		throw new UnsupportedOperationException("NativeUtils class");
	}

	/**
	 * Generate PKI key id.
	 *
	 * @return the string
	 */
	public static String generatePKIKeyId() {
		return genRandomNumber(UPPER_ALPHABET);
	}

	/**
	 * Gen random number.
	 */
	private static String genRandomNumber(String alphaNumeric) {
		StringBuilder sb = new StringBuilder();
		int length = 10;

		for (int i = 0; i < length; i++) {
			int index = RANDOM.nextInt(alphaNumeric.length());
			sb.append(alphaNumeric.charAt(index));
		}
		return sb.toString();
	}

	/**
	 * Gets the UU id.
	 * Fixed: Removed the static 'uuid' field to ensure thread safety.
	 *
	 * @return the UU id
	 */
	public static String getUUId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Validate request body.
	 * Fixed: Removed NoSuchAlgorithmException and simplified boolean return.
	 */
	public static boolean validateRequestBody(String requestBody, int hashdata) {
		return hashdata == requestBody.hashCode();
	}

	/**
	 * Gets the time stamp.
	 * Fixed: Removed temporary 'date' variable.
	 */
	public static Date getTimeStamp() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_SIMPLE);
		return format.parse(new Timestamp(System.currentTimeMillis()).toString());
	}

	/**
	 * Gets the time stamp.
	 * Fixed: Removed temporary 'certDates' variable.
	 */
	public static Date getTimeStamp(String date) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT_SIMPLE);
		return f.parse(date);
	}

	/**
	 * Gets the time stamp string.
	 */
	public static String getTimeStampString() {
		SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT_ISO);
		String formattedDate = f.format(new Date());
		if (logger.isInfoEnabled()) {
			logger.info("Current formatted date :: {}", formattedDate);
		}
		return formattedDate;
	}

	public static void checkOldPasswords(String pin, String setPin, String message) throws RAServiceException {
		String[] pins = pin.split(", ");
		List<String> pinList = Arrays.asList(pins);
		for (String oldPin : pinList) {
			if (oldPin != null && oldPin.equals(setPin)) {
				throw new RAServiceException(message);
			}
		}
	}

	public static void checkCurrentPassword(String oldPins, String setPin, String message) throws RAServiceException {
		String[] pins = oldPins.split(", ");
		List<String> pinList = Arrays.asList(pins);
		String lastPin = pinList.get(pinList.size() - 1);
		if (setPin.equals(lastPin)) {
			throw new RAServiceException(message);
		}
	}

	public static void checkOldCurrentPasswords(String oldPins, String setPin, String message)
			throws RAServiceException {
		String[] pins = oldPins.split(", ");
		List<String> pinList = Arrays.asList(pins);
		String lastPin = pinList.get(pinList.size() - 1);
		if (!setPin.equals(lastPin)) {
			throw new RAServiceException(message);
		}
	}

	/**
	 * Fixed: Used constant for date format and removed temporary 'strLong' variable.
	 */
	public static String getTotalTime(String t1, String t2) throws ParseException {
		SimpleDateFormat smpdate = new SimpleDateFormat(DATE_FORMAT_ISO);
		Date time1 = smpdate.parse(t1);
		Date time2 = smpdate.parse(t2);

		logger.info("time1 :: {} | time2 :: {}", time1, time2);

		long differenceInTime = time2.getTime() - time1.getTime();
		long differenceInSeconds = (differenceInTime / 1000) % 60;

		logger.info("differenceInSeconds :: {}", differenceInSeconds);

		return Long.toString(differenceInSeconds);
	}



	/**
	 * Fixed: Removed JsonProcessingException because it is a subclass of IOException.
	 * Declaring IOException covers all Jackson-related processing errors.
	 */
	public static LogModel getLogModel(LogModelDTO logModelDTO)
			throws IOException, ug.daes.PKICoreServiceException {

		logger.info("{} :: LogModel :: {}", CLASS_NAME, logModelDTO);

		LogModel logModel = new LogModel();
		logModel.setIdentifier(logModelDTO.getIdentifier());
		logModel.setCorrelationID(logModelDTO.getCorrelationID());
		logModel.setTransactionID(logModelDTO.getTransactionID());
		logModel.setSubTransactionID(logModelDTO.getSubTransactionID());
		logModel.setTimestamp(logModelDTO.getTimestamp());
		logModel.setStartTime(logModelDTO.getStartTime());
		logModel.setEndTime(logModelDTO.getEndTime());
		logModel.setGeoLocation(logModelDTO.getGeoLocation());
		logModel.setCallStack(logModelDTO.getCallStack());
		logModel.setServiceName(logModelDTO.getServiceName());
		logModel.setTransactionType(logModelDTO.getTransactionType());
		logModel.setTransactionSubType(logModelDTO.getTransactionSubType());
		logModel.setLogMessageType(logModelDTO.getLogMessageType());
		logModel.setLogMessage(logModelDTO.getLogMessage());
		logModel.setServiceProviderName(logModelDTO.getServiceProviderName());
		logModel.setServiceProviderAppName(logModelDTO.getServiceProviderAppName());
		logModel.setSignatureType(logModelDTO.getSignatureType());
		logModel.seteSealUsed(logModelDTO.iseSealUsed());
		logModel.setChecksum(logModelDTO.getChecksum());
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(logModel);

		logger.info("{} :: getLogModel() :: checksum :: request :: {}", CLASS_NAME, json);


		Result checksumResult = DAESService.addChecksumToTransaction(json);

		if (logger.isInfoEnabled()) {
			logger.info("{} :: getLogModel() :: checksum :: response :: {}",
					CLASS_NAME,
					new String(checksumResult.getResponse(), StandardCharsets.UTF_8));
		}

		String push = new String(checksumResult.getResponse(), StandardCharsets.UTF_8);
		return objectMapper.readValue(push, LogModel.class);
	}


	public static String getDate(String date) {
		String[] dates = date.split(" ");
		return dates[0];
	}
}