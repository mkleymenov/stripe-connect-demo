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
  // Application fee as percent of a total purchase amount
  private static final double APPLICATION_FEE_PERCENT = 10.0;

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
    return customerRepository.findById(customerId)
        .flatMap(customer ->
            stripeApi.getPrice(request.stripePriceId())
                .flatMap(price ->
                    stripeApi.createCheckoutSession(customer, price, getCustomerHomeUrl(customerId),
                        APPLICATION_FEE_PERCENT)
                )
        )
        .map(url -> ResponseEntity.created(URI.create(url)).build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/customers/{customerId}/portal")
  public ResponseEntity<?> createPortalSession(@PathVariable("customerId") Integer customerId) {
    return customerRepository.findById(customerId)
        .map(Customer::getStripeCustomerId)
        .flatMap(stripeCustomerId ->
            stripeApi.createBillingPortalSession(stripeCustomerId, getCustomerHomeUrl(customerId)))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<Customer> createCustomer(CreateCustomerRequest request) {
    return Optional.of(customerRepository.save(toCustomer(request)))
        .flatMap(customer ->
            stripeApi.createCustomer(customer).map(stripeCustomerId -> {
              customer.setStripeCustomerId(stripeCustomerId);
              return customerRepository.save(customer);
            }));
  }

  private static String getCustomerHomeUrl(Integer customerId) {
    return CUSTOMER_HOME_PAGE + customerId;
  }

  private static Customer toCustomer(CreateCustomerRequest request) {
    var customer = new Customer();
    customer.setName(request.name());
    customer.setEmail(request.email());
    return customer;
  }
}
