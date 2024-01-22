package com.sparta.calendar.service;

import com.sparta.calendar.dto.CalendarRequestDto;
import com.sparta.calendar.dto.CalendarResponseDto;
import com.sparta.calendar.entity.Calendar;
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

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO calendar (title, contents, username, password_hash, calendar_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, calendar.getTitle());
                    preparedStatement.setString(2, calendar.getContents());
                    preparedStatement.setString(3, calendar.getUsername());
                    preparedStatement.setString(4, calendar.getPassword());
                    preparedStatement.setObject(5, calendar.getDate());
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        calendar.setId(id);

        // Entity -> ResponseDto
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto(calendar);

        return calendarResponseDto;
    }

    public List<CalendarResponseDto> getCalendar() {

        String sql = "SELECT * FROM calendar";

        return jdbcTemplate.query(sql, new RowMapper<CalendarResponseDto>() {
            @Override
            public CalendarResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                java.sql.Date sqlDate = rs.getDate("calendar_date");
                LocalDate date = sqlDate.toLocalDate();
                return new CalendarResponseDto(id, title, username, contents, date);
            }
        });
    }


    public Long updateCalendar(Long id, CalendarRequestDto requestDto) {

        //해당 메모가 DB에 존재하는지 확인
        Calendar calendar = findById(id);
        if (calendar != null) {
            // 캘린더가 존재할 때
            if (calendar.getPassword().equals(requestDto.getPassword())) {
                // 비밀번호 일치
                String sql = "UPDATE calendar SET title = ?, contents = ?, username = ? WHERE id = ?";
                jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getContents(), requestDto.getUsername(), id);

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



    private Calendar findById(Long id){
        // DB 조회
        String sql = "SELECT * FROM calendar WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if (resultSet.next()) {
                Calendar calendar = new Calendar();
                calendar.setTitle(resultSet.getString("title"));
                calendar.setUsername(resultSet.getString("username"));
                calendar.setContents(resultSet.getString("contents"));
                calendar.setPassword(resultSet.getString("password_hash"));
                java.sql.Date sqlDate = resultSet.getDate("calendar_date");
                calendar.setDate(sqlDate.toLocalDate());
                return calendar;
            } else {
                return null;
            }
        }, id);


    }

    public Long deleteCalendar(Long id, Map<String, String> requestBody) {

        // 해당 캘린더가 DB에 존재하는지 확인
        Calendar calendar = findById(id);

        if (calendar != null) {
            String password = requestBody.get("password");

            if (calendar.getPassword() == null || calendar.getPassword().equals(password)) {
                // 비밀번호 일치 시, 캘린더 삭제
                String sql = "DELETE FROM calendar WHERE id = ?";
                jdbcTemplate.update(sql, id);
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



        String sql = "SELECT * FROM calendar WHERE id = ?";

        try {
            CalendarResponseDto calendar = jdbcTemplate.queryForObject(sql, new Object[]{id}, new RowMapper<CalendarResponseDto>() {
                @Override
                public CalendarResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // SQL 결과를 CalendarResponseDto로 변환
                    Long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String username = rs.getString("username");
                    String contents = rs.getString("contents");
                    java.sql.Date sqlDate = rs.getDate("calendar_date");
                    LocalDate date = sqlDate.toLocalDate();
                    return new CalendarResponseDto(id, title, username, contents, date);
                }
            });

            return ResponseEntity.ok(calendar);
        } catch (EmptyResultDataAccessException e) {
            // 해당 ID에 해당하는 데이터가 없을 경우 404 Not Found 반환
            return ResponseEntity.notFound().build();
        }
    }
}
