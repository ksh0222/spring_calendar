package com.sparta.calendar.dto;

import com.sparta.calendar.entity.Calendar;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CalendarResponseDto {
    private Long id;
    private String title;
    private String username;
    private String contents;

    private Long password;

    private LocalDate date;


    public CalendarResponseDto(Calendar calendar) {
        this.id = calendar.getId();
        this.title = calendar.getTitle();
        this.username = calendar.getUsername();
        this.contents = calendar.getContents();
        this.date = calendar.getDate();
    }
}
