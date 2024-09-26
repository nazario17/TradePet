package com.example.trade.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class AuthorizedUser extends User {

    @Column()
    private Long balance;

    @OneToMany(mappedBy = "user")
    private Set<Item> items;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<Offer> sentOffers;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private Set<Offer> receivedOffers;


    private Long tradeCount = 0L;

    private LocalDateTime banTime;

    public AuthorizedUser(String username, String password, String email, Long balance) {
        super(username, password, email);
        this.balance = balance;
    }

    public AuthorizedUser() {
    }


    public Long getBalance() {
        return balance;
    }

    public Set<Offer> getReceivedOffers() {
        return receivedOffers;
    }

    public void setReceivedOffers(Set<Offer> receivedOffers) {
        this.receivedOffers = receivedOffers;
    }

    public Set<Offer> getSentOffers() {
        return sentOffers;
    }

    public void setSentOffers(Set<Offer> sentOffers) {
        this.sentOffers = sentOffers;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }


    public Long getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Long tradeCount) {
        this.tradeCount = tradeCount;
    }

    public LocalDateTime getBanTime() {
        return banTime;
    }

    public void setBanTime(LocalDateTime banTime) {
        this.banTime = banTime;
    }

    public void addItem(Item item) {
        if (this.items == null) {
            this.items = new HashSet<>();
        }
        this.items.add(item);
    }

    public void removeItem(Item item) {
        if (this.items != null) {
            this.items.remove(item);
        }
    }


}
