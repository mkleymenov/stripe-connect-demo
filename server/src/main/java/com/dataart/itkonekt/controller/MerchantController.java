package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Merchant;
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
  public record CreateMerchantAccountRequest(String email, String businessName) {
  }

  private static final String MERCHANT_HOME_PAGE = "http://localhost:3000/merchant/";

  private final MerchantRepository merchantRepository;
  private final StripeApi stripeApi;

  @Autowired
  public MerchantController(MerchantRepository merchantRepository, StripeApi stripeApi) {
    this.merchantRepository = merchantRepository;
    this.stripeApi = stripeApi;
  }

  @PostMapping(value = "/merchant")
  public ResponseEntity<?> createMerchantAccount(@RequestBody CreateMerchantAccountRequest request) {
    return merchantRepository.findByEmail(request.email)
        .or(() -> createMerchant(request))
        .map(Merchant::getId)
        .map(merchantId -> ResponseEntity.created(URI.create(getMerchantHomeUrl(merchantId))).build())
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @GetMapping("/merchant/{merchantId}")
  public ResponseEntity<?> getMerchantAccount(@PathVariable("merchantId") Integer merchantId) {
    return merchantRepository.findById(merchantId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/merchant/{merchantId}/onboarding")
  public ResponseEntity<?> createOnboardingLink(@PathVariable("merchantId") Integer merchantId) {
    return merchantRepository.findById(merchantId)
        .flatMap(merchant -> stripeApi.createConnectOnboardingLink(merchant.getStripeAccountId(),
            getMerchantHomeUrl(merchantId)))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/merchant/{merchantId}/dashboard")
  public ResponseEntity<?> createDashboardLink(@PathVariable("merchantId") Integer merchantId) {
    return merchantRepository.findById(merchantId)
        .map(Merchant::getStripeAccountId)
        .flatMap(stripeApi::createConnectDashboardLink)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/merchant/{merchantId}/session")
  public ResponseEntity<?> createAccountSession(@PathVariable("merchantId") Integer merchantId) {
    return merchantRepository.findById(merchantId)
        .map(Merchant::getStripeAccountId)
        .flatMap(stripeApi::createConnectAccountSession)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<Merchant> createMerchant(CreateMerchantAccountRequest request) {
    var merchant = merchantRepository.save(toMerchant(request));

    return stripeApi.createConnectAccount(merchant)
        .map(stripeAccountId -> {
          merchant.setStripeAccountId(stripeAccountId);
          return merchantRepository.save(merchant);
        });
  }

  private static Merchant toMerchant(CreateMerchantAccountRequest request) {
    var merchant = new Merchant();
    merchant.setEmail(request.email);
    merchant.setBusinessName(request.businessName);
    merchant.setStatus(Merchant.Status.PENDING);
    return merchant;
  }

  private static String getMerchantHomeUrl(Integer merchantId) {
    return MERCHANT_HOME_PAGE + merchantId.toString();
  }
}
