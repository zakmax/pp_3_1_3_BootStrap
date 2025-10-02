package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        List<UserDao> userDaoList = new ArrayList<>();
        for (User user : userService.allUsers()) {
            userDaoList.add(new UserDao(user));
        }
        model.addAttribute("userList", userDaoList);
        return "table";
    }

    @PostMapping("/userAdd")
    public String addUser(UserDao userDao) {
        if (userService.addUser(userDao)) {
            return "redirect:/admin";
        } else {
            return "wrongName";
        }
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        return "redirect:/admin";
    }

    @GetMapping("/updateUserForm")
    public String updateUser(@RequestParam("id") long id, Model model) {
        model.addAttribute("user", new UserDao(userService.getUserById(id)));
        return "updateUserForm";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute("user") UserDao userDao, Model model) {
        if (userService.updateUser(userDao )) {
            model.addAttribute("userList", userService.allUsers());
            return "redirect:/admin";
        } else {
            return "wrongName";
        }
    }
}