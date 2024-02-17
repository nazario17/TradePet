package com.example.trade.model;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Lob
    private byte[] image;
    //todo додавання функціоналу зміни аватару користувача

    @Column
    private boolean active;

    @ElementCollection(targetClass = ROLE.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_name"))
    @Enumerated(EnumType.STRING)
    private Set<ROLE> roles;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
