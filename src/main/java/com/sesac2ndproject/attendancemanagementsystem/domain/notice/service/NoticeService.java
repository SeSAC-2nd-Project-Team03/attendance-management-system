package com.sesac2ndproject.attendancemanagementsystem.domain.notice.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.notice.dto.NoticeResponseDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.notice.entity.Notice;
import com.sesac2ndproject.attendancemanagementsystem.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 1. 공지사항 리스트를 조회하는 로직(Pageable 사용하여 1페이지당 10개를 받아올 수 있도록 함)
    @Transactional(readOnly = true)
    public NoticeResponseDTO getAllNotices(Pageable pageable) {

        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;    // pageable의 pageNumber는 0부터 시작하기 때문에 보정
        int size = pageable.getPageSize();
        String sortDir = "id";

        // PageRequest 객체 생성 (페이지번호, 사이즈, 정렬 방법)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 조회
        Page<Notice> noticeList = noticeRepository.findAll(pageable);



        return null;
    }
    // 2. 공지사항을 Id로 조회하는 로직.
    // 3. 접속한 날짜가 'popupStartDate'와 'popupEndDate'의 범위안에 들 경우 홈페이지에 접속하면 자동으로 해당하는 공지사항을 찾아서 보여줌.
}
