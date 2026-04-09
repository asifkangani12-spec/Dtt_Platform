//package ug.daes.ra.exception;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import ug.daes.ra.utils.AppUtil;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//    @ExceptionHandler(RAServiceException.class)
//    public ResponseEntity<Object> handleRAServiceException(RAServiceException ex) {
//        logger.error("RA Service Exception occurred: {}", ex.getMessage());
//        return new ResponseEntity<>(AppUtil.createApiResponse(false, ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}