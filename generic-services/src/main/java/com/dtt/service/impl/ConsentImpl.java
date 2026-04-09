package com.dtt.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;

import com.dtt.common.util.ExceptionHandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dtt.model.GenericConsentModel;
import com.dtt.model.GenericConsentHistory;
import com.dtt.repo.ConsentHistoryRepoIface;
import com.dtt.repo.GenericConsentRepo;
import com.dtt.requestdto.ConsentDTO;
import com.dtt.service.iface.ConsentIface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@Service
public class ConsentImpl implements ConsentIface {
    private static final Logger logger =
            LoggerFactory.getLogger(ConsentImpl.class);


    private final ConsentHistoryRepoIface consentHistoryRepoIface;
    private final GenericConsentRepo genericConsentRepo;

    private final ExceptionHandlerUtil exceptionHandlerUtil;


    @Value(value = "${directoryPath}")
    private String directoryPath;

    public ConsentImpl(ConsentHistoryRepoIface consentHistoryRepoIface, GenericConsentRepo consentRepoIface, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.consentHistoryRepoIface = consentHistoryRepoIface;
        this.genericConsentRepo = consentRepoIface;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }


    @Override
    public ApiResponse addFiles(ConsentDTO consentDTO, MultipartFile termsAndConditions, MultipartFile dataPrivacy) {

        // 1. Validate consent string
        if (isEmpty(consentDTO.getConsent())) {
            return exceptionHandlerUtil.createErrorResponse("api.error.consent.cannot.be.empty");
        }

        try {
            // 2. Retrieve or create consent
            GenericConsentModel consent = genericConsentRepo.getConsentByConsentType(consentDTO.getConsentType());
            if (consent == null) {
                consent = new GenericConsentModel();
                consent.setCreatedOn(AppUtil.getDate());
            }

            consent.setConsentType(consentDTO.getConsentType());
            consent.setConsent(consentDTO.getConsent());
            consent.setPrivacyConsent(consentDTO.getPrivacyConsent());
            consent.setStatus("INACTIVE");
            consent.setUpdatedOn(AppUtil.getDate());
            genericConsentRepo.save(consent);

            // 3. Save consent history
            saveConsentHistory(consentDTO, consent, termsAndConditions, dataPrivacy);

            // 4. Start file storage thread if any file exists
            if (!termsAndConditions.isEmpty() || !dataPrivacy.isEmpty()) {
                new Thread(() -> {
                    try {
                        storefiles(termsAndConditions, dataPrivacy);
                    } catch (IOException e) {
                        logger.error("File storage failed:", e);
                    }
                }).start();
            }

            return exceptionHandlerUtil.createSuccessResponse("api.response.consent.saved.successfully", null);

        } catch (IOException e) {
            logger.error("File handling error:", e);
            return exceptionHandlerUtil.createErrorResponse("api.error.file.operation.failed");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input provided:", e);
            return exceptionHandlerUtil.createErrorResponse("api.error.invalid.input");
        } catch (RuntimeException e) {
            logger.error("Unexpected error:", e);
            return exceptionHandlerUtil.createErrorResponse("api.error.something.went.wrong");
        }
    }

    // Helper method: Save consent history
    private void saveConsentHistory(ConsentDTO consentDTO, GenericConsentModel consent,
                                    MultipartFile termsAndConditions, MultipartFile dataPrivacy) throws IOException {

        GenericConsentHistory history = new GenericConsentHistory();
        history.setConsentId(consent.getConsentId());
        history.setConsentType(consentDTO.getConsentType());
        history.setConsent(consentDTO.getConsent());
        history.setPrivacyConsent(consentDTO.getPrivacyConsent());
        history.setConsentRequired(consentDTO.getConsentRequired());
        history.setCreatedOn(AppUtil.getDate());

        history.setOptionalTermsAndConditions(processFile(termsAndConditions, ".html", "Terms and Conditions"));
        history.setOptionalDataAndPrivacy(processFile(dataPrivacy, ".html", "Data Privacy"));

        consentHistoryRepoIface.save(history);
    }

    // Helper method: Validate file and convert to Base64
    private String processFile(MultipartFile file, String allowedExtension, String fileType) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException(fileType + " file name is missing");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        if (!allowedExtension.equals(fileExtension)) {
            throw new IllegalArgumentException(fileType + " files must have " + allowedExtension + " extension");
        }

        byte[] bytes = file.getBytes();
        return AppUtil.getBase64FromByteArr(bytes);
    }
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ApiResponse storefiles(MultipartFile termsAndConditions,MultipartFile dataPrivacy) throws IOException {

        try {

            if(!termsAndConditions.isEmpty()){
                Path filePath1 = Paths.get(directoryPath + termsAndConditions.getOriginalFilename());
                Files.write(filePath1, termsAndConditions.getBytes());
            }
            if(!dataPrivacy.isEmpty()) {
                Path filePath2 = Paths.get(directoryPath + dataPrivacy.getOriginalFilename());
                Files.write(filePath2, dataPrivacy.getBytes());
            }
            return exceptionHandlerUtil.createSuccessResponse("api.response.file.saved.successfully", null);
        }catch(Exception e){
            return exceptionHandlerUtil.createErrorResponse("api.error.file.not.saved");
        }
    }





}

