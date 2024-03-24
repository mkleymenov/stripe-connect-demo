package com.dataart.itkonekt.stripe;

import com.stripe.net.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class StripeApi {
    private static final Logger LOG = LoggerFactory.getLogger(StripeApi.class);

    private final RequestOptions requestOptions;

    public StripeApi(@Value("#{environment['STRIPE_API_KEY']}") String apiKey) {
        this.requestOptions = RequestOptions.builder().setApiKey(apiKey).build();
    }

    public Optional<String> createConnectAccount(String email, Map<String, String> metadata) {
        // TODO
        return Optional.empty();
    }

    public Optional<String> createConnectOnboardingLink(String connectAccountId, String returnUrl) {
        // TODO
        return Optional.empty();
    }

    public Optional<String> createConnectDashboardLink(String connectAccountId) {
        // TODO
        return Optional.empty();
    }
}
