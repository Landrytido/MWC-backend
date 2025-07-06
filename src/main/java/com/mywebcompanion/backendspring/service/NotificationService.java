package com.mywebcompanion.backendspring.service;

import java.time.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mywebcompanion.backendspring.model.EventReminder;
import com.mywebcompanion.backendspring.repository.EventReminderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EventReminderRepository reminderRepository;
    private final EmailService emailService;

    @Scheduled(fixedRate = 60000) // Toutes les minutes
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<EventReminder> pendingReminders = reminderRepository.findPendingReminders(now);

        for (EventReminder reminder : pendingReminders) {
            try {
                sendReminder(reminder);
                reminder.setSent(true);
                reminderRepository.save(reminder);
            } catch (Exception e) {
                // Log error
                log.error("Erreur envoi rappel pour événement {}: {}",
                        reminder.getEvent().getId(), e.getMessage());
            }
        }
    }

    private void sendReminder(EventReminder reminder) {
        switch (reminder.getType()) {
            case EMAIL:
                emailService.sendEventReminder(reminder); // UTILISATION DU SERVICE EMAIL
                break;
            case IN_APP_NOTIFICATION:
                sendInAppNotification(reminder);
                break;
        }
    }

    private void sendInAppNotification(EventReminder reminder) {
        // Implémentation notification in-app (WebSocket, SSE, etc.)
        // Pour l'instant, on peut juste logger
        log.info("Notification in-app pour événement: {}", reminder.getEvent().getTitle());
    }
}
