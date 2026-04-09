package ug.daes.onboarding.service.impl;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import ug.daes.onboarding.config.OnboardingSentryClientExceptions;
import ug.daes.onboarding.dto.DocumentResponse;
import ug.daes.onboarding.dto.FileUploadDTO;
import ug.daes.onboarding.dto.Selfie;
import ug.daes.onboarding.exceptions.ApplicationException;

import ug.daes.onboarding.model.OnboardingLiveliness;
import ug.daes.onboarding.repository.OnboardingLivelinessRepository;

@Service
public class EdmsServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(EdmsServiceImpl.class);

    private static final String CLASS = "EdmsServiceImpl";
	private static final String FILES_DOCUMENTS = "/files/downloads";
	private static final String DOCUMENTS = "/documents";
	private static final String UNEXPECTED_EXCEPTION = "Unexpected exception";
	private static final String MODEL = "model";
	private static final String FILES = "/files";
	private static final String FILE_NEW = "file_new";

	private static final String SOMETHING_WENT_WRONG = "api.error.something.went.wrong.please.try.after.sometime";
	private static final String JPEG = ".jpeg";
	private static final String SELFIE ="selfie";
	private static final String DOC = "/documents/";
	private static final String ACTION = "action";
    private static final String DOCUMNETS_RESPONSE_BODY_IS_NULL="Document response body is null";
    
    

	private final RestTemplate restTemplate;
	private final OnboardingLivelinessRepository onboardingLivelinessRepository;
	private final MessageSource messageSource;
	private final OnboardingSentryClientExceptions sentryClientExceptions;
	private final ExceptionHandlerUtil exceptionHandlerUtil;

	private static Path testFile;
	public EdmsServiceImpl(RestTemplate restTemplate,
						   OnboardingLivelinessRepository onboardingLivelinessRepository,
						   MessageSource messageSource,
						   OnboardingSentryClientExceptions sentryClientExceptions,
						   ExceptionHandlerUtil exceptionHandlerUtil) {

		this.restTemplate = restTemplate;
		this.onboardingLivelinessRepository = onboardingLivelinessRepository;
		this.messageSource = messageSource;
		this.sentryClientExceptions = sentryClientExceptions;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	@Value("${edms.localurl}")
	private String baselocalUrl;

	@Value("${edms.downloadurl}")
	private String edmsDwonlodUrl;
    private void validateUrl(String url) {

        if (url == null || url.trim().isEmpty()) {
            throw new ApplicationException("URL cannot be null or empty");
        }

        try {
            URI uri = new URI(url);

            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new ApplicationException("Only HTTPS protocol is allowed");
            }

            String allowedHost = "internal-edms.company.com";

            if (!allowedHost.equalsIgnoreCase(uri.getHost())) {
                throw new ApplicationException("Unauthorized host detected: " + uri.getHost());
            }

            InetAddress address = InetAddress.getByName(uri.getHost());

            if (address.isAnyLocalAddress() ||
                    address.isLoopbackAddress() ||
                    address.isSiteLocalAddress()) {

                throw new ApplicationException("Access to internal/private IPs is not allowed");
            }

        } catch (URISyntaxException | UnknownHostException e) {
            throw new ApplicationException("Invalid EDMS URL", e);
        }
    }
	public ApiResponse saveSelfieToEdms(Selfie image) {
		try {
			logger.info(CLASS + "saveSelfieToEdms req for saveSelfieToEdms {}", image.getSubscriberUniqueId());
			byte[] img = Base64.getDecoder().decode(image.getSubscriberSelfie());
			Resource fileRes = getTestFile(img, SELFIE, JPEG);
			String docIdUrl = baselocalUrl + DOCUMENTS;

            validateEdmsUrlOrThrow(docIdUrl);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			logger.info(CLASS + " saveSelfieToEdms req for get DocId docIdUrl {} and requestEntity {} ", docIdUrl,
					requestEntity);
			ResponseEntity<DocumentResponse> documentId = restTemplate.exchange(docIdUrl, HttpMethod.POST,
					requestEntity, DocumentResponse.class);
			logger.info(CLASS + " saveSelfieToEdms res for get DocId {}", documentId);
            String docIdAndFileUrl = baselocalUrl + DOC +
                    Objects.requireNonNull(documentId.getBody(), DOCUMNETS_RESPONSE_BODY_IS_NULL).getId() +FILES;


                validateUrl(docIdAndFileUrl);

			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			bodyMap.add(FILE_NEW, fileRes);
			bodyMap.add(MODEL, image.getSubscriberUniqueId() + " _Selfie " + AppUtil.getDate());
			bodyMap.add(ACTION, 1);
			HttpHeaders headers4 = new HttpHeaders();
			headers4.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity4 = new HttpEntity<>(bodyMap, headers4);

			logger.info(CLASS + " saveSelfieToEdms req for saveFileWithDocId docIdAndFileUrl {} and requestEntity4 {}",
					docIdAndFileUrl, requestEntity4);
			ResponseEntity<ApiResponse> result = restTemplate.exchange(
					docIdAndFileUrl, HttpMethod.POST, requestEntity4, ApiResponse.class);

			logger.info("{} saveSelfieToEdms res for saveFileWithDocId {}", CLASS, result);

			HttpStatus status = (HttpStatus) result.getStatusCode();

			switch (status) {

				case ACCEPTED:

                    String downloadurlselfie = edmsDwonlodUrl  +
                            Objects.requireNonNull(documentId.getBody(), DOCUMNETS_RESPONSE_BODY_IS_NULL).getId()
                            + FILES_DOCUMENTS;
                    Path filePath = new File(testFile.toString()).toPath();

                    deleteTempFile(filePath);

                    logger.info("{} saveSelfieToEdms downloadurlselfie {}", CLASS, downloadurlselfie);

                    return exceptionHandlerUtil.createSuccessResponse(
                            "api.response.selfie.uploaded.successfully",
                            downloadurlselfie);

                case INTERNAL_SERVER_ERROR:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							"api.error.internal.server.error", status.value());

				case BAD_REQUEST:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							"api.error.bad.request", status.value());

				case UNAUTHORIZED:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							"api.error.unauthorized", status.value());

				case FORBIDDEN:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							"api.error.forbidden", status.value());

				case REQUEST_TIMEOUT:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							"api.error.request.timeout", status.value());

				default:
					return exceptionHandlerUtil.createErrorResponseWithResult(
							SOMETHING_WENT_WRONG, status.value());
			}

        } catch (Exception e) {
			logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + "saveSelfieToEdms Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}

	}
    private void validateEdmsUrlOrThrow(String url) {
        try {
            validateUrl(url);
        } catch (ApplicationException e) {
            throw new ApplicationException("Invalid EDMS URL detected", e);
        }
    }
    private void deleteTempFile(Path filePath) {
        try {
            Files.delete(filePath);
            logger.info("{} Temp file deleted successfully {}", CLASS, filePath);
        } catch (IOException e) {
            logger.warn("{} Failed to delete temp file {} : {}", CLASS, filePath, e.getMessage());
        }
    }
	@Async
	public CompletableFuture<ApiResponse> saveFileToEdms(Object fileContent, String fileType,
			FileUploadDTO fileupload) {
		try {
			logger.info("{}{} - Request to save file to EDMS with fileType: {} and fileUpload: {}", 
				    CLASS, Utility.getMethodName(), fileType, fileupload);
			// Check if the file content is valid (video or selfie)
            if (fileContent == null
                    || (fileContent instanceof MultipartFile multipartFile && multipartFile.isEmpty())) {

				return CompletableFuture.completedFuture(
						exceptionHandlerUtil.createErrorResponse("api.error.file.cant.be.null.or.empty"));
			}
			// Handle different file types (video or selfie)
            if ("video".equals(fileType)) {
                if (!(fileContent instanceof MultipartFile)) {
                    return CompletableFuture.completedFuture(
                            exceptionHandlerUtil.createErrorResponse("api.error.invalid.file.content"));
                }
                return handleVideoUpload((MultipartFile) fileContent, fileupload);

            } else if (SELFIE.equals(fileType)) {
                return handleSelfieUpload((Selfie) fileContent);

            } else {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse("api.error.invalid.file.type"));
            }
		} catch (Exception e) {
			logger.error("{} saveFileToEdms Exception: {}", CLASS, e.getMessage());
			logger.error(UNEXPECTED_EXCEPTION, e);
			return CompletableFuture.completedFuture(exceptionHandlerUtil.handleException(e));
		}
	}

	public CompletableFuture<ApiResponse> handleVideoUpload(MultipartFile file, FileUploadDTO fileupload) {
		String contentType = file.getContentType();
		logger.info("handleVideoUpload :: file.getContentType() :: {}", contentType);
		// Validate content type for video
		if (contentType == null || !contentType.startsWith("video/")) {
			return CompletableFuture
					.completedFuture(exceptionHandlerUtil.successResponse("api.error.video.content.type.is.not.mp4"));

		}
		// Get Document ID asynchronously and upload video
		return fetchDocumentIdAsync().thenCompose(documentId -> {
			String docIdAndFileUrl = baselocalUrl + DOC + documentId.getId() + FILES;

            try {
                validateUrl(docIdAndFileUrl);
            } catch (ApplicationException e) {
                throw new ApplicationException("Invalid EDMS URL detected", e);
            }
			logger.info("{} - {} - handleVideoUpload: docIdAndFileUrl: {}", CLASS, Utility.getMethodName(), docIdAndFileUrl);

            return uploadFileAsync(docIdAndFileUrl, file, fileupload).thenApply(result -> {
                if (result.getStatusCode() == HttpStatus.ACCEPTED) {

                    String downloadUrl = edmsDwonlodUrl + documentId.getId() + FILES_DOCUMENTS;

                    logger.info("{} - {} - handleVideoUpload: downloadUrl: {}",
                            CLASS, Utility.getMethodName(), downloadUrl);

                    saveOnboardingLiveliness(fileupload, downloadUrl);

                    return exceptionHandlerUtil.successResponse("api.response.video.uploaded.successfully");

                } else {
                    return exceptionHandlerUtil.handleErrorRestTemplateResponse(result.getStatusCode().value());
                }
            });
		});
	}


	
	public CompletableFuture<ApiResponse> handleSelfieUpload(Selfie image) throws IOException {
	    byte[] img = Base64.getDecoder().decode(image.getSubscriberSelfie());
	    Resource fileRes = getTestFile(img, SELFIE, JPEG); // This creates a temp file

	    return fetchDocumentIdAsync().thenCompose(documentId -> {
	        String docIdAndFileUrl = baselocalUrl + DOC + documentId.getId() + FILES;
	        logger.info("{} - {} - fetchDocumentIdAsync: docIdAndFileUrl: {}", CLASS, Utility.getMethodName(), docIdAndFileUrl);

            try {
                validateUrl(docIdAndFileUrl);
            } catch (ApplicationException e) {
                throw new ApplicationException("Invalid EDMS URL detected", e);
            }
	        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
	        bodyMap.add(FILE_NEW, fileRes);
	        bodyMap.add(MODEL, image.getSubscriberUniqueId() + " _Selfie " + AppUtil.getDate());
	        bodyMap.add(ACTION, 1);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

	        return CompletableFuture.supplyAsync(() ->
	            restTemplate.exchange(docIdAndFileUrl, HttpMethod.POST, requestEntity, ApiResponse.class)
	        ).thenApply(result -> {
                if (fileRes instanceof FileSystemResource fileSystemResource) {
                    File tempFile = fileSystemResource.getFile();
                    if (tempFile.exists()) {
                        try {
                            Files.delete(tempFile.toPath());
                            logger.info("{} - Temp file deleted successfully", Utility.getMethodName());
                        } catch (IOException e) {
                            logger.error("{} - Failed to delete temp file: {}", Utility.getMethodName(), e.getMessage());
                        }
                    }


	                String downloadUrl = edmsDwonlodUrl + documentId.getId() + FILES_DOCUMENTS;
	                logger.info("{} - {} - handleSelfieUpload: downloadUrl: {}", CLASS, Utility.getMethodName(), downloadUrl);
	                return exceptionHandlerUtil.createSuccessResponse("api.response.selfie.uploaded.successfully", downloadUrl);
                } else {
                    return exceptionHandlerUtil.handleErrorRestTemplateResponse(result.getStatusCode().value());
                }
	        });
	    });
	}




    @Async
    public CompletableFuture<DocumentResponse> fetchDocumentIdAsync() {
        String docIdUrl = baselocalUrl + DOCUMENTS;
        logger.info("{} - {} - fetchDocumentIdAsync: downloadUrl: {}", CLASS, Utility.getMethodName(), docIdUrl);


        try {
            validateUrl(docIdUrl);
        } catch (ApplicationException e) {
            throw new ApplicationException("Invalid EDMS URL detected", e);
        }
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(new LinkedMultiValueMap<>(),
				headers);
		return CompletableFuture.supplyAsync(() -> restTemplate
				.exchange(docIdUrl, HttpMethod.POST, requestEntity, DocumentResponse.class).getBody());
	}

	@Async
	public CompletableFuture<ResponseEntity<ApiResponse>> uploadFileAsync(
	        String docIdAndFileUrl, MultipartFile file, FileUploadDTO fileupload) {

        try {
            validateUrl(docIdAndFileUrl);
        } catch (ApplicationException e) {
            throw new ApplicationException("Invalid EDMS URL detected", e);
        }
	    MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
	    
	    File convertedFile = convert(file);
	    logger.info(" convertedFile size 22222222::{}",convertedFile.length());
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    HttpEntity<FileSystemResource> fileEntity = new HttpEntity<>(new FileSystemResource(convertedFile), headers);

		bodyMap.add(FILE_NEW, fileEntity);

		bodyMap.add(FILE_NEW, new FileSystemResource(convertedFile));
	    bodyMap.add(MODEL, fileupload.getSubscriberUid() + " _Video " + AppUtil.getDate());

	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            return restTemplate.exchange(docIdAndFileUrl, HttpMethod.POST, requestEntity, ApiResponse.class);
	        } catch (HttpClientErrorException e) {
	            	logger.error(UNEXPECTED_EXCEPTION, e);

	            ApiResponse errorResponse = new ApiResponse();
	            errorResponse.setSuccess(false);
	            errorResponse.setMessage("Upload failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

	            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
	        } catch (Exception e) {
				logger.error(UNEXPECTED_EXCEPTION, e);

	            ApiResponse errorResponse = new ApiResponse();
	            errorResponse.setSuccess(false);
	            errorResponse.setMessage("File upload failed: " + e.getMessage());

	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    });
	}


	public void saveOnboardingLiveliness(FileUploadDTO fileUploadDTO, String downloadUrl) {
		OnboardingLiveliness onboardingLiveliness = new OnboardingLiveliness();
		onboardingLiveliness.setSubscriberUid(fileUploadDTO.getSubscriberUid());
		onboardingLiveliness.setRecordedTime(fileUploadDTO.getRecordedTime());
		onboardingLiveliness.setRecordedGeoLocation(fileUploadDTO.getRecordedGeoLocation());
		onboardingLiveliness.setVerificationFirst(fileUploadDTO.getVerificationFirst().name());
		onboardingLiveliness.setVerificationSecond(fileUploadDTO.getVerificationSecond().name());
		onboardingLiveliness.setVerificationThird(fileUploadDTO.getVerificationThird().name());
		onboardingLiveliness.setTypeOfService(fileUploadDTO.getTypeOfService().name());
		onboardingLiveliness.setUrl(downloadUrl);
		onboardingLivelinessRepository.save(onboardingLiveliness);
	}


	@Async
	public CompletableFuture<ApiResponse> createThumbnailOfSelfie(Selfie image) {
		try {
			if (image != null) {
				byte[] imgBytes = Base64.getDecoder().decode(image.getSubscriberSelfie());
				InputStream imgInputStream = new ByteArrayInputStream(imgBytes);

				BufferedImage originalImage = ImageIO.read(imgInputStream);
				if (originalImage == null) {
					return CompletableFuture.completedFuture(AppUtil.createApiResponse(false,
							messageSource.getMessage("api.error.invalid.image.format", null, Locale.ENGLISH), null));
				}

				BufferedImage thumbnail = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 100,
						Scalr.OP_ANTIALIAS);

				ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "jpeg", thumbOutput);

				byte[] thumbnailBytes = thumbOutput.toByteArray();
				String base64EncodedThumbnail = Base64.getEncoder().encodeToString(thumbnailBytes);

				logger.info(CLASS + " createThumbnailOfSelfie: Selfie Thumbnail Generated Successfully");

				return CompletableFuture.completedFuture(AppUtil.createApiResponse(true, messageSource
								.getMessage("api.response.selfie.thumbnail.generated.successfully", null, Locale.ENGLISH),
						base64EncodedThumbnail));
			} else {
				return CompletableFuture.completedFuture(AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.selfie.cant.be.null.or.empty", null, Locale.ENGLISH),
						null));
			}
		} catch (Exception e) {
			logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " createThumbnailOfSelfie Exception {}", e.getMessage());
			return CompletableFuture.completedFuture(AppUtil.createApiResponse(false, messageSource.getMessage(
					SOMETHING_WENT_WRONG, null, Locale.ENGLISH), null));
		}
	}


	public ApiResponse createThumlbnailOfSelfie(Selfie image) throws IOException {
		try {
			if (image != null) {
				logger.info(CLASS + " createThumlbnailOfSelfie ");
				byte[] img = Base64.getDecoder().decode(image.getSubscriberSelfie());
				Resource fileRes = getTestFile(img, "selfieThumbnail", JPEG);

				ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
				BufferedImage thumbImg = null;
				BufferedImage img2 = ImageIO.read(fileRes.getInputStream());
				thumbImg = Scalr.resize(img2, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 100, Scalr.OP_ANTIALIAS);
				ImageIO.write(thumbImg, "jpeg", thumbOutput);
				byte[] data = thumbOutput.toByteArray();
				String base64EncodedImageBytes = Base64.getEncoder().encodeToString(data);
				logger.info(CLASS + " createThumlbnailOfSelfie Selfie Thumbnail Genrated Succssfully ");
				return exceptionHandlerUtil.createSuccessResponse("api.response.selfie.thumbnail.genrated.succssfully",
						base64EncodedImageBytes);
			} else {
				return exceptionHandlerUtil.createErrorResponse("api.error.selfie.cant.be.null.or.empty");
			}

		} catch (Exception e) {
			logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + " createThumlbnailOfSelfie Exception {}", e.getMessage());
			return exceptionHandlerUtil.handleException(e);
		}
	}



	public ApiResponse saveVideoToEdms(MultipartFile file, FileUploadDTO fileupload)  {
		try {
			logger.info(CLASS + " saveVideoToEdms req fileupload {} and File {} ", fileupload,
					file.getOriginalFilename());

			if (file.isEmpty()) {
				return exceptionHandlerUtil.createErrorResponse("api.error.video.cant.be.null.or.empty");
			}

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return exceptionHandlerUtil.createErrorResponse("api.error.vedio.content.type.isnot.mp4");
            }
			String docIdUrl = baselocalUrl + DOCUMENTS;

            validateEdmsUrl(docIdUrl);
			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
			logger.info(CLASS + " saveVideoToEdms req for get DocId docIdUrl {} and requestEntity {}", docIdUrl,
					requestEntity);
			ResponseEntity<DocumentResponse> documentId = restTemplate.exchange(docIdUrl, HttpMethod.POST,
					requestEntity, DocumentResponse.class);
			logger.info(CLASS + " saveVideoToEdms res for get DocId {}", documentId);

            String docIdAndFileUrl = baselocalUrl + DOC +
                    Objects.requireNonNull(documentId.getBody(), DOCUMNETS_RESPONSE_BODY_IS_NULL).getId()
                    + FILES;
validateEdmsUrlOrThrow(docIdAndFileUrl);

			MultiValueMap<String, Object> bodyMap1 = new LinkedMultiValueMap<>();
			bodyMap1.add(FILE_NEW, new FileSystemResource(convert(file)));

			bodyMap1.add(MODEL, fileupload.getSubscriberUid() + " _Video " + AppUtil.getDate());
			bodyMap1.add(ACTION, 1);
			HttpHeaders headers1 = new HttpHeaders();
			headers1.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity1 = new HttpEntity<>(bodyMap1, headers1);
			logger.info(CLASS + " saveVideoToEdms req for saveFileWithDocId docIdAndFileUrl {} and requestEntity1 {}",
					docIdAndFileUrl, requestEntity1);
			ResponseEntity<ApiResponse> result = restTemplate.exchange(docIdAndFileUrl, HttpMethod.POST, requestEntity1,
					ApiResponse.class);
            logger.info("{} saveVideoToEdms res for saveFileWithDocId {}", CLASS, result);

            if (result.getStatusCode() == HttpStatus.ACCEPTED) {

                String download = edmsDwonlodUrl +
                        Objects.requireNonNull(documentId.getBody(), DOCUMNETS_RESPONSE_BODY_IS_NULL).getId()
                        + FILES_DOCUMENTS;
				logger.info(CLASS + " saveVideoToEdms downloadVideoUrl {}", download);
                OnboardingLiveliness onboardingLiveliness = new OnboardingLiveliness();
                onboardingLiveliness.setSubscriberUid(fileupload.getSubscriberUid());
                onboardingLiveliness.setRecordedTime(fileupload.getRecordedTime());
                onboardingLiveliness.setRecordedGeoLocation(fileupload.getRecordedGeoLocation());
                onboardingLiveliness.setVerificationFirst(fileupload.getVerificationFirst().name());
                onboardingLiveliness.setVerificationSecond(fileupload.getVerificationSecond().name());
                onboardingLiveliness.setVerificationThird(fileupload.getVerificationThird().name());
                onboardingLiveliness.setTypeOfService(fileupload.getTypeOfService().name());
                onboardingLiveliness.setUrl(download);
                onboardingLivelinessRepository.save(onboardingLiveliness);
                logger.info(CLASS + " saveVideoToEdms true Video uploaded successfully ");
                return exceptionHandlerUtil.successResponse("api.response.video.uploaded.successfully");
            }
			logger.error(CLASS + " saveVideoToEdms false Something went wrong. Try after sometime 2");

			return exceptionHandlerUtil.createErrorResponse(SOMETHING_WENT_WRONG);

		} catch (Exception e) {
			logger.error(UNEXPECTED_EXCEPTION, e);
			logger.error(CLASS + "saveVideoToEdms Exception {}", e.getMessage());
			sentryClientExceptions.captureTags(fileupload.getSubscriberUid(), null, "saveVideoToEdms",
					"VideoUploadUrl");
			sentryClientExceptions.captureExceptions(e);
			return exceptionHandlerUtil.handleException(e);
		}
	}
    private void validateEdmsUrl(String url) {

        try {
            validateUrl(url);
        } catch (ApplicationException e) {
            throw new ApplicationException("Invalid EDMS URL detected", e);
        }
    }

	public static File convert(MultipartFile file) {

		
		File folder = new File(System.getProperty("catalina.home"), "ObTempFiles");
		
		// Create a File object representing the folder

		File convFile = new File(folder.getAbsolutePath() + File.separator + file.getOriginalFilename());
		if (folder.exists()) {
			logger.info("Folder already exists. PATH ::{}" , folder.getAbsolutePath());
			try {
				if (convFile.createNewFile()) {
					try (FileOutputStream fos = new FileOutputStream(convFile)) {
						fos.write(file.getBytes());
					}
				} else {
					logger.warn("File already exists: {}", convFile.getAbsolutePath());
				}
			} catch (IOException e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			}
        } else {
			// Create the folder
			boolean created = folder.mkdir();
			// Check if the folder creation was successful
			if (created) {
			logger.info("Folder created successfully. PATH ::{}" , folder.getAbsolutePath());
			} else {
				logger.info("Failed to create the folder.");
			}
			try {
				boolean fileCreated = convFile.createNewFile();

				if (!fileCreated) {
					logger.warn("File already exists: {}", convFile.getAbsolutePath());
				}

				try (FileOutputStream fos = new FileOutputStream(convFile)) {
					fos.write(file.getBytes());
				}

			} catch (IOException e) {
				logger.error(UNEXPECTED_EXCEPTION, e);
			}
        }
        return convFile;
    }




	public static Resource getTestFile(byte[] bytes, String prefix, String suffix) throws IOException {
		testFile = Files.createTempFile(prefix, suffix);
		Files.write(testFile, bytes);

		return new FileSystemResource(testFile.toFile());
	}

}