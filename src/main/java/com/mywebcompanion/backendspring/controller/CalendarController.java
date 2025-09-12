package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.service.EventService;
import com.mywebcompanion.backendspring.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final EventService eventService;
    private final TaskService taskService;

    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<CalendarViewDto>> getMonthlyView(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int year,
            @PathVariable int month) {
        String email = userDetails.getUsername();
        List<CalendarViewDto> calendarView = eventService.getMonthlyCalendarView(email, year, month);
        return ResponseEntity.ok(calendarView);
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateEventRequest request) {
        String email = userDetails.getUsername();
        EventDto event = eventService.createEvent(email, request);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getAllEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<EventDto> events = eventService.getAllEvents(email);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventDto> getEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        EventDto event = eventService.getEventById(email, id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request) {
        String email = userDetails.getUsername();
        EventDto event = eventService.updateEvent(email, id, request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        eventService.deleteEvent(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/day/{date}")
    public ResponseEntity<CalendarViewDto> getDayView(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String date) {
        String email = userDetails.getUsername();
        CalendarViewDto dayView = eventService.getDayView(email, date);
        return ResponseEntity.ok(dayView);
    }

    @PostMapping("/create-task")
    public ResponseEntity<TaskDto> createTaskFromCalendar(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateTaskRequest request) {
        String email = userDetails.getUsername();
        TaskDto task = taskService.createTask(email, request);
        return ResponseEntity.ok(task);
    }
}