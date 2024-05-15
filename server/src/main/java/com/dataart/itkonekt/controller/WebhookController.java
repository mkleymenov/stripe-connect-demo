package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.entity.Merchant;
import com.dataart.itkonekt.repository.MerchantRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import com.stripe.model.Account;
import com.stripe.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class WebhookController {
  private static final Logger LOG = LoggerFactory.getLogger(WebhookController.class);
  private static final String STRIPE_SIGNATURE = "Stripe-Signature";

  private final StripeApi stripeApi;
  private final MerchantRepository merchantRepository;

  public WebhookController(@Autowired StripeApi stripeApi, @Autowired MerchantRepository merchantRepository) {
    this.stripeApi = stripeApi;
    this.merchantRepository = merchantRepository;
  }

  @PostMapping("/webhook")
  public ResponseEntity<?> webhook(HttpEntity<String> request) {
    var payload = request.getBody();
    var signature = request.getHeaders().getFirst(STRIPE_SIGNATURE);

    return stripeApi.verifyEvent(payload, signature)
        .map(event -> {
          processEvent(event);
          return ResponseEntity.ok().build();
        })
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  private void processEvent(Event event) {
    LOG.info("Received Stripe event {}", event.getType());

    switch (event.getType()) {
      // TODO: handle Account events
      default -> LOG.info("No handler for event of type {}", event.getType());
    }
  }

  private static Merchant.Status getMerchantStatus(Account account) {
    var disabledReason = Optional.ofNullable(account.getRequirements())
        .map(Account.Requirements::getDisabledReason);
    if (disabledReason.stream().anyMatch(reason -> reason.contains("rejected"))) {
      return Merchant.Status.REJECTED;
    }
    if (disabledReason.stream().anyMatch("under_review"::equals)) {
      return Merchant.Status.IN_REVIEW;
    }
    if (!account.getDetailsSubmitted() || !account.getChargesEnabled()) {
      return Merchant.Status.PENDING;
    }
    return Merchant.Status.ACTIVE;
  }
}
