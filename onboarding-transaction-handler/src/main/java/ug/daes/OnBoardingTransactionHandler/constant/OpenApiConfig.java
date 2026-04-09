package ug.daes.OnBoardingTransactionHandler.constant;


import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer globalResponses() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();

            responses.addApiResponse("400", new ApiResponse()
                    .description("Bad Request – invalid input or path variable"));
            responses.addApiResponse("404", new ApiResponse()
                    .description("Not Found"));
            responses.addApiResponse("500", new ApiResponse()
                    .description("Internal Server Error"));

            return operation;
        };
    }

}
