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

//    @GetMapping
//    public String getUserPage(Authentication authentication, Model model) {
//        System.out.println("=== USER CONTROLLER CALLED ===");
//        System.out.println("Authentication: " + authentication);
//
//        if (authentication == null) {
//            System.out.println("Authentication is NULL - redirecting to login");
//            return "redirect:/login";
//        }
//
//        System.out.println("User email: " + authentication.getName());
//
//        try {
//            User user = userService.getUserByEmail(authentication.getName());
//            System.out.println("User found: " + user.getEmail());
//            model.addAttribute("user", new UserDao(user));
//            return "userPage";
//        } catch (Exception e) {
//            System.out.println("Error in user controller: " + e.getMessage());
//            e.printStackTrace();
//            return "redirect:/login?error";
//        }
//    }
//}


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




//        model.addAttribute("user", new UserDao(userService.getUserByEmail(authentication.getName())));
//        return "userPage";
//    }
//}