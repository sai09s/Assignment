package com.example.rewards.rewardsystem.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TransactionRepositoryTest {
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private CustomerRepository customerRepository;

  @Test
  void testFindByCustomerIdOrderByDateDesc() {
    Customer customer = new Customer();
    customer.setName("RepoTest");
    customer = customerRepository.save(customer);
    Transaction tx = new Transaction();
    tx.setAmount(new BigDecimal("100.0"));
    tx.setDate(LocalDate.now());
    tx.setCustomer(customer);
    transactionRepository.save(tx);
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customer.getId());
    assertFalse(transactions.isEmpty());
    assertEquals(customer.getId(), transactions.get(0).getCustomer().getId());
  }

  @Test
  void testFindByCustomerIdOrderByDateDesc_NoTransactions() {
    Customer customer = new Customer();
    customer.setName("NoTxRepoTest");
    customer = customerRepository.save(customer);
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customer.getId());
    assertTrue(transactions.isEmpty());
  }

  @Test
  void testFindByCustomerIdOrderByDateDesc_MultipleTransactionsOrdering() {
    Customer customer = new Customer();
    customer.setName("OrderTest");
    customer = customerRepository.save(customer);
    Transaction tx1 = new Transaction();
    tx1.setAmount(new BigDecimal("50.0"));
    tx1.setDate(LocalDate.of(2023, 1, 1));
    tx1.setCustomer(customer);
    Transaction tx2 = new Transaction();
    tx2.setAmount(new BigDecimal("75.0"));
    tx2.setDate(LocalDate.of(2023, 2, 1));
    tx2.setCustomer(customer);
    transactionRepository.save(tx1);
    transactionRepository.save(tx2);
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customer.getId());
    assertEquals(2, transactions.size());
    assertTrue(transactions.get(0).getDate().isAfter(transactions.get(1).getDate()));
  }
}
