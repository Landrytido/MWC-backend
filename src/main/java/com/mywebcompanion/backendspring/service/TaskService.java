// src/main/java/com/mywebcompanion/backendspring/service/TaskService.java (Version mise à jour)
package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.model.Task;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    // Méthodes existantes mises à jour
    public List<TaskDto> getAllTasksByUserEmail(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdOrderByPriorityAndCreatedAt(user.getId())
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
        return taskRepository.findByUserIdAndCompletedFalseOrderByPriorityAndDates(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getOverdueTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findOverdueTasksByUserId(user.getId(), LocalDateTime.now())
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

    // Nouvelles méthodes pour la planification quotidienne
    public List<TaskDto> getTodayTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findTodayTasksByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTomorrowTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findTomorrowTasksByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByDate(String email, LocalDate date) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdAndScheduledDateOrderByPriorityAndOrder(user.getId(), date)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getCarriedOverTasks(String email) {
        User user = userService.findByEmail(email);
        return taskRepository.findCarriedOverTasksByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto createTask(String email, CreateTaskRequest request) {
        User user = userService.findByEmail(email);

        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setDueDate(request.getDueDate());
        task.setScheduledDate(request.getScheduledDate());
        task.setPriority(request.getPriority());
        task.setUser(user);
        task.setToken(UUID.randomUUID().toString());

        // Définir l'ordre si la tâche est planifiée
        if (request.getScheduledDate() != null) {
            Integer nextOrder = taskRepository.findNextOrderIndex(user.getId(), request.getScheduledDate());
            task.setOrderIndex(nextOrder != null ? nextOrder : 0);
        } else {
            task.setOrderIndex(request.getOrderIndex());
        }

        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    public TaskDto updateTask(String email, Long taskId, UpdateTaskRequest request) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription().trim());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getScheduledDate() != null) {
            task.setScheduledDate(request.getScheduledDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getCompleted() != null) {
            if (request.getCompleted() && !task.getCompleted()) {
                task.markAsCompleted();
            } else if (!request.getCompleted() && task.getCompleted()) {
                task.markAsIncomplete();
            }
        }
        if (request.getCarriedOver() != null) {
            task.setCarriedOver(request.getCarriedOver());
        }
        if (request.getOriginalDate() != null) {
            task.setOriginalDate(request.getOriginalDate());
        }
        if (request.getOrderIndex() != null) {
            task.setOrderIndex(request.getOrderIndex());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    public TaskDto toggleTaskCompletion(String email, Long taskId) {
        User user = userService.findByEmail(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        if (task.getCompleted()) {
            task.markAsIncomplete();
        } else {
            task.markAsCompleted();
        }

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

    public List<TaskDto> endDay(String email, EndDayRequest request) {
        User user = userService.findByEmail(email);
        LocalDate dateToEnd = request.getDate() != null ? request.getDate() : LocalDate.now();
        LocalDate tomorrow = dateToEnd.plusDays(1);

        List<Task> pendingTasks = taskRepository.findPendingTasksByUserIdAndScheduledDate(user.getId(), dateToEnd);

        List<Task> carriedOverTasks = new ArrayList<>();

        for (Task task : pendingTasks) {
            if (request.getTaskIdsToCarryOver() != null &&
                    !request.getTaskIdsToCarryOver().isEmpty() &&
                    !request.getTaskIdsToCarryOver().contains(task.getId())) {
                continue;
            }

            task.carryOverTo(tomorrow);

            Integer nextOrder = taskRepository.findNextOrderIndex(user.getId(), tomorrow);
            task.setOrderIndex(nextOrder != null ? nextOrder : 0);

            Task saved = taskRepository.save(task);
            carriedOverTasks.add(saved);
        }

        return carriedOverTasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskStatsDto getMonthlyStats(String email, int year, int month) {
        User user = userService.findByEmail(email);

        TaskStatsDto stats = new TaskStatsDto();
        stats.setMonth(month);
        stats.setYear(year);

        List<Object[]> priorityStats = taskRepository.findMonthlyStatsByPriority(user.getId(), year, month);
        Map<Integer, TaskStatsDto.PriorityStats> priorityStatsMap = new HashMap<>();

        long totalTasks = 0;
        long completedTasks = 0;

        for (Object[] row : priorityStats) {
            Long total = ((Number) row[0]).longValue();
            Long completed = ((Number) row[1]).longValue();
            Integer priority = ((Number) row[2]).intValue();

            totalTasks += total;
            completedTasks += completed;

            TaskStatsDto.PriorityStats priorityStat = new TaskStatsDto.PriorityStats();
            priorityStat.setTotal(total);
            priorityStat.setCompleted(completed);
            priorityStat.setCompletionRate(total > 0 ? (completed * 100.0 / total) : 0.0);

            priorityStatsMap.put(priority, priorityStat);
        }

        stats.setTotalTasks(totalTasks);
        stats.setCompletedTasks(completedTasks);
        stats.setNotCompletedTasks(totalTasks - completedTasks);
        stats.setCompletionPercentage(totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0);
        stats.setTasksByPriority(priorityStatsMap);

        List<Object[]> dailyStats = taskRepository.findDailyStatsForMonth(user.getId(), year, month);
        Map<LocalDate, TaskStatsDto.DailyStats> dailyStatsMap = new HashMap<>();

        for (Object[] row : dailyStats) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long total = ((Number) row[1]).longValue();
            Long completed = ((Number) row[2]).longValue();

            TaskStatsDto.DailyStats dailyStat = new TaskStatsDto.DailyStats();
            dailyStat.setDate(date);
            dailyStat.setTotal(total);
            dailyStat.setCompleted(completed);
            dailyStat.setCompletionRate(total > 0 ? (completed * 100.0 / total) : 0.0);

            dailyStatsMap.put(date, dailyStat);
        }

        stats.setDailyStats(dailyStatsMap);
        return stats;
    }

    public Map<String, Object> getUserTaskSummary(String email) {
        User user = userService.findByEmail(email);
        Object[] summary = taskRepository.findUserTaskSummary(user.getId());

        Map<String, Object> result = new HashMap<>();
        if (summary.length > 0) {
            result.put("totalTasks", ((Number) summary[0]).longValue());
            result.put("completedTasks", ((Number) summary[1]).longValue());
            result.put("overdueTasks", ((Number) summary[2]).longValue());
            result.put("todayTasks", ((Number) summary[3]).longValue());
            result.put("tomorrowTasks", ((Number) summary[4]).longValue());
        } else {
            result.put("totalTasks", 0L);
            result.put("completedTasks", 0L);
            result.put("overdueTasks", 0L);
            result.put("todayTasks", 0L);
            result.put("tomorrowTasks", 0L);
        }

        return result;
    }

    // Nouvelle méthode : Recherche de tâches
    public List<TaskDto> searchTasks(String email, String keyword) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdAndKeyword(user.getId(), keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Nouvelle méthode : Tâches par priorité
    public List<TaskDto> getTasksByPriority(String email, Integer priority) {
        User user = userService.findByEmail(email);
        return taskRepository.findByUserIdAndPriority(user.getId(), priority)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TaskDto convertToDto(Task task) {
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

        // Statut calculé
        dto.setStatus(task.getStatus());

        return dto;
    }
}