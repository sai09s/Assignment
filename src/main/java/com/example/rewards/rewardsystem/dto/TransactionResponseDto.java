package com.example.rewards.rewardsystem.dto;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class TransactionResponseDto {
    Long id;
    BigDecimal amount;
    String date;
}
