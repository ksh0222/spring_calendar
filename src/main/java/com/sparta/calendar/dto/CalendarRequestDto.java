package com.sparta.calendar.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CalendarRequestDto {

    private String title;
    private String username;
    private String contents;

    private Long password;
    private LocalDate date;


}
