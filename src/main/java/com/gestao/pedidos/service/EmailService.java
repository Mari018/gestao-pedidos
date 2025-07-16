package com.gestao.pedidos.service;

import com.gestao.pedidos.model.ErrorLog;
import com.gestao.pedidos.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final ErrorLogRepository errorLogRepository;

    @Value("${application.email.error-report.recipient}")
    private String errorReportRecipient;

    @Value("${application.email.error-report.subject}")
    private String errorReportSubject;

    public EmailService(JavaMailSender mailSender, ErrorLogRepository errorLogRepository) {
        this.mailSender = mailSender;
        this.errorLogRepository = errorLogRepository;
    }


    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@gestao-pedidos.com");

            mailSender.send(message);
            LOGGER.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            LOGGER.error("Failed to send email to {}: {}", to, e.getMessage());

            ErrorLog errorLog = ErrorLog.builder()
                    .errorType("EMAIL_SENDING_ERROR")
                    .errorMessage("Failed to send email to " + to + ": " + e.getMessage())
                    .stackTrace(e.getStackTrace().toString())
                    .endpoint("EMAIL_SERVICE")
                    .httpMethod("SEND")
                    .build();

            errorLogRepository.save(errorLog);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Todos os dias às 9:00
    public void sendDailyErrorReport() {
        LOGGER.info("Sending daily error report");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime today = LocalDateTime.now();

        List<ErrorLog> recentErrors = errorLogRepository.findByOccurredAtBetween(yesterday, today);

        if (recentErrors.isEmpty()) {
            LOGGER.info("No errors to report for yesterday");
            return;
        }

        String reportBody = generateErrorReportBody(recentErrors);
        String subject = errorReportSubject + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        sendEmail(errorReportRecipient, subject, reportBody);
    }

    private String generateErrorReportBody(List<ErrorLog> errors) {
        StringBuilder report = new StringBuilder();
        report.append("Relatório Diário de Erros - Sistema de Gestão de Pedidos\n");
        report.append("=========================================================\n\n");
        report.append("Total de erros nas últimas 24 horas: ").append(errors.size()).append("\n\n");

        errors.stream()
                .collect(java.util.stream.Collectors.groupingBy(ErrorLog::getErrorType))
                .forEach((type, errorList) -> {
                    report.append("Tipo de Erro: ").append(type).append("\n");
                    report.append("Quantidade: ").append(errorList.size()).append("\n");
                    report.append("Erros:\n");

                    errorList.forEach(error -> {
                        report.append("  - ").append(error.getOccurredAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                                .append(" | ").append(error.getErrorMessage()).append("\n");
                        if (error.getEndpoint() != null) {
                            report.append("    Endpoint: ").append(error.getEndpoint()).append("\n");
                        }
                        if (error.getUserEmail() != null) {
                            report.append("    Utilizador: ").append(error.getUserEmail()).append("\n");
                        }
                    });
                    report.append("\n");
                });

        report.append("=========================================================\n");
        report.append("Este relatório foi gerado automaticamente pelo sistema.\n");
        report.append("Data/Hora: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return report.toString();
    }
}
