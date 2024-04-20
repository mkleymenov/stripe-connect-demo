package com.dataart.itkonekt.model;

import java.util.Optional;

public record Product(String stripeId, String name, long price, String currency,
                      Optional<ProductRecurrence> recurrence) {
}
