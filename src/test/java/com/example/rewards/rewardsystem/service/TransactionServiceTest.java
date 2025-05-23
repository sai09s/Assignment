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
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTest {

  @Test
  void testCalculateRewards_NoTransactions() {
    // Arrange
    Long customerId = 1L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("NoTxUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdOrderByDateDesc(customerId)).thenReturn(Collections.emptyList());

    // Act
    var result = transactionService.calculateRewards(customerId);

    // Assert
    assertEquals(0, result.getTotalPoints());
    assertTrue(result.getPointsPerMonth().isEmpty());
    assertTrue(result.getTransactions().isEmpty());
  }

  @Test
  void testCalculateRewards_RewardTierBoundaries() {
    // Arrange
    Long customerId = 2L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("BoundaryUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    Transaction t50 = new Transaction(1L, new BigDecimal("50.0"), LocalDate.of(2023, 1, 10), customer);
    Transaction t51 = new Transaction(2L, new BigDecimal("51.0"), LocalDate.of(2023, 1, 11), customer);
    Transaction t100 = new Transaction(3L, new BigDecimal("100.0"), LocalDate.of(2023, 1, 12), customer);
    Transaction t101 = new Transaction(4L, new BigDecimal("101.0"), LocalDate.of(2023, 1, 13), customer);
    when(transactionRepository.findByCustomerIdOrderByDateDesc(customerId)).thenReturn(java.util.Arrays.asList(t101, t100, t51, t50));

    // Act
    var result = transactionService.calculateRewards(customerId);

    // Assert
    assertEquals(0 + 1 + 50 + 52, result.getTotalPoints()); // 0 (50), 1 (51), 50 (100), 52 (101)
    assertEquals(4, result.getTransactions().size());
  }

  @Test
  void testCalculateRewards_MultipleMonths() {
    // Arrange
    Long customerId = 3L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("MultiMonthUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    Transaction janTx = new Transaction(1L, new BigDecimal("120.0"), LocalDate.of(2023, 1, 15), customer);
    Transaction febTx = new Transaction(2L, new BigDecimal("80.0"), LocalDate.of(2023, 2, 10), customer);
    Transaction marTx = new Transaction(3L, new BigDecimal("60.0"), LocalDate.of(2023, 3, 5), customer);
    when(transactionRepository.findByCustomerIdOrderByDateDesc(customerId)).thenReturn(java.util.Arrays.asList(marTx, febTx, janTx));

    // Act
    var result = transactionService.calculateRewards(customerId);

    // Assert
    assertEquals(90 + 30 + 10, result.getTotalPoints());
    assertEquals(3, result.getTransactions().size());
    assertTrue(result.getPointsPerMonth().containsKey("2023-1"));
    assertTrue(result.getPointsPerMonth().containsKey("2023-2"));
    assertTrue(result.getPointsPerMonth().containsKey("2023-3"));
  }

  @Test
  void testCalculateRewardsCustomDateRange_EmptyRange() {
    // Arrange
    Long customerId = 4L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("EmptyRangeUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.emptyList());

    // Act
    var result = transactionService.calculateRewardsCustomDateRange(customerId, LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 31));

    // Assert
    assertEquals(0, result.getTotalPoints());
    assertTrue(result.getPointsPerMonth().isEmpty());
    assertTrue(result.getTransactions().isEmpty());
  }

  @Test
  void testCalculateRewardsCustomDateRange_InvalidDateRange() {
    // Arrange
    Long customerId = 5L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("InvalidDateUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

    // Act & Assert
    Exception ex = assertThrows(com.example.rewards.rewardsystem.exception.CustomException.class,
        () -> transactionService.calculateRewardsCustomDateRange(customerId, LocalDate.of(2023, 6, 1), LocalDate.of(2023, 5, 1)));
    assertEquals("End date must not be before start date.", ex.getMessage());
  }

  @Test
  void testCalculateRewardsByMonths_NoTransactions() {
    // Arrange
    Long customerId = 6L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("NoTxByMonthsUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.emptyList());

    // Act
    var result = transactionService.calculateRewardsByMonths(customerId, 3);

    // Assert
    assertEquals(0, result.getTotalPointsInPeriod());
    assertTrue(result.getPointsPerMonth().isEmpty());
    assertEquals(3, result.getMonthsConsidered());
  }

  @Test
  void testCalculateRewardsByMonths_WithTransactions() {
    // Arrange
    Long customerId = 7L;
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("TxByMonthsUser");
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    LocalDate now = LocalDate.now();
    LocalDate lastMonth = now.minusMonths(1);
    Transaction tx1 = new Transaction(1L, new BigDecimal("120.0"), now.withDayOfMonth(2), customer);
    Transaction tx2 = new Transaction(2L, new BigDecimal("80.0"), lastMonth.withDayOfMonth(10), customer);
    when(transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(java.util.Arrays.asList(tx1, tx2));

    // Act
    var result = transactionService.calculateRewardsByMonths(customerId, 2);

    // Assert
    assertEquals(90 + 30, result.getTotalPointsInPeriod());
    assertEquals(2, result.getPointsPerMonth().size());
    assertEquals(2, result.getMonthsConsidered());
  }
  @Mock private TransactionRepository transactionRepository;
  @Mock private CustomerRepository customerRepository;
  private RewardCalculator rewardCalculator;
  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    rewardCalculator = new RewardCalculator();
    transactionService = new TransactionService(transactionRepository, customerRepository, rewardCalculator);
  }

  @ParameterizedTest
  @CsvSource({
      "0,0",
      "50,0",
      "51,1",
      "100,50",
      "120,90",
      "200,250"
  })
  void testCalculatePointsParameterized(double amount, int expectedPoints) {
    // Arrange
    RewardCalculator calculator = new RewardCalculator();
    // Act
    int points = calculator.calculatePoints(amount);
    // Assert
    assertEquals(expectedPoints, points);
  }




  @Test
  void testCalculateRewardsCustomDateRange() {
    // Arrange
    Long customerId = 1L;
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 12, 31);
    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName("Test User");
    Transaction transaction = new Transaction();
    transaction.setId(1L);
    transaction.setAmount(new BigDecimal("120.0"));
    transaction.setDate(LocalDate.of(2023, 6, 15));
    transaction.setCustomer(customer);
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(transactionRepository.findByCustomerIdAndDateBetweenOrderByDateDesc(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Collections.singletonList(transaction));

    // Act
    var result = transactionService.calculateRewardsCustomDateRange(customerId, startDate, endDate);

    // Assert
    assertEquals(90, result.getTotalPoints());
    assertTrue(result.getPointsPerMonth().containsKey("2023-6"));
    assertEquals(1, result.getTransactions().size());
    assertEquals(1L, result.getTransactions().get(0).getId());
    assertEquals(new BigDecimal("120.0"), result.getTransactions().get(0).getAmount());
    assertEquals("2023-06-15", result.getTransactions().get(0).getDate());
  }

  @Test
  void testCalculateRewardsCustomDateRange_CustomerNotFound() {
    // Arrange
    Long customerId = 999L;
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        com.example.rewards.rewardsystem.exception.CustomException.class,
        () ->
            transactionService.calculateRewardsCustomDateRange(
                customerId, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)));
  }
}
