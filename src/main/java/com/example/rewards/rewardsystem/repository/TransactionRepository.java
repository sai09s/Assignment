package com.example.rewards.rewardsystem.repository;

import com.example.rewards.rewardsystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerId(Long customerId);

    // order by date desc
    List<Transaction> findByCustomerIdOrderByDateDesc(Long customerId);
}
