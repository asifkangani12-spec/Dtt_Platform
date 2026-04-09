package com.dtt.service.impl;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.model.GenericCard;
import com.dtt.repo.CardRepo;
import com.dtt.requestdto.CardDto;
import com.dtt.requestdto.SubscriberDetailsDto;
import com.dtt.responsedto.CardResponseDTO;
import com.dtt.service.iface.CardIface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

@Service
public class CardImpl implements CardIface {

	private static final Logger logger = LoggerFactory.getLogger(CardImpl.class);

	private final CardRepo cardRepo;

	@Value("${card.Coordinates}")
	public String cardCoordinates;
	@Value("${face.crop}")
	public String url;

	@Value("${card.expiry.years}")
	public long cardExpiryYears;

	@Value("${qr.embed.api.url}")
	private String qrEmbedApiUrl;

	@Value("${qr.embed.credentialId}")
	private String qrCredentialId;

	@Value("${card.title}")
	private String cardTitle;

	@Value("${card.title.coordinates}")
	private String cardTitleCoordinates;

	@Value("${qr.coordinates.x}")
	private String qrXCoordinate;

	@Value("${qr.coordinates.y}")
	private String qrYCoordinate;

	@Value("${card.pdf.name}")
	private String pdfCard;

	private static final String FIELD_PHOTO = "photo";


	private final ExceptionHandlerUtil exceptionHandlerUtil;

	RestTemplate restTemplate = new RestTemplate();

	static final String CLASS = "PidImpl";

	public CardImpl(CardRepo cardRepo, ExceptionHandlerUtil exceptionHandlerUtil) {
		this.cardRepo = cardRepo;
		this.exceptionHandlerUtil = exceptionHandlerUtil;
	}

	public String faceCrop(String image){

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String jsonPayload = String.format("{\"image\":\"%s\"}", image);

		HttpEntity<String> reqEntity = new HttpEntity<>(jsonPayload, headers);

		try {

			ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);


			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(res.getBody());



			return rootNode.path("result").asText();
		} catch (Exception e) {
			return "";
		}
	}


    public String modifyUAEIDCard(CardDto drivingLicenseDto) {
        try {

            ClassPathResource resource = new ClassPathResource(pdfCard);
            byte[] existingPdfBytes = Files.readAllBytes(Paths.get(resource.getURI()));

            PDDocument document = Loader.loadPDF(existingPdfBytes);

            Field[] fields = drivingLicenseDto.getClass().getDeclaredFields();

            renderTitle(document);

            for (Field field : fields) {
                processField(document, field, drivingLicenseDto);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();

            byte[] modifiedPdfBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(modifiedPdfBytes);

        } catch (Exception e) {
            logger.error("Unexpected error occurred:", e);
            return "";
        }
    }
    private void renderTitle(PDDocument document) throws IOException {

        // --- Extract title Y coordinate ---
        String[] titleCoords = cardTitleCoordinates.split(",");
        float titleY = Float.parseFloat(titleCoords[1]);

        // --- Extract title X coordinate from photo coordinates ---
        JsonNode coordsNode = jsonconvertion(cardCoordinates);
        String[] photoCoords = coordsNode.get(FIELD_PHOTO).asText().split(",");
        float titleX = Float.parseFloat(photoCoords[0]);

        PDPage page = document.getPage(0);

        try (PDPageContentStream contentStream =
                     new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
            contentStream.newLineAtOffset(titleX, titleY);
            contentStream.showText(cardTitle);
            contentStream.endText();
        }
    }

    private void processField(PDDocument document, Field field, CardDto dto) {

        int pageNumber = 1;

        try {

            PDPage page = document.getPage(pageNumber - 1);

            JsonNode jsonNode = jsonconvertion(cardCoordinates);
            String input = jsonNode.get(field.getName()).asText();
            String[] parts = input.split(",");

            float x = Float.parseFloat(parts[0].trim());
            float y = Float.parseFloat(parts[1].trim());

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                // ===== PHOTO FIELD =====
                if (FIELD_PHOTO.equals(field.getName())) {

                    byte[] imageBytes = Base64.getDecoder().decode(dto.getPhoto());

                    PDImageXObject image =
                            PDImageXObject.createFromByteArray(document, imageBytes, FIELD_PHOTO);

                    contentStream.drawImage(image, x, y, 225, 225);
                    return;
                }

                String value = (String) field.get(dto);
                if (value == null) value = "";


                contentStream.beginText();
                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        20
                );

                contentStream.newLineAtOffset(x, y);
                contentStream.showText(value.toUpperCase());
                contentStream.endText();
            }

        } catch (Exception e) {
            logger.error("Error processing field: " + field.getName(), e);
        }
    }


	public JsonNode jsonconvertion(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {

			return  objectMapper.readTree(jsonString);

		} catch (Exception e) {
			logger.error("Unexpected error:", e);
		}
		return null;
	}

	private String formatDate(String date) {
		try {
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date parsedDate = originalFormat.parse(date);
			return targetFormat.format(parsedDate);
		} catch (ParseException e) {
			return date;
		}
	}





	public String imageToBase64(String imageUrl) {

		try {


			HttpHeaders headers = new HttpHeaders();


			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<byte[]> response = restTemplate.exchange(
					imageUrl,
					HttpMethod.GET,
					entity,
					byte[].class
			);

			if (response.getStatusCode().is2xxSuccessful()) {
				byte[] imageBytes = response.getBody();

				return Base64.getEncoder().encodeToString(imageBytes);
			} else {
				logger.info("Image not found");
				return "";
			}
		}catch (Exception e){
			return "";
		}

	}


	@Override
	public ApiResponse getPidByIdDocNumber(String idDocNumber) {

		try {

			GenericCard card = cardRepo.findByIdDocNumber(idDocNumber);
			logger.info("Card response: {}", card);

			if (card != null) {

				logger.info("pid already in DB::::{}", card.getFullName());

				CardResponseDTO cardResponseDTO = new CardResponseDTO();
				cardResponseDTO.setFullName(card.getFullName());
				cardResponseDTO.setGender(card.getGender());
				cardResponseDTO.setNationality(card.getNationality());

				cardResponseDTO.setMobileNumber(card.getMobileNumber());

				cardResponseDTO.setEmail(card.getEmail());
				cardResponseDTO.setAddress(card.getAddress());
				cardResponseDTO.setIdDocNumber(card.getIdDocNumber());
				cardResponseDTO.setCardNumber(card.getCardNumber());

				cardResponseDTO.setDateOfBirth(formatDate(card.getDateOfBirth()));
				cardResponseDTO.setPidIssueDate(formatDate(card.getPidIssueDate()));
				cardResponseDTO.setPidExpiryDate(formatDate(card.getPidExpiryDate()));

				cardResponseDTO.setPhoto(card.getPhoto());

				return exceptionHandlerUtil.createSuccessResponse("api.response.pid.fetched", cardResponseDTO);
			} else {

				logger.info("PID is not present in DB");

				SubscriberDetailsDto subscriberDetailsDto = cardRepo.getSubscriberDetailsByDocumentNumber(idDocNumber);
				logger.info("Response: {}", subscriberDetailsDto);
				if(subscriberDetailsDto==null){
					return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");

				}

				logger.info("Date of Birth: {}", subscriberDetailsDto.getDateOfBirth());
				logger.info("Mobile Number: {}", subscriberDetailsDto.getMobileNumber());

				CardDto pidDto1 = new CardDto();
				pidDto1.setName(subscriberDetailsDto.getFullName());

				pidDto1.setDateOfBirth(formatDate(subscriberDetailsDto.getDateOfBirth().substring(0,10)));
				String createdOn = subscriberDetailsDto.getCreatedOn().substring(0, 10);
				pidDto1.setPidIssueDate(formatDate(createdOn));

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				String expiryDate = LocalDate.parse(createdOn, formatter)
						.plusYears(cardExpiryYears)
						.format(formatter);

				ObjectMapper mapper = new ObjectMapper();

				JsonNode rootNode = mapper.readTree(subscriberDetailsDto.getOnboardingDataFieldsJson());

				String nationality = rootNode.get("nationality").asText();

				logger.info("Nationality: {}", nationality);


				pidDto1.setPidExpiryDate(formatDate(expiryDate));
				pidDto1.setNationality(nationality);
				pidDto1.setPhoto(subscriberDetailsDto.getSelfie());
				pidDto1.setCardNumber(idDocNumber);

				GenericCard card1 = new GenericCard();
				card1.setFullName(subscriberDetailsDto.getFullName());
				card1.setDateOfBirth(subscriberDetailsDto.getDateOfBirth().substring(0,10));
				card1.setNationality(nationality);
				card1.setCountryCode(nationality);
				card1.setMobileNumber(subscriberDetailsDto.getMobileNumber());
				card1.setEmail(subscriberDetailsDto.getEmailId());
				card1.setAddress(nationality);
				card1.setPhoto(subscriberDetailsDto.getSelfie());
				card1.setIdDocNumber(idDocNumber);
				card1.setPidIssueDate(createdOn);
				card1.setPidExpiryDate(expiryDate);
				card1.setCardNumber(idDocNumber);

				card1.setPidDocument(null);

				card1.setGender(subscriberDetailsDto.getGender());
				card1.setSubscriberUid(subscriberDetailsDto.getSubscriberUid());
				card1.setCreatedOn(AppUtil.getCurrentDate());
				card1.setUpdatedOn(AppUtil.getCurrentDate());

				cardRepo.save(card1);

				logger.info("PID saved Successfully");


				CardResponseDTO cardResponseDTO = new CardResponseDTO();
				cardResponseDTO.setFullName(card1.getFullName());
				cardResponseDTO.setGender(card1.getGender());
				cardResponseDTO.setNationality(card1.getNationality());


				cardResponseDTO.setMobileNumber(card1.getMobileNumber());

				cardResponseDTO.setEmail(card1.getEmail());
				cardResponseDTO.setAddress(card1.getAddress());
				cardResponseDTO.setIdDocNumber(idDocNumber);
				cardResponseDTO.setCardNumber(idDocNumber);


				cardResponseDTO.setDateOfBirth(formatDate(card1.getDateOfBirth()));
				cardResponseDTO.setPidIssueDate(formatDate(card1.getPidIssueDate()));
				cardResponseDTO.setPidExpiryDate(formatDate(card1.getPidExpiryDate()));

				cardResponseDTO.setPhoto(card1.getPhoto());

				return exceptionHandlerUtil.createSuccessResponse("api.response.pid.fetched", cardResponseDTO);
			}
		} catch (Exception e) {
			return exceptionHandlerUtil.createErrorResponse( "api.error.something.went.wrong");
		}
	}




}
