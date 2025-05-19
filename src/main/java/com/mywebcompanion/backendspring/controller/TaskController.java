package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.TaskDto;
import com.mywebcompanion.backendspring.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Obtenir toutes les tâches
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<TaskDto> tasks = taskService.getAllTasksByClerkId(clerkId);
        return ResponseEntity.ok(tasks);
    }

    // Obtenir les tâches en attente
    @GetMapping("/pending")
    public ResponseEntity<List<TaskDto>> getPendingTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<TaskDto> tasks = taskService.getPendingTasks(clerkId);
        return ResponseEntity.ok(tasks);
    }

    // Obtenir les tâches complétées
    @GetMapping("/completed")
    public ResponseEntity<List<TaskDto>> getCompletedTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<TaskDto> tasks = taskService.getCompletedTasks(clerkId);
        return ResponseEntity.ok(tasks);
    }

    // Obtenir les tâches en retard
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(Authentication authentication) {
        String clerkId = authentication.getName();
        List<TaskDto> tasks = taskService.getOverdueTasks(clerkId);
        return ResponseEntity.ok(tasks);
    }

    // Obtenir les tâches dues dans les prochains X jours
    @GetMapping("/due-in-days")
    public ResponseEntity<List<TaskDto>> getTasksDueInDays(
            Authentication authentication,
            @RequestParam(defaultValue = "7") int days) {
        String clerkId = authentication.getName();
        List<TaskDto> tasks = taskService.getTasksDueInDays(clerkId, days);
        return ResponseEntity.ok(tasks);
    }

    // Obtenir une tâche par ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        TaskDto task = taskService.getTaskById(clerkId, id);
        return ResponseEntity.ok(task);
    }

    // Créer une nouvelle tâche
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            Authentication authentication,
            @RequestBody TaskDto taskDto) {
        String clerkId = authentication.getName();

        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto createdTask = taskService.createTask(clerkId, taskDto);
        return ResponseEntity.ok(createdTask);
    }

    // Mettre à jour une tâche
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody TaskDto taskDto) {
        String clerkId = authentication.getName();

        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto updatedTask = taskService.updateTask(clerkId, id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    // Basculer l'état de completion d'une tâche
    @PutMapping("/{id}/toggle")
    public ResponseEntity<TaskDto> toggleTaskCompletion(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        TaskDto updatedTask = taskService.toggleTaskCompletion(clerkId, id);
        return ResponseEntity.ok(updatedTask);
    }

    // Supprimer une tâche
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            Authentication authentication,
            @PathVariable Long id) {
        String clerkId = authentication.getName();
        taskService.deleteTask(clerkId, id);
        return ResponseEntity.noContent().build();
    }

    // Obtenir le nombre de tâches en attente
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingTaskCount(Authentication authentication) {
        String clerkId = authentication.getName();
        Long count = taskService.getPendingTaskCount(clerkId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}