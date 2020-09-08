package com.buddhi.banking.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "bank", name = "balance")
public class Balance {
    @Id
    private Long userId;
    private Double balance;
}
