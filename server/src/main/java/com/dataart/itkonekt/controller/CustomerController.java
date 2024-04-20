package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Customer;
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

  @PostMapping("/customers")
  public ResponseEntity<?> createCustomerAccount(@RequestBody CreateCustomerRequest request) {
    return customerRepository.findByEmail(request.email())
        .or(() -> createCustomer(request))
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
  public ResponseEntity<?> createCheckoutSession(@PathVariable("customerId") Integer customerId) {
    // TODO:
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/customers/{customerId}/portal")
  public ResponseEntity<?> createPortalSession(@PathVariable("customerId") Integer customerId) {
    return customerRepository.findById(customerId)
        .flatMap(customer -> stripeApi.createBillingPortalSession(customer.getStripeCustomerId(),
            getCustomerHomeUrl(customer.getId())))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<Customer> createCustomer(CreateCustomerRequest request) {
    var customer = new Customer();
    customer.setName(request.name());
    customer.setEmail(request.email());

    return stripeApi.createCustomer(customerRepository.save(customer))
        .map(stripeCustomerId -> {
          customer.setStripeCustomerId(stripeCustomerId);
          return customerRepository.save(customer);
        });
  }

  private static String getCustomerHomeUrl(Integer customerId) {
    return CUSTOMER_HOME_PAGE + customerId;
  }
}
