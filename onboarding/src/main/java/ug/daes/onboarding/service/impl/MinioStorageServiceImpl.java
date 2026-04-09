package ug.daes.onboarding.service.impl;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import io.minio.*;
import io.minio.http.Method;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ug.daes.onboarding.dto.FileUploadDTO;
import ug.daes.onboarding.dto.Selfie;
import ug.daes.onboarding.exceptions.ApplicationException;
import ug.daes.onboarding.model.OnboardingLiveliness;
import ug.daes.onboarding.repository.OnboardingLivelinessRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class MinioStorageServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(MinioStorageServiceImpl.class);
    private static final String CLASS = "MinioStorageServiceImpl";
    private static final String EXCEPTION = "Unexpected exception";
    private static final String PATH_SEPARATOR = "/";
    private final MinioClient minioClient;
    private final MessageSource messageSource;
    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private final OnboardingLivelinessRepository onboardingLivelinessRepository;

    public MinioStorageServiceImpl(
            MinioClient minioClient,
            MessageSource messageSource,
            ExceptionHandlerUtil exceptionHandlerUtil,
            OnboardingLivelinessRepository onboardingLivelinessRepository) {

        this.minioClient = minioClient;
        this.messageSource = messageSource;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.onboardingLivelinessRepository = onboardingLivelinessRepository;
    }

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.expiry.days}")
    private int expiryDays;

    @Value("${minio.url}")
    private String minioEndpoint;


    @Value("${app.base.url}")
    private String baseUrl;



    private String generateFileName(String prefix, String extension) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS")
                .format(new java.util.Date());
        String randomId = java.util.UUID.randomUUID().toString().substring(0, 4);
        return prefix + "_" + timestamp + "_" + randomId + extension;
    }


    private void ensureBucketExists() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new ApplicationException("Failed to ensure bucket exists", e);
        }
    }

    @Async
    public CompletableFuture<ApiResponse> saveFileToMinio(Object fileContent,
                                                          String fileType,
                                                          FileUploadDTO fileupload) {
        try {

            logger.info("{}{} - Request to save file to MinIO with fileType: {} and fileUpload: {}",
                    CLASS, Utility.getMethodName(), fileType, fileupload);

            if (fileContent == null
                    || (fileContent instanceof MultipartFile multipartFile
                    && multipartFile.isEmpty())) {

                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse(
                                "api.error.file.cant.be.null.or.empty"));
            }
            // Handle file based on type
            if ("video".equalsIgnoreCase(fileType) && fileContent instanceof MultipartFile multipartFile) {
                return uploadVideo(multipartFile, fileupload);
            }
            else if ("selfie".equalsIgnoreCase(fileType) && fileContent instanceof Selfie selfie) {
                return uploadSelfie(selfie);
            }
            else {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse("api.error.invalid.file.type"));
            }

        } catch (Exception e) {
            logger.error("{} saveFileToMinio Exception: {}", CLASS, e.getMessage(), e);
            return CompletableFuture.completedFuture(exceptionHandlerUtil.handleException(e));
        }
    }

    private ApiResponse uploadFile(InputStream inputStream, long size, String path, String contentType) {
        try {
            ensureBucketExists();

            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            // Generate presigned URL
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(path)
                            .expiry(expiryDays * 24 * 3600)
                            .build()
            );

            // Return response

            return exceptionHandlerUtil.createSuccessResponse("api.response.file.upload",presignedUrl);

        } catch (Exception e) {
            // Handle error and return error response
            return exceptionHandlerUtil.createErrorResponse("api.error.something.went.wrong.please.try.after.sometime");

        }
    }


    @Async
    public CompletableFuture<ApiResponse> uploadSelfie(Selfie image) {
        try {
            byte[] img = Base64.getDecoder().decode(image.getSubscriberSelfie());

            if (img.length == 0) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse(
                                "api.error.file.cant.be.null.or.empty"));
            }

            String fileName = generateFileName("selfie", ".jpeg");
            String path = image.getSubscriberUniqueId() + "/selfie/" + fileName;

            ApiResponse res = uploadFile(
                    new ByteArrayInputStream(img),
                    img.length,
                    path,
                    "image/jpeg");

            if (!res.isSuccess()) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse(
                                "api.error.selfie.upload.failed", null));
            }

            CompletableFuture<ApiResponse> selfieURI =
                    generateSelfieURI(image.getSubscriberUniqueId(), fileName);

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.createSuccessResponse(
                            "api.response.selfie.uploaded.successfully",
                            selfieURI.get().getResult()));

        } catch (InterruptedException e) {

            // ✅ Restore interrupt status (Sonar fix)
            Thread.currentThread().interrupt();

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e));

        } catch (Exception e) {

            logger.error(CLASS + " uploadSelfie Exception {}", e.getMessage());

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e));
        }
    }
    @Async
    public CompletableFuture<ApiResponse> uploadVideo(MultipartFile file, FileUploadDTO fileupload) {
        try {

            if (file.isEmpty() || fileupload.getSubscriberUid() == null) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse(
                                "api.error.video.cant.be.null.or.empty"));
            }

            String contentType = file.getContentType();

            if (contentType == null || !contentType.startsWith("video/")) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse(
                                "api.error.video.content.type.is.not.mp4"));
            }

            File tempFile = convertTempFile(file);
            String fileName = generateFileName("video", ".mp4");
            String path = fileupload.getSubscriberUid() + "/video/" + fileName;

            // ✅ Try-with-resources (fixes Sonar warning)
            try (FileInputStream fis = new FileInputStream(tempFile)) {

                ApiResponse res = uploadFile(
                        fis,
                        tempFile.length(),
                        path,
                        "video/mp4");

                if (!res.isSuccess()) {
                    return CompletableFuture.completedFuture(
                            exceptionHandlerUtil.createErrorResponse(
                                    "api.error.video.upload.failed", null));
                }
            } finally {
                deleteTempFile(tempFile); // always executed
            }

            CompletableFuture<ApiResponse> videoURI =
                    generateVideoURI(fileupload.getSubscriberUid(), fileName);

            saveOnboardingLiveliness(
                    fileupload,
                    videoURI.get().getResult().toString());

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.successResponse(
                            "api.response.video.uploaded.successfully"));

        } catch (InterruptedException e) {


            Thread.currentThread().interrupt();

            logger.error("{} uploadVideo InterruptedException {}", CLASS, e.getMessage());

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e));

        } catch (Exception e) {

            logger.error("{} uploadVideo Exception {}", CLASS, e.getMessage());

            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e));
        }
    }
    private void deleteTempFile(File tempFile) {
        try {
            Files.deleteIfExists(tempFile.toPath());
        } catch (IOException e) {
            logger.error("Error deleting temp file: {}", tempFile.getAbsolutePath(), e);
        }
    }

    public ApiResponse deleteFile(String subscriberUid, String folder, String fileName) {
        try {

            String path = subscriberUid + PATH_SEPARATOR
                    + folder + PATH_SEPARATOR
                    + fileName;
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );

            return exceptionHandlerUtil.createSuccessResponse("api.response.file.delete", null);
        } catch (Exception e) {
            logger.error(CLASS + " deleteFile Exception {}", e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }




    @Async
    public CompletableFuture<ApiResponse> generateSelfieURI(String subscriberUid, String fileName) {
        try {
            if (subscriberUid == null || fileName == null) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse("api.error.invalid.thumbnail.request")
                );
            }

            String downloadUrl = baseUrl + "/api/documents/" + subscriberUid + "/selfie/" + fileName + "/download";


           logger.info("Selfie URL: {}",downloadUrl);


            // Return the URL (do NOT create presigned MinIO link)
            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.createSuccessResponse(
                            "api.response.selfie.thumbnail.generated.successfully",
                            downloadUrl
                    )
            );

        } catch (Exception e) {
            logger.error(EXCEPTION, e);
            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e)
            );
        }
    }

    @Async
    public CompletableFuture<ApiResponse> generateVideoURI(String subscriberUid, String fileName) {
        try {
            if (subscriberUid == null || fileName == null) {
                return CompletableFuture.completedFuture(
                        exceptionHandlerUtil.createErrorResponse("api.error.invalid.video.uri.request")
                );
            }

            // Build download API link (similar to EDMS)
            String downloadUrl = baseUrl + "/api/documents/" + subscriberUid + "/video/" + fileName + "/download";


            logger.info("Video URL: {}" , downloadUrl);


            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.createSuccessResponse(
                            "api.response.video.uri.generated.successfully",
                            downloadUrl
                    )
            );

        } catch (Exception e) {
            logger.error(EXCEPTION, e);
            return CompletableFuture.completedFuture(
                    exceptionHandlerUtil.handleException(e)
            );
        }
    }



    private File convertTempFile(MultipartFile file) throws IOException {
        File temp = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(file.getBytes());
        }
        return temp;
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
            logger.error(EXCEPTION, e);
            logger.error(CLASS + " createThumbnailOfSelfie Exception {}", e.getMessage());
            return CompletableFuture.completedFuture(AppUtil.createApiResponse(false, messageSource.getMessage(
                    "api.error.something.went.wrong.please.try.after.sometime", null, Locale.ENGLISH), null));
        }
    }

    private void saveOnboardingLiveliness(FileUploadDTO dto, String url) {
        OnboardingLiveliness entity = new OnboardingLiveliness();
        entity.setSubscriberUid(dto.getSubscriberUid());
        entity.setRecordedTime(dto.getRecordedTime());
        entity.setRecordedGeoLocation(dto.getRecordedGeoLocation());
        entity.setVerificationFirst(dto.getVerificationFirst().name());
        entity.setVerificationSecond(dto.getVerificationSecond().name());
        entity.setVerificationThird(dto.getVerificationThird().name());
        entity.setTypeOfService(dto.getTypeOfService().name());
        entity.setUrl(url);
        onboardingLivelinessRepository.save(entity);
    }
}
