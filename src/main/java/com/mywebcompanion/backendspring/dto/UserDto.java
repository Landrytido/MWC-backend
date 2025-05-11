package com.mywebcompanion.backendspring.dto;

import lombok.Data;

@Data
public class UserDto {
    private String clerkId;
    private String email;
    private String firstName;
    private String lastName;
}