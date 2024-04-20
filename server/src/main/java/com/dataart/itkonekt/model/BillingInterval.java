package com.dataart.itkonekt.model;

import java.util.Arrays;

public enum BillingInterval {
  // https://docs.stripe.com/api/prices/object#price_object-recurring-interval
  DAY, WEEK, MONTH, YEAR;

  public static BillingInterval fromStripeInterval(String interval) {
    return Arrays.stream(values())
        .filter(value -> value.name().equalsIgnoreCase(interval))
        .findFirst()
        .orElse(null);
  }
}
