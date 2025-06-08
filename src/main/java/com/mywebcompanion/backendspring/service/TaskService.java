package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.TaskDto;
import com.mywebcompanion.backendspring.model.Task;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public List<TaskDto> getAllTasksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdOrderByDueDateAscCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getCompletedTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdAndCompletedTrueOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getPendingTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdAndCompletedFalseOrderByDueDateAscCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getOverdueTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findOverdueTasks(user.getId(), LocalDateTime.now())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksDueInDays(String email, int days) {
        User user = userService.findByEmail(email);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return taskRepository.findTasksDueInPeriod(user.getId(), now, futureDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto createTask(String email, TaskDto taskDto) {
        User user = userService.findByEmail(email);

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setCompleted(taskDto.getCompleted() != null ? taskDto.getCompleted() : false);
        task.setUser(user);
        task.setToken(UUID.randomUUID().toString()); // Token unique pour notifications

        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    public TaskDto updateTask(String email, Long taskId, TaskDto taskDto) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());

        // Si on marque la tâche comme complétée, on peut ajouter une logique spéciale
        if (taskDto.getCompleted() != null && !task.getCompleted() && taskDto.getCompleted()) {
            // La tâche vient d'être complétée
            task.setCompleted(true);
        } else if (taskDto.getCompleted() != null) {
            task.setCompleted(taskDto.getCompleted());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    public TaskDto toggleTaskCompletion(String email, Long taskId) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        task.setCompleted(!task.getCompleted());
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    public void deleteTask(String email, Long taskId) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        taskRepository.delete(task);
    }

    public TaskDto getTaskById(String email, Long taskId) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        return convertToDto(task);
    }

    public Long getPendingTaskCount(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.countByUserIdAndCompletedFalse(user.getId());
    }

    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setCompleted(task.getCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        // Calculer le statut
        if (task.getCompleted()) {
            dto.setStatus("completed");
        } else if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now())) {
            dto.setStatus("overdue");
        } else {
            dto.setStatus("upcoming");
        }

        return dto;
    }
}