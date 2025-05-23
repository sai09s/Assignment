package com.example.rewards.rewardsystem.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.rewards.rewardsystem.service.TransactionService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private TransactionService transactionService;

  @Test
  void testCalculateRewardsByRange() throws Exception {
    // Prepare a mock CustomerRewardResponseDto
    var mockResponse =
        new com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto(
            1L,
            "Test User",
            Collections.emptyList(),
            0,
            Collections.emptyMap());
    Mockito.when(
            transactionService.calculateRewardsCustomDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(mockResponse);
    mockMvc
        .perform(get("/api/v1/calculateRewardsByRange/1?startDate=2023-01-01&endDate=2023-12-31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(1L))
        .andExpect(jsonPath("$.customerName").value("Test User"))
        .andExpect(jsonPath("$.totalPoints").value(0))
        .andExpect(jsonPath("$.transactions").isArray())
        .andExpect(jsonPath("$.pointsPerMonth").isMap());
  }
}
