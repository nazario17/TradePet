package com.example.trade.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Admin extends AuthorizedUser{

    public Admin() {
    }
}
