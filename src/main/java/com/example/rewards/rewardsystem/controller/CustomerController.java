package com.example.rewards.rewardsystem.controller;

import com.example.rewards.rewardsystem.dto.CustomerResponseDto;
import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping("/customer")
  public CustomerResponseDto createCustomer(@RequestBody Customer customer) {
    return customerService.createCustomer(customer);
  }
}
