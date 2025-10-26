package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepo;

import java.util.List;

@Transactional
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    public RoleServiceImpl(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public List<Role> allRoles() {
        return roleRepo.findAll();
    }

    @Override
    public void addRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public void updateRole(Role role) {
        if (getRoleById(role.getId()).getNameRole().equals(role.getNameRole()) || isRoleNameUnique(role)) {
            roleRepo.save(role);
        }
    }

    @Override
    public void deleteRole(Long id) {
        roleRepo.deleteById(id); // Используйте deleteById вместо delete
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepo.findById(id).orElse(null);
    }


    @Override
    public Role getRoleByName(String role) throws IllegalStateException {
        return roleRepo.findByNameRole(role).orElseThrow(() -> new IllegalStateException("Role not found by name"));
    }

    private boolean isRoleNameUnique(Role role) {
        return !roleRepo.findByNameRole(role.getNameRole()).isPresent();
    }
}