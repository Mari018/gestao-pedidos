package com.gestao.pedidos.service;

import com.gestao.pedidos.model.ErrorLog;
import com.gestao.pedidos.model.User;
import com.gestao.pedidos.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ExternalValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalValidationService.class);

    private final WebClient webClient;
    private final ErrorLogRepository errorLogRepository;

   @Value("${external.validation.api.url}")
    private final String apiUrl;

    @Value("${external.validation.api.timeout:5000}")
    private final int timeoutMs;

    public ExternalValidationService(WebClient webClient, ErrorLogRepository errorLogRepository, String apiUrl, int timeoutMs) {
        this.webClient = webClient;
        this.errorLogRepository = errorLogRepository;
        this.apiUrl = apiUrl;
        this.timeoutMs = timeoutMs;
    }

    public ValidationResult validateClient(User user) {
        LOGGER.info("Validating client: {}", user.getEmail());

        try {
            String response = webClient.get()
                    .uri(apiUrl + "?email=" + user.getEmail())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .onErrorReturn("VALIDATION_FAILED")
                    .block();

            boolean isValid = response != null && !response.equals("VALIDATION_FAILED") && !response.equals("[]");

            ValidationResult result = ValidationResult.builder()
                    .isValid(isValid)
                    .message(isValid ? "Client validated successfully" : "Client validation failed")
                    .validatedAt(LocalDateTime.now())
                    .build();

            LOGGER.info("Client validation result for {}: {}", user.getEmail(), result.isValid());
            return result;

        } catch (Exception e) {
            LOGGER.error("Error validating client {}: {}", user.getEmail(), e.getMessage());

            ErrorLog errorLog = ErrorLog.builder()
                    .errorType("EXTERNAL_VALIDATION_ERROR")
                    .errorMessage("Failed to validate client: " + e.getMessage())
                    .stackTrace(getStackTraceAsString(e))
                    .endpoint(apiUrl)
                    .httpMethod("GET")
                    .userEmail(user.getEmail())
                    .occurredAt(LocalDateTime.now())
                    .build();

            errorLogRepository.save(errorLog);

            return ValidationResult.builder()
                    .isValid(false)
                    .message("External validation service unavailable: " + e.getMessage())
                    .validatedAt(LocalDateTime.now())
                    .build();
        }
    }

    private String getStackTraceAsString(Exception e) {
        return java.util.Arrays.toString(e.getStackTrace());
    }

    public static class ValidationResult {
        private boolean isValid;
        private String message;
        private LocalDateTime validatedAt;

        public static ValidationResultBuilder builder() {
            return new ValidationResultBuilder();
        }

        public boolean isValid() { return isValid; }
        public String getMessage() { return message; }
        public LocalDateTime getValidatedAt() { return validatedAt; }

        public static class ValidationResultBuilder {
            private boolean isValid;
            private String message;
            private LocalDateTime validatedAt;

            public ValidationResultBuilder isValid(boolean isValid) {
                this.isValid = isValid;
                return this;
            }

            public ValidationResultBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ValidationResultBuilder validatedAt(LocalDateTime validatedAt) {
                this.validatedAt = validatedAt;
                return this;
            }

            public ValidationResult build() {
                ValidationResult result = new ValidationResult();
                result.isValid = this.isValid;
                result.message = this.message;
                result.validatedAt = this.validatedAt;
                return result;
            }
        }
    }
}
