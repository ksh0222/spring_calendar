package com.sparta.calendar.entity;

import com.sparta.calendar.dto.CalendarRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Calendar {
    private Long id;
    private String title;
    private String username;
    private String contents;

    private String password;

    private LocalDate date;

    public Calendar(CalendarRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.contents = requestDto.getContents();
        this.password = requestDto.getPassword();
        this.date = LocalDate.now();
    }

    public void update(CalendarRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.contents = requestDto.getContents();
    }

}
