package com.example.trade.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Item{

    @Id
    private Long id;

    private Long price;

    private Long quantity;
}
