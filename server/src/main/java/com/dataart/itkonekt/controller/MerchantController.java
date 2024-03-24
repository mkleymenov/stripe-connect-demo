package com.dataart.itkonekt.controller;

import com.dataart.itkonekt.repository.MerchantRepository;
import com.dataart.itkonekt.stripe.StripeApi;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantController {
    private static final String MERCHANT_HOME_PAGE = "http://localhost:8080/merchant/";

    private final MerchantRepository merchantRepository;
    private final StripeApi stripeApi;

    @Autowired
    public MerchantController(MerchantRepository merchantRepository, StripeApi stripeApi) {
        this.merchantRepository = merchantRepository;
        this.stripeApi = stripeApi;
    }

    @PostMapping("/merchant")
    @Transactional
    public ResponseEntity<?> createAccount(@RequestParam String email) {
        // TODO
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merchant/{merchantId}/dashboard")
    public ResponseEntity<?> createDashboardLink(@PathVariable("merchantId") Integer merchantId) {
        // TODO
        return ResponseEntity.noContent().build();
    }
}
