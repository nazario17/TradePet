package com.example.trade.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Item{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Long price;

    @Column
    private String quality;

    @Column
    private Long quantity = 0L;

    @ManyToOne
    @JoinColumn(name = "username")
    private AuthorizedUser user;

    public Item(String name, String description, Long price, String quality, AuthorizedUser user) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quality = quality;
        this.user = user;
        this.quantity+=1;
    }

    public Item() {
    }
}
