package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Authentication authentication, Model model) {
        try {
            System.out.println("=== ADMIN PANEL ===");

            // Получаем текущего пользователя для header
            User currentUser = userService.getUserByEmail(authentication.getName());
            model.addAttribute("currentUser", new UserDao(currentUser));

            // Получаем список всех пользователей
            List<UserDao> userDaoList = userService.allUsers().stream()
                    .map(UserDao::new)
                    .collect(Collectors.toList());
            model.addAttribute("userList", userDaoList);

            System.out.println("Users count: " + userDaoList.size());
            return "table";
        } catch (Exception e) {
            System.out.println("Error in admin panel: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/login?error";
        }
    }

    @GetMapping("/newUser")
    public String showNewUserForm(Authentication authentication, Model model,
                                  @RequestParam(value = "error", required = false) String error) {
        try {
            System.out.println("=== NEW USER FORM ===");

            User currentUser = userService.getUserByEmail(authentication.getName());
            model.addAttribute("currentUser", new UserDao(currentUser));
            model.addAttribute("userDao", new UserDao());

            if ("email_exists".equals(error)) {
                model.addAttribute("errorMessage", "User with this email already exists!");
            }

            System.out.println("Showing new-user form");
            return "new-user";
        } catch (Exception e) {
            System.out.println("Error showing new user form: " + e.getMessage());
            return "redirect:/admin?error";
        }
    }

    @PostMapping("/userAdd")
    public String addUser(@ModelAttribute UserDao userDao,
                          @RequestParam(value = "roles", required = false) String[] roles) {

        System.out.println("=== ADD USER PROCESSING ===");
        System.out.println("UserDao: " + userDao);
        System.out.println("First Name: " + userDao.getFirstName());
        System.out.println("Last Name: " + userDao.getLastName());
        System.out.println("Email: " + userDao.getEmail());
        System.out.println("Age: " + userDao.getAge());
        System.out.println("Password: " + (userDao.getPassword() != null ? "[SET]" : "[NULL]"));
        System.out.println("Roles param: " + (roles != null ? Arrays.toString(roles) : "null"));

        if (roles != null) {
            userDao.setRoles(roles);
            System.out.println("Roles set to UserDao: " + Arrays.toString(userDao.getRoles()));
        }

        try {
            boolean result = userService.addUser(userDao);
            System.out.println("Add user result: " + result);

            if (result) {
                System.out.println("User added successfully, redirecting to /admin");
                return "redirect:/admin";
            } else {
                System.out.println("Failed to add user (email exists), redirecting with error");
                return "redirect:/admin/newUser?error=email_exists";
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION in addUser: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/newUser?error=system_error";
        }
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        System.out.println("Deleting user with id: " + id);
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/updateUserForm")
    public String showUpdateUserForm(@RequestParam("id") long id, Model model, Authentication authentication) {
        try {
            System.out.println("=== UPDATE USER FORM ===");
            System.out.println("User ID to update: " + id);

            // Получаем текущего пользователя для header
            User currentUser = userService.getUserByEmail(authentication.getName());
            model.addAttribute("currentUser", new UserDao(currentUser));

            // Получаем пользователя для редактирования
            User userToEdit = userService.getUserById(id);
            if (userToEdit == null) {
                System.out.println("User not found with ID: " + id);
                return "redirect:/admin?error=user_not_found";
            }

            UserDao userDao = new UserDao(userToEdit);
            model.addAttribute("userDao", userDao);

            System.out.println("User to edit: " + userDao.getFirstName() + " " + userDao.getLastName());
            System.out.println("User roles: " + Arrays.toString(userDao.getRoles()));

            return "updateUserForm";
        } catch (Exception e) {
            System.out.println("Error showing update form: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin?error";
        }
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute UserDao userDao,
                           @RequestParam(value = "roles", required = false) String[] roles,
                           Authentication authentication) {

        System.out.println("=== EDIT USER PROCESSING ===");
        System.out.println("UserDao ID: " + userDao.getId());
        System.out.println("First Name: " + userDao.getFirstName());
        System.out.println("Last Name: " + userDao.getLastName());
        System.out.println("Email: " + userDao.getEmail());
        System.out.println("Age: " + userDao.getAge());
        System.out.println("Password: " + (userDao.getPassword() != null ? "[SET]" : "[NULL]"));
        System.out.println("Roles param: " + (roles != null ? Arrays.toString(roles) : "null"));

        if (roles != null) {
            userDao.setRoles(roles);
            System.out.println("Roles set to UserDao: " + Arrays.toString(userDao.getRoles()));
        } else {
            System.out.println("No roles selected, using existing roles");
            // Если роли не выбраны, сохраняем существующие
            try {
                User existingUser = userService.getUserById(userDao.getId());
                if (existingUser != null) {
                    UserDao existingUserDao = new UserDao(existingUser);
                    userDao.setRoles(existingUserDao.getRoles());
                }
            } catch (Exception e) {
                System.out.println("Error getting existing user roles: " + e.getMessage());
            }
        }

        try {
            boolean result = userService.updateUser(userDao);
            System.out.println("Update user result: " + result);

            if (result) {
                System.out.println("User updated successfully, redirecting to /admin");
                return "redirect:/admin";
            } else {
                System.out.println("Failed to update user, redirecting with error");
                return "redirect:/admin/updateUserForm?id=" + userDao.getId() + "&error=update_failed";
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION in editUser: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/updateUserForm?id=" + userDao.getId() + "&error=system_error";
        }
    }
    @GetMapping("/getUserData")
    @ResponseBody
    public UserDao getUserData(@RequestParam("id") long id) {
        try {
            System.out.println("=== GET USER DATA FOR MODAL ===");
            System.out.println("User ID: " + id);

            User user = userService.getUserById(id);
            if (user == null) {
                System.out.println("User not found with ID: " + id);
                throw new RuntimeException("User not found");
            }

            UserDao userDao = new UserDao(user);
            System.out.println("Returning user data: " + userDao.getFirstName() + " " + userDao.getLastName());

            return userDao;
        } catch (Exception e) {
            System.out.println("Error getting user data: " + e.getMessage());
            throw new RuntimeException("Error loading user data");
        }
    }
}


