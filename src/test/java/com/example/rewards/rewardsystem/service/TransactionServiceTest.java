package com.example.rewards.rewardsystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.model.Transaction;
import com.example.rewards.rewardsystem.repository.CustomerRepository;
import com.example.rewards.rewardsystem.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTest {
  @Mock private TransactionRepository transactionRepository;
  @Mock private CustomerRepository customerRepository;
  @InjectMocks private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCalculateRewardsCustomDateRange() {
    Long customerId = 1L;
    java.time.LocalDate startDate = java.time.LocalDate.of(2023, 1, 1);
    java.time.LocalDate endDate = java.time.LocalDate.of(2023, 12, 31);
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("Test User");
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setAmount(new java.math.BigDecimal("120.0"));
    transaction.setDate(LocalDate.of(2023, 6, 15));
    transaction.setCustomer(customer);
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.singletonList(transaction));
    var result = transactionService.calculateRewardsCustomDateRange(customerId, startDate, endDate);
    assertEquals(90, result.getTotalPoints());
    assertTrue(result.getPointsPerMonth().containsKey("2023-6"));
    assertEquals(1, result.getTransactions().size());
    assertEquals(1L, result.getTransactions().get(0).getId());
    assertEquals(new java.math.BigDecimal("120.0"), result.getTransactions().get(0).getAmount());
    assertEquals("2023-06-15", result.getTransactions().get(0).getDate());
  }

  @Test
  void testCalculateRewardsCustomDateRange_CustomerNotFound() {
    Long customerId = 999L;
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
    assertThrows(
        com.example.rewards.rewardsystem.exception.CustomException.class,
        () ->
            transactionService.calculateRewardsCustomDateRange(
                customerId, java.time.LocalDate.of(2023, 1, 1), java.time.LocalDate.of(2023, 12, 31)));
  }
}
