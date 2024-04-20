package com.dataart.itkonekt.model;

import java.util.Optional;

public record CreateProductRequest(Integer merchantId, Long price, String currency,
                                   Optional<ProductRecurrence> recurrence) {
}
