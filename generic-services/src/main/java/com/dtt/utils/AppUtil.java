//package com.dtt.utils;
//
//import com.dtt.requestdto.ApiResponse;
//import java.text.SimpleDateFormat;
//import java.util.Base64;
//import java.util.Date;
//import java.util.Optional;
//import java.util.UUID;
//
//public class AppUtil {
//
//    // Private constructor to prevent instantiation
//    private AppUtil() {
//        throw new IllegalStateException("Utility class");
//    }
//
//    // Generate UUID
//    public static String getUUId() {
//        UUID uuid = UUID.randomUUID();  // Local variable
//        return uuid.toString();
//    }
//
//    // Get current date
//    public static Date getCurrentDate() {
//        return new Date();
//    }
//
//    // Create API response
//    public static ApiResponse createApiResponse(boolean success, String msg, Object object) {
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.setMessage(msg);
//        apiResponse.setResult(object);
//        apiResponse.setSuccess(success);
//        return apiResponse;
//    }
//
//    // Convert byte array to Base64 string
//    public static String getBase64FromByteArr(byte[] bytes){
//        Base64.Encoder base64 = Base64.getEncoder();
//        return base64.encodeToString(bytes);
//    }
//
//    // Get formatted date-time
//    public static String getDate() {
//        SimpleDateFormat smpdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return smpdate.format(new Date());
//    }
//
//    // Get timestamp string from Date
//    public static String getTimeStampString(Date date) {
//        Date safeDate = Optional.ofNullable(date).orElse(new Date());
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        return formatter.format(safeDate);
//    }
//}