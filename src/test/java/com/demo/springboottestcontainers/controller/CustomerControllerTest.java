package com.demo.springboottestcontainers.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import com.demo.springboottestcontainers.entity.Customer;
import com.demo.springboottestcontainers.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class CustomerControllerTest {

  @LocalServerPort
  private Integer port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:15-alpine"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  /**
   *
   * Before spring-boot 3.1 we should have used @DynamicPropertySource annotation to set dynamic properties.
   * With @ServiceConnection annotation we can remove @DynamicPropertySource annotation.
   * If we didn't give these parameters test application couldn't connect to test database or connects to default database in application.yaml
   * org.springframework.boot.autoconfigure.jdbc.DataSourceProperties$DataSourceBeanCreationException: Failed to determine suitable jdbc url
   *   or
   *   Caused by: org.hibernate.HibernateException: Unable to determine Dialect without JDBC metadata (please set 'javax.persistence.jdbc.url'
   *
   */

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  CustomerRepository customerRepository;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
    customerRepository.deleteAll();
  }

  @Test
  void logTestContainerDetails() {
    log.info("____________ Test container details ____________");
    log.info("Jdbc Url: {} ", postgres.getJdbcUrl());
    log.info("Username: {} ", postgres.getUsername());
    log.info("Password: {} ", postgres.getPassword());
  }

  @Test
  void shouldGetAllCustomers() {
    List<Customer> customers = List.of(
      new Customer(null, "John", "john@mail.com"),
      new Customer(null, "Dennis", "dennis@mail.com")
    );
    customerRepository.saveAll(customers);

    given()
      .contentType(ContentType.JSON)
      .when()
      .get("/api/customers")
      .then()
      .statusCode(200)
      .body(".", hasSize(2));
  }

}