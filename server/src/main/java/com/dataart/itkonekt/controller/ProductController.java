package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Merchant;
import com.dataart.itkonekt.model.BillingInterval;
import com.dataart.itkonekt.model.CreateProductRequest;
import com.dataart.itkonekt.model.Product;
import com.dataart.itkonekt.model.ProductRecurrence;
import com.dataart.itkonekt.repository.MerchantRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import com.stripe.model.Price;
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
    return stripeApi.listActivePrices(merchantId)
        .map(ProductController::toProduct)
        .toList();
  }

  @PostMapping("/products")
  public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
    return merchantRepository.findById(request.merchantId())
        .flatMap(merchant ->
            stripeApi.findMerchantPrice(merchant.getId())
                .map(oldPrice -> replacePrice(oldPrice, request))
                .orElseGet(() -> createFirstPrice(merchant, request))
        )
        .map(ProductController::toProduct)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  private static Product toProduct(Price price) {
    var recurrence = Optional.ofNullable(price.getRecurring())
        .map(recurring ->
            new ProductRecurrence(
                BillingInterval.fromStripeInterval(recurring.getInterval()),
                recurring.getIntervalCount()));

    return new Product(
        price.getId(),
        price.getProductObject().getName(),
        price.getUnitAmount(),
        price.getCurrency(),
        recurrence);
  }

  private Optional<Price> replacePrice(Price oldPrice, CreateProductRequest request) {
    return stripeApi.createPrice(oldPrice.getProduct(), request)
        .map(newPrice -> {
          stripeApi.disablePrice(oldPrice.getId());
          return newPrice;
        });
  }

  private Optional<Price> createFirstPrice(Merchant merchant, CreateProductRequest request) {
    return stripeApi.createMerchantProduct(merchant)
        .flatMap(stripeProductId -> stripeApi.createPrice(stripeProductId, request));
  }
}
