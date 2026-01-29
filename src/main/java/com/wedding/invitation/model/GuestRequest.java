package com.wedding.invitation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Attending status is required")
    private Boolean attending;

    private String foodPreference;

    private String comment;
}

