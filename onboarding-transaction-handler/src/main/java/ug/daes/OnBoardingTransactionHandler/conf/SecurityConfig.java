////package ug.daes.OnBoardingTransactionHandler.conf;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//////import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////import org.springframework.web.cors.CorsConfiguration;
////import org.springframework.web.cors.CorsConfigurationSource;
////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////
////import ug.daes.OnBoardingTransactionHandler.security.TokenValidationFilter;
////
////import java.util.Arrays;
////
////@Configuration
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true)
////public class SecurityConfig extends WebSecurityConfigurerAdapter {
////
////    @Autowired
////    private TokenValidationFilter tokenValidationFilter;
////
////    @Autowired
////    private AppCorsProperties corsProperties;
////
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .csrf().disable()
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .authorizeRequests()
////                .antMatchers("/public/**", "/actuator/**").permitAll()
////                .anyRequest().authenticated()
////                .and()
////                .addFilterBefore(tokenValidationFilter, UsernamePasswordAuthenticationFilter.class)
////                .headers()
////                .contentSecurityPolicy("default-src 'none'")
////                .and()
////                .httpStrictTransportSecurity().includeSubDomains(true).preload(true)
////                .and()
////                .frameOptions().deny()
////                .and()
////                .cors();
////    }
////
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
////        configuration.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
////        configuration.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
////        configuration.setAllowCredentials(true);
////
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }
////}
////
//
//
//
//package ug.daes.OnBoardingTransactionHandler.conf;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import ug.daes.OnBoardingTransactionHandler.security.TokenValidationFilter;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableMethodSecurity(prePostEnabled = true) // replaces @EnableGlobalMethodSecurity
//public class SecurityConfig {
//
//    private final TokenValidationFilter tokenValidationFilter;
//    private final AppCorsProperties corsProperties;
//
//    public SecurityConfig(TokenValidationFilter tokenValidationFilter, AppCorsProperties corsProperties) {
//        this.tokenValidationFilter = tokenValidationFilter;
//        this.corsProperties = corsProperties;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**","/v3/api-docs/**", "/actuator/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(tokenValidationFilter, UsernamePasswordAuthenticationFilter.class)
//                .headers(headers -> headers
//                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"))
//                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true))
//                        .frameOptions(frame -> frame.deny())
//                )
//                .cors(cors -> {}); // hook up to CorsConfigurationSource bean
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
//        configuration.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
//        configuration.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
//
