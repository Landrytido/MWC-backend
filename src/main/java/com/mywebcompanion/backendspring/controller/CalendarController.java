package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.service.EmailService;
import com.mywebcompanion.backendspring.service.EventService;

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
    private final EmailService emailService;

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

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        emailService.sendTestEmail(email);
        return ResponseEntity.ok("Email de test envoyé à " + email);
    }

    @GetMapping("/day/{date}")
    public ResponseEntity<CalendarViewDto> getDayView(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String date) {
        String email = userDetails.getUsername();
        CalendarViewDto dayView = eventService.getDayView(email, date);
        return ResponseEntity.ok(dayView);
    }
}