package com.mywebcompanion.backendspring.service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.mywebcompanion.backendspring.model.Event;
import com.mywebcompanion.backendspring.model.EventReminder;
import com.mywebcompanion.enums.EventMode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:My Web Companion}")
    private String appName;

    /**
     * Envoie un rappel par email pour un événement
     */
    public void sendEventReminder(EventReminder reminder) {
        try {
            Event event = reminder.getEvent();
            String userEmail = event.getUser().getEmail();

            // Choisir entre email simple ou HTML selon vos préférences
            sendHtmlEventReminder(userEmail, event, reminder);

            log.info("Email de rappel envoyé avec succès pour l'événement '{}' à {}",
                    event.getTitle(), userEmail);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du rappel email pour l'événement {}: {}",
                    reminder.getEvent().getId(), e.getMessage());
            throw new RuntimeException("Échec envoi email", e);
        }
    }

    /**
     * Email simple en texte brut
     */
    public void sendSimpleEventReminder(String toEmail, Event event, EventReminder reminder) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(buildEmailSubject(event, reminder));
        message.setText(buildEmailContent(event, reminder));

        mailSender.send(message);
    }

    /**
     * Email HTML avec mise en forme
     */
    public void sendHtmlEventReminder(String toEmail, Event event, EventReminder reminder)
            throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(buildEmailSubject(event, reminder));
        helper.setText(buildHtmlEmailContent(event, reminder), true);

        mailSender.send(mimeMessage);
    }

    /**
     * Construit le sujet de l'email
     */
    private String buildEmailSubject(Event event, EventReminder reminder) {
        String timeText = getTimeRemainingText(reminder.getMinutesBefore());
        return String.format("🔔 Rappel: %s %s", event.getTitle(), timeText);
    }

    /**
     * Contenu email texte brut
     */
    private String buildEmailContent(Event event, EventReminder reminder) {
        StringBuilder content = new StringBuilder();

        content.append("Bonjour,\n\n");
        content.append("Ceci est un rappel pour votre événement :\n\n");

        // Titre
        content.append("📅 ").append(event.getTitle()).append("\n");

        // Date et heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
        content.append("🕒 ").append(event.getStartDate().format(formatter));

        if (!event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate()) ||
                !event.getStartDate().toLocalTime().equals(event.getEndDate().toLocalTime())) {
            content.append(" - ").append(event.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        content.append("\n");

        // Lieu ou mode
        if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
            content.append("📍 ").append(event.getLocation()).append("\n");
        }

        if (event.getMode() != null) {
            content.append("💼 Mode: ").append(event.getMode() == EventMode.PRESENTIEL ? "Présentiel" : "Distanciel")
                    .append("\n");
        }

        // Lien de réunion
        if (event.getMeetingLink() != null && !event.getMeetingLink().trim().isEmpty()) {
            content.append("🔗 Lien: ").append(event.getMeetingLink()).append("\n");
        }

        // Description
        if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            content.append("\n📝 Description:\n").append(event.getDescription()).append("\n");
        }

        content.append("\n");
        content.append("Temps restant: ").append(getTimeRemainingText(reminder.getMinutesBefore())).append("\n\n");
        content.append("Bonne journée !\n");
        content.append("L'équipe ").append(appName);

        return content.toString();
    }

    /**
     * Contenu email HTML avec belle mise en forme
     */
    private String buildHtmlEmailContent(Event event, EventReminder reminder) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 8px 8px 0 0; }");
        html.append(".content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }");
        html.append(".event-title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }");
        html.append(
                ".event-detail { margin: 10px 0; padding: 8px; background: white; border-left: 4px solid #667eea; }");
        html.append(
                ".time-remaining { background: #e74c3c; color: white; padding: 10px; border-radius: 5px; text-align: center; font-weight: bold; }");
        html.append(
                ".meeting-link { background: #27ae60; color: white; padding: 10px; text-decoration: none; border-radius: 5px; display: inline-block; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>🔔 Rappel d'événement</h1>");
        html.append("<p>Votre événement approche !</p>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");

        // Titre événement
        html.append("<div class='event-title'>📅 ").append(event.getTitle()).append("</div>");

        // Date et heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
        html.append("<div class='event-detail'>");
        html.append("<strong>🕒 Date:</strong> ").append(event.getStartDate().format(formatter));

        if (!event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate()) ||
                !event.getStartDate().toLocalTime().equals(event.getEndDate().toLocalTime())) {
            html.append(" - ").append(event.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        html.append("</div>");

        // Lieu
        if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
            html.append("<div class='event-detail'>");
            html.append("<strong>📍 Lieu:</strong> ").append(event.getLocation());
            html.append("</div>");
        }

        // Mode
        if (event.getMode() != null) {
            html.append("<div class='event-detail'>");
            html.append("<strong>💼 Mode:</strong> ")
                    .append(event.getMode() == EventMode.PRESENTIEL ? "Présentiel" : "Distanciel");
            html.append("</div>");
        }

        // Lien de réunion
        if (event.getMeetingLink() != null && !event.getMeetingLink().trim().isEmpty()) {
            html.append("<div class='event-detail'>");
            html.append("<strong>🔗 Rejoindre:</strong> ");
            html.append("<a href='").append(event.getMeetingLink()).append("' class='meeting-link'>Cliquer ici</a>");
            html.append("</div>");
        }

        // Description
        if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            html.append("<div class='event-detail'>");
            html.append("<strong>📝 Description:</strong><br>");
            html.append(event.getDescription().replace("\n", "<br>"));
            html.append("</div>");
        }

        // Temps restant
        html.append("<div class='time-remaining'>");
        html.append("⏰ ").append(getTimeRemainingText(reminder.getMinutesBefore()));
        html.append("</div>");

        html.append("<br><p>Bonne journée !<br><strong>L'équipe ").append(appName).append("</strong></p>");

        html.append("</div>"); // content
        html.append("</div>"); // container
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * Convertit les minutes en texte lisible
     */
    private String getTimeRemainingText(int minutesBefore) {
        if (minutesBefore < 60) {
            return "dans " + minutesBefore + " minute" + (minutesBefore > 1 ? "s" : "");
        } else if (minutesBefore < 1440) { // moins de 24h
            int hours = minutesBefore / 60;
            int remainingMinutes = minutesBefore % 60;
            String text = "dans " + hours + " heure" + (hours > 1 ? "s" : "");
            if (remainingMinutes > 0) {
                text += " et " + remainingMinutes + " minute" + (remainingMinutes > 1 ? "s" : "");
            }
            return text;
        } else {
            int days = minutesBefore / 1440;
            return "dans " + days + " jour" + (days > 1 ? "s" : "");
        }
    }

    /**
     * Test d'envoi d'email (pour debug)
     */
    public void sendTestEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Test Email - " + appName);
            message.setText("Ceci est un email de test pour vérifier la configuration SMTP.");

            mailSender.send(message);
            log.info("Email de test envoyé avec succès à {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur envoi email de test: {}", e.getMessage());
            throw new RuntimeException("Test email failed", e);
        }
    }
}