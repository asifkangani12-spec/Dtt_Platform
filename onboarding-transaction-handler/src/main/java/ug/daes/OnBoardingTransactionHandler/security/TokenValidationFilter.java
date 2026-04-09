//package ug.daes.OnBoardingTransactionHandler.security;
//
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import ug.daes.OnBoardingTransactionHandler.util.PropertiesUtil;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Collections;
//
//@Component
//public class TokenValidationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    PropertiesUtil propertiesUtil;
//
//   // String baseUrl = propertiesUtil;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${idp.token.username}")
//    private String username;
//
//    @Value("${idp.token.password}")
//    private String password;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        // Skip validation for public endpoints
//        if (path.startsWith("/public") || path.startsWith("/actuator") || path.startsWith("/v3/api-docs")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
//            return;
//        }
//
//        System.out.println(" authHeader ::::" + authHeader);
//
//        // Create Basic Auth header for IDP
//        String authStr = username + ":" + password;
//        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.UTF_8));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Basic " + base64Creds);
//
//        String token = authHeader.substring(7);
//        String idpTokenValidationURL = propertiesUtil.idpTokenValidation; // external validation API
//        System.out.println("idpTokenValidationURL ::"+idpTokenValidationURL);
//
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("token", token);
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
//
//        try {
//            ResponseEntity<String> validationResponse = restTemplate.postForEntity(propertiesUtil.idpTokenValidation, entity,
//                    String.class);
//            System.out.println(validationResponse);
//
//            // Check HTTP status
//            if (!validationResponse.getStatusCode().is2xxSuccessful()) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
//                return;
//            }
//
//            String body = validationResponse.getBody();
//            if (body == null) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
//                return;
//            }
//
//            // Parse JSON response and check 'active' field
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(body);
//            boolean active = jsonNode.path("active").asBoolean(false); // default to false if missing
//
//            if (!active) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is inactive");
//                return;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Validation API unreachable");
//            return;
//        }
//
//        // Set authentication in security context
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", null,
//                Collections.emptyList());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Continue request if token is valid
//        filterChain.doFilter(request, response);
//    }
//
//}
