package com.dataart.itkonekt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "merchant")
public class Merchant {
  public enum Status {
    REJECTED,
    PENDING,
    IN_REVIEW,
    ACTIVE
  }

  @Id
  @GeneratedValue
  private Integer id;

  @Column
  private String email;

  @Column(name = "business_name")
  private String businessName;

  @Column(name = "stripe_account_id")
  private String stripeAccountId;

  @Column
  @Enumerated
  private Status status;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getBusinessName() {
    return businessName;
  }

  public void setBusinessName(String businessName) {
    this.businessName = businessName;
  }

  public String getStripeAccountId() {
    return stripeAccountId;
  }

  public void setStripeAccountId(String stripeAccountId) {
    this.stripeAccountId = stripeAccountId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Merchant merchant = (Merchant) o;
    return Objects.equals(id, merchant.id) && Objects.equals(email, merchant.email) && Objects.equals(businessName,
        merchant.businessName) && Objects.equals(stripeAccountId, merchant.stripeAccountId) && status == merchant.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, businessName, stripeAccountId, status);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Merchant.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("email='" + email + "'")
        .add("businessName='" + businessName + "'")
        .add("stripeAccountId='" + stripeAccountId + "'")
        .add("status=" + status)
        .toString();
  }
}
