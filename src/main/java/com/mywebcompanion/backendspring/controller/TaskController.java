package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getAllTasksByUserEmail(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TaskDto>> getPendingTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getPendingTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskDto>> getCompletedTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getCompletedTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getOverdueTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-in-days")
    public ResponseEntity<List<TaskDto>> getTasksDueInDays(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "7") int days) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getTasksDueInDays(email, days);
        return ResponseEntity.ok(tasks);
    }

    // NOUVEAUX ENDPOINTS pour la planification quotidienne
    @GetMapping("/today")
    public ResponseEntity<List<TaskDto>> getTodayTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getTodayTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tomorrow")
    public ResponseEntity<List<TaskDto>> getTomorrowTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getTomorrowTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<TaskDto>> getTasksByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam LocalDate date) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getTasksByDate(email, date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/carried-over")
    public ResponseEntity<List<TaskDto>> getCarriedOverTasks(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getCarriedOverTasks(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        TaskDto task = taskService.getTaskById(email, id);
        return ResponseEntity.ok(task);
    }

    // CORRECTION : Utiliser CreateTaskRequest au lieu de TaskDto
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateTaskRequest request) {
        String email = userDetails.getUsername();

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto createdTask = taskService.createTask(email, request);
        return ResponseEntity.ok(createdTask);
    }

    // CORRECTION : Utiliser UpdateTaskRequest au lieu de TaskDto
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        String email = userDetails.getUsername();

        if (request.getTitle() != null && request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto updatedTask = taskService.updateTask(email, id, request);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<TaskDto> toggleTaskCompletion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        TaskDto updatedTask = taskService.toggleTaskCompletion(email, id);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        taskService.deleteTask(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingTaskCount(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long count = taskService.getPendingTaskCount(email);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // NOUVEAUX ENDPOINTS pour les fonctionnalités avancées
    @PostMapping("/end-day")
    public ResponseEntity<List<TaskDto>> endDay(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EndDayRequest request) {
        String email = userDetails.getUsername();
        List<TaskDto> carriedOverTasks = taskService.endDay(email, request);
        return ResponseEntity.ok(carriedOverTasks);
    }

    @PostMapping("/reorder")
    public ResponseEntity<List<TaskDto>> reorderTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReorderTasksRequest request) {
        String email = userDetails.getUsername();
        List<TaskDto> reorderedTasks = taskService.reorderTasks(email, request);
        return ResponseEntity.ok(reorderedTasks);
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<TaskStatsDto> getMonthlyStats(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        String email = userDetails.getUsername();
        TaskStatsDto stats = taskService.getMonthlyStats(email, year, month);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getUserTaskSummary(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Map<String, Object> summary = taskService.getUserTaskSummary(email);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.searchTasks(email, keyword);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-priority")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer priority) {
        String email = userDetails.getUsername();
        List<TaskDto> tasks = taskService.getTasksByPriority(email, priority);
        return ResponseEntity.ok(tasks);
    }
}