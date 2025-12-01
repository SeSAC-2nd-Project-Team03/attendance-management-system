package com.sesac2ndproject.attendancemanagementsystem.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
