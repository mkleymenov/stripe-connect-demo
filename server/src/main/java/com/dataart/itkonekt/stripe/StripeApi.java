package com.dataart.itkonekt.stripe;

import com.dataart.itkonekt.entity.Merchant;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.AccountSession;
import com.stripe.model.Event;
import com.stripe.model.LoginLink;
import com.stripe.model.StripeObject;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.AccountSessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StripeApi {
  private static final Logger LOG = LoggerFactory.getLogger(StripeApi.class);

  private final StripeClient stripeClient;
  private final String webhookSecret;

  public StripeApi(@Autowired StripeClient stripeClient,
                   @Value("#{environment['STRIPE_WEBHOOK_SECRET']}") String webhookSecret) {
    this.stripeClient = stripeClient;
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

      return Optional.of(stripeClient.accounts().create(params)).map(Account::getId);
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

      return Optional.of(stripeClient.accountLinks().create(params)).map(AccountLink::getUrl);
    } catch (StripeException ex) {
      LOG.error("Couldn't create onboarding link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  public Optional<String> createConnectDashboardLink(String stripeAccountId) {
    try {
      return Optional.of(stripeClient.accounts().loginLinks().create(stripeAccountId)).map(LoginLink::getUrl);
    } catch (StripeException ex) {
      LOG.error("Couldn't create login link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  public Optional<String> createConnectAccountSession(String stripeAccountId) {
    try {
      var params = AccountSessionCreateParams.builder()
          .setAccount(stripeAccountId)
          .setComponents(AccountSessionCreateParams.Components.builder()
              .setPayments(AccountSessionCreateParams.Components.Payments.builder().setEnabled(true).build())
              .setNotificationBanner(AccountSessionCreateParams.Components.NotificationBanner.builder()
                  .setEnabled(true)
                  .build())
              .build())
          .build();

      return Optional.of(stripeClient.accountSessions().create(params)).map(AccountSession::getClientSecret);
    } catch (StripeException ex) {
      LOG.error("Couldn't create session for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  public Optional<Event> verifyEvent(String payload, String signature) {
    try {
      return Optional.ofNullable(stripeClient.constructEvent(payload, signature, webhookSecret));
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
