package com.demo.springboottestcontainers.controller;

import java.util.List;

import com.demo.springboottestcontainers.entity.Customer;
import com.demo.springboottestcontainers.repository.CustomerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final CustomerRepository customerRepository;

  public CustomerController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @GetMapping("/api/customers")
  public List<Customer> getAll() {
    return customerRepository.findAll();
  }
}