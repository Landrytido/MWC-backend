package com.mywebcompanion.backendspring.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.mywebcompanion.backendspring.dto.CalendarViewDto;
import com.mywebcompanion.backendspring.dto.CreateEventRequest;
import com.mywebcompanion.backendspring.dto.CreateReminderRequest;
import com.mywebcompanion.backendspring.dto.CreateTaskFromCalendarRequest;
import com.mywebcompanion.backendspring.dto.EventDto;
import com.mywebcompanion.backendspring.dto.EventReminderDto;
import com.mywebcompanion.backendspring.dto.TaskDto;
import com.mywebcompanion.backendspring.exception.ValidationException;
import com.mywebcompanion.backendspring.model.Event;
import com.mywebcompanion.backendspring.model.EventReminder;
import com.mywebcompanion.backendspring.model.Task;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.EventReminderRepository;
import com.mywebcompanion.backendspring.repository.EventRepository;
import com.mywebcompanion.backendspring.repository.TaskRepository;
import com.mywebcompanion.enums.EventType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventReminderRepository reminderRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    // Créer un événement
    public EventDto createEvent(String email, CreateEventRequest request) {
        User user = userService.findByEmail(email);

        // Validations
        validateEventDates(request.getStartDate(), request.getEndDate());

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setMode(request.getMode());
        event.setMeetingLink(request.getMeetingLink());
        event.setType(request.getType());
        event.setUser(user);

        // Lier à une tâche existante si spécifié
        if (request.getRelatedTaskId() != null) {
            Task task = taskRepository.findByIdAndUserId(request.getRelatedTaskId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
            event.setRelatedTask(task);
            event.setType(EventType.TASK_BASED);
        }

        Event savedEvent = eventRepository.save(event);

        // Créer les rappels
        if (request.getReminders() != null && !request.getReminders().isEmpty()) {
            createReminders(savedEvent, request.getReminders());
        }

        return convertToDto(savedEvent);
    }

    public TaskDto createTaskFromCalendar(String email, CreateTaskFromCalendarRequest request) {
        User user = userService.findByEmail(email);

        // 🔍 AJOUTEZ CES LOGS
        log.info("=== BACKEND RECEIVED ===");
        log.info("scheduledDate reçu: {}", request.getScheduledDate());
        log.info("dueDate reçu: {}", request.getDueDate());
        log.info("Request complet: {}", request);
        log.info("========================");

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setScheduledDate(request.getScheduledDate());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority() != null ? request.getPriority() : 2);
        task.setUser(user);
        task.setToken(UUID.randomUUID().toString());

        Task savedTask = taskRepository.save(task);

        // 🔍 AJOUTEZ CE LOG AUSSI
        log.info("=== TASK SAVED ===");
        log.info("scheduledDate final en BD: {}", savedTask.getScheduledDate());
        log.info("ID de la tâche: {}", savedTask.getId());
        log.info("==================");

        return convertTaskToDto(savedTask);
    }

    // Vue calendrier pour un mois
    public List<CalendarViewDto> getMonthlyCalendarView(String email, int year, int month) {
        User user = userService.findByEmail(email);

        // Récupérer événements du mois
        List<Event> events = eventRepository.findByUserIdAndYearAndMonth(user.getId(), year, month);

        // Récupérer tâches planifiées du mois
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        // CORRECTION: Utiliser la bonne signature de méthode
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);

        List<Task> tasks = taskRepository.findTasksInDateRange(user.getId(), startOfMonth, endOfMonth, startDateTime,
                endDateTime);

        // Grouper par jour
        Map<LocalDate, List<Event>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartDate().toLocalDate()));

        Map<LocalDate, List<Task>> tasksByDate = tasks.stream()
                .filter(t -> t.getCalendarEvent() == null) // Éviter les doublons
                .collect(Collectors.groupingBy(t -> t.getScheduledDate() != null ? t.getScheduledDate()
                        : t.getDueDate() != null ? t.getDueDate().toLocalDate() : LocalDate.now()));

        // Créer la vue calendrier
        return IntStream.rangeClosed(1, endOfMonth.getDayOfMonth())
                .mapToObj(day -> LocalDate.of(year, month, day))
                .map(date -> {
                    CalendarViewDto dayView = new CalendarViewDto();
                    dayView.setDate(date);

                    List<EventDto> dayEvents = eventsByDate.getOrDefault(date, List.of())
                            .stream().map(this::convertToDto).collect(Collectors.toList());

                    List<TaskDto> dayTasks = tasksByDate.getOrDefault(date, List.of())
                            .stream().map(this::convertTaskToDto).collect(Collectors.toList());

                    dayView.setEvents(dayEvents);
                    dayView.setTasks(dayTasks);
                    dayView.setTotalItems(dayEvents.size() + dayTasks.size());

                    return dayView;
                })
                .collect(Collectors.toList());
    }

    // CRUD supplémentaires
    public List<EventDto> getAllEvents(String email) {
        User user = userService.findByEmail(email);
        return eventRepository.findByUserIdOrderByStartDateAsc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(String email, Long eventId) {
        User user = userService.findByEmail(email);
        Event event = eventRepository.findByIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        return convertToDto(event);
    }

    public EventDto updateEvent(String email, Long eventId, CreateEventRequest request) {
        User user = userService.findByEmail(email);
        Event event = eventRepository.findByIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Validations
        validateEventDates(request.getStartDate(), request.getEndDate());

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setMode(request.getMode());
        event.setMeetingLink(request.getMeetingLink());

        Event updatedEvent = eventRepository.save(event);
        return convertToDto(updatedEvent);
    }

    public void deleteEvent(String email, Long eventId) {
        User user = userService.findByEmail(email);
        Event event = eventRepository.findByIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        eventRepository.delete(event);
    }

    // ========= MÉTHODES PRIVÉES DE CONVERSION =========

    private void createReminders(Event event, List<CreateReminderRequest> reminderRequests) {
        for (CreateReminderRequest request : reminderRequests) {
            EventReminder reminder = new EventReminder();
            reminder.setEvent(event);
            reminder.setType(request.getType());
            reminder.setMinutesBefore(request.getMinutesBefore());

            // Calculer la date d'envoi
            LocalDateTime scheduledFor = event.getStartDate().minusMinutes(request.getMinutesBefore());
            reminder.setScheduledFor(scheduledFor);

            reminderRepository.save(reminder);
        }
    }

    private void validateEventDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ValidationException("La date de fin doit être après la date de début");
        }
        if (start.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ValidationException("Impossible de créer un événement dans le passé");
        }
    }

    // MÉTHODE MANQUANTE : Conversion Event vers EventDto
    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setLocation(event.getLocation());
        dto.setMode(event.getMode());
        dto.setMeetingLink(event.getMeetingLink());
        dto.setType(event.getType());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        // Si lié à une tâche
        if (event.getRelatedTask() != null) {
            Task task = event.getRelatedTask();
            dto.setRelatedTaskId(event.getRelatedTask().getId());
            dto.setRelatedTaskTitle(event.getRelatedTask().getTitle());
            dto.setDescription(task.getDescription());
            dto.setTaskPriority(task.getPriority());

            if (task.getDueDate() != null) {
                dto.setEndDate(task.getDueDate());
            }
        }

        // Convertir les rappels
        if (event.getReminders() != null) {
            dto.setReminders(event.getReminders().stream()
                    .map(this::convertReminderToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // MÉTHODE MANQUANTE : Conversion EventReminder vers EventReminderDto
    private EventReminderDto convertReminderToDto(EventReminder reminder) {
        EventReminderDto dto = new EventReminderDto();
        dto.setId(reminder.getId());
        dto.setType(reminder.getType());
        dto.setMinutesBefore(reminder.getMinutesBefore());
        dto.setSent(reminder.getSent());
        dto.setScheduledFor(reminder.getScheduledFor());
        dto.setCreatedAt(reminder.getCreatedAt());
        return dto;
    }

    // MÉTHODE MANQUANTE : Conversion Task vers TaskDto (réutilise celle de
    // TaskService)
    private TaskDto convertTaskToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setScheduledDate(task.getScheduledDate());
        dto.setPriority(task.getPriority());
        dto.setCompleted(task.getCompleted());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCarriedOver(task.getCarriedOver());
        dto.setOriginalDate(task.getOriginalDate());
        dto.setOrderIndex(task.getOrderIndex());
        dto.setStatus(task.getStatus());
        return dto;
    }

    public CalendarViewDto getDayView(String email, String dateString) {
        User user = userService.findByEmail(email);

        // Parser la date (format attendu: "2025-01-15")
        LocalDate date;
        try {
            date = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Format de date invalide. Utilisez YYYY-MM-DD");
        }

        // Récupérer événements de la journée
        List<Event> events = eventRepository.findByUserIdAndDate(user.getId(), date);

        // Récupérer tâches de la journée (scheduledDate ou dueDate)
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Task> tasks = taskRepository.findTasksInDateRange(
                user.getId(),
                date,
                date,
                startOfDay,
                endOfDay);

        // Filtrer les tâches pour éviter les doublons avec événements liés
        List<Task> filteredTasks = tasks.stream()
                .filter(t -> t.getCalendarEvent() == null)
                .collect(Collectors.toList());

        // Créer la vue du jour
        CalendarViewDto dayView = new CalendarViewDto();
        dayView.setDate(date);

        List<EventDto> dayEvents = events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        List<TaskDto> dayTasks = filteredTasks.stream()
                .map(this::convertTaskToDto)
                .collect(Collectors.toList());

        dayView.setEvents(dayEvents);
        dayView.setTasks(dayTasks);
        dayView.setTotalItems(dayEvents.size() + dayTasks.size());

        return dayView;
    }
}