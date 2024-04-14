package com.dataart.itkonekt.repository;

import com.dataart.itkonekt.entity.Merchant;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MerchantRepository extends CrudRepository<Merchant, Integer> {

    Optional<Merchant> findByEmail(String email);
}
