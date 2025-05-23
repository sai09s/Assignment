package com.example.rewards.rewardsystem.controller;

import com.example.rewards.rewardsystem.dto.CustomerResponseDto;
import com.example.rewards.rewardsystem.model.Customer;
import com.example.rewards.rewardsystem.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping("/customer")
  public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody Customer customer) {
    CustomerResponseDto createdCustomer = customerService.createCustomer(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
  }
}
