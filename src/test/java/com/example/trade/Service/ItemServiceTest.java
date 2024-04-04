package com.example.trade.Service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.Item;
import com.example.trade.model.Offer;
import com.example.trade.model.STATUS;
import com.example.trade.repository.ItemRepository;
import com.example.trade.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    void testGetItemsById() {
        String[] ids = {"1", "2", "3"};

        Set<Item> resultItems = itemService.getItemsById(ids);
        assertNotNull(resultItems);
    }

    @Test
    void testIsItemInOtherOffers_ItemPresentInOtherOffer_StatusDeclined() {
        Item item1 = new Item();
        Item item2 = new Item();
        // Створення поточної пропозиції
        Offer currentOffer = new Offer();

        Set<Item> receiverItems = new HashSet<>();
        receiverItems.add(item2);
        currentOffer.setReceiversItems(receiverItems);

        Set<Item> senderItems = new HashSet<>();
        senderItems.add(item1);
        senderItems.add(item2);
        currentOffer.setReceiversItems(receiverItems);
        currentOffer.setSendersItems(senderItems);

        //Створення користувача
        AuthorizedUser user = new AuthorizedUser();
        user.setReceivedOffers(new HashSet<>());

        //створення іншого оферу
        Offer otherOffer = new Offer();
        otherOffer.getSendersItems().add(item1);
        user.getReceivedOffers().add(otherOffer);

        itemService.isItemInOtherOffers(currentOffer, user);

        assertEquals(STATUS.DECLINED, otherOffer.getStatus());
    }
}
