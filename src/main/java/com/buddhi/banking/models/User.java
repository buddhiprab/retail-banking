package com.buddhi.banking.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "bank", name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
