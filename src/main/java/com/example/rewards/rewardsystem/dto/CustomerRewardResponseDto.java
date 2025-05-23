package com.example.rewards.rewardsystem.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardResponseDto {
  private Long customerId;
  private String customerName;
  private List<TransactionResponseDto> transactions;
  private int totalPoints;
  private Map<String, Integer> pointsPerMonth;
}
