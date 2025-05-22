package com.example.rewards.rewardsystem.service;

import com.example.rewards.rewardsystem.TransactionDTO;
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
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final CustomerRepository customerRepository;

  public TransactionService(
      TransactionRepository transactionRepository, CustomerRepository customerRepository) {

    this.transactionRepository = transactionRepository;
    this.customerRepository = customerRepository;
  }

  public Transaction createTransaction(TransactionDTO transactionDTO) {
    Long customerId = transactionDTO.getCustomerId();

    Customer customer = customerRepository.findById(customerId).orElse(null);
    if (customer == null) {
      throw new com.example.rewards.rewardsystem.exception.CustomException(
          com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND);
    }
    Transaction transaction = new Transaction();
    transaction.setAmount(transactionDTO.getAmount());
    transaction.setDate(LocalDate.parse(transactionDTO.getDate()));
    transaction.setCustomer(customer);

    return transactionRepository.save(transaction);
  }

  public CustomerRewardResponseDto calculateRewards(Long customerId) {
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(
                () ->
                    new com.example.rewards.rewardsystem.exception.CustomException(
                        com.example.rewards.rewardsystem.exception.ErrorMessages
                            .CUSTOMER_NOT_FOUND));
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    int totalPoints = 0;
    Map<String, Integer> pointsPerMonth = new HashMap<>();
    List<TransactionResponseDto> transactionDtos =
        transactions.stream()
            .map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getDate().toString()))
            .collect(Collectors.toList());
    for (Transaction transaction : transactions) {
      int points = this.calculatePoints(transaction.getAmount());
      totalPoints += points;
      String month = transaction.getDate().getYear() + "-" + transaction.getDate().getMonthValue();
      pointsPerMonth.put(month, pointsPerMonth.getOrDefault(month, 0) + points);
    }
    return new com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto(
        customer.getId(), customer.getName(), transactionDtos, totalPoints, pointsPerMonth);
  }

  public CustomerRewardResponseDto calculateRewardsCustomDateRange(
      Long customerId, String startDate, String endDate) {
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(
                () ->
                    new com.example.rewards.rewardsystem.exception.CustomException(
                        com.example.rewards.rewardsystem.exception.ErrorMessages
                            .CUSTOMER_NOT_FOUND));
    LocalDate start;
    LocalDate end;
    try {
      start = LocalDate.parse(startDate);
      end = LocalDate.parse(endDate);
    } catch (Exception e) {
      throw new com.example.rewards.rewardsystem.exception.CustomException(
          "Invalid date format. Use yyyy-MM-dd.");
    }
    if (end.isBefore(start)) {
      throw new com.example.rewards.rewardsystem.exception.CustomException(
          "End date must not be before start date.");
    }
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    int totalPoints = 0;
    Map<String, Integer> pointsPerMonth = new HashMap<>();
    List<TransactionResponseDto> transactionDtos =
        transactions.stream()
            .filter(
                t -> {
                  LocalDate date = t.getDate();
                  return (date.isEqual(start) || date.isAfter(start))
                      && (date.isEqual(end) || date.isBefore(end));
                })
            .map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getDate().toString()))
            .collect(Collectors.toList());
    for (Transaction transaction : transactions) {
      LocalDate date = transaction.getDate();
      if ((date.isEqual(start) || date.isAfter(start))
          && (date.isEqual(end) || date.isBefore(end))) {
        int points = this.calculatePoints(transaction.getAmount());
        totalPoints += points;
        String month = date.getYear() + "-" + date.getMonthValue();
        pointsPerMonth.put(month, pointsPerMonth.getOrDefault(month, 0) + points);
      }
    }
    return new com.example.rewards.rewardsystem.dto.CustomerRewardResponseDto(
        customer.getId(), customer.getName(), transactionDtos, totalPoints, pointsPerMonth);
  }

  public Map<String, Object> calculateRewardsByMonths(Long customerId, int months) {
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    int totalPoints = 0;
    Map<String, Integer> pointsPerMonth = new HashMap<>();
    LocalDate now = LocalDate.now();
    LocalDate fromDate = now.minusMonths(months).withDayOfMonth(1);

    for (Transaction transaction : transactions) {
      LocalDate date = transaction.getDate();
      if (!date.isBefore(fromDate)) {
        int points = this.calculatePoints(transaction.getAmount());
        totalPoints += points;
        String month = date.getYear() + "-" + date.getMonthValue();
        pointsPerMonth.put(month, pointsPerMonth.getOrDefault(month, 0) + points);
      }
    }
    Map<String, Object> response = new HashMap<>();
    response.put("totalPoints", totalPoints);
    response.put("pointsPerMonth", pointsPerMonth);
    return response;
  }

  public List<TransactionResponseDto> getTransactions(Long customerId) {
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
    return transactions.stream()
        .map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getDate().toString()))
        .collect(Collectors.toList());
  }

  private int calculatePoints(double amount) {
    if (amount <= 50) {
      return 0;
    } else if (amount <= 100) {
      return (int) (amount - 50);
    } else {
      return (int) (50 + (amount - 100) * 2);
    }
  }
}
