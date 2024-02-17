package com.example.trade.controllers;


import com.example.trade.model.*;
import com.example.trade.repository.UserRepository;
import com.example.trade.service.ItemService;
import com.example.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

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
    public String newAdmin(@PathVariable String username){
         AuthorizedUser user = (AuthorizedUser)userService.getByName(username);
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
    public String userPage(Principal principal, Model model){
        User user = userService.getByName(principal.getName());
        List<Item> inventory = itemService.getByUser(user);
        model.addAttribute("inventory",inventory);
        model.addAttribute("user",user);
        return "user_page";
    }

    @PostMapping("/trade/my")
    public String userPageProccesing(@RequestParam String name,
                                     @RequestParam Long price,
                                     @RequestParam String quality,
                                     @RequestParam String description,
                                     Principal principal){

        itemService.save(name,description,price,quality,
                (AuthorizedUser)userService.getByName(principal.getName()));
        return "redirect:/trade/my";
    }
///////////////////////////////////////////////////////////


    //todo delete at the end
    @GetMapping("/info")
    public String info(Model model, Principal principal)
    {
        User user = userService.getByName(principal.getName());
        model.addAttribute("role",user.getRoles());
        model.addAttribute("name",user.getUsername());
        return "info";
    }


    //todo delete at the end too
    @GetMapping("/init")
    public String init(){
    AuthorizedUser user = (AuthorizedUser) userService.getByName("a");
    user.getRoles().add(ROLE.ADMIN);
    userService.save(user);
    return "redirect:/trade";
    }


}

