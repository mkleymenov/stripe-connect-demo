package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.model.CreateProductRequest;
import com.dataart.itkonekt.model.Product;
import com.dataart.itkonekt.repository.MerchantRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class ProductController {
  private final StripeApi stripeApi;
  private final MerchantRepository merchantRepository;

  @Autowired
  public ProductController(StripeApi stripeApi, MerchantRepository merchantRepository) {
    this.stripeApi = stripeApi;
    this.merchantRepository = merchantRepository;
  }

  @GetMapping("/products")
  public List<Product> getProducts(@RequestParam Optional<Integer> merchantId) {
    // TODO: get a list of merchant's active Stripe Products
    return List.of();
  }

  @PostMapping("/products")
  public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
    // TODO: Create a new Stripe Product and a Price or replace an existing Price for the same Product
    return ResponseEntity.internalServerError().body("Not implemented");
  }
}
