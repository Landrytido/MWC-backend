package com.mywebcompanion.backendspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@ToString(exclude = { "notes", "links", "notebooks", "blocNote", "comments", "tasks",
        "dailyPlans", "dailyTasks", "dailyTaskHistory", "noteTasks",
        "linkGroups", "files" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clerkId;

    @Column(nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    // ✅ TOUTES LES COLLECTIONS EN LAZY + NO_PROXY
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<SavedLink> links = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<Notebook> notebooks = new ArrayList<>();

    // ✅ BlocNote avec LAZY + NO_PROXY
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JsonIgnore
    private BlocNote blocNote;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<DailyPlan> dailyPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<DailyTask> dailyTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<DailyTaskHistory> dailyTaskHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<NoteTask> noteTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<LinkGroup> linkGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private List<File> files = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}