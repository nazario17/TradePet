package com.example.trade.controllers;

import com.example.trade.model.*;
import com.example.trade.service.ItemService;
import com.example.trade.service.MailService;
import com.example.trade.service.OfferService;
import com.example.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private MailService mailService;

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    OfferService offerService;

    @GetMapping("/trade")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup() {
        return "register";
    }

    @PostMapping("/signup")
    public String signup_processing(Model model, @RequestParam String cPassword, User user) {
        if (userService.getByName(user.getUsername()) != null) {
            model.addAttribute("error", "User exists");
            return "register";
        } else if (!(user.getPassword().equals(cPassword))) {
            model.addAttribute("error", "Passwords don't match");
            return "register";
        }
        userService.createUser(user);
        return "redirect:/login";
    }

    // /admin controllers
    @GetMapping("/trade/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/trade/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllAuthorizedUsers());
        return "users";
    }

    @PostMapping("trade/admin/{username}")
    public String newAdmin(@PathVariable String username, @RequestParam String action) {
        AuthorizedUser user = userService.getByName(username);
        if (action.equals("makeAdmin")) {
            userService.addRole(ROLE.ADMIN, user);
        } else {
            userService.banUser(user);
        }
        return "redirect:/trade/admin/users";
    }


// /user controllers
    @GetMapping("/trade/my")
    public String userPage(Principal principal, Model model) {
        AuthorizedUser user = userService.getByName(principal.getName());
        List<Item> inventory = itemService.getByUser(user);
        userService.calcBalance(user);
        model.addAttribute("user", user);
        model.addAttribute("image", userService.getUserImage(user));
        model.addAttribute("inventory", inventory);
        return "user_page";
    }

    @PostMapping("/trade/my")
    public String userPageProccesing(@RequestParam String name,
                                     @RequestParam Long price,
                                     @RequestParam String quality,
                                     @RequestParam String description,
                                     Principal principal) {
        AuthorizedUser authorizedUser = userService.getByName(principal.getName());
        itemService.save(name, description, price, quality,
                authorizedUser);
        return "redirect:/trade/my";
    }

    @GetMapping("/trade/search")
    public String search(@RequestParam String query) {
        return "redirect:/trade/my/" + query;
    }


    @GetMapping("/trade/my/{username}")
    public String viewUserProfile(@PathVariable String username,
                                  @RequestParam(name = "text", required = false) String text,
                                  @RequestParam(name = "offerId", required = false) Long offerId,
                                  Model model, Principal principal) {

        if (!principal.getName().equals(username)) {
            User user = userService.getByName(username.trim());

            if (text != null && !text.isEmpty()) {
                model.addAttribute("text", text);
                model.addAttribute("offerId", offerId);
            }
            String image = userService.getUserImage(user);
            List<Item> inventory = itemService.getByUser(user);
            model.addAttribute("inventory", inventory);
            model.addAttribute("user", user);
            model.addAttribute("image", image);
            return "userProfile";
        } else return "redirect:/trade/my";
    }


    @PostMapping("/trade/my/{username}")
    public String postTrade(@PathVariable String username,
                            @RequestParam(name = "offerId", required = false) Long offerId,
                            @RequestParam(value = "inventoryIds", required = false) String[] inventoryIds,
                            Principal principal) {
        if (offerId == null) {
            AuthorizedUser receiver = userService.getByName(username);
            AuthorizedUser sender = userService.getByName(principal.getName());
            Offer offer = offerService.createNewOffer(inventoryIds, sender, receiver);
            offerService.save(offer, sender, receiver);
            String content = "<p>Hello,</p>"
                    + "<p>You have new request to trade</p>"
                    + "<p>Please, check this out";
            String from = "vowa.legun@gmail.com";
            String to = receiver.getEmail();
            mailService.createAndSendMimeMessageAsync(content, from, to);
            return "redirect:/trade/my/select?offerId=" + offer.getId() + "&update=false&url=c";
        } else {
            Offer offer = offerService.getById(offerId);
            offer.setSendersItems(itemService.getItemsById(inventoryIds));
            offerService.save(offer);
            return "redirect:/trade/my/select?offerId=" + offer.getId() + "&update=true&url=c";
        }


    }

    @GetMapping("trade/my/select")
    public String selectItems(@RequestParam(name = "offerId", required = false) Long offerId,
                              @RequestParam(name = "update", required = false) boolean update,
                              @RequestParam(name = "url", required = false) String url,
                              Principal principal, Model model) {
        if (url.equals("c")) {
            AuthorizedUser user = userService.getByName(principal.getName());
            List<Item> inventory = itemService.getByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("inventory", inventory);
            model.addAttribute("offerId", offerId);
            model.addAttribute("update", update);
            return "select_items";
        } else return "user_page";
    }

    @PostMapping("trade/my/select")
    public String selectItemsForm(@RequestParam("offerId") Long offerId,
                                  @RequestParam(value = "inventoryIds", required = false) String[] inventoryIds,
                                  @RequestParam("update") boolean update) {

        if (update) {
            Offer offer = offerService.getById(offerId);
            offerService.setReceiversItemsById(offerId, inventoryIds);
            offer.setStatus(STATUS.UPDATED);
            AuthorizedUser user = (AuthorizedUser) offer.getReceiver();
            offerService.setReceiver(offer.getSender(), offer);
            offerService.setSender(user, offer);
            offerService.setUpdatedAt(LocalDateTime.now(), offer);
            offerService.save(offer);
            String content = "<p>Hello,</p>"
                    + "<p>Your trade request has been updated</p>"
                    + "<p>Please, check this out";
            String from = "vowa.legun@gmail.com";
            String to = offer.getSender().getEmail();
            mailService.createAndSendMimeMessageAsync(content, from, to);
        } else {
            offerService.setSendersItemsById(offerId, inventoryIds);
        }
        return "redirect:/trade/my";
    }


    @GetMapping("trade/my/offers")
    public String offers(Model model, Principal principal) {
        AuthorizedUser user = userService.getByName(principal.getName());
        model.addAttribute("sent_offers", offerService.sort(user.getSentOffers()));
        model.addAttribute("receive_offers", offerService.sort(user.getReceivedOffers()));

        return "your_offers_page";
    }

    @GetMapping("trade/my/offers/edit/{offerId}")
    public String editOffer(@PathVariable Long offerId, Principal principal) {
        Offer offer = offerService.getById(offerId);
        offerService.setStatus(STATUS.CANCELLED, offer);
        offerService.save(offer);
        if (principal.getName().equals(offer.getSender().getUsername())) {
            return "redirect:/trade/my/" + offer.getReceiver().getUsername() +
                    "?offerId=" + offerId + "&text=choose items you want to get";
        } else {
            return "redirect:/trade/my/" + offer.getSender().getUsername() +
                    "?offerId=" + offerId + "&text=choose items you want to get";
        }
    }

    @GetMapping("/trade/my/offers/decline/{offerId}")
    public String declineOffer(@PathVariable Long offerId) {
        Offer offer = offerService.getById(offerId);
        offerService.setStatus(STATUS.DECLINED, offer);
        offerService.save(offer);
        return "redirect:/trade/my/offers";
    }

    @GetMapping("/trade/my/offers/accept/{offerId}")
    public String acceptOffer(@PathVariable Long offerId) {
        offerService.acceptOffer(offerId);
        return "redirect:/trade/my/offers";
    }


    @PostMapping("/trade/my/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        AuthorizedUser user = userService.getByName(principal.getName());
        try {
            userService.setImage(file.getBytes(), user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userService.save(user);
        return "redirect:/trade/my";
    }

}

