package com.example.trade.service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.Item;
import com.example.trade.model.User;
import com.example.trade.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public void save(String name, String description, Long price, String quality, AuthorizedUser user){
        Item item = new Item(name,description,price,quality,user);
        itemRepository.save(item);
    }

    public List<Item> getByUser(User user){
        return itemRepository.findAllByUser(user);
    }
}
