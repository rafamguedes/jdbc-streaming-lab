package com.jdbc.streaming.controller;

import com.jdbc.streaming.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService service;

  @GetMapping("/items")
  public void exportItems(HttpServletResponse response) throws Exception {

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=items.csv");

    service.generate(response.getOutputStream());
  }
}
