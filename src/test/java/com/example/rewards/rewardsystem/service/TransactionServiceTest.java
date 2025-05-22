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
    String startDate = "2023-01-01";
    String endDate = "2023-12-31";
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("Test User");
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setAmount("120.0");
    transaction.setDate(LocalDate.of(2023, 6, 15));
    transaction.setCustomer(customer);
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdOrderByDateDesc(customerId))
        .thenReturn(Collections.singletonList(transaction));
    var result = transactionService.calculateRewardsCustomDateRange(customerId, startDate, endDate);
    assertEquals(90, result.get("totalPoints"));
    assertTrue(((java.util.Map<?, ?>) result.get("pointsPerMonth")).containsKey("2023-6"));
  }

  @Test
  void testCalculateRewardsCustomDateRange_CustomerNotFound() {
    Long customerId = 999L;
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
    assertThrows(
        com.example.rewards.rewardsystem.exception.CustomException.class,
        () ->
            transactionService.calculateRewardsCustomDateRange(
                customerId, "2023-01-01", "2023-12-31"));
  }
}
