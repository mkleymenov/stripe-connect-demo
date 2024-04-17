package com.dataart.itkonekt.repository;

import com.dataart.itkonekt.entity.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

  Optional<Customer> findByEmail(String email);
}
