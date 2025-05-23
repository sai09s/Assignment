package com.example.rewards.rewardsystem.dto;

import java.util.List;
import java.util.Map;
import lombok.Value;

@Value
public class CustomerRewardResponseDto {
    Long customerId;
    String customerName;
    List<TransactionResponseDto> transactions;
    int totalPoints;
    Map<String, Integer> pointsPerMonth;
}
