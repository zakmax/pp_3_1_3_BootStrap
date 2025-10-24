package ru.kata.spring.boot_security.demo.services;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repository.UserRepo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService {



    private final UserRepo userRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        return userRepo.findAll();
    }

    @Override
    public boolean addUser(UserDao userDao) {

        System.out.println("=== USER SERVICE - ADD USER ===");
        System.out.println("Email: " + userDao.getEmail());

        // Проверяем уникальность email
        if (!isEmailUnique(userDao)) {
            System.out.println("Email already exists: " + userDao.getEmail());
            return false;
        }
        System.out.println("Email is unique");

        try {
            // Создаем нового пользователя
            User user = new User();
            user.setFirstName(userDao.getFirstName());
            user.setLastName(userDao.getLastName());
            user.setEmail(userDao.getEmail());
            user.setAge(userDao.getAge());

            // Кодируем пароль
            if (userDao.getPassword() != null && !userDao.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(userDao.getPassword());
                user.setPassword(encodedPassword);
                System.out.println("Password encoded");
            } else {
                System.out.println("Password is empty!");
                return false;
            }

            // Устанавливаем роли
            if (userDao.getRoles() != null && userDao.getRoles().length > 0) {
                Set<Role> userRoles = Arrays.stream(userDao.getRoles())
                        .map(roleName -> {
                            try {
                                return roleService.getRoleByName(roleName);
                            } catch (Exception e) {
                                System.out.println("Role not found: " + roleName);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                user.setRoles(userRoles);
                System.out.println("Roles set: " + userRoles);
            } else {
                // Роль по умолчанию
                try {
                    Role userRole = roleService.getRoleByName("user");
                    user.setRoles(Set.of(userRole));
                    System.out.println("Default role set: user");
                } catch (Exception e) {
                    System.out.println("Default role 'user' not found!");
                    return false;
                }
            }

            // Сохраняем пользователя
            userRepo.save(user);
            System.out.println("User saved successfully with ID: " + user.getId());
            return true;

        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
//        if (!isEmailUnique(userDao)) {
//            return false;
//        }
//        User user = createUserFromForm(userDao); // Используем новый метод для создания
//        userRepo.save(user);
//        return true;
//    }

    @Override
    public boolean updateUser(UserDao userDao) {
        User existingUser = getUserById(userDao.getId());
        if (existingUser.getEmail().equals(userDao.getEmail()) || isEmailUnique(userDao)) {
            User updatedUser = updateUserFromForm(userDao, existingUser);
            userRepo.save(updatedUser);
            return true;
        }
        return false;
    }

    // НОВЫЙ МЕТОД для создания пользователя
    private User createUserFromForm(UserDao userDao) {
        User user = new User();
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setAge(userDao.getAge());
        user.setEmail(userDao.getEmail());

        // Обязательно кодируем пароль
        if (userDao.getPassword() != null && !userDao.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDao.getPassword()));
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Устанавливаем роли
        setRoles(user, userDao);
        return user;
    }

    // Обновленный метод для обновления пользователя
    private User updateUserFromForm(UserDao userDao, User existingUser) {
        existingUser.setFirstName(userDao.getFirstName());
        existingUser.setLastName(userDao.getLastName());
        existingUser.setAge(userDao.getAge());
        existingUser.setEmail(userDao.getEmail());

        setRoles(existingUser, userDao);

        if (userDao.getPassword() != null && !userDao.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDao.getPassword()));
        }
        return existingUser;
    }

    private boolean isEmailUnique(UserDao userDao) {
        return !userRepo.findByEmail(userDao.getEmail()).isPresent();
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByEmail(String email) throws IllegalStateException {
        return userRepo.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found by email"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        Hibernate.initialize(user.getRoles());
        return user;
    }

    private void setRoles(User user, UserDao userDao) {
        if (userDao.getRoles() != null && userDao.getRoles().length > 0) {
            user.setRoles(Arrays.stream(userDao.getRoles())
                    .map(roleService::getRoleByName)
                    .collect(Collectors.toSet()));
        } else {
            // Устанавливаем роль USER по умолчанию, если не выбрано ни одной роли
            Role userRole = roleService.getRoleByName("user");
            user.setRoles(Set.of(userRole));
        }
    }
}

//    private final UserRepo userRepo;
//    private final RoleService roleService;
//    private final PasswordEncoder passwordEncoder;
//
//    public UserServiceImpl(UserRepo userRepo, RoleService roleService, PasswordEncoder passwordEncoder) {
//        this.userRepo = userRepo;
//        this.roleService = roleService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    public List<User> allUsers() {
//        return userRepo.findAll();
//    }
//
//    @Override
//    public boolean addUser(UserDao userDao) {
//        if (!isEmailUnique(userDao)) {
//            return false;
//        }
//        User user = updateUserFromForm(userDao);
//        userRepo.save(user);
//        return true;
//    }
//
//    @Override
//    public boolean updateUser(UserDao userDao) {
//        if (getUserById(userDao.getId()).getEmail().equals(userDao.getEmail()) || isEmailUnique(userDao)) {
//            User user = updateUserFromForm(userDao);
//            userRepo.save(user);
//            return true;
//        }
//        return false;
//    }
//
//    private  User updateUserFromForm (UserDao userDao) {
//        User existingUser = getUserById(userDao.getId());
//

//        existingUser.setFirstName(userDao.getFirstName());
//        existingUser.setLastName(userDao.getLastName());
//        existingUser.setAge(userDao.getAge());
//        existingUser.setEmail(userDao.getEmail());
//
//        setRoles(existingUser, userDao);
//
//        if (userDao.getPassword() != null && !userDao.getPassword().trim().isEmpty()) {
//            existingUser.setPassword(passwordEncoder.encode(userDao.getPassword()));
//        }
//        return existingUser;
//    }
//
//    private boolean isEmailUnique(UserDao userDao) {
//        return !userRepo.findByEmail(userDao.getEmail()).isPresent();
//    }
//
//
//    @Override
//    public void deleteUser(Long id) {
//        userRepo.deleteById(id);
//    }
//
//    @Override
//    public User getUserById(Long id) {
//        return userRepo.findById(id);
//    }
//
//
//
//    @Transactional(readOnly = true)
//   @Override
//    public User getUserByEmail(String email) throws IllegalStateException {
//        return userRepo.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found by email"));
//    }
//
//
//    @Transactional(readOnly = true)
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//       User user = getUserByEmail(email);
//        Hibernate.initialize(user.getRoles());
//        return user;
//    }
//
//    private void setRoles(User user, UserDao userDao) {
//        user.setRoles(Arrays.stream(userDao.getRoles())
//                .map(roleService::getRoleByName)
//                .collect(Collectors.toSet()));
//    }
//}