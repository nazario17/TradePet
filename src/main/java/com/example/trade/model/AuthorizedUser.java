package com.example.trade.model;


import javax.persistence.*;

import lombok.Data;

import java.util.Set;

@Entity
@Data
public class AuthorizedUser extends User{

    @Column()
    private Long balance;

    @OneToMany(mappedBy = "user")
    private Set<Item> items;

    private Long tradeCount = 0L;

    public AuthorizedUser(String username, String password, String email, Long balance) {
        super(username, password, email);
        this.balance = balance;
    }

    public AuthorizedUser() {
    }
}
