package com.example.trade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class AuthorizedUser extends User{

    @Column()
    private Long balance;

    public AuthorizedUser(String username, String password, String email, Long balance) {
        super(username, password, email);
        this.balance = balance;
    }

    public AuthorizedUser() {
    }
}
