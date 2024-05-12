package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Merchant;
import com.dataart.itkonekt.model.CreateMerchantAccountRequest;
import com.dataart.itkonekt.repository.MerchantRepository;
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
public class MerchantController {
  private static final String MERCHANT_HOME_PAGE = "http://localhost:3000/merchant/";

  private final MerchantRepository merchantRepository;
  private final StripeApi stripeApi;

  @Autowired
  public MerchantController(MerchantRepository merchantRepository, StripeApi stripeApi) {
    this.merchantRepository = merchantRepository;
    this.stripeApi = stripeApi;
  }

  @GetMapping("/merchants")
  public Iterable<Merchant> listMerchants() {
    return merchantRepository.findAll();
  }

  @PostMapping(value = "/merchants")
  public ResponseEntity<?> createMerchantAccount(@RequestBody CreateMerchantAccountRequest request) {
    return createMerchant(request)
        .map(Merchant::getId)
        .map(merchantId -> ResponseEntity.created(URI.create(getMerchantHomeUrl(merchantId))).build())
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @GetMapping("/merchants/{merchantId}")
  public ResponseEntity<?> getMerchantAccount(@PathVariable("merchantId") Integer merchantId) {
    return merchantRepository.findById(merchantId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/merchants/{merchantId}/onboarding")
  public ResponseEntity<?> createOnboardingLink(@PathVariable("merchantId") Integer merchantId) {
    // TODO: create Connect onboarding link for the merchant
    return ResponseEntity.internalServerError().body("Not implemented");
  }

  @PostMapping("/merchants/{merchantId}/dashboard")
  public ResponseEntity<?> createDashboardLink(@PathVariable("merchantId") Integer merchantId) {
    // TODO: create Express Dashboard link for the merchant
    return ResponseEntity.internalServerError().body("Not implemented");
  }

  @PostMapping("/merchants/{merchantId}/session")
  public ResponseEntity<?> createAccountSession(@PathVariable("merchantId") Integer merchantId) {
    // TODO: create Connect account session for the merchant
    return ResponseEntity.internalServerError().body("Not implemented");
  }

  private Optional<Merchant> createMerchant(CreateMerchantAccountRequest request) {
    var merchant = merchantRepository.save(toMerchant(request));

    // TODO: create Connect account for the merchant
    return Optional.of(merchant);
  }

  private static Merchant toMerchant(CreateMerchantAccountRequest request) {
    var merchant = new Merchant();
    merchant.setEmail(request.email());
    merchant.setBusinessName(request.businessName());
    merchant.setStatus(Merchant.Status.PENDING);
    return merchant;
  }

  private static String getMerchantHomeUrl(Integer merchantId) {
    return MERCHANT_HOME_PAGE + merchantId.toString();
  }
}
