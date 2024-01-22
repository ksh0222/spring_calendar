package com.sparta.calendar.controller;

import com.sparta.calendar.dto.CalendarRequestDto;
import com.sparta.calendar.dto.CalendarResponseDto;
import com.sparta.calendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class CalendarController {

    private final JdbcTemplate jdbcTemplate;

    public CalendarController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/calendar")
    public CalendarResponseDto createCalendar(@RequestBody CalendarRequestDto requestDto) {
        CalendarService calendarService = new CalendarService(jdbcTemplate);
        return calendarService.createCalendar(requestDto);
    }


    @GetMapping("/calendars")
    public List<CalendarResponseDto> getCalendars() {
        CalendarService calendarService = new CalendarService(jdbcTemplate);
        return calendarService.getCalendar();
    }

    @GetMapping("/calendars/{id}")
    public ResponseEntity<CalendarResponseDto> getCalendarById(@PathVariable Long id) {
        CalendarService calendarService = new CalendarService(jdbcTemplate);
        return calendarService.getCalendarById(id);
    }

    @PutMapping("/calendar/{id}")
    public Long updateCalendar(@PathVariable Long id, @RequestBody CalendarRequestDto requestDto) {
        CalendarService calendarService = new CalendarService(jdbcTemplate);
        return calendarService.updateCalendar(id, requestDto);
    }

    @DeleteMapping("/calendar/{id}")
    public Long deleteCalendar(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        CalendarService calendarService = new CalendarService(jdbcTemplate);
        return calendarService.deleteCalendar(id, requestBody);
    }

    }

