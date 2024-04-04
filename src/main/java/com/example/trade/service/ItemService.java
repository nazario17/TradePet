package com.example.trade.service;

import com.example.trade.model.*;
import com.example.trade.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public void save(String name, String description, Long price, String quality, AuthorizedUser user) {
        Item item = new Item(name, description, price, quality, user);
        itemRepository.save(item);
    }

    public Set<Item> getItemsById(String[] ids) {
        Set<Item> items = new HashSet<>();
        if (ids != null) {
            for (String id : ids) {
                items.add(getById(Integer.parseInt(id)));
            }
            return items;
        } else return Collections.EMPTY_SET;
    }

    public Item getById(int id) {
        return itemRepository.findItemById(id);
    }

    public List<Item> getByUser(User user) {
        return itemRepository.findAllByUser(user);
    }


    public boolean isItemInOtherOffers(Offer currentOffer, AuthorizedUser user) {
        Set<Item> currentOfferItems = new HashSet<>(currentOffer.getSendersItems());
        currentOfferItems.addAll(currentOffer.getReceiversItems());

        for (Offer offer : user.getReceivedOffers()) {
            if (!offer.equals(currentOffer)) {
                Set<Item> offerItems = new HashSet<>(offer.getSendersItems());
                offerItems.addAll(offer.getReceiversItems());
                for (Item currentItem : currentOfferItems) {
                    if (offerItems.contains(currentItem)) {
                        offer.setStatus(STATUS.DECLINED);
                    }
                }
            }
        }

        return true; // Якщо всі предмети доступні, повертаємо true
    }
}
