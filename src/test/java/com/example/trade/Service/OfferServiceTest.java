package com.example.trade.Service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.Item;
import com.example.trade.model.Offer;
import com.example.trade.model.STATUS;
import com.example.trade.repository.OfferRepository;
import com.example.trade.service.ItemService;
import com.example.trade.service.OfferService;
import com.example.trade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @InjectMocks
    private OfferService offerService;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAcceptOffer_AllItemsAvailable() {
        // Створення поточної пропозиції
        Offer currentOffer = new Offer();
        currentOffer.setId(1L);

        Item item1 = new Item();
        Item item2 = new Item();

        Set<Item> receiverItems = new HashSet<>();
        receiverItems.add(item2);
        currentOffer.setReceiversItems(receiverItems);

        Set<Item> senderItems = new HashSet<>();
        senderItems.add(item1);
        currentOffer.setSendersItems(senderItems);

        AuthorizedUser sender = new AuthorizedUser();
        sender.setUsername("A");
        sender.setItems(new HashSet<>());

        AuthorizedUser receiver = new AuthorizedUser();
        receiver.setUsername("B");
        receiver.setItems(new HashSet<>());

        currentOffer.setSender(sender);
        currentOffer.setReceiver(receiver);

        Offer otherOffer = new Offer();
        otherOffer.getSendersItems().add(item1);

        when(offerRepository.getById(anyLong())).thenReturn(currentOffer);
        when(itemService.isItemInOtherOffers(any(), any())).thenReturn(true);

        offerService.acceptOffer(1L);

        // Перевірка, чи статус пропозиції був змінений на STATUS.ACCEPTED
        assertEquals(STATUS.ACCEPTED, currentOffer.getStatus());
        // Перевірка, чи викликався метод save для поточної пропозиції
        verify(offerRepository, times(1)).save(currentOffer);
    }

    @Test
    void testAcceptOffer_NotAllItemsAvailable() {
        // Створення поточної пропозиції
        Offer currentOffer = new Offer();
        currentOffer.setId(1L);

        Item item1 = new Item();
        Item item2 = new Item();

        Set<Item> receiverItems = new HashSet<>();
        receiverItems.add(item2);
        currentOffer.setReceiversItems(receiverItems);

        Set<Item> senderItems = new HashSet<>();
        senderItems.add(item1);
        currentOffer.setSendersItems(senderItems);

        AuthorizedUser sender = new AuthorizedUser();
        sender.setUsername("A");
        sender.setItems(new HashSet<>());

        AuthorizedUser receiver = new AuthorizedUser();
        receiver.setUsername("B");
        receiver.setItems(new HashSet<>());

        currentOffer.setSender(sender);
        currentOffer.setReceiver(receiver);

        Offer otherOffer = new Offer();
        otherOffer.getSendersItems().add(item1);

        when(offerRepository.getById(anyLong())).thenReturn(currentOffer);
        when(itemService.isItemInOtherOffers(any(), any())).thenReturn(false);

        offerService.acceptOffer(1L);

        // Перевірка, чи статус пропозиції був змінений на STATUS.DECLINED
        assertEquals(STATUS.DECLINED, currentOffer.getStatus());
        // Перевірка, чи викликався метод save для поточної пропозиції
        verify(offerRepository, times(1)).save(currentOffer);
    }
}
