package com.sesac2ndproject.attendancemanagementsystem.domain.course.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Enrollment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 객체(@ManyToOne) 대신 ID(Long)를 사용하여 의존성 제거
    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long courseId;


    private LocalDateTime finishedAt; // 수료일/중도포기일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

}
