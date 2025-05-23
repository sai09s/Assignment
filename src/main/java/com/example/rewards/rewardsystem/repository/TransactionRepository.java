package com.example.rewards.rewardsystem.repository;

import com.example.rewards.rewardsystem.model.Transaction;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // order by date desc
    List<Transaction> findByCustomerIdOrderByDateDesc(Long customerId);

    // Efficient date range query
    List<Transaction> findByCustomerIdAndDateBetweenOrderByDateDesc(Long customerId, LocalDate start, LocalDate end);
}
