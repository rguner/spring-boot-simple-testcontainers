package com.guner.springbootsimpletestcontainers.repository;

import com.guner.springbootsimpletestcontainers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}