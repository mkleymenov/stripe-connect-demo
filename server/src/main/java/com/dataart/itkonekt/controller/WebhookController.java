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

  private final StripeApi stripeApi;
  private final MerchantRepository merchantRepository;

  public WebhookController(@Autowired StripeApi stripeApi, @Autowired MerchantRepository merchantRepository) {
    this.stripeApi = stripeApi;
    this.merchantRepository = merchantRepository;
  }

  @PostMapping("/webhook")
  public ResponseEntity<?> webhook(HttpEntity<String> request) {
    var payload = request.getBody();
    var signature = request.getHeaders().getFirst("Stripe-Signature");

    return stripeApi.verifyEvent(payload, signature)
        .filter(this::processEvent)
        .map(__ -> ResponseEntity.ok().build())
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  private boolean processEvent(Event event) {
    LOG.info("Received event of type: {}", event.getType());

    return switch (event.getType()) {
      // Connect events
      case "account.updated" ->
          StripeApi.deserializeEventObject(event, Account.class).stream().anyMatch(this::onAccountUpdated);

      default -> {
        LOG.info("No handler for event of type: {}", event.getType());
        yield true;
      }
    };
  }

  private boolean onAccountUpdated(Account account) {
    return Optional.ofNullable(account.getMetadata())
        .map(metadata -> metadata.get("merchant_id"))
        .map(Integer::parseInt)
        .flatMap(merchantRepository::findById)
        .map(merchant -> {
          merchant.setStatus(getMerchantStatus(account));
          return merchantRepository.save(merchant);
        })
        .isPresent();
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
