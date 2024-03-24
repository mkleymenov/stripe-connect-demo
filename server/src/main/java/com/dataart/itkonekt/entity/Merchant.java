package com.dataart.itkonekt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "stripe_account_id")
    private String stripeAccountId;

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

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Merchant merchant = (Merchant) o;
        return Objects.equals(id, merchant.id) && Objects.equals(email, merchant.email) && Objects.equals(stripeAccountId, merchant.stripeAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, stripeAccountId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Merchant.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("email='" + email + "'")
                .add("stripeAccountId='" + stripeAccountId + "'")
                .toString();
    }
}
