package ru.kata.spring.boot_security.demo.configs;

import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.services.RoleService;

import javax.annotation.PostConstruct;

public class DataLoader {

    private final RoleService roleService;

    public DataLoader(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostConstruct
    public void loadData() {
        // Создаем роли, если они не существуют
        try {
            roleService.getRoleByName("user");
        } catch (Exception e) {
            roleService.addRole(new Role(null, "user"));
        }

        try {
            roleService.getRoleByName("admin");
        } catch (Exception e) {
            roleService.addRole(new Role(null, "admin"));
        }
    }
}
