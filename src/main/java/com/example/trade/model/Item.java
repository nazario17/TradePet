package com.example.trade.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Item{

    @Id
    private Long id;

    private Long price;

    private Long quantity;
}
