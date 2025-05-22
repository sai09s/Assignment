package com.example.rewards.rewardsystem.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.rewards.rewardsystem.service.TransactionService;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    Mockito.when(
            transactionService.calculateRewardsCustomDateRange(anyLong(), anyString(), anyString()))
        .thenReturn(new HashMap<>());
    mockMvc
        .perform(post("/calculateRewardsByRange/1?startDate=2023-01-01&endDate=2023-12-31"))
        .andExpect(status().isOk());
  }
}
