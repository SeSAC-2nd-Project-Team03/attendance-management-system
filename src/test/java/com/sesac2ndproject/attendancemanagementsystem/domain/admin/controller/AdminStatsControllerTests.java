package com.sesac2ndproject.attendancemanagementsystem.domain.admin.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.service.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminStatsControllerTests {

    @Autowired
    private AdminStatsController adminStatsController;
    @Autowired
    private AdminStatsService adminStatsService;
}
