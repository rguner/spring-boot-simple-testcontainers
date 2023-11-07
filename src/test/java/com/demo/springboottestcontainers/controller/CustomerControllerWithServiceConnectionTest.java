package com.demo.springboottestcontainers.controller;

import com.demo.springboottestcontainers.entity.Customer;
import com.demo.springboottestcontainers.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Testcontainers
class CustomerControllerWithServiceConnectionTest {

  @LocalServerPort
  private Integer port;
  @Container
  @ServiceConnection // With @ServiceConnection annotation we can remove @DynamicPropertySource annotation.
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
          "postgres:15-alpine"
  );

  /*
  With the Testcontainers JUnit 5 Extension,
  we can use @Testcontainers and @Container to automatically start and stop the container.
  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }
   */


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