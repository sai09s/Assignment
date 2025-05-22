package com.example.rewards.rewardsystem.repository;

import com.example.rewards.rewardsystem.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByCustomerId(Long customerId);

  // order by date desc
  List<Transaction> findByCustomerIdOrderByDateDesc(Long customerId);
}
