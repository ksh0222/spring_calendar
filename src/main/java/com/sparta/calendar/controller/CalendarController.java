package com.sparta.calendar.controller;

import com.sparta.calendar.dto.CalendarRequestDto;
import com.sparta.calendar.dto.CalendarResponseDto;
import com.sparta.calendar.entity.Calendar;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CalendarController {

    private final Map<Long, Calendar> calendarList = new HashMap<>();

    @PostMapping("/calendar")
    public CalendarResponseDto createCalendar(@RequestBody CalendarRequestDto requestDto){
        Calendar calendar = new Calendar(requestDto);

        //메모 아이디 체크
        Long maxId = calendarList.size() > 0 ? Collections.max(calendarList.keySet()) + 1 : 1;
        calendar.setId(maxId);

        //DB저장
        calendarList.put(calendar.getId(), calendar);

        //Entity -> ResponseDto
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto(calendar);

        return calendarResponseDto;
    }

    @GetMapping("/calendars")
    public List<CalendarResponseDto> getCalendars(){
        List<CalendarResponseDto> responseList= calendarList.values().stream()
                .map(CalendarResponseDto::new).toList();

        return responseList;
    }

    @GetMapping("/calendar/{id}")
    public CalendarResponseDto getCalendar(@PathVariable Long id){
        Calendar calendar = calendarList.get(id);

        if (calendar != null) {
            return new CalendarResponseDto(calendar);
        } else {
            throw new IllegalArgumentException("해당 ID에 대응하는 캘린더가 없습니다");
        }

    }

    @PutMapping("/calendar/{id}")
    public Long updateCalendar(@PathVariable Long id, @RequestBody CalendarRequestDto requestDto){
        //해당 메모가 DB에 존재하는지 확인
        if(calendarList.containsKey(id)){
            //해당 일정 가져오기
            Calendar calendar = calendarList.get(id);
            if (requestDto.getPassword() != null && requestDto.getPassword().equals(calendar.getPassword())) {
                calendar.update(requestDto);
                return calendar.getId();
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }else{
            throw new IllegalArgumentException("선택한 일정이 존재하지 않습니다");
        }
    }

    @DeleteMapping("/calendar/{id}")
    public Long deleteCalendar(@PathVariable Long id, @RequestBody Map<String, String> requestBody){


        //해당 메모가 DB에 존재하는지 확인
        if(calendarList.containsKey(id)){

            Calendar calendar = calendarList.get(id);

            String Password = requestBody.get("password");

            if (calendar.getPassword() == null || calendar.getPassword().equals(Long.parseLong(Password))){
                // 비밀번호 일치 시, 캘린더 삭제
                calendarList.remove(id);
                return id;
            } else {
                // 비밀번호 불일치 시, 예외 발생 또는 다른 처리 수행
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }else{
            throw new IllegalArgumentException("선택한 일정이 존재하지 않습니다");
        }
    }



}
