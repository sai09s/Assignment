package com.example.rewards.rewardsystem.controller;

import com.example.rewards.rewardsystem.TransactionDTO;
import com.example.rewards.rewardsystem.dto.TransactionResponseDto;
import com.example.rewards.rewardsystem.model.Transaction;
import com.example.rewards.rewardsystem.service.TransactionService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping("/transaction")
  public Transaction createTransaction(@RequestBody TransactionDTO transactionDTO) {
    return transactionService.createTransaction(transactionDTO);
  }

  @GetMapping("/transactions/{customerId}")
  public List<TransactionResponseDto> getTransactions(@PathVariable Long customerId) {
    return transactionService.getTransactions(customerId);
  }

  @GetMapping("/calculateRewards/{customerId}")
  public com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto calculateRewards(
      @PathVariable Long customerId) {
    return transactionService.calculateRewards(customerId);
  }

  // calculate rewards for custom 3 months - may be date range
  @PostMapping("/calculateRewardsByRange/{customerId}")
  public com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto calculateRewardsByRange(
      @PathVariable Long customerId, @RequestParam String startDate, @RequestParam String endDate) {
    return transactionService.calculateRewardsCustomDateRange(customerId, startDate, endDate);
  }

  // calculate rewards for custom months
  // like last 3 months
  // or last 6 months
  @PostMapping("/calculateRewardsByMonths/{customerId}")
  public Map<String, Object> calculateRewardsByMonths(
      @PathVariable Long customerId, @RequestParam int months) {
    return transactionService.calculateRewardsByMonths(customerId, months);
  }
}
