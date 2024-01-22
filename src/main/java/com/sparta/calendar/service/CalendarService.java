package com.sparta.calendar.service;

import com.sparta.calendar.dto.CalendarRequestDto;
import com.sparta.calendar.dto.CalendarResponseDto;
import com.sparta.calendar.entity.Calendar;
import com.sparta.calendar.repository.CalendarRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CalendarService {

    private final JdbcTemplate jdbcTemplate;

    public CalendarService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CalendarResponseDto createCalendar(CalendarRequestDto requestDto) {

        Calendar calendar = new Calendar(requestDto);

        CalendarRepository calendarRepository = new CalendarRepository(jdbcTemplate);
        Calendar saveCalendar = calendarRepository.save(calendar);

        // Entity -> ResponseDto
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto(calendar);

        return calendarResponseDto;
    }

    public List<CalendarResponseDto> getCalendar() {
        CalendarRepository calendarRepository = new CalendarRepository(jdbcTemplate);
        return calendarRepository.findAll();
    }


    public Long updateCalendar(Long id, CalendarRequestDto requestDto) {
        CalendarRepository calendarRepository = new CalendarRepository(jdbcTemplate);

        //해당 메모가 DB에 존재하는지 확인
        Calendar calendar = calendarRepository.findById(id);
        if (calendar != null) {
            if (calendar.getPassword().equals(requestDto.getPassword())) {
                calendarRepository.update(id,requestDto);
                // 비밀번호 일치
                return id;
            } else {
                // 비밀번호 불일치 시, 예외 발생 또는 다른 처리 수행
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 선택한 캘린더가 존재하지 않을 경우 예외 발생 또는 다른 처리 수행
            throw new IllegalArgumentException("선택한 캘린더가 존재하지 않습니다.");
        }
    }

    public Long deleteCalendar(Long id, Map<String, String> requestBody) {
        CalendarRepository calendarRepository = new CalendarRepository(jdbcTemplate);
        // 해당 캘린더가 DB에 존재하는지 확인
        Calendar calendar = calendarRepository.findById(id);

        if (calendar != null) {
            String password = requestBody.get("password");

            if (calendar.getPassword() == null || calendar.getPassword().equals(password)) {
                // 비밀번호 일치 시, 캘린더 삭제
                calendarRepository.delete(id,requestBody);
                return id;
            } else {
                // 비밀번호 불일치 시, 예외 발생 또는 다른 처리 수행
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 선택한 캘린더가 존재하지 않을 경우 예외 발생 또는 다른 처리 수행
            throw new IllegalArgumentException("선택한 캘린더가 존재하지 않습니다.");
        }
    }

    public ResponseEntity<CalendarResponseDto> getCalendarById(Long id) {
        CalendarRepository calendarRepository = new CalendarRepository(jdbcTemplate);
        return calendarRepository.findId(id);
    }
}
