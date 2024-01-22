package com.sparta.calendar.repository;

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

public class CalendarRepository {
    private final JdbcTemplate jdbcTemplate;
    public CalendarRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Calendar save(Calendar calendar) {
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

        return calendar;
    }

    public List<CalendarResponseDto> findAll() {


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

    public ResponseEntity<CalendarResponseDto> findId(Long id) {



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


    public void update(Long id, CalendarRequestDto requestDto) {
        String sql = "UPDATE calendar SET title = ?, contents = ?, username = ? WHERE id = ?";
        jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getContents(), requestDto.getUsername(), id);

    }

    public void delete(Long id, Map<String, String> requestBody) {
        String sql = "DELETE FROM calendar WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


    public Calendar findById(Long id){
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

}
