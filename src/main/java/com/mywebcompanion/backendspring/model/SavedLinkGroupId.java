package com.mywebcompanion.backendspring.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class SavedLinkGroupId implements Serializable {
    private String linkGroupId;
    private Long savedLinkId;

    public SavedLinkGroupId() {
    }

    public SavedLinkGroupId(String linkGroupId, Long savedLinkId) {
        this.linkGroupId = linkGroupId;
        this.savedLinkId = savedLinkId;
    }
}