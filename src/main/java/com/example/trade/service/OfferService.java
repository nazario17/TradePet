package com.example.trade.service;

import com.example.trade.model.*;
import com.example.trade.repository.ItemRepository;
import com.example.trade.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


import static com.example.trade.model.STATUS.*;

@Service
public class OfferService {

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    public Offer createNewOffer(String[] ids, AuthorizedUser sender, AuthorizedUser receiver) {
        Set<Item> items = itemService.getItemsById(ids);
        Offer offer = new Offer(sender, receiver, items);
        offer.setCreatedAt(LocalDateTime.now());
        return offer;
    }


    public Offer getById(Long id) {
        return offerRepository.getById(id);
    }

    public void setSendersItemsById(Long id, String[] sendersItems) {
        Offer offer = getById(id);
        offer.setSendersItems(itemService.getItemsById(sendersItems));
        offerRepository.save(offer);
    }

    public void setReceiversItemsById(Long id, String[] sendersItems) {
        Offer offer = getById(id);
        offer.setReceiversItems(itemService.getItemsById(sendersItems));
        offerRepository.save(offer);
    }


    //todo accept logic
    public void acceptOffer(Long offerId) {
        Offer offer = getById(offerId);
        AuthorizedUser sender = (AuthorizedUser) offer.getSender();
        AuthorizedUser receiver = (AuthorizedUser) offer.getReceiver();
        boolean areAllItemsAvailable = itemService.isItemInOtherOffers(offer, receiver);
        if (areAllItemsAvailable) {
            Set<Item> sendersItems = offer.getSendersItems();
            Set<Item> receiversItems = offer.getReceiversItems();
            sender.getItems().removeAll(sendersItems);
            for (Item item : receiversItems) {
                item.setUser(sender);
            }
            sender.setTradeCount(sender.getTradeCount() + 1);
            userService.save(sender);
            receiver.getItems().removeAll(receiversItems);
            for (Item item : sendersItems) {
                item.setUser(receiver);
            }
            receiver.setTradeCount(receiver.getTradeCount() + 1);
            userService.save(receiver);

            offer.setStatus(ACCEPTED);
        }
        else {
            offer.setStatus(DECLINED);
        }
        save(offer);
    }

    public Set<Offer> sort(Set<Offer> offers) {
        Set<Offer> sortedOffers = new TreeSet<>(Comparator.comparingInt((Offer o) -> {
            switch (o.getStatus()) {
                case NEW:
                    return 1;
                case UPDATED:
                    return 2;
                case ACCEPTED:
                    return 3;
                case DECLINED:
                    return 4;
                default:
                    return 0; // Якщо потрібно додати додаткові статуси
            }
        }).thenComparing(Offer::getId));

        sortedOffers.addAll(offers);
        return sortedOffers;
    }


    public void save(Offer offer, AuthorizedUser sender, AuthorizedUser receiver) {//good
        sender.getSentOffers().add(offer);
        receiver.getReceivedOffers().add(offer);

        offerRepository.save(offer);
    }

    public void save(Offer offer) {
        offerRepository.save(offer);
    }
}


//todo decline && accept offer functional