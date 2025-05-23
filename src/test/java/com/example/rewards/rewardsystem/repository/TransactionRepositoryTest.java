package com.example.rewards.rewardsystem.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.model.Transaction;
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
    tx.setAmount(new java.math.BigDecimal("100.0"));
    tx.setDate(LocalDate.now());
    tx.setCustomer(customer);
    transactionRepository.save(tx);
    List<Transaction> transactions =
        transactionRepository.findByCustomerIdOrderByDateDesc(customer.getId());
    assertFalse(transactions.isEmpty());
    assertEquals(customer.getId(), transactions.get(0).getCustomer().getId());
  }
}
