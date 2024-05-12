package com.dataart.itkonekt.repository;

import com.dataart.itkonekt.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
}
