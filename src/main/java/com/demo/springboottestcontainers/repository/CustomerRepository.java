package com.demo.springboottestcontainers.repository;

import com.demo.springboottestcontainers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}