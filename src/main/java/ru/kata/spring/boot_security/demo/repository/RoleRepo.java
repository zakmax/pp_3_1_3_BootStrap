package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends CrudRepository <Role, Integer> {

    List<Role> findAll();
    Optional<Role> findByNameRole(String nameRole);
    Role findById(Long id);
}