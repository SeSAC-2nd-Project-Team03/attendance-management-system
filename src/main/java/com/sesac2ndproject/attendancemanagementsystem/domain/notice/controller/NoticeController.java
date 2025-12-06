package com.sesac2ndproject.attendancemanagementsystem.domain.notice.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.notice.dto.NoticeResponseDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.notice.repository.NoticeRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공지사항(notice) 조회", description = "Admin과 Member 둘 다 가능한 공지사항 조회 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NoticeController {
    private final NoticeService noticeService;
    // 만들어야 할 것(Member,Admin 용)
    // Controller는 Admin, Member 둘 다 접근 가능하다.
    // 공지사항 리스트는 공지사항id 기준 내림차순으로 한다.
    // Member가 할 수 있는건 조회뿐이다. 생성/수정/삭제 권한은 Admin만 가질 수 있다.
    //공지 목록 조회	GET	/api/v1/notices
    //공지 상세 조회	GET	/api/v1/notices/{id}
    //팝업 공지 조회	GET	/api/v1/notices/popups
    // 1. 공지사항 리스트를 조회하는 로직(Pageable 사용하여 1페이지당 10개를 받아올 수 있도록 함)
    @GetMapping("/notices")
    public ResponseEntity<Page<NoticeResponseDTO>> getAllNotices(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
//        Page<NoticeResponseDTO> result =;
//        return result;
        return null;
    }

    // 2. 공지사항을 Id로 조회하는 로직.
    // 3. 접속한 날짜가 'popupStartDate'와 'popupEndDate'의 범위안에 들 경우 홈페이지에 접속하면 자동으로 해당하는 공지사항을 찾아서 보여줌.
    // 4. Admin이 공지사항을 생성(POST)하는 로직.
    // 5. Admin이 Id로 불러낸 공지사항을 수정(PUT)하는 로직.
    // 6. Admin이 Id로 해당 공지사항을 삭제(DELETE)하는 로직.
}
