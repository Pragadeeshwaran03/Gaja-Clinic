package com.gaja.clinic.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityItemDto {

    private String description;
    private LocalDateTime timestamp;
    private String type;
}
