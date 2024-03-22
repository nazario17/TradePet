package com.example.trade.controllers;


import com.example.trade.model.*;
import com.example.trade.repository.UserRepository;
import com.example.trade.service.ItemService;
import com.example.trade.service.MailService;
import com.example.trade.service.OfferService;
import com.example.trade.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

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

    ///admin controllers
    @GetMapping("/trade/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/trade/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllAuthorizedUsers());
        return "users";
    }


    @GetMapping("trade/admin/{username}")
    public String newAdmin(@PathVariable String username) {
        AuthorizedUser user = (AuthorizedUser) userService.getByName(username);
        Admin admin = new Admin(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getBalance());
        admin.setActive(true);
        admin.setRoles(user.getRoles());
        admin.setItems(user.getItems());
        userService.save(admin);
        return "redirect:/trade/admin/users";
    }

//////////////////////////////////////////////////////////
///////user controllers//////////////////////

    @GetMapping("/trade/my")
    public String userPage(Principal principal, Model model) {
        User user = userService.getByName(principal.getName());
        List<Item> inventory = itemService.getByUser(user);
        model.addAttribute("user", (AuthorizedUser) user);
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
        AuthorizedUser authorizedUser =
                (AuthorizedUser) userService.getByName(principal.getName());
        itemService.save(name, description, price, quality,
                authorizedUser);
        userService.calcBalance(authorizedUser);
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
            //todo Як ці 3 їбучі рядки фіксять проблему оновлення сторінки /trade/my/{username}????????
            if (user == null) {
                return "redirect:/trade/my";
            }
            if (text != null && !text.isEmpty()) {
                model.addAttribute("text", text);
                model.addAttribute("offerId", offerId);
            }
            String image = userService.getUserImage(user);
            List<Item> inventory = itemService.getByUser(user);
            model.addAttribute("inventory", inventory);
            model.addAttribute("user", (AuthorizedUser) user);
            model.addAttribute("image", image);
            return "userProfile";
        } else return "redirect:/trade/my";
    }


    @PostMapping("/trade/my/{username}")
    public String postTrade(@PathVariable String username,
                            @RequestParam(name = "offerId", required = false) Long offerId,
                            @RequestParam(value = "inventoryIds", required = false) String[] inventoryIds,
                            Principal principal, Model model) {
        if (offerId == null) {
            AuthorizedUser receiver = (AuthorizedUser) userService.getByName(username);
            AuthorizedUser sender = (AuthorizedUser) userService.getByName(principal.getName());
            Offer offer = offerService.createNewOffer(inventoryIds, sender, receiver);
            offerService.save(offer, sender, receiver);
            String content = "<p>Hello,</p>"
                    + "<p>You have new request to trade</p>"
                    + "<p>Please, check this out";
            String from = "vowa.legun@gmail.com";
            String to = receiver.getEmail();
            mailService.createAndSendMimeMessage(content, from, to);
            //todo класи для роботи з урлами
            return "redirect:/trade/my/select?offerId=" + offer.getId() + "&update=false";
        } else {
            Offer offer = offerService.getById(offerId);
            offer.setSendersItems(itemService.getItemsById(inventoryIds));
            offerService.save(offer);
            return "redirect:/trade/my/select?offerId=" + offer.getId() + "&update=true";
        }


    }


    @GetMapping("trade/my/select")
    public String selectItems(@RequestParam(name = "offerId", required = false) Long offerId,
                              @RequestParam(name = "update", required = false) boolean update,
                              Principal principal, Model model) {
        AuthorizedUser user = (AuthorizedUser) userService.getByName(principal.getName());
        List<Item> inventory = itemService.getByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("inventory", inventory);
        model.addAttribute("offerId", offerId);
        model.addAttribute("update", update);
        return "select_items";
    }

    //todo класи для роботи з урлами
    @PostMapping("trade/my/select")
    public String selectItemsForm(@RequestParam("offerId") Long offerId,
                                  @RequestParam(value = "inventoryIds", required = false) String[] inventoryIds,
                                  @RequestParam("update") boolean update,
                                  Principal principal, Model model) {

//todo класи для роботи з урлами
        if (update) {
            Offer offer = offerService.getById(offerId);
            offerService.setReceiversItemsById(offerId, inventoryIds);
            offer.setStatus(STATUS.UPDATED);
            AuthorizedUser user = (AuthorizedUser) offer.getReceiver();
            offer.setReceiver(offer.getSender());
            offer.setSender(user);
            offer.setUpdatedAt(LocalDateTime.now());
            offerService.save(offer);
            String content = "<p>Hello,</p>"
                    + "<p>Your trade request has been updated</p>"
                    + "<p>Please, check this out";
            String from = "vowa.legun@gmail.com";
            String to = offer.getSender().getEmail();
            mailService.createAndSendMimeMessage(content, from, to);
        } else {
            offerService.setSendersItemsById(offerId, inventoryIds);
        }
        return "redirect:/trade/my";
    }


    @GetMapping("trade/my/offers")
    public String offers(Model model, Principal principal) {
        AuthorizedUser user = (AuthorizedUser) userService.getByName(principal.getName());
        model.addAttribute("sent_offers", offerService.sort(user.getSentOffers()));
        model.addAttribute("receive_offers", offerService.sort(user.getReceivedOffers()));
        model.addAttribute("status", STATUS.values());

        return "your_offers_page";
    }

    //todo change to PostMethod
    @GetMapping("trade/my/offers/edit/{offerId}")
    public String editOffer(@PathVariable Long offerId, Principal principal) {
        Offer offer = offerService.getById(offerId);
        System.out.println(1);
        offer.setStatus(STATUS.CANCELLED);
        System.out.println(2);
        offerService.save(offer);
        if (principal.getName().equals(offer.getSender().getUsername())) {
            return "redirect:/trade/my/" + offer.getReceiver().getUsername() +
                    "?offerId=" + offerId + "&text=choose items you want to get";
        } else {
            return "redirect:/trade/my/" + offer.getSender().getUsername() +
                    "?offerId=" + offerId + "&text=c    hoose items you want to get";
        }
    }

    @GetMapping("/trade/my/offers/decline/{offerId}")
    public String declineOffer(@PathVariable Long offerId) {
        Offer offer = offerService.getById(offerId);
        offer.setStatus(STATUS.DECLINED);
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
        AuthorizedUser user = (AuthorizedUser) userService.getByName(principal.getName());
        try {
            user.setImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userService.save(user);
        return "redirect:/trade/my";
    }


///////////////////////////////////////////////////////////

    //todo delete at the end
    @GetMapping("/info")
    public String info(Model model, Principal principal) {
        User user = userService.getByName(principal.getName());
        model.addAttribute("role", user.getRoles());
        model.addAttribute("name", user.getUsername());
        return "info";
    }

    //todo delete at the end too
    @GetMapping("/init")
    public String init() {
        AuthorizedUser user = (AuthorizedUser) userService.getByName("a");
        Set<ROLE> roles = new HashSet<>();
        roles.add(ROLE.ADMIN);
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/trade";
    }


}

//TODO COMMIT