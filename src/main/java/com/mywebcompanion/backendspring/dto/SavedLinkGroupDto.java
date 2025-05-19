package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class SavedLinkGroupDto {
    private Long savedLinkId;
    private String linkGroupId;
    private String linkName;
    private String url; // URL du lien
    private Integer clickCounter;

    private SavedLinkDetailsDto savedLinkDetails;
}