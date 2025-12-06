package com.sesac2ndproject.attendancemanagementsystem.domain.notice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공지사항(notice) 생성/수정/삭제", description = "Admin만 가능 한 공지사항 CUD 기능")
@RestController
@RequestMapping("/api/v1/admin/")
public class AdminNoticeController {
    // 만들어야 할 것(관리자용)
    // 본 Controller는 Admin만 접근 가능하다.
    // 공지 등록	POST	/api/v1/admin/notices
    // 공지 수정	PUT	    /api/v1/admin/notices/{id}
    // 공지 삭제	DELETE	/api/v1/admin/notices/{id}
    // 4. Admin이 공지사항을 생성(POST)하는 로직.
    // 5. Admin이 Id로 불러낸 공지사항을 수정(PUT)하는 로직.
    // 6. Admin이 Id로 해당 공지사항을 삭제(DELETE)하는 로직.
}
