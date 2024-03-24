package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.repository.CustomerRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private static final String CUSTOMER_HOME_PAGE = "http://localhost:8080/customer/";

    private final CustomerRepository customerRepository;
    private final StripeApi stripeApi;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, StripeApi stripeApi) {
        this.customerRepository = customerRepository;
        this.stripeApi = stripeApi;
    }

    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@RequestParam String email) {
        // TODO
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/customer/{customerId}/checkout")
    public ResponseEntity<?> createCheckoutSession(@PathVariable("customerId") Integer customerId) {
        // TODO:
        return ResponseEntity.noContent().build();
    }
}
