package com.dataart.itkonekt.stripe;

import com.dataart.itkonekt.entity.Customer;
import com.dataart.itkonekt.entity.Merchant;
import com.dataart.itkonekt.model.CreateProductRequest;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.AccountSession;
import com.stripe.model.Event;
import com.stripe.model.LoginLink;
import com.stripe.model.Price;
import com.stripe.model.StripeObject;
import com.stripe.param.AccountSessionCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.PriceRetrieveParams;
import com.stripe.param.PriceUpdateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

  /**
   * Creates an Express Connect account for the merchant, prefilling some account details.
   *
   * @param merchant merchant
   * @return Stripe account ID
   */
  public Optional<String> createConnectAccount(Merchant merchant) {
    try {
      throw new RuntimeException("createConnectAccount not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create Stripe account for merchant {}", merchant.getId(), ex);
      return Optional.empty();
    }
  }

  /**
   * Generates an onboarding link for a Stripe account.
   *
   * @param stripeAccountId Stripe account ID
   * @param returnUrl       URL to return to after the onboarding
   * @return account onboarding link URL
   */
  public Optional<String> createConnectOnboardingLink(String stripeAccountId, String returnUrl) {
    try {
      throw new RuntimeException("createConnectOnboardingLink not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create onboarding link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  /**
   * Generates a Stripe Express Dashboard login link for a Connect account.
   *
   * @param stripeAccountId Stripe account ID
   * @return Express Dashboard login link URL
   */
  public Optional<String> createConnectDashboardLink(String stripeAccountId) {
    try {
      return Optional.of(stripeClient.accounts().loginLinks().create(stripeAccountId)).map(LoginLink::getUrl);
    } catch (Exception ex) {
      LOG.error("Couldn't create login link for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  /**
   * Creates a Connect Account session and returns the session client secret.
   *
   * @param stripeAccountId Stripe account ID
   * @return session client secret
   */
  public Optional<String> createConnectAccountSession(String stripeAccountId) {
    try {
      var params = AccountSessionCreateParams.builder()
          .setAccount(stripeAccountId)
          .setComponents(AccountSessionCreateParams.Components.builder()
              .setPayments(AccountSessionCreateParams.Components.Payments.builder().setEnabled(true).build())
              .build())
          .build();

      return Optional.of(stripeClient.accountSessions().create(params)).map(AccountSession::getClientSecret);
    } catch (Exception ex) {
      LOG.error("Couldn't create session for Stripe account {}", stripeAccountId, ex);
      return Optional.empty();
    }
  }

  /**
   * Creates a new Customer in Stripe.
   *
   * @param customer Customer domain object
   * @return Stripe Customer ID
   */
  public Optional<String> createCustomer(Customer customer) {
    try {
      throw new RuntimeException("createCustomer not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create Stripe customer for app customer {}", customer.getId(), ex);
      return Optional.empty();
    }
  }

  /**
   * Creates a Checkout session for a customer and a specific Stripe product.
   * <p>
   * The product can represent a one-time payment as well as a recurring subscription.
   *
   * @param customer       Customer domain object
   * @param stripePrice    Stripe Price object corresponding to a product being purchased
   * @param returnUrl      URL to return the customer to after successful or cancelled checkout
   * @param applicationFee application fee as percent of a total price
   * @return Checkout session URL
   */
  public Optional<String> createCheckoutSession(Customer customer, Price stripePrice, String returnUrl,
                                                double applicationFee) {
    try {
      throw new RuntimeException("createCheckoutSession not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create Checkout session for customer {}", customer.getId(), ex);
      return Optional.empty();
    }
  }

  /**
   * Generates a Billing Portal session for a customer.
   *
   * @param stripeCustomerId Stripe Customer ID
   * @param returnUrl        URL to return the customer to from the Billing Portal page
   * @return Billing Portal session URL
   */
  public Optional<String> createBillingPortalSession(String stripeCustomerId, String returnUrl) {
    try {
      var params = com.stripe.param.billingportal.SessionCreateParams.builder()
          .setCustomer(stripeCustomerId)
          .setReturnUrl(returnUrl)
          .build();

      return Optional.of(stripeClient.billingPortal().sessions().create(params))
          .map(com.stripe.model.billingportal.Session::getUrl);
    } catch (Exception ex) {
      LOG.error("Couldn't create billing portal session for Stripe customer {}", stripeCustomerId, ex);
      return Optional.empty();
    }
  }

  /**
   * Retrieves a Price by ID.
   *
   * @param stripePriceId Stripe Price ID
   * @return Stripe Price object
   */
  public Optional<Price> getPrice(String stripePriceId) {
    try {
      var params = PriceRetrieveParams.builder().addExpand("product").build();

      return Optional.of(stripeClient.prices().retrieve(stripePriceId, params));
    } catch (Exception ex) {
      LOG.error("Couldn't retrieve Stripe price {}", stripePriceId, ex);
      return Optional.empty();
    }
  }

  /**
   * Retrieves all active Stripe Prices, optionally filtered by a merchant.
   *
   * @param merchantId restricts returned Prices to those belonging to the specified merchant
   * @return stream of active Stripe Prices (with expanded Product objects)
   */
  public Stream<Price> listActivePrices(Optional<Integer> merchantId) {
    try {
      var params = PriceListParams.builder().setActive(true).addExpand("data.product");
      merchantId.map(Object::toString).ifPresent(params::addLookupKey);

      return StreamSupport.stream(stripeClient.prices().list(params.build()).autoPagingIterable().spliterator(), false);
    } catch (Exception ex) {
      LOG.error("Couldn't fetch prices from Stripe", ex);
      return Stream.empty();
    }
  }

  /**
   * Creates a new Stripe Product for a merchant.
   *
   * @param merchant merchant account
   * @return Stripe Product ID
   */
  public Optional<String> createMerchantProduct(Merchant merchant) {
    try {
      throw new RuntimeException("createMerchantProduct not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create product for merchant {}", merchant.getId(), ex);
      return Optional.empty();
    }
  }

  /**
   * Creates a new Stripe Price under the specified Stripe Product.
   *
   * @param stripeProductId Stripe Product ID
   * @param request         Price attributes
   * @return Stripe Price object (with expanded Product object)
   */
  public Optional<Price> createPrice(String stripeProductId, CreateProductRequest request) {
    try {
      throw new RuntimeException("createPrice not implemented");
    } catch (Exception ex) {
      LOG.error("Couldn't create price for product {} and merchant {}", stripeProductId, request.merchantId(), ex);
      return Optional.empty();
    }
  }

  /**
   * Disables Stripe Price.
   *
   * @param stripePriceId Stripe Price ID
   */
  public void disablePrice(String stripePriceId) {
    try {
      var params = PriceUpdateParams.builder().setActive(false).build();

      stripeClient.prices().update(stripePriceId, params);
    } catch (Exception ex) {
      LOG.error("Couldn't update price {}", stripePriceId, ex);
    }
  }

  /**
   * Constructs a Stripe Event object from a webhook request payload, verifying the request signature.
   *
   * @param payload   webhook request payload (event JSON)
   * @param signature webhook request signature (header)
   * @return Stripe Event object
   */
  public Optional<Event> verifyEvent(String payload, String signature) {
    try {
      return Optional.ofNullable(stripeClient.constructEvent(payload, signature, webhookSecret));
    } catch (Exception ex) {
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
