package com.example.trade.repository;


import com.example.trade.model.Item;
import com.example.trade.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

    List<Item> findAllByUser(User user);


}
