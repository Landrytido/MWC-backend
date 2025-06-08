package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.model.*;
import com.mywebcompanion.backendspring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyTaskService {

    private final DailyTaskRepository dailyTaskRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final DailyTaskHistoryRepository dailyTaskHistoryRepository;
    private final UserService userService;

    public List<DailyTaskDto> getDailyTasks(String email, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        User user = userService.findByEmail(email);
        return dailyTaskRepository.findByUserIdAndScheduledDateOrderByOrderIndexAsc(user.getId(), date)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DailyTaskDto createDailyTask(String email, CreateDailyTaskDto dto) {
        User user = userService.findByEmail(email);

        LocalDate scheduledDate = dto.getScheduledDate() != null ? dto.getScheduledDate() : LocalDate.now();

        // Limite de 6 tâches pour le futur (demain et après)
        if (scheduledDate.isAfter(LocalDate.now())) {
            Long count = dailyTaskRepository.countByUserIdAndScheduledDate(user.getId(), scheduledDate);
            if (count >= 6) {
                throw new RuntimeException("Vous ne pouvez pas planifier plus de 6 tâches pour " + scheduledDate);
            }
        }

        DailyTask task = new DailyTask();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setScheduledDate(scheduledDate);
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : 1);
        task.setUser(user);

        // Définir l'ordre (à la fin)
        List<DailyTask> existingTasks = dailyTaskRepository
                .findByUserIdAndScheduledDateOrderByOrderIndexAsc(user.getId(), scheduledDate);
        task.setOrderIndex(existingTasks.size());

        DailyTask savedTask = dailyTaskRepository.save(task);
        return convertToDto(savedTask);
    }

    public DailyTaskDto updateDailyTask(String email, Long taskId, DailyTaskDto dto) {
        User user = userService.findByEmail(email);
        DailyTask task = dailyTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());

        // Si on marque comme complétée
        if (dto.getCompleted() != null && !task.getCompleted() && dto.getCompleted()) {
            task.setCompleted(true);
            task.setCompletedAt(LocalDateTime.now());
        } else if (dto.getCompleted() != null && task.getCompleted() && !dto.getCompleted()) {
            task.setCompleted(false);
            task.setCompletedAt(null);
        }

        DailyTask updatedTask = dailyTaskRepository.save(task);
        return convertToDto(updatedTask);
    }

    public void deleteDailyTask(String email, Long taskId) {
        User user = userService.findByEmail(email);
        DailyTask task = dailyTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        dailyTaskRepository.delete(task);
    }

    public List<DailyTaskDto> reorderDailyTasks(String email, ReorderDailyTasksDto dto) {
        User user = userService.findByEmail(email);
        List<Long> orderedIds = dto.getOrderedIds();
        AtomicInteger index = new AtomicInteger(0);

        // Mettre à jour l'ordre pour chaque tâche
        orderedIds.forEach(id -> {
            DailyTask task = dailyTaskRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée: " + id));
            task.setOrderIndex(index.getAndIncrement());
            dailyTaskRepository.save(task);
        });

        // Retourner les tâches réorganisées
        LocalDate date = LocalDate.now(); // On assume que c'est pour aujourd'hui
        return getDailyTasks(email, date);
    }

    public boolean confirmEndOfDay(String email) {
        User user = userService.findByEmail(email);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Archiver toutes les tâches du jour
        List<DailyTask> todayTasks = dailyTaskRepository.findByUserIdAndScheduledDateOrderByOrderIndexAsc(user.getId(),
                today);

        for (DailyTask task : todayTasks) {
            // Créer l'entrée d'historique
            DailyTaskHistory history = convertToHistory(task);
            dailyTaskHistoryRepository.save(history);

            // Si la tâche n'est pas complétée, la reporter à demain
            if (!task.getCompleted()) {
                task.setScheduledDate(tomorrow);
                task.setOriginalDate(task.getOriginalDate() != null ? task.getOriginalDate() : today);
                task.setCarriedOver(true);
                dailyTaskRepository.save(task);
            } else {
                // Supprimer la tâche complétée
                dailyTaskRepository.delete(task);
            }
        }

        // Marquer le plan du jour comme confirmé
        DailyPlan plan = dailyPlanRepository.findByUserIdAndDate(user.getId(), today)
                .orElse(createDailyPlan(user, today));
        plan.setConfirmed(true);
        dailyPlanRepository.save(plan);

        return true;
    }

    public DailyPlanDto getDailyPlan(String email, LocalDate date) {
        User user = userService.findByEmail(email);
        DailyPlan plan = dailyPlanRepository.findByUserIdAndDate(user.getId(), date)
                .orElse(null);

        return plan != null ? convertPlanToDto(plan) : null;
    }

    public MonthlyReportDto getMonthlyReport(String email, int year, int month) {
        User user = userService.findByEmail(email);
        Object[] result = dailyTaskHistoryRepository.getMonthlyReportByUserId(user.getId(), year, month);

        if (result.length > 0 && result[0] != null) {
            Long total = ((Number) result[0]).longValue();
            Long completed = result[1] != null ? ((Number) result[1]).longValue() : 0L;

            double percentage = total > 0 ? (completed.doubleValue() / total.doubleValue()) * 100 : 0.0;

            MonthlyReportDto report = new MonthlyReportDto();
            report.setTotalTasks(total.intValue());
            report.setCompletedTasks(completed.intValue());
            report.setNotCompletedTasks((int) (total - completed));
            report.setCompletionPercentage(percentage);

            return report;
        }

        // Retourner un rapport vide si aucune donnée
        MonthlyReportDto emptyReport = new MonthlyReportDto();
        emptyReport.setTotalTasks(0);
        emptyReport.setCompletedTasks(0);
        emptyReport.setNotCompletedTasks(0);
        emptyReport.setCompletionPercentage(0.0);

        return emptyReport;
    }

    public List<DailyTaskDto> getDailyHistory(String email, LocalDate date) {
        User user = userService.findByEmail(email);
        return dailyTaskHistoryRepository.findByUserIdAndScheduledDateOrderByOrderIndexAsc(user.getId(), date)
                .stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    private DailyPlan createDailyPlan(User user, LocalDate date) {
        DailyPlan plan = new DailyPlan();
        plan.setUser(user);
        plan.setDate(date);
        plan.setConfirmed(false);

        return plan;
    }

    private DailyTaskHistory convertToHistory(DailyTask task) {
        DailyTaskHistory history = new DailyTaskHistory();
        history.setUniqueTaskId(task.getUniqueTaskId());
        history.setTitle(task.getTitle());
        history.setDescription(task.getDescription());
        history.setScheduledDate(task.getScheduledDate());
        history.setOriginalDate(task.getOriginalDate());
        history.setCarriedOver(task.getCarriedOver());
        history.setOrderIndex(task.getOrderIndex());
        history.setPriority(task.getPriority());
        history.setCompleted(task.getCompleted());
        history.setCompletedAt(task.getCompletedAt());
        history.setUser(task.getUser());
        history.setCreatedAt(task.getCreatedAt());
        history.setArchivedAt(LocalDateTime.now());

        return history;
    }

    private DailyTaskDto convertToDto(DailyTask task) {
        DailyTaskDto dto = new DailyTaskDto();
        dto.setId(task.getId());
        dto.setUniqueTaskId(task.getUniqueTaskId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setScheduledDate(task.getScheduledDate());
        dto.setOriginalDate(task.getOriginalDate());
        dto.setCarriedOver(task.getCarriedOver());
        dto.setOrderIndex(task.getOrderIndex());
        dto.setPriority(task.getPriority());
        dto.setCompleted(task.getCompleted());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        return dto;
    }

    private DailyTaskDto convertHistoryToDto(DailyTaskHistory history) {
        DailyTaskDto dto = new DailyTaskDto();
        dto.setUniqueTaskId(history.getUniqueTaskId());
        dto.setTitle(history.getTitle());
        dto.setDescription(history.getDescription());
        dto.setScheduledDate(history.getScheduledDate());
        dto.setOriginalDate(history.getOriginalDate());
        dto.setCarriedOver(history.getCarriedOver());
        dto.setOrderIndex(history.getOrderIndex());
        dto.setPriority(history.getPriority());
        dto.setCompleted(history.getCompleted());
        dto.setCompletedAt(history.getCompletedAt());
        dto.setCreatedAt(history.getCreatedAt());

        return dto;
    }

    private DailyPlanDto convertPlanToDto(DailyPlan plan) {
        DailyPlanDto dto = new DailyPlanDto();
        dto.setId(plan.getId());
        dto.setDate(plan.getDate());
        dto.setConfirmed(plan.getConfirmed());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());

        return dto;
    }
}