package com.example.trade.model;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class Admin extends AuthorizedUser{
    public Admin(String username, String password, String email, Long balance) {
        super(username, password, email, balance);
    }

    public Admin() {
    }
}
