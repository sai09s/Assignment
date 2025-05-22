package com.example.rewards.rewardsystem.service;

import com.example.rewards.rewardsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import com.example.rewards.rewardsystem.model.Customer;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

}
