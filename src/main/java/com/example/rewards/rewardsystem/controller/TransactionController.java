package com.example.rewards.rewardsystem.controller;

import com.example.rewards.rewardsystem.dto.TransactionDTO;
import com.example.rewards.rewardsystem.dto.TransactionResponseDto;
import com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto;

import com.example.rewards.rewardsystem.dto.CustomerMonthlyRewardsResponseDto;
import org.springframework.http.ResponseEntity;
import com.example.rewards.rewardsystem.service.TransactionService;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated // Required for validating @RequestParam, @PathVariable, etc.
@RequestMapping("/api/v1")
public class TransactionController {
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping("/transaction")
  public ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody TransactionDTO transactionDTO) {
    TransactionResponseDto responseDto = transactionService.createTransaction(transactionDTO);
    return ResponseEntity.status(201).body(responseDto);
  }

  @GetMapping("/transactions/{customerId}")
  public List<TransactionResponseDto> getTransactions(@PathVariable Long customerId) {
    return transactionService.getTransactions(customerId);
  }

  @GetMapping("/calculateRewards/{customerId}")
  public CustomerRewardResponseDto calculateRewards(@PathVariable Long customerId) {
    return transactionService.calculateRewards(customerId);
  }

  // calculate rewards for custom 3 months - may be date range
  @GetMapping("/calculateRewardsByRange/{customerId}")
  public CustomerRewardResponseDto calculateRewardsByRange(
      @PathVariable Long customerId,
      @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("endDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return transactionService.calculateRewardsCustomDateRange(customerId, startDate, endDate);
  }

  // calculate rewards for custom months
  // like last 3 months
  // or last 6 months
  @GetMapping("/calculateRewardsByMonths/{customerId}")
  public CustomerMonthlyRewardsResponseDto calculateRewardsByMonths(
      @PathVariable Long customerId, @RequestParam @Min(1) int months) {
    return transactionService.calculateRewardsByMonths(customerId, months);
  }
}
