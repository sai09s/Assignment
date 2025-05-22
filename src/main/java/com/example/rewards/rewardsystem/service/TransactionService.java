package com.example.rewards.rewardsystem.service;

import com.example.rewards.rewardsystem.TransactionDTO;
import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.model.Transaction;
import com.example.rewards.rewardsystem.repository.CustomerRepository;
import com.example.rewards.rewardsystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.rewards.rewardsystem.dto.TransactionResponseDto;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public TransactionService(TransactionRepository transactionRepository,

                              CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Long customerId = transactionDTO.getCustomerId();

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new com.example.rewards.rewardsystem.exception.CustomException(
                com.example.rewards.rewardsystem.exception.ErrorMessages.CUSTOMER_NOT_FOUND
            );
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDate(LocalDate.parse(transactionDTO.getDate()));
        transaction.setCustomer(customer);

        return transactionRepository.save(transaction);
    }

    public Map<String, Object> calculateRewards(Long customer_id) {
        List<Transaction> transactions = transactionRepository.findByCustomerIdOrderByDateDesc(customer_id);
        int totalPoints = 0;
        // pointsPerMonth is Object that contains year-month and points
         Map<String, Integer> pointsPerMonth = new HashMap<>();
        for (Transaction transaction : transactions) {
            int points = this.calculatePoints(transaction.getAmount());
            totalPoints += points;
            String month = transaction.getDate().getYear() + "-" + transaction.getDate().getMonthValue();
            if (pointsPerMonth.containsKey(month)) {
                pointsPerMonth.put(month, pointsPerMonth.get(month) + points);
            } else {
                pointsPerMonth.put(month, points);
            }
        }
        Map<String, Object> makeResponse = new HashMap<>();
        makeResponse.put("totalPoints", totalPoints);
        makeResponse.put("pointsPerMonth", pointsPerMonth);
        return makeResponse;
    }

    public List<TransactionResponseDto> getTransactions(Long customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerIdOrderByDateDesc(customerId);
        return transactions.stream()
                .map(t -> new TransactionResponseDto(
                        t.getId(),
                        t.getAmount(),
                        t.getDate().toString()
                ))
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
