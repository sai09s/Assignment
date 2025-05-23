package com.example.rewards.rewardsystem.dto;

import lombok.Value;
import java.util.Map;

@Value
public class CustomerMonthlyRewardsResponseDto {
    Long customerId;
    String customerName;
    int totalPointsInPeriod;
    Map<String, Integer> pointsPerMonth;
    int monthsConsidered;
}
