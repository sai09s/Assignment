package com.example.rewards.rewardsystem.repository;

import com.example.rewards.rewardsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>{


}
