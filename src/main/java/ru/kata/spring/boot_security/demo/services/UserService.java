package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> allUsers();

    boolean addUser(UserDao userDto);

    boolean updateUser(UserDao userDao);

    void deleteUser(Long id);

    User getUserById(Long id);

    User getUserByName(String name);

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;
}