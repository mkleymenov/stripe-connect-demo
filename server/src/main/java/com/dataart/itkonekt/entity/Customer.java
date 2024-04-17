package com.dataart.itkonekt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "customer")
public class Customer {

  @Id
  @GeneratedValue
  private Integer id;

  @Column
  private String name;

  @Column
  private String email;

  @Column(name = "stripe_customer_id")
  private String stripeCustomerId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStripeCustomerId() {
    return stripeCustomerId;
  }

  public void setStripeCustomerId(String stripeCustomerId) {
    this.stripeCustomerId = stripeCustomerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email,
        customer.email) && Objects.equals(stripeCustomerId, customer.stripeCustomerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, stripeCustomerId);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Customer.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("name='" + name + "'")
        .add("email='" + email + "'")
        .add("stripeCustomerId='" + stripeCustomerId + "'")
        .toString();
  }
}
