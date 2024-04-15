package com.dataart.itkonekt.stripe;

import com.dataart.itkonekt.entity.Merchant;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Event;
import com.stripe.model.LoginLink;
import com.stripe.model.StripeObject;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.LoginLinkCreateOnAccountParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StripeApi {
  private static final Logger LOG = LoggerFactory.getLogger(StripeApi.class);

  private final RequestOptions requestOptions;
  private final String webhookSecret;

  public StripeApi(@Value("#{environment['STRIPE_API_KEY']}") String apiKey,
                   @Value("#{environment['STRIPE_WEBHOOK_SECRET']}") String webhookSecret) {
    this.requestOptions = RequestOptions.builder().setApiKey(apiKey).build();
    this.webhookSecret = webhookSecret;
  }

  public Optional<String> createConnectAccount(Merchant merchant) {
    try {
      var params = new AccountCreateParams.Builder()
          .setType(AccountCreateParams.Type.EXPRESS)
          .setEmail(merchant.getEmail())
          .setCapabilities(AccountCreateParams.Capabilities.builder()
              // Enable the connected account to receive card payments
              .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
              // Enable the platform to transfer funds to the connected account
              .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
              .build())
          .setBusinessProfile(AccountCreateParams.BusinessProfile.builder().setName(merchant.getBusinessName()).build())
          .putMetadata("merchant_id", merchant.getId().toString())
          .build();

      return Optional.of(Account.create(params, requestOptions)).map(Account::getId);
    } catch (StripeException ex) {
      LOG.error("Couldn't create Stripe account for merchant {}", merchant.getId(), ex);
      return Optional.empty();
    }
  }

  public Optional<String> createConnectOnboardingLink(String stripeAccountId, String returnUrl) {
    try {
      var params = AccountLinkCreateParams.builder()
          .setAccount(stripeAccountId)
          .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
          .setRefreshUrl(returnUrl)
          .setReturnUrl(returnUrl)
          .build();

      return Optional.of(AccountLink.create(params, requestOptions)).map(AccountLink::getUrl);
    } catch (StripeException ex) {
      LOG.error("Couldn't create onboarding link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  public Optional<String> createConnectDashboardLink(String stripeAccountId) {
    try {
      return Optional.of(LoginLink.createOnAccount(stripeAccountId, LoginLinkCreateOnAccountParams.builder()
          .build(), requestOptions)).map(LoginLink::getUrl);
    } catch (StripeException ex) {
      LOG.error("Couldn't create login link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  public Optional<Event> verifyEvent(String payload, String signature) {
    try {
      return Optional.ofNullable(Webhook.constructEvent(payload, signature, webhookSecret));
    } catch (StripeException ex) {
      LOG.error("Couldn't verify Stripe event '{}'. Signature: '{}'", payload, signature, ex);
      return Optional.empty();
    }
  }

  public static <T extends StripeObject> Optional<T> deserializeEventObject(Event event, Class<T> objectClass) {
    var deserializer = event.getDataObjectDeserializer();
    return deserializer.getObject().or(() -> {
      try {
        return Optional.of(deserializer.deserializeUnsafe());
      } catch (StripeException ex) {
        LOG.error("Couldn't deserialize object of event {} due to Stripe API version mismatch. Event API version: {}," +
                  " Stripe SDK API version: {}",
            event.getId(), event.getApiVersion(), Stripe.API_VERSION);
        return Optional.empty();
      }
    }).map(objectClass::cast);
  }
}
