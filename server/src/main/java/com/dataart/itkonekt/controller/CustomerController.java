package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Customer;
import com.dataart.itkonekt.model.CreateCheckoutSessionRequest;
import com.dataart.itkonekt.model.CreateCustomerRequest;
import com.dataart.itkonekt.repository.CustomerRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class CustomerController {
  private static final String CUSTOMER_HOME_PAGE = "http://localhost:3000/customer/";

  private final CustomerRepository customerRepository;
  private final StripeApi stripeApi;

  @Autowired
  public CustomerController(CustomerRepository customerRepository, StripeApi stripeApi) {
    this.customerRepository = customerRepository;
    this.stripeApi = stripeApi;
  }

  @GetMapping("/customers")
  public Iterable<Customer> listCustomers() {
    return customerRepository.findAll();
  }

  @PostMapping("/customers")
  public ResponseEntity<?> createCustomerAccount(@RequestBody CreateCustomerRequest request) {
    return createCustomer(request)
        .map(Customer::getId)
        .map(id -> ResponseEntity.created(URI.create(getCustomerHomeUrl(id))).build())
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<?> getCustomerAccount(@PathVariable Integer customerId) {
    return customerRepository.findById(customerId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/customers/{customerId}/checkout")
  public ResponseEntity<?> createCheckoutSession(@PathVariable("customerId") Integer customerId,
                                                 @RequestBody CreateCheckoutSessionRequest request) {
    // TODO: create a Stripe Checkout session for a customer and a price
    return ResponseEntity.internalServerError().body("Not implemented");
  }

  @PostMapping("/customers/{customerId}/portal")
  public ResponseEntity<?> createPortalSession(@PathVariable("customerId") Integer customerId) {
    // TODO: create a Stripe Billing Portal session for a customer
    return ResponseEntity.internalServerError().body("Not implemented");
  }

  private Optional<Customer> createCustomer(CreateCustomerRequest request) {
    var customer = new Customer();
    customer.setName(request.name());
    customer.setEmail(request.email());

    // TODO: create Stripe Customer
    return Optional.of(customerRepository.save(customer));
  }

  private static String getCustomerHomeUrl(Integer customerId) {
    return CUSTOMER_HOME_PAGE + customerId;
  }
}
