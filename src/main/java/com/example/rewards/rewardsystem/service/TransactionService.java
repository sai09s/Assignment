package com.example.rewards.rewardsystem.service;

import com.example.rewards.rewardsystem.dto.TransactionDTO;
import com.example.rewards.rewardsystem.exception.ResourceNotFoundException;
import com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto;
import com.example.rewards.rewardsystem.dto.TransactionResponseDto;
import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.model.Transaction;
import com.example.rewards.rewardsystem.repository.CustomerRepository;
import com.example.rewards.rewardsystem.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  // Helper record for reward calculation result
  private static record RewardCalculationResult(int totalPoints, Map<String, Integer> pointsPerMonth) {}
  private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M");
  private final TransactionRepository transactionRepository;
  private final CustomerRepository customerRepository;
  private final RewardCalculator rewardCalculator;

  @Autowired
  public TransactionService(
      TransactionRepository transactionRepository,
      CustomerRepository customerRepository,
      RewardCalculator rewardCalculator) {
    this.transactionRepository = transactionRepository;
    this.customerRepository = customerRepository;
    this.rewardCalculator = rewardCalculator;
  }

  public TransactionResponseDto createTransaction(TransactionDTO transactionDTO) {
    Long customerId = transactionDTO.getCustomerId();
    Customer customer = customerRepository.findById(customerId).orElse(null);
    if (customer == null) {
      throw new ResourceNotFoundException(
          com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND + ": " + customerId);
    }
    Transaction transaction = new Transaction();
    transaction.setAmount(transactionDTO.getAmount());
    transaction.setDate(LocalDate.parse(transactionDTO.getDate()));
    transaction.setCustomer(customer);
    Transaction saved = transactionRepository.save(transaction);
    return new TransactionResponseDto(saved.getId(), saved.getAmount(), saved.getDate().toString());
  }

  public CustomerRewardResponseDto calculateRewards(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND + ": " + customerId));
    List<Transaction> transactions = transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    return buildCustomerRewardResponse(customer, transactions);
  }

public CustomerRewardResponseDto calculateRewardsCustomDateRange(
      Long customerId, LocalDate startDate, LocalDate endDate) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND + ": " + customerId));
    if (endDate.isBefore(startDate)) {
      throw new com.example.rewards.rewardsystem.exception.CustomException(
          "End date must not be before start date.");
    }
    List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(customerId, startDate, endDate);
    return buildCustomerRewardResponse(customer, transactions);
  }
  private CustomerRewardResponseDto buildCustomerRewardResponse(Customer customer, List<Transaction> transactions) {
    List<TransactionResponseDto> transactionDtos =
        transactions.stream()
            .map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getDate().toString()))
            .collect(Collectors.toList());
    RewardCalculationResult rewardResult = processTransactionsForRewards(transactions);
    return new CustomerRewardResponseDto(
        customer.getId(),
        customer.getName(),
        transactionDtos,
        rewardResult.totalPoints(),
        rewardResult.pointsPerMonth());
  }

  public com.example.rewards.rewardsystem.dto.CustomerMonthlyRewardsResponseDto calculateRewardsByMonths(Long customerId, int months) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND + ": " + customerId));
    LocalDate now = LocalDate.now();
    LocalDate fromDate = now.minusMonths(months).withDayOfMonth(1);
    List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(
        customerId, fromDate, now);
    RewardCalculationResult rewardResult = processTransactionsForRewards(transactions);
    return new com.example.rewards.rewardsystem.dto.CustomerMonthlyRewardsResponseDto(
        customer.getId(),
        customer.getName(),
        rewardResult.totalPoints(),
        rewardResult.pointsPerMonth(),
        months
    );
  }
// Removed duplicate processTransactionsForRewards method

  public List<TransactionResponseDto> getTransactions(Long customerId) {
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    return transactions.stream()
        .map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getDate().toString()))
        .collect(Collectors.toList());
  }

  private RewardCalculationResult processTransactionsForRewards(List<Transaction> transactions) {
    int totalPoints = 0;
    Map<String, Integer> pointsPerMonth = new HashMap<>();
    for (Transaction transaction : transactions) {
      LocalDate date = transaction.getDate();
      int points = rewardCalculator.calculatePoints(transaction.getAmount());
      totalPoints += points;
      String monthKey = date.format(MONTH_YEAR_FORMATTER);
      pointsPerMonth.put(monthKey, pointsPerMonth.getOrDefault(monthKey, 0) + points);
    }
    return new RewardCalculationResult(totalPoints, pointsPerMonth);
  }

  // calculatePoints moved to RewardCalculator
}
