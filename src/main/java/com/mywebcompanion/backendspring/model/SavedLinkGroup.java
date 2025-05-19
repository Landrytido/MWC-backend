package com.mywebcompanion.backendspring.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "saved_link_groups")
@Data
@IdClass(SavedLinkGroupId.class)
public class SavedLinkGroup {

    @Id
    @Column(name = "link_group_id")
    private String linkGroupId;

    @Id
    @Column(name = "saved_link_id")
    private Long savedLinkId;

    @Column(nullable = false)
    private String linkName; // Nom personnalisé du lien dans ce groupe

    @Column(nullable = false)
    private Integer clickCounter = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_group_id", insertable = false, updatable = false)
    private LinkGroup linkGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_link_id", insertable = false, updatable = false)
    private SavedLink savedLink;
}