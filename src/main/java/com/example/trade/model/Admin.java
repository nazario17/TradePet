package com.example.trade.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Admin extends User{
    public Admin(String username, String password, String email) {
        super(username, password, email);
    }

    public Admin() {
    }
}
