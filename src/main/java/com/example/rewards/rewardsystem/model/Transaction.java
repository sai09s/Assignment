package com.example.rewards.rewardsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amount;

    @Getter
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonManagedReference
    @JsonIgnore
    private Customer customer;

    public double getAmount() {
        return Double.parseDouble(amount);
    }
}
