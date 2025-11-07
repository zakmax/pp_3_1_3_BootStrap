package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;


@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserPage(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", new UserDao(user));
            return "userPage";
        } catch (Exception e) {
            return "redirect/login?error";
                }
            }
      }



