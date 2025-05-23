package com.example.rewards.rewardsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMonthlyRewardsResponseDto {
    private Long customerId;
    private String customerName;
    private int totalPointsInPeriod;
    private Map<String, Integer> pointsPerMonth;
    private int monthsConsidered;
}
