package com.example.rewards.rewardsystem.controller;

import com.example.rewards.rewardsystem.TransactionDTO;
import com.example.rewards.rewardsystem.model.Transaction;
import com.example.rewards.rewardsystem.dto.TransactionResponseDto;
import com.example.rewards.rewardsystem.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public Map<String, Object> calculateRewards(@PathVariable Long customerId) {
        return transactionService.calculateRewards(customerId);
    }
}
