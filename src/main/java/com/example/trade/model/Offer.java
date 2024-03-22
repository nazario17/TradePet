package com.example.trade.model;

import lombok.Data;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToMany
    @JoinTable(
            name = "receivers_items",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> receiversItems = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "senders_items",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> sendersItems = new HashSet<>();
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    @Column
    private STATUS status;





    public Offer() {}

    public Offer(User sender, User receiver, Set<Item> receiversItems) {
        this.sender = sender;
        this.receiver = receiver;
        this.receiversItems = receiversItems;
        this.status = STATUS.NEW;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Set<Item> getReceiversItems() {
        return receiversItems;
    }

    public void setReceiversItems(Set<Item> offeredItems) {
        this.receiversItems = offeredItems;
    }

    public Set<Item> getSendersItems() {
        return sendersItems;
    }

    public void setSendersItems(Set<Item> sendersItems) {
        this.sendersItems = sendersItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
