package com.example.trade.controllers;


import com.example.trade.model.Admin;
import com.example.trade.model.ROLE;
import com.example.trade.model.User;
import com.example.trade.repository.UserRepository;
import com.example.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class MainController {

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


    @GetMapping("/trade/my")
    public String accountPage() {
        return "account";
    }


    @GetMapping("/trade/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/trade/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("trade/admin/{username}")
    public String newAdmin(@PathVariable String username){
         User user = userService.getByName(username);
         Admin admin = new Admin(
                 user.getUsername(),
                 user.getPassword(),
                 user.getEmail());
         admin.setActive(true);
         admin.setRoles(user.getRoles());
        userService.save(admin);
        return "redirect:/trade/admin/users";
    }

    @GetMapping("/info")
    public String info(Model model, Principal principal)
    {
        User user = userService.getByName(principal.getName());
        model.addAttribute("role",user.getRoles());
        model.addAttribute("name",user.getUsername());
        return "info";
    }

    @GetMapping("/init")
    public String init(){
    User user = userService.getByName("a");
    user.getRoles().add(ROLE.ADMIN);
    userService.save(user);
    return "redirect:/trade";
    }


}

