package com.example.rewards.rewardsystem.service;

import com.example.rewards.rewardsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.dto.CustomerResponseDto;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDto createCustomer(Customer customer) {
        Customer saved = customerRepository.save(customer);
        return new CustomerResponseDto(saved.getId(), saved.getName());
    }

}
