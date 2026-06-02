package com.jdbc.streaming.controller;

import com.jdbc.streaming.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService service;

  @GetMapping("/items-streaming")
  public void exportItemsStreaming(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
      HttpServletResponse response)
      throws Exception {

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=items.csv");

    service.generateStreaming(response.getOutputStream(), startDate, endDate);
  }

  @GetMapping("/items-traditional")
  public void exportItemsTraditional(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
      HttpServletResponse response)
      throws Exception {

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=items.csv");

    service.generateTraditional(response.getOutputStream(), startDate, endDate);
  }
}
