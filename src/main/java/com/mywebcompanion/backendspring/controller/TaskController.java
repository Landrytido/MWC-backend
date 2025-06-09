package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.TaskDto;
import com.mywebcompanion.backendspring.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        TaskDto task = taskService.getTaskById(email, id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaskDto taskDto) {
        String email = userDetails.getUsername();

        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto createdTask = taskService.createTask(email, taskDto);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody TaskDto taskDto) {
        String email = userDetails.getUsername();

        if (taskDto.getTitle() == null || taskDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDto updatedTask = taskService.updateTask(email, id, taskDto);
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
}